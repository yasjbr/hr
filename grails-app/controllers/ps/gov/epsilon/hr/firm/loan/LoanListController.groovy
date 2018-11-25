package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route LoanList requests between model and views.
 *@see LoanListService
 *@see FormatService
**/
class LoanListController  {

    LoanListService loanListService
    FormatService formatService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
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
        respond sharedService.getAttachmentTypeListAsMap(LoanList.getName(), EnumOperation.LOAN_LIST)
    }
    
    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            LoanList loanList = loanListService.getInstance(params)
            if(loanList){
                respond loanList
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
        respond new LoanList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = loanListService.searchWithRemotingValues(params)
        render text: (loanListService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        LoanList loanList = loanListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'loanList.entity', default: 'LoanList'), loanList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'loanList.entity', default: 'LoanList'), loanList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanList, successMessage, failMessage, true, getControllerName(),"manageLoanList") as JSON), contentType: "application/json"
        }
        else {
            if (loanList?.hasErrors()) {
                respond loanList, view:'create'
                return
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
            LoanList loanList = loanListService.getInstance(params)
            //allow edit when have CREATED status only
            if(loanList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED){
                respond loanList
                return
            }else {
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
        LoanList loanList = loanListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'loanList.entity', default: 'LoanList'), loanList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'loanList.entity', default: 'LoanList'), loanList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (loanList.hasErrors()) {
                respond loanList, view:'edit'
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
        DeleteBean deleteBean = loanListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanList.entity', default: 'LoanList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanList.entity', default: 'LoanList'), params?.id,deleteBean.responseMessage?:""])
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
    def manageLoanList = {
        if (params.encodedId || params.id) {
            LoanList loanList = loanListService?.getInstance(params)
            if(loanList){
                Map map = sharedService.getAttachmentTypeListAsMap(LoanList.getName(), EnumOperation.LOAN_LIST)
                map.loanList = loanList
                map.showReceiveList = correspondenceListService.getCanReceiveList(loanList)
                respond map
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }


    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            LoanList loanList = loanListService?.getInstance(params)
            if(loanList){
                respond loanList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }


    //to send the loanList to the receiving party
    def sendList = {
        LoanList loanList = loanListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(loanList,successMessage,failMessage) as JSON), contentType: "application/json"
        }else {
            notFound()
        }
    }

    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def addRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            LoanList loanList = loanListService?.getInstance(params)
            if(loanList){
                respond loanList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * To add the loan request into list
     */
    def addRequest = {
        Map map = loanListService?.addRequest(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildMapResponse(map,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

     /**
     * this action was added to return the loan list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            LoanList loanList = loanListService.getInstance(params)
            if(loanList){
                respond loanList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }

    //to receive the loanList
    def receiveList = {
        LoanList loanList = loanListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(loanList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            LoanList loanList = loanListService.getInstance(params)
            if(loanList){
                respond loanList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }


    /**
     * to send the loanList to the receiving party
     */
    def closeList = {
        Map map = loanListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {
            render text: (formatService.buildMapResponse(map,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            LoanList loanList = loanListService.getInstanceWithRemotingValues(params)
            if(loanList){
                respond loanList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }

    //to change the status of loanList request to Approved
    def approveRequest = {
        Map map = loanListService.approveRequest(params)
        String successMessage = message(code: 'list.requestApproved.message')
        String failMessage = message(code: 'list.not.requestApproved.message')
        if (request.xhr) {
            render text: (formatService.buildMapResponse(map,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }


    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            LoanList loanList = loanListService.getInstanceWithRemotingValues(params)
            if(loanList){
                respond loanList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }



    //to change the status of loanList request to Rejected
    def rejectRequest = {
        Map map = loanListService.rejectRequest(params)
        String successMessage = message(code: 'list.requestApproved.message')
        String failMessage = message(code: 'list.not.requestApproved.message')
        if (request.xhr) {
            render text: (formatService.buildMapResponse(map,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }



    /**
     * render the note list modal!
     */
    def noteList = {
        return [encodedId:params["encodedId"]]
    }

    /**
     * render the create note modal!
     */
    def noteCreate = {
        return [encodedId:params["encodedId"]]
    }



    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def getReceivedLoanPersonAJAX = {
        if (params.id) {
            render loan.getReceivedLoanPerson([id:params.id])
        } else {
            render ""
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanList.entity', default: 'LoanList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

