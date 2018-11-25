package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.InspectionCategoryService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import javax.servlet.http.HttpServletRequest

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 * <h1>Purpose</h1>
 * Route Vacancy requests between model and views.
 * @see VacancyService
 * @see FormatService
 * */
class VacancyController {

    VacancyService vacancyService
    FormatService formatService
    RecruitmentCycleService recruitmentCycleService
    InspectionCategoryService inspectionCategoryService
    JobRequisitionService jobRequisitionService
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService
    SharedService sharedService

    static allowedMethods = [save: "POST", update: "POST"]


    def index = {
        redirect action: "list", method: "GET"
    }

    def list = {
        respond sharedService.getAttachmentTypeListAsMap(Vacancy.getName(), EnumOperation.VACANCY)
    }

    def show = {
        if (params.encodedId) {
            Vacancy vacancy = vacancyService?.getInstanceWithRemotingValues(params)
            if (vacancy) {
                respond vacancy
            } else {
                notFound()
            }
        } else {
            notFound()
        }

    }


    def create = {
        //create inspectionCategoriesList and after that create new jobRequisition
        List inspectionCategoriesList = inspectionCategoryService?.search(params)
        Map data = [vacancy                 : new Vacancy(params),
                    inspectionCategoriesList: inspectionCategoriesList]
        respond data
    }

    //to get mandatory inspection when create vacancy
    def getMandatoryInspection = {
        params["isRequiredByFirmPolicy"] = true
        params["allInspectionCategory"] = true
        PagedResultList inspectionCategoriesList = inspectionCategoryService?.search(params)
        def inspections = []
        inspectionCategoriesList.collect().each {
            inspections.add([id: it.id, text: it.descriptionInfo.localName])
        }

        render text: (inspections as JSON)?.toString(), contentType: "application/json"
    }

