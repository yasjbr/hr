package ps.gov.epsilon.hr.firm.request

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route BordersSecurityCoordination requests between model and views.
 * @see BordersSecurityCoordinationService
 * @see FormatService
 * */
class BordersSecurityCoordinationController {

    BordersSecurityCoordinationService bordersSecurityCoordinationService
    FormatService formatService
    EmployeeService employeeService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

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
        respond sharedService.getAttachmentTypeListAsMap(BordersSecurityCoordination.getName(), EnumOperation.BORDERS_SECURITY_COORDINATION)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            BordersSecurityCoordination bordersSecurityCoordination = bordersSecurityCoordinationService.getInstanceWithRemotingValues(params)
            if (bordersSecurityCoordination) {
                respond bordersSecurityCoordination
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
        respond new BordersSecurityCoordination(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = bordersSecurityCoordinationService.searchWithRemotingValues(params)
        render text: (bordersSecurityCoordinationService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        BordersSecurityCoordination bordersSecurityCoordination = bordersSecurityCoordinationService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), bordersSecurityCoordination?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), bordersSecurityCoordination?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(bordersSecurityCoordination, successMessage, failMessage, true, getControllerName(), "show") as JSON), contentType: "application/json"
        } else {
            if (bordersSecurityCoordination?.hasErrors()) {
                respond bordersSecurityCoordination, view: 'create'
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
            BordersSecurityCoordination bordersSecurityCoordination = bordersSecurityCoordinationService.getInstanceWithRemotingValues(params)
            if (bordersSecurityCoordination) {
                respond bordersSecurityCoordination
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
        BordersSecurityCoordination bordersSecurityCoordination = bordersSecurityCoordinationService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), bordersSecurityCoordination?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), bordersSecurityCoordination?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(bordersSecurityCoordination, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (bordersSecurityCoordination.hasErrors()) {
                respond bordersSecurityCoordination, view: 'edit'
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
        DeleteBean deleteBean = bordersSecurityCoordinationService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), params?.id, '' ?: ""])
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
        render text: (bordersSecurityCoordinationService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * this action is used to return employee id who is selected
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
     * this action used to create new border security coordination for  selected employee
     */
    def createNewBordersSecurityCoordination = {

        Map map = [:]

        if (params["employeeId"]) {

            /*
            * create new vacation request
            */
            BordersSecurityCoordination bordersSecurityCoordination = new BordersSecurityCoordination(params)

            /**
             * assign employee object to borders security coordination
             */
            GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService?.getInstanceWithRemotingValues(employeeParam)
            bordersSecurityCoordination.employee = employee

            /**
             * in case: if the user has HR role, get the suitable workflow
             */
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        bordersSecurityCoordination?.employee?.id,
                        bordersSecurityCoordination.employee?.currentEmploymentRecord?.department?.id,
                        bordersSecurityCoordination?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                        bordersSecurityCoordination?.employee?.currentEmploymentRecord?.jobTitle?.id,
                        BordersSecurityCoordination.getName(),
                        bordersSecurityCoordination?.id,
                        false)

                /**
                 * sort workflow path details
                 */
                workflowPathHeader?.workflowPathDetails = workflowPathHeader?.workflowPathDetails?.sort { a, b -> b.sequence <=> a.sequence }

                /**
                 * add workflowPathHeader to map
                 */
                map.workflowPathHeader = workflowPathHeader
            }
            /**
             * add request to map
             */
            map.bordersSecurityCoordination = bordersSecurityCoordination

            /**
             * respond map
             */
            respond(map)
        } else {
            notFound()
        }
    }
}

