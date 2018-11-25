package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonEmploymentHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonEmploymentHistoryDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonEmploymentHistory requests between model and views.
 *@see PersonEmploymentHistoryService
 *@see FormatService
 **/
class PersonEmploymentHistoryController  {

    PersonEmploymentHistoryService personEmploymentHistoryService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonEmploymentHistoryDTO personEmploymentHistory = personEmploymentHistoryService.getPersonEmploymentHistory(PCPUtils.convertParamsToSearchBean(params))
            respond personEmploymentHistory
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonEmploymentHistoryCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personEmploymentHistoryService.searchPersonEmploymentHistory(PCPUtils.convertParamsToSearchBean(params))
        render text: (personEmploymentHistoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonEmploymentHistoryCommand personEmploymentHistory = new PersonEmploymentHistoryCommand()

        bindData(personEmploymentHistory,params)
        PCPUtils.bindZonedDateTimeFields(personEmploymentHistory,params)

        if(personEmploymentHistory.validate()) {
            personEmploymentHistory = personEmploymentHistoryService.savePersonEmploymentHistory(personEmploymentHistory)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), personEmploymentHistory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), personEmploymentHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personEmploymentHistory, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personEmploymentHistory?.hasErrors()) {
                respond personEmploymentHistory, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personEmploymentHistoryService.getPersonEmploymentHistory(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonEmploymentHistoryCommand personEmploymentHistory = new PersonEmploymentHistoryCommand()

        bindData(personEmploymentHistory,params)
        PCPUtils.bindZonedDateTimeFields(personEmploymentHistory,params)

        if(personEmploymentHistory.validate()) {
            personEmploymentHistory = personEmploymentHistoryService.savePersonEmploymentHistory(personEmploymentHistory)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), personEmploymentHistory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), personEmploymentHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personEmploymentHistory,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personEmploymentHistory.hasErrors()) {
                respond personEmploymentHistory, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personEmploymentHistoryService.deletePersonEmploymentHistory(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personEmploymentHistoryService.autoCompletePersonEmploymentHistory(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

