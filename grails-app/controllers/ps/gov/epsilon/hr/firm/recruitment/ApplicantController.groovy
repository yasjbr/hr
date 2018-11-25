package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.core.personEducation.ManagePersonEducationService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.lookups.InspectionCategoryService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonEducationCommand
import java.time.ZonedDateTime
import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route Applicant requests between model and views.
 * @see ApplicantService
 * @see FormatService
 * */
class ApplicantController {

    ApplicantService applicantService
    FormatService formatService
    ManagePersonService managePersonService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    InspectionCategoryService inspectionCategoryService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(Applicant.getName(), EnumOperation.APPLICANT)
    }

    def show = {
        if (params.encodedId || params['applicant.encodedId']) {
            Applicant applicant = applicantService.getInstanceWithRemotingValues(params)
            if (applicant) {
                respond applicant
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * this action is used to create New Person person which is not found in core:
     */
    def createNewPerson = {
    }


    def create = {
        respond new Applicant(params),params
    }


    def filter = {
        PagedResultList pagedResultList = applicantService.search(params)
        render text: (applicantService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    //todo  send the params from screen and use regular filter
    def filterApplicant = {
        //to filter applicant according to status needed here INTERVIEWED and ACCEPTED to use it in the trainee list
        params.applicantCurrentStatus = new ApplicantStatusHistory(applicantStatus: EnumApplicantStatus.ACCEPTED, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now())
        PagedResultList pagedResultList = applicantService.search(params)
        render text: (applicantService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }


    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        Applicant applicant = applicantService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'applicant.entity', default: 'Applicant'), applicant?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'applicant.entity', default: 'Applicant'), applicant?.id])

        if (request.xhr) {
            render text: (formatService.buildResponse(applicant, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (applicant?.hasErrors()) {
                respond applicant, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * save new person in core
     */
    def saveNewPerson() {
        PersonCommand personCommand = new PersonCommand()
        bindData(personCommand,params)
        personCommand = managePersonService.saveNewPerson(personCommand, params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])

        if (request.xhr) {
            render text: (formatService.buildResponse(personCommand, successMessage, failMessage) as JSON), contentType: "application/json"
        }

    }

    def edit = {
        if (params.encodedId) {
            Applicant applicant = applicantService.getInstanceWithRemotingValues(params)
            Map map = applicantService.getApplicantStatus(applicant?.applicantCurrentStatus?.applicantStatus)
            map.put("applicant", applicant)
            respond map
        } else {
            notFound()
        }
    }

    def update = {
        Applicant applicant = applicantService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'applicant.entity', default: 'Applicant'), applicant?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'applicant.entity', default: 'Applicant'), applicant?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(applicant, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (applicant.hasErrors()) {
                respond applicant, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = applicantService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'applicant.entity', default: 'applicant'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'applicant.entity', default: 'applicant'), params?.id, deleteBean.responseMessage ?: ""])
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

    def autocomplete = { render text: (applicantService.autoComplete(params)), contentType: "application/json" }

    // this action used to create new applicant
    def createNewApplicant = {
        if (params.personId) {
            params.createNewApplicant = true
            Map map = applicantService.getPreCreateInstance(params)
            if (map?.applicant?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(map?.applicant)?.message)
                redirect(action: "create",params: map)
            } else if(map?.anyApplicantExist && !params.boolean("createAnotherApplicant")){
                redirect(action: "create",params: map)
            }else {
                respond map
            }
        } else {
            notFound()
        }


    }

    /**
     * this action is used to return person info from core to be used in create new applicant
     */
    def selectPerson = {
        if (params["personId"]) {
            render text: ([success: true, personId: params["personId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'applicant.person.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action to return  inspection categories for applicant
     */
    def getInspectionCategory = {
        if (params.inspectionCategoryId || params.encodedId) {
            render "${g.renderInspection(inspectionCategoryId: params.inspectionCategoryId, applicantInspectionCategoryResultId: params.encodedId)}"
        } else {
            render ""
        }
    }

    /**
     * this action to return  inspection categories for applicant by vacancy/firm
     */
    def getInspectionCategoryByApplicant = {
        Applicant applicant
        if (params["applicant.id"]) {
            applicant = applicantService.getInstance(params)
            //to get inspection category by vacancy
            if (applicant?.vacancy) {
                params << ["ids[]": applicant?.vacancy?.inspectionCategories?.id?.toList()]
            } else {
                //to get inspection category by firm
                params["firm.id"] = applicant?.firm?.id
                params["allInspectionCategory"] = true
            }
            render inspectionCategoryService.autoComplete(params)
        }
    }

    /**
     * this action to return the interview which the applicant attended:
     */
    def getInterview = {
        if (params["applicant.encodedId"]) {
            Map map = [:]
            Applicant applicant = applicantService.getInstance(params)
            map.put("tabEntityName", "applicant")
            map.put("interview", applicant?.interview)
            map.put("errorType", "success")
            map.put("template", "/interview/show_withOutButtons")
            respond map
        } else {
            String failMessage = message(code: 'applicant.person.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }

    }

    /**
     * this action is used to return link of list
     */
    def goToListTrainee = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "trainee", Applicant, true))
        } else {
            notFound()
        }
    }

    /**
     * this action is used to return link of list
     */
    def goToListRecruitment = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "recruitment", Applicant, true))
        } else {
            notFound()
        }
    }

    /**
     * render vacancies modal
     */
    def getVacancies = {

    }

    /**
     * to render previous disciplinary judgments .
     */
    def personApplicantProfilesModal = {
        String personId = params["personId"]
        boolean anyOpenApplicantExist = params.boolean("anyOpenApplicantExist")
        Map map = [personId: personId,anyOpenApplicantExist: anyOpenApplicantExist]
        respond map
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'applicant.entity', default: 'Applicant'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

