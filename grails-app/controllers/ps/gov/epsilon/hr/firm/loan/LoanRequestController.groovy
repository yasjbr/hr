package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequest
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route LoanRequest requests between model and views.
 * @see LoanRequestService
 * @see FormatService
 * */
class LoanRequestController {

    LoanRequestService loanRequestService
    FormatService formatService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

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
        respond sharedService.getAttachmentTypeListAsMap(LoanRequest.getName(), EnumOperation.LOAN_REQUEST)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params['encodedId'] || params['encodedRequestId']) {
            if (params['encodedRequestId']) {
                params['encodedId'] = params.remove("encodedRequestId")
            }
            LoanRequest loanRequest = loanRequestService.getInstanceWithRemotingValues(params)
            if (loanRequest) {
                respond loanRequest
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

        Map map = [:]

        LoanRequest loanRequestInstance = new LoanRequest(params)
        map.loanRequest = loanRequestInstance

        // in case: if the user has HR role, get the suitable workflow
        if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

            WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                    null, null, null, null,
                    LoanRequest.getName(),
                    loanRequestInstance?.id,
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
        PagedResultList pagedResultList = loanRequestService.searchWithRemotingValues(params)
        render text: (loanRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        LoanRequest loanRequest = loanRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), loanRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), loanRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (loanRequest?.hasErrors()) {
                respond loanRequest, view: 'create'
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
            params['requestStatus'] = EnumRequestStatus.CREATED.toString()
            LoanRequest loanRequest = loanRequestService.getInstanceWithRemotingValues(params)
            if (loanRequest) {
                respond loanRequest
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
        LoanRequest loanRequest = loanRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), loanRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), loanRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (loanRequest.hasErrors()) {
                respond loanRequest, view: 'edit'
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
        DeleteBean deleteBean = loanRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (loanRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "loan", LoanListPerson, false, null, true))
        } else {
            notFound()
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanRequest.entity', default: 'LoanRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

