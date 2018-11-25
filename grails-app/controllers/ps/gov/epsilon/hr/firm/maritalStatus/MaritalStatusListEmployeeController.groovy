package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route MaritalStatusListEmployee requests between model and views.
 *@see MaritalStatusListEmployeeService
 *@see FormatService
**/
class MaritalStatusListEmployeeController  {

    MaritalStatusListEmployeeService maritalStatusListEmployeeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}


    def filter = {
        PagedResultList pagedResultList = maritalStatusListEmployeeService.searchWithRemotingValues(params)
        render text: (maritalStatusListEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * filter the marital status requests with list domain columns
     */
    def filterRequest = {
        params.domainName = "LIST_DOMAIN_COLUMNS"
        PagedResultList pagedResultList = maritalStatusListEmployeeService.searchWithRemotingValues(params)
        render text: (maritalStatusListEmployeeService.resultListToMap(pagedResultList,params, maritalStatusListEmployeeService.LIST_DOMAIN_COLUMNS) as JSON), contentType: "application/json"
    }

    def delete = {
        DeleteBean deleteBean = maritalStatusListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'maritalStatusListEmployee.entity', default: 'MaritalStatusListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'maritalStatusListEmployee.entity', default: 'MaritalStatusListEmployee'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (maritalStatusListEmployeeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'maritalStatusListEmployee.entity', default: 'MaritalStatusListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

