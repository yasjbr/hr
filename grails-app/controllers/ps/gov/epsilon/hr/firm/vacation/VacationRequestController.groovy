package ps.gov.epsilon.hr.firm.vacation

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.BordersSecurityCoordination
import ps.gov.epsilon.hr.firm.request.BordersSecurityCoordinationService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.PagedList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route vacationRequest requests between model and views.
 * @see VacationRequestService
 * @see FormatService
 * */
class VacationRequestController {

    VacationRequestService vacationRequestService
    FormatService formatService
    SharedService sharedService
    BordersSecurityCoordinationService bordersSecurityCoordinationService
    VacationListEmployeeService vacationListEmployeeService
    WorkFlowProcessService workFlowProcessService
    EmployeeService employeeService

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
        respond sharedService.getAttachmentTypeListAsMap(VacationRequest.getName(), EnumOperation.VACATION_REQUEST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        def map = [:]
        PagedResultList pagedResultList
        if (params.encodedId) {
            VacationRequest vacationRequest = vacationRequestService.getInstanceWithRemotingValues(params)
            if (vacationRequest) {
                /**
                 * get vacation list employee by vacation request
                 */
                GrailsParameterMap vacationListEmployeeParam = new GrailsParameterMap(['vacationRequest.id': vacationRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                pagedResultList = vacationListEmployeeService?.search(vacationListEmployeeParam)

                if (pagedResultList?.resultList?.size() > 0) {
                    map = [vacationRequest: vacationRequest, vacationListEmployee: pagedResultList?.resultList?.get(0)]
                } else {
                    map = [vacationRequest: vacationRequest]
                }
                respond map
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new VacationRequest(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = vacationRequestService.searchWithRemotingValues(params)
        render text: (vacationRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
//        println 'FROM SAVE'
        params["firm.id"] = session.getAttribute("firmId")
        VacationRequest vacationRequest = vacationRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), vacationRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), vacationRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationRequest, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (vacationRequest?.hasErrors()) {
                vacationRequest.errors?.each {
                    println it
                }
                respond vacationRequest, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * get parameters from page and save instance
     */
    def saveAll = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        VacationRequest vacationRequest = vacationRequestService.saveAll(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), vacationRequest?.id])
        String failMessage = message(code: 'vacationRequest.error.create.vacation.for.list.of.employee.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (vacationRequest?.hasErrors()) {
                respond vacationRequest, view: 'create'
                return
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
            VacationRequest vacationRequest = vacationRequestService.getInstanceWithRemotingValues(params)
            String failMessage = message(code: 'vacationRequest.failEdit.label', args: [])

            /**
             * to prevent edit vacation request when request status in [EnumRequestStatus.APPROVED, EnumRequestStatus.REJECTED, EnumRequestStatus.FINISHED]
             */
            if (vacationRequest && vacationRequest?.requestStatus != EnumRequestStatus.CREATED) {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            } else {
                respond vacationRequest
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        VacationRequest vacationRequest = vacationRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), vacationRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), vacationRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacationRequest.hasErrors()) {
                respond vacationRequest, view: 'edit'
                return
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
        DeleteBean deleteBean = vacationRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), "", ""])
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
        render text: (vacationRequestService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * this action is used to return  vacation request for selected employee
     */
    def selectEmployee = {
        if (params["employee.id"] && params["vacationType.id"]) {
            VacationRequest vacationRequest = vacationRequestService.selectEmployee(params)
            String successMessage = ''
            String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacationRequest.entity', default: 'vacationRequest'), vacationRequest?.id])
            render text: (formatService.buildResponse(vacationRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            notFound()
        }
    }

    /**
     * this action used to create new vacation request for the selected employee
     */
    def createNewVacationRequest = {

//        println 'createNewVacationRequest....'

        if (params["employee.id"] && params["vacationType.id"]) {
            Map map = [:]
            GrailsParameterMap employeeParam = new GrailsParameterMap(['id': params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            VacationRequest vacationRequest = vacationRequestService.getVacationRequest(params)
            Employee employee = employeeService.getInstance(employeeParam)

            /**
             * in case: if the user has HR role, get the suitable workflow
             */
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id,
                        employee?.currentEmploymentRecord?.department?.id,
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                        employee?.currentEmploymentRecord?.jobTitle?.id,
                        VacationRequest.getName(),
                        vacationRequest?.id,
                        false)

                /**
                 * sort workflow path details
                 */
                map.workflowPathHeader = workflowPathHeader
            }

            /**
             * add vacation request to map
             */
            map.vacationRequest = vacationRequest

            /**
             * respond map
             */
            respond(map)
        } else {
            notFound()
        }

    }
/**
 * this action used to create new list vacation request for list of employee
 */
    def createNewListVacationRequest = {
        Map map = [:]

        VacationRequest vacationRequest = new VacationRequest()

        /**
         * in case: if the user has HR role, get the suitable workflow
         */
        if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

            WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                    null,
                    null,
                    null,
                    null,
                    VacationRequest.getName(),
                    vacationRequest?.id,
                    false)

            /**
             * sort workflow path details
             */
            map.workflowPathHeader = workflowPathHeader
        }

        /**
         * add vacation request to map
         */
        map.vacationRequest = vacationRequest

        /**
         * respond map
         */
        respond(map)
    }

/**
 * return employee id
 */
    def selectedEmployeeBorders = {
        return ["id": params["id"]]
    }

/**
 * return bordersSecurityCoordination
 */
    def getBordersSecurityCoordination = {
        if (params.id) {
            BordersSecurityCoordination bordersSecurityCoordination = bordersSecurityCoordinationService.getInstance(params)
            if (bordersSecurityCoordination) {
                render text: (bordersSecurityCoordination as JSON), contentType: "application/json"
            }
        }
    }

/**
 * this action is used to return link of list
 */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "vacation", VacationListEmployee))
        } else {
            notFound()
        }
    }

    /**
     * represent the show page with get instance
     */
    def showThread = {
        if (params.threadId) {
            PagedResultList<VacationRequest> vacationRequestPagedResultList = vacationRequestService.getThreadWithRemotingValues(params)
            if (vacationRequestPagedResultList) {
                return [vacationRequestList: vacationRequestPagedResultList?.resultList]
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filterCanHaveOperation = {
        PagedList pagedResultList = vacationRequestService.searchCanHaveOperation(params)
        render text: (vacationRequestService.resultListToMap(pagedResultList, params, vacationRequestService.DOMAIN_COLUMNS) as JSON),
                contentType: "application/json"
    }
}

