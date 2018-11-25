package ps.gov.epsilon.aoc.correspondences

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.allowance.AllowanceList
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils

import java.time.ZonedDateTime

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route AocCorrespondenceList requests between model and views.
 * @see AocCorrespondenceListService
 * @see FormatService
 * */
class AocCorrespondenceListController {

    AocCorrespondenceListService aocCorrespondenceListService
    FormatService formatService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService
    AocListRecordService aocListRecordService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * default action in controller
     */
    def index = {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list = {
        EnumCorrespondenceType correspondenceType = params.correspondenceType ? EnumCorrespondenceType.valueOf(params.correspondenceType) : null
        EnumCorrespondenceDirection correspondenceDirection = EnumCorrespondenceDirection.INCOMING
        if (correspondenceType) {
            Map map= sharedService.getAttachmentTypeListAsMap(AocCorrespondenceList.getName(), EnumOperation.AOC_CORRESPONDENCE_LIST, EnumOperation.AOC_CORRESPONDENCE_LIST)
            map.putAll([correspondenceDirection: correspondenceDirection, correspondenceType: correspondenceType, createAction: 'createIncoming'])
            respond map
        } else {
            notFound()
        }
    }

    /**
     * represent the list page
     */
    def listOutgoing = {
        EnumCorrespondenceType correspondenceType = params.correspondenceType ? EnumCorrespondenceType.valueOf(params.correspondenceType) : null
        EnumCorrespondenceDirection correspondenceDirection = EnumCorrespondenceDirection.OUTGOING
        if (correspondenceType) {
            Map map= sharedService.getAttachmentTypeListAsMap(AocCorrespondenceList.getName(), EnumOperation.AOC_CORRESPONDENCE_LIST)
            map.putAll([correspondenceDirection: correspondenceDirection, correspondenceType: params.correspondenceType, createAction: 'createOutgoing'])
            render(view: 'list', model: map)
        } else {
            notFound()
        }
    }

    /**
     * represent the list page
     */
    def listWorkflow = {

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.getInstance(params)
            if (aocCorrespondenceList) {
                String listController = aocCorrespondenceList?.correspondenceType?.listDomain
                String listAction = 'list' + (aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING ? 'Incoming' : 'Outgoing')
                return [aocCorrespondenceList: aocCorrespondenceList, listAction: listAction, listController: listController]
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def createIncoming = {
        AocCorrespondenceListParty fromParty
        if (params.encodedId) {
            AocCorrespondenceList parentList = aocCorrespondenceListService.getInstance(params)
            params.remove('encodedId')
            params.remove('withRemotingValues')
            params.remove('id')
            params.name = parentList?.name
            params.coverLetter = parentList?.coverLetter
            params.notes = parentList?.notes
            params.deliveryDate = parentList?.deliveryDate
            params.originalSerialNumber = parentList?.originalSerialNumber
            fromParty = new AocCorrespondenceListParty(partyType: EnumCorrespondencePartyType.FROM, transientData: parentList?.receivingParty?.transientData,
                    partyClass: parentList?.receivingParty?.partyClass, partyId: parentList?.receivingParty?.partyId)
            params.correspondenceType = parentList?.correspondenceType
            params.parentCorrespondenceList = parentList.properties
        }
        params.correspondenceDirection = EnumCorrespondenceDirection.INCOMING
        params.archivingDate = ZonedDateTime.now()
        AocCorrespondenceList aocCorrespondenceList = new AocCorrespondenceList(params)
        if (!fromParty) {
            fromParty = new AocCorrespondenceListParty(partyType: EnumCorrespondencePartyType.FROM, partyClass: EnumCorrespondencePartyClass.FIRM)
        }
        aocCorrespondenceList.addToCorrespondenceListParties(fromParty)
        respond aocCorrespondenceList
    }

    def createOutgoing = {
        AocCorrespondenceListParty toParty
        if (params.encodedId) {
            AocCorrespondenceList parentList = aocCorrespondenceListService.getInstance(params)
            params.remove('encodedId')
            params.remove('withRemotingValues')
            params.remove('id')
            params.name = parentList?.name
            params.coverLetter = parentList?.coverLetter
            params.notes = parentList?.notes
            params.deliveryDate = parentList?.deliveryDate
            params.originalSerialNumber = parentList?.originalSerialNumber
            params.correspondenceType = parentList?.correspondenceType
            params['province'] = parentList.province?.properties
            params['provinceLocation'] = parentList.provinceLocation?.properties
            params.parentCorrespondenceList = parentList.properties
            toParty = new AocCorrespondenceListParty(partyType: EnumCorrespondencePartyType.TO, transientData: parentList?.sendingParty?.transientData,
                    partyClass: parentList?.sendingParty?.partyClass, partyId: parentList?.sendingParty?.partyId)
        }
        params.correspondenceDirection = EnumCorrespondenceDirection.OUTGOING
        params.archivingDate = ZonedDateTime.now()
        AocCorrespondenceList aocCorrespondenceList = new AocCorrespondenceList(params)
        if(!toParty){
            toParty= new AocCorrespondenceListParty(partyType: EnumCorrespondencePartyType.TO)
        }
        aocCorrespondenceList.addToCorrespondenceListParties(toParty)
        respond aocCorrespondenceList
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = aocCorrespondenceListService.searchWithRemotingValues(params)
        render text: (aocCorrespondenceListService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filterWorkflow = {
        PagedList pagedResultList = aocCorrespondenceListService.SearchCorrespondenceInWorkflow(params)
        render text: (aocCorrespondenceListService.resultListToMap(pagedResultList, params, aocCorrespondenceListService.DOMAIN_COLUMNS_WORKFLOW) as JSON),
                contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(aocCorrespondenceList, successMessage, failMessage, true, 'aocCorrespondenceList', 'manageList') as JSON), contentType: "application/json"
        } else {
            if (aocCorrespondenceList?.hasErrors()) {
                respond aocCorrespondenceList, view: 'create'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: listAction, controller: listController)
            }
        }
    }

    /**
     * get parameters from page and save instance
     */
    def finishList = {
        params.correspondenceStatus = EnumCorrespondenceStatus.FINISHED.toString()
        AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.changeStatus(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
        String listController = aocCorrespondenceList?.correspondenceType?.listDomain
        String listAction = 'list' + (aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING ? 'Incoming' : 'Outgoing')
        if (aocCorrespondenceList.hasErrors()) {
            flash.message = msg.success(label: failMessage)
            redirect(action: manageList, controller: listController, params: [encodedId: aocCorrespondenceList.encodedId])
        } else {
            flash.message = msg.success(label: successMessage)
            redirect(action: listAction, controller: listController)
        }
    }

/**
 * represent the edit page with get instance
 */
    def edit = {
        if (params.encodedId || params.id) {
            AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.getInstance(params)
            if (aocCorrespondenceList) {
                String listController = aocCorrespondenceList?.correspondenceType?.listDomain
                String listAction = 'list' + (aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING ? 'Incoming' : 'Outgoing')
                if (aocCorrespondenceList.currentStatus in [EnumCorrespondenceStatus.NEW, EnumCorrespondenceStatus.CREATED]) {
                    // force incoming employee to insert incomingSerial number
                    if(aocCorrespondenceList.serialNumber?.equals(aocCorrespondenceListService.DEFAULT_SERIAL_NUMBER)){
                        aocCorrespondenceList.serialNumber= null
                    }
                    [aocCorrespondenceList: aocCorrespondenceList, listController: listController, listAction: listAction]
                } else {
                    String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
                    flash.message = msg.error(label: failMessage)
                    redirect(action: 'show', params: [encodedId: aocCorrespondenceList.encodedId])
                }
            } else {
                notFound()
            }

        } else {
            notFound()
        }
    }

/**
 * get parameters from page and update instance
 */
    def update = {
        AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
        render text: (formatService.buildResponse(aocCorrespondenceList, successMessage, failMessage, true, getControllerName(), 'manageList') as JSON), contentType: "application/json"
    }

/**
 * delete declared instance depends on parameters
 */
    def delete = {
        DeleteBean deleteBean = aocCorrespondenceListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), params?.id, ""])
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
    def manageList = {
        boolean manageWorkflow = params.boolean('manageWorkflow', false)
        if (params.encodedId || (params.id && manageWorkflow)) {
            AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.getInstance(params)
            if(aocCorrespondenceList) {
                String listController = aocCorrespondenceList?.correspondenceType?.listDomain
                String listAction = 'list' + (aocCorrespondenceList?.correspondenceDirection == EnumCorrespondenceDirection.INCOMING ? 'Incoming' : 'Outgoing')
                WorkflowPathHeader workflowPathHeader = null
                String employeeId = null
                if (manageWorkflow) {
                    GrailsParameterMap workflowPathHeaderParam = new GrailsParameterMap([objectId: aocCorrespondenceList.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                    workflowPathHeader = aocCorrespondenceListService.getWorkflowPathHeader(workflowPathHeaderParam)
                    //TODO: MOVE TO SERVICE AND CALL THEM
                    employeeId = Employee.createCriteria().get {
                        eq('personId', PCPSessionUtils.getValue('personId'))
                        projections {
                            property('id')
                        }
                    }
                }
                Map map = sharedService.getAttachmentTypeListAsMap(AocCorrespondenceList.getName(), EnumOperation.AOC_CORRESPONDENCE_LIST, EnumOperation.AOC_CORRESPONDENCE_LIST)
                map.putAll([aocCorrespondenceList: aocCorrespondenceList, serviceName: aocListRecordService.getListRecordServiceName(aocCorrespondenceList.correspondenceType, false),
                            actionMatrix         : aocCorrespondenceListService.getManageListActionViewMatrix(aocCorrespondenceList),
                            requestEntityName    : message(code: 'EnumCorrespondenceType.' + aocCorrespondenceList.correspondenceType),
                            listAction           : listAction, listController: listController, workflowPathHeader: workflowPathHeader, employeeId: employeeId])
                respond map
            }else{
                notFound()
            }
        } else {
            notFound()
        }
    }

    def startWorkflow = {
        if (params.encodedId) {
            AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.getInstance(params)
            String successMessage = message(code: 'workflow.started.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
            String failMessage = message(code: 'workflow.not.started.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), aocCorrespondenceList?.id])
            if (aocCorrespondenceListService.startWorkflow(aocCorrespondenceList)) {
                flash.message = msg.success(label: successMessage)
            } else {
                flash.message = msg.error(label: failMessage)
            }
            String listController = aocCorrespondenceList?.correspondenceType?.listDomain
            String listAction = 'list' + (aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING ? 'Incoming' : 'Outgoing')
            redirect(action: listAction, controller: listController)
        } else {
            notFound()
        }
    }

    def sendList = {
        if (params.encodedId) {
            Boolean isManagerial= params.boolean('isManagerial', false)
            AocCorrespondenceList aocCorrespondenceList = aocCorrespondenceListService.getInstance(params)
            String successMessage = message(code: 'list.sent.message')
            String failMessage = message(code: 'list.not.sent.message')
            Map sendResult= aocCorrespondenceListService.sendList(aocCorrespondenceList, isManagerial)
            String listController = aocCorrespondenceList?.correspondenceType?.listDomain
            String listAction = 'list' + (aocCorrespondenceList.correspondenceDirection == EnumCorrespondenceDirection.INCOMING ? 'Incoming' : 'Outgoing')
            params.clear()
            if (sendResult.success) {
                flash.message = msg.success(label: successMessage)
            } else {
                if(sendResult.failMessage){
                    failMessage= message(code:sendResult.failMessage, default:sendResult.failMessage)
                }
                flash.message = msg.error(label: failMessage)
                listController= getControllerName()
                listAction= 'manageList'
                params.encodedId= aocCorrespondenceList?.encodedId
            }
            redirect(action: listAction, controller: listController, params: params)
        } else {
            notFound()
        }
    }

/**
 * autocomplete data depends on parameters
 */
    def autocomplete = {
        render text: (aocCorrespondenceListService.autoComplete(params)), contentType: "application/json"
    }

/**
 * to manage workflow
 */
    def manageListWorkflow = {
        params.manageWorkflow = true
        redirect(action: 'manageList', params: params)
    }


    /**
     * This action adds a copy to party
     */
    def addCopyToModal = {

    }

/**
 * to handle requests if object not found.
 * @return void
 */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'aocCorrespondenceList.entity', default: 'AocCorrespondenceList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

