package ps.gov.epsilon.aoc.correspondences

import grails.converters.JSON
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 *<h1>Purpose</h1>
 * Route AocListRecord requests between model and views.
 *@see AocListRecordService
 *@see FormatService
**/
class AocListRecordController  {

    AocListRecordService aocListRecordService
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
            AocListRecord aocListRecord = aocListRecordService.getInstance(params)
            if(aocListRecord){
                respond aocListRecord
                return
            }
        }else{
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def createRequestModal = {
        if (params.aocCorrespondenceListId) {
            AocCorrespondenceList aocCorrespondenceList= AocCorrespondenceList.read(params.long('aocCorrespondenceListId'))
            Long firmId= aocCorrespondenceList.hrFirmId
            return [aocCorrespondenceList: aocCorrespondenceList, firmId:firmId,
                    requestEntityName:message(code:'EnumCorrespondenceType.'+aocCorrespondenceList.correspondenceType)]
        } else {
            render ""
        }
    }

    /**
     * this action is used to add request to correspondence list
     */
    def addRequestModal = {
        if (params.aocCorrespondenceListId) {
            AocCorrespondenceList aocCorrespondenceList= AocCorrespondenceList.read(params.long('aocCorrespondenceListId'))
            if(aocCorrespondenceList) {
                Long firmId = aocCorrespondenceList.hrFirmId
                List<Firm> firms = null
                if (!firmId) {
                    if (aocCorrespondenceList.parentCorrespondenceList) {
                        firms = aocCorrespondenceList.parentCorrespondenceList.joinedAocHrCorrespondenceLists?.firm?.toList()
                    } else {
                        firms = aocCorrespondenceList.joinedAocHrCorrespondenceLists?.firm?.toList()
                    }
                }
                return [aocCorrespondenceList: aocCorrespondenceList, requestEntityName: message(code: 'EnumCorrespondenceType.' + aocCorrespondenceList.correspondenceType),
                        serviceName          : aocListRecordService.getListRecordServiceName(aocCorrespondenceList.correspondenceType, false),
                        messagePrefix        : aocCorrespondenceList.correspondenceType.hrListEmployee, firmId: firmId, firms: firms]
            } else{
                log.error("addRequestModal: aocCorrespondenceList not found for id = " + params.aocCorrespondenceListId)
                render ""
            }
        } else {
            log.error("addRequestModal: aocCorrespondenceListId is null ")
            render ""
        }
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedResultList = aocListRecordService.searchWithRemotingValues(params)
        render text: (aocListRecordService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    def filterNotIncludedRecords = {
        PagedList pagedResultList = aocListRecordService.searchNotIncludedRecords(params)
        render text: (aocListRecordService.resultListToMap(pagedResultList,params, aocListRecordService.getHrDomainColumns(params)) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        AocListRecord aocListRecord = aocListRecordService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), aocListRecord?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), aocListRecord?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(aocListRecord, successMessage, failMessage, false, getControllerName(), "manageList") as JSON), contentType: "application/json"
        }
        else {
            if (aocListRecord?.hasErrors()) {
                respond aocListRecord, view:'createRequestModal'
            }else{
                flash.message = msg.success(label:successMessage)
                respond aocListRecord, view:'createRequestModal'
            }
        }
    }

    /**
     * get parameters from page and save instance
     */
    def saveExistingRecords = {
        def json = [:]
        try{
            aocListRecordService.saveExistingRecords(params)
            json.success = true
            json.message = msg.success(label: message(code: 'default.created.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), '']))
        }catch (Exception ex){
            log.error("saveExistingRecords failed : $ex.message")
            json.success= false
            json.message = msg.error(label:message(code: 'default.not.created.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), '']))
        }
        render text: (json as JSON), contentType: "application/json"
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        if(params.encodedId){
            AocListRecord aocListRecord = aocListRecordService.getInstance(params)
            if(aocListRecord){
                respond aocListRecord
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
    def saveStatusChange = {
        AocListRecord aocListRecord = aocListRecordService.saveStatusChange(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'aocListRecord.recordStatus.label', default: 'Record Status'), aocListRecord?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'aocListRecord.recordStatus.label', default: 'Record Status'), aocListRecord?.id])
        render text: (formatService.buildResponse(aocListRecord,successMessage,failMessage) as JSON), contentType: "application/json"
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = aocListRecordService.delete(PCPUtils.convertParamsToDeleteBean(params,"encodedId"),
                params.long('aocCorrespondenceListId'), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), params?.id,deleteBean.responseMessage?:""])
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
        render text: (aocListRecordService.autoComplete(params)), contentType: "application/json"
    }

    def selectEmployee = {
        Map result= aocListRecordService.getEmployeeRequestInfo(params)
        if(result.success==false){
            String failMessage = message(code: result.message, args: null, default: result.message)
            result['message']= msg.error(label: failMessage)
            render text: (result as JSON), contentType: "application/json"
        } else{
            result.hideManagerialOrderInfo=true
            if(result.formName){
                render(template: "/$params.parentFolder/$result.formName", model: result)
            } else if(params["formName"]){
                render(template: "/$params.parentFolder/$params.formName", model: result)
            } else{
                render(template: "/$params.parentFolder/form", model: result)
            }
        }
    }

    def operationsForm = {
        Map result= aocListRecordService.getOperationFormInfo(params)
        if(result.success==false){
            String failMessage = message(code: result.message, args: null, default: result.message)
            result['message']= msg.error(label: failMessage)
            render text: (result as JSON), contentType: "application/json"
        }else{
            render(template: "/$params.parentFolder/operationsForm", model: result)
        }
    }

    /**
     * this action is used to add request to correspondence list
     */
    def changeRecordStatusModal = {
        if (params.encodedId) {
            AocListRecord aocListRecordInstance = aocListRecordService.getInstance(params)
            if(aocListRecordInstance){
                params['listRecord.id']= aocListRecordInstance.id
                params.viewChangeStatus= true
                params.saveAction= 'saveStatusChange'
                params.saveController= 'aocListRecord'
                params.parentFolder = aocListRecordService.getAcceptFormParentFolder(aocListRecordInstance)
                params.noteFormTitle=message(code:'aocListRecord.changeStatus.label', args: [message(code:'aocListRecord.entity')])
                params.callBackFunction = 'statusCallBackFun'
                params.formName = 'listRecordStatusForm'
                redirect(controller:'aocListRecordNote', action: 'createModal', params:params)
            }else{
                render "Not found"
            }
        } else {
            render "Not found"
        }
    }


    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.not.found.message', args: [message(code: 'aocListRecord.entity', default: 'AocListRecord'), params?.id])
//                redirect action: "list", method: "GET"
//            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

