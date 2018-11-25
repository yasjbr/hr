package ps.gov.epsilon.hr.firm.dispatch

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route DispatchRequest requests between model and views.
 * @see DispatchRequestService
 * @see FormatService
 * */
class DispatchRequestController {

    DispatchRequestService dispatchRequestService
    FormatService formatService
    EmployeeService employeeService
    DispatchListEmployeeService dispatchListEmployeeService
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
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(DispatchRequest.getName(), EnumOperation.DISPATCH)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.requestEncodedId) {
            params.encodedId = params.requestEncodedId
        }
        def map
        PagedResultList pagedResultList
        if (params.encodedId) {
            DispatchRequest dispatchRequest = dispatchRequestService.getInstanceWithRemotingValues(params)
            if (dispatchRequest) {
                /**
                 * get dispatch list employee by dispatch request
                 */
                GrailsParameterMap dispatchListEmployeeParam = new GrailsParameterMap(['dispatchRequest.id': dispatchRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                pagedResultList = dispatchListEmployeeService?.search(dispatchListEmployeeParam)

                if (pagedResultList?.resultList?.size() > 0) {
                    map = [dispatchRequest: dispatchRequest, dispatchListEmployee: pagedResultList?.resultList?.get(0)]
                } else {
                    map = [dispatchRequest: dispatchRequest]
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
            PagedResultList<DispatchRequest> dispatchRequestPagedResultList = dispatchRequestService.getThreadWithRemotingValues(params)
            if (dispatchRequestPagedResultList) {
                return [dispatchRequestList: dispatchRequestPagedResultList?.resultList]
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
        PagedList pagedResultList = dispatchRequestService.searchCanHaveOperation(params)
        render text: (dispatchRequestService.resultListToMap(pagedResultList, params, dispatchRequestService.LITE_DOMAIN_COLUMNS) as JSON),
                contentType: "application/json"
    }


    /**
     * represent the create page empty instance
     */
    def create = {}

    /**
     * this action used to create new Dispatch Request for the selected employee
     */
    def createNewDispatchRequest = {
        Map map = [:]
        if (params["employeeId"]) {
            DispatchRequest dispatchRequest = dispatchRequestService.getPreCreateInstance(params)
            if (dispatchRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(dispatchRequest)?.message)
                redirect(action: "create")
            } else {

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            dispatchRequest?.employee?.id,
                            dispatchRequest.employee?.currentEmploymentRecord?.department?.id,
                            dispatchRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            dispatchRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            DispatchRequest.getName(),
                            dispatchRequest?.id,
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
                map.dispatchRequest = dispatchRequest

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
     * this action is used to return employee id who is selected
     */
    def selectEmployee = {
        if (params["employeeId"]) {
            List<EmployeeStatusHistory> employeeStatusHistories = dispatchRequestService?.getCurrentStatus(params);
            if (employeeStatusHistories.size() > 0) {
                String failMessage = message(code: 'dispatchRequest.employee.currentDispatch.error.label', args: null, default: "")
                render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
            } else {
                render text: ([success: true, employeeId: params["employeeId"]] as JSON), contentType: "application/json"
            }
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = dispatchRequestService.searchWithRemotingValues(params)
        render text: (dispatchRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DispatchRequest dispatchRequest = dispatchRequestService?.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), dispatchRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), dispatchRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (dispatchRequest?.hasErrors()) {
                respond dispatchRequest, view: 'create'
                return
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
            DispatchRequest dispatchRequest = dispatchRequestService.getInstanceWithRemotingValues(params)
            //allow edit when have CREATED status only
            if (dispatchRequest?.requestStatus == EnumRequestStatus.CREATED) {
                respond dispatchRequest
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
        DispatchRequest dispatchRequest = dispatchRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), dispatchRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), dispatchRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (dispatchRequest.hasErrors()) {
                respond dispatchRequest, view: 'edit'
                return
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
        DeleteBean deleteBean = dispatchRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "dispatch", DispatchRequest, true))
        } else {
            notFound()
        }
    }

    def autocomplete = {
        render text: (dispatchRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * render the extension request  modal
     */
    def extensionRequestList = {
        return [id: params["id"]]
    }


    /**
     * render the stop request  modal
     */
    def stopRequestCreate = {
        if (params.id) {
            //get dispatch request
            DispatchRequest dispatchRequest = dispatchRequestService?.getInstanceWithRemotingValues(params)
            if(dispatchRequest?.canStopRequest){
                return [dispatchRequest:dispatchRequest, requestType:EnumRequestType.DISPATCH_STOP_REQUEST]
            }else{
                log.error("Cannot stop dispatch request " + dispatchRequest?.id)
                notFound()
            }
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dispatchRequest.entity', default: 'DispatchRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

