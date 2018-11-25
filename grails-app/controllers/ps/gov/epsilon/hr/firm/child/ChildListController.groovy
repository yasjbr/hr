package ps.gov.epsilon.hr.firm.child

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.*
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route ChildList requests between model and views.
 * @see ChildListService
 * @see FormatService
 * */
class ChildListController {

    ChildListService childListService
    FormatService formatService
    SharedService sharedService
    CorrespondenceListService correspondenceListService

    static allowedMethods = [save: "POST", update: "POST"]

    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(ChildList.getName(), EnumOperation.CHILD_LIST)
    }

    def create = {
        respond new ChildList(params)
    }

    def show = {
        if (params.encodedId) {
            ChildList childList = childListService.getInstanceWithRemotingValues(params)
            if (childList) {
                respond childList
                return
            }
        } else {
            notFound()
        }
    }

    def filter = {
        PagedList pagedResultList = childListService.searchWithRemotingValues(params)
        render text: (childListService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ChildList childList = childListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'childList.entity', default: 'ChildList'), childList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'childList.entity', default: 'ChildList'), childList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(childList, successMessage, failMessage, true, getControllerName(), "manageChildList") as JSON), contentType: "application/json"
        } else {
            if (childList?.hasErrors()) {
                respond childList, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        String failMessage = message(code: 'list.fail.edit.message', args: [])
        if (params.encodedId) {
            ChildList childList = childListService.getInstance(params)
            if (childList && childList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond childList
                return
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    def update = {
        ChildList childList = childListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'childList.entity', default: 'ChildList'), childList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'childList.entity', default: 'ChildList'), childList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(childList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (childList.hasErrors()) {
                respond childList, view: 'edit'
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
        DeleteBean deleteBean = childListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'childList.entity', default: 'ChildList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'childList.entity', default: 'ChildList'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (childListService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageChildList = {
        if (params.encodedId || params.id) {
            ChildList childList = childListService.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(ChildList.getName(), EnumOperation.CHILD_LIST)
            map.childList = childList
            map.showReceiveList = correspondenceListService.getCanReceiveList(childList)
            respond map
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the child list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ChildList childList = childListService.getInstance(params)
            respond childList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the child list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ChildList childList = childListService.getInstance(params)
            respond childList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the child list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ChildList childList = childListService.getInstance(params)
            respond childList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the child list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ChildList childList = childListService.getInstanceWithRemotingValues(params)
            respond childList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the child list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ChildList childList = childListService.getInstanceWithRemotingValues(params)
            respond childList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to add the child requests modal
     */
    def addRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ChildList childList = childListService?.getInstance(params)
            respond childList
        } else {
            notFound()
        }
    }

    /**
     * To add the child request into list
     */
    def addRequestToList = {
        ChildList childList = childListService?.addChildRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(childList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (childList.hasErrors()) {
                respond childList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * To send the childList to the receiving party
     */
    def sendList = {
        ChildList childList = childListService.sendData(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(childList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (childList.hasErrors()) {
                respond childList, view: 'sendData'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to receive the childList
     */
    def receiveList = {
        ChildList childList = childListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(childList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (childList.hasErrors()) {
                respond childList, view: 'receiveListModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of childList request to Approved
     */
    def changeRequestToApproved = {
        Map dataMap = childListService.changeRequestToApproved(params)
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
     * to change the status of childList request to Rejected
     */
    def changeRequestToRejected = {
        Map dataMap = childListService.changeRequestToRejected(params)
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
     * to send the childList to the receiving party
     */
    def closeList = {
        ChildList childList = childListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {

            render text: (formatService.buildResponse(childList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (childList.hasErrors()) {
                respond childList, view: 'rejectRequestModal'
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'childList.entity', default: 'ChildList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

