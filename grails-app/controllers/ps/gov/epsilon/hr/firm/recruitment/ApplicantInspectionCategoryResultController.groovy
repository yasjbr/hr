package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route ApplicantInspectionCategoryResult requests between model and views.
 *@see ApplicantInspectionCategoryResultService
 *@see FormatService
**/
class ApplicantInspectionCategoryResultController  {

    ApplicantInspectionCategoryResultService applicantInspectionCategoryResultService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            ApplicantInspectionCategoryResult applicantInspectionCategoryResult = applicantInspectionCategoryResultService.getInstance(params)
            respond applicantInspectionCategoryResult
        }else{
            notFound()
        }
    }

    def create = {
        respond new ApplicantInspectionCategoryResult(params)
    }

    def filter = {
        PagedResultList pagedResultList = applicantInspectionCategoryResultService.search(params)
        render text: (applicantInspectionCategoryResultService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        ApplicantInspectionCategoryResult applicantInspectionCategoryResult = applicantInspectionCategoryResultService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), applicantInspectionCategoryResult?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), applicantInspectionCategoryResult?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionCategoryResult, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (applicantInspectionCategoryResult?.hasErrors()) {
                respond applicantInspectionCategoryResult, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            respond applicantInspectionCategoryResultService.getInstance(params)
        }else{
            notFound()
        }
    }

    def update = {
        ApplicantInspectionCategoryResult applicantInspectionCategoryResult = applicantInspectionCategoryResultService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), applicantInspectionCategoryResult?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), applicantInspectionCategoryResult?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionCategoryResult,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (applicantInspectionCategoryResult.hasErrors()) {
                respond applicantInspectionCategoryResult, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = applicantInspectionCategoryResultService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (applicantInspectionCategoryResultService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

