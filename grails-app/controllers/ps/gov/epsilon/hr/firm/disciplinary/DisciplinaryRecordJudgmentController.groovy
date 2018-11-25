package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route DisciplinaryRecordJudgment requests between model and views.
 *@see DisciplinaryRecordJudgmentService
 *@see FormatService
**/
class DisciplinaryRecordJudgmentController  {

    DisciplinaryRecordJudgmentService disciplinaryRecordJudgmentService
    FormatService formatService

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = disciplinaryRecordJudgmentService.searchWithRemotingValues(params)
        render text: (disciplinaryRecordJudgmentService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }


    def show= {
        if(params.encodedId){
            DisciplinaryRecordJudgment disciplinaryRecordJudgment = disciplinaryRecordJudgmentService.getInstanceWithRemotingValues(params)
            if(disciplinaryRecordJudgment){
                respond disciplinaryRecordJudgment
                return
            }
        }else{
            notFound()
        }
    }

    def showDetails= {
        if(params.encodedId){
            DisciplinaryRecordJudgment disciplinaryRecordJudgment = disciplinaryRecordJudgmentService.getInstanceWithRemotingValues(params)
            if(disciplinaryRecordJudgment){
                respond disciplinaryRecordJudgment
                return
            }
        }else{
            notFound()
        }
    }


    def delete = {
        DeleteBean deleteBean = disciplinaryRecordJudgmentService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment'), params?.id,deleteBean.responseMessage?:""])
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
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

