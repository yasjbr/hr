package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route PetitionRequest requests between model and views.
 * @see PetitionRequestService
 * @see FormatService
 * */
class PetitionRequestController {

    PetitionRequestService petitionRequestService
    FormatService formatService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * default action in controller
     */
    def index = {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list = {
        respond sharedService.getAttachmentTypeListAsMap(PetitionRequest.getName(), EnumOperation.PETITION_REQUEST)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.requestEncodedId) {
            //this param was passed from showing request inside list
            params.encodedId = params.requestEncodedId
        }
        if (params.encodedId) {
            PetitionRequest petitionRequest = petitionRequestService.getInstanceWithRemotingValues(params)
            if (petitionRequest) {
                respond petitionRequest
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new PetitionRequest(params)
    }

    /**
     * this action used to create new petitionRequest
     */
    def createNewRequest = {
        Map map = [:]
        if (params["disciplinaryRequestId"]) {
            PetitionRequest petitionRequest = petitionRequestService.getPreCreateInstance(params)
            if (petitionRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(petitionRequest)?.message)
                redirect(action: "create")
            } else {
                // in case: if the user has HR role, get the suitable workflow
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {
                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            petitionRequest?.employee?.id,
                            petitionRequest.employee?.currentEmploymentRecord?.department?.id,
                            petitionRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            petitionRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            PetitionRequest.getName(),
                            petitionRequest?.id,
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
                map.petitionRequest = petitionRequest
                /**
                 * respond map
                 */
                respond(map)
            }
        } else {
            notFound()
        }
    }

    def selectRequest = {
        if (params["disciplinaryRequestId"]) {
            render text: ([success: true, disciplinaryRequestId: params["disciplinaryRequestId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = petitionRequestService.searchWithRemotingValues(params)
        render text: (petitionRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        PetitionRequest petitionRequest = petitionRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), petitionRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), petitionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (petitionRequest?.hasErrors()) {
                respond petitionRequest, view: 'create'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        String failMessage = message(code: 'request.fail.edit.message', args: [])
        if (params.encodedId) {
            PetitionRequest petitionRequest = petitionRequestService.getInstanceWithRemotingValues(params)
            if (petitionRequest && (petitionRequest?.requestStatus == EnumRequestStatus.CREATED)) {
                respond petitionRequest
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        PetitionRequest petitionRequest = petitionRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), petitionRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), petitionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (petitionRequest.hasErrors()) {
                respond petitionRequest, view: 'edit'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = petitionRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), params?.id, deleteBean.responseMessage ?: ""])
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

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (petitionRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * represent the show page with get instance
     */
    def showRelatedRequest = {
        if (params.disciplinaryRequestId) {
            params["disciplinaryRequest.id"] = params.remove("disciplinaryRequestId")
            PetitionRequest petitionRequest = petitionRequestService.searchWithRemotingValues(params).resultList[0]
            if (petitionRequest) {
                respond petitionRequest
            }
        } else {
            notFound()
        }
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "petition", PetitionListEmployee, false, "petitionRequest", false))
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'petitionRequest.entity', default: 'PetitionRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

