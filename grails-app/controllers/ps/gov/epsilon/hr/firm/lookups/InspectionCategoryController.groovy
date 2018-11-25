package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route InspectionCategory requests between model and views.
 * @see InspectionCategoryService
 * @see FormatService
 * */
class InspectionCategoryController {

    InspectionCategoryService inspectionCategoryService
    FormatService formatService
    CommitteeRoleService committeeRoleService
    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            InspectionCategory inspectionCategory = inspectionCategoryService.getInstance(params)
            respond inspectionCategory
        } else {
            notFound()
        }
    }

    def create = {
        //to get list of committee
        Map data = [:]
        List committeeRoleList = committeeRoleService?.search(params)
        data = [inspectionCategory: new InspectionCategory(params),
                committeeRoleList : committeeRoleList]
        respond data

    }
    def filter = {
        PagedResultList pagedResultList = inspectionCategoryService.search(params)
        render text: (inspectionCategoryService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        InspectionCategory inspectionCategory = inspectionCategoryService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'inspectionCategory.entity', default: 'InspectionCategory'), inspectionCategory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'inspectionCategory.entity', default: 'InspectionCategory'), inspectionCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(inspectionCategory, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (inspectionCategory?.hasErrors()) {
                respond inspectionCategory, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            //to get list of committee
            Map data = [:]
            //to get all committee role
            GrailsParameterMap roleParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List committeeRoleList = committeeRoleService?.search(roleParams)
            data = [inspectionCategory      : inspectionCategoryService.getInstance(params),
                    committeeRoleList: committeeRoleList]
            respond data
            return
        } else {
            notFound()
        }
    }

    def update = {
        InspectionCategory inspectionCategory = inspectionCategoryService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'inspectionCategory.entity', default: 'InspectionCategory'), inspectionCategory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'inspectionCategory.entity', default: 'InspectionCategory'), inspectionCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(inspectionCategory, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (inspectionCategory.hasErrors()) {
                respond inspectionCategory, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = inspectionCategoryService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'inspectionCategory.entity', default: 'InspectionCategory'), params?.id])
        String failMessage = message(code: 'inspectionCategory.not.deleted.label', default: 'can not delete inspection category used.') ?: ""
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
        render text: (inspectionCategoryService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'inspectionCategory.entity', default: 'InspectionCategory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

