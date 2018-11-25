package ps.gov.epsilon.hr.firm.dispatch

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.allowance.AllowanceList
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route DispatchList requests between model and views.
 * @see DispatchListService
 * @see FormatService
 * */
class DispatchListController {

    DispatchListService dispatchListService
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
        respond sharedService.getAttachmentTypeListAsMap(DispatchList.getName(), EnumOperation.DISPATCH_LIST)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            DispatchList dispatchList = dispatchListService.getInstanceWithRemotingValues(params)
            if (dispatchList) {
                respond dispatchList
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
        respond new DispatchList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = dispatchListService.searchWithRemotingValues(params)
        render text: (dispatchListService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DispatchList dispatchList = dispatchListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), dispatchList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), dispatchList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchList, successMessage, failMessage, true, getControllerName(), "manageDispatchList") as JSON), contentType: "application/json"
        } else {
            if (dispatchList?.hasErrors()) {
                respond dispatchList, view: 'create'
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
        if (params.encodedId) {
            DispatchList dispatchList = dispatchListService.getInstance(params)
            //allow edit when have CREATED status only
            if (dispatchList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond dispatchList
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        DispatchList dispatchList = dispatchListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), dispatchList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), dispatchList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (dispatchList.hasErrors()) {
                respond dispatchList, view: 'edit'
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
        DeleteBean deleteBean = dispatchListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), params?.id, deleteBean.responseMessage ?: ""])
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
    def manageDispatchList = {
        if(params["encodedListId"] || params.id){
            params.encodedId = params.remove("encodedListId")
        }
        if (params.encodedId) {
            DispatchList dispatchList = dispatchListService?.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(DispatchList.getName(), EnumOperation.DISPATCH_LIST)
            map.dispatchList = dispatchList
            map.showReceiveList = correspondenceListService.getCanReceiveList(dispatchList)
            respond map
        } else {
            notFound()
        }
    }



    /**
     * this action was added to add the dispatch requests modal
     */
    def addDispatchRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DispatchList dispatchList = dispatchListService?.getInstance(params)
            respond dispatchList
        } else {
            notFound()
        }
    }


    /**
     * this action was added to return the dispatch list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DispatchList dispatchList = dispatchListService?.getInstance(params)
            respond dispatchList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the dispatch list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DispatchList dispatchList = dispatchListService.getInstance(params)
            respond dispatchList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the dispatch list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DispatchList dispatchList = dispatchListService.getInstance(params)
            respond dispatchList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the dispatch list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DispatchList dispatchList = dispatchListService.getInstanceWithRemotingValues(params)
            respond dispatchList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the dispatch list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DispatchList dispatchList = dispatchListService.getInstanceWithRemotingValues(params)
            respond dispatchList
        } else {
            notFound()
        }
    }


    /**
     * To add the dispatch request into list
     */
    def addRequestToList = {
        DispatchList dispatchList = dispatchListService?.addRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (dispatchList.hasErrors()) {
                respond dispatchList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }


    //to send the dispatchList to the receiving party
    def sendList = {
        DispatchList dispatchList = dispatchListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (dispatchList.hasErrors()) {
                respond dispatchList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    //to receive the dispatchList
    def receiveList = {
        DispatchList dispatchList = dispatchListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(dispatchList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (dispatchList.hasErrors()) {
                respond dispatchList, view: 'receiveListModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    //to change the status of dispatchList request to Approved
    def changeRequestToApproved = {
        Map dataMap = dispatchListService.changeRequestToApproved(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success = !errors;
            def errorFormat = msg.errorList(data: (errors), isCustom: "true");
            json.message = json.success ? msg.success(label: successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        } else {
            notFound()
        }
    }

    //to change the status of dispatchList request to Rejected
    def changeRequestToRejected = {
        Map dataMap = dispatchListService.changeRequestToRejected(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success = !errors;
            def errorFormat = msg.errorList(data: (errors), isCustom: "true");
            json.message = json.success ? msg.success(label: successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        } else {
            notFound()
        }
    }

    /**
     * to send the dispatchList to the receiving party
     */
    def closeList = {
        DispatchList dispatchList = dispatchListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {

            render text: (formatService.buildResponse(dispatchList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (dispatchList.hasErrors()) {
                respond dispatchList, view: 'rejectRequestModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dispatchList.entity', default: 'DispatchList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

