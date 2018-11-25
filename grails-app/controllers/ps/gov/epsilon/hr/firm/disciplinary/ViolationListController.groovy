package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.*

import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route ViolationList requests between model and views.
 * @see ViolationListService
 * @see FormatService
 * */
class ViolationListController {

    ViolationListService violationListService
    FormatService formatService
    SharedService sharedService

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
    def list = {
        respond sharedService.getAttachmentTypeListAsMap(ViolationList.getName(), EnumOperation.VIOLATION_LIST)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            ViolationList violationList = violationListService.getInstance(params)
            if (violationList) {
                respond violationList
                return
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new ViolationList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = violationListService.searchWithRemotingValues(params)
        render text: (violationListService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ViolationList violationList = violationListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), violationList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), violationList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(violationList, successMessage, failMessage, true, getControllerName(), "manageViolationList") as JSON), contentType: "application/json"
        } else {
            if (violationList?.hasErrors()) {
                respond violationList, view: 'create'
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
        String failMessage = message(code: 'list.fail.edit.message', args: [])
        if (params.encodedId) {
            ViolationList violationList = violationListService.getInstance(params)
            if (violationList && violationList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond violationList
                return
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        ViolationList violationList = violationListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), violationList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), violationList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(violationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (violationList.hasErrors()) {
                respond violationList, view: 'edit'
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
        DeleteBean deleteBean = violationListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), params?.id,deleteBean.responseMessage?:""])
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
     * this action was added to manage the list itself, will return the list instance
     */
    def manageViolationList = {
        if (params.encodedId || params.id) {
            ViolationList violationList = violationListService.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(ViolationList.getName(), EnumOperation.VIOLATION_LIST)
            map.violationList = violationList
            respond map
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the violation list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ViolationList violationList = violationListService.getInstance(params)
            respond violationList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the violation list instance to be used in modal
     */
    def addViolationModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ViolationList violationList = violationListService.getInstance(params)
            respond violationList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the violation list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ViolationList violationList = violationListService.getInstance(params)
            respond violationList
        } else {
            return
        }
        return
    }

    /*
     * to send the violationList to the receiving party
     */
    def sendList = {
        ViolationList violationList = violationListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(violationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (violationList.hasErrors()) {
                respond violationList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /*
     * to add the violations to the list
     */
    def addViolationToList = {
        ViolationList violationList = violationListService?.addEmployeeViolationToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(violationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (violationList.hasErrors()) {
                respond violationList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to send the dispatchList to the receiving party
     */
    def closeList = {
        ViolationList violationList = violationListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {

            render text: (formatService.buildResponse(violationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (violationList.hasErrors()) {
                respond violationList, view: 'rejectRequestModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'violationList.entity', default: 'ViolationList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

