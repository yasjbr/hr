package ps.gov.epsilon.hr.firm.profile.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route EmployeeStatus requests between model and views.
 * @see EmployeeStatusService
 * @see FormatService
 * */
class EmployeeStatusController {

    EmployeeStatusService employeeStatusService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            EmployeeStatus employeeStatus = employeeStatusService.getInstance(params)
            if (employeeStatus) {
                respond employeeStatus
            } else {
                notFound()

            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new EmployeeStatus(params)
    }

    def filter = {
        PagedResultList pagedResultList = employeeStatusService.search(params)
        render text: (employeeStatusService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EmployeeStatus employeeStatus = employeeStatusService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employeeStatus.entity', default: 'EmployeeStatus'), employeeStatus?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employeeStatus.entity', default: 'EmployeeStatus'), employeeStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeStatus, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (employeeStatus?.hasErrors()) {
                respond employeeStatus, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            EmployeeStatus employeeStatus = employeeStatusService.getInstance(params)
            if (employeeStatus) {
                respond employeeStatus
            } else {
                notFound()

            }
        } else {
            notFound()
        }
    }

    def update = {
        EmployeeStatus employeeStatus = employeeStatusService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employeeStatus.entity', default: 'EmployeeStatus'), employeeStatus?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employeeStatus.entity', default: 'EmployeeStatus'), employeeStatus?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeStatus, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (employeeStatus.hasErrors()) {
                respond employeeStatus, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = employeeStatusService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employeeStatus.entity', default: 'EmployeeStatus'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employeeStatus.deleteMessage.label', default: 'EmployeeStatusCategory'), ""])

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
        render text: (employeeStatusService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeStatus.entity', default: 'EmployeeStatus'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

