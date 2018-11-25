package ps.gov.epsilon.hr.firm.employmentService

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route EmploymentServiceRequest requests between model and views.
 * @see EmploymentServiceRequestService
 * @see FormatService
 * */
class EmploymentServiceRequestController {

    EmploymentServiceRequestService employmentServiceRequestService
    FormatService formatService
    EmployeeService employeeService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * default action in controller
     */
    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    /**
     * represent the list ReturnToService page
     */
    def listReturnToService = {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(EmploymentServiceRequest.getName(), EnumOperation.RECALL_TO_SERVICE)
    }

    /**
     * represent the list EndOfService page
     */
    def listEndOfService = {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(EmploymentServiceRequest.getName(), EnumOperation.END_OF_SERVICE)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.requestEncodedId) {
            params.encodedId = params.requestEncodedId
        }
        if (params.encodedId) {
            EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.getInstanceWithRemotingValues(params)
            if (employmentServiceRequest) {
                respond employmentServiceRequest
                return
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the createReturnToService page empty instance
     */
    def redirectReturnToService = {

    }

    /**
     * represent the createEndOfService page empty instance
     */
    def redirectEndOfService = {

    }

    /**
     * Represent the createEndOfService page with return to service request type
     */
    def createReturnToService = {
        Map map = [:]
        if (params["employeeId"] && params["serviceActionReasonId"]) {
            params["requestType"] = EnumRequestType.RETURN_TO_SERVICE
            EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.getPreCreateInstance(params)
            if (employmentServiceRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(employmentServiceRequest)?.message)
                redirect(action: "redirectReturnToService")
            } else {
                employmentServiceRequest?.requestType = EnumRequestType.RETURN_TO_SERVICE

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            employmentServiceRequest?.employee?.id,
                            employmentServiceRequest.employee?.currentEmploymentRecord?.department?.id,
                            employmentServiceRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            employmentServiceRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            EmploymentServiceRequest.getName(),
                            employmentServiceRequest?.id,
                            false,
                            "${employmentServiceRequest?.requestType}")

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
                map.employmentServiceRequest = employmentServiceRequest

                /**
                 * respond map
                 */
                respond(map)
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the createReturnToService page empty instance
     */
    def createEndOfService = {
        Map map = [:]
        if (params["employeeId"]) {
            params["requestType"] = EnumRequestType.END_OF_SERVICE
            EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.getPreCreateInstance(params)
            if (employmentServiceRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(employmentServiceRequest)?.message)
                redirect(action: "redirectEndOfService")
            } else {
                employmentServiceRequest?.requestType = EnumRequestType.END_OF_SERVICE
                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            employmentServiceRequest?.employee?.id,
                            employmentServiceRequest.employee?.currentEmploymentRecord?.department?.id,
                            employmentServiceRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            employmentServiceRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            EmploymentServiceRequest.getName(),
                            employmentServiceRequest?.id,
                            false,
                            "${employmentServiceRequest?.requestType}")

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
                map.employmentServiceRequest = employmentServiceRequest

                //This flag is used to indicate if the form will be used in HR system so we have to show more fields in UI.
                map.isHRApplication = true

                /**
                 * respond map
                 */
                respond(map)
            }
        } else {
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = employmentServiceRequestService.searchWithRemotingValues(params)
        render text: (employmentServiceRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), employmentServiceRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), employmentServiceRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employmentServiceRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (employmentServiceRequest?.hasErrors()) {
                respond employmentServiceRequest, view: 'create'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def selectEmployeeEndOfService = {
        if (params["employeeId"]) {
            render text: ([success: true, employeeId: params["employeeId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    def selectEmployeeReturnToService = {
        if (params["employeeId"] && params["serviceActionReason.id"]) {
            render text: ([success: true, employeeId: params["employeeId"], serviceActionReasonId: params['serviceActionReason.id']] as JSON), contentType: "application/json"
        }else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if (params.encodedId) {
            EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.getInstanceWithRemotingValues(params)
            if (employmentServiceRequest) {
                respond employmentServiceRequest
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), employmentServiceRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), employmentServiceRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employmentServiceRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (employmentServiceRequest.hasErrors()) {
                respond employmentServiceRequest, view: 'edit'
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
        DeleteBean deleteBean = employmentServiceRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(employmentServiceRequestService.goToList(params["encodedId"]?.toString(), "manageServiceList"))
        } else {
            notFound()
        }
    }

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (employmentServiceRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employmentServiceRequest.entity', default: 'EmploymentServiceRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

