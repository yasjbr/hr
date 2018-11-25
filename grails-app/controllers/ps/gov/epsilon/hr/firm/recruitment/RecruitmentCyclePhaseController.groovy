package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route RecruitmentCyclePhase requests between model and views.
 *@see RecruitmentCyclePhaseService
 *@see FormatService
**/
class RecruitmentCyclePhaseController  {

    RecruitmentCyclePhaseService recruitmentCyclePhaseService
    FormatService formatService
    RecruitmentCycleService recruitmentCycleService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {
        if(params.id){
            RecruitmentCycle recruitmentCycle = recruitmentCycleService.getInstance(params)
            respond recruitmentCycle
        }
    }

    def filter = {
        PagedResultList pagedResultList = recruitmentCyclePhaseService.search(params)
        render text: (recruitmentCyclePhaseService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def autocomplete = {
        render text: (recruitmentCyclePhaseService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'recruitmentCyclePhase.entity', default: 'RecruitmentCyclePhase'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

