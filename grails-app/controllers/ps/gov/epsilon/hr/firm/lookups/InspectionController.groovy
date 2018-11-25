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
 *<h1>Purpose</h1>
 * Route Inspection requests between model and views.
 *@see InspectionService
 *@see FormatService
**/
class InspectionController  {

    InspectionService inspectionService
    FormatService formatService
    CommitteeRoleService committeeRoleService

    static allowedMethods = [save: "POST", update: "POST"]


    def index= {
        redirect action: "list", method: "GET"
    }

    def list= {}

    def show= {
        if (params.encodedId) {
            Inspection inspection = inspectionService.getInstance(params)
            if (inspection) {
                respond inspection
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        //to get list of committee
        Map data = [:]
        GrailsParameterMap committeeRoleParam=new GrailsParameterMap([:],WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        List committeeRoleList = committeeRoleService?.search(committeeRoleParam)
        data  = [inspection:new Inspection(params),
                 committeeRoleList:committeeRoleList]
        respond data

    }

    def filter = {
        PagedResultList pagedResultList = inspectionService.search(params)
        render text: (inspectionService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        Inspection inspection = inspectionService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'inspection.entity', default: 'Inspection'), inspection?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'inspection.entity', default: 'Inspection'), inspection?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(inspection, successMessage, failMessage, true, getControllerName(),"create") as JSON), contentType: "application/json"

        }
        else {
            if (inspection?.hasErrors()) {
                respond inspection, view:'create'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if(params.encodedId){
            //to get list of committee
            Map data = [:]
            GrailsParameterMap committeeRoleParam=new GrailsParameterMap([:],WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List committeeRoleList = committeeRoleService?.search(committeeRoleParam)
            data  = [inspection:inspectionService.getInstance(params),
                    committeeRoleList:committeeRoleList]
            respond data
        }else{
            notFound()
        }
    }

    def update = {
        Inspection inspection = inspectionService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'inspection.entity', default: 'Inspection'), inspection?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'inspection.entity', default: 'Inspection'), inspection?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(inspection,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (inspection.hasErrors()) {
                respond inspection, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = inspectionService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'inspection.entity', default: 'Inspection'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'inspection.entity', default: 'Inspection'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (inspectionService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'inspection.entity', default: 'Inspection'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

