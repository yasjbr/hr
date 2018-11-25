package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.commands.v1.PersonLanguageInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonLanguageInfoDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route PersonLanguageInfo requests between model and views.
 *@see PersonLanguageInfoService
 *@see FormatService
 **/
class PersonLanguageInfoController  {

    PersonLanguageInfoService personLanguageInfoService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            PersonLanguageInfoDTO personLanguageInfo = personLanguageInfoService.getPersonLanguageInfo(PCPUtils.convertParamsToSearchBean(params))
            respond personLanguageInfo
        }else{
            notFound()
        }
    }

    def create = {
        respond new PersonLanguageInfoCommand(params)
    }

    def filter = {
        PagedList pagedResultList = personLanguageInfoService.searchPersonLanguageInfo(PCPUtils.convertParamsToSearchBean(params))
        render text: (personLanguageInfoService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        PersonLanguageInfoCommand personLanguageInfo = new PersonLanguageInfoCommand()

        bindData(personLanguageInfo,params)
        PCPUtils.bindZonedDateTimeFields(personLanguageInfo,params)

        if(personLanguageInfo.validate()) {
            personLanguageInfo = personLanguageInfoService.savePersonLanguageInfo(personLanguageInfo)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), personLanguageInfo?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), personLanguageInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personLanguageInfo, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personLanguageInfo?.hasErrors()) {
                respond personLanguageInfo, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond personLanguageInfoService.getPersonLanguageInfo(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        PersonLanguageInfoCommand personLanguageInfo = new PersonLanguageInfoCommand()

        bindData(personLanguageInfo,params)
        PCPUtils.bindZonedDateTimeFields(personLanguageInfo,params)

        if(personLanguageInfo.validate()) {
            personLanguageInfo = personLanguageInfoService.savePersonLanguageInfo(personLanguageInfo)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), personLanguageInfo?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), personLanguageInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personLanguageInfo,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (personLanguageInfo.hasErrors()) {
                respond personLanguageInfo, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = personLanguageInfoService.deletePersonLanguageInfo(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (personLanguageInfoService.autoCompletePersonLanguageInfo(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