    def filter = {
        //search with remoting values from core
        PagedResultList pagedResultList = vacancyService.search(params)
        render text: (vacancyService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    def save = {

        //todo: get the firm from params without need to use the session value in case the user is super admin
        params["firm.id"] = session.getAttribute("firmId")
        Vacancy vacancy = vacancyService.save(params)
        String successMessage = message(code: 'default.created.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), vacancy?.id])
        String failMessage = message(code: 'default.not.created.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), vacancy?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacancy, successMessage, failMessage, true, getControllerName(), "list") as JSON), contentType: "application/json"
        } else {
            if (vacancy?.hasErrors()) {
                respond vacancy, view: 'create'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def edit = {
        if (params.encodedId) {
            Vacancy vacancy = vacancyService.getInstanceWithRemotingValues(params)
            if (vacancy?.vacancyStatus != EnumVacancyStatus.POSTED) {
                //check the current phase:
                if (vacancy?.vacancyStatus in [ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.NEW]) {
                    vacancy = removeMandatoryInspection(vacancy)
                    Map data = [type: "edit", vacancy: vacancy]
                    return data
                } else {
                    //return error message that cycle phase is not allowed to be edited
                    flash.message = msg.error(label: messageSource.getMessage('vacancy.errorEditMessage.label'))
                    redirect(action: "list")
                }
            } else {
                notFound()
            }
        } else {
            notFound()
        }
    }

    /**
     * to remove mandatory inspection from vacancy
     * @return vacancy without mandatory inspection
     * */
    private Vacancy removeMandatoryInspection(Vacancy vacancy) {
        GrailsParameterMap inspectionCategoryParam = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        List<InspectionCategory> inspectionCategoriesList = []
        vacancy.inspectionCategories.each {
            inspectionCategoryParam["id"] = it.id
            InspectionCategory inspectionCategory = inspectionCategoryService?.getInstance(inspectionCategoryParam)
            if (inspectionCategory) {
                if (!inspectionCategory?.isRequiredByFirmPolicy) {
                    inspectionCategoriesList?.add(inspectionCategory)
                }
            }
            inspectionCategoryParam?.remove("id")
        }
        vacancy?.inspectionCategories = null
        vacancy?.inspectionCategories = inspectionCategoriesList?.toSet()
        return vacancy
    }

    def update = {
        Vacancy vacancy = vacancyService.save(params)
        String successMessage = message(code: 'default.updated.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), vacancy?.id])
        String failMessage = message(code: 'default.not.updated.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), vacancy?.id])
        if (request.xhr) {
            render text: (formatService.buildResponse(vacancy, successMessage, failMessage) as JSON), contentType: "application/json"
        } else {
            if (vacancy.hasErrors()) {
                respond vacancy, view: 'edit'
                return
            } else {
                flash.message = msg.success(label: successMessage)
                redirect(action: "list")
            }
        }
    }

    def delete = {
        DeleteBean deleteBean = vacancyService.delete(PCPUtils.convertParamsToDeleteBean(params, "encodedId"), true)
        String successMessage = message(code: 'default.deleted.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), params?.id])
        String failMessage = message(code: 'default.not.deleted.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), params?.id, deleteBean.responseMessage ?: ""])
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
        render text: (vacancyService.autoComplete(params)), contentType: "application/json"
    }

    // auto complete to get all recruitment cycle that requisitionAnnouncementStatus is VACANCY
    def autoCompleteVacancyRecruitmentCycle = {
        params.requisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.VACANCY.toString()
        render text: (recruitmentCycleService?.autoComplete(params)), contentType: "application/json"
    }

    //to get the information of job requisition to copy this information when create the vacancy
    def getJobRequisitionInfo = {
        params["isRequiredByFirmPolicy"] = true
        params["allInspectionCategory"] = true
        PagedResultList inspectionCategoriesList = inspectionCategoryService?.search(params)
        def inspections = []
        inspectionCategoriesList.collect().each {
            inspections.add([id: it.id, text: it.descriptionInfo.localName])
        }



        params.id = params.check_jobRequisitionTable1
        JobRequisition jobRequisitionInstance = jobRequisitionService?.getInstanceWithRemotingValues(params)
        if (jobRequisitionInstance) {
            //get requisitionWorkExperiences information as a list
            List myResult = []
            Map dataRequisition = [:]
            jobRequisitionInstance?.requisitionWorkExperiences?.eachWithIndex { requisitionWork, index ->
                //get the  professionTypeName
                dataRequisition.professionTypeName = requisitionWork?.workExperience?.transientData?.professionTypeName
                dataRequisition.professionTypeId = requisitionWork?.workExperience?.professionType
                //get the competencyName
                dataRequisition.competencyName = requisitionWork?.workExperience?.transientData?.competencyName
                dataRequisition.competencyId = requisitionWork?.workExperience?.competency
                //get the periodInYears
                dataRequisition.periodInYears = requisitionWork?.periodInYears
                //get the otherSpecifications
                dataRequisition.otherSpecifications = requisitionWork?.otherSpecifications
                //put the information in the myResult list
                myResult << dataRequisition
                dataRequisition = [:]
            }
            //get jobRequisitionInstance data
            def map = [id                              : jobRequisitionInstance?.id,
                       recruitmentCycleId              : jobRequisitionInstance?.recruitmentCycle?.id,
                       recruitmentCycleName            : jobRequisitionInstance?.recruitmentCycle?.name,

                       requestedForDepartmentId        : jobRequisitionInstance?.requestedForDepartment?.id,
                       requestedForDepartmentName      : jobRequisitionInstance?.requestedForDepartment?.descriptionInfo?.localName,

                       jobId                           : jobRequisitionInstance?.job?.id,
                       jobName                         : jobRequisitionInstance?.job?.descriptionInfo?.localName,

                       jobTypeId                       : jobRequisitionInstance?.jobType?.id,
                       jobTypeName                     : jobRequisitionInstance?.jobType.descriptionInfo?.localName,

                       governorates                    : jobRequisitionInstance?.governorates,
                       governorateMapList              : jobRequisitionInstance?.transientData?.governorateMapList?.collect {
                           it?.get(1)
                       },

                       fromGovernorates                : jobRequisitionInstance?.fromGovernorates,
                       fromGovernorateMapList          : jobRequisitionInstance?.transientData?.fromGovernorateMapList?.collect {
                           it?.get(1)
                       },

                       educationDegrees                : jobRequisitionInstance?.educationDegrees,
                       educationDegreeMapList          : jobRequisitionInstance?.transientData?.educationDegreeMapList?.collect {
                           it?.get(1)
                       },

                       educationMajors                 : jobRequisitionInstance?.educationMajors,
                       educationMajorMapList           : jobRequisitionInstance?.transientData?.educationMajorMapList?.collect {
                           it?.get(1)
                       },

                       numberOfPositions               : jobRequisitionInstance?.numberOfPositions,

                       proposedRankId                  : jobRequisitionInstance?.proposedRank?.id ? jobRequisitionInstance?.proposedRank?.id : '',
                       proposedRankName                : jobRequisitionInstance?.proposedRank?.descriptionInfo?.localName ? jobRequisitionInstance?.proposedRank?.descriptionInfo?.localName : '',

                       inspectionCategoriesId          : jobRequisitionInstance?.inspectionCategories?.id,
                       inspectionCategoriesName        : jobRequisitionInstance?.inspectionCategories?.descriptionInfo?.localName,
                       inspectionCategoriesRequiredName: jobRequisitionInstance?.inspectionCategories?.findAll {
                           it?.isRequiredByFirmPolicy == true
                       }?.descriptionInfo?.localName,

                       jobDescription                  : jobRequisitionInstance?.jobDescription,

                       fromAge                         : jobRequisitionInstance?.fromAge,
                       toAge                           : jobRequisitionInstance?.toAge,

                       fromTall                        : jobRequisitionInstance?.fromHeight,
                       toTall                          : jobRequisitionInstance?.toHeight,

                       fromWeight                      : jobRequisitionInstance?.fromWeight,
                       toWeight                        : jobRequisitionInstance?.toWeight,

                       maritalStatusId                 : jobRequisitionInstance?.maritalStatusId ? jobRequisitionInstance?.maritalStatusId : '',
                       maritalStatusName               : jobRequisitionInstance?.transientData?.maritalStatusName ? jobRequisitionInstance?.transientData?.maritalStatusName : '',

                       note                            : jobRequisitionInstance?.note,

                       requisitionWorkExperiences      : myResult,
                       sexTypeAccepted                 : jobRequisitionInstance?.sexTypeAccepted ? EnumSexAccepted.valueOf(jobRequisitionInstance?.sexTypeAccepted?.toString()) : ''
            ]
            map.inspectionCategoriesRequiredName = map.inspectionCategoriesRequiredName - map.inspectionCategoriesName
            render text: ([data: map] as JSON), contentType: "application/json"
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    /**
     * the action used to render modal
     */
    def getTheSameJobRequisitionName = {
        return [:]
    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'vacancy.entity', default: 'Vacancy'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

