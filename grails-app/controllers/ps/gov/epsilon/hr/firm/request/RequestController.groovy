package ps.gov.epsilon.hr.firm.request

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.allowance.AllowanceRequest
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route Request requests between model and views.
 * @see RequestService
 * @see FormatService
 * */
class RequestController {

    RequestService requestService
    FormatService formatService
    RequestChangesHandlerService requestChangesHandlerService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            Request request = requestService.getInstance(params)
            if (request) {
                respond request
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new Request(params)
    }

    def filter = {
        PagedResultList pagedResultList = requestService.searchWithRemotingValues(params)
        render text: (requestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * this action used to get the all requests need to approve by workflow
     */
    def filterWorkflowRequest = {
        PagedList pagedResultList = requestService.getRequestWaitingForApproval(params)
        render text: (requestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * filter used to insert promotion request into promotion list:
     */
    def filterPromotionRequest = {
        params.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW.toString()
        params.requestTypeList = [EnumRequestType.UPDATE_MILITARY_RANK_TYPE, EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION, EnumRequestType.SITUATION_SETTLEMENT, EnumRequestType.PERIOD_SETTLEMENT, EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST, EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD, EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST, EnumRequestType.EXCEPTIONAL_REQUEST]
        filter(params)
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        Request request = requestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'request.entity', default: 'Request'), request?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'request.entity', default: 'Request'), request?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(request, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (request?.hasErrors()) {
                respond request, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            Request request = requestService.getInstance(params)
            if (request) {
                respond request
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        Request request = requestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'request.entity', default: 'Request'), request?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'request.entity', default: 'Request'), request?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(request, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (request.hasErrors()) {
                respond request, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = requestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'request.entity', default: 'Request'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'request.entity', default: 'Request'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (requestService.autoComplete(params)), contentType: "application/json"
    }


    /**
     * to manage request
     */
    def manageRequestModal = {
        Map map = [:]
        GrailsParameterMap workflowPathHeaderParam = new GrailsParameterMap([objectId: params.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        WorkflowPathHeader workflowPathHeader = requestService.getWorkflowPathHeader(workflowPathHeaderParam)
        String domainName = params.controllerName
        Request request = requestService.getInstanceWithRemotingValues(params)
        map = [domainName: domainName, request: ["${domainName}": request], workflowPathHeader: workflowPathHeader]
        respond map
    }


    /**
     * render the stop request  modal
     */
    def stopRequestCreate = {
        if (params.encodedId) {
            /**
             * get request
             */
            EnumRequestCategory requestCategory= EnumRequestCategory.STOP
            params.requestCategory= requestCategory.name()
            Request request = requestChangesHandlerService?.getInstanceWithRemotingValues(params)

            if(request?.canStopRequest){
                return [request:request, requestCategory: requestCategory]
            }else{
                log.error("Cannot stop request " + request?.id)
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * render the continue request  modal
     */
    def extendRequestCreate = {
        if (params.encodedId) {
            /**
             * get request
             */
            EnumRequestCategory requestCategory= EnumRequestCategory.EXTEND
            params.requestCategory= requestCategory.name()
            Request request = requestChangesHandlerService?.getInstanceWithRemotingValues(params)

            if(request?.canExtendRequest){
                return [request:request, requestCategory:requestCategory]
            }else{
                log.error("Cannot extend request " + request?.id)
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * render the cancel request  modal
     */
    def cancelRequestCreate = {
        if (params.encodedId) {
            /**
             * get allowance request
             */
            EnumRequestCategory requestCategory= EnumRequestCategory.CANCEL
            params.requestCategory= requestCategory.name()
            Request request = requestChangesHandlerService?.getInstanceWithRemotingValues(params)

            if(request?.canCancelRequest){
                Boolean showAllLevels= request?.requestStatus == EnumRequestStatus.APPROVED
                return [request:request, requestCategory: requestCategory, showAllLevels:showAllLevels]
            }else{
                // cannot cancel not approved request
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * render the edit request
     */
    def editRequestCreate = {
        if (params.encodedId) {
            /**
             * get request
             */
            EnumRequestCategory requestCategory= EnumRequestCategory.EDIT
            params.requestCategory= requestCategory.name()
            Request request = requestChangesHandlerService?.getInstanceWithRemotingValues(params)

            if(request?.canEditRequest){
                return [request:request, requestCategory:requestCategory]
            }else{
                log.error("Cannot edit request " + request?.id)
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and save instance
     */
    def saveOperation = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        if(!params['firm.id']){
            params["firm.id"] = session.getAttribute("firmId")
        }

        Request requestInstance = requestChangesHandlerService.saveOperation(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'request.entity', default: 'Request'), requestInstance?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'request.entity', default: 'Request'), requestInstance?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(requestInstance, successMessage, failMessage, true, requestInstance?.requestType?.domain, "list") as JSON), contentType: "application/json"
        }
    }

    /**
     * set internal order number for a request
     * Params:
     * 1. encodedId of the request
     * 2. dataTableId to be reloaded
     */
    def setInternalManagerialOrder= {
        Request requestInstance = requestService.getInstance(params)
        if(!requestInstance){
            render notFound()
        } else if(requestInstance.internalOrderNumber){
            params.failMessage= message(code:'request.orderNumber.already.exists.error.message')
            render notFound()
        }
        [requestId:requestInstance?.encodedId, requestTypeDescription:requestInstance.requestTypeDescription, dataTableId:params.dataTableId]
    }

    /**
     * set internal order number for a request
     * Params:
     * 1. encodedId of the request
     * 2. dataTableId to be reloaded
     */
    def setExternalManagerialOrder= {
        Request requestInstance = requestService.getInstance(params)
        if(!requestInstance){
            render notFound()
        } else if(requestInstance.externalOrderNumber){
            params.failMessage= message(code:'request.orderNumber.already.exists.error.message')
            render notFound()
        }
        [requestId:requestInstance?.encodedId, requestTypeDescription:requestInstance.requestTypeDescription, dataTableId:params.dataTableId]
    }

    /**
     * saves internal order number for a request
     */
    def saveInternalManagerialOrder= {
        Request requestInstance = requestService.saveManagerialOrderInfo(params)
        String successMessage = message(code: 'request.managerialOrderInfo.saved.message', args: [requestInstance?.id])
        String failMessage = message(code: 'request.managerialOrderInfo.not.saved.message', args: [requestInstance?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(requestInstance, successMessage, failMessage, true, requestInstance?.requestType?.domain, "list") as JSON), contentType: "application/json"
        }
    }

    /**
     * saves internal order number for a request
     */
    def saveExternalManagerialOrder= {
        Request requestInstance = requestService.saveManagerialOrderInfo(params)
        String successMessage = message(code: 'request.managerialOrderInfo.saved.message', args: [requestInstance?.id])
        String failMessage = message(code: 'request.managerialOrderInfo.not.saved.message', args: [requestInstance?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(requestInstance, successMessage, failMessage, true, requestInstance?.requestType?.domain, "list") as JSON), contentType: "application/json"
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        if(params.failMessage){
            flash.message = params.failMessage
        }
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'request.entity', default: 'Request'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

