package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonCharacteristicsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonCharacteristicsDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonCharacteristics requests between model and views.
 *@see PersonCharacteristicsService
 *@see FormatService
 **/
class PersonCharacteristicsController  {

    PersonCharacteristicsService personCharacteristicsService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonCharacteristicsDTO personCharacteristics = personCharacteristicsService.getPersonCharacteristics(PCPUtils.convertParamsToSearchBean(params))
            respond personCharacteristics
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonCharacteristicsCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personCharacteristicsService.searchPersonCharacteristics(PCPUtils.convertParamsToSearchBean(params))
        render text: (personCharacteristicsService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonCharacteristicsCommand personCharacteristics = new PersonCharacteristicsCommand()

        bindData(personCharacteristics,params)
        PCPUtils.bindZonedDateTimeFields(personCharacteristics,params)

        if(personCharacteristics.validate()) {
            personCharacteristics = personCharacteristicsService.savePersonCharacteristics(personCharacteristics)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), personCharacteristics?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), personCharacteristics?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personCharacteristics, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personCharacteristics?.hasErrors()) {
                respond personCharacteristics, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personCharacteristicsService.getPersonCharacteristics(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonCharacteristicsCommand personCharacteristics = new PersonCharacteristicsCommand()

        bindData(personCharacteristics,params)
        PCPUtils.bindZonedDateTimeFields(personCharacteristics,params)

        if(personCharacteristics.validate()) {
            personCharacteristics = personCharacteristicsService.savePersonCharacteristics(personCharacteristics)
        }
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), personCharacteristics?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), personCharacteristics?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personCharacteristics,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personCharacteristics.hasErrors()) {
                respond personCharacteristics, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personCharacteristicsService.deletePersonCharacteristics(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personCharacteristicsService.autoCompletePersonCharacteristics(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

