package ps.gov.epsilon.hr.firm.child

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ChildListEmployeeNote requests between model and views.
 *@see ChildListEmployeeNoteService
 *@see FormatService
**/
class ChildListEmployeeNoteController  {

    ChildListEmployeeNoteService childListEmployeeNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            ChildListEmployeeNote childListEmployeeNote = childListEmployeeNoteService.getInstance(params)
            if(childListEmployeeNote){
                respond childListEmployeeNote
                return
            }
        }else{
            notFound()
        }
    }

    def create = {
        respond new ChildListEmployeeNote(params)
    }

    def filter = {
        PagedResultList pagedResultList = childListEmployeeNoteService.search(params)
        render text: (childListEmployeeNoteService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        params["childListEmployee.id"] = params["save_childListEmployeeId"];
        params["firm.id"] = session.getAttribute("firmId")?:1L
        ChildListEmployeeNote childListEmployeeNote = childListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), childListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), childListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(childListEmployeeNote, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (childListEmployeeNote?.hasErrors()) {
                respond childListEmployeeNote, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            ChildListEmployeeNote childListEmployeeNote = childListEmployeeNoteService.getInstance(params)
            if(childListEmployeeNote){
                respond childListEmployeeNote
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        ChildListEmployeeNote childListEmployeeNote = childListEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), childListEmployeeNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), childListEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(childListEmployeeNote,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (childListEmployeeNote.hasErrors()) {
                respond childListEmployeeNote, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = childListEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (childListEmployeeNoteService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

