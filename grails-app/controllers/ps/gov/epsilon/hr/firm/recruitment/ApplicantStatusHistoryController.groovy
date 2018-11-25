package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route ApplicantStatusHistory requests between model and views.
 *@see ApplicantStatusHistoryService
 *@see FormatService
**/
class ApplicantStatusHistoryController  {

    ApplicantStatusHistoryService applicantStatusHistoryService
    FormatService formatService
    ApplicantService applicantService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {
        if(params.id){
            Applicant applicant = applicantService.getInstance(params, true)
            respond applicant
        }
    }

    def filter = {
        PagedResultList pagedResultList = applicantStatusHistoryService.search(params,true)
        render text: (applicantStatusHistoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        ApplicantStatusHistory applicantStatusHistory = applicantStatusHistoryService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'applicantStatusHistory.entity', default: 'ApplicantStatusHistory'), applicantStatusHistory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'applicantStatusHistory.entity', default: 'ApplicantStatusHistory'), applicantStatusHistory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantStatusHistory, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (applicantStatusHistory?.hasErrors()) {
                respond applicantStatusHistory, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def autocomplete = {
        render text: (applicantStatusHistoryService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'applicantStatusHistory.entity', default: 'ApplicantStatusHistory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

