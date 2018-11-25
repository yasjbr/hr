package ps.gov.epsilon.hr.firm.correspondenceList

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route CorrespondenceListStatus requests between model and views.
 *@see CorrespondenceListStatusService
 *@see FormatService
**/
class CorrespondenceListStatusController  {

    CorrespondenceListStatusService correspondenceListStatusService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.id){
            CorrespondenceListStatus correspondenceListStatus = correspondenceListStatusService.getInstance(params, true)
            respond correspondenceListStatus
        }else{
            notFound()
        }
    }

    def create = {
        respond new CorrespondenceListStatus(params)
    }

    def filter = {
        PagedResultList pagedResultList = correspondenceListStatusService.search(params,true)
        render text: (correspondenceListStatusService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        CorrespondenceListStatus correspondenceListStatus = correspondenceListStatusService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), correspondenceListStatus?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), correspondenceListStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(correspondenceListStatus, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (correspondenceListStatus?.hasErrors()) {
                respond correspondenceListStatus, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.id){
            respond correspondenceListStatusService.getInstance(params, true)
        }else{
            notFound()
        }
    }

    def update = {
        CorrespondenceListStatus correspondenceListStatus = correspondenceListStatusService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), correspondenceListStatus?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), correspondenceListStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(correspondenceListStatus,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (correspondenceListStatus.hasErrors()) {
                respond correspondenceListStatus, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = correspondenceListStatusService.delete(PCPUtils.convertParamsToDeleteBean(params),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (correspondenceListStatusService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'correspondenceListStatus.entity', default: 'CorrespondenceListStatus'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

