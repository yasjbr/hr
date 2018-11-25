package ps.gov.epsilon.hr.firm.vacation.lookup

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route VacationType requests between model and views.
 * @see VacationTypeService
 * @see FormatService
 * */
class VacationTypeController {

    VacationTypeService vacationTypeService
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
            VacationType vacationType = vacationTypeService.getInstanceWithRemotingValues(params)
            if (vacationType) {
                respond vacationType
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
        respond new VacationType(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = vacationTypeService.searchWithRemotingValues(params)
        render text: (vacationTypeService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        VacationType vacationType = vacationTypeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), vacationType?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), vacationType?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationType, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (vacationType?.hasErrors()) {
                respond vacationType, view: 'create'
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
            VacationType vacationType = vacationTypeService.getInstanceWithRemotingValues(params)
            if (vacationType) {
                respond vacationType
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
        VacationType vacationType = vacationTypeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), vacationType?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), vacationType?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationType, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacationType.hasErrors()) {
                respond vacationType, view: 'edit'
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
        DeleteBean deleteBean = vacationTypeService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (vacationTypeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacationType.entity', default: 'VacationType'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

