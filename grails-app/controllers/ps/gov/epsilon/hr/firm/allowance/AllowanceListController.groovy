package ps.gov.epsilon.hr.firm.allowance

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route AllowanceList requests between model and views.
 * @see AllowanceListService
 * @see FormatService
 * */
class AllowanceListController {

    AllowanceListService allowanceListService
    FormatService formatService
    SharedService sharedService
    CorrespondenceListService correspondenceListService
    FirmSettingService firmSettingService

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
        respond sharedService.getAttachmentTypeListAsMap(AllowanceList.getName(), EnumOperation.ALLOWANCE_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            AllowanceList allowanceList = allowanceListService.getInstanceWithRemotingValues(params)
            if (allowanceList) {
                respond allowanceList
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
        respond new AllowanceList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = allowanceListService.searchWithRemotingValues(params)
        render text: (allowanceListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        AllowanceList allowanceList = allowanceListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), allowanceList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), allowanceList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceList, successMessage, failMessage, true, getControllerName(), "manageAllowanceList") as JSON), contentType: "application/json"
        } else {
            if (allowanceList?.hasErrors()) {
                respond allowanceList, view: 'create'
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
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList && allowanceList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond allowanceList
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
        AllowanceList allowanceList = allowanceListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), allowanceList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), allowanceList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (allowanceList?.hasErrors()) {
                respond allowanceList, view: 'edit'
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
        DeleteBean deleteBean = allowanceListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), params?.id, deleteBean.responseMessage ?: ""])
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
    def manageAllowanceList = {
        if (params.encodedId || params.id) {
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList) {
                Map map = sharedService.getAttachmentTypeListAsMap(AllowanceList.getName(), EnumOperation.ALLOWANCE_LIST)
                map.allowanceList = allowanceList
                map.showReceiveList = correspondenceListService.getCanReceiveList(allowanceList)
                map.isCentralizedWithAOC = firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.value, allowanceList?.firm?.id)?.toBoolean()
                respond map
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }


    /**
     * this action is used to add allowance  request to allowance  list
     */
    def addAllowanceRequestsModal = {
        if (params.id) {
            Map map = [id: params["id"]]
            respond map
        } else {
            render ""
        }
    }

    /**
     * add allowance request to allowance list
     */
    def addAllowanceRequests = {
        AllowanceList allowanceList = allowanceListService.addAllowanceRequests(params)
        String successMessage = message(code: 'allowanceList.addAllowanceRequest.message')
        String failMessage = message(code: 'allowanceList.not.addAllowanceRequest.message')
        render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of allowance list.
     */
    def sendDataModal = {
        if (params.id) {
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList) {
                Map map = [allowanceList: allowanceList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to send the allowance list to the receiving party
     */
    def sendData = {
        AllowanceList allowanceList = allowanceListService.sendData(params)
        String successMessage = message(code: 'allowanceList.sendData.message')
        String failMessage = message(code: 'allowanceList.not.sendData.message')
        render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of allowance list.
     */
    def receiveDataModal = {
        if (params.id) {
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList) {
                Map map = [allowanceList: allowanceList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * receive allowance list from receiving party
     */
    def receiveData = {
        AllowanceList allowanceList = allowanceListService.receiveData(params)
        String successMessage = message(code: 'allowanceList.saveReceivedForm.message')
        String failMessage = message(code: 'allowanceList.not.saveReceivedForm.message')
        render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of allowance list.
     */
    def approveRequestModal = {
        if (params.id) {
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList) {
                Map map = [allowanceList: allowanceList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of allowance list request to Approved
     */
    def changeRequestToApproved = {
        AllowanceList allowanceList = allowanceListService.approveAllowanceRequest(params)
        String successMessage = message(code: 'allowanceList.requestApproved.message')
        String failMessage = message(code: 'allowanceList.not.requestApproved.message')
        render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of allowance list.
     */
    def rejectRequestModal = {
        if (params.id) {
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList) {
                Map map = [allowanceList: allowanceList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of allowance list request to Rejected
     */
    def rejectRequest = {
        AllowanceList allowanceList = allowanceListService.changeAllowanceRequestToRejected(params)
        String successMessage = message(code: 'allowanceList.requestRejected.message')
        String failMessage = message(code: 'allowanceList.not.requestRejected.message')
        render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of allowance list.
     */
    def closeModal = {
        if (params.id) {
            AllowanceList allowanceList = allowanceListService.getInstance(params)
            if (allowanceList) {
                Map map = [allowanceList: allowanceList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to close the  allowance list
     */
    def closeList = {
        AllowanceList allowanceList = allowanceListService.closeList(params);
        String successMessage = message(code: 'allowanceList.closeList.message')
        String failMessage = message(code: 'allowanceList.not.closeList.message')
        render text: (formatService.buildResponse(allowanceList, successMessage, failMessage) as JSON), contentType: "application/json"
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'allowanceList.entity', default: 'AllowanceList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

