package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.organization.commands.v1.OrganizationCommand

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route Firm requests between model and views.
 * @see FirmService
 * @see FormatService
 * */
class FirmController {

    FirmService firmService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            Firm firm = firmService.getInstanceWithRemotingValues(params)
            if (firm) {
                respond firm
                return
            }
        }
        notFound()
    }

    def create = {
        respond new Firm(params)
    }

    def filter = {
        PagedResultList pagedResultList = firmService.searchWithRemotingValues(params)
        render text: (firmService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        Firm firm = firmService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'firm.entity', default: 'Firm'), firm?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'firm.entity', default: 'Firm'), firm?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(firm, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (firm?.hasErrors()) {
                respond firm, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            Firm firm = firmService.getInstanceWithRemotingValues(params)
            if (firm) {

                if (firm?.provinceFirms) {
                    firm?.transientData?.put("provinceList", firm?.provinceFirms?.collect {
                        [it.id, it.province?.descriptionInfo?.localName]
                    })
                }
                respond firm
                return
            }
        }
        notFound()
    }

    def update = {
        Firm firm = firmService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'firm.entity', default: 'Firm'), firm?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'firm.entity', default: 'Firm'), firm?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(firm, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (firm.hasErrors()) {
                respond firm, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = firmService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'firm.entity', default: 'Firm'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'firm.entity', default: 'Firm'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (firmService.autoComplete(params)), contentType: "application/json"
    }

    def saveOrganization = {
        OrganizationCommand organizationCommand = firmService.saveOrganization(params)
        if (organizationCommand.validate()) {
            render text: ([id: organizationCommand?.id, organizationName: organizationCommand?.descriptionInfo?.localName] as JSON), contentType: "application/json"
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'firm.entity', default: 'Firm'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }


}

