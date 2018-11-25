package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route DepartmentContactInfo requests between model and views.
 * @see DepartmentContactInfoService
 * @see FormatService
 * */
class DepartmentContactInfoController {

    DepartmentContactInfoService departmentContactInfoService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            DepartmentContactInfo departmentContactInfo = departmentContactInfoService.getInstanceWithRemotingValues(params)
            if (departmentContactInfo) {
                respond departmentContactInfo
                return
            }
        }
        notFound()
    }

    def create = {
        respond new DepartmentContactInfo(params)
    }

    def filter = {
        PagedResultList pagedResultList = departmentContactInfoService.searchWithRemotingValues(params)
        render text: (departmentContactInfoService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        DepartmentContactInfo departmentContactInfo = departmentContactInfoService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), departmentContactInfo?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), departmentContactInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(departmentContactInfo, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (departmentContactInfo?.hasErrors()) {
                respond departmentContactInfo, view: 'create'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            DepartmentContactInfo departmentContactInfo = departmentContactInfoService.getInstanceWithRemotingValues(params)
            if (departmentContactInfo) {
                respond departmentContactInfo
                return
            }
        }
        notFound()
    }

    def update = {
        DepartmentContactInfo departmentContactInfo = departmentContactInfoService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), departmentContactInfo?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), departmentContactInfo?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(departmentContactInfo, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (departmentContactInfo.hasErrors()) {
                respond departmentContactInfo, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = departmentContactInfoService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (departmentContactInfoService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

