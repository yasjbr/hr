package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.LocationAddressUtil
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.lookups.ProfessionTypeService
import ps.police.pcore.v2.entity.lookups.dtos.v1.ProfessionTypeDTO
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.*
import sun.util.resources.ar.LocaleNames_ar

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create a applicant instance
 * <h1>Usage</h1>
 * - used to create an applicant
 * - add applicant details, specification, education which will be used in employment
 * <h1>Restriction</h1>
 * restriction on create new applicant who has old entries
 * - person who is employee could not create an applicant.
 * - person who has open applicant in same recruitment cycle could not create new applicant in the same cycle unless his applicant is in REJECTED state)
 * no delete is allowed.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ApplicantService {

    MessageSource messageSource
    def formatService
    PersonService personService
    LocationService locationService
    ProfessionTypeService professionTypeService
    SpringSecurityService springSecurityService
    ManageLocationService manageLocationService
    PersonMaritalStatusService personMaritalStatusService
    TraineeListService traineeListService
    TraineeListEmployeeService traineeListEmployeeService
    RecruitmentListService recruitmentListService
    RecruitmentListEmployeeService recruitmentListEmployeeService

    //return the applicant current status
    public static getCurrentStatus = { cService, Applicant rec, object, params ->
        if (rec?.applicantCurrentStatus) {
            return rec?.applicantCurrentStatus?.applicantStatus.toString()
        } else {
            return ""
        }
    }

    //return the vacancy name
    public static vacancyLocalName = { formatService, Applicant rec, object, params ->
        if (rec?.vacancy?.job) {
            return rec?.vacancy?.job?.descriptionInfo?.localName
        } else {
            return ""
        }
    }

    //return the physical inspection mark
    //TODO ask why is it needed? also the code should be fixed to not use static string
    public static getPhysicalInspectionMark = { formatService, Applicant rec, object, params ->
        if (rec?.inspectionCategoriesResult?.mark) {
            return rec?.inspectionCategoriesResult?.find {
                it.inspectionCategory.descriptionInfo.localName == 'فحص لياقة بدنية'
            }?.mark
        } else {
            return ""
        }
    }
    //check if applicant recruitment list
    public static hasRecruitmentList = { formatService, Applicant rec, object, params ->
        if (rec?.recruitmentListEmployee?.id) {
            return true
        }
        return false
    }

    //check if applicant trainee list
    public static hasTraineeList = { formatService, Applicant rec, object, params ->
        if (rec?.traineeListEmployee?.id) {
            return true
        }
        return false
    }

    //return traineeList encodedId
    public static getTraineeListId = { formatService, Applicant rec, object, params ->
        return HashHelper.encode(params['traineeList.id'] + "")
    }

    //return traineeList encodedId
    public static getRecruitmentListId = { formatService, Applicant rec, object, params ->
        return HashHelper.encode(params['recruitmentList.id'] + "")
    }

    //return age as long
    public static getAgeAsLong = { formatService, Applicant rec, object, params ->
        return rec?.age?.longValue()
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applyingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicantCurrentStatus.applicantStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "currentStatus", type: getCurrentStatus, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "hasTraineeList", type: hasTraineeList, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "hasRecruitmentList", type: hasRecruitmentList, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]


    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
    ]


    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.genderType", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.locationName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applyingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicantCurrentStatus.applicantStatus", type: "enum", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_CUSTOM_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.genderType", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.locationName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applyingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicantCurrentStatus.applicantStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "recruitmentListId", type: getRecruitmentListId, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "personId", type: "long", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TRAINEE_LIST_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.genderType", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.locationName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applyingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicantCurrentStatus.applicantStatus", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "physicalInspectionMark", type: getPhysicalInspectionMark, source: 'domain']
    ]

    public static final List<String> DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy", type: vacancyLocalName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "age", type: getAgeAsLong, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.genderType", type: "string", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.locationName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applyingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "applicantCurrentStatus.applicantStatus", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "physicalInspectionMark", type: getPhysicalInspectionMark, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "traineeListId", type: getTraineeListId, source: 'domain'],
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
        String domainColumnName = params['domainColumnName']
        String columnName

        // get order column name
        if (column) {
            if (domainColumnName) {
                switch (domainColumnName) {
                    case "DOMAIN_TAB_COLUMNS":
                        columnName = DOMAIN_TAB_COLUMNS[column]?.name
                        break;
                    case "DOMAIN_TRAINEE_LIST_COLUMNS":
                        columnName = DOMAIN_TRAINEE_LIST_COLUMNS[column]?.name
                        break;
                    default:
                        columnName = DOMAIN_COLUMNS[column]?.name
                        break;
                }
            } else {
                columnName = DOMAIN_COLUMNS[column]?.name
            }
        }


        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        List excludedIdsInRecruitment = params.listString('excludedIdsInRecruitment')
        List excludedIdsInTrainee = params.listString('excludedIdsInTrainee')
        List excludedIds = params.listString('excludedIds')

        String id
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId) as String)
        } else {
            id = params['id']
        }

        if (params["applicant.id"]) {
            id = params["applicant.id"]
        }

        if (params["applicant.encodedId"]) {
            id = (HashHelper.decode(params["applicant.encodedId"]))
        }

        List<Map<String, String>> orderBy = params.list("orderBy")
        Double ageFrom = params.long("fromAge")
        Double ageTo = params.long("toAge")
        Double age = params.long("age")
        String applicantCurrentStatusId = params["applicantCurrentStatus.id"]
        ZonedDateTime applyingDate = PCPUtils.parseZonedDateTime(params['applyingDate'])
        ZonedDateTime applyingDateFrom = PCPUtils.parseZonedDateTime(params['applyingDateFrom'])
        ZonedDateTime applyingDateTo = PCPUtils.parseZonedDateTime(params['applyingDateTo'])
        String archiveNumber = params["archiveNumber"]
        Set arrestHistories = params.listString("arrestHistories")
        Set contactInfos = params.listString("contactInfos")
        Set educationEnfos = params.listString("educationEnfos")
        String fatherJobDesc = params["fatherJobDesc"]
        Long fatherProfessionType = params.long("fatherProfessionType")
        Double height = params.long("height")
        Set inspectionCategoriesResultIds = params.listString("inspectionCategoriesResult.id")
        String interviewId = params["interview.id"]
        Long locationId = params.long("locationId")
        String motherJobDesc = params["motherJobDesc"]
        Long motherProfessionType = params.long("motherProfessionType")
        String nominationParty = params["nominationParty"]
        Long personId = params.long("personId")
        String previousJobDesc = params["previousJobDesc"]
        Long firmId = params.long("firm.id")
        Long previousProfessionType = params.long("previousProfessionType")
        String recruitmentCycleId = params["recruitmentCycle.id"]
        String recruitmentListEmployeeId = params["recruitmentListEmployee.id"]
        String rejectionReason = params["rejectionReason"]
        String relativesInCivilianFirm = params["relativesInCivilianFirm"]
        String relativesInMilitaryFirms = params["relativesInMilitaryFirms"]
        Set statusHistoryIds = params.listString("statusHistory.id")
        String unstructuredLocation = params["unstructuredLocation"]
        String vacancyId = params["vacancy.id"]
        Double weight = params.long("weight")
        String personName = params["personName"]
        boolean hasNoInterview = params.boolean("hasNoInterview")
        ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus applicantCurrentStatusValue = params["applicantCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.valueOf(params["applicantCurrentStatusValue"] as String) : null

        List<EnumApplicantStatus> applicantStatusList = []
        params.listString("applicantCurrentStatusValueList")?.each { String value ->
            applicantStatusList.add(EnumApplicantStatus.valueOf(value))
        }

        // we need more than one applicant status to be excluded
        Set excludedApplicantCurrentStatusValueStrings = params.listString("excludedApplicantCurrentStatusValue")
        List<ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus> excludedApplicantCurrentStatusValue = []
        excludedApplicantCurrentStatusValueStrings.each { String statusString ->
            excludedApplicantCurrentStatusValue.add(ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.valueOf(statusString))
        }
        //ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus excludedApplicantCurrentStatusValue = params["excludedApplicantCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.valueOf(params["excludedApplicantCurrentStatusValue"] as String) : null

        //boolean value to filter applicant as exceptional records in the recruitment list
        boolean filterApplicantToAddAsException = params.boolean("filterApplicantToAddAsException")
        //boolean value to filter applicant as exceptional records in the trainee list
        boolean filterApplicantToAddAsExceptionInTraineeList = params.boolean("filterApplicantToAddAsExceptionInTraineeList")
        String status = params["status"]

        return Applicant.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("archiveNumber", sSearch)
                    ilike("fatherJobDesc", sSearch)
                    ilike("motherJobDesc", sSearch)
                    ilike("nominationParty", sSearch)
                    ilike("previousJobDesc", sSearch)
                    ilike("rejectionReason", sSearch)
                    ilike("relativesInCivilianFirm", sSearch)
                    ilike("relativesInMilitaryFirms", sSearch)
                    ilike("unstructuredLocation", sSearch)
                }
            }
            and {
                //the values of applicant status that not allowed in the exceptional values in recruitment list
                if (filterApplicantToAddAsException) {
                    applicantCurrentStatus {
                        ne("applicantStatus", EnumApplicantStatus.TRAINING_PASSED)
                        ne("applicantStatus", EnumApplicantStatus.EMPLOYED)
                        ne("applicantStatus", EnumApplicantStatus.NOT_EMPLOYED)
                    }
                }

                //the values of applicant status that not allowed in the exceptional values in trainee list
                if (filterApplicantToAddAsExceptionInTraineeList) {
                    applicantCurrentStatus {
                        ne("applicantStatus", EnumApplicantStatus.TRAINING_PASSED)
                        ne("applicantStatus", EnumApplicantStatus.TRAINING_FAILED)
                        ne("applicantStatus", EnumApplicantStatus.ACCEPTED)
                        ne("applicantStatus", EnumApplicantStatus.EMPLOYED)
                        ne("applicantStatus", EnumApplicantStatus.NOT_EMPLOYED)
                    }
                }

                if (hasNoInterview) {
                    isNull("interview.id")
                }
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }

                if (excludedIds) {
                    not {
                        inList("id", excludedIds)
                    }
                }
                if (excludedIdsInRecruitment) {
                    not {
                        inList("id", excludedIdsInRecruitment)
                    }
                }
                if (excludedIdsInTrainee) {
                    not {
                        inList("id", excludedIdsInTrainee)
                    }
                }
                if (applicantCurrentStatusValue) {
                    applicantCurrentStatus {
                        eq("applicantStatus", applicantCurrentStatusValue)
                    }
                }
                if (applicantStatusList) {
                    applicantCurrentStatus {
                        inList("applicantStatus", applicantStatusList)
                    }
                }
                if (excludedApplicantCurrentStatusValue) {
                    applicantCurrentStatus {
                        not {
                            inList("applicantStatus", excludedApplicantCurrentStatusValue)
                        }
                    }
                }
                if (applicantCurrentStatusId) {
                    eq("applicantCurrentStatus.id", applicantCurrentStatusId)
                }
                if (applyingDate) {
                    eq("applyingDate", applyingDate)
                }
                //from/to :RequestDate
                if (applyingDateFrom) {
                    ge("applyingDate", applyingDateFrom)
                }
                if (applyingDateTo) {
                    le("applyingDate", applyingDateTo)
                }
                //from/to :age
                if (ageFrom) {
                    ge("age", ageFrom)
                }
                if (ageTo) {
                    le("age", ageTo)
                }
                if (age) {
                    eq("age", age)
                }
                if (archiveNumber) {
                    ilike("archiveNumber", "%${archiveNumber}%")
                }
                if (arrestHistories) {
                    arrestHistories {
                        inList("id", arrestHistories)
                    }
                }
                if (contactInfos) {
                    contactInfos {
                        inList("id", contactInfos)
                    }
                }
                if (educationEnfos) {
                    educationEnfos {
                        inList("id", educationEnfos)
                    }
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (personName) {
                    ilike("personName", "%${personName}%")
                }
                if (fatherJobDesc) {
                    ilike("fatherJobDesc", "%${fatherJobDesc}%")
                }
                if (fatherProfessionType) {
                    eq("fatherProfessionType", fatherProfessionType)
                }
                if (height) {
                    eq("height", height)
                }
                if (inspectionCategoriesResultIds) {
                    inspectionCategoriesResult {
                        inList("id", inspectionCategoriesResultIds)
                    }
                }
                if (interviewId) {
                    eq("interview.id", interviewId)
                }
                if (locationId) {
                    eq("locationId", locationId)
                }
                if (motherJobDesc) {
                    ilike("motherJobDesc", "%${motherJobDesc}%")
                }
                if (motherProfessionType) {
                    eq("motherProfessionType", motherProfessionType)
                }
                if (nominationParty) {
                    ilike("nominationParty", "%${nominationParty}%")
                }

                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }

                if (personId) {
                    eq("personId", personId)
                }
                if (previousJobDesc) {
                    ilike("previousJobDesc", "%${previousJobDesc}%")
                }
                if (previousProfessionType) {
                    eq("previousProfessionType", previousProfessionType)
                }
                if (recruitmentCycleId) {
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }
                if (recruitmentListEmployeeId) {
                    eq("recruitmentListEmployee.id", recruitmentListEmployeeId)
                }
                if (rejectionReason) {
                    ilike("rejectionReason", "%${rejectionReason}%")
                }
                if (relativesInCivilianFirm) {
                    ilike("relativesInCivilianFirm", "%${relativesInCivilianFirm}%")
                }
                if (relativesInMilitaryFirms) {
                    ilike("relativesInMilitaryFirms", "%${relativesInMilitaryFirms}%")
                }
                if (statusHistoryIds) {
                    statusHistory {
                        inList("id", statusHistoryIds)
                    }
                }
                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                if (vacancyId) {
                    eq("vacancy.id", vacancyId)
                }
                if (weight) {
                    eq("weight", weight)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "applicantCurrentStatus.applicantStatus":
                        applicantCurrentStatus {
                            order("applicantStatus", dir)
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
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList applicantList = search(params)
        if (applicantList) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: applicantList?.resultList?.personId))
            List<PersonDTO> personList = personService?.searchPerson(searchBean)?.resultList

            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: applicantList?.resultList?.locationId?.unique()))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList


            PersonDTO personDTO
            applicantList?.each { Applicant applicant ->
                personDTO = personList?.find { it?.id == applicant?.personId }
                applicant.transientData.put("genderType", personDTO?.genderType?.descriptionInfo?.localName)
                applicant.transientData.put("locationName", LocationAddressUtil.renderLocation(locationList?.find {
                    it?.id == applicant?.locationId
                }, applicant.unstructuredLocation));
            }
            return applicantList
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return Applicant.
     */
    Applicant save(GrailsParameterMap params) {
        Applicant applicantInstance
        if (params.id) {
            applicantInstance = Applicant.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (applicantInstance.version > version) {
                    applicantInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('applicant.label', null, 'applicant', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this applicant while you were editing")
                    return applicantInstance
                }
            }
            if (!applicantInstance) {
                applicantInstance = new Applicant()
                applicantInstance.errors.reject('default.not.found.message', [messageSource.getMessage('applicant.label', null, 'applicant', LocaleContextHolder.getLocale())] as Object[], "This applicant with ${params.id} not found")
                return applicantInstance
            }
        } else {
            applicantInstance = new Applicant()
            applicantInstance?.applyingDate = ZonedDateTime.now()
        }
        try {
            applicantInstance.properties = params;

            // REJECTED, if the person has other applicant was filed in same recruitment cycle.
            if (!params.id && params.long("recruitmentCycle.id")) {

                //initialize new params
                GrailsParameterMap searchParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest());
                searchParams["recruitmentCycle.id"] = params["recruitmentCycle.id"]
                searchParams["personId"] = params["personId"]
                searchParams["excludedApplicantCurrentStatusValue"] = EnumApplicantStatus.REJECTED
                PagedResultList result = search(searchParams)
                if (result.totalCount > 0) {
                    applicantInstance.errors.reject("applicant.hasOldInstance.error");
                    return applicantInstance
                }
            }

            ApplicantStatusHistory applicantStatusHistory
            if (!params.id) {//create new applicant case:
                //1- create history status instance:
                /*
                * TODO, please set toDate to nullable true, run unit testing to check them */
                applicantStatusHistory = new ApplicantStatusHistory(applicant: applicantInstance, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now(), applicantStatus: EnumApplicantStatus.NEW)

            } else {//update applicant case:

                //only applicant with under checking and under interview statuses are allowed to be changed manually , other statuses are changed auto
                if (applicantInstance?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.UNDER_INTERVIEW) {
                    ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus applicantStatus = params["applicantStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.valueOf(params["applicantStatus"]) : null
                    if (applicantStatus && applicantStatus != applicantInstance?.applicantCurrentStatus?.applicantStatus) {
                        //1- create history status instance:
                        applicantInstance?.applicantCurrentStatus?.toDate = ZonedDateTime.now()
                        /*
                         *  TODO, please set toDate to nullable true, run unit testing to check them */
                        applicantStatusHistory = new ApplicantStatusHistory(applicant: applicantInstance, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now(), applicantStatus: applicantStatus)
                        //3- add to joined
                    }
                }
            }

            //add the status history to applicant:
            if (applicantStatusHistory) {
                applicantInstance?.addToStatusHistory(applicantStatusHistory)
            }

            //save the related location of applicant
            if (params.long("location.governorate.id")) {
                params["location.withWrapper"] = true
                LocationCommand locationCommand = manageLocationService?.saveLocation(params["location"])
                if (locationCommand?.id) {
                    applicantInstance?.locationId = locationCommand?.id
                    //assign reference id of location from core
                } else {
                    // if there is any error while saving location, reject the save.
                    applicantInstance.errors.reject("location.save.error");
                    return applicantInstance
                }
            }

            //4- save
            applicantInstance.save(flush: true, failOnError: true);

            //5- save current status
            if (applicantInstance?.id && applicantStatusHistory && applicantInstance?.locationId) {
                applicantInstance?.applicantCurrentStatus = applicantStatusHistory
                applicantInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            applicantInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], " ")
        }
        return applicantInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see ps.police.common.beans.v1.DeleteBean.
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

            Applicant instance = Applicant.get(id)
            //to be able to delete an trainee list when status is created
            if (instance?.applicantCurrentStatus?.applicantStatus in [EnumApplicantStatus.NEW]) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo.status = GeneralStatus.DELETED
                    instance.save()
                    deleteBean.status = true
                }
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('applicant.deleteMessage.label', null, "", new Locale("ar"))
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
     * @return Applicant.
     */
    @Transactional(readOnly = true)
    Applicant getInstance(GrailsParameterMap params) {

        if (params["applicant.encodedId"]) {
            params["encodedId"] = params["applicant.encodedId"]
        }

        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //search for the applicant instance using the passed params (in case of edit/show):
            PagedResultList results = this.search(params)
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * this method used to get the person, profession type, location remoting info
     * @param applicant
     * @return
     */
    @Transactional(readOnly = true)
    Applicant getInstanceWithRemotingValues(GrailsParameterMap params) {
        Applicant applicant = getInstance(params);
        if (applicant) {
            ProfessionTypeDTO professionTypeDTO
            //get father profession type from core
            if (applicant?.fatherProfessionType) {
                GrailsParameterMap paramsForProfessionTypeDTO = new GrailsParameterMap([id: applicant?.fatherProfessionType], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                professionTypeDTO = professionTypeService.getProfessionType(PCPUtils.convertParamsToSearchBean(paramsForProfessionTypeDTO))
                applicant?.transientData.put("fatherProfessionName", professionTypeDTO?.descriptionInfo?.localName)
            }

            //get mother profession type from core
            if (applicant?.motherProfessionType) {
                GrailsParameterMap paramsForProfessionTypeDTO = new GrailsParameterMap([id: applicant?.motherProfessionType], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                professionTypeDTO = professionTypeService.getProfessionType(PCPUtils.convertParamsToSearchBean(paramsForProfessionTypeDTO))
                applicant?.transientData.put("motherProfessionName", professionTypeDTO?.descriptionInfo?.localName)
            }

            //get the applicant details as person
            if (applicant?.personId) {
                GrailsParameterMap paramsForPersonDTO = new GrailsParameterMap([id: applicant?.personId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                PersonDTO personDTO = personService.getPerson(PCPUtils.convertParamsToSearchBean(paramsForPersonDTO))
                applicant?.transientData.put("personDTO", personDTO)

                //get person marital status
                GrailsParameterMap filterParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                filterParams["person.id"] = applicant?.personId
                filterParams["isCurrent"] = true

                PersonMaritalStatusDTO personMaritalStatus = personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(filterParams))
                applicant?.transientData.put("personMaritalStatus", personMaritalStatus);
                applicant?.transientData.put("birthPlace", LocationAddressUtil.renderLocation(personDTO?.birthPlace, personDTO?.unstructuredBirthPlaceLocation))
                applicant?.age = personDTO?.age //the age is saved in core as formula .
            }

            //get the location remote value
            if (applicant?.locationId) {
                GrailsParameterMap paramsForLocationDTO = new GrailsParameterMap([id: applicant?.locationId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                LocationDTO locationDTO = locationService.getLocation(PCPUtils.convertParamsToSearchBean(paramsForLocationDTO))
                applicant?.transientData.put("locationDTO", locationDTO);
                applicant?.transientData.put("location", LocationAddressUtil.renderLocation(locationDTO, applicant.unstructuredLocation));
            }

            //get the location remote value for training location
            if (applicant?.traineeListEmployee?.traineeList) {
                GrailsParameterMap paramsForLocationDTO = new GrailsParameterMap([id: applicant?.traineeListEmployee?.traineeList?.trainingLocationId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                LocationDTO locationDTO = locationService.getLocation(PCPUtils.convertParamsToSearchBean(paramsForLocationDTO))
                applicant?.traineeListEmployee?.traineeList?.transientData.put("location", LocationAddressUtil.renderLocation(locationDTO, applicant.unstructuredLocation));
            }
        }
        return applicant
    }

    /**
     * return the map which used in edit applicant form, to pass the status values:
     * @param params
     * @return
     */
    Map getApplicantStatus(EnumApplicantStatus status) {
        List<EnumApplicantStatus> applicantStatusList = []
        applicantStatusList.push(status)//push the current status
        Map map = [:]
        switch (status) {

        /*
         * case1:
         * if the applicant is under interview:
         * only below statuses are allowed to be chosen
         */
            case EnumApplicantStatus.UNDER_INTERVIEW.toString():
                applicantStatusList.push(EnumApplicantStatus.INTERVIEW_ABSENCE)
                applicantStatusList.push(EnumApplicantStatus.REJECTED)
                applicantStatusList.push(EnumApplicantStatus.ACCEPTED)
                applicantStatusList.push(EnumApplicantStatus.REJECTED_FOR_EVER)
                map.put("editStatus", true)
                break
            default:
                break
        }
        map.put("applicantStatusList", applicantStatusList)
        return map

    }

    /**
     * new service was added to get person remote details from core and use them in the create new applicant
     * @param params
     * @return applicant instance
     */
    Applicant getPersonInstanceWithRemotingValues(GrailsParameterMap params) {
        //1- get the applicant instance:
        Applicant applicant = new Applicant()

        //get the applicant details as person
        if (params.personId) {
            GrailsParameterMap paramsForPersonDTO = new GrailsParameterMap([id: params.personId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            PersonDTO personDTO = personService.getPerson(PCPUtils.convertParamsToSearchBean(paramsForPersonDTO))
            applicant?.transientData.put("personDTO", personDTO)

            //get person marital status
            GrailsParameterMap filterParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            filterParams["person.id"] = params.personId
            filterParams["isCurrent"] = true
            PersonMaritalStatusDTO personMaritalStatus = personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(filterParams))
            applicant?.transientData.put("personMaritalStatus", personMaritalStatus);
            applicant?.transientData.put("birthPlace", LocationAddressUtil.renderLocation(personDTO?.birthPlace, personDTO?.unstructuredBirthPlaceLocation))
            applicant?.age = personDTO?.age //the age is saved in core as formula

            //get mother,father, full name for new applicant, but these values could be changed (those 3 values will be saved in applicant it self)
            applicant?.personName = applicant?.transientData?.personDTO?.localFullName
            applicant?.motherName = applicant?.transientData?.personDTO?.localMotherName
            applicant?.fatherName = applicant?.transientData?.personDTO?.localSecondName
            applicant?.personId = applicant?.transientData?.personDTO?.id
        }

        // passed the applicant instance and person DTO to be used in view
        return applicant
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
        String nameProperty = params["nameProperty"] ?: "personName"
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
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["traineeList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        TraineeList traineeList = traineeListService.getInstance(parameterMap)
        PagedResultList pagedResultList = traineeListEmployeeService.search(params)
        PagedList customPagedList = new PagedList()
        customPagedList.resultList = pagedResultList.applicant
        customPagedList.totalCount = pagedResultList.totalCount
        def details = formatService.buildDataToDataTable(DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS, customPagedList, params)?.data
        Map map = [:]
        map.code = traineeList?.code
        map.coverLetter = traineeList?.coverLetter
        map.details = details
        return [map]
    }

    /**
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getRecruitmentReportData(GrailsParameterMap params) {
        String id = params["recruitmentList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        RecruitmentList recruitmentList = recruitmentListService.getInstance(parameterMap)
        // to disciplinary  list record
        PagedResultList pagedResultList = recruitmentListEmployeeService.search(params)
        PagedList customPagedList = new PagedList()
        customPagedList.resultList = pagedResultList.applicant
        customPagedList.totalCount = pagedResultList.totalCount
        def details = formatService.buildDataToDataTable(DOMAIN_TAB_CUSTOM_COLUMNS, customPagedList, params)?.data
        Map map = [:]
        map.code = recruitmentList?.code
        map.coverLetter = recruitmentList?.coverLetter
        map.details = details
        return [map]
    }

    /**
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return applicant instance.
     */
    @Transactional(readOnly = true)
    Map getPreCreateInstance(GrailsParameterMap params) {
        //TODO check if the employee is still employed in the military organization
        Applicant applicant

        Employee employee = Employee.findByPersonId(params.long("personId"))

        // get applicants for the same person and check the status
        boolean anyApplicantExist = false, anyOpenApplicantExist = false
        List<Applicant> personApplicantList = Applicant.findAllByPersonId(params.long("personId"))
        if (personApplicantList) {
            personApplicantList.each { Applicant previousApplicant ->
                if (previousApplicant?.trackingInfo?.status != GeneralStatus.DELETED) {
                    anyApplicantExist = true
                    if (previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.NEW ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.ADD_TO_LIST ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.UNDER_INTERVIEW ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.INTERVIEW_ABSENCE ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.UNDER_TRAINING ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.EMPLOYED ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.TRAINING_PASSED ||
                            previousApplicant?.applicantCurrentStatus?.applicantStatus == EnumApplicantStatus.TRAINING_FAILED) {
                        anyOpenApplicantExist = true
                    }
                }
            }
        }


        if (employee && (employee?.firm?.id == PCPSessionUtils.getValue("firmId"))) {
            //if the selected person is already was employed in this organization
            applicant = new Applicant(params)
            applicant?.errors.reject("applicant.isEmployee.error.label")
        } else if (employee && (employee?.firm?.id != PCPSessionUtils.getValue("firmId"))) {
            //if the selected person is already was employed in another organization
            applicant = new Applicant(params)
            applicant?.errors.reject("applicant.isEmployee.otherOrganization.error.label")
        } else if (anyOpenApplicantExist) {
            applicant = new Applicant(params)
        } else if (anyApplicantExist && !params.boolean("createAnotherApplicant")) {
            applicant = new Applicant(params)
        } else {
            //get applicant remoting values
            applicant = getPersonInstanceWithRemotingValues(params)
        }

        return [applicant: applicant, anyApplicantExist: anyApplicantExist, anyOpenApplicantExist: anyOpenApplicantExist, personId: applicant?.personId]
    }

}