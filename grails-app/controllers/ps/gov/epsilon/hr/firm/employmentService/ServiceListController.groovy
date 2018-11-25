package ps.gov.epsilon.hr.firm.employmentService

import grails.converters.JSON
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.PagedList

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route ServiceList requests between model and views.
 * @see ServiceListService
 * @see FormatService
 * */
class ServiceListController {

    ServiceListService serviceListService
    FormatService formatService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
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
    def listEndOfServiceList = {
        respond sharedService.getAttachmentTypeListAsMap(ServiceList.getName(), EnumOperation.SERVICE_LIST)
    }

    /**
     * represent the list page
     */
    def listReturnToServiceList = {
        respond sharedService.getAttachmentTypeListAsMap(ServiceList.getName(), EnumOperation.SERVICE_LIST)
    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            ServiceList serviceList = serviceListService.getInstanceWithRemotingValues(params)
            if (serviceList) {
                respond serviceList
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
    def createEndOfServiceList = {
        ServiceList serviceList = new ServiceList()
        serviceList.serviceListType = EnumServiceListType.END_OF_SERVICE
        respond serviceList
    }

    /**
     * represent the create page empty instance
     */
    def createReturnToServiceList = {
        ServiceList serviceList = new ServiceList()
        serviceList.serviceListType = EnumServiceListType.RETURN_TO_SERVICE
        respond serviceList
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedList pagedList = serviceListService.searchWithRemotingValues(params)
        render text: (serviceListService.resultListToMap(pagedList, params) as JSON), contentType: "application/json"
    }

    /**
     * get parameters from page and save instance
     */
    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        ServiceList serviceList = serviceListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), serviceList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), serviceList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage, true, getControllerName(), "listEndOfServiceList") as JSON), contentType: "application/json"
        } else {
            if (serviceList?.hasErrors()) {
                respond serviceList, view: 'createReturnToServiceList'
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
            ServiceList serviceList = serviceListService.getInstance(params)
            if (serviceList && serviceList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond serviceList
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
        ServiceList serviceList = serviceListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), serviceList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), serviceList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (serviceList.hasErrors()) {
                respond serviceList, view: 'edit'
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
        DeleteBean deleteBean = serviceListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), params?.id, deleteBean.responseMessage ?: ""])
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
    def manageServiceList = {
        if (params.encodedId || params.id) {
            ServiceList serviceList = serviceListService?.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(ServiceList.getName(), EnumOperation.SERVICE_LIST)
            map.serviceList = serviceList
            map.showReceiveList = correspondenceListService.getCanReceiveList(serviceList)
            respond map
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def sendListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService?.getInstance(params)
            respond serviceList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def addRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService?.getInstance(params)
            respond serviceList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def addExceptionModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService?.getInstance(params)
            if (serviceList?.serviceListType == EnumServiceListType.RETURN_TO_SERVICE) {
                respond serviceList
            }
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def receiveListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService.getInstance(params)
            respond serviceList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def closeListModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService.getInstance(params)
            respond serviceList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService.getInstanceWithRemotingValues(params)
            respond serviceList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the service list instance to be used in modal
     */
    def rejectRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            ServiceList serviceList = serviceListService.getInstanceWithRemotingValues(params)
            respond serviceList
        } else {
            notFound()
        }
    }

    //to send the serviceList to the receiving party
    def sendList = {
        ServiceList serviceList = serviceListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (serviceList.hasErrors()) {
                respond serviceList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    //to receive the serviceList
    def receiveList = {
        ServiceList serviceList = serviceListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (serviceList.hasErrors()) {
                respond serviceList, view: 'receiveListModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * to change the status of applicant to be EMPLOYED
     */
    def changeRequestToApproved = {
        Map dataMap = serviceListService.changeRequestToApproved(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success = !errors;
            def errorFormat = msg.errorList(data: (errors), isCustom: "true");
            json.message = json.success ? msg.success(label: successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        } else {
            notFound()
        }
    }

    /**
     * to change the status of serviceList request to Rejected
     */
    def changeRequestToRejected = {
        Map dataMap = serviceListService.changeRequestToRejected(params)
        String successMessage = message(code: 'list.requestApproved.message')
        if (request.xhr) {
            Map json = [:]
            List<Map> errors = dataMap.errors
            json.success = !errors;
            def errorFormat = msg.errorList(data: (errors), isCustom: "true");
            json.message = json.success ? msg.success(label: successMessage) : errorFormat
            json.data = json.success ? dataMap.data : null
            json.errorList = !json.success ? errors : []
            render text: (json as JSON), contentType: "application/json"
        } else {
            notFound()
        }
    }

    /**
     * to send the serviceList to the receiving party
     */
    def closeList = {
        ServiceList serviceList = serviceListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (serviceList.hasErrors()) {
                respond serviceList, view: 'receiveListModal'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * add applicant to recruitment list instance
     */
    def addEmploymentServiceRequestToList = {
        ServiceList serviceList = serviceListService?.addEmploymentServiceRequestToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (serviceList.hasErrors()) {
                respond serviceList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    /**
     * add applicants as special case to the recruitment list
     */
    def addExceptionalToList = {
        ServiceList serviceList = serviceListService?.addExceptionalToList(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(serviceList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (serviceList.hasErrors()) {
                respond serviceList, view: 'sendList'
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
        return [id: params["id"]]
    }

    /**
     * render the create note modal!
     */
    def noteCreate = {
        return [id: params["id"]]
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'serviceList.entity', default: 'ServiceList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

