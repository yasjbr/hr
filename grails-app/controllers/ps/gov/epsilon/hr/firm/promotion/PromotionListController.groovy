package ps.gov.epsilon.hr.firm.promotion

import grails.converters.JSON
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.commons.collections.MapUtils
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.PagedList

import java.time.ZonedDateTime

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route PromotionList requests between model and views.
 *@see PromotionListService
 *@see FormatService
**/
class PromotionListController  {

    PromotionListService promotionListService
    FormatService formatService
    SharedService sharedService
    PromotionListEmployeeService promotionListEmployeeService
    CorrespondenceListService correspondenceListService

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
        respond sharedService.getAttachmentTypeListAsMap(PromotionList.getName(), EnumOperation.PROMOTION_LIST)
    }
    
    /**
     * represent the show page with get instance
     */
    def show= {
        if(params.encodedId){
            PromotionList promotionList = promotionListService.getInstanceWithRemotingValues(params)
            if(promotionList){
                respond promotionList
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new PromotionList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = promotionListService.searchWithRemotingValues(params)
        render text: (promotionListService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        PromotionList promotionList = promotionListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), promotionList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), promotionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionList, successMessage, failMessage, true, getControllerName(),"managePromotionList") as JSON), contentType: "application/json"
        }
        else {
            if (promotionList?.hasErrors()) {
                respond promotionList, view:'create'
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
            PromotionList promotionList = promotionListService.getInstance(params)
            //allow edit when have CREATED status only
            if(promotionList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED){
                respond promotionList
                return
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
        PromotionList promotionList = promotionListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), promotionList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), promotionList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (promotionList.hasErrors()) {
                respond promotionList, view:'edit'
                return
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
        DeleteBean deleteBean = promotionListService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), params?.id,deleteBean.responseMessage?:""])
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
     * this action was added to manage the list itself, will return the list instance
     */
    def managePromotionList = {
        if (params.encodedId || params.id) {
            PromotionList promotionList = promotionListService?.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(PromotionList.getName(), EnumOperation.PROMOTION_LIST)
            map.promotionList = promotionList
            map.showReceiveList = correspondenceListService.getCanReceiveList(promotionList)
            respond map
        } else {
            notFound()
        }
    }


    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService?.getInstance(params)
            respond promotionList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def addRequestModal = {
        if (params.id) {
            Map map = [:]
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService?.getInstance(params)
            map.promotionList = promotionList
            List requestTypeList = []
            requestTypeList.push(EnumRequestType.UPDATE_MILITARY_RANK_TYPE)
            requestTypeList.push(EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION)
            requestTypeList.push(EnumRequestType.SITUATION_SETTLEMENT)
            requestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST)
            requestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD)
            requestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT)
            requestTypeList.push(EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST)
            map.requestTypeList = requestTypeList
            respond map
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def addEligibleEmployeeModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService?.getInstance(params)
            respond promotionList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def addExceptionEmployeeModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService?.getInstance(params)
            respond promotionList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService.getInstance(params)
            respond promotionList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService.getInstance(params)
            respond promotionList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.encodedId) {
            PromotionList promotionList = promotionListService.getInstanceWithRemotingValues(params)
            GrailsParameterMap employeeParams = new GrailsParameterMap([id:params["selectedRow"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            PromotionListEmployee promotionListEmployee = promotionListEmployeeService.getInstance(employeeParams)
            Map data = [promotionList:promotionList,promotionListEmployee:promotionListEmployee]

            respond data
        } else {
            notFound()
        }
    }


    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            PromotionList promotionList = promotionListService.getInstanceWithRemotingValues(params)
            respond promotionList
        } else {
            notFound()
        }
    }


    //to send the promotionList to the receiving party
    def sendList = {
        PromotionList promotionList = promotionListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (promotionList.hasErrors()) {
                respond promotionList, view:'sendList'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    //to receive the promotionList
    def receiveList = {
        PromotionList promotionList = promotionListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionList,successMessage,failMessage) as JSON), contentType: "application/json"
        }
        else {
            if (promotionList.hasErrors()) {
                respond promotionList, view:'receiveListModal'
                return
            }else{
                flash.message = msg.success(label:successMessage)
                redirect(action: "list")
            }
        }
    }

    //to change the status of promotionList request to Approved
    def changeRequestToApproved = {
        Map dataMap = promotionListService.changeRequestToApproved(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success =   !errors;
            def errorFormat = msg.errorList(data: (errors),isCustom:"true");
            json.message = json.success ? msg.success(label:successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }




    //to change the status of promotionList request to Rejected
    def changeRequestToRejected = {
        Map dataMap = promotionListService.changeRequestToRejected(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success =   !errors;
            def errorFormat = msg.errorList(data: (errors),isCustom:"true");
            json.message = json.success ? msg.success(label:successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

    /**
     * to send the promotionList to the receiving party
     */
    def closeList = {
        Map dataMap = promotionListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success =   !errors;
            def errorFormat = msg.errorList(data: (errors),isCustom:"true");
            json.message = json.success ? msg.success(label:successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        }
        else {
            notFound()
        }
    }

    /**
     * To add the promotion request into list
     */
    def addPromotionRequestToList = {
        PromotionList promotionList = promotionListService?.addPromotionRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (promotionList.hasErrors()) {
                respond promotionList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }


    /**
     * To add the eligible Employee into list
     */
    def addEmployeeToList = {
        PromotionList promotionList = promotionListService?.addEmployeeToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(promotionList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (promotionList.hasErrors()) {
                respond promotionList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }


    /**
     * render the note list modal!
     */
    def noteList = {
        return [id:params["id"]]
    }

    /**
     * render the create note modal!
     */
    def noteCreate = {
        return [id:params["id"]]
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'promotionList.entity', default: 'PromotionList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

