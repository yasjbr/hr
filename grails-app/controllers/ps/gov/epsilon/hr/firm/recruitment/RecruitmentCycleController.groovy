package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import org.springframework.context.MessageSource
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route RecruitmentCycle requests between model and views.
 * @see RecruitmentCycleService
 * @see FormatService
 * */
class RecruitmentCycleController {

    MessageSource messageSource
    RecruitmentCycleService recruitmentCycleService
    FormatService formatService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    SharedService sharedService
    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(RecruitmentCycle.getName(), EnumOperation.RECRUITMENT_CYCLE)
    }

    def show = {
        if (params.encodedId) {
            RecruitmentCycle recruitmentCycle = recruitmentCycleService.getInstance(params)
            respond recruitmentCycle
        } else {
            notFound()
        }
    }

    def create = {
        respond new RecruitmentCycle(params)
    }

    def filter = {
        PagedResultList pagedResultList = recruitmentCycleService.search(params)
        render text: (recruitmentCycleService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        RecruitmentCycle recruitmentCycle = recruitmentCycleService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), recruitmentCycle?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), recruitmentCycle?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentCycle, successMessage, failMessage,true, getControllerName(),"show") as JSON), contentType: "application/json"
        } else {
            if (recruitmentCycle?.hasErrors()) {
                respond recruitmentCycle, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * edit action to update the recruitment cycle instance
     * the edit is allowed in case the phase is NEW/OPEN
     */
    def edit = {
        if (params.encodedId) {
            RecruitmentCycle recruitmentCycle = recruitmentCycleService.getInstance(params)

            if (RecruitmentCycle) {
                //check the current phase:
                if (recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
                    respond recruitmentCycle
                } else {
                    //return error message that cycle phase is not allowed to be edited
                    flash.message = msg.error(label: messageSource.getMessage('recruitmentCycle.errorEditMessage.label'))
                    redirect(action: "list")
                }
            }
        } else {
            notFound()
        }
    }

    /**
     * action to return the next phase map contains the cycle instance and the next phase name.
     */
    def changePhase = {
        if (params.encodedId) {
            Map map = recruitmentCycleService.getNextPhase(params)
            if (map.errorType == "notAllowed") {
                //first check if its allowed to change the phase, if not return error message
                flash.message = msg.error(label: message(code: 'recruitmentCycle.errorNotAllowedMessage.label'))
                redirect(action: "list")
            } else if (map.errorType == "success") {//return map of values
                respond map
            } else {//if the instance is not exist then return not found
                notFound()
            }
        } else {//if the instance is not exist then return not found
            notFound()
        }
    }

    def update = {
        RecruitmentCycle recruitmentCycle = recruitmentCycleService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), recruitmentCycle?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), recruitmentCycle?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentCycle, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentCycle.hasErrors()) {
                respond recruitmentCycle, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = recruitmentCycleService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), params?.id, deleteBean.responseMessage ?: ""])
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

    /*
     * return the recruitment cycle invited departments list:
     * the list will be returned if cycle phase:NEW/OPEN - other wise its not possible to add/delete departments
     */
    def manageDepartments = {
        if (params.encodedId) {
            Map map = recruitmentCycleService.manageDepartmentData(params)
            if (map.errorType == "notAllowed") {
                //first check if its allowed to add/delete departments, if not return error message
                String failMessage = message(code: 'recruitmentCycle.departments.notAllowed.manage.label', args: null, default: "")
                println failMessage
                render failMessage
            } else if (map.errorType == "success") {//return map of values
                render(template: "/recruitmentCycle/inLine/edit", model: map)
            } else {//if the instance is not exist then return not found
                notFound()
            }
        }
    }

    /*
     * save the joined departments in the recruitment cycle
     */
    def updateRecruitmentCycleDepartment = {
        RecruitmentCycle recruitmentCycle = recruitmentCycleService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle'), recruitmentCycle?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'recruitmentCycle.entity', default: 'recruitmentCycle'), recruitmentCycle?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentCycle, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentCycle?.hasErrors()) {
                respond recruitmentCycle, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def autocomplete = {
        render text: (recruitmentCycleService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action is used to include job requisitions with no recruitment cycle to the used recruitment cycle:
     */
    def addJobRequisition = {
        String successMessage = message(code: 'recruitmentCycle.addJobRequisition.message')
        String failMessage = message(code: 'recruitmentCycle.not.addJobRequisition.message')
        def json = [:]
        Boolean isAdded = recruitmentCycleService.addRecruitmentCycleToJobRequisition(params)
        if (isAdded) {
            json.success = true
        } else {
            json.success = false
        }
        json.message = json.success ? msg.success(label: successMessage) : msg.error(label: failMessage)
        render text: (json as JSON), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

