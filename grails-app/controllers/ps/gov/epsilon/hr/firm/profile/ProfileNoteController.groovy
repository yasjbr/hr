package ps.gov.epsilon.hr.firm.profile

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ProfileNote requests between model and views.
 *@see ProfileNoteService
 *@see FormatService
**/
class ProfileNoteController  {

    ProfileNoteService profileNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            ProfileNote profileNote = profileNoteService.getInstance(params)
            if(profileNote){
                respond profileNote
                return
            }
        }else{
            notFound()
        }
    }

    def create = {
        respond new ProfileNote(params)
    }

    def filter = {
        PagedResultList pagedResultList = profileNoteService.search(params)
        render text: (profileNoteService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId")?:1L
        ProfileNote profileNote = profileNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), profileNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), profileNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNote, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (profileNote?.hasErrors()) {
                respond profileNote, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            ProfileNote profileNote = profileNoteService.getInstance(params)
            if(profileNote){
                respond profileNote
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        ProfileNote profileNote = profileNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), profileNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), profileNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNote,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (profileNote.hasErrors()) {
                respond profileNote, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = profileNoteService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (profileNoteService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'profileNote.entity', default: 'ProfileNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

