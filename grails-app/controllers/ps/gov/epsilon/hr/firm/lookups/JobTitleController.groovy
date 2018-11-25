package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route JobTitle requests between model and views.
 * @see JobTitleService
 * @see FormatService
 * */
class JobTitleController {

    JobTitleService jobTitleService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            JobTitle jobTitle = jobTitleService.getInstanceWithRemotingValues(params)
            if (jobTitle) {
                respond jobTitle
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new JobTitle(params)
    }

    def filter = {
        PagedResultList pagedResultList = jobTitleService.search(params)
        render text: (jobTitleService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        JobTitle jobTitle = jobTitleService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'jobTitle.entity', default: 'JobTitle'), jobTitle?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'jobTitle.entity', default: 'JobTitle'), jobTitle?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(jobTitle, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (jobTitle?.hasErrors()) {
                respond jobTitle, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            JobTitle jobTitle = jobTitleService.getInstanceWithRemotingValues(params)
            if (jobTitle) {
                respond jobTitle
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        JobTitle jobTitle = jobTitleService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'jobTitle.entity', default: 'JobTitle'), jobTitle?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'jobTitle.entity', default: 'JobTitle'), jobTitle?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(jobTitle, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (jobTitle.hasErrors()) {
                respond jobTitle, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = jobTitleService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'jobTitle.entity', default: 'JobTitle'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'jobTitle.delete.label', default: 'JobTitle'), ""])
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
        render text: (jobTitleService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobTitle.entity', default: 'JobTitle'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

