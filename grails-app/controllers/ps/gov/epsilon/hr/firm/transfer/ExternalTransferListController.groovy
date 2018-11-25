package ps.gov.epsilon.hr.firm.transfer

import grails.converters.JSON
import grails.gorm.PagedResultList
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
 * Route ExternalTransferList requests between model and views.
 * @see ExternalTransferListService
 * @see FormatService
 * */
class ExternalTransferListController {

    ExternalTransferListService externalTransferListService
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
        respond sharedService.getAttachmentTypeListAsMap(ExternalTransferList.getName(), EnumOperation.EXTERNAL_TRANSFER_LIST)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstanceWithRemotingValues(params)
            if (externalTransferList) {
                respond externalTransferList
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
        respond new ExternalTransferList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = externalTransferListService.searchWithRemotingValues(params)
        render text: (externalTransferListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ExternalTransferList externalTransferList = externalTransferListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), externalTransferList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), externalTransferList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage, true, getControllerName(), "manageExternalTransferList") as JSON), contentType: "application/json"
        } else {
            if (externalTransferList?.hasErrors()) {
                respond externalTransferList, view: 'create'
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
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList && externalTransferList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond externalTransferList
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
        ExternalTransferList externalTransferList = externalTransferListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), externalTransferList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), externalTransferList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (externalTransferList?.hasErrors()) {
                respond externalTransferList, view: 'edit'
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
        DeleteBean deleteBean = externalTransferListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), params?.id, ""])
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
    def manageExternalTransferList = {
        if (params.encodedId || params.id) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList) {
                Map map = sharedService.getAttachmentTypeListAsMap(ExternalTransferList.getName(), EnumOperation.EXTERNAL_TRANSFER_LIST)
                map.externalTransferList = externalTransferList
                map.showReceiveList = correspondenceListService.getCanReceiveList(externalTransferList)
                respond map
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * this action is used to add external transfer request to external transfer list
     */
    def addExternalTransferRequestsModal = {
        if (params.id) {
            Map map = [id: params["id"]]
            respond map
        } else {
            render ""
        }
    }

    /**
     * add externalTransferRequest to externalTransfer list instance
     */
    def addExternalTransferRequests = {
        ExternalTransferList externalTransferList = externalTransferListService.addExternalTransferRequests(params)
        String successMessage = message(code: 'externalTransferList.addExternalTransferRequest.message')
        String failMessage = message(code: 'externalTransferList.not.addExternalTransferRequest.message')
        render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of externalTransfer list.
     */
    def sendDataModal = {
        if (params.id) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList) {
                Map map = [externalTransferList: externalTransferList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to send the externalTransfer list to the receiving party
     */
    def sendData = {
        ExternalTransferList externalTransferList = externalTransferListService.sendData(params)
        String successMessage = message(code: 'externalTransferList.sendData.message')
        String failMessage = message(code: 'externalTransferList.not.sendData.message')
        render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of externalTransfer list.
     */
    def receiveDataModal = {
        if (params.id) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList) {
                Map map = [externalTransferList: externalTransferList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * receive externalTransfer list from receiving party
     */
    def receiveData = {
        ExternalTransferList externalTransferList = externalTransferListService.receiveData(params)
        String successMessage = message(code: 'externalTransferList.saveReceivedForm.message')
        String failMessage = message(code: 'externalTransferList.not.saveReceivedForm.message')
        render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of externalTransfer list.
     */
    def approveRequestModal = {
        if (params.id) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList) {
                Map map = [externalTransferList: externalTransferList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to add exceptional  external transfer to list.
     */
    def addExceptionalModal = {
        if (params.id) {
            Map map = [id: params["id"]]
            respond map
        } else {
            render ""
        }
    }

    /**
     * to change the status of externalTransfer list request to Approved
     */
    def changeRequestToApproved = {
        ExternalTransferList externalTransferList = externalTransferListService.approveExternalTransferRequest(params)
        String successMessage = message(code: 'externalTransferList.requestApproved.message')
        String failMessage = message(code: 'externalTransferList.not.requestApproved.message')
        render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of externalTransfer list.
     */
    def rejectRequestModal = {
        if (params.id) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList) {
                Map map = [externalTransferList: externalTransferList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of externalTransfer list request to Rejected
     */
    def rejectRequest = {
        ExternalTransferList externalTransferList = externalTransferListService.changeExternalTransferRequestToRejected(params)
        String successMessage = message(code: 'externalTransferList.requestRejected.message')
        String failMessage = message(code: 'externalTransferList.not.requestRejected.message')
        render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of externalTransfer list.
     */
    def closeModal = {
        if (params.id) {
            ExternalTransferList externalTransferList = externalTransferListService.getInstance(params)
            if (externalTransferList) {
                Map map = [externalTransferList: externalTransferList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to close the  externalTransfer list
     */
    def closeList = {
        ExternalTransferList externalTransferList = externalTransferListService.closeList(params);
        String successMessage = message(code: 'externalTransferList.closeList.message')
        String failMessage = message(code: 'externalTransferList.not.closeList.message')
        render text: (formatService.buildResponse(externalTransferList, successMessage, failMessage) as JSON), contentType: "application/json"
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

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalTransferList.entity', default: 'ExternalTransferList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

