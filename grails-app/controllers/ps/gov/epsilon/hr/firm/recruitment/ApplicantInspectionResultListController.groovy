package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route ApplicantInspectionResultList requests between model and views.
 * @see ApplicantInspectionResultListService
 * @see FormatService
 * */
class ApplicantInspectionResultListController {


    ApplicantInspectionResultListService applicantInspectionResultListService
    FormatService formatService
    SharedService sharedService
    CorrespondenceListService correspondenceListService

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
        respond sharedService.getAttachmentTypeListAsMap(ApplicantInspectionResultList.getName(), EnumOperation.APPLICANT_INSPECTION_RESULT_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            if (applicantInspectionResultList) {
                respond applicantInspectionResultList
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * represent the create page empty instance
     */
    def create = {
        respond new ApplicantInspectionResultList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = applicantInspectionResultListService.searchWithRemotingValues(params)
        render text: (applicantInspectionResultListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), applicantInspectionResultList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), applicantInspectionResultList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage, true, getControllerName(), "manageApplicantInspectionResultList") as JSON), contentType: "application/json"
        } else {
            if (applicantInspectionResultList?.hasErrors()) {
                respond applicantInspectionResultList, view: 'create'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * represent the edit page with get instance
     */
    def edit = {
        String failMessage = message(code: 'list.fail.edit.message', args: [])
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            if (applicantInspectionResultList && applicantInspectionResultList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond applicantInspectionResultList
            } else {
                flash.message = msg.error(label: failMessage)
                redirect(action: "list")
            }
        } else {
            notFound()
        }
    }

    /**
     * get parameters from page and update instance
     */
    def update = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), applicantInspectionResultList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), applicantInspectionResultList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (applicantInspectionResultList.hasErrors()) {
                respond applicantInspectionResultList, view: 'edit'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * delete declared instance depends on parameters
     */
    def delete = {
        DeleteBean deleteBean = applicantInspectionResultListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (applicantInspectionResultListService.autoComplete(params)), contentType: "application/json"
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'applicantInspectionResultList.entity', default: 'ApplicantInspectionResultList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageApplicantInspectionResultList = {
        if (params.encodedId || params.id) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            Map map = sharedService.getAttachmentTypeListAsMap(ApplicantInspectionResultList.getName(), EnumOperation.APPLICANT_INSPECTION_RESULT_LIST)
            map.applicantInspectionResultList = applicantInspectionResultList
            map.showReceiveList = correspondenceListService.getCanReceiveList(applicantInspectionResultList)
            respond map
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def sendListModal = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            respond applicantInspectionResultList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def receiveListModal = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            respond applicantInspectionResultList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def closeListModal = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstance(params)
            respond applicantInspectionResultList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            respond applicantInspectionResultList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to return the list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.getInstanceWithRemotingValues(params)
            respond applicantInspectionResultList
        } else {
            notFound()

        }
    }

    /**
     * this action was added to add requests modal
     */
    def addRequestModal = {
        if (params.encodedId) {
            ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService?.getInstance(params)
            respond applicantInspectionResultList
        } else {
            notFound()
        }
    }

    /**
     * To add the request into list
     */
    def addRequestToList = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService?.addRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (applicantInspectionResultList.hasErrors()) {
                respond applicantInspectionResultList, view: 'sendList'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * To send the list to the receiving party
     */
    def sendList = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.sendData(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (applicantInspectionResultList.hasErrors()) {
                respond applicantInspectionResultList, view: 'sendData'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to receive the list from received party.
     */
    def receiveList = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (applicantInspectionResultList.hasErrors()) {
                respond applicantInspectionResultList, view: 'receiveListModal'
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of  list's request to Approved
     */
    def approveRequest = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.approveRequest(params)
        String successMessage = message(code: 'applicantInspectionResultList.requestApproved.message')
        String failMessage = message(code: 'applicantInspectionResultList.not.requestApproved.message')
        render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to change the status of  list's request to Rejected
     */
    def rejectRequest = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.rejectRequest(params)
        String successMessage = message(code: 'applicantInspectionResultList.requestRejected.message')
        String failMessage = message(code: 'applicantInspectionResultList.not.requestRejected.message')
        render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to close the  applicantInspectionResult list
     */
    def closeList = {
        ApplicantInspectionResultList applicantInspectionResultList = applicantInspectionResultListService.closeList(params)
        String successMessage = message(code: 'applicantInspectionResultList.closeList.message')
        String failMessage = message(code: 'applicantInspectionResultList.not.closeList.message')
        render text: (formatService.buildResponse(applicantInspectionResultList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * render the note list modal
     */
    def noteList = {
        return [id: params["id"]]
    }

    /**
     * render the create note modal
     */
    def noteCreate = {
        return [id: params["id"]]
    }
}

