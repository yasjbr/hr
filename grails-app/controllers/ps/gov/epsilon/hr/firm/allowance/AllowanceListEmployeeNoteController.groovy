package ps.gov.epsilon.hr.firm.allowance

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route AllowanceListEmployeeNote requests between model and views.
 * @see AllowanceListEmployeeNoteService
 * @see FormatService
 * */
class AllowanceListEmployeeNoteController {

    AllowanceListEmployeeNoteService allowanceListEmployeeNoteService
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
     * represent the create page empty instance
     */
    def create = {
        respond new AllowanceListEmployeeNote(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = allowanceListEmployeeNoteService.search(params)
        render text: (allowanceListEmployeeNoteService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        AllowanceListEmployeeNote allowanceListEmployeeNote = allowanceListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote'), allowanceListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote'), allowanceListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceListEmployeeNote, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (allowanceListEmployeeNote?.hasErrors()) {
                respond allowanceListEmployeeNote, view: 'create'
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
        DeleteBean deleteBean = allowanceListEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote'), params?.id, deleteBean.responseMessage ?: ""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

