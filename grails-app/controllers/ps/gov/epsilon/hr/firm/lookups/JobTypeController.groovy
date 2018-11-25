package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route JobType requests between model and views.
 * @see JobTypeService
 * @see FormatService
 * */
class JobTypeController {

    JobTypeService jobTypeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            JobType jobType = jobTypeService.getInstance(params)
            if (jobType) {
                respond jobType
                return
            }

        } else {
            notFound()
        }
    }

    def create = {
        respond new JobType(params)
    }

    def filter = {
        PagedResultList pagedResultList = jobTypeService.search(params)
        render text: (jobTypeService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        JobType jobType = jobTypeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'jobType.entity', default: 'JobType'), jobType?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'jobType.entity', default: 'JobType'), jobType?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(jobType, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (jobType?.hasErrors()) {
                respond jobType, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            JobType jobType = jobTypeService.getInstance(params)
            if (jobType) {
                respond jobType
                return
            }

        } else {
            notFound()
        }
    }

    def update = {
        JobType jobType = jobTypeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'jobType.entity', default: 'JobType'), jobType?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'jobType.entity', default: 'JobType'), jobType?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(jobType, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (jobType.hasErrors()) {
                respond jobType, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = jobTypeService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'jobType.entity', default: 'JobType'), ""])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'jobType.failDelete.label', default: 'jobType'), "", ""])

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
        render text: (jobTypeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobType.entity', default: 'JobType'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

