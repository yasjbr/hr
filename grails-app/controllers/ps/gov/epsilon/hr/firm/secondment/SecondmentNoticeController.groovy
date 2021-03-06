package ps.gov.epsilon.hr.firm.secondment

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route SecondmentNotice requests between model and views.
 *@see SecondmentNoticeService
 *@see FormatService
**/
class SecondmentNoticeController  {

    SecondmentNoticeService secondmentNoticeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            SecondmentNotice secondmentNotice = secondmentNoticeService.getInstance(params)
            if(secondmentNotice){
                respond secondmentNotice
                return
            }
        }else{
            notFound()
        }
    }

    def create = {
        respond new SecondmentNotice(params)
    }

    def filter = {
        PagedResultList pagedResultList = secondmentNoticeService.search(params)
        render text: (secondmentNoticeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId")?:1L
        SecondmentNotice secondmentNotice = secondmentNoticeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), secondmentNotice?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), secondmentNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(secondmentNotice, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (secondmentNotice?.hasErrors()) {
                respond secondmentNotice, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            SecondmentNotice secondmentNotice = secondmentNoticeService.getInstance(params)
            if(secondmentNotice){
                respond secondmentNotice
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        SecondmentNotice secondmentNotice = secondmentNoticeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), secondmentNotice?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), secondmentNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(secondmentNotice,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (secondmentNotice.hasErrors()) {
                respond secondmentNotice, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = secondmentNoticeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (secondmentNoticeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'secondmentNotice.entity', default: 'SecondmentNotice'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

