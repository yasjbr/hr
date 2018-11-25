package ps.gov.epsilon.hr.firm.absence

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route Absence requests between model and views.
 * @see AbsenceService
 * @see FormatService
 * */
class AbsenceController {

    AbsenceService absenceService
    FormatService formatService
    EmployeeService employeeService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list = {
        //Add the absence attachment type
        respond sharedService.getAttachmentTypeListAsMap(Absence.getName(), EnumOperation.ABSENCE)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if(params["absenceEncodedId"]){
            params.encodedId = params["absenceEncodedId"]
        }
        if (params.encodedId) {
            Absence absence = absenceService.getInstanceWithRemotingValues(params)
            if (absence) {
                Map respondData = [absence: absence]
                respond respondData
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = absenceService.searchWithRemotingValues(params)
        render text: (absenceService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filterAbsenceForList = {
        PagedList pagedList = absenceService.customListSearch(params)
        render text: (absenceService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    def save = {
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        Absence absence = absenceService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'absence.entity', default: 'Absence'), absence?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'absence.entity', default: 'Absence'), absence?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(absence, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (absence?.hasErrors()) {
                respond absence, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            Absence absence = absenceService.getInstanceWithRemotingValues(params)
            if (absence.violationStatus == EnumViolationStatus.NEW) {
                respond absence
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        params["firm.id"] = session.getAttribute("firmId") ?: 1L
        Absence absence = absenceService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'absence.entity', default: 'Absence'), absence?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'absence.entity', default: 'Absence'), absence?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(absence, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (absence.hasErrors()) {
                respond absence, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = absenceService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'absence.entity', default: 'Absence'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'absence.entity', default: 'Absence'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (absenceService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action on autocomplete select event to return the employee id
     */
    def selectEmployee = {
        if (params["employeeId"]) {
            render text: ([success: true, employeeId: params["employeeId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new absence for employee
     */
    def createNewAbsence = {
        if (params["employeeId"]) {
            Absence absence = absenceService.getPreCreateInstance(params)
            if (absence?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(absence)?.message)
                redirect(action: "create")
            } else {
                respond absence
            }
        } else {
            notFound()
        }
    }

    /**
     * to render previous Absence.
     */
    def previousAbsenceModal = {
        String employeeId = (HashHelper.decode(params.employeeEncodedId))
        String absenceId
        if (params.absenceEncodedId){
            absenceId = (HashHelper.decode(params.absenceEncodedId))
        }
        Map map = [employeeId: employeeId, absenceId: absenceId]
        respond map
    }

    /**
     * used in create page to render the belong employee, when select absence before select employee.
     * @return void
     */
    def getInstance = {
        Absence absence = absenceService.getInstanceWithRemotingValues(params)
        String employeeName = absence?.employee?.transientData?.personDTO?.localFullName
        String employeeId = absence?.employee?.id
        render text: ([employeeName: employeeName, employeeId: employeeId] as JSON), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'absence.entity', default: 'Absence'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

