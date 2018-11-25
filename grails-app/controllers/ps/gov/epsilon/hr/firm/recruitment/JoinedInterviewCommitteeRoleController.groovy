package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route JoinedInterviewCommitteeRole requests between model and views.
 *@see JoinedInterviewCommitteeRoleService
 *@see FormatService
**/
class JoinedInterviewCommitteeRoleController  {

    JoinedInterviewCommitteeRoleService joinedInterviewCommitteeRoleService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.id){
            JoinedInterviewCommitteeRole joinedInterviewCommitteeRole = joinedInterviewCommitteeRoleService.getInstance(params, true)
            respond joinedInterviewCommitteeRole
        }else{
            notFound()
        }
    }

    def create = {
        respond new JoinedInterviewCommitteeRole(params)
    }

    def filter = {
        PagedResultList pagedResultList = joinedInterviewCommitteeRoleService.search(params,true)
        render text: (joinedInterviewCommitteeRoleService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        JoinedInterviewCommitteeRole joinedInterviewCommitteeRole = joinedInterviewCommitteeRoleService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), joinedInterviewCommitteeRole?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), joinedInterviewCommitteeRole?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedInterviewCommitteeRole, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedInterviewCommitteeRole?.hasErrors()) {
                respond joinedInterviewCommitteeRole, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.id){
            respond joinedInterviewCommitteeRoleService.getInstance(params, true)
        }else{
            notFound()
        }
    }

    def update = {
        JoinedInterviewCommitteeRole joinedInterviewCommitteeRole = joinedInterviewCommitteeRoleService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), joinedInterviewCommitteeRole?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), joinedInterviewCommitteeRole?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedInterviewCommitteeRole,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedInterviewCommitteeRole.hasErrors()) {
                respond joinedInterviewCommitteeRole, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = joinedInterviewCommitteeRoleService.delete(PCPUtils.convertParamsToDeleteBean(params),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (joinedInterviewCommitteeRoleService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'joinedInterviewCommitteeRole.entity', default: 'JoinedInterviewCommitteeRole'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

