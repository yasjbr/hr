package ps.gov.epsilon.hr.firm.evaluation.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EvaluationItem requests between model and views.
 *@see EvaluationItemService
 *@see FormatService
**/
class EvaluationItemController  {

    EvaluationItemService evaluationItemService
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
            EvaluationItem evaluationItem = evaluationItemService.getInstance(params)
            if(evaluationItem){
                respond evaluationItem
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
        respond new EvaluationItem(params)
    }

    /**
     * represent the create page empty instance
     */
    def createItemModal = {
        Map map = [:]
        map.evaluationSectionId = params.remove("id")
        EvaluationItem evaluationItem = new EvaluationItem(params)
        map.evaluationItem = evaluationItem
        respond map
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = evaluationItemService.search(params)
        render text: (evaluationItemService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EvaluationItem evaluationItem = evaluationItemService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), evaluationItem?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), evaluationItem?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationItem, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (evaluationItem?.hasErrors()) {
                respond evaluationItem, view:'create'
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
            EvaluationItem evaluationItem = evaluationItemService.getInstance(params)
            if(evaluationItem){
                respond evaluationItem
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
        EvaluationItem evaluationItem = evaluationItemService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), evaluationItem?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), evaluationItem?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationItem,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (evaluationItem.hasErrors()) {
                respond evaluationItem, view:'edit'
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
        DeleteBean deleteBean = evaluationItemService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (evaluationItemService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'evaluationItem.entity', default: 'EvaluationItem'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

