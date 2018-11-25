package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route DisciplinaryCategory requests between model and views.
 * @see DisciplinaryCategoryService
 * @see FormatService
 * */
class DisciplinaryCategoryController {

    DisciplinaryCategoryService disciplinaryCategoryService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            DisciplinaryCategory disciplinaryCategory = disciplinaryCategoryService.getInstance(params)
            if (disciplinaryCategory) {
                respond disciplinaryCategory
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new DisciplinaryCategory(params)
    }

    def filter = {
        PagedResultList pagedResultList = disciplinaryCategoryService.search(params)
        render text: (disciplinaryCategoryService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryCategory disciplinaryCategory = disciplinaryCategoryService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), disciplinaryCategory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), disciplinaryCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryCategory, successMessage, failMessage, true, getControllerName(),"create") as JSON), contentType: "application/json"
        } else {
            if (disciplinaryCategory?.hasErrors()) {
                respond disciplinaryCategory, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            DisciplinaryCategory disciplinaryCategory = disciplinaryCategoryService.getInstance(params)
            if (disciplinaryCategory) {
                respond disciplinaryCategory
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        DisciplinaryCategory disciplinaryCategory = disciplinaryCategoryService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), disciplinaryCategory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), disciplinaryCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryCategory, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        } else {
            if (disciplinaryCategory.hasErrors()) {
                respond disciplinaryCategory, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = disciplinaryCategoryService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (disciplinaryCategoryService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

