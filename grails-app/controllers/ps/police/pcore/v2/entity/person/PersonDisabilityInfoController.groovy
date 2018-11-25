package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonDisabilityInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDisabilityInfoDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonDisabilityInfo requests between model and views.
 *@see PersonDisabilityInfoService
 *@see FormatService
 **/
class PersonDisabilityInfoController  {

    PersonDisabilityInfoService personDisabilityInfoService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonDisabilityInfoDTO personDisabilityInfo = personDisabilityInfoService.getPersonDisabilityInfo(PCPUtils.convertParamsToSearchBean(params))
            respond personDisabilityInfo
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonDisabilityInfoCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personDisabilityInfoService.searchPersonDisabilityInfo(PCPUtils.convertParamsToSearchBean(params))
        render text: (personDisabilityInfoService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonDisabilityInfoCommand personDisabilityInfo = new PersonDisabilityInfoCommand()

        bindData(personDisabilityInfo,params)
        PCPUtils.bindZonedDateTimeFields(personDisabilityInfo,params)

        if(personDisabilityInfo.validate()) {
            personDisabilityInfo = personDisabilityInfoService.savePersonDisabilityInfo(personDisabilityInfo)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), personDisabilityInfo?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), personDisabilityInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personDisabilityInfo, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personDisabilityInfo?.hasErrors()) {
                respond personDisabilityInfo, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personDisabilityInfoService.getPersonDisabilityInfo(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonDisabilityInfoCommand personDisabilityInfo = new PersonDisabilityInfoCommand()

        bindData(personDisabilityInfo,params)
        PCPUtils.bindZonedDateTimeFields(personDisabilityInfo,params)

        if(personDisabilityInfo.validate()) {
            personDisabilityInfo = personDisabilityInfoService.savePersonDisabilityInfo(personDisabilityInfo)
        }
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), personDisabilityInfo?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), personDisabilityInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personDisabilityInfo,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personDisabilityInfo.hasErrors()) {
                respond personDisabilityInfo, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personDisabilityInfoService.deletePersonDisabilityInfo(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personDisabilityInfoService.autoCompletePersonDisabilityInfo(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

