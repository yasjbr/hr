package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route JoinedRecruitmentCycleDepartment requests between model and views.
 *@see JoinedRecruitmentCycleDepartmentService
 *@see FormatService
**/
class JoinedRecruitmentCycleDepartmentController  {

    JoinedRecruitmentCycleDepartmentService joinedRecruitmentCycleDepartmentService
    FormatService formatService
    RecruitmentCycleService recruitmentCycleService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {
        if(params.id){
            RecruitmentCycle recruitmentCycle = recruitmentCycleService.getInstance(params)
            respond recruitmentCycle
        }else{
            redirect(controller:"recruitmentCycle", action: "list")
        }
    }

    def show= {
        if(params.id){
            JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment = joinedRecruitmentCycleDepartmentService.getInstance(params)
            respond joinedRecruitmentCycleDepartment
        }else{
            notFound()
        }
    }

    def create = {
        respond new JoinedRecruitmentCycleDepartment(params)
    }

    def filter = {
        PagedResultList pagedResultList = joinedRecruitmentCycleDepartmentService.searchWithRemotingValues(params,true)
        render text: (joinedRecruitmentCycleDepartmentService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment = joinedRecruitmentCycleDepartmentService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), joinedRecruitmentCycleDepartment?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), joinedRecruitmentCycleDepartment?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedRecruitmentCycleDepartment, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedRecruitmentCycleDepartment?.hasErrors()) {
                respond joinedRecruitmentCycleDepartment, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.id){
            List<JoinedRecruitmentCycleDepartment> joinedRecruitmentCycleDepartmentList = JoinedRecruitmentCycleDepartment.getAll()
            return [joinedRecruitmentCycleDepartmentService.getInstance(params) , joinedRecruitmentCycleDepartmentList]
        }else{
            notFound()
        }
    }

    def update = {
        JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment = joinedRecruitmentCycleDepartmentService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), joinedRecruitmentCycleDepartment?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), joinedRecruitmentCycleDepartment?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedRecruitmentCycleDepartment,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedRecruitmentCycleDepartment.hasErrors()) {
                respond joinedRecruitmentCycleDepartment, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = joinedRecruitmentCycleDepartmentService.delete(PCPUtils.convertParamsToDeleteBean(params),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (joinedRecruitmentCycleDepartmentService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

