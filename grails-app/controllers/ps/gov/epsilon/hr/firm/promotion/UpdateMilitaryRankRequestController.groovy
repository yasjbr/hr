package ps.gov.epsilon.hr.firm.promotion

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
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
 * Route UpdateMilitaryRankRequest requests between model and views.
 * @see UpdateMilitaryRankRequestService
 * @see FormatService
 * */
class UpdateMilitaryRankRequestController {

    UpdateMilitaryRankRequestService updateMilitaryRankRequestService
    FormatService formatService
    SharedService sharedService
    EmployeeService employeeService
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
        List<EnumRequestType> enumRequestTypeList = new ArrayList<EnumRequestType>()
        enumRequestTypeList.push(EnumRequestType.UPDATE_MILITARY_RANK_TYPE)
        enumRequestTypeList.push(EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION)
        Map map = sharedService.getAttachmentTypeListAsMap(UpdateMilitaryRankRequest.getName(), EnumOperation.PROMOTION_REQUEST)
        map.enumRequestTypeList = enumRequestTypeList
        respond map
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            UpdateMilitaryRankRequest updateMilitaryRankRequest = updateMilitaryRankRequestService.getInstanceWithRemotingValues(params)
            if (updateMilitaryRankRequest) {
                respond updateMilitaryRankRequest
                return
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance with allowed request types to be selected before create new request.
     */
    def create = {
        List<EnumRequestType> enumRequestTypeList = new ArrayList<EnumRequestType>()
        enumRequestTypeList.push(EnumRequestType.UPDATE_MILITARY_RANK_TYPE)
        enumRequestTypeList.push(EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION)
        Map data = [type: "create", updateMilitaryRankRequest: new UpdateMilitaryRankRequest(params), enumRequestTypeList: enumRequestTypeList]
        respond data
    }

    /**
     * this action used to create new updateMilitaryRankType Request for the selected employee
     */
    def createNewRequest = {
        if (params["employeeId"]) {
            UpdateMilitaryRankRequest updateMilitaryRankRequest = updateMilitaryRankRequestService.getPreCreateInstance(params)
            if (updateMilitaryRankRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(updateMilitaryRankRequest)?.message)
                redirect(action: "create")
            } else {

                Map map = [:]

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            updateMilitaryRankRequest?.employee?.id,
                            updateMilitaryRankRequest.employee?.currentEmploymentRecord?.department?.id,
                            updateMilitaryRankRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            updateMilitaryRankRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            UpdateMilitaryRankRequest.getName(),
                            updateMilitaryRankRequest?.id,
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
                map.updateMilitaryRankRequest = updateMilitaryRankRequest

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
            render text: ([success: true, employeeId: params["employeeId"], requestType: params["requestType"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    def filter = {
        PagedResultList pagedResultList = updateMilitaryRankRequestService.searchWithRemotingValues(params)
        render text: (updateMilitaryRankRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        UpdateMilitaryRankRequest updateMilitaryRankRequest = updateMilitaryRankRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), updateMilitaryRankRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), updateMilitaryRankRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(updateMilitaryRankRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (updateMilitaryRankRequest?.hasErrors()) {
                respond updateMilitaryRankRequest, view: 'create'
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
            UpdateMilitaryRankRequest updateMilitaryRankRequest = updateMilitaryRankRequestService.getInstanceWithRemotingValues(params)
            if (updateMilitaryRankRequest?.requestStatus == EnumRequestStatus.CREATED) {
                respond updateMilitaryRankRequest
                return
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
        UpdateMilitaryRankRequest updateMilitaryRankRequest = updateMilitaryRankRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), updateMilitaryRankRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), updateMilitaryRankRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(updateMilitaryRankRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (updateMilitaryRankRequest.hasErrors()) {
                respond updateMilitaryRankRequest, view: 'edit'
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
        DeleteBean deleteBean = updateMilitaryRankRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (updateMilitaryRankRequestService.autoComplete(params)), contentType: "application/json"
    }

    /** this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "promotion", PromotionListEmployee, false, "request"))
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

