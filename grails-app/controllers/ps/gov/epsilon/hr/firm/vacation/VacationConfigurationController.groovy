package ps.gov.epsilon.hr.firm.vacation

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankService
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationTypeService

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route VacationConfiguration requests between model and views.
 * @see VacationConfigurationService
 * @see FormatService
 * */
class VacationConfigurationController {

    VacationConfigurationService vacationConfigurationService
    FormatService formatService
    VacationTypeService vacationTypeService
    MilitaryRankService militaryRankService

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
    def list = {}

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            VacationConfiguration vacationConfiguration = vacationConfigurationService.getInstanceWithRemotingValues(params)
            if (vacationConfiguration) {
                respond vacationConfiguration
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
        GrailsParameterMap listParam = new GrailsParameterMap([max: 100], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        respond([vacationConfiguration: new VacationConfiguration(params), vacationTypeList: vacationTypeService.search(listParam)?.resultList, militaryRankList: militaryRankService.search(listParam)?.resultList])
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = vacationConfigurationService.searchWithRemotingValues(params)
        render text: (vacationConfigurationService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        VacationConfiguration vacationConfiguration = vacationConfigurationService.saveAll(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), vacationConfiguration?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), vacationConfiguration?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationConfiguration, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (vacationConfiguration?.hasErrors()) {
                respond vacationConfiguration, view: 'create'
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
            VacationConfiguration vacationConfiguration = vacationConfigurationService.getInstanceWithRemotingValues(params)
            if (vacationConfiguration) {
                respond vacationConfiguration
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
        VacationConfiguration vacationConfiguration = vacationConfigurationService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), vacationConfiguration?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), vacationConfiguration?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationConfiguration, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacationConfiguration.hasErrors()) {
                respond vacationConfiguration, view: 'edit'
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
        DeleteBean deleteBean = vacationConfigurationService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), params?.id, " "])
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
        render text: (vacationConfigurationService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

