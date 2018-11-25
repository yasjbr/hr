package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmploymentCategory
import ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.CompetencyService
import ps.police.pcore.v2.entity.lookups.EducationDegreeService
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.ProfessionTypeService
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationDegreeDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationMajorDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.MaritalStatusDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.ProfessionTypeDTO
import ps.police.pcore.v2.entity.person.lookups.dtos.v1.CompetencyDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service is used to manage the job requisition (add, edit, delete, manage)-
 * <h1>Usage</h1>
 * -add, edit, delete the job requisition-
 * -add the ability to file new job requisition for other department if the the user has HR-Admin role
 * <h1>Restriction</h1>
 * -delete when the record is new-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JobRequisitionService {

    MessageSource messageSource
    def formatService
    WorkExperienceService workExperienceService
    GovernorateService governorateService
    EducationMajorService educationMajorService
    EducationDegreeService educationDegreeService
    ProfessionTypeService professionTypeService
    CompetencyService competencyService
    MaritalStatusService maritalStatusService

    //to get the value of requisition status
    public static requisitionStatusValue = { cService, JobRequisition rec, object, params ->
        return rec?.requisitionStatus?.toString()
    }

    //to get the name of recruitment cycle
    public static recruitmentCycleLocalName = { cService, JobRequisition rec, object, params ->
        if (rec.recruitmentCycle) {
            return rec.recruitmentCycle?.name
        } else {
            return ""
        }
    }
    public static recruitmentCycleStatusLocalName = { cService, JobRequisition rec, object, params ->
        if (rec.recruitmentCycle) {
            return rec.recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus
        } else {
            return ""
        }
    }

    //to get the list name of governorates
    public static renderGovernorateNames = { cService, JobRequisition rec, object, params ->
        if (rec.governorates && rec.transientData.governorateMapList) {
            return rec.transientData.governorateMapList?.collect { it.governorateName }
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, editable: false, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "recruitmentCycle", type: recruitmentCycleLocalName, source: 'domain'],
            [sort: true, search: false, hidden: true, editable: false, name: "recruitmentCycleStatus", type: recruitmentCycleStatusLocalName, source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "requestedForDepartment", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "job.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "governorates", type: renderGovernorateNames, source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "numberOfApprovedPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "requisitionStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requisitionStatusValue", type: requisitionStatusValue, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "rejectionReason", type: "string", source: 'domain']
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_MANAGER_COLUMNS = [
            [sort: true, search: true, hidden: true, editable: false, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "recruitmentCycle", type: recruitmentCycleLocalName, source: 'domain'],
            [sort: true, search: false, hidden: true, editable: false, name: "recruitmentCycleStatus", type: recruitmentCycleStatusLocalName, source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "requestedForDepartment", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "job.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, editable: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "governorates", type: renderGovernorateNames, source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "numberOfApprovedPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, editable: false, name: "requisitionStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requisitionStatusValue", type: requisitionStatusValue, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "rejectionReason", type: "string", source: 'domain']
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS_FOR_RECRUITMENT_CYCLE = [
            [sort: true, search: true, hidden: true, editable: false, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, editable: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recruitmentCycle", type: recruitmentCycleLocalName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestedForDepartment", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "job.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "governorates", type: renderGovernorateNames, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numberOfApprovedPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requisitionStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requisitionStatusValue", type: requisitionStatusValue, source: 'domain'],
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        Integer sSearchNumber = params.int("sSearch")
        ZonedDateTime sSearchDate = PCPUtils.parseZonedDateTime(params["sSearch"])

        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }

        List<Map<String, String>> orderBy = params.list("orderBy")
        Set educationDegrees = params.listString("educationDegrees")
        Set educationMajors = params.listString("educationMajors")
        Long firmId = params.long("firm.id")
        Short fromAge = params.long("fromAge")
        Float fromHeight = params.long("fromHeight")
        Float fromWeight = params.long("fromWeight")
        ZonedDateTime fulfillFromDate = PCPUtils.parseZonedDateTime(params['fulfillFromDate'])
        ZonedDateTime fulfillToDate = PCPUtils.parseZonedDateTime(params['fulfillToDate'])
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['requestDateTo'])

        Long governorates = params.long("governorates")
        Set inspectionCategoriesIds = params.listString("inspectionCategories.id")
        ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted sexTypeAccepted = params["sexTypeAccepted"] ? ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted.valueOf(params["sexTypeAccepted"]) : null
        String jobDescription = params["jobDescription"]
        String jobId = params["job.id"] ?: (params["jobRequisitionIdTitle"] ? (JobRequisition.get(params["jobRequisitionIdTitle"])?.job?.id) : null)

        String jobTypeId = params["jobType.id"]
        String maritalStatusId = params["personMaritalStatus.id"]
        String note = params["note"]
        Integer numberOfApprovedPositions = params.int("numberOfApprovedPositions")
        Integer numberOfPositions = params.int("numberOfPositions")
        String proposedRankId = params["proposedRank.id"]
        String recruitmentCycleId = params["recruitmentCycle.id"]
        String requestedByDepartmentId = params["requestedByDepartment.id"]
        String requestedForDepartmentId = params["requestedForDepartment.id"]
        String rejectionReason = params["rejectionReason"]

        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requisitionStatus = params["requisitionStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requisitionStatus"]) : null
        Short toAge = params.long("toAge")
        Float toHeight = params.long("toHeight")
        Float toWeight = params.long("toWeight")
        Set workExperienceIds = params.listString("workExperience.id")
        Boolean withNoRecruitmentCycle = params.boolean(("withNoRecruitmentCycle"))
        Boolean withRecruitmentCycle = params.boolean(("withRecruitmentCycle"))
        EnumRequisitionAnnouncementStatus recruitmentCycleStatus = params["recruitmentCycle.status"]
        Boolean filterManager = params.boolean("filterManager")
        Boolean forShow = params.boolean("forShow")
        Boolean isSoldier = params.boolean("isSoldier")

        String recruitmentCycleIdTheSameName = params["recruitmentCycleId.theSameName"]
        String jobIdTheSameName = params["recruitmentCjobId.theSameName"]
        ZonedDateTime fromRequestDateTheSameName = PCPUtils.parseZonedDateTime(params['fromRequestDate.theSameName'])
        ZonedDateTime toRequestDateTheSameName = PCPUtils.parseZonedDateTime(params['toRequestDate.theSameName'])
        Integer numberOfPositionsTheSameName = params.int("numberOfPositionsForSearch.theSameName")
        Integer numberOfApprovedPositionsTheSameName = params.int("numberOfApprovedPositions.theSameName")
        String status = params["status"]

        //in case: search in job requisition in vacancy create form
        if (params.recruitmentCycleId) {
            recruitmentCycleId = params["recruitmentCycleId"]
        }
        if (params.jobId) {
            jobId = params["jobId"]
        }
        if (params.numberOfPositionsForSearch) {
            numberOfPositions = params.int("numberOfPositionsForSearch")
        }

        return JobRequisition.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                if (sSearch) {
                    or {
                        ilike("jobDescription", sSearch)
                        ilike("note", sSearch)
                        recruitmentCycle {
                            ilike("name", sSearch)
                        }
                        requestedForDepartment {
                            ilike("descriptionInfo.localName", sSearch)
                        }
                        job {
                            ilike("descriptionInfo.localName", sSearch)
                        }
                        if (sSearchNumber) {
                            eq("numberOfPositions", sSearchNumber)
                            eq("numberOfApprovedPositions", sSearchNumber)
                        }
                        if (sSearchDate) {
                            le('requestDate', sSearchDate)
                        }
                    }
                }
            }
            and {
                //requestDate
                if (fromRequestDateTheSameName) {
                    ge("requestDate", fromRequestDateTheSameName)
                }
                if (toRequestDateTheSameName) {
                    le("requestDate", toRequestDateTheSameName)
                }
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (educationDegrees) {
                    educationDegrees {
                        inList("id", educationDegrees)
                    }
                }
                if (educationMajors) {
                    educationMajors {
                        inList("id", educationMajors)
                    }
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))

                if (fromAge) {
                    eq("fromAge", fromAge)
                }

                //in case: get all job requisition that is having the same job title's name
                if (forShow) {
                    job {
                        descriptionInfo {
                            eq('localName', JobRequisition.load(params["jobRequisitionId"])?.job?.descriptionInfo?.localName)
                        }
                    }
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                if (fromHeight) {
                    eq("fromHeight", fromHeight)
                }
                if (fromWeight) {
                    eq("fromWeight", fromWeight)
                }
                if (fulfillFromDate) {
                    le("fulfillFromDate", fulfillFromDate)
                }
                if (requestDate) {
                    le("requestDate", requestDate)
                }
                if (fulfillToDate) {
                    le("fulfillToDate", fulfillToDate)
                }
                if (governorates) {
                    eq("governorates", governorates)
                }
                if (inspectionCategoriesIds) {
                    inspectionCategories {
                        inList("id", inspectionCategoriesIds)
                    }
                }
                if (sexTypeAccepted) {
                    eq("sexTypeAccepted", sexTypeAccepted)
                }
                if (jobDescription) {
                    ilike("jobDescription", "%${jobDescription}%")
                }
                if (jobId) {
                    eq("job.id", jobId)
                }

                if (jobIdTheSameName) {
                    eq("job.id", jobIdTheSameName)
                }
                if (maritalStatusId) {
                    eq("personMaritalStatus.id", maritalStatusId)
                }
                if (jobTypeId) {
                    eq("jobType.id", jobTypeId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (rejectionReason) {
                    ilike("rejectionReason", "%${rejectionReason}%")
                }
                if (numberOfApprovedPositions != null) {
                    if (numberOfApprovedPositions == 0) {
                        isNull("numberOfApprovedPositions")
                    } else if (numberOfApprovedPositions > 0) {
                        eq("numberOfApprovedPositions", numberOfApprovedPositions)
                    } else if (numberOfApprovedPositionsTheSameName) {
                        eq("numberOfApprovedPositions", numberOfApprovedPositionsTheSameName)
                    }
                }
                if (filterManager) {

                    ne("requisitionStatus", EnumRequestStatus.FINISHED)
                }
                if (numberOfPositions) {
                    eq("numberOfPositions", numberOfPositions)
                }

                if (numberOfPositionsTheSameName) {
                    eq("numberOfPositions", numberOfPositionsTheSameName)
                }
                if (proposedRankId) {
                    eq("proposedRank.id", proposedRankId)
                }
                if (recruitmentCycleId) {
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }

                if (recruitmentCycleIdTheSameName) {
                    eq("recruitmentCycle.id", recruitmentCycleIdTheSameName)
                }
                if (withNoRecruitmentCycle) {
                    isNull("recruitmentCycle")
                }
                if (withRecruitmentCycle) {
                    isNotNull("recruitmentCycle")
                }
                if (recruitmentCycleStatus) {
                    recruitmentCycle {
                        currentRecruitmentCyclePhase {
                            eq("requisitionAnnouncementStatus", recruitmentCycleStatus)
                        }
                    }
                }
                if (requestedByDepartmentId) {
                    eq("requestedByDepartment.id", requestedByDepartmentId)
                }
                if (requestedForDepartmentId) {
                    eq("requestedForDepartment.id", requestedForDepartmentId)
                }
                if (requisitionStatus) {
                    eq("requisitionStatus", requisitionStatus)
                }
                if (toAge) {
                    eq("toAge", toAge)
                }

                if (toHeight) {
                    eq("toHeight", toHeight)
                }
                if (toWeight) {
                    eq("toWeight", toWeight)
                }
                if (workExperienceIds) {
                    workExperience {
                        inList("id", workExperienceIds)
                    }
                }
                if (isSoldier != null) {
                    if (isSoldier == true) {
                        eq('employmentCategory.id', EnumEmploymentCategory.SOLDIER.value)
                    } else {
                        ne('employmentCategory.id', EnumEmploymentCategory.SOLDIER.value)
                    }
                }
            }
            //requestDate
            if (requestDateFrom) {
                ge("requestDate", requestDateFrom)
            }
            if (requestDateTo) {
                le("requestDate", requestDateTo)
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "requestedForDepartment":
                        requestedForDepartment {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
                        break;
                    case "job.descriptionInfo.localName":
                        job {
                            descriptionInfo {
                                order("localName", dir)
                            }
                        }
                        break;
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    default:
                        order(columnName, dir)
                        break;
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
    }

    /**
     * to get data for report.
     * @param GrailsParameterMap params the search map.
     * @return List < JobRequisition > .
     */
    @Transactional(readOnly = true)
    List getJobRequisitionReportData(GrailsParameterMap params) {
        PagedResultList resultList = this.search(params)
        List data = []
        Map map = [:]
        resultList.each { JobRequisition jobRequisition ->
            map = [:]
            map["jobTitleName"] = jobRequisition?.job?.descriptionInfo?.toString()
            map["numberOfPositions"] = jobRequisition?.numberOfPositions
            map["requestedForDepartmentName"] = jobRequisition?.requestedForDepartment?.descriptionInfo?.toString()
            map["recruitmentCycleName"] = jobRequisition?.recruitmentCycle?.name
            map["recruitmentCycleFromDate"] = jobRequisition?.recruitmentCycle?.currentRecruitmentCyclePhase?.fromDate?.format(PCPUtils.ZONED_DATE_FORMATTER)
            map["recruitmentCycleToDate"] = jobRequisition?.recruitmentCycle?.currentRecruitmentCyclePhase?.toDate?.format(PCPUtils.ZONED_DATE_FORMATTER)
            data << map
            map = null
        }
        return data
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return JobRequisition.
     */
    JobRequisition save(GrailsParameterMap params) {
        JobRequisition jobRequisitionInstance
        //if the id passed is encrypted:
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params["id"]) {
            jobRequisitionInstance = JobRequisition.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (jobRequisitionInstance.version > version) {
                    jobRequisitionInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('jobRequisition.label', null, 'jobRequisition', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this jobRequisition while you were editing")
                    return jobRequisitionInstance
                }
            }
            if (!jobRequisitionInstance) {
                jobRequisitionInstance = new JobRequisition()
                jobRequisitionInstance.errors.reject('default.not.found.message', [messageSource.getMessage('jobRequisition.label', null, 'jobRequisition', LocaleContextHolder.getLocale())] as Object[], "This jobRequisition with ${params.id} not found")
                return jobRequisitionInstance
            }
        } else {
            jobRequisitionInstance = new JobRequisition()
            jobRequisitionInstance.requisitionStatus = EnumRequestStatus.CREATED
        }
        try {
            //remove records from requisition work experience table when edit
            if (jobRequisitionInstance?.id) {
                List requisitionWorkExperienceIds = jobRequisitionInstance?.requisitionWorkExperiences?.id
                if (requisitionWorkExperienceIds) {
                    RequisitionWorkExperience.executeUpdate('delete from RequisitionWorkExperience a where a.jobRequisition.id =:jobRequisitionId', ['jobRequisitionId': jobRequisitionInstance?.id])
                }
            }
            jobRequisitionInstance.properties = params;

            //check if the RequisitionWorkExperience list contains data
            List professionTypeIds = params.list("professionType")
            List competencyIds = params.list("competency")
            List periodInYearsValues = params.listString("periodInYears")
            List<String> otherSpecificationsValues = params.list("otherSpecifications")

            jobRequisitionInstance.fulfillFromDate = ZonedDateTime.now()
            jobRequisitionInstance.fulfillToDate = ZonedDateTime.now()

            //loop on the list and create RequisitionWorkExperience instance
            if (periodInYearsValues) {
                GrailsParameterMap paramsForWorkExperience
                RequisitionWorkExperience requisitionWorkExperience
                WorkExperience workExperienceInstance

                //check if the professionType or competency is exist to create work experience or make work experience instance null
                // and save the value of work experience instance in requisition work experience

                Long professionTypeId = null
                Long competencyId = null
                String otherSpecificationsValue = null

                periodInYearsValues.eachWithIndex { it, index ->
                    professionTypeId = null
                    competencyId = null
                    workExperienceInstance = null

                    if (professionTypeIds[index] != null && professionTypeIds[index] != "" && professionTypeIds[index] != "null") {
                        professionTypeId = professionTypeIds[index] as long
                    }

                    if (competencyIds[index] != null && competencyIds[index] != "" && competencyIds[index] != "null") {
                        competencyId = competencyIds[index] as long
                    }

                    if (professionTypeId || competencyId) {
                        paramsForWorkExperience = new GrailsParameterMap([professionType: professionTypeId, competency: competencyId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        workExperienceInstance = workExperienceService.save(paramsForWorkExperience)
                    }
                    //create new  requisitionWorkExperience
                    requisitionWorkExperience = new RequisitionWorkExperience(workExperience: workExperienceInstance, periodInYears: it, otherSpecifications: otherSpecificationsValues[index])
                    if (!requisitionWorkExperience.validate()) {
                        if (requisitionWorkExperience.hasErrors()) {
                            requisitionWorkExperience.errors.allErrors.each { ObjectError error ->
                                jobRequisitionInstance.errors.reject(messageSource.getMessage(error.code, error.arguments, error.defaultMessage, LocaleContextHolder.getLocale()))
                            }
                        }
                        throw new Exception("requisitionWorkExperience error")
                    }
                    //add the created instance tio the jobRequisitionInstance instance
                    jobRequisitionInstance.addToRequisitionWorkExperiences(requisitionWorkExperience)
                }
            }

            List educationDegreesList = params.listString("educationDegrees")
            List educationMajorsList = params.listString("educationMajors")

            //add educationDegree to jobRequisitionInstance variable
            if (educationDegreesList) {
                jobRequisitionInstance.addToEducationDegrees(educationDegreesList)

            }

            //add educationMajors to jobRequisitionInstance variable
            if (educationMajorsList) {
                jobRequisitionInstance.addToEducationMajors(educationMajorsList)
            }

            if (jobRequisitionInstance?.job?.fromAge > 0 ? jobRequisitionInstance?.fromAge < jobRequisitionInstance?.job?.fromAge : false) {
                jobRequisitionInstance.errors.reject("jobRequisition.errorAge.label")
                return jobRequisitionInstance
            } else if (jobRequisitionInstance?.job?.toAge > 0 ? jobRequisitionInstance?.toAge > jobRequisitionInstance?.job?.toAge : false) {
                jobRequisitionInstance.errors.reject("jobRequisition.errorAge.label")
                return jobRequisitionInstance
            } else if (jobRequisitionInstance?.job?.fromHeight > 0 ? jobRequisitionInstance?.fromHeight < jobRequisitionInstance?.job?.fromHeight : false) {
                jobRequisitionInstance.errors.reject("jobRequisition.errorHeight.label")
                return jobRequisitionInstance
            } else if (jobRequisitionInstance?.job?.toHeight > 0 ? jobRequisitionInstance?.toHeight > jobRequisitionInstance?.job?.toHeight : false) {
                jobRequisitionInstance.errors.reject("jobRequisition.errorHeight.label")
                return jobRequisitionInstance
            } else if (jobRequisitionInstance?.job?.fromWeight > 0 ? jobRequisitionInstance?.fromWeight < jobRequisitionInstance?.job?.fromWeight : false) {
                jobRequisitionInstance.errors.reject("jobRequisition.errorWeight.label")
                return jobRequisitionInstance
            } else if (jobRequisitionInstance?.job?.toWeight > 0 ? jobRequisitionInstance?.toWeight > jobRequisitionInstance?.job?.toWeight : false) {
                jobRequisitionInstance.errors.reject("jobRequisition.errorWeight.label")
                return jobRequisitionInstance
            } else {
                jobRequisitionInstance.save(failOnError: true, flush: true);
            }

            // change the department state in (Recruitment cycle Departments tab) to (تم ملء طلبات احتياج)
            if (!jobRequisitionInstance.hasErrors() && jobRequisitionInstance.recruitmentCycle) {
                RecruitmentCycle recruitmentCycle = jobRequisitionInstance.recruitmentCycle
                boolean hasDepartment = false
                recruitmentCycle?.joinedRecruitmentCycleDepartment?.each {
                    if (it.department.id == jobRequisitionInstance.requestedForDepartment.id) {
                        hasDepartment = true
                        it.recruitmentCycleDepartmentStatus = EnumRecruitmentCycleDepartmentStatus.CLOSED_WITH_REPLAY
                        it.save(failOnError: true, flush: true)
                    }


                }
                if (!hasDepartment) {
                    // department to be add to RecruitmentCycle
                    JoinedRecruitmentCycleDepartment joinedRecruitmentCycleDepartment
                    joinedRecruitmentCycleDepartment = new JoinedRecruitmentCycleDepartment(recruitmentCycle: recruitmentCycle, department: jobRequisitionInstance.requestedForDepartment, recruitmentCycleDepartmentStatus: EnumRecruitmentCycleDepartmentStatus.CLOSED_WITH_REPLAY);
                    recruitmentCycle.addToJoinedRecruitmentCycleDepartment(joinedRecruitmentCycleDepartment)
                    recruitmentCycle.save(failOnError: true, flush: true)
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            jobRequisitionInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return jobRequisitionInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            //TODO: make sure from Mureed if we want to apply the virtual delete here:
            def id
            //if the id is encrypted
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }
            if (id) {
                JobRequisition instance = JobRequisition.load(id)
                //  delete jobRequisitionInstance which requisitionStatus is CREATED
                if (instance?.requisitionStatus in [EnumRequestStatus.CREATED]) {
                    //to apply virtual delete, we change tracking info's status to deleted
                    if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                        instance?.trackingInfo.status = GeneralStatus.DELETED
                        instance.save(flush: true)
                        deleteBean.status = true
                    }
                } else {
                    deleteBean.status = false
                    deleteBean.responseMessage << messageSource.getMessage('jobRequisition.deleteMessage.label')
                }
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('jobRequisition.deleteMessage.label')
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return JobRequisition.
     */
    @Transactional(readOnly = true)
    JobRequisition getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return JobRequisition.
     */
    @Transactional(readOnly = true)
    JobRequisition getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                JobRequisition jobRequisitionInstance = results[0]
                if (jobRequisitionInstance) {
                    SearchBean searchBean = new SearchBean()
                    //get the remoting values
                    //TODO read from session

                    //get the list of governorate DTO that had been selected from the user
                    List<GovernorateDTO> governorateDTOList = governorateService?.searchGovernorate(searchBean)?.resultList

                    List<EducationDegreeDTO> educationDegreeDTOList
                    List<EducationMajorDTO> educationMajorDTOList
                    List<ProfessionTypeDTO> professionTypeDTOList
                    List<CompetencyDTO> competencyDTOList
                    MaritalStatusDTO maritalStatusDTO

                    // get the list of educationDegrees that had been selected
                    if (jobRequisitionInstance?.educationDegrees) {
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: jobRequisitionInstance?.educationDegrees?.toList()))
                        educationDegreeDTOList = educationDegreeService?.searchEducationDegree(searchBean)?.resultList
                    }

                    // get the list of educationMajors that had been selected
                    if (jobRequisitionInstance?.educationMajors) {
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: jobRequisitionInstance?.educationMajors?.toList()))
                        educationMajorDTOList = educationMajorService?.searchEducationMajor(searchBean)?.resultList

                    }

                    // get the list of professionType that had been selected
                    if (jobRequisitionInstance?.requisitionWorkExperiences) {
                        searchBean = new SearchBean()
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "id", value1: jobRequisitionInstance?.requisitionWorkExperiences?.workExperience.professionType))
                        professionTypeDTOList = professionTypeService?.searchProfessionType(searchBean)?.resultList
                    }

                    // get the list of competency that had been selected
                    if (jobRequisitionInstance?.requisitionWorkExperiences) {
                        searchBean = new SearchBean()
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "id", value1: jobRequisitionInstance?.requisitionWorkExperiences?.workExperience.competency))
                        competencyDTOList = competencyService?.searchCompetency(searchBean)?.resultList
                    }

                    // get the maritalStatusId that had been selected
                    if (jobRequisitionInstance?.maritalStatusId) {
                        searchBean = new SearchBean()
                        searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: "id", value1: jobRequisitionInstance?.maritalStatusId))
                        maritalStatusDTO = maritalStatusService?.getMaritalStatus(searchBean)
                    }

                    //to get the list of name of governorate list
                    jobRequisitionInstance.transientData.put("governorateMapList", governorateDTOList.findAll {
                        it.id in jobRequisitionInstance.governorates
                    }?.collect { [it.id, it.descriptionInfo.localName] })
                    //to get the list of name of fromGovernorate List
                    jobRequisitionInstance.transientData.put("fromGovernorateMapList", governorateDTOList.findAll {
                        it.id in jobRequisitionInstance.fromGovernorates
                    }?.collect { [it.id, it.descriptionInfo.localName] })

                    //to get the list of name of educationDegree List
                    jobRequisitionInstance.transientData.put("educationDegreeMapList", educationDegreeDTOList?.collect {
                        [it.id, it.descriptionInfo.localName]
                    })

                    //to get the list of name of educationMajor List
                    jobRequisitionInstance.transientData.put("educationMajorMapList", educationMajorDTOList?.collect {
                        [it.id, it.descriptionInfo.localName]
                    })

                    //to get the list of name of professionType List
                    jobRequisitionInstance?.requisitionWorkExperiences?.workExperience?.each { WorkExperience workExperience ->
                        if (workExperience && workExperience != null) {
                            if (workExperience.professionType && workExperience.professionType != null) {
                                workExperience?.transientData.put("professionTypeName", professionTypeDTOList.find { professionTypeDTO -> professionTypeDTO.id == workExperience?.professionType }?.descriptionInfo?.localName
                                )
                            }
                        }
                    }

                    //to get the list of name of competency List
                    jobRequisitionInstance?.requisitionWorkExperiences?.workExperience?.each { WorkExperience workExperience ->
                        if (workExperience && workExperience != null) {
                            if (workExperience.competency && workExperience.competency != null) {
                                workExperience?.transientData.put("competencyName", competencyDTOList.find { competencyDTO -> competencyDTO.id == workExperience?.competency }?.descriptionInfo?.localName
                                )
                            }
                        }
                    }
                    //to get the personMaritalStatus name
                    jobRequisitionInstance.transientData.put("maritalStatusName", maritalStatusDTO?.descriptionInfo?.localName)

                    /**
                     * in case edit job requisition:
                     * to prevent remove jobs value, we get all job value and prevent remove them
                     */
                    jobRequisitionInstance.transientData.put("educationDegreeList", educationDegreeDTOList.findAll {
                        it.id in jobRequisitionInstance.job?.joinedJobEducationDegrees?.educationDegreeId
                    }?.collect { it.descriptionInfo.localName })


                    jobRequisitionInstance.transientData.put("educationMajorList", educationMajorDTOList.findAll {
                        it.id in jobRequisitionInstance.job?.joinedJobEducationMajors?.educationMajorId
                    }?.collect { it.descriptionInfo.localName })


                    jobRequisitionInstance.transientData.put("inspectionList", jobRequisitionInstance.job?.joinedJobInspectionCategories?.inspectionCategory?.collect {
                        it.descriptionInfo.localName
                    })


                }
                return jobRequisitionInstance
            }
        }
        return null
    }

    /**
     * to auto complete model entry.
     * @param GrailsParameterMap params the search map.
     * @return JSON.
     */
    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "job.descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List < String >  DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     * @see PagedResultList.
     * @see PagedList.
     */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * to set number of approved positions
     * @return JobRequisition.
     */
    @Transactional
    JobRequisition setApprovedPositions(GrailsParameterMap params) {

        def approvedPositions = params.int("acceptNumberOfApprovedPositions")
        String note = params.note
        params.remove("acceptNumberOfApprovedPositions")
        params.remove("note")
        JobRequisition jobRequisitionInstance = getInstance(params)

        if (jobRequisitionInstance) {
            // accept modal case
            if (approvedPositions != null ) {
                //to validate numberOfApprovedPositions grater than 0
                if(approvedPositions < 1){
                    jobRequisitionInstance.errors.reject('jobRequisition.acceptNumberOfApprovedPositions.label')
                    return jobRequisitionInstance
                }else{
                    if (note) {
                        jobRequisitionInstance.rejectionReason = note
                    }
                    jobRequisitionInstance.numberOfApprovedPositions = approvedPositions
                    jobRequisitionInstance.requisitionStatus= EnumRequestStatus.APPROVED
                }
            }
            // reject modal case
            else{
                if (note) {
                    jobRequisitionInstance.rejectionReason = note
                    jobRequisitionInstance.requisitionStatus= EnumRequestStatus.REJECTED
                    jobRequisitionInstance.numberOfApprovedPositions = 0
                }else {
                    jobRequisitionInstance.errors.reject('jobRequisition.error.label')
                    return jobRequisitionInstance
                }
            }
            jobRequisitionInstance.save(flush: true)
        }
        return jobRequisitionInstance

    }

    // to search about remoting values to return the name of governorates
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in job requisition
        PagedResultList<JobRequisition> pagedResultList = this.search(params)

        if (pagedResultList) {
            SearchBean searchBean = new SearchBean()
            //TODO check if governorate in session
            //return the list of governorate from the core
            List<GovernorateDTO> governorateList = governorateService?.searchGovernorate(searchBean)?.resultList
            // to fill the data of governorate from the core to the map
            pagedResultList.each { JobRequisition jobRequisition ->
                jobRequisition.transientData = [:]
                jobRequisition.transientData.put("governorateMapList", governorateList?.findAll {
                    it.id in jobRequisition.governorates
                }?.collect { ['governorateId': it.id, 'governorateName': it.descriptionInfo.localName] })
            }
        }
        return pagedResultList
    }

    public Map addApprovedNumberOfPosition(GrailsParameterMap params) {

        Object data = params.find { it.key.toString().contains("data[") }
        String allValuesB = data?.key?.toString()?.replaceAll("data", "")?.replaceAll("]", "")?.replaceFirst("\\[", "")
        String[] allValues = allValuesB?.split("\\[")
        String id = allValues[0]
        String field = allValues[1]
        Map newMap = [id: id, field: field, value: data?.value]

        //save new data
        Map dataToRender = [:]

        String recordId = newMap["id"]
        def instance
        if (recordId) {
            instance = JobRequisition.get(recordId)
        }
        if (instance) {
            Object value
            value = newMap["value"]
            if (Integer.parseInt(value) <= instance.numberOfPositions) {
                instance.numberOfApprovedPositions = Integer.parseInt(value)
                if (Integer.parseInt(value) > 0) {
                    instance.requisitionStatus = EnumRequestStatus.APPROVED
                } else {
                    instance.requisitionStatus = EnumRequestStatus.REJECTED
                }
                instance.save(flush: true, failOnError: true)
            }
            //return new data
            Map row = [:]
            row["DT_RowId"] = "row_$id"
            row[field] = data?.value
            dataToRender.data = [row]
        } else {
            Map row = [:]
            row["DT_RowId"] = "row_$id"
            dataToRender.data = [row]
        }
        return dataToRender
    }
}