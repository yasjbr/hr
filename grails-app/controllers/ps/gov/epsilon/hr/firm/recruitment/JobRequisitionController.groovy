package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.InspectionCategoryService
import ps.gov.epsilon.hr.firm.lookups.JobService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import java.time.ZonedDateTime
import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route JobRequisition requests between model and views.
 * @see JobRequisitionService
 * @see FormatService
 * */
class JobRequisitionController {

    JobRequisitionService jobRequisitionService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    FormatService formatService
    InspectionCategoryService inspectionCategoryService
    RecruitmentCycleService recruitmentCycleService
    JobService jobService
    SharedService sharedService
    def messageSource
    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        //for attachment
        respond sharedService.getAttachmentTypeListAsMap(JobRequisition.getName(), EnumOperation.JOB_REQUISITION)
    }

    // to list the job requisitions to firm manager
    def listManager = {
    }

    def saveRecord = {
        render text: (jobRequisitionService.addApprovedNumberOfPosition(params) as JSON), contentType: "application/json"
    }

    /**
     * return the job requisition instance with the inspection List to show requisition in manager list
     */
    def showManager = {
        if (params.encodedId) {
            //to check remoting values
            JobRequisition jobRequisitionInstance = jobRequisitionService.getInstanceWithRemotingValues(params)
            //  check if jobRequisitionInstance is available and then it its available return inspectionCategoriesList
            if (jobRequisitionInstance) {
                List inspectionCategoriesList = inspectionCategoryService?.search(params)
                Map data = [jobRequisition          : jobRequisitionInstance,
                            inspectionCategoriesList: inspectionCategoriesList]
                return data
            }
        } else {
            notFound()
        }
    }

    def show = {
        if (params.encodedId) {
            //to check remoting values
            JobRequisition jobRequisitionInstance = jobRequisitionService.getInstanceWithRemotingValues(params)
            //  check if jobRequisitionInstance is available and then it its available return inspectionCategoriesList
            if (jobRequisitionInstance) {
                List inspectionCategoriesList = inspectionCategoryService?.search(params)
                Map data = [jobRequisition          : jobRequisitionInstance,
                            inspectionCategoriesList: inspectionCategoriesList]
                respond data
            }
        } else {
            notFound()
        }
    }

    /**
     * this action used to return the mandatory inspection to be shown in drop down list as required
     */
    def getMandatoryInspection = {
        params["isRequiredByFirmPolicy"] = true
        params["allInspectionCategory"] = true
        PagedResultList inspectionCategoriesList = inspectionCategoryService.search(params)
        def inspections = []
        inspectionCategoriesList.collect().each {
            inspections.add([id: it.id, text: it.descriptionInfo.localName])
        }
        render text: (inspections as JSON)?.toString(), contentType: "application/json"
    }

    def create = {
        Map data = [type: "create", jobRequisition: new JobRequisition(params), currentDate: ZonedDateTime.now()]
        respond data
    }

    def filter = {
        PagedResultList pagedResultList = jobRequisitionService.searchWithRemotingValues(params)
        render text: (jobRequisitionService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def filterManager = {
        params.withRecruitmentCycle = true
        params.filterManager = true
        PagedResultList pagedResultList = jobRequisitionService.searchWithRemotingValues(params)
        render text: (jobRequisitionService.resultListToMap(pagedResultList, params, jobRequisitionService.DOMAIN_MANAGER_COLUMNS) as JSON), contentType: "application/json"
    }

    /**
     * filter the job requisition with no recruitment cycle, this action was added to be used in recruitment cycle page
     */
    def filterJobRequisitionToAdd = {
        params.withNoRecruitmentCycle = true
        params.remove("recruitmentCycle.id")
        PagedResultList pagedResultList = jobRequisitionService.searchWithRemotingValues(params)
        render text: (jobRequisitionService.resultListToMap(pagedResultList, params, jobRequisitionService.DOMAIN_COLUMNS_FOR_RECRUITMENT_CYCLE) as JSON), contentType: "application/json"
    }

    private Date getDateWithoutTime(Date date) {
        Date result = date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        result = calendar.getTime();
        return result
    }

    def save = {
        params["firm.id"] = session.getAttribute("firmId")
        params["requestedByDepartment.id"] = session.getAttribute("departmentId")
        boolean isInvalidReq = false
        // validate date (date should be less or equal current date)
        //else, return error
        if (params['requestDate']) {
            ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
            if (requestDate > ZonedDateTime.now()) {
                isInvalidReq = true
                String errorMessage = message(code: 'jobRequisition.save.invalid.date.label')
                Map json = [:]
                json.success = false
                json.data = null
                json.message = "<div class='alert alert-block alert-danger'>" +
                        "<button data-dismiss='alert' class='close' type='button'>" +
                        "<i class='ace-icon fa fa-times'>" +
                        "</i>" +
                        "</button>" +
                        "<ul>" +
                        "<li>" + errorMessage + "</li> " +
                        "</ul>" +
                        "</div>"
                render text: (json as JSON), contentType: "application/json"
            }
        }
        if (SpringSecurityUtils?.ifNotGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {
            //set the requestedForDepartment from the session, it means the department ask a requisition for itself
            params["requestedForDepartment.id"] = session.getAttribute("departmentId")
        }
        if (!isInvalidReq) {
            JobRequisition jobRequisition = jobRequisitionService.save(params)
            String successMessage = message(code: 'default.created.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), jobRequisition?.id])
            String failMessage = message(code: 'default.not.created.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), jobRequisition?.id])
            if (request.xhr) {
                render text: (formatService.buildResponse(jobRequisition, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
            } else {
                if (jobRequisition?.hasErrors()) {
                    respond jobRequisition, view: 'create'
                    return
                } else {
                    flash.message = msg.success(label: successMessage)
                    redirect(action: "list")
                }
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            //to check remoting values
            JobRequisition jobRequisitionInstance = jobRequisitionService.getInstanceWithRemotingValues(params)
            //  check if jobRequisitionInstance is available and then it its available return inspectionCategoriesList
            if (jobRequisitionInstance?.requisitionStatus == EnumRequestStatus.CREATED) {
                jobRequisitionInstance = removeMandatoryInspection(jobRequisitionInstance)
                Map data = [type: "edit", jobRequisition: jobRequisitionInstance]
                respond data
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * to remove mandatory inspection from job requisition
     * @return job requisition  without mandatory inspection
     * */
    private JobRequisition removeMandatoryInspection(JobRequisition jobRequisition) {
        GrailsParameterMap inspectionCategoryParam = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        List<InspectionCategory> inspectionCategoriesList = []
        jobRequisition?.inspectionCategories?.each {
            inspectionCategoryParam["id"] = it.id
            InspectionCategory inspectionCategory = inspectionCategoryService?.getInstance(inspectionCategoryParam)
            if (inspectionCategory) {
                if (!inspectionCategory?.isRequiredByFirmPolicy) {
                    inspectionCategoriesList?.add(inspectionCategory)
                }
            }
            inspectionCategoryParam?.remove("id")
        }
        jobRequisition?.inspectionCategories = null
        jobRequisition?.inspectionCategories = inspectionCategoriesList?.toSet()
        return jobRequisition
    }


    def editManager = {
        if (params.encodedId) {
            //to check remoting values
            JobRequisition jobRequisitionInstance = jobRequisitionService?.getInstanceWithRemotingValues(params)
            //  check if jobRequisitionInstance is available and then it its available return inspectionCategoriesList
            if (jobRequisitionInstance) {
                List inspectionCategoriesList = inspectionCategoryService?.search(params)
                Map data = [jobRequisition          : jobRequisitionInstance,
                            inspectionCategoriesList: inspectionCategoriesList]
                return data
            }
        } else {
            notFound()
        }
    }

    def update = {
        boolean isInvalidReq = false
        // validate date (date should be less or equal current date)
        //else return error
        if (params['requestDate']) {
            ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
            if (requestDate > ZonedDateTime.now()) {
                isInvalidReq = true
                String errorMessage = message(code: 'jobRequisition.save.invalid.date.label')
                Map json = [:]
                json.success = false
                json.data = null
                json.message = "<div class='alert alert-block alert-danger'>" +
                        "<button data-dismiss='alert' class='close' type='button'>" +
                        "<i class='ace-icon fa fa-times'>" +
                        "</i>" +
                        "</button>" +
                        "<ul>" +
                        "<li>" + errorMessage + "</li> " +
                        "</ul>" +
                        "</div>"

                render text: (json as JSON), contentType: "application/json"
            }
        }
        if (!isInvalidReq) {
            JobRequisition jobRequisition = jobRequisitionService.save(params)
            String successMessage = message(code: 'default.updated.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), jobRequisition?.id])
            String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), jobRequisition?.id])
            if (request.xhr) {
                render text: (formatService.buildResponse(jobRequisition, successMessage, failMessage) as JSON), contentType: "application/json"
            } else {
                if (jobRequisition.hasErrors()) {
                    respond jobRequisition, view: 'edit'
                    return
                } else {
                    flash.message = msg.success(label: successMessage)
                    redirect(action: "list")
                }
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = jobRequisitionService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), params?.id, deleteBean.responseMessage ?: ""])

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

    def autocomplete = {
        render text: (jobRequisitionService?.autoComplete(params)), contentType: "application/json"
    }

    // auto complete to get all recruitment cycle that requisitionAnnouncementStatus is open
    def autoCompleteOpenedRecruitmentCycle = {
        params.requisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.OPEN.toString()
        if (SpringSecurityUtils?.ifNotGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)) {
            //in case not HR-Role, then filter the recruitment cycle depends on department
            params.departmentId = session.getAttribute("departmentId")
        }
        render text: (recruitmentCycleService?.autoComplete(params)), contentType: "application/json"
    }

    // to accept the all positions that numberOfPositions is equal to numberOfApprovedPositions
    def setApprovedPositions = {
        String failMessage = message(code: 'jobRequisition.not.jobRequisition.message')
        String rejectSuccessMessage = message(code: 'jobRequisition.rejectAllPositions.message')
        String approveSuccessMessage= message(code: 'jobRequisition.acceptPositions.message')
        def json = [:]

        JobRequisition jobRequisition = jobRequisitionService?.setApprovedPositions(params)
        if (!jobRequisition?.hasErrors()) {
            json.success = true
            if(jobRequisition?.requisitionStatus == EnumRequestStatus.APPROVED){
                flash.message = msg.success(label: approveSuccessMessage)
            }
            if(jobRequisition?.requisitionStatus == EnumRequestStatus.REJECTED){
                flash.message = msg.success(label: rejectSuccessMessage)
            }
        } else {
            json.success = false
        }
        json.message = json.success ? '' : msg.error(label: failMessage)

        render text: (json as JSON), contentType: "application/json"
    }

    //to get the job information from setting
    def getJobInformation = {
        params["isRequiredByFirmPolicy"] = true
        params["allInspectionCategory"] = true
        if (params.id) {
            def jobInstance = jobService?.getInstanceWithRemotingValues(params)
            params.remove("id")
            params['notIncluded'] = jobInstance?.joinedJobInspectionCategories?.inspectionCategory?.id?.toList()
            PagedResultList inspectionCategoriesList = inspectionCategoryService?.search(params)
            def inspections = []
            inspectionCategoriesList.collect().each {
                inspections.add([id: it.id, text: it.descriptionInfo.localName])
            }
            def militaryRanks = jobInstance?.joinedJobMilitaryRanks?.militaryRank?.id?.toList()
            def data = [:]
            data = [job: jobInstance, inspections: inspections, militaryRanks: militaryRanks]
            render text: (data as JSON), contentType: "application/json"
        }
    }

    /**
     * this action is used to show the previous work modal in create/update view
     */
    def previousWorkModal = {
    }

    /**
     * this action used to return job requisition - in manager list
     */
    def acceptFormModal = {
        if (params.id) {
            JobRequisition jobRequisition = jobRequisitionService.getInstance(params)
            if (jobRequisition) {
                respond jobRequisition
            }
        } else {
            notFound()
        }
    }

    /**
     * this action used to return job requisition - in manager list
     */
    def rejectFormModal = {
        if (params.id) {
            JobRequisition jobRequisition = jobRequisitionService.getInstance(params)
            if (jobRequisition) {
                respond jobRequisition
            }
        } else {
            notFound()
        }
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'jobRequisition.entity', default: 'JobRequisition'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

