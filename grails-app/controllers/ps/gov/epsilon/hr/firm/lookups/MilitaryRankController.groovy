package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route MilitaryRank requests between model and views.
 * @see MilitaryRankService
 * @see FormatService
 * */
class MilitaryRankController {

    MilitaryRankService militaryRankService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            MilitaryRank militaryRank = militaryRankService.getInstance(params)
            if (militaryRank) {
                respond militaryRank
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new MilitaryRank(params)
    }

    def filter = {
        PagedResultList pagedResultList = militaryRankService.search(params)
        render text: (militaryRankService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        MilitaryRank militaryRank = militaryRankService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'militaryRank.entity', default: 'MilitaryRank'), militaryRank?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'militaryRank.entity', default: 'MilitaryRank'), militaryRank?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(militaryRank, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (militaryRank?.hasErrors()) {
                respond militaryRank, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            MilitaryRank militaryRank = militaryRankService.getInstance(params)
            if (militaryRank) {
                respond militaryRank
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        MilitaryRank militaryRank = militaryRankService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'militaryRank.entity', default: 'MilitaryRank'), militaryRank?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'militaryRank.entity', default: 'MilitaryRank'), militaryRank?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(militaryRank, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (militaryRank.hasErrors()) {
                respond militaryRank, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = militaryRankService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'militaryRank.entity', default: 'MilitaryRank'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: ["",""])
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
        render text: (militaryRankService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'militaryRank.entity', default: 'MilitaryRank'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

