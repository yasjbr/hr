package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonEducationCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonEducationDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonEducation requests between model and views.
 *@see PersonEducationService
 *@see FormatService
 **/
class PersonEducationController  {

    PersonEducationService personEducationService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonEducationDTO personEducation = personEducationService.getPersonEducation(PCPUtils.convertParamsToSearchBean(params))
            respond personEducation
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonEducationCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personEducationService.searchPersonEducation(PCPUtils.convertParamsToSearchBean(params))
        render text: (personEducationService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonEducationCommand personEducation = new PersonEducationCommand()

        bindData(personEducation,params)
        PCPUtils.bindZonedDateTimeFields(personEducation,params)

        if(personEducation.validate()) {
            personEducation = personEducationService.savePersonEducation(personEducation)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), personEducation?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), personEducation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personEducation, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personEducation?.hasErrors()) {
                respond personEducation, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personEducationService.getPersonEducation(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonEducationCommand personEducation = new PersonEducationCommand()

        bindData(personEducation,params)
        PCPUtils.bindZonedDateTimeFields(personEducation,params)

        if(personEducation.validate()) {
            personEducation = personEducationService.savePersonEducation(personEducation)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), personEducation?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), personEducation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personEducation,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personEducation.hasErrors()) {
                respond personEducation, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personEducationService.deletePersonEducation(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personEducationService.autoCompletePersonEducation(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personEducation.entity', default: 'PersonEducation'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

