package ps.gov.epsilon.aoc.firm.employee

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.Employee

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EmployeeSalaryInfo requests between model and views.
 *@see EmployeeSalaryInfoService
 *@see FormatService
**/
class EmployeeSalaryInfoController  {

    EmployeeSalaryInfoService employeeSalaryInfoService
    FormatService formatService
    SharedService sharedService

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
        respond sharedService.getAttachmentTypeListAsMap(Employee.getName(), EnumOperation.EMPLOYEE)
    }


    /**
     * Import the employee financial data from excel file
     */
    def importFinancialData() {
        String successMessage = message(code: 'employee.updateFinancialData.message')
        String failMessage = message(code: 'employee.not.updateFinancialData.message')
        if(params["excelFile"]){
            def file = request.getFile("excelFile")
            Map dataMap = employeeSalaryInfoService?.importFinancialData(params, file)
            if (request.xhr) {
                Map json = [:]
                List<Map> errors = dataMap.errors
                json.success = !errors;
                def errorFormat = msg.errorList(data: (errors), isCustom: "true");
                json.message = json.success ? msg.success(label: successMessage) : errorFormat
                json.data = json.success ? dataMap.data : null
                json.errorList = !json.success ? errors : []
                render text: (json as JSON), contentType: "application/json"
            } else {
                notFound()
            }
        }else {
            flash.message = msg.error(label: failMessage)
        }
    }



    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            EmployeeSalaryInfo employeeSalaryInfo = employeeSalaryInfoService.getInstanceWithRemotingValues(params)
            if(employeeSalaryInfo){
                respond employeeSalaryInfo
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
//    def create = {
//        respond new EmployeeSalaryInfo(params)
//    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = employeeSalaryInfoService.searchWithRemotingValues(params)
        render text: (employeeSalaryInfoService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
//    def save = {
//        //todo: get the firm from params without need to use the session value in case the user is super admin
//        params["firm.id"] = session.getAttribute("firmId")
//        EmployeeSalaryInfo employeeSalaryInfo = employeeSalaryInfoService.save(params)
//        String successMessage = message(code: 'default.created.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), employeeSalaryInfo?.id])
//        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), employeeSalaryInfo?.id])
//        if (request.xhr) {
//            render text: (formatService.buildResponse(employeeSalaryInfo, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
//        }
//        else {
//            if (employeeSalaryInfo?.hasErrors()) {
//                respond employeeSalaryInfo, view:'create'
//            }else{
//                flash.message = msg.success(label:successMessage)
//                redirect(action: "list")
//            }
//        }
//    }

    /**
     * represent the edit page with get instance
     */
//    def edit = {
//        if(params.encodedId){
//            EmployeeSalaryInfo employeeSalaryInfo = employeeSalaryInfoService.getInstance(params)
//            if(employeeSalaryInfo){
//                respond employeeSalaryInfo
//            }else{
//                notFound()
//            }
//        }else{
//            notFound()
//        }
//    }

    /**
     * get parameters from page and update instance
     */
//    def update = {
//        EmployeeSalaryInfo employeeSalaryInfo = employeeSalaryInfoService.save(params)
//        String successMessage = message(code: 'default.updated.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), employeeSalaryInfo?.id])
//        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), employeeSalaryInfo?.id])
//        if (request.xhr) {
//            render text: (formatService.buildResponse(employeeSalaryInfo,successMessage,failMessage) as JSON), contentType: "application/json"
//        }
//        else {
//            if (employeeSalaryInfo.hasErrors()) {
//                respond employeeSalaryInfo, view:'edit'
//            }else{
//                flash.message = msg.success(label:successMessage)
//                redirect(action: "list")
//            }
//        }
//    }
//
//    /**
//     * delete declared instance depends on parameters
//     */
//    def delete = {
//        DeleteBean deleteBean = employeeSalaryInfoService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
//        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), params?.id])
//        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), params?.id,deleteBean.responseMessage?:""])
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

    /**
     * autocomplete data depends on parameters
     */
//    def autocomplete = {
//        render text: (employeeSalaryInfoService.autoComplete(params)), contentType: "application/json"
//    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

