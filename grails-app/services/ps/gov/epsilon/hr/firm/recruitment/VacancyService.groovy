package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus
import ps.gov.epsilon.hr.firm.Firm
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
 * -this service is aims to create a vacancy and manage its life cycle
 * <h1>Usage</h1>
 * -Used to create a vacancy
 * <h1>Restriction</h1>
 * -restriction on change status
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class VacancyService {

    MessageSource messageSource
    def formatService
    WorkExperienceService workExperienceService
    GovernorateService governorateService
    EducationMajorService educationMajorService
    EducationDegreeService educationDegreeService
    ProfessionTypeService professionTypeService
    CompetencyService competencyService
    MaritalStatusService maritalStatusService

    //to return the name of recruitmentCycle
    public static recruitmentCycleLocalName = { cService, Vacancy rec, object, params ->
        if (rec.recruitmentCycle) {
            return rec.recruitmentCycle?.name
        } else {
            return ""
        }
    }

    //to return the value of vacancy Status
    public static vacancyStatusValue = { cService, Vacancy rec, object, params ->
        return rec?.vacancyStatus?.toString()
    }

    //to get the list name of governorates
    public static renderGovernorateNames = { cService, Vacancy rec, object, params ->
        if (rec.governorates && rec.transientData.governorateList) {
            return rec.transientData.governorateList?.collect { it.governorateName }
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recruitmentCycle", type: recruitmentCycleLocalName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "job.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacancyStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "vacancyStatusValue", type: vacancyStatusValue, source: 'domain'],
    ]


    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: false, hidden: false, name: "recruitmentCycle", type: recruitmentCycleLocalName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "job.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacancyStatus", type: "enum", source: 'domain']
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
        String sSearchNumber = params["sSearch"]

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
        Set educationDegrees = params.listLong("educationDegrees")
        Set educationMajors = params.listLong("educationMajors")
        Long firmId = params.long("firm.id")
        Short fromAge = params.long("fromAge")
        Set fromGovernorates = params.listLong("fromGovernorates")
        Float fromTall = params.long("fromTall")
        Float fromWeight = params.long("fromWeight")
        ZonedDateTime fulfillFromDate = PCPUtils.parseZonedDateTime(params['fulfillFromDate'])
        ZonedDateTime fulfillToDate = PCPUtils.parseZonedDateTime(params['fulfillToDate'])
        Set governorates = params.listLong("governorates")
        Set inspectionCategoriesIds = params.listString("inspectionCategories.id")
        ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted sexTypeAccepted = params["sexTypeAccepted"] ? ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted.valueOf(params["sexTypeAccepted"]) : null
        String jobDescription = params["jobDescription"]
        String jobId = params["job.id"]
        String jobTypeId = params["jobType.id"]
        String maritalStatusId = params["maritalStatusId"]
        String note = params["note"]
        Long numberOfPositions = params.long("numberOfPositions")
        String proposedRankId = params["proposedRank.id"]

        String recruitmentCycleId = params["recruitmentCycle.id"]

        String requestedByDepartmentId = params["requestedByDepartment.id"]
        String requestedForDepartmentId = params["requestedForDepartment.id"]

        Short toAge = params.long("toAge")
        Float toTall = params.long("toTall")
        Float toWeight = params.long("toWeight")
        ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus vacancyStatus = params["vacancyStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.valueOf(params["vacancyStatus"]) : null
        Set workExperienceIds = params.listString("workExperience.id")
        String status = params["status"]
        boolean filterByStatus = params.boolean("filterByStatus")
        List execludedIds = params.listString("execludedIds")
        boolean filterForVacancyAdvertisement = params.boolean("filterForVacancyAdvertisement")

        return Vacancy.createCriteria().list(max: max, offset: offset) {
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

                    requestedByDepartment {
                        ilike("descriptionInfo.localName", sSearch)
                    }

                    governorates {
                        iLike("descriptionInfo.localName", sSearch)
                    }

                    job {
                        ilike("descriptionInfo.localName", sSearch)
                    }
                    if (sSearchNumber) {
                        eq("numberOfPositions", sSearchNumber)
                        eq("fromAge", sSearchNumber)
                        eq("toAge", sSearchNumber)
                    }
                }
            }

            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (execludedIds) {
                    not { 'in'("id", execludedIds) }
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
                if (fromGovernorates) {
                    fromGovernorates {
                        inList("id", fromGovernorates)
                    }
                }
                if (fromTall) {
                    eq("fromTall", fromTall)
                }
                if (fromWeight) {
                    eq("fromWeight", fromWeight)
                }
                if (fulfillFromDate) {
                    le("fulfillFromDate", fulfillFromDate)
                }
                if (fulfillToDate) {
                    le("fulfillToDate", fulfillToDate)
                }
                if (governorates) {
                    governorates {
                        inList("id", governorates)
                    }
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
                if (jobTypeId) {
                    eq("jobType.id", jobTypeId)
                }
                if (maritalStatusId) {
                    eq("maritalStatusId", maritalStatusId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (numberOfPositions) {
                    eq("numberOfPositions", numberOfPositions)
                }
                if (proposedRankId) {
                    eq("proposedRank.id", proposedRankId)
                }
                if (recruitmentCycleId && !filterForVacancyAdvertisement) {
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }
                if (requestedByDepartmentId) {
                    eq("requestedByDepartment.id", requestedByDepartmentId)
                }
                if (requestedForDepartmentId) {
                    eq("requestedForDepartment.id", requestedForDepartmentId)
                }
                if (toAge) {
                    eq("toAge", toAge)
                }
                if (toTall) {
                    eq("toTall", toTall)
                }
                if (toWeight) {
                    eq("toWeight", toWeight)
                }
                if (vacancyStatus) {
                    eq("vacancyStatus", vacancyStatus)
                }
                if (filterByStatus) {
                    eq("vacancyStatus", EnumVacancyStatus.NEW)

                }

                if (workExperienceIds) {
                    workExperience {
                        inList("id", workExperienceIds)
                    }
                }


                if (filterForVacancyAdvertisement) {
                    or {
                        if (recruitmentCycleId) {
                            eq('recruitmentCycle.id', recruitmentCycleId)
                        } else {
                            recruitmentCycle {
                                currentRecruitmentCyclePhase {
                                    not {
                                        eq('requisitionAnnouncementStatus', EnumRequisitionAnnouncementStatus.CLOSED)
                                    }
                                }
                            }
                        }
                        isNull("recruitmentCycle.id")
                        eq("vacancyStatus", EnumVacancyStatus.NEW)
                    }
                }

                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "recruitmentCycle":
                        recruitmentCycle {
                            order("name", dir)
                        }
                        order("job", dir)

                        break;
                    case "job.descriptionInfo.localName":
                        order("job", dir)
                        break;
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    default:
                        order(columnName, 'desc')
                        break;
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return Vacancy.
     */
    Vacancy save(GrailsParameterMap params) {
        Vacancy vacancyInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            vacancyInstance = Vacancy.get((HashHelper.decode(params.encodedId)))
            if (params.long("version")) {
                long version = params.long("version")
                if (vacancyInstance.version > version) {
                    vacancyInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('vacancy.label', null, 'vacancy', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this vacancy while you were editing")
                    return vacancyInstance
                }
            }
            if (!vacancyInstance) {
                vacancyInstance = new Vacancy()
                vacancyInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacancy.label', null, 'vacancy', LocaleContextHolder.getLocale())] as Object[], "This vacancy with ${params.id} not found")
                return vacancyInstance
            }
        } else {
            vacancyInstance = new Vacancy()
            vacancyInstance.vacancyStatus = EnumVacancyStatus.NEW

        }
        try {
            vacancyInstance.properties = params;

            //to validate fromDate less than or equals toDate
            if (!vacancyInstance?.fulfillToDate || vacancyInstance?.fulfillFromDate <= vacancyInstance?.fulfillToDate) {

                //to remove records from requisition work experience table when edit
                if (vacancyInstance?.id) {
                    List requisitionWorkExperienceIds = vacancyInstance?.requisitionWorkExperiences?.id
                    if (requisitionWorkExperienceIds) {
                        RequisitionWorkExperience?.executeUpdate('delete from RequisitionWorkExperience a where a.vacancy.id =:vacancyId', ['vacancyId': vacancyInstance?.id])
                    }
                }

//            get the list of professionType
                List professionTypeIds = params.list("professionType")
                //get the list of competency
                List competencyIds = params.list("competency")
                //get the list of periodInYears
                List periodInYearsValues = params.list("periodInYears")
                //get the list of otherSpecifications
                List otherSpecificationsValues = params.list("otherSpecifications")

                //loop on the list and create RequisitionWorkExperience instance
                if (periodInYearsValues) {
                    GrailsParameterMap paramsForWorkExperience
                    RequisitionWorkExperience requisitionWorkExperience
                    WorkExperience workExperienceInstance

                    Long professionTypeId = null
                    Long competencyId = null

                    periodInYearsValues?.eachWithIndex { it, index ->
                        professionTypeId = null
                        competencyId = null
                        workExperienceInstance?.competency = null
                        workExperienceInstance?.professionType = null
                        //to check if professionTypeIds[index] is null or not and if not null then parse it to long
                        if (professionTypeIds[index] != null && professionTypeIds[index] != "null" && professionTypeIds[index]?.toString()?.trim()?.size() > 0) {
                            professionTypeId = professionTypeIds[index] as long
                        }

                        //to check if competencyIds[index] is null or not and if not null then parse it to long
                        if (competencyIds[index] != null && competencyIds[index] != "null" && competencyIds[index]?.toString()?.trim()?.size() > 0) {
                            competencyId = competencyIds[index] as long
                        }
                        //check if the professionType or competency is exist to create work experience or make work experience instance null
                        // and save the value of work experience instance in requisition work experience
                        if (professionTypeId || competencyId) {
                            paramsForWorkExperience = new GrailsParameterMap([professionType: professionTypeId, competency: competencyId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                            workExperienceInstance = workExperienceService?.save(paramsForWorkExperience)
                            //create new  requisitionWorkExperience
                            requisitionWorkExperience = new RequisitionWorkExperience(workExperience: workExperienceInstance, periodInYears: it, otherSpecifications: otherSpecificationsValues[index])
                        } else {
                            //create new  requisitionWorkExperience
                            requisitionWorkExperience = new RequisitionWorkExperience(periodInYears: it, otherSpecifications: otherSpecificationsValues[index])
                        }

                        //add the created instance tio the jobRequisitionInstance instance
                        vacancyInstance?.addToRequisitionWorkExperiences(requisitionWorkExperience)
                    }
                }
                //to get the list of educationDegrees
                List educationDegreesList = params.listLong("educationDegrees")
                //to get the list of educationMajors
                List educationMajorsList = params.listLong("educationMajors")

                //add educationDegree to jobRequisitionInstance variable
                if (educationDegreesList.size() > 0) {
                    vacancyInstance?.addToEducationDegrees(educationDegreesList)
                }
                //add educationMajors to jobRequisitionInstance variable
                if (educationMajorsList.size() > 0) {
                    vacancyInstance?.addToEducationMajors(educationMajorsList)
                }
                vacancyInstance.save(failOnError: true)

            } else {

                vacancyInstance.errors.reject('vacancy.dateError.label', [] as Object[], "")
                return vacancyInstance
            }

        } catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            vacancyInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return vacancyInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            def id
            //if the id is encrypted
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }
            if (id) {
                Vacancy instance = Vacancy.get(id)
                //  delete vacancy which current status is NEW
                if (instance?.vacancyStatus in [EnumVacancyStatus.NEW]) {
                    //to apply virtual delete, we change tracking info's status to deleted
                    if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                        instance?.trackingInfo.status = GeneralStatus.DELETED
                        instance.save(flush: true)
                        deleteBean.status = true
                    }
                } else {
                    deleteBean.status = false
                    deleteBean.responseMessage << messageSource.getMessage('vacancy.deleteErrorMessage.label')
                }
            }
        } catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return Vacancy.
     */
    @Transactional(readOnly = true)
    Vacancy getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }

        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            //search for the recruitmentCycle instance using the passed params (in case of edit/show):
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return Vacancy.
     */
    @Transactional(readOnly = true)
    Vacancy getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                return results[0]
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

    // to search about remoting values to return the name of governorates
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in job requisition
        PagedResultList<Vacancy> pagedResultList = search(params)

        if (pagedResultList) {
            SearchBean searchBean = new SearchBean()
            //TODO check if governorate in session
            //return the list of governorate from the core
            List<GovernorateDTO> governorateList = governorateService?.searchGovernorate(searchBean)?.resultList
            List<MaritalStatusDTO> maritalStatusList = maritalStatusService?.searchMaritalStatus(searchBean)?.resultList

            /***
             * To get the remoting values by calling the service for one time only
             * prepare all values that need remote value set
             * and send for one time to the core
             * */
            List vacanciesEducationDegreeList = []
            List vacanciesEducationMajorList = []
            List vacanciesProfessionTypeList = []
            List vacanciesCompetencyList = []
            List<EducationDegreeDTO> educationDegreeDTOList
            List<EducationMajorDTO> educationMajorDTOList
            List<ProfessionTypeDTO> professionTypeDTOList
            List<CompetencyDTO> competencyDTOList
            pagedResultList?.each { Vacancy vacancy ->

                if (vacancy) {

                    if (vacancy?.educationDegrees) {
                        vacanciesEducationDegreeList?.addAll(vacancy?.educationDegrees?.unique())
                    }

                    if (vacancy?.educationMajors) {
                        vacanciesEducationMajorList?.addAll(vacancy?.educationMajors?.unique())
                    }

                    if (vacancy?.requisitionWorkExperiences?.workExperience?.professionType) {
                        vacanciesProfessionTypeList?.addAll(vacancy?.requisitionWorkExperiences?.workExperience?.professionType?.unique())
                    }

                    if (vacancy?.requisitionWorkExperiences?.workExperience?.competency) {
                        vacanciesCompetencyList?.addAll(vacancy?.requisitionWorkExperiences?.workExperience?.competency?.unique())
                    }

                    if (vacanciesEducationDegreeList?.size() > 0) {
                        // get the list of educationDegrees that had been selected
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacanciesEducationDegreeList?.unique()))
                        educationDegreeDTOList = educationDegreeService?.searchEducationDegree(searchBean)?.resultList
                    }

                    if (vacanciesEducationMajorList?.size() > 0) {
                        // get the list of educationMajors that had been selected
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacanciesEducationMajorList?.unique()))
                        educationMajorDTOList = educationMajorService?.searchEducationMajor(searchBean)?.resultList
                    }


                    if (vacanciesProfessionTypeList?.size() > 0) {
                        // get the list of professionType that had been selected
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "id", value1: vacanciesProfessionTypeList?.unique()))
                        professionTypeDTOList = professionTypeService?.searchProfessionType(searchBean)?.resultList
                    }

                    if (vacanciesCompetencyList?.size() > 0) {
                        // get the list of competency that had been selected
                        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "id", value1: vacanciesCompetencyList?.unique()))
                        competencyDTOList = competencyService?.searchCompetency(searchBean)?.resultList
                    }
                    vacancy.transientData = [:]

                    //to get the list of name of governorate list
                    vacancy.transientData.put("governorateMapList", governorateList?.findAll {
                        it.id in vacancy.governorates
                    }?.collect { [it.id, it.descriptionInfo.localName] })

                    //to get the list of name of fromGovernorate List
                    vacancy.transientData.put("fromGovernorateMapList", governorateList?.findAll {
                        it.id in vacancy.fromGovernorates
                    }?.collect { [it.id, it.descriptionInfo.localName] })

                    //to get the list of name of educationDegree List
                    vacancy.transientData.put("educationDegreeMapList", educationDegreeDTOList?.collect {
                        [it.id, it.descriptionInfo.localName]
                    })
                    //to get the list of name of educationMajor List
                    vacancy.transientData.put("educationMajorMapList", educationMajorDTOList?.collect {
                        [it.id, it.descriptionInfo.localName]
                    })

                    //to get the personMaritalStatus name
                    vacancy.transientData.put("personMaritalStatus", maritalStatusList?.find {
                        it.id == vacancy.maritalStatusId
                    })

                    //to get the list of name of professionType List
                    vacancy?.requisitionWorkExperiences?.workExperience?.each { WorkExperience workExperience ->
                        workExperience?.transientData?.put("professionTypeName", professionTypeDTOList?.find {
                            it.id == workExperience?.professionType
                        }?.descriptionInfo?.localName
                        )
                    }

                    //to get the list of name of competency List
                    vacancy?.requisitionWorkExperiences?.workExperience?.each { WorkExperience workExperience ->
                        if (workExperience) {
                            workExperience?.transientData?.put("competencyName", competencyDTOList?.find {
                                it.id == workExperience?.competency
                            }?.descriptionInfo?.localName
                            )
                        }
                    }
                }
                vacanciesEducationDegreeList = []
                vacanciesEducationMajorList = []
                vacanciesProfessionTypeList = []
                vacanciesCompetencyList = []
            }
        }

        return pagedResultList
    }
}