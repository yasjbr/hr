package ps.gov.epsilon.hr.firm.settings

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.enums.v1.EnumSystemModule
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route FirmActiveModule requests between model and views.
 * @see FirmActiveModuleService
 * @see FormatService
 * */
class FirmActiveModuleController {

    FirmActiveModuleService firmActiveModuleService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            FirmActiveModule firmActiveModule = firmActiveModuleService.getInstance(params)
            if (firmActiveModule) {
                respond firmActiveModule
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        def data = [:]
        //to get list of selected module
        GrailsParameterMap EnumSystemModuleParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        List<EnumSystemModule> selectedSystemModuleList = firmActiveModuleService.search(EnumSystemModuleParams)?.systemModule
        data = [firmActiveModule: new FirmActiveModule(params), selectedSystemModuleList: selectedSystemModuleList]
        respond data
    }

    def filter = {
        PagedResultList pagedResultList = firmActiveModuleService.search(params)
        render text: (firmActiveModuleService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        FirmActiveModule firmActiveModule = firmActiveModuleService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), firmActiveModule?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), firmActiveModule?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(firmActiveModule, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (firmActiveModule?.hasErrors()) {
                respond firmActiveModule, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        def data = [:]
        if (params.encodedId) {
            //to get list of selected module
            List<EnumSystemModule> selectedSystemModuleList = firmActiveModuleService.search(params)?.systemModule
            FirmActiveModule firmActiveModule = firmActiveModuleService.getInstance(params)
            if (firmActiveModule) {
                data = [firmActiveModule        : firmActiveModule,
                        selectedSystemModuleList: selectedSystemModuleList]
                respond data
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        FirmActiveModule firmActiveModule = firmActiveModuleService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), firmActiveModule?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), firmActiveModule?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(firmActiveModule, successMessage, failMessage, true, getControllerName(), "Show") as JSON), contentType: "application/json"
        } else {
            if (firmActiveModule.hasErrors()) {
                respond firmActiveModule, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = firmActiveModuleService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (firmActiveModuleService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'firmActiveModule.entity', default: 'FirmActiveModule'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

