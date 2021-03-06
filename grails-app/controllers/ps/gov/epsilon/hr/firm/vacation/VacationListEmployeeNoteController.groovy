package ps.gov.epsilon.hr.firm.vacation

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route VacationListEmployeeNote requests between model and views.
 * @see VacationListEmployeeNoteService
 * @see FormatService
 * */
class VacationListEmployeeNoteController {

    VacationListEmployeeNoteService vacationListEmployeeNoteService
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
            VacationListEmployeeNote vacationListEmployeeNote = vacationListEmployeeNoteService.getInstance(params)
            if (vacationListEmployeeNote) {
                respond vacationListEmployeeNote
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
        respond new VacationListEmployeeNote(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = vacationListEmployeeNoteService.search(params)
        render text: (vacationListEmployeeNoteService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        VacationListEmployeeNote vacationListEmployeeNote = vacationListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), vacationListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), vacationListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationListEmployeeNote, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (vacationListEmployeeNote?.hasErrors()) {
                respond vacationListEmployeeNote, view: 'create'
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
            VacationListEmployeeNote vacationListEmployeeNote = vacationListEmployeeNoteService.getInstance(params)
            if (vacationListEmployeeNote) {
                respond vacationListEmployeeNote
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
        VacationListEmployeeNote vacationListEmployeeNote = vacationListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), vacationListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), vacationListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationListEmployeeNote, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacationListEmployeeNote.hasErrors()) {
                respond vacationListEmployeeNote, view: 'edit'
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
        DeleteBean deleteBean = vacationListEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), params?.id, deleteBean.responseMessage ?: ""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

