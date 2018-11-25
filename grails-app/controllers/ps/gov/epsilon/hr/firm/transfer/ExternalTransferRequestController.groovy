package ps.gov.epsilon.hr.firm.transfer

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.child.ChildListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route ExternalTransferRequest requests between model and views.
 * @see ExternalTransferRequestService
 * @see FormatService
 * */
class ExternalTransferRequestController {

    ExternalTransferRequestService externalTransferRequestService
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

    /**
     * represent the list page
     */
    def list = {
        respond sharedService.getAttachmentTypeListAsMap(ExternalTransferRequest.getName(), EnumOperation.EXTERNAL_TRANSFER)
    }
    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.getInstanceWithRemotingValues(params)
            if (externalTransferRequest) {
                respond externalTransferRequest
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
    }

    /**
     * this action used to select employee
     */
    def selectEmployee = {
        if (params["employeeId"]) {
            render text: ([success: true, employeeId: params["employeeId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label')
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new external transfer Request
     */
    def createNewExternalTransferRequest = {
        if (params["employeeId"]) {
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.getPreCreateInstance(params)
            if (externalTransferRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(externalTransferRequest)?.message)
                redirect(action: "create")
            } else {

                Map map = [:]

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            externalTransferRequest?.employee?.id,
                            externalTransferRequest.employee?.currentEmploymentRecord?.department?.id,
                            externalTransferRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            externalTransferRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            ExternalTransferRequest.getName(),
                            externalTransferRequest?.id,
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
                map.externalTransferRequest = externalTransferRequest

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
        PagedResultList pagedResultList = externalTransferRequestService.searchWithRemotingValues(params)
        render text: (externalTransferRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ExternalTransferRequest externalTransferRequest = externalTransferRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), externalTransferRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), externalTransferRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalTransferRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (externalTransferRequest?.hasErrors()) {
                respond externalTransferRequest, view: 'create'
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
        if (params.encodedId) {
            params['requestStatus'] = EnumRequestStatus.CREATED.toString()
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.getInstanceWithRemotingValues(params)
            if (externalTransferRequest) {
                respond externalTransferRequest
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
        ExternalTransferRequest externalTransferRequest = externalTransferRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), externalTransferRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), externalTransferRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalTransferRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (externalTransferRequest.hasErrors()) {
                respond externalTransferRequest, view: 'edit'
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
        DeleteBean deleteBean = externalTransferRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (externalTransferRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * represent the add clearance page with get instance
     */
    def addClearance = {
        if (params.encodedId) {
            params['includeRequestStatusList'] = [EnumRequestStatus.APPROVED_BY_WORKFLOW, EnumRequestStatus.APPROVED, EnumRequestStatus.ADD_TO_LIST, EnumRequestStatus.SENT_BY_LIST]
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.getInstanceWithRemotingValues(params)
            if (externalTransferRequest) {
                respond externalTransferRequest
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * save the add clearance info
     */
    def saveClearance = {
        params.hasClearance = "true"
        ExternalTransferRequest externalTransferRequest = externalTransferRequestService.save(params)
        String successMessage = message(code: 'externalTransferRequest.addedClearance.message', default: 'add clearance success')
        String failMessage = message(code: 'externalTransferRequest.not.addedClearance.message', default: 'add clearance failed')
        render text: (formatService.buildResponse(externalTransferRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
    }

    /**
     * represent the add transfer page with get instance
     */
    def addTransfer = {
        if (params.encodedId) {
            params['includeRequestStatusList'] = [EnumRequestStatus.APPROVED_BY_WORKFLOW, EnumRequestStatus.APPROVED, EnumRequestStatus.ADD_TO_LIST, EnumRequestStatus.SENT_BY_LIST]
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.getInstanceWithRemotingValues(params)
            if (externalTransferRequest) {
                respond externalTransferRequest
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * save the added transfer info
     */
    def saveTransfer = {
        params.hasTransferPermission = "true"
        ExternalTransferRequest externalTransferRequest = externalTransferRequestService.save(params)
        String successMessage = message(code: 'externalTransferRequest.addedTransfer.message', default: 'add transfer success')
        String failMessage = message(code: 'externalTransferRequest.not.addedTransfer.message', default: 'add transfer failed')
        render text: (formatService.buildResponse(externalTransferRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
    }

    /**
     * close request action
     */
    def closeRequest = {
        if (params.encodedId) {
            params.closeRequest = "true"
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.save(params)
            String successMessage = message(code: 'externalTransferRequest.closeRequest.message')
            String failMessage = message(code: 'externalTransferRequest.not.closeRequest.message')
            if (!externalTransferRequest.hasErrors()) {
                flash.message = msg.success(label: successMessage)
            } else {
                flash.message = msg.error(label: failMessage)
            }
            redirect(action: "list")
        } else {
            notFound()
        }
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "externalTransfer", ExternalTransferListEmployee))
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

