package ps.gov.epsilon.hr.firm.profile

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EmployeeExternalAssignation requests between model and views.
 *@see EmployeeExternalAssignationService
 *@see FormatService
**/
class EmployeeExternalAssignationController  {

    EmployeeExternalAssignationService employeeExternalAssignationService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            EmployeeExternalAssignation employeeExternalAssignation = employeeExternalAssignationService.getInstance(params)
            if(employeeExternalAssignation){
                respond employeeExternalAssignation
                return
            }
        }else{
            notFound()
        }
    }

    def create = {
        respond new EmployeeExternalAssignation(params)
    }

    def filter = {
        PagedResultList pagedResultList = employeeExternalAssignationService.searchWithRemotingValues(params)
        render text: (employeeExternalAssignationService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //TODO
        params["firm.id"] = session.getAttribute("firmId")?:1L
        EmployeeExternalAssignation employeeExternalAssignation = employeeExternalAssignationService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), employeeExternalAssignation?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), employeeExternalAssignation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeExternalAssignation, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (employeeExternalAssignation?.hasErrors()) {
                respond employeeExternalAssignation, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            EmployeeExternalAssignation employeeExternalAssignation = employeeExternalAssignationService.getInstance(params)
            if(employeeExternalAssignation){
                respond employeeExternalAssignation
                return
            }
        }else{
            notFound()
        }
    }

    def update = {
        EmployeeExternalAssignation employeeExternalAssignation = employeeExternalAssignationService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), employeeExternalAssignation?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), employeeExternalAssignation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeExternalAssignation,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (employeeExternalAssignation.hasErrors()) {
                respond employeeExternalAssignation, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = employeeExternalAssignationService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (employeeExternalAssignationService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeExternalAssignation.entity', default: 'EmployeeExternalAssignation'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

