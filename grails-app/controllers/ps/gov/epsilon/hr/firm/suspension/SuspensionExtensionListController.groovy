package ps.gov.epsilon.hr.firm.suspension

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route SuspensionExtensionList requests between model and views.
 * @see SuspensionExtensionListService
 * @see FormatService
 * */
class SuspensionExtensionListController {

    SuspensionExtensionListService suspensionExtensionListService
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
        respond sharedService.getAttachmentTypeListAsMap(SuspensionExtensionList.getName(), EnumOperation.SUSPENSION_EXTENSION_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            if (suspensionExtensionList) {
                respond suspensionExtensionList
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
        respond new SuspensionExtensionList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = suspensionExtensionListService.searchWithRemotingValues(params)
        render text: (suspensionExtensionListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), suspensionExtensionList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), suspensionExtensionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage, true, getControllerName(), "manageList") as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionList?.hasErrors()) {
                respond suspensionExtensionList, view: 'create'
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
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            if (suspensionExtensionList && suspensionExtensionList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond suspensionExtensionList
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
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), suspensionExtensionList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), suspensionExtensionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionList.hasErrors()) {
                respond suspensionExtensionList, view: 'edit'
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
        DeleteBean deleteBean = suspensionExtensionListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (suspensionExtensionListService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageList = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(SuspensionExtensionList.getName(), EnumOperation.SUSPENSION_EXTENSION_LIST)
            map.suspensionExtensionList = suspensionExtensionList
            respond map
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the suspension list instance to be used in modal
     */
    def sendListModal = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            respond suspensionExtensionList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the suspension extension list instance to be used in modal
     */
    def receiveListModal = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            respond suspensionExtensionList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the suspension extension list instance to be used in modal
     */
    def closeListModal = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            respond suspensionExtensionList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the suspension extension list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            respond suspensionExtensionList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the suspension extension list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(params)
            respond suspensionExtensionList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to add the suspension extension requests modal
     */
    def addRequestModal = {
        if (params.encodedId) {
            SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService?.getInstance(params)
            respond suspensionExtensionList
        } else {
            notFound()
        }
    }

    /**
     * To add the suspension extension request into list
     */
    def addRequestToList = {
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService?.addRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionList.hasErrors()) {
                respond suspensionExtensionList, view: 'sendList'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * To send the suspension extension to the receiving party
     */
    def sendList = {
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.sendData(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionList.hasErrors()) {
                respond suspensionExtensionList, view: 'sendData'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to receive the suspension extension
     */
    def receiveList = {
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionExtensionList.hasErrors()) {
                respond suspensionExtensionList, view: 'receiveListModal'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of SuspensionExtension list request to Approved
     */
    def approveRequest = {
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.approveRequest(params)
        String successMessage = message(code: 'suspensionExtensionList.requestApproved.message')
        String failMessage = message(code: 'suspensionExtensionList.not.requestApproved.message')
        render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to change the status of SuspensionExtension list request to Rejected
     */
    def rejectRequest = {
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.rejectRequest(params)
        String successMessage = message(code: 'suspensionExtensionList.requestRejected.message')
        String failMessage = message(code: 'suspensionExtensionList.not.requestRejected.message')
        render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to close the  suspensionExtension list
     */
    def closeList = {
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.closeList(params)
        String successMessage = message(code: 'suspensionExtensionList.closeList.message')
        String failMessage = message(code: 'suspensionExtensionList.not.closeList.message')
        render text: (formatService.buildResponse(suspensionExtensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * render the note list modal
     */
    def noteList = {
        return [id: params["id"]]
    }

    /**
     * render the create note modal
     */
    def noteCreate = {
        return [id: params["id"]]
    }
}

