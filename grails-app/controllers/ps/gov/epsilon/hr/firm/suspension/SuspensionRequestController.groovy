package ps.gov.epsilon.hr.firm.suspension

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import guiplugin.FormatService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route SuspensionRequest requests between model and views.
 * @see SuspensionRequestService
 * @see FormatService
 * */
class SuspensionRequestController {

    static allowedMethods = [save: "POST", update: "POST"]

    SuspensionRequestService suspensionRequestService
    FormatService formatService
    SharedService sharedService
    SuspensionExtensionRequestService suspensionExtensionRequestService
    WorkFlowProcessService workFlowProcessService

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
        respond sharedService.getAttachmentTypeListAsMap(SuspensionRequest.getName(), EnumOperation.SUSPENSION_REQUEST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            SuspensionRequest suspensionRequest = suspensionRequestService.getInstanceWithRemotingValues(params)
            if (suspensionRequest) {
                respond suspensionRequest
                return
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new SuspensionRequest(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = suspensionRequestService.searchWithRemotingValues(params)
        render text: (suspensionRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        SuspensionRequest suspensionRequest = suspensionRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), suspensionRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), suspensionRequest?.id])
        //
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (suspensionRequest?.hasErrors()) {
                respond suspensionRequest, view: 'create'
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
        String errorMessage = message(code: 'suspensionRequest.error.edit.message')
        if (params.encodedId) {

            /**
             * get suspension request by encodedId
             */
            SuspensionRequest suspensionRequest = suspensionRequestService.getInstanceWithRemotingValues(params)

            /**
             * allow edit suspension request when request status is CREATED
             */
            if (suspensionRequest && suspensionRequest.requestStatus == EnumRequestStatus.CREATED) {
                respond suspensionRequest
            } else {
                flash.message = msg.error(label: errorMessage)
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
        SuspensionRequest suspensionRequest = suspensionRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), suspensionRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), suspensionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionRequest.hasErrors()) {
                respond suspensionRequest, view: 'edit'
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

        DeleteBean deleteBean = suspensionRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), params?.id, deleteBean.responseMessage ?: ""])

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
        render text: (suspensionRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'suspensionRequest.entity', default: 'SuspensionRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * this action is used to return  suspension request for selected employee
     */
    def selectEmployee = {
        if (params["employee.id"] && params.suspensionType) {

            SuspensionRequest suspensionRequest = suspensionRequestService.selectEmployee(params)
            String successMessage = message(code: 'suspensionRequest.success.select.employee.message', args: [], '')
            String failMessage = message(code: 'default.not.created.message', args: [message(code: 'suspensionRequest.entity', default: 'suspensionRequest'), suspensionRequest?.id])
            render text: (formatService.buildResponse(suspensionRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            notFound()
        }
    }

    /**
     * this action used to create new suspension request for the selected employee
     */
    def createNewSuspensionRequest = {
        Map map = [:]

        if (params["employee.id"] && params.suspensionType) {
            SuspensionRequest suspensionRequest = suspensionRequestService.getSuspensionRequest(params)

            /**
             * in case: if the user has HR role, get the suitable workflow
             */
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        suspensionRequest?.employee?.id,
                        suspensionRequest.employee?.currentEmploymentRecord?.department?.id,
                        suspensionRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                        suspensionRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                        SuspensionRequest.getName(),
                        suspensionRequest?.id,
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
            map.suspensionRequest = suspensionRequest

            /**
             * respond map
             */
            respond(map)
        } else {
            notFound()
        }
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "suspension", SuspensionListEmployee))
        } else {
            notFound()
        }
    }

    /**
     * render the extension request  modal
     */
    def extensionRequestList = {
        return [id: params["id"]]
    }

    /**
     * render the create  extension request  modal
     */
    def extensionRequestCreate = {

        Map map = [:]

        if (params.id) {
            /**
             * get suspension request
             */
            SuspensionRequest suspensionRequest = suspensionRequestService?.getInstanceWithRemotingValues(params)

            /**
             * create new suspension extension request
             */
            SuspensionExtensionRequest suspensionExtensionRequest = new SuspensionExtensionRequest(params)

            /**
             * assign suspension request for suspension extension request
             */
            suspensionExtensionRequest.suspensionRequest = suspensionRequest

            /**
             * in case: if the user has HR role, get the suitable workflow
             */
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        suspensionExtensionRequest?.suspensionRequest?.employee?.id,
                        suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.department?.id,
                        suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                        suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                        SuspensionExtensionRequest.getName(),
                        suspensionExtensionRequest?.id,
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
            map.suspensionExtensionRequest = suspensionExtensionRequest

            /**
             * respond map
             */
            respond(map)

        } else {
            notFound()
        }
    }

    /**
     * render the edit  extension request  modal
     */
    def extensionRequestEdit = {

        if (params.encodedId) {
            /**
             * get suspension extension request
             */
            SuspensionExtensionRequest suspensionExtensionRequest = suspensionExtensionRequestService.getInstanceWithRemotingValues(params)

            /**
             * respond suspensionExtensionRequest
             */
            respond(suspensionExtensionRequest)
        } else {
            notFound()
        }
    }

    /**
     * render the show  extension request  modal
     */
    def extensionRequestShow = {

        Map map = [:]

        if (params.encodedId || params['suspensionExtensionRequest.encodedId']) {

            //show request in list
            if (params['suspensionExtensionRequest.encodedId']) {
                params['encodedId'] = params['suspensionExtensionRequest.encodedId']
                map.hideBacak = true
            }
            /**
             * get suspension extension request
             */
            SuspensionExtensionRequest suspensionExtensionRequest = suspensionExtensionRequestService.getInstanceWithRemotingValues(params)

            /**
             * respond suspensionExtensionRequest
             */
            map.suspensionExtensionRequest = suspensionExtensionRequest

            respond map
        } else {
            notFound()
        }
    }

    /**
     * to render previous suspension request.
     */
    def previousSuspensionsModal = {
        String employeeId = params["id"]
        Map map = [employeeId: employeeId]
        respond map
    }
}

