package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route LoanNotice requests between model and views.
 * @see LoanNoticeService
 * @see FormatService
 * */
class LoanNoticeController {

    LoanNoticeService loanNoticeService
    FormatService formatService
    SharedService sharedService

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
        respond sharedService.getAttachmentTypeListAsMap(LoanNotice.getName(), EnumOperation.LOAN_NOTICE)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            LoanNotice loanNotice = loanNoticeService.getInstanceWithRemotingValues(params)
            if (loanNotice) {
                Map data = (sharedService.getAttachmentTypeListAsMap(LoanRequest.getName(), EnumOperation.LOAN_NOTICE_REPLAY_REQUEST))
                data.loanNotice = loanNotice
                respond data
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new LoanNotice(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = loanNoticeService.searchWithRemotingValues(params)
        render text: (loanNoticeService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        LoanNotice loanNotice = loanNoticeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), loanNotice?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), loanNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanNotice, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (loanNotice?.hasErrors()) {
                respond loanNotice, view: 'create'
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
        String failMessage = message(code: 'loanNotice.fail.edit.message', args: [])
        if (params.encodedId) {
            LoanNotice loanNotice = loanNoticeService.getInstanceWithRemotingValues(params)
            if (loanNotice && loanNotice.loanNoticeStatus == EnumLoanNoticeStatus.UNDER_NOMINATION) {
                respond loanNotice
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
        LoanNotice loanNotice = loanNoticeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), loanNotice?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), loanNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(loanNotice, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (loanNotice.hasErrors()) {
                respond loanNotice, view: 'edit'
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
        DeleteBean deleteBean = loanNoticeService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), params?.id, deleteBean.responseMessage ?: ""])
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
     * end nomination and change status
     */
    def endNomination = {
        params['endNomination'] = 'true'
        LoanNotice loanNotice = loanNoticeService.save(params)
        String successMessage = message(code: 'loanNotice.endNomination.success.message')
        if (loanNotice.hasErrors()) {
            flash.message = msg.errorList(data: (formatService.formatAllErrors(loanNotice)?.message))
        } else {
            flash.message = msg.success(label: successMessage)
        }
        redirect(action: "list")
    }

    /**
     * close nomination and change status
     */
    def closeNomination = {
        params['closeNomination'] = 'true'
        LoanNotice loanNotice = loanNoticeService.save(params)
        String successMessage = message(code: 'loanNotice.closeNomination.success.message')
        if (loanNotice.hasErrors()) {
            flash.message = msg.errorList(data: (formatService.formatAllErrors(loanNotice)?.message))
        } else {
            flash.message = msg.success(label: successMessage)
        }
        redirect(action: "list")
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanNotice.entity', default: 'LoanNotice'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

