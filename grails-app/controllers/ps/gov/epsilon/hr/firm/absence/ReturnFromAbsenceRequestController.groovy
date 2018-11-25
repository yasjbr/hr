package ps.gov.epsilon.hr.firm.absence

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.child.ChildRequest
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ReturnFromAbsenceRequest requests between model and views.
 *@see ReturnFromAbsenceRequestService
 *@see FormatService
**/
class ReturnFromAbsenceRequestController  {

    ReturnFromAbsenceRequestService returnFromAbsenceRequestService
    FormatService formatService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService

    static allowedMethods = [save: "POST", update: "POST"]


    /**
     * default action in controller
     */
    def index= {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list= {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(ReturnFromAbsenceRequest.getName(), EnumOperation.RETURN_FROM_ABSENCE_REQUEST)
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if (params.requestEncodedId) {
            //this param was passed from showing request inside list
            params.encodedId = params.requestEncodedId
        }
        if(params.encodedId){
            ReturnFromAbsenceRequest returnFromAbsenceRequest = returnFromAbsenceRequestService.getInstanceWithRemotingValues(params)
            if(returnFromAbsenceRequest){
                respond returnFromAbsenceRequest
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the show page with get instance
     */
    def showRelatedRequest= {
        if(params.absenceId){
            params["absence.id"] = params.remove("absenceId")
            ReturnFromAbsenceRequest returnFromAbsenceRequest = returnFromAbsenceRequestService.searchWithRemotingValues(params).resultList[0]
            if(returnFromAbsenceRequest){
                respond returnFromAbsenceRequest
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new ReturnFromAbsenceRequest(params)
    }


    /**
     * this action on autocomplete select event to return the absence id
     */
    def selectAbsence = {
        if (params["absenceId"]) {
            render text: ([success: true, absenceId: params["absenceId"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'absence.notFound.error.label', args: null, default: "notFound")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new absence for employee
     */
    def createNewRequest = {
        Map map=[:]
        if (params["absenceId"] && params["absenceId"] != "null") {
            ReturnFromAbsenceRequest returnFromAbsenceRequest = returnFromAbsenceRequestService.getPreCreateInstance(params)
            if (returnFromAbsenceRequest?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(returnFromAbsenceRequest)?.message)
                redirect(action: "create")
            } else {
                /**
                 * in case: if the user has HR role, get the suitable workflow
                 */
                if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {

                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            returnFromAbsenceRequest?.employee?.id,
                            returnFromAbsenceRequest?.employee?.currentEmploymentRecord?.department?.id,
                            returnFromAbsenceRequest?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id,
                            returnFromAbsenceRequest?.employee?.currentEmploymentRecord?.jobTitle?.id,
                            ReturnFromAbsenceRequest.getName(),
                            returnFromAbsenceRequest?.id,
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
                map.returnFromAbsenceRequest = returnFromAbsenceRequest

                /**
                 * respond map
                 */
                respond(map)
            }
        } else {
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = returnFromAbsenceRequestService.searchWithRemotingValues(params)
        render text: (returnFromAbsenceRequestService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ReturnFromAbsenceRequest returnFromAbsenceRequest = returnFromAbsenceRequestService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), returnFromAbsenceRequest?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), returnFromAbsenceRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(returnFromAbsenceRequest, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (returnFromAbsenceRequest?.hasErrors()) {
                respond returnFromAbsenceRequest, view:'create'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        String failMessage = message(code: 'request.fail.edit.message', args: [])
        if (params.encodedId) {
            ReturnFromAbsenceRequest returnFromAbsenceRequest = returnFromAbsenceRequestService.getInstanceWithRemotingValues(params)
            if (returnFromAbsenceRequest && (returnFromAbsenceRequest?.requestStatus == EnumRequestStatus.CREATED || returnFromAbsenceRequest?.requestStatus == EnumRequestStatus.IN_PROGRESS)) {
                respond returnFromAbsenceRequest
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        ReturnFromAbsenceRequest returnFromAbsenceRequest = returnFromAbsenceRequestService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), returnFromAbsenceRequest?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), returnFromAbsenceRequest?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(returnFromAbsenceRequest,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (returnFromAbsenceRequest.hasErrors()) {
                respond returnFromAbsenceRequest, view:'edit'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = returnFromAbsenceRequestService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (returnFromAbsenceRequestService.autoComplete(params)), contentType: "application/json"
    }


    /** this action is used to return link of list
     */
    def goToList = {
        if (params["encodedId"]) {
            redirect(sharedService.goToList(params["encodedId"]?.toString(), "returnFromAbsence", ReturnFromAbsenceListEmployee, false, "returnFromAbsenceRequest", false))
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

