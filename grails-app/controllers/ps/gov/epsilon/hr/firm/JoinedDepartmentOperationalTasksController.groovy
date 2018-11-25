package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.gov.epsilon.hr.firm.lookups.OperationalTaskService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route JoinedDepartmentOperationalTasks requests between model and views.
 *@see JoinedDepartmentOperationalTasksService
 *@see FormatService
 **/
class JoinedDepartmentOperationalTasksController  {

    JoinedDepartmentOperationalTasksService joinedDepartmentOperationalTasksService
    FormatService formatService
    OperationalTaskService operationalTaskService


    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.id){
            JoinedDepartmentOperationalTasks joinedDepartmentOperationalTasks = joinedDepartmentOperationalTasksService.getInstance(params, true)
            respond joinedDepartmentOperationalTasks
        }else{
            notFound()
        }
    }

    def create = {
        respond new JoinedDepartmentOperationalTasks(params)


    }

    def filter = {
        PagedResultList pagedResultList = joinedDepartmentOperationalTasksService.search(params,true)
        render text: (joinedDepartmentOperationalTasksService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        JoinedDepartmentOperationalTasks joinedDepartmentOperationalTasks = joinedDepartmentOperationalTasksService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), joinedDepartmentOperationalTasks?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), joinedDepartmentOperationalTasks?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedDepartmentOperationalTasks, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedDepartmentOperationalTasks?.hasErrors()) {
                respond joinedDepartmentOperationalTasks, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.id){
            respond joinedDepartmentOperationalTasksService.getInstance(params, true)
        }else{
            notFound()
        }
    }

    def update = {
        JoinedDepartmentOperationalTasks joinedDepartmentOperationalTasks = joinedDepartmentOperationalTasksService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), joinedDepartmentOperationalTasks?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), joinedDepartmentOperationalTasks?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedDepartmentOperationalTasks,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedDepartmentOperationalTasks.hasErrors()) {
                respond joinedDepartmentOperationalTasks, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = joinedDepartmentOperationalTasksService.delete(PCPUtils.convertParamsToDeleteBean(params),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (joinedDepartmentOperationalTasksService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

