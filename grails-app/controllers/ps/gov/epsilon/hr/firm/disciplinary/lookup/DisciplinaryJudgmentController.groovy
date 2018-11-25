package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route DisciplinaryJudgment requests between model and views.
 * @see DisciplinaryJudgmentService
 * @see FormatService
 * */
class DisciplinaryJudgmentController {

    DisciplinaryJudgmentService disciplinaryJudgmentService
    FormatService formatService
    DisciplinaryReasonService disciplinaryReasonService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            DisciplinaryJudgment disciplinaryJudgment = disciplinaryJudgmentService.getInstanceWithRemotingValue(params)
            if (disciplinaryJudgment) {
                respond disciplinaryJudgment
                return
            }

        } else {
            notFound()
        }
    }

    def create = {
        Map data = [:]
        List disciplinaryReasonList = disciplinaryReasonService?.search(params)
        data = [disciplinaryJudgment  : new DisciplinaryJudgment(params),
                disciplinaryReasonList: disciplinaryReasonList]
        respond data
    }

    def filter = {
        PagedResultList pagedResultList = disciplinaryJudgmentService.search(params)
        render text: (disciplinaryJudgmentService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryJudgment disciplinaryJudgment = disciplinaryJudgmentService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), disciplinaryJudgment?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), disciplinaryJudgment?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryJudgment, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (disciplinaryJudgment?.hasErrors()) {
                respond disciplinaryJudgment, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            //to get list of reasons
            Map data = [:]
            GrailsParameterMap paramsJudgment = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List disciplinaryReasonList = disciplinaryReasonService?.search(paramsJudgment)
            data = [disciplinaryJudgment  : disciplinaryJudgmentService.getInstanceWithRemotingValue(params),
                    disciplinaryReasonList: disciplinaryReasonList]
            respond data
        } else {
            notFound()
        }
    }

    def update = {
        DisciplinaryJudgment disciplinaryJudgment = disciplinaryJudgmentService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), disciplinaryJudgment?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), disciplinaryJudgment?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryJudgment, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (disciplinaryJudgment.hasErrors()) {
                respond disciplinaryJudgment, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = disciplinaryJudgmentService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (disciplinaryJudgmentService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryJudgment.entity', default: 'DisciplinaryJudgment'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

