package ps.gov.epsilon.aoc.correspondences

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

class AocListRecordNoteController {

    AocListRecordNoteService aocListRecordNoteService

    def list() {
        [listRecordId:params.long('id')]
    }

    def filter = {
        PagedResultList pagedResultList = aocListRecordNoteService.search(params)
        render text: (aocListRecordNoteService.resultListToMap(pagedResultList,params) as JSON), contentType: "application/json"
    }

    /**
     * this action is used to add request to correspondence list
     */
    def createModal = {
        if(params.id){
            params['listRecord.id']= params.remove('id')
        }
        if(params['listRecord.id']){
            AocListRecord aocListRecord = AocListRecord.read(params.long('listRecord.id'))
            if(aocListRecord){
                if(!params.saveAction){
                    params.saveAction= 'save'
                    params.callBackFunction = 'noteCallBackFun'
                    params.saveController = 'aocListRecordNote'
                    params.formName = 'listRecordNoteForm'
                    params.noteFormTitle = message(code:'default.create.label', args: [message(code:'promotionListEmployeeNote.entity')])
                }
                AocListRecordNote aocListRecordNote= new AocListRecordNote()
                aocListRecordNote.listRecord= aocListRecord
                aocListRecordNote.noteDate= ZonedDateTime.now()
                [aocListRecordNoteInstance:aocListRecordNote, saveAction:params.saveAction,noteFormTitle:params.noteFormTitle,
                 callBackFunction:params.callBackFunction,formName:params.formName,
                 saveController:params.saveController, viewChangeStatus:params.viewChangeStatus, parentFolder:params.parentFolder]
            }else{
                render "Not found"
            }
        }else{
            render "Not found"
        }
    }

    /**
     * this action is used to add request to correspondence list
     */
    def editModal = {
        if (params.aocCorrespondenceListId) {
            AocCorrespondenceList aocCorrespondenceList= AocCorrespondenceList.read(params.long('aocCorrespondenceListId'))
            return [aocCorrespondenceList: aocCorrespondenceList, requestEntityName:message(code:'EnumCorrespondenceType.'+aocCorrespondenceList.correspondenceType),
                    serviceName:aocListRecordService.getListRecordServiceName(aocCorrespondenceList.correspondenceType, false),
                    messagePrefix:aocCorrespondenceList.correspondenceType.hrListEmployee]
        } else {
            render ""
        }
    }

    def delete = {
        DeleteBean deleteBean = aocListRecordNoteService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'aocListRecordNote.entity', default: 'aocListRecordNote'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'aocListRecordNote.entity', default: 'aocListRecordNote'), params?.id, deleteBean.responseMessage ?: ""])
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

    def save(){
        AocListRecordNote aocListRecordNote = aocListRecordNoteService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'aocListRecordNote.entity', default: 'AocListRecordNote'), aocListRecordNote?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'aocListRecordNote.entity', default: 'AocListRecordNote'), aocListRecordNote?.id])
        def json = [:]
        json.success = !aocListRecordNote.hasErrors()
        json.message = json.success ? msg.success(label: successMessage) : msg.error(label: failMessage)
        render text: (json as JSON), contentType: "application/json"
    }

    def update(){

    }

}
