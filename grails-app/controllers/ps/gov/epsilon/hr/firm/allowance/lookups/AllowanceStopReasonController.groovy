package ps.gov.epsilon.hr.firm.allowance.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route AllowanceStopReason requests between model and views.
 *@see AllowanceStopReasonService
 *@see FormatService
**/
class AllowanceStopReasonController  {

    AllowanceStopReasonService allowanceStopReasonService
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
            AllowanceStopReason allowanceStopReason = allowanceStopReasonService.getInstance(params)
            if(allowanceStopReason){
                respond allowanceStopReason
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
        respond new AllowanceStopReason(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = allowanceStopReasonService.search(params)
        render text: (allowanceStopReasonService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        AllowanceStopReason allowanceStopReason = allowanceStopReasonService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason'), allowanceStopReason?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason'), allowanceStopReason?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceStopReason, successMessage, failMessage, true, getControllerName(),"create") as JSON), contentType: "application/json"
        }
        else {
            if (allowanceStopReason?.hasErrors()) {
                respond allowanceStopReason, view:'create'
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
            AllowanceStopReason allowanceStopReason = allowanceStopReasonService.getInstance(params)
            if(allowanceStopReason){
                respond allowanceStopReason
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
        AllowanceStopReason allowanceStopReason = allowanceStopReasonService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason'), allowanceStopReason?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason'), allowanceStopReason?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceStopReason,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (allowanceStopReason.hasErrors()) {
                respond allowanceStopReason, view:'edit'
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
        DeleteBean deleteBean = allowanceStopReasonService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason')])
        String failMessage = message(code:'virtualDelete.error.fail.delete.label')
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
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (allowanceStopReasonService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

