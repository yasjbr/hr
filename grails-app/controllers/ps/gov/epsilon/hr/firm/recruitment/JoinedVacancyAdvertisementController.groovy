package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import guiplugin.FormatService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route JoinedVacancyAdvertisement requests between model and views.
 *@see JoinedVacancyAdvertisementService
 *@see FormatService
**/
class JoinedVacancyAdvertisementController  {

    JoinedVacancyAdvertisementService joinedVacancyAdvertisementService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if(params.encodedId){
            JoinedVacancyAdvertisement joinedVacancyAdvertisement = joinedVacancyAdvertisementService.getInstance(params)
            respond joinedVacancyAdvertisement
        }else{
            notFound()
        }
    }

    def create = {
        respond new JoinedVacancyAdvertisement(params)
    }

    def filter = {
        PagedResultList pagedResultList = joinedVacancyAdvertisementService.search(params)
        render text: (joinedVacancyAdvertisementService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        JoinedVacancyAdvertisement joinedVacancyAdvertisement = joinedVacancyAdvertisementService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), joinedVacancyAdvertisement?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), joinedVacancyAdvertisement?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedVacancyAdvertisement, successMessage, failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedVacancyAdvertisement?.hasErrors()) {
                respond joinedVacancyAdvertisement, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            respond joinedVacancyAdvertisementService.getInstance(params)
        }else{
            notFound()
        }
    }

    def update = {
        JoinedVacancyAdvertisement joinedVacancyAdvertisement = joinedVacancyAdvertisementService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), joinedVacancyAdvertisement?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), joinedVacancyAdvertisement?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(joinedVacancyAdvertisement,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (joinedVacancyAdvertisement.hasErrors()) {
                respond joinedVacancyAdvertisement, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {

        DeleteBean deleteBean = joinedVacancyAdvertisementService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), params?.id,deleteBean.responseMessage?:""])
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

    def autocomplete = {
        render text: (joinedVacancyAdvertisementService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'joinedVacancyAdvertisement.entity', default: 'JoinedVacancyAdvertisement'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

