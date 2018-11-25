package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route MaritalStatusEmployeeNote requests between model and views.
 *@see MaritalStatusEmployeeNoteService
 *@see FormatService
**/
class MaritalStatusEmployeeNoteController  {

    MaritalStatusEmployeeNoteService maritalStatusEmployeeNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            MaritalStatusEmployeeNote maritalStatusEmployeeNote = maritalStatusEmployeeNoteService.getInstance(params)
            if(maritalStatusEmployeeNote){
                respond maritalStatusEmployeeNote
                return
            }
        }else{
            notFound()
        }
    }

    def create = {
        respond new MaritalStatusEmployeeNote(params)
    }

    def filter = {
        PagedResultList pagedResultList = maritalStatusEmployeeNoteService.search(params)
        render text: (maritalStatusEmployeeNoteService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        params["maritalStatusListEmployee.id"] = params["save_maritalStatusListEmployeeId"]
        params["firm.id"] = session.getAttribute("firmId")?:1L
        MaritalStatusEmployeeNote maritalStatusEmployeeNote = maritalStatusEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), maritalStatusEmployeeNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), maritalStatusEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusEmployeeNote, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusEmployeeNote?.hasErrors()) {
                respond maritalStatusEmployeeNote, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            MaritalStatusEmployeeNote maritalStatusEmployeeNote = maritalStatusEmployeeNoteService.getInstance(params)
            if(maritalStatusEmployeeNote){
                respond maritalStatusEmployeeNote
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        MaritalStatusEmployeeNote maritalStatusEmployeeNote = maritalStatusEmployeeNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), maritalStatusEmployeeNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), maritalStatusEmployeeNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusEmployeeNote,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusEmployeeNote.hasErrors()) {
                respond maritalStatusEmployeeNote, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = maritalStatusEmployeeNoteService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (maritalStatusEmployeeNoteService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

