package ps.gov.epsilon.hr.firm.suspension

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route SuspensionExtensionRequest requests between model and views.
 * @see SuspensionExtensionRequestService
 * @see FormatService
 * */
class SuspensionExtensionRequestController {

    SuspensionExtensionRequestService suspensionExtensionRequestService
    FormatService formatService

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
    def list = {}

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            SuspensionExtensionRequest suspensionExtensionRequest = suspensionExtensionRequestService.getInstanceWithRemotingValues(params)
            if (suspensionExtensionRequest) {
                respond suspensionExtensionRequest
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
        respond new SuspensionExtensionRequest(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = suspensionExtensionRequestService.searchWithRemotingValues(params)
        render text: (suspensionExtensionRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        SuspensionExtensionRequest suspensionExtensionRequest = suspensionExtensionRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), suspensionExtensionRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), suspensionExtensionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionRequest?.hasErrors()) {
                respond suspensionExtensionRequest, view: 'create'
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
            SuspensionExtensionRequest suspensionExtensionRequest = suspensionExtensionRequestService.getInstanceWithRemotingValues(params)
            if (suspensionExtensionRequest) {
                respond suspensionExtensionRequest
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
        SuspensionExtensionRequest suspensionExtensionRequest = suspensionExtensionRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), suspensionExtensionRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), suspensionExtensionRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionRequest.hasErrors()) {
                respond suspensionExtensionRequest, view: 'edit'
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
        DeleteBean deleteBean = suspensionExtensionRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), params?.id, ""])
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
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

