package ps.gov.epsilon.hr.firm.profile

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import guiplugin.FormatService
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import static org.springframework.http.HttpStatus.NOT_FOUND


/**
 * <h1>Purpose</h1>
 * Route employee requests between model and views.
 * @see EmployeeService
 * @see EmployeeService
 * */
class EmployeeController {

    FormatService formatService
    EmployeeService employeeService
    ManagePersonService managePersonService
    SharedService sharedService


    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(Employee.getName(), EnumOperation.EMPLOYEE)
    }

    def create = {
        respond new Employee(params)
    }

    def filter = {
        PagedResultList pagedResultList = employeeService.searchWithRemotingValues(params)
        render text: (employeeService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }


    def filterIds = {
        List ids = employeeService.searchIds(params)
        render text: (ids as JSON), contentType: "application/json"
    }

    def filterEmployeeForModal = {
        PagedList pagedList = employeeService.customSearch(params)
        render text: (employeeService.resultListToMap(pagedList, params, employeeService.DOMAIN_COLUMNS_MODAL) as JSON), contentType: "application/json"
    }

    def show = {
        if (params.encodedId || params.emlpoyeeEncodedId) {
            Employee employee = null
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value)) {
                employee = employeeService.readInstanceWithRemotingValues(params)
            } else {
                employee = employeeService.getInstanceWithRemotingValues(params)
            }
            if (employee) {
                Map data = [employee: employee]
                data.put("imageData", (sharedService.getImageData(employee?.id, "Personal Picture", request)))
                respond data
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def edit = {
        if (params.encodedId) {
            Employee employee = employeeService.getInstanceWithRemotingValues(params)
            if (employee) {
                respond employee
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def save = {
        Employee employee = employeeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employee.entity', default: 'Employee'), employee?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employee.entity', default: 'Employee'), employee?.id])

        if (request.xhr) {
            render text: (formatService.buildResponse(employee, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (employee?.hasErrors()) {
                respond employee, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def update = {
        Employee employee = employeeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employee.entity', default: 'Employee'), employee?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employee.entity', default: 'Employee'), employee?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employee, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (employee.hasErrors()) {
                respond employee, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

//    def delete = {
//        DeleteBean deleteBean = employeeService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
//        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employee.entity', default: 'Employee'), params?.id])
//        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employee.entity', default: 'Employee'), params?.id, deleteBean.responseMessage ?: ""])
//        if (request.xhr) {
//            def json = [:]
//            json.success = deleteBean.status
//            json.message = deleteBean.status ? msg.success(label: successMessage) : msg.error(label: failMessage)
//            render text: (json as JSON), contentType: "application/json"
//        } else {
//            if (deleteBean.status) {
//                flash.message = msg.success(label: successMessage)
//            } else {
//                flash.message = msg.error(label: failMessage)
//            }
//            redirect(action: "list")
//        }
//    }

    def getEmployeeInfo = {
        render text: (employeeService.getEmployeeInfo(params) as JSON), contentType: "application/json"
    }

    def autocomplete = {
        render text: (employeeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * this action is used to return person info from core to be used in create applicant
     */
    def getPerson = {
        if (params.long("personId") && employeeService.count(params) == 0) {
            Map renderedMap = [:]
            renderedMap.put("success", true)
            renderedMap.put("personId", params.long("personId"))
            if (params["firm.id"]) {
                renderedMap.put("firmEncodedId", HashHelper.encode(params["firm.id"]))
            }
            render text: (renderedMap as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.person.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new employee
     */
    def createNewEmployee = {
        if (params.long("personId")) {
            params.isNewEmployee = "true"
            Employee employee = employeeService.getInstanceWithRemotingValues(params)
            if (employee) {
                respond employee
            } else {
                notFound()
            }

        } else {
            notFound()
        }
    }

    /**
     * this action is used to create new person which is not found in core:
     */
    def createNewPerson = {
    }

    /**
     * save new person in core
     */
    def saveNewPerson = {
        PersonCommand personCommand = new PersonCommand()
        bindData(personCommand, params)
        personCommand = managePersonService.saveNewPerson(personCommand, params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'person.entity', default: 'Person'), personCommand?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(personCommand, successMessage, failMessage) as JSON), contentType: "application/json"
        }
    }

    /**
     * Modal to lock employee profile
     */
    def changeEmployeeProfileStatusModal = {
        if (params.encodedId) {
            params.withRemotingValues = false
            Employee employee = employeeService.getInstance(params)
            if (employee) {
                EnumProfileStatus profileStatus = EnumProfileStatus.valueOf(params.newProfileStatus)
                String titleCode = profileStatus == EnumProfileStatus.LOCKED ? 'employee.lockProfile.label' : 'employee.unlockProfile.label'
                return [employeeId: params.encodedId, profileStatus: profileStatus, titleCode: titleCode]
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * Saves the status of employee profile
     */
    def saveEmployeeProfileStatus = {
        Employee employee = employeeService.saveChangeProfileStatus(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employee.entity', default: 'Employee'), employee?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employee.entity', default: 'Employee'), employee?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employee, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (employee.hasErrors()) {
                respond employee, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = msg.error(label: message(code: 'default.not.found.message', args: [message(code: 'employee.entity', default: 'Employee'), params?.id]))
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
