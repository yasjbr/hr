package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route Interview requests between model and views.
 * @see InterviewService
 * @see FormatService
 * */
class InterviewController {

    InterviewService interviewService
    FormatService formatService
    ManageLocationService manageLocationService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            Interview interview = interviewService.getInstanceWithRemotingValues(params)
            if (interview) {
                respond interview
                return
            }
        }
        notFound()
    }

    def create = {
        Interview interview = new Interview(params)

        //to get default interview location
        LocationDTO locationDTO = interviewService.getDefaultInterviewLocation()
        if (locationDTO) {
            interview.transientData = [:]
            interview.transientData.put("locationDTO", locationDTO)
        }

        respond interview
    }

    def filter = {
        PagedResultList pagedResultList = interviewService.searchWithRemotingValues(params)
        render text: (interviewService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        Interview interview = interviewService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'interview.entity', default: 'Interview'), interview?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'interview.entity', default: 'Interview'), interview?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(interview, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (interview?.hasErrors()) {
                respond interview, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        String failMessage = message(code: 'interview.fail.edit.message', args: [])
        if (params.encodedId) {
            Interview interview = interviewService.getInstanceWithRemotingValues(params)
            if (interview && interview?.interviewStatus == EnumInterviewStatus.OPEN) {
                respond interview
                return
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    def update = {
        Interview interview = interviewService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'interview.entity', default: 'Interview'), interview?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'interview.entity', default: 'Interview'), interview?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(interview, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (interview.hasErrors()) {
                respond interview, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = interviewService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'interview.entity', default: 'Interview'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'interview.entity', default: 'Interview'), params?.id, ""])
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
        render text: (interviewService.autoComplete(params)), contentType: "application/json"
    }

    def changeInterviewStatus = {
        if (params.encodedId) {
            Boolean successClose = interviewService.changeInterviewStatus(params)
            if (request.xhr) {
                def json = [:]
                json.success = successClose
                json.message = successClose ? message(code: 'interview.successClose.label', default: "success close the interview") : message(code: 'interview.failClose.label', default: 'failed close the interview')
                render text: (json as JSON), contentType: "application/json"
            }
        }
    }


    def addApplicantToInterview = {
        Boolean successAdd = interviewService.addApplicantToInterview(params)
        if (request.xhr) {
            def json = [:]
            json.success = successAdd
            json.message = successAdd ? message(code: 'interview.successAdd.label', default: "success add") : message(code: 'interview.failAdd.label', default: 'failed add ')
            render text: (json as JSON), contentType: "application/json"
        }

    }

    def deleteApplicantFromInterview = {
        if (params.encodedId) {
            boolean successDelete = interviewService.deleteApplicantFromInterview(params)
            if (request.xhr) {
                def json = [:]
                json.success = successDelete
                json.message = successDelete ? message(code: 'interview.successDelete.label', default: 'success delete') : message(code: 'interview.failDelete.label', default: 'failed delete')
                render text: (json as JSON), contentType: "application/json"
            }
        }


    }

    /**
     * render applicants modal
     */
    def getApplicants = {
        if (params.id) {
            Interview interview = interviewService?.getInstance(params)
            respond interview
        } else {
            notFound()
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'interview.entity', default: 'Interview'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

