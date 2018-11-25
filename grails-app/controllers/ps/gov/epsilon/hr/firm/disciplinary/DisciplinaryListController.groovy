package ps.gov.epsilon.hr.firm.disciplinary

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.dispatch.DispatchList
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route DisciplinaryList requests between model and views.
 *@see DisciplinaryListService
 *@see FormatService
**/
class DisciplinaryListController  {

    DisciplinaryListService disciplinaryListService
    FormatService formatService
    SharedService sharedService

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
    def list= {
        respond sharedService.getAttachmentTypeListAsMap(DisciplinaryList.getName(), EnumOperation.DISCIPLINARY_LIST)
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        Map map = sharedService.getAttachmentTypeListAsMap(DisciplinaryList.getName(), EnumOperation.DISCIPLINARY_LIST)
        map.put("disciplinaryList",new DisciplinaryList(params))
        respond map
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        DisciplinaryList disciplinaryList = disciplinaryListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), disciplinaryList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), disciplinaryList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryList, successMessage, failMessage, true, getControllerName(),"manageDisciplinaryList") as JSON), contentType: "application/json"
        }
        else {
            if (disciplinaryList?.hasErrors()) {
                respond disciplinaryList, view:'create'
                return
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
            DisciplinaryList disciplinaryList = disciplinaryListService.getInstance(params)
            //allow edit when have CREATED status only
            if(disciplinaryList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED){
                respond disciplinaryList
            }else {
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
        DisciplinaryList disciplinaryList = disciplinaryListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), disciplinaryList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), disciplinaryList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (disciplinaryList.hasErrors()) {
                respond disciplinaryList, view:'edit'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            DisciplinaryList disciplinaryList = disciplinaryListService.getInstance(params)
            if(disciplinaryList){
                respond disciplinaryList
            }else{
                notFound()
            }
        }else{
            notFound()
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = disciplinaryListService.searchWithRemotingValues(params)
        render text: (disciplinaryListService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }


    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageDisciplinaryList= {
        if (params.encodedId || params.id) {
            DisciplinaryList disciplinaryList = disciplinaryListService.getInstance(params)
            if (disciplinaryList) {
                Map map = sharedService.getAttachmentTypeListAsMap(DisciplinaryList.getName(), EnumOperation.DISCIPLINARY_LIST)
                map.disciplinaryList = disciplinaryList
                respond map
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }


    /**
     * to show details of disciplinary judgments .
     */
    def sendListModal = {
        if(params.id){
            params.encodedId = params.remove("id")
            DisciplinaryList disciplinaryList = disciplinaryListService.getInstance(params)
            if(disciplinaryList){
                Map map = [disciplinaryList:disciplinaryList]
                respond map
            }else{
                render ""
            }
        }else{
            render ""
        }
    }


    /**
     * to send the disciplinaryList to the receiving party
     */
    def sendList = {
        DisciplinaryList disciplinaryList = disciplinaryListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryList,successMessage,failMessage) as JSON), contentType: "application/json"
        }else {
            notFound()
        }
    }

    /**
     * this action was added to return the loan list instance to be used in modal
     */
    def addDisciplinaryRecordJudgmentModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            DisciplinaryList disciplinaryList = disciplinaryListService.getInstance(params)
            if(disciplinaryList){
                respond disciplinaryList
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * To add the loan request into list
     */
    def addDisciplinaryRecordJudgment = {
        DisciplinaryList disciplinaryList = disciplinaryListService.addDisciplinaryRecordJudgment(params)
        String successMessage = message(code: 'disciplinaryList.addRecord.message')
        String failMessage = message(code: 'disciplinaryList.not.addRecord.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(disciplinaryList,successMessage,failMessage) as JSON), contentType: "application/json"
        }else {
            notFound()
        }
    }

    /**
     * To delete list record
     */
    def delete = {
        DeleteBean deleteBean = disciplinaryListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), params?.id,deleteBean.responseMessage?:""])
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
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'disciplinaryList.entity', default: 'DisciplinaryList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

