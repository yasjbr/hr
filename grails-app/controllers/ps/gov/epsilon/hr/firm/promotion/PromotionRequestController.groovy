package ps.gov.epsilon.hr.firm.promotion

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route PromotionRequest requests between model and views.
 * @see PromotionRequestService
 * @see FormatService
 * */
class PromotionRequestController {

    PromotionRequestService promotionRequestService
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
        List<EnumRequestType> enumRequestTypeList = new ArrayList<EnumRequestType>()
        enumRequestTypeList.push(EnumRequestType.SITUATION_SETTLEMENT)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST)
        enumRequestTypeList.push(EnumRequestType.EXCEPTIONAL_REQUEST)
        Map map = sharedService.getAttachmentTypeListAsMap(PromotionRequest.getName(), EnumOperation.PROMOTION_REQUEST)
        map.enumRequestTypeList = enumRequestTypeList
        respond map
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            PromotionRequest promotionRequest = promotionRequestService.getInstanceWithRemotingValues(params)
            if (promotionRequest) {
                respond promotionRequest
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
        List<EnumRequestType> enumRequestTypeList = new ArrayList<EnumRequestType>()
        enumRequestTypeList.push(EnumRequestType.SITUATION_SETTLEMENT)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD)
        enumRequestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST)
        enumRequestTypeList.push(EnumRequestType.EXCEPTIONAL_REQUEST)
        Map data = [type: "create", promotionRequest: new PromotionRequest(params), enumRequestTypeList: enumRequestTypeList]
        respond data
    }

    /**
     * this action used to create new updateMilitaryRankType Request for the selected employee
     */
    def createNewRequest = {
        if (params["employeeId"]) {
            PromotionRequest promotionRequest = promotionRequestService.getPreCreateInstance(params)
            if (promotionRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(promotionRequest)?.message)
                redirect(action: "create")
            } else {

                Map map = [:]

                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            promotionRequest?.employee?.id,
                            promotionRequest.employee?.currentEmploymentRecord?.department?.id,
                            promotionRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            promotionRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            PromotionRequest.getName(),
                            promotionRequest?.id,
                            false,
                            "${promotionRequest?.requestType}")

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
                map.promotionRequest = promotionRequest

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

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = promotionRequestService.searchWithRemotingValues(params)
        render text: (promotionRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        PromotionRequest promotionRequest = promotionRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), promotionRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), promotionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (promotionRequest?.hasErrors()) {
                respond promotionRequest, view: 'create'
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
            PromotionRequest promotionRequest = promotionRequestService.getInstanceWithRemotingValues(params)
            if (promotionRequest?.requestStatus == EnumRequestStatus.CREATED) {
                respond promotionRequest
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
        PromotionRequest promotionRequest = promotionRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), promotionRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), promotionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (promotionRequest.hasErrors()) {
                respond promotionRequest, view: 'edit'
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
        DeleteBean deleteBean = promotionRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (promotionRequestService.autoComplete(params)), contentType: "application/json"
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'promotionRequest.entity', default: 'PromotionRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

