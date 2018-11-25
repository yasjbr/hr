package ps.gov.epsilon.hr.firm.transfer

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployee
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployeeService
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route ExternalTransferListEmployee requests between model and views.
 * @see ExternalTransferListEmployeeService
 * @see FormatService
 * */
class ExternalTransferListEmployeeController {

    ExternalTransferListEmployeeService externalTransferListEmployeeService
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
            ExternalTransferListEmployee externalTransferListEmployee = externalTransferListEmployeeService.getInstanceWithRemotingValues(params)
            if (externalTransferListEmployee) {
                respond externalTransferListEmployee
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
        respond new ExternalTransferListEmployee(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = externalTransferListEmployeeService.searchWithRemotingValues(params)
        render text: (externalTransferListEmployeeService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        ExternalTransferListEmployee externalTransferListEmployee = externalTransferListEmployeeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), externalTransferListEmployee?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), externalTransferListEmployee?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalTransferListEmployee, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (externalTransferListEmployee?.hasErrors()) {
                respond externalTransferListEmployee, view: 'create'
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
            ExternalTransferListEmployee externalTransferListEmployee = externalTransferListEmployeeService.getInstanceWithRemotingValues(params)
            if (externalTransferListEmployee) {
                respond externalTransferListEmployee
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
        ExternalTransferListEmployee externalTransferListEmployee = externalTransferListEmployeeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), externalTransferListEmployee?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), externalTransferListEmployee?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalTransferListEmployee, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (externalTransferListEmployee.hasErrors()) {
                respond externalTransferListEmployee, view: 'edit'
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
        DeleteBean deleteBean = externalTransferListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), params?.id, deleteBean.responseMessage ?: ""])
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
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

