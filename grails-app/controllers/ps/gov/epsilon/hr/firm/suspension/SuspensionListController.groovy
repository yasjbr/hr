package ps.gov.epsilon.hr.firm.suspension

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route SuspensionList requests between model and views.
 * @see SuspensionListService
 * @see FormatService
 * */
class SuspensionListController {
    SuspensionListService suspensionListService
    FormatService formatService
    SharedService sharedService
    CorrespondenceListService correspondenceListService

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
        respond sharedService.getAttachmentTypeListAsMap(SuspensionList.getName(), EnumOperation.SUSPENSION_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                respond suspensionList
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
        respond new SuspensionList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = suspensionListService.searchWithRemotingValues(params)
        render text: (suspensionListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        SuspensionList suspensionList = suspensionListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), suspensionList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), suspensionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionList, successMessage, failMessage, true, getControllerName(), "manageSuspensionList") as JSON), contentType: "application/json"
        } else {
            if (suspensionList?.hasErrors()) {
                respond suspensionList, view: 'create'
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
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList && suspensionList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond suspensionList
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
        SuspensionList suspensionList = suspensionListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), suspensionList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), suspensionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (suspensionList.hasErrors()) {
                respond suspensionList, view: 'edit'
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
        DeleteBean deleteBean = suspensionListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), params?.id, deleteBean.responseMessage ?: ""])
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
    def manageSuspensionList = {
        if (params.encodedId || params.id) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                Map map = sharedService.getAttachmentTypeListAsMap(SuspensionList.getName(), EnumOperation.SUSPENSION_LIST)
                map.suspensionList = suspensionList
                map.showReceiveList = correspondenceListService.getCanReceiveList(suspensionList)
                respond map
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * this action is used to add suspension  request to suspension  list
     */
    def addSuspensionRequestsModal = {
        if (params.id) {
            Map map = [id: params["id"]]
            respond map
        } else {
            render ""
        }
    }

    /**
     * add suspension request to suspension list instance
     */
    def addSuspensionRequests = {
        SuspensionList suspensionList = suspensionListService.addSuspensionRequests(params)
        String successMessage = message(code: 'suspensionList.addSuspensionRequest.message')
        String failMessage = message(code: 'suspensionList.not.addSuspensionRequest.message')
        render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of suspension list.
     */
    def sendDataModal = {
        if (params.id) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                Map map = [suspensionList: suspensionList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to send the suspension list to the receiving party
     */
    def sendData = {
        SuspensionList suspensionList = suspensionListService.sendData(params)
        String successMessage = message(code: 'suspensionList.sendData.message')
        String failMessage = message(code: 'suspensionList.not.sendData.message')
        render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of suspension list
     */
    def receiveDataModal = {
        if (params.id) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                Map map = [suspensionList: suspensionList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * receive suspension list from receiving party
     */
    def receiveData = {
        SuspensionList suspensionList = suspensionListService.receiveData(params)
        String successMessage = message(code: 'suspensionList.saveReceivedForm.message')
        String failMessage = message(code: 'suspensionList.not.saveReceivedForm.message')
        render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of suspension list
     */
    def approveRequestModal = {
        if (params.id) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                Map map = [suspensionList: suspensionList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of suspension list request to Approved
     */
    def changeRequestToApproved = {
        SuspensionList suspensionList = suspensionListService.approveSuspensionRequest(params)
        String successMessage = message(code: 'suspensionList.requestApproved.message')
        String failMessage = message(code: 'suspensionList.not.requestApproved.message')
        render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of suspension list.
     */
    def rejectRequestModal = {
        if (params.id) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                Map map = [suspensionList: suspensionList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of suspension list request to Rejected
     */
    def rejectRequest = {
        SuspensionList suspensionList = suspensionListService.changeSuspensionRequestToRejected(params)
        String successMessage = message(code: 'suspensionList.requestRejected.message')
        String failMessage = message(code: 'suspensionList.error.note.message')
        render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of suspension list.
     */
    def closeModal = {
        if (params.id) {
            SuspensionList suspensionList = suspensionListService.getInstance(params)
            if (suspensionList) {
                Map map = [suspensionList: suspensionList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to close the  suspension list
     */
    def closeList = {
        SuspensionList suspensionList = suspensionListService.closeList(params);
        String successMessage = message(code: 'suspensionList.closeList.message')
        String failMessage = message(code: 'suspensionList.not.closeList.message')
        render text: (formatService.buildResponse(suspensionList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * render the note list modal!
     */
    def noteList = {
        return [id: params["id"]]
    }

    /**
     * render the create note modal!
     */
    def noteCreate = {
        return [id: params["id"]]
    }

    /* to handle requests if object not found.
    * @return void
    */

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'suspensionList.entity', default: 'SuspensionList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

