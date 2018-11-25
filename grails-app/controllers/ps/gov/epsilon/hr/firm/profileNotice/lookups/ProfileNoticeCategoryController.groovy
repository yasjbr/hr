package ps.gov.epsilon.hr.firm.profileNotice.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.utils.v1.PCPSessionUtils

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ProfileNoticeCategory requests between model and views.
 *@see ProfileNoticeCategoryService
 *@see FormatService
**/
class ProfileNoticeCategoryController  {

    ProfileNoticeCategoryService profileNoticeCategoryService
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
            ProfileNoticeCategory profileNoticeCategory = profileNoticeCategoryService.getInstance(params)
            if(profileNoticeCategory){
                respond profileNoticeCategory
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
        ProfileNoticeCategory profileNoticeCategory= new ProfileNoticeCategory()
        profileNoticeCategory.properties= params
        profileNoticeCategory.firm= Firm.read(PCPSessionUtils.getValue('firmId'))
        [profileNoticeCategory:profileNoticeCategory]
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = profileNoticeCategoryService.search(params)
        render text: (profileNoticeCategoryService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        ProfileNoticeCategory profileNoticeCategory = profileNoticeCategoryService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), profileNoticeCategory?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), profileNoticeCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNoticeCategory, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (profileNoticeCategory?.hasErrors()) {
                respond profileNoticeCategory, view:'create'
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
            ProfileNoticeCategory profileNoticeCategory = profileNoticeCategoryService.getInstance(params)
            if(profileNoticeCategory){
                respond profileNoticeCategory
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
        ProfileNoticeCategory profileNoticeCategory = profileNoticeCategoryService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), profileNoticeCategory?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), profileNoticeCategory?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(profileNoticeCategory,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (profileNoticeCategory.hasErrors()) {
                respond profileNoticeCategory, view:'edit'
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
        DeleteBean deleteBean = profileNoticeCategoryService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (profileNoticeCategoryService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

