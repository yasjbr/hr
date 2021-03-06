package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route TraineeListEmployee requests between model and views.
 *@see TraineeListEmployeeService
 *@see FormatService
**/
class TraineeListEmployeeController  {

    TraineeListEmployeeService traineeListEmployeeService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            TraineeListEmployee traineeListEmployee = traineeListEmployeeService.getInstance(params)
            respond traineeListEmployee
        }else{
            notFound()
        }
    }

    def filter = {
        PagedResultList pagedResultList = traineeListEmployeeService.searchWithRemotingValues(params)
        render text: (traineeListEmployeeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }


    def delete = {
        DeleteBean deleteBean = traineeListEmployeeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'traineeListEmployee.entity', default: 'TraineeListEmployee'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'traineeListEmployee.entity', default: 'TraineeListEmployee'), params?.id,deleteBean.responseMessage?:""])
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
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'traineeListEmployee.entity', default: 'TraineeListEmployee'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

