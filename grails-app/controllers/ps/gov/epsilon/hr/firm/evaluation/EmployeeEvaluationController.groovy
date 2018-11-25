package ps.gov.epsilon.hr.firm.evaluation

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EmployeeEvaluation requests between model and views.
 *@see EmployeeEvaluationService
 *@see FormatService
**/
class EmployeeEvaluationController  {

    EmployeeEvaluationService employeeEvaluationService
    FormatService formatService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(EmployeeEvaluation.getName(), EnumOperation.EMPLOYEE_EVALUATION)
    }

    def show = {
        if (params.requestEncodedId) {
            //this param was passed from showing request inside list
            params.encodedId = params.requestEncodedId
        }
        if (params.encodedId) {
            EmployeeEvaluation employeeEvaluation = employeeEvaluationService.getInstanceWithRemotingValues(params)
            if (employeeEvaluation) {
                respond employeeEvaluation
                return
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
     * this action on autocomplete select event to return the employee id
     */
    def selectEmployee = {
        if (params["employeeId"]) {
            render text: ([success: true, employeeId: params["employeeId"], evaluationTemplateId: params["evaluationTemplate.id"]] as JSON), contentType: "application/json"
        } else {
            String failMessage = message(code: 'employee.notFound.error.label', args: null, default: "")
            render text: ([success: false, message: msg.error(label: failMessage)] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action used to create new employeeEvaluation for employee
     */
    def createNewEmployeeEvaluation = {
        if (params["employeeId"]) {
            EmployeeEvaluation employeeEvaluation = employeeEvaluationService.getPreCreateInstance(params)
            if (employeeEvaluation?.hasErrors()) {
                flash.message = msg.errorList(data: formatService.formatAllErrors(employeeEvaluation)?.message)
                redirect(action: "create")
            } else {
                respond employeeEvaluation
            }
        } else {
            notFound()
        }
    }



    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = employeeEvaluationService.searchWithRemotingValues(params)
        render text: (employeeEvaluationService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        EmployeeEvaluation employeeEvaluation = employeeEvaluationService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), employeeEvaluation?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), employeeEvaluation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeEvaluation, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (employeeEvaluation?.hasErrors()) {
                respond employeeEvaluation, view:'create'
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
            EmployeeEvaluation employeeEvaluation = employeeEvaluationService.getInstanceWithRemotingValues(params)
            if (employeeEvaluation && (employeeEvaluation?.requestStatus == EnumRequestStatus.CREATED || employeeEvaluation?.requestStatus == EnumRequestStatus.IN_PROGRESS)) {
                respond employeeEvaluation
                return
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        EmployeeEvaluation employeeEvaluation = employeeEvaluationService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), employeeEvaluation?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), employeeEvaluation?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(employeeEvaluation,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (employeeEvaluation.hasErrors()) {
                respond employeeEvaluation, view:'edit'
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
        DeleteBean deleteBean = employeeEvaluationService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (employeeEvaluationService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

