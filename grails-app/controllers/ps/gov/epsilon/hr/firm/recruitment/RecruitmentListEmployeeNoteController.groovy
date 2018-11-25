package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route RecruitmentListEmployeeNote requests between model and views.
 * @see RecruitmentListEmployeeNoteService
 * @see FormatService
 * */
class RecruitmentListEmployeeNoteController {

    RecruitmentListEmployeeNoteService recruitmentListEmployeeNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            RecruitmentListEmployeeNote recruitmentListEmployeeNote = recruitmentListEmployeeNoteService.getInstance(params)
            if (recruitmentListEmployeeNote) {
                respond recruitmentListEmployeeNote
            } else {
                notFound()

            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new RecruitmentListEmployeeNote(params)
    }

    def filter = {
        PagedResultList pagedResultList = recruitmentListEmployeeNoteService.search(params)
        render text: (recruitmentListEmployeeNoteService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        RecruitmentListEmployeeNote recruitmentListEmployeeNote = recruitmentListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), recruitmentListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), recruitmentListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentListEmployeeNote, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (recruitmentListEmployeeNote?.hasErrors()) {
                respond recruitmentListEmployeeNote, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            RecruitmentListEmployeeNote recruitmentListEmployeeNote = recruitmentListEmployeeNoteService.getInstance(params)
            if (recruitmentListEmployeeNote) {
                respond recruitmentListEmployeeNote
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        RecruitmentListEmployeeNote recruitmentListEmployeeNote = recruitmentListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), recruitmentListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), recruitmentListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentListEmployeeNote, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentListEmployeeNote.hasErrors()) {
                respond recruitmentListEmployeeNote, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = recruitmentListEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), params?.id, deleteBean.responseMessage ?: ""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

