package ps.gov.epsilon.hr.firm.general

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
 * Route GeneralList requests between model and views.
 * @see GeneralListService
 * @see FormatService
 * */
class GeneralListController {

    GeneralListService generalListService
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
        respond sharedService.getAttachmentTypeListAsMap(GeneralList.getName(), EnumOperation.GENERAL_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            if (generalList) {
                respond generalList
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
        respond new GeneralList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = generalListService.searchWithRemotingValues(params)
        render text: (generalListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        GeneralList generalList = generalListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), generalList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), generalList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(generalList, successMessage, failMessage, true, getControllerName(), "manageList") as JSON), contentType: "application/json"
        } else {
            if (generalList?.hasErrors()) {
                respond generalList, view: 'create'
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
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            if (generalList && generalList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond generalList
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
        GeneralList generalList = generalListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), generalList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), generalList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (generalList.hasErrors()) {
                respond generalList, view: 'edit'
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
        DeleteBean deleteBean = generalListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (generalListService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'generalList.entity', default: 'GeneralList'), params?.id])
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
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            Map map = sharedService.getAttachmentTypeListAsMap(GeneralList.getName(), EnumOperation.GENERAL_LIST)
            map.generalList = generalList
            respond map
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def sendListModal = {
        if (params.encodedId) {
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            respond generalList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def receiveListModal = {
        if (params.encodedId) {
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            respond generalList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def closeListModal = {
        if (params.encodedId) {
            GeneralList generalList = generalListService.getInstance(params)
            respond generalList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.encodedId) {
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            respond generalList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.encodedId) {
            GeneralList generalList = generalListService.getInstanceWithRemotingValues(params)
            respond generalList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to add requests modal
     */
    def addRequestModal = {
        if (params.encodedId) {
            GeneralList generalList = generalListService?.getInstance(params)
            respond generalList
        } else {
            notFound()
        }
    }

    /**
     * To add the request into list
     */
    def addRequestToList = {
        GeneralList generalList = generalListService?.addRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (generalList.hasErrors()) {
                respond generalList, view: 'sendList'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * To send the list to the receiving party
     */
    def sendList = {
        GeneralList generalList = generalListService.sendData(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.sendList.error')
        if (request.xhr) {
            render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (generalList.hasErrors()) {
                respond generalList, view: 'sendData'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to receive the list from received party.
     */
    def receiveList = {
        GeneralList generalList = generalListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (generalList.hasErrors()) {
                respond generalList, view: 'receiveListModal'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of  list's request to Approved
     */
    def approveRequest = {
        GeneralList generalList = generalListService.approveRequest(params)
        String successMessage = message(code: 'generalList.requestApproved.message')
        String failMessage = message(code: 'generalList.not.requestApproved.message')
        render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to change the status of  list's request to Rejected
     */
    def rejectRequest = {
        GeneralList generalList = generalListService.rejectRequest(params)
        String successMessage = message(code: 'generalList.requestRejected.message')
        String failMessage = message(code: 'generalList.not.requestRejected.message')
        render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to close the  general list
     */
    def closeList = {
        GeneralList generalList = generalListService.closeList(params)
        String successMessage = message(code: 'generalList.closeList.message')
        String failMessage = message(code: 'generalList.not.closeList.message')
        render text: (formatService.buildResponse(generalList, successMessage, failMessage) as JSON), contentType: "application/json"
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

