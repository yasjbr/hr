package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route DisciplinaryListJudgmentSetup requests between model and views.
 * @see DisciplinaryListJudgmentSetupService
 * @see FormatService
 * */
class DisciplinaryListJudgmentSetupController {

    DisciplinaryListJudgmentSetupService disciplinaryListJudgmentSetupService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            DisciplinaryListJudgmentSetup disciplinaryListJudgmentSetup = disciplinaryListJudgmentSetupService.getInstance(params)
            if (disciplinaryListJudgmentSetup) {
                respond disciplinaryListJudgmentSetup
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new DisciplinaryListJudgmentSetup(params)
    }

    def filter = {
        PagedResultList pagedResultList = disciplinaryListJudgmentSetupService.search(params)
        render text: (disciplinaryListJudgmentSetupService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryListJudgmentSetup disciplinaryListJudgmentSetup = disciplinaryListJudgmentSetupService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), disciplinaryListJudgmentSetup?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), disciplinaryListJudgmentSetup?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryListJudgmentSetup, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (disciplinaryListJudgmentSetup?.hasErrors()) {
                respond disciplinaryListJudgmentSetup, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            DisciplinaryListJudgmentSetup disciplinaryListJudgmentSetup = disciplinaryListJudgmentSetupService.getInstance(params)
            if (disciplinaryListJudgmentSetup) {
                respond disciplinaryListJudgmentSetup
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        DisciplinaryListJudgmentSetup disciplinaryListJudgmentSetup = disciplinaryListJudgmentSetupService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), disciplinaryListJudgmentSetup?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), disciplinaryListJudgmentSetup?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryListJudgmentSetup, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (disciplinaryListJudgmentSetup.hasErrors()) {
                respond disciplinaryListJudgmentSetup, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = disciplinaryListJudgmentSetupService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (disciplinaryListJudgmentSetupService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

