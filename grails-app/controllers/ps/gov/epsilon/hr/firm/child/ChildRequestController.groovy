package ps.gov.epsilon.hr.firm.child

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route ChildRequest requests between model and views.
 * @see ChildRequestService
 * @see FormatService
 * */
class ChildRequestController {

    ChildRequestService childRequestService
    FormatService formatService
    ManagePersonService managePersonService
    EmployeeService employeeService
    ChildListEmployeeService childListEmployeeService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(ChildRequest.getName(), EnumOperation.CHILD_REGISTRATION)
    }

    def show = {
        if (params.requestEncodedId) {
            params.encodedId = params.requestEncodedId
        }
        def map
        PagedResultList pagedResultList
        if (params.encodedId) {
            ChildRequest childRequest = childRequestService.getInstanceWithRemotingValues(params)
            if (childRequest) {
                /**
                 * get child list employee by child request
                 */
                GrailsParameterMap childListEmployeeParam = new GrailsParameterMap(['childRequest.id': childRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                pagedResultList = childListEmployeeService?.search(childListEmployeeParam)

                if (pagedResultList?.resultList?.size() > 0) {
                    map = [childRequest: childRequest, childListEmployee: pagedResultList?.resultList?.get(0)]
                } else {
                    map = [childRequest: childRequest]
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
            PagedResultList<ChildRequest> childRequestPagedResultList = childRequestService.getThreadWithRemotingValues(params)
            if (childRequestPagedResultList) {
                return [childRequestList: childRequestPagedResultList?.resultList]
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
        PagedList pagedResultList = childRequestService.searchCanHaveOperation(params)
        render text: (childRequestService.resultListToMap(pagedResultList, params, childRequestService.LITE_DOMAIN_COLUMNS) as JSON),
                contentType: "application/json"
    }



    def create = {
        respond new ChildRequest(params)
    }

    /**
     * this action used to create new childRequest
     */
    def createNewRequest = {
        Map map = [:]
        if (params["employeeId"]) {
            ChildRequest childRequest = childRequestService.getPreCreateInstance(params)
            if (childRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(childRequest)?.message)
                redirect(action: "create")
            } else {
                // in case: if the user has HR role, get the suitable workflow
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            childRequest?.employee?.id,
                            childRequest.employee?.currentEmploymentRecord?.department?.id,
                            childRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            childRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            ChildRequest.getName(),
                            childRequest?.id,
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
                map.childRequest = childRequest

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
        PagedResultList pagedResultList = childRequestService.searchWithRemotingValues(params)
        render text: (childRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ChildRequest childRequest = childRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), childRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), childRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(childRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (childRequest?.hasErrors()) {
                respond childRequest, view: 'createNewRequest'
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
            ChildRequest childRequest = childRequestService.getInstanceWithRemotingValues(params)
            if (childRequest && (childRequest?.requestStatus == EnumRequestStatus.CREATED || childRequest?.requestStatus == EnumRequestStatus.IN_PROGRESS)) {
                respond childRequest
                return
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    def update = {
        ChildRequest childRequest = childRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), childRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), childRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(childRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (childRequest.hasErrors()) {
                respond childRequest, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = childRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (childRequestService.autoComplete(params)), contentType: "application/json"
    }

    def getPersonDetails = {
        PersonDTO personDTO = managePersonService.getPersonDTO(params);
        render(template: "/childRequest/personDetails", model: [personDTO: personDTO])
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
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "child", ChildListEmployee))
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'childRequest.entity', default: 'ChildRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

