package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonLiveStatusCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonLiveStatusDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonLiveStatus requests between model and views.
 *@see PersonLiveStatusService
 *@see FormatService
 **/
class PersonLiveStatusController  {

    PersonLiveStatusService personLiveStatusService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonLiveStatusDTO personLiveStatus = personLiveStatusService.getPersonLiveStatus(PCPUtils.convertParamsToSearchBean(params))
            respond personLiveStatus
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonLiveStatusCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personLiveStatusService.searchPersonLiveStatus(PCPUtils.convertParamsToSearchBean(params))
        render text: (personLiveStatusService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonLiveStatusCommand personLiveStatus = new PersonLiveStatusCommand()

        bindData(personLiveStatus,params)
        PCPUtils.bindZonedDateTimeFields(personLiveStatus,params)

        if(personLiveStatus.validate()) {
            personLiveStatus = personLiveStatusService.savePersonLiveStatus(personLiveStatus)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), personLiveStatus?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), personLiveStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personLiveStatus, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personLiveStatus?.hasErrors()) {
                respond personLiveStatus, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personLiveStatusService.getPersonLiveStatus(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonLiveStatusCommand personLiveStatus = new PersonLiveStatusCommand()

        bindData(personLiveStatus,params)
        PCPUtils.bindZonedDateTimeFields(personLiveStatus,params)

        if(personLiveStatus.validate()) {
            personLiveStatus = personLiveStatusService.savePersonLiveStatus(personLiveStatus)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), personLiveStatus?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), personLiveStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personLiveStatus,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personLiveStatus.hasErrors()) {
                respond personLiveStatus, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personLiveStatusService.deletePersonLiveStatus(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personLiveStatusService.autoCompletePersonLiveStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personLiveStatus.entity', default: 'PersonLiveStatus'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

