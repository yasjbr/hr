package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonHealthHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonHealthHistoryDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonHealthHistory requests between model and views.
 *@see PersonHealthHistoryService
 *@see FormatService
 **/
class PersonHealthHistoryController  {

    PersonHealthHistoryService personHealthHistoryService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonHealthHistoryDTO personHealthHistory = personHealthHistoryService.getPersonHealthHistory(PCPUtils.convertParamsToSearchBean(params))
            respond personHealthHistory
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonHealthHistoryCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personHealthHistoryService.searchPersonHealthHistory(PCPUtils.convertParamsToSearchBean(params))
        render text: (personHealthHistoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonHealthHistoryCommand personHealthHistory = new PersonHealthHistoryCommand()

        bindData(personHealthHistory,params)
        PCPUtils.bindZonedDateTimeFields(personHealthHistory,params)

        if(personHealthHistory.validate()) {
            personHealthHistory = personHealthHistoryService.savePersonHealthHistory(personHealthHistory)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), personHealthHistory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), personHealthHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personHealthHistory, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personHealthHistory?.hasErrors()) {
                respond personHealthHistory, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personHealthHistoryService.getPersonHealthHistory(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonHealthHistoryCommand personHealthHistory = new PersonHealthHistoryCommand()

        bindData(personHealthHistory,params)
        PCPUtils.bindZonedDateTimeFields(personHealthHistory,params)

        if(personHealthHistory.validate()) {
            personHealthHistory = personHealthHistoryService.savePersonHealthHistory(personHealthHistory)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), personHealthHistory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), personHealthHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personHealthHistory,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personHealthHistory.hasErrors()) {
                respond personHealthHistory, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personHealthHistoryService.deletePersonHealthHistory(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personHealthHistoryService.autoCompletePersonHealthHistory(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

