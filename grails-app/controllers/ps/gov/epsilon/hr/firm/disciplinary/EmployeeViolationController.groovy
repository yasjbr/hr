package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route EmployeeViolation requests between model and views.
 * @see EmployeeViolationService
 * @see FormatService
 * */
class EmployeeViolationController {

    EmployeeViolationService employeeViolationService
    FormatService formatService
    EmployeeService employeeService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * default action in controller
     */
    def index = {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list = {
        respond sharedService.getAttachmentTypeListAsMap(EmployeeViolation.getName(), EnumOperation.EMPLOYEE_VIOLATION)
    }

    def listModal = {
        if (params["employeeId"]) {
            return [employeeId: params["employeeId"], disciplinaryCategoryId: params["disciplinaryCategoryId"], excludedId: params.list("excludedIds")]
        }
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.employeeViolationEncodedId) {
            params.encodedId = params.remove("employeeViolationEncodedId")
        }
        if (params.encodedId) {
            EmployeeViolation employeeViolation = employeeViolationService.getInstanceWithRemotingValues(params)
            if (employeeViolation) {
                Map respondData = [employeeViolation: employeeViolation]
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
     * this action used to select employee
     */
    def selectEmployee = {
        if (params["employeeId"]) {
            render text: ([success: true, employeeId: params["employeeId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label')
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new Disciplinary Request
     */
    def createNewEmployeeViolation = {
        if (params["employeeId"]) {
            GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)
            EmployeeViolation employeeViolation = new EmployeeViolation(params)
            employeeViolation.employee = employee
            respond employeeViolation
        } else {
            notFound()
        }
    }

    /**
     * this action used to create new Disciplinary Request from Modal
     */
    def createNewEmployeeViolationModal = {
        if (params["employeeId"]) {
            GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)
            EmployeeViolation employeeViolation = new EmployeeViolation(params)
            employeeViolation.employee = employee
            respond employeeViolation
        } else {
            notFound()
        }
    }

    def filterViolationForList = {
        params["violationStatusList"] = [EnumViolationStatus.NEW, EnumViolationStatus.PUNISHED]
        this.filter(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = employeeViolationService.searchWithRemotingValues(params)
        render text: (employeeViolationService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EmployeeViolation employeeViolation = employeeViolationService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), employeeViolation?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), employeeViolation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeViolation, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (employeeViolation?.hasErrors()) {
                respond employeeViolation, view: 'create'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if (params.encodedId) {
            EmployeeViolation employeeViolation = employeeViolationService.getInstanceWithRemotingValues(params)
            if (employeeViolation) {
                Map respondData = [employeeViolation: employeeViolation]
                respond respondData
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        EmployeeViolation employeeViolation = employeeViolationService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), employeeViolation?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), employeeViolation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeViolation, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (employeeViolation.hasErrors()) {
                respond employeeViolation, view: 'edit'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = employeeViolationService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), params?.id, deleteBean.responseMessage ?: ""])
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

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (employeeViolationService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "violation", ViolationListEmployee, false, "employeeViolation", false))
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeViolation.entity', default: 'EmployeeViolation'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

