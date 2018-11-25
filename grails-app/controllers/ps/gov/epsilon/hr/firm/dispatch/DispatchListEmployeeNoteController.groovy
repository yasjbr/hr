package ps.gov.epsilon.hr.firm.dispatch

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route DispatchListEmployeeNote requests between model and views.
 *@see DispatchListEmployeeNoteService
 *@see FormatService
**/
class DispatchListEmployeeNoteController  {

    DispatchListEmployeeNoteService dispatchListEmployeeNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            DispatchListEmployeeNote dispatchListEmployeeNote = dispatchListEmployeeNoteService.getInstance(params)
            if(dispatchListEmployeeNote){
                respond dispatchListEmployeeNote
                return
            }
        }else{
            notFound()
        }
    }


    def filter = {
        PagedResultList pagedResultList = dispatchListEmployeeNoteService.search(params)
        render text: (dispatchListEmployeeNoteService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //set the dispatchListEmployeeId value to be used in save. it was saved in different param name in view (modal).
        params["dispatchListEmployee.id"] = params["save_dispatchListEmployeeId"];
        //TODO
        params["firm.id"] = session.getAttribute("firmId")?:1L
        DispatchListEmployeeNote dispatchListEmployeeNote = dispatchListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), dispatchListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), dispatchListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchListEmployeeNote, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (dispatchListEmployeeNote?.hasErrors()) {
                respond dispatchListEmployeeNote, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            DispatchListEmployeeNote dispatchListEmployeeNote = dispatchListEmployeeNoteService.getInstance(params)
            if(dispatchListEmployeeNote){
                respond dispatchListEmployeeNote
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        DispatchListEmployeeNote dispatchListEmployeeNote = dispatchListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), dispatchListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), dispatchListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchListEmployeeNote,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (dispatchListEmployeeNote.hasErrors()) {
                respond dispatchListEmployeeNote, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = dispatchListEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (dispatchListEmployeeNoteService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dispatchListEmployeeNote.entity', default: 'DispatchListEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

