package ps.gov.epsilon.hr.firm.settings

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route FirmDocument requests between model and views.
 * @see FirmDocumentService
 * @see FormatService
 * */
class FirmDocumentController {

    FirmDocumentService firmDocumentService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            FirmDocument firmDocument = firmDocumentService.getInstance(params)
            if (firmDocument) {
                respond firmDocument
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new FirmDocument(params)
    }

    def filter = {
        PagedResultList pagedResultList = firmDocumentService.search(params)
        render text: (firmDocumentService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        FirmDocument firmDocument = firmDocumentService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), firmDocument?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), firmDocument?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(firmDocument, successMessage, failMessage, true, getControllerName(),"create") as JSON), contentType: "application/json"
        } else {
            if (firmDocument?.hasErrors()) {
                respond firmDocument, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            FirmDocument firmDocument = firmDocumentService.getInstance(params)
            if (firmDocument) {
                respond firmDocument
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        FirmDocument firmDocument = firmDocumentService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), firmDocument?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), firmDocument?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(firmDocument, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (firmDocument.hasErrors()) {
                respond firmDocument, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = firmDocumentService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (firmDocumentService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'firmDocument.entity', default: 'FirmDocument'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

