package ps.gov.epsilon.hr.firm.profileNotice

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus

import java.time.ZonedDateTime

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ProfileNotice requests between model and views.
 *@see ProfileNoticeService
 *@see FormatService
**/
class ProfileNoticeController  {

    ProfileNoticeService profileNoticeService
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
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            ProfileNotice profileNotice = profileNoticeService.getInstance(params)
            if(profileNotice){
                respond profileNotice
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        params.profileNoticeStatus= EnumProfileNoticeStatus.NEW
        params.noticeDate= ZonedDateTime.now()
        ProfileNotice profileNotice= new ProfileNotice(params)
        [profileNotice:profileNotice]
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = profileNoticeService.searchWithRemotingValues(params)
        render text: (profileNoticeService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ProfileNotice profileNotice = profileNoticeService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), profileNotice?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), profileNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNotice, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (profileNotice?.hasErrors()) {
                respond profileNotice, view:'create'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId){
            ProfileNotice profileNotice = profileNoticeService.getInstance(params)
            if(profileNotice){
                respond profileNotice
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        ProfileNotice profileNotice = profileNoticeService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), profileNotice?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), profileNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNotice,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (profileNotice.hasErrors()) {
                respond profileNotice, view:'edit'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = profileNoticeService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), params?.id,deleteBean.responseMessage?:""])
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
     * represent the edit page with get instance
     */
    def endNoticeModal = {
        if(params.encodedId){
            params.withRemotingValues=false
            ProfileNotice profileNotice = profileNoticeService.getInstance(params)
            if(profileNotice){
                return [profileNoticeId:params.encodedId]
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    def saveChangeStatus = {
        ProfileNotice profileNotice = profileNoticeService.saveChangeStatus(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), profileNotice?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), profileNotice?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNotice,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (profileNotice.hasErrors()) {
                respond profileNotice, view:'show'
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * autocomplete data depends on parameters
     */
    def autocomplete = {
        render text: (profileNoticeService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'profileNotice.entity', default: 'ProfileNotice'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

