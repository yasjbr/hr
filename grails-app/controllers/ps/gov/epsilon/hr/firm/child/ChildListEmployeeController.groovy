package ps.gov.epsilon.hr.firm.child

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ChildListEmployee requests between model and views.
 *@see ChildListEmployeeService
 *@see FormatService
**/
class ChildListEmployeeController  {

    ChildListEmployeeService childListEmployeeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

//    def show= {
//        if(params.encodedId){
//            ChildListEmployee childListEmployee = childListEmployeeService.getInstance(params)
//            if(childListEmployee){
//                respond childListEmployee
//                return
//            }
//        }else{
//            notFound()
//        }
//    }

//    def create = {
//        respond new ChildListEmployee(params)
//    }

    def filter = {
        PagedResultList pagedResultList = childListEmployeeService.search(params)
        render text: (childListEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def filterRequest = {
        params.domainName = "LIST_DOMAIN_COLUMNS"
        PagedResultList pagedResultList = childListEmployeeService.searchWithRemotingValues(params)
        render text: (childListEmployeeService.resultListToMap(pagedResultList,params, childListEmployeeService.LIST_DOMAIN_COLUMNS) as JSON), contentType: "application/json"
    }

//    def save = {
//        //TODO
//        params["firm.id"] = session.getAttribute("firmId")?:1L
//        ChildListEmployee childListEmployee = childListEmployeeService.save(params)
//        String successMessage = message(code: 'default.created.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), childListEmployee?.id])
//        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), childListEmployee?.id])
//        if (request.xhr) {
//            render text: (formatService.buildResponse(childListEmployee, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
//        }
//        else {
//            if (childListEmployee?.hasErrors()) {
//                respond childListEmployee, view:'create'
//                return
//            }else{
//                flash.message = msg.success(label:successMessage)
//                redirect(action: "list")
//            }
//        }
//    }
//
//    def edit = {
//        if(params.encodedId){
//            ChildListEmployee childListEmployee = childListEmployeeService.getInstance(params)
//            if(childListEmployee){
//                respond childListEmployee
//                return
//            }
//        }else{
//            notFound()
//        }
//    }
//
//    def update = {
//        ChildListEmployee childListEmployee = childListEmployeeService.save(params)
//        String successMessage = message(code: 'default.updated.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), childListEmployee?.id])
//        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), childListEmployee?.id])
//        if (request.xhr) {
//            render text: (formatService.buildResponse(childListEmployee,successMessage,failMessage) as JSON), contentType: "application/json"
//        }
//        else {
//            if (childListEmployee.hasErrors()) {
//                respond childListEmployee, view:'edit'
//                return
//            }else{
//                flash.message = msg.success(label:successMessage)
//                redirect(action: "list")
//            }
//        }
//    }
//
    def delete = {
        DeleteBean deleteBean = childListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (childListEmployeeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'childListEmployee.entity', default: 'ChildListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

