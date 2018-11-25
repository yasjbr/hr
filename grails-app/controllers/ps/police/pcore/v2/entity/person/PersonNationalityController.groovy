package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonNationalityCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonNationalityDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonNationality requests between model and views.
 *@see PersonNationalityService
 *@see FormatService
 **/
class PersonNationalityController  {

    PersonNationalityService personNationalityService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonNationalityDTO personNationality = personNationalityService.getPersonNationality(PCPUtils.convertParamsToSearchBean(params))
            respond personNationality
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonNationalityCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personNationalityService.searchPersonNationality(PCPUtils.convertParamsToSearchBean(params))
        render text: (personNationalityService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonNationalityCommand personNationality = new PersonNationalityCommand()

        bindData(personNationality,params)
        PCPUtils.bindZonedDateTimeFields(personNationality,params)

        if(personNationality.validate()) {
            personNationality = personNationalityService.savePersonNationality(personNationality)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), personNationality?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), personNationality?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personNationality, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personNationality?.hasErrors()) {
                respond personNationality, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personNationalityService.getPersonNationality(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonNationalityCommand personNationality = new PersonNationalityCommand()

        bindData(personNationality,params)
        PCPUtils.bindZonedDateTimeFields(personNationality,params)

        if(personNationality.validate()) {
            personNationality = personNationalityService.savePersonNationality(personNationality)
        }
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), personNationality?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), personNationality?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personNationality,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personNationality.hasErrors()) {
                respond personNationality, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personNationalityService.deletePersonNationality(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personNationalityService.autoCompletePersonNationality(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personNationality.entity', default: 'PersonNationality'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

