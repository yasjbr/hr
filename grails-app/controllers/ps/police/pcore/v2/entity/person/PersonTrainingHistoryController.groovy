package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonTrainingHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonTrainingHistoryDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonTrainingHistory requests between model and views.
 *@see PersonTrainingHistoryService
 *@see FormatService
 **/
class PersonTrainingHistoryController  {

    PersonTrainingHistoryService personTrainingHistoryService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonTrainingHistoryDTO personTrainingHistory = personTrainingHistoryService.getPersonTrainingHistory(PCPUtils.convertParamsToSearchBean(params))
            respond personTrainingHistory
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonTrainingHistoryCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personTrainingHistoryService.searchPersonTrainingHistory(PCPUtils.convertParamsToSearchBean(params))
        render text: (personTrainingHistoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonTrainingHistoryCommand personTrainingHistory = new PersonTrainingHistoryCommand()

        bindData(personTrainingHistory,params)
        PCPUtils.bindZonedDateTimeFields(personTrainingHistory,params)

        if(personTrainingHistory.validate()) {
            personTrainingHistory = personTrainingHistoryService.savePersonTrainingHistory(personTrainingHistory)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), personTrainingHistory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), personTrainingHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personTrainingHistory, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personTrainingHistory?.hasErrors()) {
                respond personTrainingHistory, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personTrainingHistoryService.getPersonTrainingHistory(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonTrainingHistoryCommand personTrainingHistory = new PersonTrainingHistoryCommand()

        bindData(personTrainingHistory,params)
        PCPUtils.bindZonedDateTimeFields(personTrainingHistory,params)

        if(personTrainingHistory.validate()) {
            personTrainingHistory = personTrainingHistoryService.savePersonTrainingHistory(personTrainingHistory)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), personTrainingHistory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), personTrainingHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personTrainingHistory,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personTrainingHistory.hasErrors()) {
                respond personTrainingHistory, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personTrainingHistoryService.deletePersonTrainingHistory(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personTrainingHistoryService.autoCompletePersonTrainingHistory(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

