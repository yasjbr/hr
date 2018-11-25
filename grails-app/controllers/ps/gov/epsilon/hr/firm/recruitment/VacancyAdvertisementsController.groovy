package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route VacancyAdvertisements requests between model and views.
 * @see VacancyAdvertisementsService
 * @see FormatService
 * */
class VacancyAdvertisementsController {

    VacancyAdvertisementsService vacancyAdvertisementsService
    FormatService formatService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(VacancyAdvertisements.getName(), EnumOperation.VACANCY_ADVERTISEMENTS)

    }

    def show = {

        if (params.encodedId) {
            VacancyAdvertisements vacancyAdvertisements = vacancyAdvertisementsService.getInstance(params)
            respond vacancyAdvertisements
        } else {
            notFound()
        }
    }

    def create = {
        respond new VacancyAdvertisements(params)
    }

    def filter = {
        PagedResultList pagedResultList = vacancyAdvertisementsService.search(params)
        render text: (vacancyAdvertisementsService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        VacancyAdvertisements vacancyAdvertisements = vacancyAdvertisementsService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), vacancyAdvertisements?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle'), vacancyAdvertisements?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacancyAdvertisements, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (vacancyAdvertisements?.hasErrors()) {
                respond vacancyAdvertisements, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            respond vacancyAdvertisementsService.getInstance(params)
        } else {
            notFound()
        }
    }

    def update = {
        VacancyAdvertisements vacancyAdvertisements = vacancyAdvertisementsService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements'), vacancyAdvertisements?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements'), vacancyAdvertisements?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacancyAdvertisements, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacancyAdvertisements.hasErrors()) {
                respond vacancyAdvertisements, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = vacancyAdvertisementsService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (vacancyAdvertisementsService.autoComplete(params)), contentType: "application/json"
    }


    def addVacancyToVacancyAdvertisements = {
        Boolean successAdd = vacancyAdvertisementsService.addVacancyToVacancyAdvertisements(params)
        if (request.xhr) {
            def json = [:]
            json.success = successAdd
            json.message = successAdd ? message(code: 'vacancyAdvertisements.successAdd.label', default: "success add") : message(code: 'vacancyAdvertisements.failAdd.label', default: 'failed add ')
            render text: (json as JSON), contentType: "application/json"
        }
    }

    def deleteVacancyFromVacancyAdvertisements = {
        boolean successDelete = vacancyAdvertisementsService.deleteVacancyFromVacancyAdvertisements(params)
        if (request.xhr) {
            def json = [:]
            json.success = successDelete
            json.message = successDelete ? message(code: 'vacancyAdvertisements.successDelete.label', default: 'success delete') : message(code: 'vacancyAdvertisements.failDelete.label', default: 'failed delete')
            render text: (json as JSON), contentType: "application/json"
        }
    }

    /**
     * render vacancies modal
     */
    def getVacancies = {
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacancyAdvertisements.entity', default: 'VacancyAdvertisements'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

