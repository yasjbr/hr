package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route traineeList requests between model and views.
 * @see traineeListService
 * @see FormatService
 * */
class TraineeListController {

    TraineeListService traineeListService
    FormatService formatService
    ApplicantService applicantService
    TraineeListEmployeeService traineeListEmployeeService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    SharedService sharedService
    CorrespondenceListService correspondenceListService

    static allowedMethods = [save: "POST", update: "POST"]

    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(TraineeList.getName(), EnumOperation.TRAINEE_LIST)
    }

    def show = {
        if (params.encodedId) {
            TraineeList traineeList = traineeListService.getInstanceWithRemotingValues(params)
            if (traineeList) {
                respond traineeList
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new TraineeList(params)
    }

    def filter = {
        PagedList pagedResultList = traineeListService.searchWithRemotingValues(params)
        render text: (traineeListService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }


    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        TraineeList traineeList = traineeListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), traineeList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), traineeList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage, true, getControllerName(), "manageTraineeList") as JSON), contentType: "application/json"
        } else {
            if (traineeList?.hasErrors()) {
                respond traineeList, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            TraineeList traineeList = traineeListService.getInstanceWithRemotingValues(params)
            //allow edit when have CREATED status only
            if (traineeList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond traineeList
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        TraineeList traineeList = traineeListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), traineeList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), traineeList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (traineeList.hasErrors()) {
                respond traineeList, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = traineeListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), params?.id, deleteBean.responseMessage ?: ""])
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

    //filter applicant list with status ACCEPTED
    def filterApplicantToAdd = {
        params["traineeList.id"] = params['traineeListId']
        params["personName"] = params['personNameToAdd']
        params["vacancy.id"] = params['vacancy.idToAdd']
        params["applyingDate"] = params['applyingDateToAdd']
        params["age"] = params['ageToAdd']
        params.domainColumnName = "DOMAIN_TAB_COLUMNS"
        //to slow 10 recodes per view
        int max = params.int("max") ?: 10
        params.max = Integer.MAX_VALUE
        PagedResultList traineeListPagedResultList = traineeListEmployeeService.search(params)
        if (traineeListPagedResultList) {
            //to exclude all records of Applicant that added to traineeList from the Add Applicant data tables
            params["excludedIdsInTrainee"] = traineeListPagedResultList?.applicant?.id
        }
        //define params applicantCurrentStatusValue to ACCEPTED to filter Applicant related to this status
        params.applicantCurrentStatusValue = EnumApplicantStatus.ACCEPTED
        //reset the max value to return all records that added to traineeList
        params.max = max
        PagedResultList pagedResultList = applicantService.searchWithRemotingValues(params)
        render text: (applicantService.resultListToMap(pagedResultList, params, applicantService.DOMAIN_TAB_COLUMNS) as JSON), contentType: "application/json"
    }

    //filter applicants in the list, all applicants status not ACCEPTED
    def filterApplicantToAddAsExceptional = {
        //to slow 10 recodes per view
        PagedResultList traineeListPagedResultList = traineeListEmployeeService.search(params)

        if (traineeListPagedResultList) {
            //to exclude all records of Applicant that added to traineeList from the Add Exceptional Applicant data tables
            params["excludedIdsInTrainee"] = traineeListPagedResultList?.applicant?.id
        }
        params.filterApplicantToAddAsException = true
        params.personName = params.personNameException
        params["vacancy.id"] = params.long("vacancyException.id")
        params.age = params.ageException
        params.applyingDate = params.applyingDateException
        params.domainColumnName = "DOMAIN_TAB_COLUMNS"
        //reset the max value to return all records that added to traineeList
        PagedResultList pagedResultList = applicantService.searchWithRemotingValues(params)
        render text: (applicantService.resultListToMap(pagedResultList, params, applicantService.DOMAIN_TAB_COLUMNS) as JSON), contentType: "application/json"
    }

    //filter trainee list employee that added in trainee list instance
    def filterApplicant = {
        params.domainName = "applicantService.DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS"
        PagedResultList pagedResultList = traineeListEmployeeService.searchWithRemotingValues(params)
        PagedList customPagedList = new PagedList()
        customPagedList.resultList = pagedResultList?.applicant
        customPagedList.totalCount = pagedResultList!=null?pagedResultList.totalCount:0
        render text: (applicantService.resultListToMap(customPagedList, params, applicantService.DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS) as JSON), contentType: "application/json"
    }


    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageTraineeList = {
        if (params.encodedId || params.id) {
            TraineeList traineeList = traineeListService?.getInstanceWithRemotingValues(params)
            Map map = sharedService.getAttachmentTypeListAsMap(TraineeList.getName(), EnumOperation.TRAINEE_LIST)
            map.traineeList = traineeList
            map.showReceiveList = correspondenceListService.getCanReceiveList(traineeList)
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
            TraineeList traineeList = traineeListService?.getInstanceWithRemotingValues(params)
            respond traineeList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def addEligibleApplicantsModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            TraineeList traineeList = traineeListService?.getInstanceWithRemotingValues(params)
            respond traineeList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def addExceptionApplicantsModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            TraineeList traineeList = traineeListService?.getInstanceWithRemotingValues(params)
            respond traineeList
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
            TraineeList traineeList = traineeListService.getInstanceWithRemotingValues(params)
            respond traineeList
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
            TraineeList traineeList = traineeListService.getInstance(params)
            respond traineeList
        } else {
            notFound()
        }
    }

    /**
     * this action was added to return the promotion list instance to be used in modal
     */
    def approveRequestModal = {
        if (params.id) {
            params.encodedId = params.remove("id")
            TraineeList traineeList = traineeListService.getInstanceWithRemotingValues(params)
            respond traineeList
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
            TraineeList traineeList = traineeListService.getInstanceWithRemotingValues(params)
            respond traineeList
        } else {
            notFound()
        }
    }

    //to send the traineeList to the receiving party
    def sendList = {
        TraineeList traineeList = traineeListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (traineeList.hasErrors()) {
                respond traineeList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    //to receive the traineeList
    def receiveList = {
        TraineeList traineeList = traineeListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (traineeList.hasErrors()) {
                respond traineeList, view: 'receiveListModal'
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
    def changeApplicantToTrainingPassed = {
        Map dataMap = traineeListService.changeApplicantToTrainingPassed(params)
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
     * to change the status of traineeList request to Rejected
     */
    def changeApplicantToRejected = {
        Map dataMap = traineeListService.changeApplicantToRejected(params)
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
     * to send the traineeList to the receiving party
     */
    def closeList = {
        TraineeList traineeList = traineeListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (traineeList.hasErrors()) {
                respond traineeList, view: 'receiveListModal'
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
    def addApplicants = {
        TraineeList traineeList = traineeListService?.addApplicants(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (traineeList.hasErrors()) {
                respond traineeList, view: 'sendList'
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
    def addExceptionalApplicants = {
        TraineeList traineeList = traineeListService?.addApplicants(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(traineeList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (traineeList.hasErrors()) {
                respond traineeList, view: 'sendList'
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
     * return the applicantId to be used in cancel button click
     * return traineeListEmployee.id to be used in save note
     */
    def noteCreate = {
        return [id: params["id"]]
    }

    def inspectionList = {
        return ['id': params["id"]]
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'traineeList.entity', default: 'traineeList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

