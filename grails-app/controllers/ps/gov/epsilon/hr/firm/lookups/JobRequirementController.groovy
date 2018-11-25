package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route JobRequirement requests between model and views.
 * @see JobRequirementService
 * @see FormatService
 * */
class JobRequirementController {

    JobRequirementService jobRequirementService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            JobRequirement jobRequirement = jobRequirementService.getInstance(params)
            if (jobRequirement) {
                respond jobRequirement
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new JobRequirement(params)
    }

    def filter = {
        PagedResultList pagedResultList = jobRequirementService.search(params)
        render text: (jobRequirementService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        JobRequirement jobRequirement = jobRequirementService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'jobRequirement.entity', default: 'JobRequirement'), jobRequirement?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'jobRequirement.entity', default: 'JobRequirement'), jobRequirement?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(jobRequirement, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (jobRequirement?.hasErrors()) {
                respond jobRequirement, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            JobRequirement jobRequirement = jobRequirementService.getInstance(params)
            if (jobRequirement) {
                respond jobRequirement
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        JobRequirement jobRequirement = jobRequirementService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'jobRequirement.entity', default: 'JobRequirement'), jobRequirement?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'jobRequirement.entity', default: 'JobRequirement'), jobRequirement?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(jobRequirement, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (jobRequirement.hasErrors()) {
                respond jobRequirement, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = jobRequirementService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'jobRequirement.entity', default: 'JobRequirement'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'jobRequirement.delete.label', default: 'jobRequirement'), "", ""])

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
        render text: (jobRequirementService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobRequirement.entity', default: 'JobRequirement'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

