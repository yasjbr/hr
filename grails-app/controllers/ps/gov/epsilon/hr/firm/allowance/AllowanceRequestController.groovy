package ps.gov.epsilon.hr.firm.allowance

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceTypeService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route AllowanceRequest requests between model and views.
 * @see AllowanceRequestService
 * @see FormatService
 * */
class AllowanceRequestController {

    AllowanceRequestService allowanceRequestService
    FormatService formatService
    EmployeeService employeeService
    AllowanceListEmployeeService allowanceListEmployeeService
    SharedService sharedService
    AllowanceTypeService allowanceTypeService
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
        respond sharedService.getAttachmentTypeListAsMap(AllowanceRequest.getName(), EnumOperation.ALLOWANCE_REQUEST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        def map
        PagedResultList pagedResultList
        if (params.encodedId) {
            AllowanceRequest allowanceRequest = allowanceRequestService.getInstanceWithRemotingValues(params)
            if (allowanceRequest) {
                /**
                 * get allowance list employee by allowance request
                 */
                GrailsParameterMap allowanceListEmployeeParam = new GrailsParameterMap(['allowanceRequest.id': allowanceRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                pagedResultList = allowanceListEmployeeService?.search(allowanceListEmployeeParam)

                if (pagedResultList?.resultList?.size() > 0) {
                    map = [allowanceRequest: allowanceRequest, allowanceListEmployee: pagedResultList?.resultList?.get(0)]
                } else {
                    map = [allowanceRequest: allowanceRequest]
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
            PagedResultList<AllowanceRequest> allowanceRequestPagedResultList = allowanceRequestService.getThreadWithRemotingValues(params)
            if (allowanceRequestPagedResultList) {
                return [allowanceRequestList: allowanceRequestPagedResultList?.resultList]
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new AllowanceRequest(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = allowanceRequestService.searchWithRemotingValues(params)
        render text: (allowanceRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filterCanHaveOperation = {
        PagedList pagedResultList = allowanceRequestService.searchCanHaveOperation(params)
        render text: (allowanceRequestService.resultListToMap(pagedResultList, params, allowanceRequestService.LITE_DOMAIN_COLUMNS) as JSON),
                contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        AllowanceRequest allowanceRequest = allowanceRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), allowanceRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), allowanceRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (allowanceRequest?.hasErrors()) {
                respond allowanceRequest, view: 'create'
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
        String failMessage = message(code: 'request.fail.edit.message', args: [])
        if (params.encodedId) {
            AllowanceRequest allowanceRequest = allowanceRequestService.getInstanceWithRemotingValues(params)
            if (allowanceRequest && (allowanceRequest?.requestStatus == EnumRequestStatus.CREATED || allowanceRequest?.requestStatus == EnumRequestStatus.IN_PROGRESS)) {
                respond allowanceRequest
                return
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
        AllowanceRequest allowanceRequest = allowanceRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), allowanceRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), allowanceRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (allowanceRequest.hasErrors()) {
                respond allowanceRequest, view: 'edit'
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
        DeleteBean deleteBean = allowanceRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
     * this action is used to return employee info to be used in create allowance request
     */
    def getEmployee = {
        if (params["employeeId"] && params["allowanceType.id"]) {
            render text: ([success: true, employeeId: params["employeeId"], allowanceTypeId: params["allowanceType.id"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'allowanceRequest.employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new allowance request for the selected employee
     */
    def createNewAllowanceRequest = {

        GrailsParameterMap mapParam
        Map map = [:]
        if (params["employeeId"] && params["allowanceTypeId"]) {

            AllowanceRequest allowanceRequest = new AllowanceRequest()
            /**
             * get selected employee
             */
            mapParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            allowanceRequest.employee = employeeService?.getInstanceWithRemotingValues(mapParam)

            /**
             * get selected allowance type
             */
            mapParam = new GrailsParameterMap([id: params["allowanceTypeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            allowanceRequest.allowanceType = allowanceTypeService?.getInstance(mapParam)

            /**
             * in case: if the user has HR role, get the suitable workflow
             */
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        allowanceRequest?.employee?.id,
                        allowanceRequest.employee?.currentEmploymentRecord?.department?.id,
                        allowanceRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                        allowanceRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                        AllowanceRequest.getName(),
                        allowanceRequest?.id,
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
            map.allowanceRequest = allowanceRequest

            /**
             * respond map
             */
            respond(map)
        } else {
            notFound()
        }
    }

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (allowanceRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            AllowanceRequest allowanceRequest= allowanceRequestService.getInstance(params)
            if(allowanceRequest?.includedInList){
                redirect(sharedService.goToList(params["encodedId"]?.toString(), "allowance", AllowanceListEmployee))
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * get relationships for employee
     */
    def employeeRelationShipsAutoComplete = {
        render text: (allowanceRequestService.employeeRelationShipsAutoComplete(params)), contentType: "application/json"
    }

    /**
     * render the stop request  modal
     */
    def stopRequestCreate = {
        if (params.id) {
            /**
             * get allowance request
             */
            AllowanceRequest allowanceRequest = allowanceRequestService?.getInstanceWithRemotingValues(params)

            if(allowanceRequest?.canStopRequest){
                return [allowanceRequest:allowanceRequest, requestType:EnumRequestType.ALLOWANCE_STOP_REQUEST]
            }else{
                log.error("Cannot stop allowance request " + allowanceRequest?.id)
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'allowanceRequest.entity', default: 'AllowanceRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

