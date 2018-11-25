package ps.gov.epsilon.hr.firm.settings

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route JoinedFirmOperationDocument requests between model and views.
 * @see JoinedFirmOperationDocumentService
 * @see FormatService
 * */
class JoinedFirmOperationDocumentController {

    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params['transientData.operation']) {
            JoinedFirmOperationDocument joinedFirmOperationDocument = joinedFirmOperationDocumentService.getInstanceByOperation(params)
            if (joinedFirmOperationDocument) {
                respond joinedFirmOperationDocument
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def create = {
         respond new JoinedFirmOperationDocument(params)
    }

    def filter = {
        PagedResultList pagedResultList = joinedFirmOperationDocumentService.searchWithoutDuplicate(params)
        render text: (joinedFirmOperationDocumentService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        JoinedFirmOperationDocument joinedFirmOperationDocument = joinedFirmOperationDocumentService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument'), joinedFirmOperationDocument?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument'), joinedFirmOperationDocument?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedFirmOperationDocument, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (joinedFirmOperationDocument?.hasErrors()) {
                respond joinedFirmOperationDocument, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params['transientData.operation']) {
            JoinedFirmOperationDocument joinedFirmOperationDocument = joinedFirmOperationDocumentService.getInstanceByOperation(params)
            if (joinedFirmOperationDocument) {
                respond joinedFirmOperationDocument
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        JoinedFirmOperationDocument joinedFirmOperationDocument = joinedFirmOperationDocumentService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument'), joinedFirmOperationDocument?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument'), joinedFirmOperationDocument?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedFirmOperationDocument, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (joinedFirmOperationDocument.hasErrors()) {
                respond joinedFirmOperationDocument, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }


    def autocomplete = {
        render text: (joinedFirmOperationDocumentService.autoComplete(params)), contentType: "application/json"
    }


    def getOperationSelectElement = {
        render joinedFirmOperationDocumentTagLib.getOperationSelectElement()
    }


    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

