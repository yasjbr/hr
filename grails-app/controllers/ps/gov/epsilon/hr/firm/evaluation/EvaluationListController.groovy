package ps.gov.epsilon.hr.firm.evaluation

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EvaluationList requests between model and views.
 *@see EvaluationListService
 *@see FormatService
**/
class EvaluationListController  {

    EvaluationListService evaluationListService
    FormatService formatService
    SharedService sharedService

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
        respond sharedService.getAttachmentTypeListAsMap(EvaluationList.getName(), EnumOperation.EVALUATION_LIST)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            if(evaluationList){
                respond evaluationList
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
        respond new EvaluationList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = evaluationListService.searchWithRemotingValues(params)
        render text: (evaluationListService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EvaluationList evaluationList = evaluationListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), evaluationList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), evaluationList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationList, successMessage, failMessage, true, getControllerName(),"manageEvaluationList") as JSON), contentType: "application/json"
        }
        else {
            if (evaluationList?.hasErrors()) {
                respond evaluationList, view:'create'
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
        String failMessage = message(code: 'list.fail.edit.message', args: [])
        if (params.encodedId) {
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            if (evaluationList && evaluationList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond evaluationList
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
        EvaluationList evaluationList = evaluationListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), evaluationList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), evaluationList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (evaluationList.hasErrors()) {
                respond evaluationList, view:'edit'
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
        DeleteBean deleteBean = evaluationListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (evaluationListService.autoComplete(params)), contentType: "application/json"
    }


    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageEvaluationList = {
        if (params.encodedId || params.id) {
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(EvaluationList.getName(), EnumOperation.EVALUATION_LIST)
            map.evaluationList = evaluationList
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
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            respond evaluationList
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
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            respond evaluationList
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
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            respond evaluationList
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
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            respond evaluationList
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
            EvaluationList evaluationList = evaluationListService.getInstance(params)
            respond evaluationList
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
            EvaluationList evaluationList = evaluationListService?.getInstance(params)
            respond evaluationList
        } else {
            notFound()
        }
    }

    /**
     * To add the child request into list
     */
    def addRequestToList = {
        EvaluationList evaluationList = evaluationListService?.addEmployeeEvaluationToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (evaluationList.hasErrors()) {
                respond evaluationList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * To send the evaluationList to the receiving party
     */
    def sendList = {
        EvaluationList evaluationList = evaluationListService.sendData(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (evaluationList.hasErrors()) {
                respond evaluationList, view: 'sendData'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to receive the evaluationList
     */
    def receiveList = {
        EvaluationList evaluationList = evaluationListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(evaluationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (evaluationList.hasErrors()) {
                respond evaluationList, view: 'receiveListModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of evaluationList request to Approved
     */
    def changeRequestToApproved = {
        Map dataMap = evaluationListService.changeRequestToApproved(params)
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
     * to change the status of evaluationList request to Rejected
     */
    def changeRequestToRejected = {
        Map dataMap = evaluationListService.changeRequestToRejected(params)
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
     * to send the evaluationList to the receiving party
     */
    def closeList = {
        EvaluationList evaluationList = evaluationListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {

            render text: (formatService.buildResponse(evaluationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (evaluationList.hasErrors()) {
                respond evaluationList, view: 'rejectRequestModal'
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'evaluationList.entity', default: 'EvaluationList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

