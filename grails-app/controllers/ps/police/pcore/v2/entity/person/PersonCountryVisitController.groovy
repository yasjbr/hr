package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonCountryVisitCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonCountryVisitDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonCountryVisit requests between model and views.
 *@see PersonCountryVisitService
 *@see FormatService
 **/
class PersonCountryVisitController  {

    PersonCountryVisitService personCountryVisitService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonCountryVisitDTO personCountryVisit = personCountryVisitService.getPersonCountryVisit(PCPUtils.convertParamsToSearchBean(params))
            respond personCountryVisit
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonCountryVisitCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personCountryVisitService.searchPersonCountryVisit(PCPUtils.convertParamsToSearchBean(params))
        render text: (personCountryVisitService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonCountryVisitCommand personCountryVisit = new PersonCountryVisitCommand()

        bindData(personCountryVisit,params)
        PCPUtils.bindZonedDateTimeFields(personCountryVisit,params)

        if(personCountryVisit.validate()) {
            personCountryVisit = personCountryVisitService.savePersonCountryVisit(personCountryVisit)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), personCountryVisit?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), personCountryVisit?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personCountryVisit, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personCountryVisit?.hasErrors()) {
                respond personCountryVisit, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personCountryVisitService.getPersonCountryVisit(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonCountryVisitCommand personCountryVisit = new PersonCountryVisitCommand()

        bindData(personCountryVisit,params)
        PCPUtils.bindZonedDateTimeFields(personCountryVisit,params)

        if(personCountryVisit.validate()) {
            personCountryVisit = personCountryVisitService.savePersonCountryVisit(personCountryVisit)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), personCountryVisit?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), personCountryVisit?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personCountryVisit,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personCountryVisit.hasErrors()) {
                respond personCountryVisit, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personCountryVisitService.deletePersonCountryVisit(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personCountryVisitService.autoCompletePersonCountryVisit(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personCountryVisit.entity', default: 'PersonCountryVisit'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

