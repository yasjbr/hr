package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import guiplugin.FormatService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route MaritalStatusList requests between model and views.
 *@see MaritalStatusListService
 *@see FormatService
**/
class MaritalStatusListController  {

    MaritalStatusListService maritalStatusListService
    FormatService formatService
    SharedService sharedService
    CorrespondenceListService correspondenceListService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(MaritalStatusList.getName(), EnumOperation.MARITAL_STATUS_LIST)
    }

    def create = {
        respond new MaritalStatusList(params)
    }

    def show= {
        if(params.encodedId){
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstanceWithRemotingValues(params)
            if(maritalStatusList){
                respond maritalStatusList
                return
            }
        }else{
            notFound()
        }
    }

    def filter = {
        PagedList pagedResultList = maritalStatusListService.searchWithRemotingValues(params)
        render text: (maritalStatusListService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }



    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        MaritalStatusList maritalStatusList = maritalStatusListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), maritalStatusList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), maritalStatusList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusList, successMessage, failMessage, true, getControllerName(),"manageMaritalStatusList") as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusList?.hasErrors()) {
                respond maritalStatusList, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        String failMessage = message(code: 'list.fail.edit.message', args: [])
        if(params.encodedId){
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstance(params)
            if(maritalStatusList && maritalStatusList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED){
                respond maritalStatusList
                return
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        }else{
            notFound()
        }
    }

    def update = {
        MaritalStatusList maritalStatusList = maritalStatusListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), maritalStatusList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), maritalStatusList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusList.hasErrors()) {
                respond maritalStatusList, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = maritalStatusListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (maritalStatusListService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageMaritalStatusList = {
        if (params.encodedId || params.id) {
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(MaritalStatusList.getName(), EnumOperation.MARITAL_STATUS_LIST)
            map.maritalStatusList = maritalStatusList
            map.showReceiveList = correspondenceListService.getCanReceiveList(maritalStatusList)
            respond map
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the maritalStatus list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstance(params)
            respond maritalStatusList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the maritalStatus list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstance(params)
            respond maritalStatusList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the maritalStatus list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstance(params)
            respond maritalStatusList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the maritalStatus list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstanceWithRemotingValues(params)
            respond maritalStatusList
        } else {
            return
        }
        return
    }


    /**
     * this action was added to return the maritalStatus list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            MaritalStatusList maritalStatusList = maritalStatusListService.getInstanceWithRemotingValues(params)
            respond maritalStatusList
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
            MaritalStatusList maritalStatusList = maritalStatusListService?.getInstance(params)
            respond maritalStatusList
        } else {
            notFound()
        }
    }


    /**
     * To add the child request into list
     */
    def addRequestToList = {
        MaritalStatusList maritalStatusList = maritalStatusListService?.addMaritalStatusRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (maritalStatusList.hasErrors()) {
                respond maritalStatusList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }


    //to send the maritalStatusList to the receiving party
    def sendList = {
        MaritalStatusList maritalStatusList = maritalStatusListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusList.hasErrors()) {
                respond maritalStatusList, view:'sendData'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    //to receive the maritalStatusList
    def receiveList = {
        MaritalStatusList maritalStatusList = maritalStatusListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(maritalStatusList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusList.hasErrors()) {
                respond maritalStatusList, view:'receiveListModal'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    //to change the status of maritalStatusList request to Approved
    def changeRequestToApproved = {
        Map dataMap = maritalStatusListService.changeRequestToApproved(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success =   !errors;
            def errorFormat = msg.errorList(data: (errors),isCustom:"true");
            json.message = json.success ? msg.success(label:successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

    //to change the status of maritalStatusList request to Rejected
    def changeRequestToRejected = {
        Map dataMap = maritalStatusListService.changeRequestToRejected(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success =   !errors;
            def errorFormat = msg.errorList(data: (errors),isCustom:"true");
            json.message = json.success ? msg.success(label:successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

    /**
     * to send the dispatchList to the receiving party
     */
    def closeList = {
        MaritalStatusList maritalStatusList = maritalStatusListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {

            render text: (formatService.buildResponse(maritalStatusList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (maritalStatusList.hasErrors()) {
                respond maritalStatusList, view:'rejectRequestModal'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }


    /**
     * render the note list modal!
     */
    def noteList = {
        return [id:params["id"]]
    }

    /**
     * render the create note modal!
     */
    def noteCreate = {
        return [id:params["id"]]
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'maritalStatusList.entity', default: 'MaritalStatusList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

