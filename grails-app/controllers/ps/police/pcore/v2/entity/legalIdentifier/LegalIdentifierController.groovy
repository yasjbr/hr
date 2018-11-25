package ps.police.pcore.v2.entity.legalIdentifier

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import ps.police.common.beans.v1.CommandParamsMap
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.legalIdentifier.commands.v1.JoinedLegalIdentifierRelatedRestrictionCommand
import ps.police.pcore.v2.entity.legalIdentifier.commands.v1.LegalIdentifierCommand
import ps.police.pcore.v2.entity.legalIdentifier.commands.v1.LegalIdentifierRestrictionCommand
import ps.police.pcore.v2.entity.legalIdentifier.dtos.v1.LegalIdentifierDTO
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.DocumentClassificationCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.DocumentTypeCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.JoinedDocumentTypeClassificationCommand

import java.lang.reflect.Field
import java.time.ZonedDateTime

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route LegalIdentifier requests between model and views.
 *@see LegalIdentifierService
 *@see FormatService
**/
class LegalIdentifierController  {

    LegalIdentifierService legalIdentifierService
    def formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.long("id")){
            LegalIdentifierDTO legalIdentifier = legalIdentifierService.getLegalIdentifier(PCPUtils.convertParamsToSearchBean(params))
            respond legalIdentifier
        }else{
            notFound()
        }
    }

    def create = {
        respond new LegalIdentifierCommand(params)
    }

    def filter = {
        PagedList pagedList = legalIdentifierService.searchLegalIdentifier(PCPUtils.convertParamsToSearchBean(params))
        render text: (legalIdentifierService.resultListToMap(pagedList,params) as JSON), contentType: "application/json"
    }

    def save = {
        LegalIdentifierCommand legalIdentifier = new LegalIdentifierCommand()

        List legalIdentifierRelatedRestrictions = params.list("legalIdentifierRelatedRestrictions.id") ?: []
        if (legalIdentifierRelatedRestrictions) {
            params.remove("legalIdentifierRelatedRestrictions")
            params.remove("legalIdentifierRelatedRestrictions.id")
            params["legalIdentifierRelatedRestrictions"] = legalIdentifierRelatedRestrictions?.collect {
                new JoinedLegalIdentifierRelatedRestrictionCommand(legalIdentifierRestriction: new LegalIdentifierRestrictionCommand(id:it as long))
            }
        }

        bindData(legalIdentifier,params)
        PCPUtils.bindZonedDateTimeFields(legalIdentifier,params)
        legalIdentifier.issueLocation =  new LocationCommand()
        bindData(legalIdentifier.issueLocation,params["location"])

        Long documentClassificationId = params.long("documentClassification.id")
        Long documentTypeId = params.long("documentType.id")
        if (documentTypeId && documentClassificationId) {
            legalIdentifier.documentTypeClassification = new JoinedDocumentTypeClassificationCommand(documentClassification: new DocumentClassificationCommand(id:documentClassificationId),documentType: new DocumentTypeCommand(id:documentTypeId))
        }

        legalIdentifier.paramsMap.put("legalIdentifierRelatedRestrictions",new CommandParamsMap(nameOfParameterKeyInService: "legalIdentifierRelatedRestrictions.id",nameOfValueInCommand: "legalIdentifierRestriction.id"))
        legalIdentifier.paramsMap.put("documentTypeClassification",new CommandParamsMap(nameOfPropertiesToSend:["documentClassification.id":"documentClassification.id","documentType.id":"documentType.id"]))
        legalIdentifier.paramsMap.put("issueLocation",new CommandParamsMap(nameOfParameterKeyInService: "location",nameOfValueInCommand: "issueLocation"))

        if(legalIdentifier.validate()) {
            legalIdentifier = legalIdentifierService.saveLegalIdentifier(legalIdentifier)
        }

        String successMessage = message(code: 'default.created.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), legalIdentifier?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), legalIdentifier?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(legalIdentifier, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (legalIdentifier?.hasErrors()) {
                respond legalIdentifier, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.long("id")){
            respond legalIdentifierService.getLegalIdentifier(PCPUtils.convertParamsToSearchBean(params))
        }else{
            notFound()
        }
    }

    def update = {
        LegalIdentifierCommand legalIdentifier = new LegalIdentifierCommand()

        List legalIdentifierRelatedRestrictions = params.list("legalIdentifierRelatedRestrictions.id") ?: []
        if (legalIdentifierRelatedRestrictions) {
            params.remove("legalIdentifierRelatedRestrictions")
            params.remove("legalIdentifierRelatedRestrictions.id")
            params["legalIdentifierRelatedRestrictions"] = legalIdentifierRelatedRestrictions?.collect {
                new JoinedLegalIdentifierRelatedRestrictionCommand(legalIdentifierRestriction: new LegalIdentifierRestrictionCommand(id:it as long))
            }
        }

        bindData(legalIdentifier,params)
        PCPUtils.bindZonedDateTimeFields(legalIdentifier,params)
        legalIdentifier.issueLocation =  new LocationCommand()
        bindData(legalIdentifier.issueLocation,params["location"])

        Long documentClassificationId = params.long("documentClassification.id")
        Long documentTypeId = params.long("documentType.id")
        if (documentTypeId && documentClassificationId) {
            legalIdentifier.documentTypeClassification = new JoinedDocumentTypeClassificationCommand(documentClassification: new DocumentClassificationCommand(id:documentClassificationId),documentType: new DocumentTypeCommand(id:documentTypeId))
        }

        legalIdentifier.paramsMap.put("legalIdentifierRelatedRestrictions",new CommandParamsMap(nameOfParameterKeyInService: "legalIdentifierRelatedRestrictions.id",nameOfValueInCommand: "legalIdentifierRestriction.id"))
        legalIdentifier.paramsMap.put("documentTypeClassification",new CommandParamsMap(nameOfPropertiesToSend:["documentClassification.id":"documentClassification.id","documentType.id":"documentType.id"]))
        legalIdentifier.paramsMap.put("issueLocation",new CommandParamsMap(nameOfParameterKeyInService: "location",nameOfValueInCommand: "issueLocation"))

        if(legalIdentifier.validate()) {
            legalIdentifier = legalIdentifierService.saveLegalIdentifier(legalIdentifier)
        }

        String successMessage = message(code: 'default.updated.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), legalIdentifier?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), legalIdentifier?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(legalIdentifier,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (legalIdentifier.hasErrors()) {
                respond legalIdentifier, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = legalIdentifierService.deleteLegalIdentifier(PCPUtils.convertParamsToDeleteBean(params))
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (legalIdentifierService.autoCompleteLegalIdentifier(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'legalIdentifier.entity', default: 'LegalIdentifier'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

