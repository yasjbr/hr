package ps.gov.epsilon.hr.firm.dispatch

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route DispatchListEmployee requests between model and views.
 *@see DispatchListEmployeeService
 *@see FormatService
**/
class DispatchListEmployeeController  {

    DispatchListEmployeeService dispatchListEmployeeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def filter = {
        PagedResultList pagedResultList = dispatchListEmployeeService.searchWithRemotingValues(params)
        render text: (dispatchListEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def delete = {
        DeleteBean deleteBean = dispatchListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'dispatchListEmployee.entity', default: 'DispatchListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'dispatchListEmployee.entity', default: 'DispatchListEmployee'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (dispatchListEmployeeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dispatchListEmployee.entity', default: 'DispatchListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

