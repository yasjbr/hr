package ps.gov.epsilon.hr.firm.allowance

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route AllowanceListEmployee requests between model and views.
 * @see AllowanceListEmployeeService
 * @see FormatService
 * */
class AllowanceListEmployeeController {

    AllowanceListEmployeeService allowanceListEmployeeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * default action in controller
     */
    def index = {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list = {}

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            AllowanceListEmployee allowanceListEmployee = allowanceListEmployeeService.getInstanceWithRemotingValues(params)
            if (allowanceListEmployee) {
                respond allowanceListEmployee
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new AllowanceListEmployee(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = allowanceListEmployeeService.searchWithRemotingValues(params)
        render text: (allowanceListEmployeeService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        AllowanceListEmployee allowanceListEmployee = allowanceListEmployeeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), allowanceListEmployee?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), allowanceListEmployee?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceListEmployee, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (allowanceListEmployee?.hasErrors()) {
                respond allowanceListEmployee, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if (params.encodedId) {
            AllowanceListEmployee allowanceListEmployee = allowanceListEmployeeService.getInstanceWithRemotingValues(params)
            if (allowanceListEmployee) {
                respond allowanceListEmployee
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        AllowanceListEmployee allowanceListEmployee = allowanceListEmployeeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), allowanceListEmployee?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), allowanceListEmployee?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceListEmployee, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (allowanceListEmployee.hasErrors()) {
                respond allowanceListEmployee, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = allowanceListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (allowanceListEmployeeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'allowanceListEmployee.entity', default: 'AllowanceListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

