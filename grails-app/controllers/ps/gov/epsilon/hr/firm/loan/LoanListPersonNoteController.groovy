package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route LoanListPersonNote requests between model and views.
 *@see LoanListPersonNoteService
 *@see FormatService
**/
class LoanListPersonNoteController  {

    LoanListPersonNoteService loanListPersonNoteService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    /**
     * default action in controller
     */
    def index= {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list= {}

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            LoanListPersonNote loanListPersonNote = loanListPersonNoteService.getInstance(params)
            if(loanListPersonNote){
                respond loanListPersonNote
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new LoanListPersonNote(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = loanListPersonNoteService.search(params)
        render text: (loanListPersonNoteService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        LoanListPersonNote loanListPersonNote = loanListPersonNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), loanListPersonNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), loanListPersonNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanListPersonNote, successMessage, failMessage, true) as JSON), contentType: "application/json"
        }
        else {
            if (loanListPersonNote?.hasErrors()) {
                respond loanListPersonNote, view:'create'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId){
            LoanListPersonNote loanListPersonNote = loanListPersonNoteService.getInstance(params)
            if(loanListPersonNote){
                respond loanListPersonNote
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        LoanListPersonNote loanListPersonNote = loanListPersonNoteService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), loanListPersonNote?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), loanListPersonNote?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanListPersonNote,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (loanListPersonNote.hasErrors()) {
                respond loanListPersonNote, view:'edit'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = loanListPersonNoteService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), params?.id,deleteBean.responseMessage?:""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

