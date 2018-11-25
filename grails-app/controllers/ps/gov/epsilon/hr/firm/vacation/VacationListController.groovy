package ps.gov.epsilon.hr.firm.vacation

import grails.converters.JSON
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
 * Route VacationList requests between model and views.
 * @see VacationListService
 * @see FormatService
 * */
class VacationListController {

    VacationListService vacationListService
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
        respond sharedService.getAttachmentTypeListAsMap(VacationList.getName(), EnumOperation.VACATION_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                respond vacationList
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
        respond new VacationList(params)
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = vacationListService.searchWithRemotingValues(params)
        render text: (vacationListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        VacationList vacationList = vacationListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), vacationList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), vacationList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationList, successMessage, failMessage, true, getControllerName(), "manageVacationList") as JSON), contentType: "application/json"
        } else {
            if (vacationList?.hasErrors()) {
                respond vacationList, view: 'create'
                return
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
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList && vacationList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond vacationList
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
        VacationList vacationList = vacationListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), vacationList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), vacationList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacationList.hasErrors()) {
                respond vacationList, view: 'edit'
                return
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
        DeleteBean deleteBean = vacationListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), params?.id, ""])
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
    def manageVacationList = {
        if (params.encodedId || params.id) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                Map map = sharedService.getAttachmentTypeListAsMap(VacationList.getName(), EnumOperation.VACATION_LIST)
                map.vacationList = vacationList
                map.showReceiveList = correspondenceListService.getCanReceiveList(vacationList)
                respond map
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * this action is used to add vacation  request to vacation  list
     */
    def addVacationRequestsModal = {
        if (params.id) {
            Map map = [id: params["id"]]
            respond map
        } else {
            render ""
        }
    }

    /**
     * add vacationRequest to vacation list instance
     */
    def addVacationRequests = {
        VacationList vacationList = vacationListService.addVacationRequests(params)
        String successMessage = message(code: 'vacationList.addVacationRequest.message')
        String failMessage = message(code: 'vacationList.not.addVacationRequest.message')
        render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of vacation list.
     */
    def sendDataModal = {
        if (params.id) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                Map map = [vacationList: vacationList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to send the vacation list to the receiving party
     */
    def sendData = {
        VacationList vacationList = vacationListService.sendData(params)
        String successMessage = message(code: 'vacationList.sendData.message')
        String failMessage = message(code: 'vacationList.not.sendData.message')
        render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of vacation list.
     */
    def receiveDataModal = {
        if (params.id) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                Map map = [vacationList: vacationList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * receive vacation list from receiving party
     */
    def receiveData = {
        VacationList vacationList = vacationListService.receiveData(params)
        String successMessage = message(code: 'vacationList.saveReceivedForm.message')
        String failMessage = message(code: 'vacationList.not.saveReceivedForm.message')
        render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of vacation list.
     */
    def approveRequestModal = {
        if (params.id) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                Map map = [vacationList: vacationList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of vacation list request to Approved
     */
    def changeRequestToApproved = {
        VacationList vacationList = vacationListService.approveVacationRequest(params)
        String successMessage = message(code: 'vacationList.requestApproved.message')
        String failMessage = message(code: 'vacationList.not.requestApproved.message')
        render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of vacation list.
     */
    def rejectRequestModal = {
        if (params.id) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                Map map = [vacationList: vacationList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to change the status of vacation list request to Rejected
     */
    def rejectRequest = {
        VacationList vacationList = vacationListService.changeVacationRequestToRejected(params)
        String successMessage = message(code: 'vacationList.requestRejected.message')
        String failMessage = message(code: 'vacationList.not.requestRejected.message')
        render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * to show details of vacation list.
     */
    def closeModal = {
        if (params.id) {
            VacationList vacationList = vacationListService.getInstance(params)
            if (vacationList) {
                Map map = [vacationList: vacationList]
                respond map
            } else {
                render ""
            }
        } else {
            render ""
        }
    }

    /**
     * to close the  vacation list
     */
    def closeList = {
        VacationList vacationList = vacationListService.closeList(params);
        String successMessage = message(code: 'vacationList.closeList.message')
        String failMessage = message(code: 'vacationList.not.closeList.message')
        render text: (formatService.buildResponse(vacationList, successMessage, failMessage) as JSON), contentType: "application/json"
    }

    /**
     * render the note list modal!
     */
    def noteList = {
        return [id: params["id"]]
    }

    /**
     * render the create note modal!
     */
    def noteCreate = {
        return [id: params["id"]]
    }

    /* to handle requests if object not found.
    * @return void
    */

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacationList.entity', default: 'VacationList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

