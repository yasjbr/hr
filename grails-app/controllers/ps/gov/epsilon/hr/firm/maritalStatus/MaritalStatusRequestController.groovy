package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import static org.springframework.http.HttpStatus.NOT_FOUND
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route MaritalStatusRequest requests between model and views.
 * @see MaritalStatusRequestService
 * @see FormatService
 * */
class MaritalStatusRequestController {

    MaritalStatusRequestService maritalStatusRequestService
    FormatService formatService
    ManagePersonService managePersonService
    EmployeeService employeeService
    PersonService personService
    PersonRelationShipsService personRelationShipsService
    MaritalStatusService maritalStatusService
    PersonMaritalStatusService personMaritalStatusService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(MaritalStatusRequest.getName(), EnumOperation.CHANGE_MARITAL_STATUS)
    }


    def show = {
        if (params.requestEncodedId) {
            params.encodedId = params.requestEncodedId
        }
        def map
        PagedResultList pagedResultList
        if (params.encodedId) {
            MaritalStatusRequest maritalStatusRequest = maritalStatusRequestService.getInstanceWithRemotingValues(params)
            if (maritalStatusRequest) {
                if (pagedResultList?.resultList?.size() > 0) {
                    map = [maritalStatusRequest: maritalStatusRequest]
                } else {
                    map = [maritalStatusRequest: maritalStatusRequest]
                }
                respond map
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }


    /**
     * represent the show page with get instance
     */
    def showThread = {
        if (params.threadId) {
            PagedResultList<MaritalStatusRequest> maritalStatusRequestPagedResultList = maritalStatusRequestService.getThreadWithRemotingValues(params)
            if (maritalStatusRequestPagedResultList) {
                return [maritalStatusRequestList: maritalStatusRequestPagedResultList?.resultList]
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filterCanHaveOperation = {
        PagedList pagedResultList = maritalStatusRequestService.searchCanHaveOperation(params)
        render text: (maritalStatusRequestService.resultListToMap(pagedResultList, params, maritalStatusRequestService.LITE_DOMAIN_COLUMNS) as JSON),
                contentType: "application/json"
    }



    def create = {
        respond new MaritalStatusRequest(params)
    }

    /**
     * this action used to create new NewMaritalStatusRequest
     */
    def createNewRequest = {
        if (params["employeeId"]) {
            MaritalStatusRequest maritalStatusRequest = maritalStatusRequestService.getPreCreateInstance(params)
            if (maritalStatusRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(maritalStatusRequest)?.message)
                redirect(action: "create")
            } else {

                Map map = [:]

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            maritalStatusRequest?.employee?.id,
                            maritalStatusRequest.employee?.currentEmploymentRecord?.department?.id,
                            maritalStatusRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            maritalStatusRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            MaritalStatusRequest.getName(),
                            maritalStatusRequest?.id,
                            false)

                    /**
                     * sort workflow path details
                     */
                    workflowPathHeader?.workflowPathDetails = workflowPathHeader?.workflowPathDetails?.sort { a, b -> b.sequence <=> a.sequence }

                    /**
                     * add workflowPathHeader to map
                     */
                    map.workflowPathHeader = workflowPathHeader
                }
                /**
                 * add request to map
                 */
                map.maritalStatusRequest = maritalStatusRequest

                /**
                 * respond map
                 */
                respond(map)
            }
        } else {
            notFound()
        }
    }

    def selectEmployee = {
        if (params["employeeId"]) {
            render text: ([success: true, employeeId: params["employeeId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    def filter = {
        PagedResultList pagedResultList = maritalStatusRequestService.searchWithRemotingValues(params)
        render text: (maritalStatusRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }


    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        MaritalStatusRequest maritalStatusRequest = maritalStatusRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), maritalStatusRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), maritalStatusRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (maritalStatusRequest?.hasErrors()) {
                respond maritalStatusRequest, view: 'createNewRequest'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        String failMessage = message(code: 'request.fail.edit.message', args: [])
        if (params.encodedId) {
            MaritalStatusRequest maritalStatusRequest = maritalStatusRequestService.getInstanceWithRemotingValues(params)
            if (maritalStatusRequest && (maritalStatusRequest?.requestStatus == EnumRequestStatus.CREATED || maritalStatusRequest?.requestStatus == EnumRequestStatus.IN_PROGRESS)) {
                respond maritalStatusRequest
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    def update = {
        MaritalStatusRequest maritalStatusRequest = maritalStatusRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), maritalStatusRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), maritalStatusRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (maritalStatusRequest.hasErrors()) {
                respond maritalStatusRequest, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = maritalStatusRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), params?.id, deleteBean.responseMessage ?: ""])
        if (request.xhr) {
            def json = [:]
            json.success = deleteBean.status
            json.message = deleteBean.status ? msg.success(label: successMessage) : msg.error(label: failMessage)
            render text: (json as JSON), contentType: "application/json"
        } else {
            if (deleteBean.status) {
                flash.message = msg.success(label: successMessage)
            } else {
                flash.message = msg.error(label: failMessage)
            }
            redirect(action: "list")
        }
    }

    def autocomplete = {
        render text: (maritalStatusRequestService.autoComplete(params)), contentType: "application/json"
    }

    def getPersonDetails = {
        PersonDTO personDTO = managePersonService.getPersonDTO(params);
        render(template: "/maritalStatusRequest/personDetails", model: [personDTO: personDTO])
    }

    /**
     * this action is used to create New Person person which is not found in core:
     */
    def createNewPerson = {
    }

    /**
     * save new person in core
     */
    def saveNewPerson() {
        PersonCommand personCommand = new PersonCommand()
        bindData(personCommand,params)
        personCommand = managePersonService.saveNewPerson(personCommand, params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])

        if (request.xhr) {
            render text: (formatService.buildResponse(personCommand, successMessage, failMessage) as JSON), contentType: "application/json"
        }

    }

    /**
     * get the person autocomplete list depends on
     *  1. new marital status selection.
     *  2. gender type (show the opposite gender type).
     */
    def personAutocomplete = {
        render text: (maritalStatusRequestService.autoCompletePerson(params)), contentType: "application/json"
    }

    /**
     * get the person autocomplete data depend on marital status selection:
     */
    def maritalStatusAutocomplete = {
        SearchBean searchBean = new SearchBean()
        List<ps.police.pcore.enums.v1.MaritalStatusEnum> maritalStatusList = maritalStatusRequestService.maritalStatusAutocomplete(params)
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: maritalStatusList))
        searchBean.searchCriteria.put("nameProperty", new SearchConditionCriteriaBean(operand: "nameProperty", value1: "operationLocalName"))
        render text: (maritalStatusService.autoCompleteMaritalStatus(searchBean)), contentType: "application/json"
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "maritalStatus", MaritalStatusListEmployee))
        } else {
            notFound()
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

