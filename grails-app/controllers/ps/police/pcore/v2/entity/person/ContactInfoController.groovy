package ps.police.pcore.v2.entity.person

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.CommandParamsMap
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.ContactInfoDTO

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route ContactInfo requests between model and views.
 *@see ContactInfoService
 *@see FormatService
**/
class ContactInfoController  {

    ContactInfoService contactInfoService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            ContactInfoDTO contactInfo = contactInfoService.getContactInfo(PCPUtils.convertParamsToSearchBean(params))
            respond contactInfo
        }else{
            notFound()
        }
    }

    def create = {
        respond new ContactInfoCommand(params)
    }

    def filter = {
        PagedList pagedResultList = contactInfoService.searchContactInfo(PCPUtils.convertParamsToSearchBean(params))
        render text: (contactInfoService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {

        ContactInfoCommand contactInfo = new ContactInfoCommand()
        bindData(contactInfo,params)
        PCPUtils.bindZonedDateTimeFields(contactInfo,params)
        contactInfo.address =  new LocationCommand()
        bindData(contactInfo.address,params["location"])

        contactInfo.paramsMap.put("address",new CommandParamsMap(nameOfParameterKeyInService: "location",nameOfValueInCommand: "address"))

        if(!(contactInfo.contactMethod.id in [ps.police.pcore.enums.v1.ContactMethod.CURRENT_ADDRESS.value(),
                                            ps.police.pcore.enums.v1.ContactMethod.ORIGINAL_ADDRESS.value(),
                                            ps.police.pcore.enums.v1.ContactMethod.WORK_ADDRESS.value(),
                                            ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS.value()])){
            contactInfo.address = null
        }

        if(contactInfo.validate()) {
            contactInfo = contactInfoService.saveContactInfo(contactInfo)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'contactInfo.address.label', default: 'ContactInfo'), contactInfo?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'contactInfo.address.label', default: 'ContactInfo'), contactInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(contactInfo, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (contactInfo?.hasErrors()) {
                respond contactInfo, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond contactInfoService.getContactInfo(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        ContactInfoCommand contactInfo = new ContactInfoCommand()

        bindData(contactInfo,params)
        PCPUtils.bindZonedDateTimeFields(contactInfo,params)
        contactInfo.address =  new LocationCommand()
        bindData(contactInfo.address,params["location"])

        contactInfo.paramsMap.put("address",new CommandParamsMap(nameOfParameterKeyInService: "location",nameOfValueInCommand: "address"))

        if(!(contactInfo.contactMethod.id in [ps.police.pcore.enums.v1.ContactMethod.CURRENT_ADDRESS.value(),
                                              ps.police.pcore.enums.v1.ContactMethod.ORIGINAL_ADDRESS.value(),
                                              ps.police.pcore.enums.v1.ContactMethod.WORK_ADDRESS.value(),
                                              ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS.value()])){
            contactInfo.address = null
        }

        if(contactInfo.validate()) {
            contactInfo = contactInfoService.saveContactInfo(contactInfo)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'contactInfo.address.label', default: 'ContactInfo'), contactInfo?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'contactInfo.address.label', default: 'ContactInfo'), contactInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(contactInfo,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (contactInfo.hasErrors()) {
                respond contactInfo, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = contactInfoService.deleteContactInfo(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'contactInfo.address.label', default: 'ContactInfo'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'contactInfo.address.label', default: 'ContactInfo'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (contactInfoService.autoCompleteContactInfo(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'contactInfo.entity', default: 'ContactInfo'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

