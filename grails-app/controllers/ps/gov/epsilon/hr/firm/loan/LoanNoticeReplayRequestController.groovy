package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route LoanNoticeReplayRequest requests between model and views.
 *@see LoanNoticeReplayRequestService
 *@see FormatService
**/
class LoanNoticeReplayRequestController  {

    LoanNoticeReplayRequestService loanNoticeReplayRequestService
    FormatService formatService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

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
        respond sharedService.getAttachmentTypeListAsMap(LoanNoticeReplayRequest.getName(), EnumOperation.LOAN_NOTICE_REPLAY_REQUEST)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            LoanNoticeReplayRequest loanNoticeReplayRequest = loanNoticeReplayRequestService.getInstanceWithRemotingValues(params)
            if(loanNoticeReplayRequest){
                respond loanNoticeReplayRequest
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
        Map map = [:]

        LoanNoticeReplayRequest loanNoticeReplayRequestInstance = new LoanNoticeReplayRequest(params)
        map.loanNoticeReplayRequest = loanNoticeReplayRequestInstance

        // in case: if the user has HR role, get the suitable workflow
        if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

            WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                    null, null, null, null,
                    LoanNoticeReplayRequest.getName(),
                    loanNoticeReplayRequestInstance?.id,
                    false)

            // sort workflow path details
            workflowPathHeader?.workflowPathDetails = workflowPathHeader?.workflowPathDetails?.sort { a, b -> b.sequence <=> a.sequence }

            // add workflowPathHeader to map
            map.workflowPathHeader = workflowPathHeader
        }
        respond map
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = loanNoticeReplayRequestService.searchWithRemotingValues(params)
        render text: (loanNoticeReplayRequestService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        LoanNoticeReplayRequest loanNoticeReplayRequest = loanNoticeReplayRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), loanNoticeReplayRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), loanNoticeReplayRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanNoticeReplayRequest, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (loanNoticeReplayRequest?.hasErrors()) {
                respond loanNoticeReplayRequest, view:'create'
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
            params['requestStatus'] = EnumRequestStatus.CREATED.toString()
            LoanNoticeReplayRequest loanNoticeReplayRequest = loanNoticeReplayRequestService.getInstance(params)
            if(loanNoticeReplayRequest){
                respond loanNoticeReplayRequest
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
        LoanNoticeReplayRequest loanNoticeReplayRequest = loanNoticeReplayRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), loanNoticeReplayRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), loanNoticeReplayRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanNoticeReplayRequest,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (loanNoticeReplayRequest.hasErrors()) {
                respond loanNoticeReplayRequest, view:'edit'
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
        DeleteBean deleteBean = loanNoticeReplayRequestService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), params?.id,deleteBean.responseMessage?:""])
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
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect (sharedService.goToList(params["encodedId"]?.toString(),"loanNoticeReplay",LoanNominatedEmployee,false,null,true))
        }else{
            notFound()
        }
    }

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (loanNoticeReplayRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

