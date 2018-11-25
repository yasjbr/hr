package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequest

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route LoanRequestRelatedPerson requests between model and views.
 *@see LoanRequestRelatedPersonService
 *@see FormatService
**/
class LoanRequestRelatedPersonController  {

    LoanRequestRelatedPersonService loanRequestRelatedPersonService
    FormatService formatService
    SharedService sharedService

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
    def list= {
        respond sharedService.getAttachmentTypeListAsMap(LoanRequestRelatedPerson.getName(), EnumOperation.LOAN_REQUEST_RELATED_PERSON)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            LoanRequestRelatedPerson loanRequestRelatedPerson = loanRequestRelatedPersonService.getInstance(params)
            if(loanRequestRelatedPerson){
                respond loanRequestRelatedPerson
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        params['justApprovedPerson'] = 'true'
        PagedResultList pagedResultList = loanRequestRelatedPersonService.searchWithRemotingValues(params)
        render text: (loanRequestRelatedPersonService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        LoanRequestRelatedPerson loanRequestRelatedPerson = loanRequestRelatedPersonService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), loanRequestRelatedPerson?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), loanRequestRelatedPerson?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanRequestRelatedPerson, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (loanRequestRelatedPerson?.hasErrors()) {
                respond loanRequestRelatedPerson, view:'create'
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
            LoanRequestRelatedPerson loanRequestRelatedPerson = loanRequestRelatedPersonService.getInstance(params)
            if(loanRequestRelatedPerson){
                respond loanRequestRelatedPerson
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
        LoanRequestRelatedPerson loanRequestRelatedPerson = loanRequestRelatedPersonService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), loanRequestRelatedPerson?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), loanRequestRelatedPerson?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanRequestRelatedPerson,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (loanRequestRelatedPerson.hasErrors()) {
                respond loanRequestRelatedPerson, view:'edit'
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
        DeleteBean deleteBean = loanRequestRelatedPersonService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), params?.id,deleteBean.responseMessage?:""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

