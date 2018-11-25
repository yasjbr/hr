package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgmentService
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReasonService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.*
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route DisciplinaryRequest requests between model and views.
 * @see DisciplinaryRequestService
 * @see FormatService
 * */
class DisciplinaryRequestController {

    DisciplinaryRequestService disciplinaryRequestService
    FormatService formatService
    EmployeeService employeeService
    DisciplinaryReasonService disciplinaryReasonService
    DisciplinaryJudgmentService disciplinaryJudgmentService
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
        Map respondData = sharedService.getAttachmentTypeListAsMap(DisciplinaryRequest.getName(), EnumOperation.DISCIPLINARY)
        respondData.employeeId = params["employeeId"]
        respond respondData
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params["disciplinaryEncodedId"]) {
            params.encodedId = params["disciplinaryEncodedId"]
        }
        if (params.encodedId) {
            DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(params)
            if (disciplinaryRequest) {
                respond disciplinaryRequest
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
    def createNewDisciplinaryRequest = {
        if (params["employeeId"]) {
            GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)
            DisciplinaryRequest disciplinaryRequest = new DisciplinaryRequest(params)
            disciplinaryRequest.employee = employee

            Map map = [:]

            /**
             * in case: if the user has HR role, get the suitable workflow
             */
            if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        disciplinaryRequest?.employee?.id,
                        disciplinaryRequest.employee?.currentEmploymentRecord?.department?.id,
                        disciplinaryRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                        disciplinaryRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                        DisciplinaryRequest.getName(),
                        disciplinaryRequest?.id,
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
            map.disciplinaryRequest = disciplinaryRequest

            /**
             * respond map
             */
            respond(map)

        } else {
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = disciplinaryRequestService.searchWithRemotingValues(params)
        render text: (disciplinaryRequestService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), disciplinaryRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), disciplinaryRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryRequest, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (disciplinaryRequest?.hasErrors()) {
                respond disciplinaryRequest, view: 'create'
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
            params['requestStatus'] = EnumRequestStatus.CREATED.toString()
            DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(params)
            if (disciplinaryRequest) {
                List disciplinaryReasons = disciplinaryReasonService.search(new GrailsParameterMap(['disciplinaryCategory.id': disciplinaryRequest?.disciplinaryCategory?.id], request))
                List disciplinaryReasonIds = disciplinaryRequest?.disciplinaryJudgments?.first()?.disciplinaryReasons?.id?.toList()
                List disciplinaryJudgments = disciplinaryJudgmentService.getJoinedReasonJudgments(disciplinaryReasonIds)
                Map data = [disciplinaryRequest: disciplinaryRequest, disciplinaryReasons: disciplinaryReasons, disciplinaryJudgments: disciplinaryJudgments]
                respond data
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
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), disciplinaryRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), disciplinaryRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryRequest, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (disciplinaryRequest.hasErrors()) {
                respond disciplinaryRequest, view: 'edit'
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
        DeleteBean deleteBean = disciplinaryRequestService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (disciplinaryRequestService.autoComplete(params)), contentType: "application/json"
    }


    def getViolationsWithJudgments = {
        String employeeId = params["employeeId"]
        String disciplinaryRequestId = params["disciplinaryRequestId"]
        String disciplinaryCategoryId = params["disciplinaryCategoryId"]
        List employeeViolationIds = params.listString("employeeViolationIds[]")
        String violationCount = params["violationCount"]
        render disciplinary.getViolationsWithJudgments([disciplinaryRequestId : disciplinaryRequestId,
                                                        employeeId            : employeeId,
                                                        disciplinaryCategoryId: disciplinaryCategoryId,
                                                        employeeViolationIds  : employeeViolationIds,
                                                        violationCount        : violationCount
        ])
    }

    /**
     * to render add new disciplinary judgments .
     */
    def getDisciplinaryJudgmentsInputs = {
        String disciplinaryJudgmentId = params["disciplinaryJudgmentId"]
        String disciplinaryRequestId = params["disciplinaryRequestId"]
        String violationCount = params["violationCount"]
        render disciplinary.getDisciplinaryJudgmentsInputs([disciplinaryJudgmentId: disciplinaryJudgmentId,
                                                            disciplinaryRequestId : disciplinaryRequestId,
                                                            violationCount        : violationCount])
    }

    /**
     * to render previous disciplinary judgments .
     */
    def previousJudgmentsModal = {
        String employeeId = params["id"]
        Map map = [employeeId: employeeId]
        respond map
    }

    /**
     * to show details of disciplinary judgments .
     */
    def showDetails = {
        if (params.disciplinaryRequestEncodedId) {
            params.encodedId = params.disciplinaryRequestEncodedId
        }
        if (params.encodedId) {
            DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(params)
            if (disciplinaryRequest) {
                Map map = [disciplinaryRequest: disciplinaryRequest]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    def getInstance = {
        DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(params)
        String employeeName = disciplinaryRequest?.employee?.transientData?.personDTO?.localFullName
        String employeeId = disciplinaryRequest?.employee?.id
        render text: ([employeeName: employeeName, employeeId: employeeId] as JSON), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

