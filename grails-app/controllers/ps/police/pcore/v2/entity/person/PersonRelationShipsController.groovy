package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonRelationShips requests between model and views.
 *@see PersonRelationShipsService
 *@see FormatService
 **/
class PersonRelationShipsController  {

    PersonRelationShipsService personRelationShipsService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonRelationShipsDTO personRelationShips = personRelationShipsService.getPersonRelationShips(PCPUtils.convertParamsToSearchBean(params))
            respond personRelationShips
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonRelationShipsCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(params))
        render text: (personRelationShipsService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonRelationShipsCommand personRelationShips = new PersonRelationShipsCommand()

        bindData(personRelationShips,params)
        PCPUtils.bindZonedDateTimeFields(personRelationShips,params)

        if(personRelationShips.validate()) {
            personRelationShips = personRelationShipsService.savePersonRelationShips(personRelationShips)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), personRelationShips?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), personRelationShips?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personRelationShips, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personRelationShips?.hasErrors()) {
                respond personRelationShips, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personRelationShipsService.getPersonRelationShips(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonRelationShipsCommand personRelationShips = new PersonRelationShipsCommand()

        bindData(personRelationShips,params)
        PCPUtils.bindZonedDateTimeFields(personRelationShips,params)

        if(personRelationShips.validate()) {
            personRelationShips = personRelationShipsService.savePersonRelationShips(personRelationShips)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), personRelationShips?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), personRelationShips?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personRelationShips,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personRelationShips.hasErrors()) {
                respond personRelationShips, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personRelationShipsService.deletePersonRelationShips(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personRelationShipsService.autoCompletePersonRelationShips(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personRelationShips.entity', default: 'PersonRelationShips'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

