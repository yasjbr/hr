package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route Department requests between model and views.
 * @see DepartmentService
 * @see FormatService
 * */
class DepartmentController {

    DepartmentService departmentService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            Department department = departmentService.getInstanceWithRemotingValues(params)
            if (department) {
                respond department
                return
            }
        }
        notFound()
    }


    def create = {
        respond new Department(params)
    }

    def filter = {
        PagedResultList pagedResultList = departmentService.searchWithRemotingValues(params)
        render text: (departmentService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        Department department = departmentService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'department.entity', default: 'Department'), department?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'department.entity', default: 'Department'), department?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(department, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (department?.hasErrors()) {
                respond department, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            Department department = departmentService.getInstanceWithRemotingValues(params)
            if (department) {
                respond department
                return
            }
        }
        notFound()
    }

    def update = {
        Department department = departmentService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'department.entity', default: 'Department'), department?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'department.entity', default: 'Department'), department?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(department, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (department.hasErrors()) {
                respond department, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = departmentService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'department.entity', default: 'Department'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'department.entity', default: 'Department'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (departmentService.autoComplete(params)), contentType: "application/json"
    }

    def autocompleteHierarchy = {
        render text: (departmentService.autocompleteHierarchy(params)), contentType: "application/json"
    }

    def getInstance = {
        render text: (departmentService.getInstanceWithRemotingValues(params) as JSON) , contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'department.entity', default: 'Department'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

