package ps.gov.epsilon.hr.firm.training

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route Trainer requests between model and views.
 *@see TrainerService
 *@see FormatService
**/
class TrainerController  {

    TrainerService trainerService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            Trainer trainer = trainerService.getInstance(params)
            if(trainer){
                respond trainer
                return
            }
        }else{
            notFound()
        }
    }

    def create = {
        respond new Trainer(params)
    }

    def filter = {
        PagedResultList pagedResultList = trainerService.search(params)
        render text: (trainerService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId")?:1L
        Trainer trainer = trainerService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'trainer.entity', default: 'Trainer'), trainer?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'trainer.entity', default: 'Trainer'), trainer?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(trainer, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (trainer?.hasErrors()) {
                respond trainer, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            Trainer trainer = trainerService.getInstance(params)
            if(trainer){
                respond trainer
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        Trainer trainer = trainerService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'trainer.entity', default: 'Trainer'), trainer?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'trainer.entity', default: 'Trainer'), trainer?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(trainer,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (trainer.hasErrors()) {
                respond trainer, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = trainerService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'trainer.entity', default: 'Trainer'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'trainer.entity', default: 'Trainer'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (trainerService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'trainer.entity', default: 'Trainer'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

