package ps.gov.epsilon.hr.firm.transfer

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route InternalTransferRequest requests between model and views.
 *@see InternalTransferRequestService
 *@see FormatService
**/
class InternalTransferRequestController  {

    InternalTransferRequestService internalTransferRequestService
    FormatService formatService
    EmployeeService employeeService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

    static allowedMethods = [save: "POST", update: "POST"]


    /**
     * default action in controller
     */
    def index= {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list= {
        respond sharedService.getAttachmentTypeListAsMap(InternalTransferRequest.getName(), EnumOperation.INTERNAL_TRANSFER)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            InternalTransferRequest internalTransferRequest = internalTransferRequestService.getInstanceWithRemotingValues(params)
            if(internalTransferRequest){
                respond internalTransferRequest
                return
            }
        }else{
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
     * this action used to create new internal transfer Request
     */
    def createNewInternalTransferRequest = {
        if (params["employeeId"]) {
            InternalTransferRequest internalTransferRequest = internalTransferRequestService.getPreCreateInstance(params)
            if(internalTransferRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(internalTransferRequest)?.message)
                redirect(action: "create")
            }else{


                Map map = [:]

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            internalTransferRequest?.employee?.id,
                            internalTransferRequest.employee?.currentEmploymentRecord?.department?.id,
                            internalTransferRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            internalTransferRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            InternalTransferRequest.getName(),
                            internalTransferRequest?.id,
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
                map.internalTransferRequest = internalTransferRequest

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
        PagedResultList pagedResultList = internalTransferRequestService.searchWithRemotingValues(params)
        render text: (internalTransferRequestService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        InternalTransferRequest internalTransferRequest = internalTransferRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), internalTransferRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), internalTransferRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(internalTransferRequest, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (internalTransferRequest?.hasErrors()) {
                respond internalTransferRequest, view:'create'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId){
            params['requestStatus'] = EnumRequestStatus.CREATED.toString()
            InternalTransferRequest internalTransferRequest = internalTransferRequestService.getInstanceWithRemotingValues(params)
            if(internalTransferRequest){
                respond internalTransferRequest
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        InternalTransferRequest internalTransferRequest = internalTransferRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), internalTransferRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), internalTransferRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(internalTransferRequest,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (internalTransferRequest.hasErrors()) {
                respond internalTransferRequest, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        params['requestStatus'] = EnumRequestStatus.CREATED.toString()
        DeleteBean deleteBean = internalTransferRequestService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (internalTransferRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * close request action
     */
    def close = {
        if(params.encodedId){
            params['requestStatus'] = EnumRequestStatus.APPROVED_BY_WORKFLOW.toString()
            InternalTransferRequest internalTransferRequest = internalTransferRequestService.getInstanceWithRemotingValues(params)
            if(internalTransferRequest){
                respond internalTransferRequest
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * save close request action info
     */
    def saveClose = {
        params.closeRequest = true
        InternalTransferRequest internalTransferRequest = internalTransferRequestService.save(params)
        String successMessage = message(code: 'internalTransferRequest.closeRequest.message')
        String failMessage = message(code: 'internalTransferRequest.not.closeRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(internalTransferRequest,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

