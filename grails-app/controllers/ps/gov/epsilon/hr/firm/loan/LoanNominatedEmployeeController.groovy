package ps.gov.epsilon.hr.firm.loan

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route LoanNominatedEmployee requests between model and views.
 *@see LoanNominatedEmployeeService
 *@see FormatService
**/
class LoanNominatedEmployeeController  {

    LoanNominatedEmployeeService loanNominatedEmployeeService
    FormatService formatService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {
        respond sharedService.getAttachmentTypeListAsMap(LoanNominatedEmployee.getName(), EnumOperation.LOAN_NOMINATED_EMPLOYEE)
    }

    def show= {
        if(params.encodedId){
            LoanNominatedEmployee loanNominatedEmployee = loanNominatedEmployeeService.getInstance(params)
            if(loanNominatedEmployee){
                respond loanNominatedEmployee
                return
            }
        }else{
            notFound()
        }
    }

    def filter = {
        PagedResultList pagedResultList = loanNominatedEmployeeService.searchWithRemotingValues(params)
        render text: (loanNominatedEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def delete = {
        DeleteBean deleteBean = loanNominatedEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee'), params?.id,deleteBean.responseMessage?:""])
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
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

