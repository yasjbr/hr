package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import grails.gorm.PagedResultList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ViolationListEmployee requests between model and views.
 *@see ViolationListEmployeeService
 *@see FormatService
**/
class ViolationListEmployeeController {

    ViolationListEmployeeService violationListEmployeeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def filter = {
        PagedResultList pagedResultList = violationListEmployeeService.searchWithRemotingValues(params)
        render text: (violationListEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def filterAbsence = {
        PagedResultList pagedResultList = violationListEmployeeService.searchWithRemotingValues(params)
        render text: (violationListEmployeeService.resultListToMap(pagedResultList,params, violationListEmployeeService.DOMAIN_COLUMNS) as JSON), contentType: "application/json"
    }

    def delete = {
        DeleteBean deleteBean = violationListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'violationListEmployee.entity', default: 'ViolationListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'violationListEmployee.entity', default: 'ViolationListEmployee'), params?.id,deleteBean.responseMessage?:""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'absence.entity', default: 'ViolationListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

