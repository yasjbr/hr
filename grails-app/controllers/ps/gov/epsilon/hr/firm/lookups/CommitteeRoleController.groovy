package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route CommitteeRole requests between model and views.
 * @see CommitteeRoleService
 * @see FormatService
 * */
class CommitteeRoleController {

    CommitteeRoleService committeeRoleService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            CommitteeRole committeeRole = committeeRoleService.getInstance(params)
            if (committeeRole) {
                respond committeeRole
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new CommitteeRole(params)
    }

    def filter = {
        PagedResultList pagedResultList = committeeRoleService.search(params)
        render text: (committeeRoleService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        CommitteeRole committeeRole = committeeRoleService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), committeeRole?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), committeeRole?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(committeeRole, successMessage, failMessage,true, getControllerName(),"create") as JSON), contentType: "application/json"
        } else {
            if (committeeRole?.hasErrors()) {
                respond committeeRole, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            CommitteeRole committeeRole = committeeRoleService.getInstance(params)
            if (committeeRole) {
                respond committeeRole
                return
            }
        } else {
            notFound()
        }
    }

    def update = {
        CommitteeRole committeeRole = committeeRoleService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), committeeRole?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), committeeRole?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(committeeRole, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (committeeRole.hasErrors()) {
                respond committeeRole, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = committeeRoleService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (committeeRoleService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'committeeRole.entity', default: 'CommitteeRole'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

