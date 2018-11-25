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
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route RecruitmentList requests between model and views.
 * @see RecruitmentListService
 * @see FormatService
 * */
class RecruitmentListController {

    RecruitmentListService recruitmentListService
    FormatService formatService
    ApplicantService applicantService
    RecruitmentListEmployeeService recruitmentListEmployeeService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    SharedService sharedService
    CorrespondenceListService correspondenceListService


    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(RecruitmentList.getName(), EnumOperation.RECRUITMENT_LIST)

    }

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            RecruitmentList recruitmentList = recruitmentListService.getInstance(params)
            if (recruitmentList) {
                respond recruitmentList
                return
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new RecruitmentList(params)
    }

    def filter = {
        PagedList pagedResultList = recruitmentListService.searchWithRemotingValues(params)
        render text: (recruitmentListService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }


    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        RecruitmentList recruitmentList = recruitmentListService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), recruitmentList?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), recruitmentList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage, true, getControllerName(), "manageRecruitmentList") as JSON), contentType: "application/json"
        } else {
            if (recruitmentList?.hasErrors()) {
                respond recruitmentList, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            RecruitmentList recruitmentList = recruitmentListService.getInstance(params)
            //allow edit when have CREATED status only
            if (recruitmentList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.CREATED) {
                respond recruitmentList
                return
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        RecruitmentList recruitmentList = recruitmentListService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), recruitmentList?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), recruitmentList?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentList.hasErrors()) {
                respond recruitmentList, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = recruitmentListService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), params?.id, deleteBean.responseMessage ?: ""])
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

    //return traineeList encodedId
    public getRecruitmentListId = { formatService, Applicant rec, object, params ->
        return HashHelper.encode(params['recruitmentList.id'] + "")
    }

    //filter applicant list with status ACCEPTED
    def filterApplicantToAdd = {
        params["recruitmentList.id"] = params['recruitmentListId']
        params["personName"] = params['personNameToAdd']
        params["vacancy.id"] = params['vacancy.idToAdd']
        params["applyingDate"] = params['applyingDateToAdd']
        params["age"] = params['ageToAdd']
        params.domainColumnName = "DOMAIN_TAB_COLUMNS"
        //to slow 10 recodes per view
        int max = params.int("max") ?: 10
        params.max = Integer.MAX_VALUE
        PagedResultList recruitmentListPagedResultList = recruitmentListEmployeeService.search(params, false)
        if (recruitmentListPagedResultList) {
            //to exclude all records of Applicant that added to traineeList from the Add Applicant data tables
            params["excludedIdsInRecruitment"] = recruitmentListPagedResultList?.applicant?.id
        }
        //define params applicantCurrentStatusValue to ACCEPTED to filter Applicant related to this status
        params.applicantCurrentStatusValue = EnumApplicantStatus.TRAINING_PASSED
        //reset the max value to return all records that added to traineeList
        params.max = max
        PagedResultList pagedResultList = applicantService.searchWithRemotingValues(params)
        render text: (applicantService.resultListToMap(pagedResultList, params, applicantService.DOMAIN_TAB_COLUMNS) as JSON), contentType: "application/json"
    }

    //filter applicants in the list, all applicants status not ACCEPTED
    def filterApplicantToAddAsExceptional = {
        params["recruitmentList.id"] = params['recruitmentListId']
        //to slow 10 recodes per view
        int max = params.int("max") ?: 10
        params.max = Integer.MAX_VALUE
        PagedResultList recruitmentListPagedResultList = recruitmentListEmployeeService.search(params, false)
        if (recruitmentListPagedResultList) {
            //to exclude all records of Applicant that added to traineeList from the Add Exceptional Applicant data tables
            params["excludedIdsInRecruitment"] = recruitmentListPagedResultList?.applicant?.id
        }
        params.filterApplicantToAddAsException = true
        params.personName = params.personNameException
        params["vacancy.id"] = params.long("vacancyException.id")
        params.age = params.ageException
        params.applyingDate = params.applyingDateException
        params.domainColumnName = "DOMAIN_TAB_COLUMNS"
        //reset the max value to return all records that added to traineeList
        params.max = max
        PagedResultList pagedResultList = applicantService.searchWithRemotingValues(params)
        render text: (applicantService.resultListToMap(pagedResultList, params, applicantService.DOMAIN_TAB_COLUMNS) as JSON), contentType: "application/json"
    }

    //filter recruitment list employee that added in recruitment list instance
    def filterApplicant = {
        params.domainName = "applicantService.DOMAIN_TAB_CUSTOM_COLUMNS"
        PagedResultList pagedResultList = recruitmentListEmployeeService.search(params, false)
        PagedList customPagedList = new PagedList()
        customPagedList.resultList = pagedResultList.applicant
        customPagedList.totalCount = pagedResultList.totalCount
        render text: (applicantService.resultListToMap(customPagedList, params, applicantService.DOMAIN_TAB_CUSTOM_COLUMNS) as JSON), contentType: "application/json"
    }

    /**
     * this action was added to manage the list itself, will return the list instance
     */
    def manageRecruitmentList = {
        if (params.encodedId || params.id) {
            RecruitmentList recruitmentList = recruitmentListService?.getInstance(params)
            Map map = sharedService.getAttachmentTypeListAsMap(RecruitmentList.getName(), EnumOperation.RECRUITMENT_LIST)
            map.recruitmentList = recruitmentList
            map.showReceiveList = correspondenceListService.getCanReceiveList(recruitmentList)
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
            RecruitmentList recruitmentList = recruitmentListService?.getInstance(params)
            respond recruitmentList
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
            RecruitmentList recruitmentList = recruitmentListService?.getInstance(params)
            respond recruitmentList
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
            RecruitmentList recruitmentList = recruitmentListService?.getInstance(params)
            respond recruitmentList
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
            RecruitmentList recruitmentList = recruitmentListService.getInstance(params)
            respond recruitmentList
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
            RecruitmentList recruitmentList = recruitmentListService.getInstance(params)
            respond recruitmentList
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
            RecruitmentList recruitmentList = recruitmentListService.getInstanceWithRemotingValues(params)
            respond recruitmentList
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
            RecruitmentList recruitmentList = recruitmentListService.getInstanceWithRemotingValues(params)
            respond recruitmentList
        } else {
            notFound()
        }
    }

    //to send the recruitmentList to the receiving party
    def sendList = {
        RecruitmentList recruitmentList = recruitmentListService.sendList(params)
        String successMessage = message(code: 'list.sent.message')
        String failMessage = message(code: 'list.not.sent.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentList.hasErrors()) {
                respond recruitmentList, view: 'sendList'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    //to receive the recruitmentList
    def receiveList = {
        RecruitmentList recruitmentList = recruitmentListService.receiveList(params)
        String successMessage = message(code: 'list.receive.message')
        String failMessage = message(code: 'list.not.receive.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentList.hasErrors()) {
                respond recruitmentList, view: 'receiveListModal'
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
    def changeApplicantToEmployed = {
        Map dataMap = recruitmentListService.changeApplicantToEmployed(params)
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
     * to change the status of recruitmentList request to Rejected
     */
    def changeApplicantToRejected = {
        Map dataMap = recruitmentListService.changeApplicantToRejected(params)
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
     * to send the recruitmentList to the receiving party
     */
    def closeList = {
        RecruitmentList recruitmentList = recruitmentListService.closeList(params)
        String successMessage = message(code: 'list.closeList.message')
        String failMessage = message(code: 'list.not.closeList.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentList.hasErrors()) {
                respond recruitmentList, view: 'receiveListModal'
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
        RecruitmentList recruitmentList = recruitmentListService?.addApplicants(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentList.hasErrors()) {
                respond recruitmentList, view: 'sendList'
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
        RecruitmentList recruitmentList = recruitmentListService?.addApplicants(params)
        String successMessage = message(code: 'list.addRequest.message')
        String failMessage = message(code: 'list.not.addRequest.message')
        if (request.xhr) {
            render text: (formatService.buildResponse(recruitmentList, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (recruitmentList.hasErrors()) {
                respond recruitmentList, view: 'sendList'
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
        GrailsParameterMap newParams =  new GrailsParameterMap(["applicant.id": params.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        RecruitmentListEmployee recruitmentListEmployee = recruitmentListEmployeeService.search(newParams).resultList.get(0)
        return [recruitmentListEmployeeId: recruitmentListEmployee?.id, applicantId:params.id]
    }

    /**
     * render the create note modal!
     * return the applicantId to be used in cancel button click
     * return recruitmentListEmployee.id to be used in save note
     */
    def noteCreate = {
        GrailsParameterMap newParams =  new GrailsParameterMap(["applicant.id": params.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        RecruitmentListEmployee recruitmentListEmployee = recruitmentListEmployeeService.search(newParams).resultList.get(0)
        return [recruitmentListEmployeeId: recruitmentListEmployee?.id, applicantId:params.id]
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'recruitmentList.entity', default: 'RecruitmentList'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

