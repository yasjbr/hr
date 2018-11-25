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
 * Route DisciplinaryReason requests between model and views.
 * @see DisciplinaryReasonService
 * @see FormatService
 * */
class DisciplinaryReasonController {

    DisciplinaryReasonService disciplinaryReasonService
    FormatService formatService
    DisciplinaryJudgmentService disciplinaryJudgmentService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            DisciplinaryReason disciplinaryReason = disciplinaryReasonService.getInstance(params)
            if (disciplinaryReason) {
                respond disciplinaryReason
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        Map data = [:]
        List disciplinaryJudgmentList = disciplinaryJudgmentService?.search(params)
        data = [disciplinaryReason      : new DisciplinaryReason(params),
                disciplinaryJudgmentList: disciplinaryJudgmentList]
        respond data
    }

    def filter = {
        PagedResultList pagedResultList = disciplinaryReasonService.search(params)
        render text: (disciplinaryReasonService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryReason disciplinaryReason = disciplinaryReasonService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), disciplinaryReason?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), disciplinaryReason?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryReason, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (disciplinaryReason?.hasErrors()) {
                respond disciplinaryReason, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            //to get list of judgment
            Map data = [:]
            GrailsParameterMap paramsJudgment = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List disciplinaryJudgmentList = disciplinaryJudgmentService?.search(paramsJudgment)
            data = [disciplinaryReason      : disciplinaryReasonService.getInstance(params),
                    disciplinaryJudgmentList: disciplinaryJudgmentList]
            respond data
        } else {
            notFound()
        }
    }

    def update = {
        DisciplinaryReason disciplinaryReason = disciplinaryReasonService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), disciplinaryReason?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), disciplinaryReason?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryReason, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (disciplinaryReason.hasErrors()) {
                respond disciplinaryReason, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = disciplinaryReasonService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (disciplinaryReasonService.autoComplete(params)), contentType: "application/json"
    }

    def getInstance = {
        DisciplinaryReason disciplinaryReason = disciplinaryReasonService.getInstance(params)
        Map map = [:]
        map["id"] = disciplinaryReason?.id
        map["descriptionInfo"] = [localName: disciplinaryReason?.descriptionInfo?.localName, latinName: disciplinaryReason?.descriptionInfo?.latinName]
        Map descriptionInfoCategory = [localName: disciplinaryReason?.disciplinaryCategories?.descriptionInfo?.localName, latinName: disciplinaryReason?.disciplinaryCategories?.descriptionInfo?.latinName]
        Map disciplinaryCategory = [id: disciplinaryReason?.disciplinaryCategories?.id, descriptionInfo: descriptionInfoCategory]
        map["disciplinaryCategories"] = disciplinaryCategory
        render text: (map as JSON), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

