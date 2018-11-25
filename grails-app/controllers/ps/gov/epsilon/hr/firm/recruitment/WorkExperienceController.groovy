package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route WorkExperience requests between model and views.
 *@see WorkExperienceService
 *@see FormatService
**/
class WorkExperienceController  {

    WorkExperienceService workExperienceService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.id){
            WorkExperience workExperience = workExperienceService.getInstance(params, true)
            respond workExperience
        }else{
            notFound()
        }
    }

    def create = {
        respond new WorkExperience(params)
    }

    def filter = {
        PagedResultList pagedResultList = workExperienceService.search(params,true)
        render text: (workExperienceService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        WorkExperience workExperience = workExperienceService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), workExperience?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), workExperience?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(workExperience, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (workExperience?.hasErrors()) {
                respond workExperience, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.id){
            respond workExperienceService.getInstance(params, true)
        }else{
            notFound()
        }
    }

    def update = {
        WorkExperience workExperience = workExperienceService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), workExperience?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), workExperience?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(workExperience,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (workExperience.hasErrors()) {
                respond workExperience, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = workExperienceService.delete(PCPUtils.convertParamsToDeleteBean(params),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (workExperienceService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'workExperience.entity', default: 'WorkExperience'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

