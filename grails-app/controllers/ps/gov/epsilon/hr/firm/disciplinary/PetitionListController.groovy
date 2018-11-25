package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route PetitionList requests between model and views.
 *@see PetitionListService
 *@see FormatService
**/
class PetitionListController  {

    PetitionListService petitionListService
    FormatService formatService
    SharedService sharedService
    CorrespondenceListService correspondenceListService

    static allowedMethods = [save: "POST", update: "POST"]


    /**
     * default action in controller
     */
    def index= {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list= {
        respond sharedService.getAttachmentTypeListAsMap(PetitionList.getName(), EnumOperation.PETITION_LIST)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            PetitionList petitionList = petitionListService.getInstanceWithRemotingValues(params)
            if(petitionList){
                respond petitionList
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new PetitionList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = petitionListService.searchWithRemotingValues(params)
        render text: (petitionListService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        PetitionList petitionList = petitionListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), petitionList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), petitionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionList, successMessage, failMessage, true, getControllerName(),"managePetitionList") as JSON), contentType: "application/json"
        }
        else {
            if (petitionList?.hasErrors()) {
                respond petitionList, view:'create'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId){
            PetitionList petitionList = petitionListService.getInstance(params)
            if(petitionList){
                respond petitionList
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        PetitionList petitionList = petitionListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), petitionList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), petitionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (petitionList.hasErrors()) {
                respond petitionList, view:'edit'
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
        DeleteBean deleteBean = petitionListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (petitionListService.autoComplete(params)), contentType: "application/json"
    }




    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def managePetitionList = {
        if (params.encodedId || params.id) {
            PetitionList petitionList = petitionListService.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(PetitionList.getName(), EnumOperation.PETITION_LIST)
            map.petitionList = petitionList
            map.showReceiveList = correspondenceListService.getCanReceiveList(petitionList)
            respond map
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the petition list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PetitionList petitionList = petitionListService.getInstance(params)
            respond petitionList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the petition list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PetitionList petitionList = petitionListService.getInstance(params)
            respond petitionList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the petition list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PetitionList petitionList = petitionListService.getInstance(params)
            respond petitionList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the petition list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PetitionList petitionList = petitionListService.getInstanceWithRemotingValues(params)
            respond petitionList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to return the petition list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PetitionList petitionList = petitionListService.getInstanceWithRemotingValues(params)
            respond petitionList
        } else {
            return
        }
        return
    }

    /**
     * this action was added to add the petition requests modal
     */
    def addRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PetitionList petitionList = petitionListService?.getInstance(params)
            respond petitionList
        } else {
            notFound()
        }
    }

    /**
     * To add the petition request into list
     */
    def addRequestToList = {
        PetitionList petitionList = petitionListService?.addRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (petitionList.hasErrors()) {
                respond petitionList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * To send the petitionList to the receiving party
     */
    def sendList = {
        PetitionList petitionList = petitionListService.sendData(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (petitionList.hasErrors()) {
                respond petitionList, view: 'sendData'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to receive the petitionList
     */
    def receiveList = {
        PetitionList petitionList = petitionListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(petitionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (petitionList.hasErrors()) {
                respond petitionList, view: 'receiveListModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of petitionList request to Approved
     */
    def changeRequestToApproved = {
        Map dataMap = petitionListService.changeRequestToApproved(params)
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
     * to change the status of petitionList request to Rejected
     */
    def changeRequestToRejected = {
        Map dataMap = petitionListService.changeRequestToRejected(params)
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
     * to send the petitionList to the receiving party
     */
    def closeList = {
        PetitionList petitionList = petitionListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {

            render text: (formatService.buildResponse(petitionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (petitionList.hasErrors()) {
                respond petitionList, view: 'rejectRequestModal'
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'petitionList.entity', default: 'PetitionList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

