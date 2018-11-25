package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonMaritalStatusDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonMaritalStatus requests between model and views.
 *@see PersonMaritalStatusService
 *@see FormatService
 **/
class PersonMaritalStatusController  {

    PersonMaritalStatusService personMaritalStatusService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonMaritalStatusDTO personMaritalStatus = personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(params))
            respond personMaritalStatus
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonMaritalStatusCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personMaritalStatusService.searchPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(params))
        render text: (personMaritalStatusService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonMaritalStatusCommand personMaritalStatus = new PersonMaritalStatusCommand()

        bindData(personMaritalStatus,params)
        PCPUtils.bindZonedDateTimeFields(personMaritalStatus,params)

        if(personMaritalStatus.validate()) {
            personMaritalStatus = personMaritalStatusService.savePersonMaritalStatus(personMaritalStatus)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), personMaritalStatus?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), personMaritalStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personMaritalStatus, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personMaritalStatus?.hasErrors()) {
                respond personMaritalStatus, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonMaritalStatusCommand personMaritalStatus = new PersonMaritalStatusCommand()

        bindData(personMaritalStatus,params)
        PCPUtils.bindZonedDateTimeFields(personMaritalStatus,params)

        if(personMaritalStatus.validate()) {
            personMaritalStatus = personMaritalStatusService.savePersonMaritalStatus(personMaritalStatus)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), personMaritalStatus?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), personMaritalStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personMaritalStatus,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personMaritalStatus.hasErrors()) {
                respond personMaritalStatus, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personMaritalStatusService.deletePersonMaritalStatus(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personMaritalStatusService.autoCompletePersonMaritalStatus(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

