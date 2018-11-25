package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route TrainingListEmployeeNote requests between model and views.
 * @see TrainingListEmployeeNoteService
 * @see FormatService
 * */
class TrainingListEmployeeNoteController {

    TrainingListEmployeeNoteService trainingListEmployeeNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            TrainingListEmployeeNote trainingListEmployeeNote = trainingListEmployeeNoteService.getInstance(params)
            if (trainingListEmployeeNote) {
                respond trainingListEmployeeNote
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new TrainingListEmployeeNote(params)
    }

    def filter = {
        PagedResultList pagedResultList = trainingListEmployeeNoteService.search(params)
        render text: (trainingListEmployeeNoteService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        TrainingListEmployeeNote trainingListEmployeeNote = trainingListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), trainingListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), trainingListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(trainingListEmployeeNote, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (trainingListEmployeeNote?.hasErrors()) {
                respond trainingListEmployeeNote, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            TrainingListEmployeeNote trainingListEmployeeNote = trainingListEmployeeNoteService.getInstance(params)
            if (trainingListEmployeeNote) {
                respond trainingListEmployeeNote
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        TrainingListEmployeeNote trainingListEmployeeNote = trainingListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), trainingListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), trainingListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(trainingListEmployeeNote, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (trainingListEmployeeNote.hasErrors()) {
                respond trainingListEmployeeNote, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = trainingListEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), params?.id, deleteBean.responseMessage ?: ""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

