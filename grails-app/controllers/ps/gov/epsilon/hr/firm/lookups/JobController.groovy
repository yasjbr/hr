package ps.gov.epsilon.hr.firm.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route Job requests between model and views.
 * @see JobService
 * @see FormatService
 * */
class JobController {

    JobService jobService
    FormatService formatService
    InspectionCategoryService inspectionCategoryService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {}

    def show = {
        if (params.encodedId) {
            Job job = jobService.getInstanceWithRemotingValues(params)
            if (job) {
                respond job
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def create = {
        respond new Job(params)
    }

    def filter = {
        PagedResultList pagedResultList = jobService.search(params)
        render text: (jobService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {
        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        Job job = jobService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'job.entity', default: 'Job'), job?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'job.entity', default: 'Job'), job?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(job, successMessage, failMessage, true, getControllerName(), "create") as JSON), contentType: "application/json"
        } else {
            if (job?.hasErrors()) {
                respond job, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            Job job = jobService.getInstanceWithRemotingValues(params)
            if (job) {
                job = removeMandatoryInspection(job)
                respond job
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    def update = {
        Job job = jobService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'job.entity', default: 'Job'), job?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'job.entity', default: 'Job'), job?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(job, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (job.hasErrors()) {
                respond job, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = jobService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'job.entity', default: 'Job'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'job.delete.label', default: 'job'), "", ""])

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
        render text: (jobService.autoComplete(params)), contentType: "application/json"
    }

    /*
    * to get the mandatory inspection
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


    private Job removeMandatoryInspection(Job job) {

        List<InspectionCategory> inspectionCategoriesList = []
        job?.joinedJobInspectionCategories?.each {
            InspectionCategory inspectionCategory = InspectionCategory?.findById(it.id)
            if (inspectionCategory) {
                if (!inspectionCategory.isRequiredByFirmPolicy) {
                    inspectionCategoriesList.add(inspectionCategory)
                }
            }
        }
        job.joinedJobInspectionCategories = null
        job.joinedJobInspectionCategories = inspectionCategoriesList.toSet()

        return job
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'job.entity', default: 'Job'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

