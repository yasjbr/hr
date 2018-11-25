package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route EmploymentCategory requests between model and views.
 * @see EmploymentCategoryService
 * @see FormatService
 * */
class EmploymentCategoryController {

    EmploymentCategoryService employmentCategoryService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            EmploymentCategory employmentCategory = employmentCategoryService.getInstance(params)
            if (employmentCategory) {
                respond employmentCategory
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new EmploymentCategory(params)
    }

    def filter = {
        PagedResultList pagedResultList = employmentCategoryService.search(params)
        render text: (employmentCategoryService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EmploymentCategory employmentCategory = employmentCategoryService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), employmentCategory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), employmentCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employmentCategory, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (employmentCategory?.hasErrors()) {
                respond employmentCategory, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            EmploymentCategory employmentCategory = employmentCategoryService.getInstance(params)
            if (employmentCategory) {
                respond employmentCategory
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        EmploymentCategory employmentCategory = employmentCategoryService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), employmentCategory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), employmentCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employmentCategory, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (employmentCategory.hasErrors()) {
                respond employmentCategory, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = employmentCategoryService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (employmentCategoryService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employmentCategory.entity', default: 'EmploymentCategory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

