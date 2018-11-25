package ps.gov.epsilon.hr.firm.transfer

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ExternalReceivedTransferredPerson requests between model and views.
 *@see ExternalReceivedTransferredPersonService
 *@see FormatService
**/
class ExternalReceivedTransferredPersonController  {

    ExternalReceivedTransferredPersonService externalReceivedTransferredPersonService
    FormatService formatService
    ManagePersonService managePersonService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    /**
     * default action in controller
     */
    def index= {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list= {
        respond sharedService.getAttachmentTypeListAsMap(ExternalReceivedTransferredPerson.getName(), EnumOperation.EXTERNAL_RECEIVED_TRANSFERRED_PERSON)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            ExternalReceivedTransferredPerson externalReceivedTransferredPerson = externalReceivedTransferredPersonService.getInstanceWithRemotingValues(params)
            if(externalReceivedTransferredPerson){
                respond externalReceivedTransferredPerson
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
    }

    /**
     * this action is used to create new person which is not found in core:
     */
    def createNewPerson = {
    }

    /**
     * represent the create page empty instance
     */
    def createNewExternalReceived = {

        if (params.long("personId")) {
            params.isNewInstance = "true"
            ExternalReceivedTransferredPerson externalReceivedTransferredPerson = externalReceivedTransferredPersonService.getInstanceWithRemotingValues(params)
            if(externalReceivedTransferredPerson){
                respond externalReceivedTransferredPerson
            }else{
                notFound()
            }

        }else {
            notFound()
        }
    }

    /**
     * save new person in core
     */
    def saveNewPerson = {
        PersonCommand personCommand = new PersonCommand()
        bindData(personCommand, params)
        personCommand = managePersonService.saveNewPerson(personCommand, params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])
        render text: (formatService.buildResponse(personCommand, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * this action is used to return person info from core to be used in create applicant
     */
    def getPerson = {
        if (params.long("personId") && externalReceivedTransferredPersonService.count(params) == 0) {
            render text: ([success: true, personId: params.long("personId")] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'externalReceivedTransferredPerson.person.notFound.error.label')
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = externalReceivedTransferredPersonService.searchWithRemotingValues(params)
        render text: (externalReceivedTransferredPersonService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ExternalReceivedTransferredPerson externalReceivedTransferredPerson = externalReceivedTransferredPersonService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), externalReceivedTransferredPerson?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), externalReceivedTransferredPerson?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalReceivedTransferredPerson, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (externalReceivedTransferredPerson?.hasErrors()) {
                respond externalReceivedTransferredPerson, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId){
            ExternalReceivedTransferredPerson externalReceivedTransferredPerson = externalReceivedTransferredPersonService.getInstanceWithRemotingValues(params)
            if(externalReceivedTransferredPerson){
                respond externalReceivedTransferredPerson
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        ExternalReceivedTransferredPerson externalReceivedTransferredPerson = externalReceivedTransferredPersonService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), externalReceivedTransferredPerson?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), externalReceivedTransferredPerson?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalReceivedTransferredPerson,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (externalReceivedTransferredPerson.hasErrors()) {
                respond externalReceivedTransferredPerson, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = externalReceivedTransferredPersonService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), params?.id,deleteBean.responseMessage?:""])
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

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (externalReceivedTransferredPersonService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

