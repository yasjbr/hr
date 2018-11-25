<%=packageName ? "package ${packageName}" : ''%>

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.${versionNumber}.DeleteBean
import ps.police.common.utils.${versionNumber}.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route ${className} requests between model and views.
 *@see ${className}Service
 *@see FormatService
**/
class ${className}Controller  {

    ${className}Service ${propertyName}Service
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
            ${className} ${propertyName} = ${propertyName}Service.getInstance(params)
            if(${propertyName}){
                respond ${propertyName}
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
        respond new ${className}(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = ${propertyName}Service.search(params)
        render text: (${propertyName}Service.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ${className} ${propertyName} = ${propertyName}Service.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: '${propertyName}.entity', default: '${className}'), ${propertyName}?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: '${propertyName}.entity', default: '${className}'), ${propertyName}?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(${propertyName}, successMessage, failMessage, true, getControllerName(),"list") as JSON), contentType: "application/json"
        }
        else {
            if (${propertyName}?.hasErrors()) {
                respond ${propertyName}, view:'create'
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
            ${className} ${propertyName} = ${propertyName}Service.getInstance(params)
            if(${propertyName}){
                respond ${propertyName}
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
        ${className} ${propertyName} = ${propertyName}Service.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: '${propertyName}.entity', default: '${className}'), ${propertyName}?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: '${propertyName}.entity', default: '${className}'), ${propertyName}?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(${propertyName},successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (${propertyName}.hasErrors()) {
                respond ${propertyName}, view:'edit'
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
        DeleteBean deleteBean = ${propertyName}Service.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: '${propertyName}.entity', default: '${className}'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: '${propertyName}.entity', default: '${className}'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (${propertyName}Service.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: '${propertyName}.entity', default: '${className}'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

