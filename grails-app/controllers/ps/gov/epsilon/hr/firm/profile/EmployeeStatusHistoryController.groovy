package ps.gov.epsilon.hr.firm.profile

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EmployeeStatusHistory requests between model and views.
 *@see EmployeeStatusHistoryService
 *@see FormatService
**/
class EmployeeStatusHistoryController  {

    EmployeeStatusHistoryService employeeStatusHistoryService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            EmployeeStatusHistory employeeStatusHistory = employeeStatusHistoryService.getInstance(params)
            if(employeeStatusHistory){
                respond employeeStatusHistory
                return
            }
        }else{
            notFound()
        }
    }

    def filter = {
        PagedResultList pagedResultList = employeeStatusHistoryService.search(params)
        render text: (employeeStatusHistoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId")?:1L
        EmployeeStatusHistory employeeStatusHistory = employeeStatusHistoryService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), employeeStatusHistory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), employeeStatusHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeStatusHistory, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (employeeStatusHistory?.hasErrors()) {
                respond employeeStatusHistory, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId && SpringSecurityUtils.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_SUPER_ADMIN.value)){
            EmployeeStatusHistory employeeStatusHistory = employeeStatusHistoryService.getInstance(params)
            if(employeeStatusHistory){
                respond employeeStatusHistory
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        EmployeeStatusHistory employeeStatusHistory = employeeStatusHistoryService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), employeeStatusHistory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), employeeStatusHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeStatusHistory,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (employeeStatusHistory.hasErrors()) {
                respond employeeStatusHistory, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = employeeStatusHistoryService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (employeeStatusHistoryService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeStatusHistory.entity', default: 'EmployeeStatusHistory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

