package ps.gov.epsilon.hr.firm.evaluation

import grails.converters.JSON
import grails.gorm.PagedResultList
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route EvaluationListEmployee requests between model and views.
 *@see EvaluationListEmployeeService
 *@see FormatService
**/
class EvaluationListEmployeeController  {

    EvaluationListEmployeeService evaluationListEmployeeService
    FormatService formatService

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
    def list= {}


    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def exportExcel = {
        params["evaluationList.id"] = params.remove("id")
        PagedResultList pagedResultList = evaluationListEmployeeService.searchWithRemotingValues(params)
        response.setContentType('application/vnd.ms-excel')
        response.setHeader('Content-Disposition', 'Attachment;Filename="employeeEvaluation.xls"')
        WritableWorkbook workbook = Workbook.createWorkbook(response.outputStream)
        WritableSheet sheet1 = workbook.createSheet("Students", 0)
        sheet1.addCell(new Label(0,0, g.message(code: 'employeeEvaluation.id.label')))
        sheet1.addCell(new Label(1,0, g.message(code: 'employee.financialNumber.label')))
        sheet1.addCell(new Label(2,0, g.message(code: 'employeeEvaluation.evaluationTemplate.label')))
        sheet1.addCell(new Label(3,0, g.message(code: 'employeeEvaluation.evaluationSum.label')))
        sheet1.addCell(new Label(4,0, g.message(code: 'employeeEvaluation.evaluationResult.label')))

        Integer colIndex = sheet1.getColumns()
        Integer internal_colIndex = 0
        def employeeEvaluationItems

        pagedResultList.eachWithIndex{ EvaluationListEmployee evaluationListEmployee, int idx ->
            sheet1.addCell(new Label(0,idx+1, evaluationListEmployee?.employeeEvaluation?.id))
            sheet1.addCell(new Label(1,idx+1, evaluationListEmployee?.employeeEvaluation?.employee?.financialNumber))
            sheet1.addCell(new Label(2,idx+1, evaluationListEmployee?.employeeEvaluation?.evaluationTemplate?.universalCode))

            sheet1.addCell(new Label(3,idx+1, evaluationListEmployee?.employeeEvaluation?.evaluationSum.toString()))
            sheet1.addCell(new Label(4,idx+1, evaluationListEmployee?.employeeEvaluation?.evaluationResult?.descriptionInfo?.localName))

            internal_colIndex = colIndex // to fill the first question for the next employee

            employeeEvaluationItems = evaluationListEmployee?.employeeEvaluation?.employeeEvaluationItems?.sort{it.evaluationItem.index}
            employeeEvaluationItems?.eachWithIndex { JoinedEmployeeEvaluationItems joinedEmployeeEvaluationItem, Integer index ->
                sheet1.addCell(new Label(internal_colIndex,0, "Q_${index}"))
                sheet1.addCell(new Label(internal_colIndex,idx+1, joinedEmployeeEvaluationItem?.evaluationItem?.universalCode))
                sheet1.addCell(new Label(++internal_colIndex, 0, "M_${index}"))
                sheet1.addCell(new Label(internal_colIndex++, idx+1, joinedEmployeeEvaluationItem?.mark?.toString()))
            }
        }
        workbook.write();
        workbook.close();
    }



    /**
     * represent the show page with get instance
     */
//    def show= {
//        if(params.encodedId){
//            EvaluationListEmployee evaluationListEmployee = evaluationListEmployeeService.getInstance(params)
//            if(evaluationListEmployee){
//                respond evaluationListEmployee
//                return
//            }
//        }else{
//            notFound()
//        }
//    }

    /**
     * represent the create page empty instance
     */
//    def create = {
//        respond new EvaluationListEmployee(params)
//    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = evaluationListEmployeeService.searchWithRemotingValues(params)
        render text: (evaluationListEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
//    def save = {
//        //todo: get the firm from params without need to use the session value in case the user is super admin
//        params["firm.id"] = session.getAttribute("firmId")
//        EvaluationListEmployee evaluationListEmployee = evaluationListEmployeeService.save(params)
//        String successMessage = message(code: 'default.created.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), evaluationListEmployee?.id])
//        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), evaluationListEmployee?.id])
//        if (request.xhr) {
//            render text: (formatService.buildResponse(evaluationListEmployee, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
//        }
//        else {
//            if (evaluationListEmployee?.hasErrors()) {
//                respond evaluationListEmployee, view:'create'
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
//            EvaluationListEmployee evaluationListEmployee = evaluationListEmployeeService.getInstance(params)
//            if(evaluationListEmployee){
//                respond evaluationListEmployee
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
//        EvaluationListEmployee evaluationListEmployee = evaluationListEmployeeService.save(params)
//        String successMessage = message(code: 'default.updated.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), evaluationListEmployee?.id])
//        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), evaluationListEmployee?.id])
//        if (request.xhr) {
//            render text: (formatService.buildResponse(evaluationListEmployee,successMessage,failMessage) as JSON), contentType: "application/json"
//        }
//        else {
//            if (evaluationListEmployee.hasErrors()) {
//                respond evaluationListEmployee, view:'edit'
//            }else{
//                flash.message = msg.success(label:successMessage)
//                redirect(action: "list")
//            }
//        }
//    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = evaluationListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), params?.id,deleteBean.responseMessage?:""])
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
//    def autocomplete = {
//        render text: (evaluationListEmployeeService.autoComplete(params)), contentType: "application/json"
//    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

