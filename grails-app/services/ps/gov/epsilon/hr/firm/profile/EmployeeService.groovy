package ps.gov.epsilon.hr.firm.profile

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmploymentCategory
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.loan.LoanRequestRelatedPerson
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.gui.operators.OrderOperatorResult
import ps.police.gui.operators.SearchOperatorResult
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.lookups.RelationshipTypeService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonEducationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.recruitment.Applicant
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonMaritalStatusDTO
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -manage all employee transactions and get data from domain
 * <h1>Usage</h1>
 * -any service to get employee info or search about employees and by employee controller
 * <h1>Restriction</h1>
 * -must connect with pcore application to get person information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class EmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    PersonMaritalStatusService personMaritalStatusService
    GovernorateService governorateService
    OrganizationService organizationService
    SharedService sharedService
    def sessionFactory
    PersonEducationService personEducationService
    PersonRelationShipsService personRelationShipsService
    RelationshipTypeService relationshipTypeService

    public static militaryRankFullInfo = { formatService, Employee dataRow, object, params ->

        String rankInfo = ""

        if (dataRow) {
            rankInfo = dataRow?.currentEmployeeMilitaryRank?.militaryRank?.toString()
            if (dataRow?.currentEmployeeMilitaryRank?.militaryRankClassification) {
                rankInfo = rankInfo + " " + dataRow?.currentEmployeeMilitaryRank?.militaryRankClassification?.toString()
            }
            if (dataRow?.currentEmployeeMilitaryRank?.militaryRankType) {
                rankInfo = rankInfo + " " + dataRow?.currentEmployeeMilitaryRank?.militaryRankType?.toString()
            }
        }
        return rankInfo
    }

    //this closure is used to get the number of disciplinary per employee with link to disciplinary request list.
    public static getDisciplinaryRequestLink = { formatService, Employee dataRow, object, params ->
        String link = "0"
        if (dataRow?.id && dataRow?.transientData?.noOfDisciplinaryRequest != 0) {
            String hoverMessage = formatService.messageSource.getMessage("disciplinaryListLink.hoverMessage.label", null, LocaleContextHolder.getLocale())
            link = "<a title='${hoverMessage}' href='../disciplinaryRequest/list?employeeId=${dataRow?.id}'>" + dataRow.transientData.noOfDisciplinaryRequest + "</a>";
        }
        return link
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.personDTO.localFullName", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "militaryRankFullInfo", type: militaryRankFullInfo, source: 'domain'],
            [sort: true, search: false, hidden: true, name: "currentEmployeeMilitaryRank.actualDueDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.governorateDTO.descriptionInfo.localName", type: "Map", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "currentEmploymentRecord.province.descriptionInfo.localName", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentEmploymentRecord.department.descriptionInfo.localName", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "currentEmploymentRecord.jobTitle.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "financialNumber", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.recentCardNo", type: "Map", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "employmentDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "joinDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "categoryStatus.descriptionInfo", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],

    ]


    public static final List<String> DOMAIN_COLUMNS_DT_CONTROL = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.personDTO.localFullName", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "militaryRankFullInfo", type: militaryRankFullInfo, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentEmployeeMilitaryRank.actualDueDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.governorateDTO.descriptionInfo.localName", type: "Map", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentEmploymentRecord.province.descriptionInfo.localName", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentEmploymentRecord.department.descriptionInfo.localName", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentEmploymentRecord.jobTitle.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "financialNumber", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.recentCardNo", type: "Map", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "joinDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "categoryStatus.descriptionInfo", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.personDTO.localFullName", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "militaryRankFullInfo", type: militaryRankFullInfo, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "financialNumber", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.recentCardNo", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "categoryStatus.descriptionInfo", type: "Map", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations, used in promotion list to add employees
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS_MODAL = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.personDTO.localFullName", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "militaryRankFullInfo", type: militaryRankFullInfo, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.governorateDTO.descriptionInfo.localName", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "currentEmploymentRecord.department.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "financialNumber", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.recentCardNo", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "noOfDisciplinaryRequest", type: getDisciplinaryRequestLink, source: 'domain'],
    ]

    public static final List<String> DOMAIN_COLUMNS_SHOW = [
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.localFullName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.employmentAndEmploymentDate", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.militaryRankFullInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "yearsServiceDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "currentEmployeeMilitaryRank.actualDueDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "financialNumber", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "currentEmploymentRecord.department.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "currentEmploymentRecord.province.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.recentCardNo", type: "Map", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "currentEmploymentRecord.jobTitle.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.MaritalStatusAndNumOfEmployeeSon", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.employeeStatuses", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personDTO.localMotherName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.employeeStatusStatus", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.personEducation", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.employeeStatusDate", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.dateOfBirthAndgender", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.name", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.noteStatus", type: "String", source: 'domain'],

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
        String domainColumns = params["domainColumns"]
        List listOfColumns = DOMAIN_COLUMNS
        if (domainColumns) {
            listOfColumns = this."${domainColumns}"
        }
        if (column) {
            columnName = listOfColumns[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        List<Long> personIds = params.listLong('personIds[]')
        List<Long> personIdsToExclude = params.listLong('personIdsToExclude[]')

        String id
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId) as String)
        } else {
            id = params['id']
        }
        String id2 = params['id2']
        List<Map<String, String>> orderBy = params.list("orderBy")
        String applicantId = params["applicant.id"]
        String archiveNumber = params["archiveNumber"]
        ZonedDateTime attendanceStatusDate = PCPUtils.parseZonedDateTime(params['attendanceStatusDate'])
        String attendanceTypeId = params["attendanceType.id"]
        String bankAccountNumber = params["bankAccountNumber"]
        Long bankBranchId = params.long("bankBranchId")
        String militaryRankId = params["militaryRank.id"]
        String militaryRankTypeId = params["militaryRankType.id"]
        String militaryRankClassificationId = params["militaryRankClassification.id"]
        Long governorateId = params.long("governorateId")
        String departmentId = params["department.id"]
        List<String> militaryRankIds = params.listString('militaryRankIds')
        List<String> departmentIds = params.listString('departmentIds')
        String jobTitleId = params["jobTitle.id"]
        String jobCategoryId = params["jobCategory.id"]
        String jobCategoryName = params["jobCategoryName"]
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        Set disciplinaryRecordsIds = params.listString("disciplinaryRecords.id")
        Set employeeMilitaryRankIds = params.listString("employeeMilitaryRank.id")
        Set employeeOperationalTasksIds = params.listString("employeeOperationalTasks.id")
        Set employeeStatusHistoriesIds = params.listString("employeeStatusHistories.id")
        Set eploymentRecordIds = params.listString("eploymentRecord.id")
        String financialNumber = params["financialNumber"]
        Long firmId = params.long("firm.id") ?: PCPSessionUtils.getValue("firmId")
        String internalId = params["internalId"]
        ZonedDateTime joinDate = PCPUtils.parseZonedDateTime(params['joinDate'])
        String militaryNumber = params["militaryNumber"]
        Long personId = params.long("personId")
        Long sourceOrganizationId = params.long("sourceOrganizationId")
        ZonedDateTime statusDate = PCPUtils.parseZonedDateTime(params['statusDate'])
        Set trainingRecordsIds = params.listString("trainingRecords.id")
        String categoryStatusId = params["categoryStatusId"]
        String noFirmCategoryStatusId = params["noFirmCategoryStatusId"]
        Boolean allowReturnToService = params.boolean("allowReturnToService")
        //list of ids which are not included
        List idsToExclude = params.listString('idsToExclude[]')

        if (!categoryStatusId && noFirmCategoryStatusId) {
            EnumEmployeeStatusCategory employeeStatusCategory = EnumEmployeeStatusCategory.valueOf(noFirmCategoryStatusId)
            categoryStatusId = employeeStatusCategory.getValue(Firm.read(firmId)?.code)
            log.debug("resulting categoryStatusId = " + categoryStatusId)
        }

        //to get just ids
        Boolean withIdProjection = params.boolean("withIdProjection")

        //tell operator im created alias manual
        if (jobTitleId || jobCategoryName) {
            params.createdAliasList = [
                    '_currentEmploymentRecord',
                    '_currentEmployeeMilitaryRank',
                    '_militaryRank',
                    '_department',
                    '_jobTitle',
                    '_jobCategory',
            ]
        } else {
            params.createdAliasList = [
                    '_currentEmploymentRecord',
                    '_currentEmployeeMilitaryRank',
                    '_militaryRank',
                    '_department',
            ]
        }

        SearchOperatorResult searchOperatorResult = formatService.buildStaticSearchOperatorCriteria(params)
        OrderOperatorResult orderOperatorResult = formatService.buildOrderOperator(params)

        return Employee.createCriteria().list(max: max, offset: offset) {

            createAlias("currentEmployeeMilitaryRank", "_currentEmployeeMilitaryRank")
            createAlias("currentEmploymentRecord", "_currentEmploymentRecord")
            createAlias("_currentEmployeeMilitaryRank.militaryRank", "_militaryRank")
            createAlias("_currentEmploymentRecord.department", "_department")


            if (jobTitleId || jobCategoryName || jobCategoryId) {
                createAlias("_currentEmploymentRecord.jobTitle", "_jobTitle")
                createAlias("_jobTitle.jobCategory", "_jobCategory")
            }


            if (sSearch) {
                or {
                    ilike("archiveNumber", sSearch)
                    ilike("bankAccountNumber", sSearch)
                    ilike("financialNumber", sSearch)
                    ilike("internalId", sSearch)
                    ilike("militaryNumber", sSearch)
                }
            }
            and {
                eq("firm.id", firmId)

                if (searchOperatorResult?.isApplied) {
                    inList("id", searchOperatorResult?.listOfIds)
                }

                if (id) {
                    eq("id", id)
                }
                if (id2) {
                    eq("id", id2)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (idsToExclude) {
                    not {
                        inList("id", idsToExclude)
                    }
                }
                if (personIds) {
                    inList("personId", personIds)
                }
                if (personIdsToExclude) {
                    not {
                        inList("personId", personIdsToExclude)
                    }
                }
                if (applicantId) {
                    eq("applicant.id", applicantId)
                }
                if (archiveNumber) {
                    ilike("archiveNumber", "%${archiveNumber}%")
                }
                if (attendanceStatusDate) {
                    le("attendanceStatusDate", attendanceStatusDate)
                }
                if (attendanceTypeId) {
                    eq("attendanceType.id", attendanceTypeId)
                }
                if (bankAccountNumber) {
                    ilike("bankAccountNumber", "%${bankAccountNumber}%")
                }
                if (bankBranchId) {
                    eq("bankBranchId", bankBranchId)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("_currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("_currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (disciplinaryRecordsIds) {
                    disciplinaryRecords {
                        inList("id", disciplinaryRecordsIds)
                    }
                }
                if (employeeMilitaryRankIds) {
                    employeeMilitaryRank {
                        inList("id", employeeMilitaryRankIds)
                    }
                }
                if (employeeOperationalTasksIds) {
                    employeeOperationalTasks {
                        inList("id", employeeOperationalTasksIds)
                    }
                }
                if (employeeStatusHistoriesIds) {
                    employeeStatusHistories {
                        inList("id", employeeStatusHistoriesIds)
                    }
                }
                if (eploymentRecordIds) {
                    eploymentRecord {
                        inList("id", eploymentRecordIds)
                    }
                }
                if (financialNumber) {
                    ilike("financialNumber", "%${financialNumber}%")
                }
                if (internalId) {
                    ilike("internalId", "%${internalId}%")
                }
                if (joinDate) {
                    le("joinDate", joinDate)
                }
                if (militaryNumber) {
                    ilike("militaryNumber", "%${militaryNumber}%")
                }
                if (personId) {
                    eq("personId", personId)
                }

                if (sourceOrganizationId) {
                    eq("sourceOrganizationId", sourceOrganizationId)
                }
                if (statusDate) {
                    le("statusDate", statusDate)
                }
                if (trainingRecordsIds) {
                    trainingRecords {
                        inList("id", trainingRecordsIds)
                    }
                }
                if (militaryRankId || militaryRankIds || militaryRankTypeId || militaryRankClassificationId) {
                    if (militaryRankId) {
                        eq("_militaryRank.id", militaryRankId)
                    }
                    if (militaryRankIds) {
                        inList("_militaryRank.id", militaryRankIds)
                    }

                    if (militaryRankTypeId) {
                        eq("_currentEmployeeMilitaryRank.militaryRankType.id", militaryRankTypeId)
                    }

                    if (militaryRankClassificationId) {
                        eq("_currentEmployeeMilitaryRank.militaryRankClassification.id", militaryRankClassificationId)
                    }
                }
                if (departmentId || departmentIds || governorateId) {
                    if (departmentId) {
                        eq("_department.id", departmentId)
                    }
                    if (departmentIds) {
                        inList('_department.id', departmentIds)
                    }
                    if (governorateId) {
                        eq("_department.governorateId", governorateId)
                    }
                }
                if (jobTitleId) {
                    eq("_jobTitle.id", jobTitleId)
                }
                if (jobCategoryId) {
                    eq("_jobCategory.id", jobCategoryId)
                }
                if (jobCategoryName) {
                    eq("_jobCategory.descriptionInfo.localName", jobCategoryName)
                }

                if (categoryStatusId) {
                    categoryStatus {
                        eq("id", categoryStatusId)
                    }
                }

                if (allowReturnToService) {
                    employeeStatusHistories {
                        employeeStatus {
                            eq("allowReturnToService", allowReturnToService)
                        }
                        eq("toDate", PCPUtils.DEFAULT_ZONED_DATE_TIME)
                    }
                }
            }

            if (orderOperatorResult?.isApplied) {
                orderOperatorResult.delegateCriteria.delegate = delegate
                orderOperatorResult.delegateCriteria()
            } else if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                if (columnName.contains(".")) {
                    switch (columnName) {
                        case "transientData.personDTO.localFullName":
                            break;
                        case "currentEmployeeMilitaryRank.militaryRank":
                            order("_militaryRank", dir)
                            break;
                        case "currentEmploymentRecord.department.descriptionInfo.localName":
                            order("_department.descriptionInfo.localName", dir)
                            break;
                        case "currentEmployeeMilitaryRank.militaryRank.descriptionInfo.localName":
                            order("_militaryRank.descriptionInfo.localName", dir)
                            break;
                        case "currentEmploymentRecord.jobTitle.descriptionInfo.localName":
                            order("_jobTitle.descriptionInfo.localName", dir)
                            break;
                        case 'id':
                            order("trackingInfo.dateCreatedUTC", dir)
                            break;
                        default:
                            order(columnName, dir)
                            break;
                    }
                } else {
                    order(columnName, dir)
                }
            } else {
                //can't sort person depends on name because we have just 10 records
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

            if (withIdProjection) {
                projections {
                    groupProperty "id"
                }
                order("id")
            }
        }
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchReport(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.searchWithRemotingValues(params)
        String rankInfo
        SearchBean searchBean
        PagedList pagedReportResultList
        Long sonRelationShipId
        pagedResultList.each { Employee employee ->
            // militaryRankFullInfo
            rankInfo = ""
            if (employee?.currentEmployeeMilitaryRank?.militaryRankType) {
                rankInfo = employee?.currentEmployeeMilitaryRank?.militaryRankType?.toString()
            }
            rankInfo = rankInfo + " " + employee?.currentEmployeeMilitaryRank?.militaryRank?.toString()
            if (employee?.currentEmployeeMilitaryRank?.militaryRankClassification) {
                rankInfo = rankInfo + " " + employee?.currentEmployeeMilitaryRank?.militaryRankClassification?.toString()
            }
            employee.transientData.militaryRankFullInfo = rankInfo

            // employmentAndEmploymentDate
            employee.transientData.employmentAndEmploymentDate = employee?.employmentDate?.toLocalDate()?.toString() + " | " + employee?.employmentNumber

            //employee status
            def employeeStatuses = employee?.employeeStatusHistories?.findAll {
                !it.toDate || it.toDate == ps.police.common.utils.v1.PCPUtils.DEFAULT_ZONED_DATE_TIME
            }?.sort { it.employeeStatus.descriptionInfo.localName }
            employee.transientData.employeeStatuses = employeeStatuses?.join(",")?.toString()

            //employeeStatuses status
            employee.transientData.employeeStatusStatus = ""

            //person education
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("max", new SearchConditionCriteriaBean(operand: 'max', value1: "1"))
            searchBean.searchCriteria.put("orderColumn", new SearchConditionCriteriaBean(operand: 'orderColumn', value1: "5"))
            searchBean.searchCriteria.put("orderDirection", new SearchConditionCriteriaBean(operand: 'orderDirection', value1: "desc"))
            searchBean.searchCriteria.put("person.id", new SearchConditionCriteriaBean(operand: 'person.id', value1: employee.personId))
            pagedReportResultList = personEducationService.searchPersonEducation(searchBean)
            if (pagedReportResultList) {
                employee.transientData.personEducation = pagedReportResultList?.resultList[0]?.educationDegree?.descriptionInfo?.localName
            } else {
                employee.transientData.personEducation = ""
            }

            // personMaritalStatusDTO and and numOfEmployeeSon RelationshipClassification
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("descriptionInfo.localName", new SearchConditionCriteriaBean(operand: 'descriptionInfo.localName', value1: "ابن"))
            sonRelationShipId = relationshipTypeService.searchRelationshipType(searchBean)?.resultList[0]?.id

            searchBean = new SearchBean()
            searchBean.searchCriteria.put("person.id", new SearchConditionCriteriaBean(operand: 'person.id', value1: employee.personId))
            searchBean.searchCriteria.put("relationshipType.id", new SearchConditionCriteriaBean(operand: 'relationshipType.id', value1: sonRelationShipId))
            pagedReportResultList = personRelationShipsService.searchPersonRelationShips(searchBean)

            searchBean = new SearchBean()
            searchBean.searchCriteria.put("person.id", new SearchConditionCriteriaBean(operand: 'person.id', value1: employee?.personId))
            searchBean.searchCriteria.put("isCurrent", new SearchConditionCriteriaBean(operand: 'isCurrent', value1: "true"))
            PersonMaritalStatusDTO personMaritalStatusDTO = personMaritalStatusService?.getPersonMaritalStatus(searchBean)

            employee.transientData.MaritalStatusAndNumOfEmployeeSon = personMaritalStatusDTO?.maritalStatus?.toString() + "" + " | عدد الأبناء : " + pagedReportResultList.getTotalCount()

            //employeeStatuses date
            employee.transientData.employeeStatusDate = ""

            // dateOfBirth and gender
            employee.transientData.dateOfBirthAndgender = employee?.transientData?.personDTO?.genderType?.descriptionInfo?.localName + " | " + employee?.transientData?.personDTO?.dateOfBirth?.toLocalDate()?.toString()

            //note status
            employee.transientData.noteStatus = ""

        }

        return pagedResultList

    }
    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {

        String recentCardNo = params["recentCardNo"]
        String sSearch = params.remove("sSearch")
        String employeeName = params.remove("employeeName")
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("max", new SearchConditionCriteriaBean(operand: 'max', value1: params["max"]))
        List<PersonDTO> persons


        if (recentCardNo || sSearch || employeeName) {
            if (recentCardNo) {
                searchBean.searchCriteria.put("recentCardNo", new SearchConditionCriteriaBean(operand: 'recentCardNo', value1: recentCardNo))
            }

            if (sSearch) {
                //todo remove the list of employee ids and use service in service layer application to solve this issue
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: Employee.findAll()?.personId?.toList()))
                searchBean.searchCriteria.put("localFullName", new SearchConditionCriteriaBean(operand: 'localFullName', value1: sSearch))
            }

            if (employeeName) {
                searchBean.searchCriteria.put("localFullName", new SearchConditionCriteriaBean(operand: 'localFullName', value1: employeeName))
            }

            //fill all employee persons info
            persons = personService.searchPerson(searchBean)?.resultList
            //to prevent get employee info when no PCORE result
            params["personIds[]"] = persons?.id?.toList() ?: [-1L]
        }

        PagedResultList pagedResultList = this.search(params)
        List personIds = pagedResultList.resultList.personId.toList()
        List governorateIds = pagedResultList.resultList?.currentEmploymentRecord?.department?.governorateId.toList()
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))


        if (personIds?.size() > 0 && !recentCardNo && !sSearch && !employeeName) {
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
            //fill all employee persons info
            persons = personService.searchPerson(searchBean)?.resultList
        }

        //fill all employee governorates
        List<GovernorateDTO> governorates
        if (governorateIds) {
            governorates = governorateService.searchGovernorate(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: governorateIds)]))?.resultList
        }

        if (persons || governorates) {
            pagedResultList?.resultList?.each { Employee employee ->
                employee.transientData.personDTO = persons?.find { it.id == employee.personId }
                employee.transientData.governorateDTO = governorates?.find {
                    it.id == employee?.currentEmploymentRecord?.department?.governorateId
                }
            }
        }

        def orderColumn = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        String domainColumns = params["domainColumns"]
        List listOfColumns = DOMAIN_COLUMNS
        if (domainColumns) {
            listOfColumns = this."${domainColumns}"
        }
        if (orderColumn != null) {
            columnName = listOfColumns[orderColumn]?.name
        }
        if (columnName == "transientData.personDTO.localFullName") {
            //sort by person name
            pagedResultList.resultList.sort { a, b ->
                if (dir == "asc") {
                    return a?.transientData?.personDTO?.localFullName <=> b?.transientData?.personDTO?.localFullName
                } else {
                    return b?.transientData?.personDTO?.localFullName <=> a?.transientData?.personDTO?.localFullName
                }
            }
        }
        return pagedResultList
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return List.
     */
    @Transactional(readOnly = true)
    List searchIds(GrailsParameterMap params) {
        params.withIdProjection = "true"
        params.offset = "0"
        params.max = Integer.MAX_VALUE.toString()
        String recentCardNo = params["recentCardNo"]
        String sSearch = params.remove("sSearch")
        String employeeName = params.remove("employeeName")
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("max", new SearchConditionCriteriaBean(operand: 'max', value1: params["max"]))
        List<PersonDTO> persons
        if (recentCardNo || sSearch || employeeName) {
            if (recentCardNo) {
                searchBean.searchCriteria.put("recentCardNo", new SearchConditionCriteriaBean(operand: 'recentCardNo', value1: recentCardNo))
            }
            if (sSearch) {
                //todo remove the list of employee ids and use service in service layer application to solve this issue
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: Employee.findAll()?.personId?.toList()))
                searchBean.searchCriteria.put("localFullName", new SearchConditionCriteriaBean(operand: 'localFullName', value1: sSearch))
            }
            if (employeeName) {
                searchBean.searchCriteria.put("localFullName", new SearchConditionCriteriaBean(operand: 'localFullName', value1: employeeName))
            }
            //fill all employee persons info
            persons = personService.searchPerson(searchBean)?.resultList
            //to prevent get employee info when no PCORE result
            params["personIds[]"] = persons?.id?.toList() ?: [-1L]
        }
        List ids = this.search(params)
        return ids
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return Employee.
     */
    Employee save(GrailsParameterMap params) {
        Employee employeeInstance
        if (params.id) {
            String id = HashHelper.decode(params["id"])
            employeeInstance = Employee.get(id)
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeInstance.version > version) {
                    employeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employee.label', null, 'employee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employee while you were editing")
                    return employeeInstance
                }
            }
            if (!employeeInstance) {
                employeeInstance = new Employee()
                employeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employee.label', null, 'employee', LocaleContextHolder.getLocale())] as Object[], "This employee with ${params.id} not found")
                return employeeInstance
            }
        } else {
            //prevent create employee exit
            if (this.count(params) > 0) {
                employeeInstance = new Employee()
                employeeInstance.errors.reject('employee.person.notFound.error.label', "not found")
                return employeeInstance
            } else {
                employeeInstance = new Employee()
            }
        }

        try {

            if (params["firmEncodedId"]) {
                params["firm.id"] = HashHelper.decode(params["firmEncodedId"])
            }

            //if the user has AOC role, then firm should be selected in create screen. Otherwise, get the firm from session.

            if (!params["firm.id"]) {
                params["firm.id"] = PCPSessionUtils.getValue("firmId")
            }
            employeeInstance.properties = params;

            EmploymentRecord currentEmploymentRecord
            EmployeePromotion currentEmployeeMilitaryRank

            //assign employment record and rank just when create employee
            if (!employeeInstance?.id) {

                employeeInstance.attendanceStatusDate = employeeInstance.employmentDate
                employeeInstance.categoryStatusDate = employeeInstance.employmentDate

                //assign employment record
                currentEmploymentRecord = new EmploymentRecord()
                currentEmploymentRecord.properties = params["employmentRecordData"]
                currentEmploymentRecord.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                if(currentEmploymentRecord.internalOrderDate == null || currentEmploymentRecord.internalOrderDate == PCPUtils.DEFAULT_ZONED_DATE_TIME){
                    currentEmploymentRecord.internalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }
                currentEmploymentRecord.firm = employeeInstance?.firm

                boolean isInternalAssignationValue = params.boolean("employmentRecordData.isInternalAssignationValue")

                if (isInternalAssignationValue) {
                    Department assignedToDepartment = Department.load(params["employmentRecordData.assignedToDepartment.id"])
                    if (assignedToDepartment) {
                        EmployeeInternalAssignation assignation = new EmployeeInternalAssignation()
                        assignation.assignedToDepartment = assignedToDepartment
                        assignation.assignedToDepartmentFromDate = PCPUtils.parseZonedDateTime(params["employmentRecordData"]["assignedToDepartmentFromDate"])
                        assignation.assignedToDepartmentToDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        currentEmploymentRecord.addToEmployeeInternalAssignations(assignation)
                    }
                }

                employeeInstance.addToEploymentRecord(currentEmploymentRecord)

                //assign rank
                currentEmployeeMilitaryRank = new EmployeePromotion()
                currentEmployeeMilitaryRank.properties = params["militaryRankData"]
                currentEmployeeMilitaryRank.firm = employeeInstance?.firm
                if (!currentEmployeeMilitaryRank?.managerialRankDate) {
                    currentEmployeeMilitaryRank?.managerialRankDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }
                if (!currentEmployeeMilitaryRank?.militaryRankTypeDate) {
                    currentEmployeeMilitaryRank?.militaryRankTypeDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }
                if (!currentEmployeeMilitaryRank?.dueDate) {
                    currentEmployeeMilitaryRank?.dueDate = currentEmployeeMilitaryRank?.actualDueDate
                }
                employeeInstance.addToEmployeeMilitaryRank(currentEmployeeMilitaryRank)

                //set that employee is loaned
                LoanRequestRelatedPerson loanRequestRelatedPerson = LoanRequestRelatedPerson.findByRequestedPersonIdAndRecordSource(employeeInstance?.personId, EnumPersonSource.RECEIVED)

                if (loanRequestRelatedPerson) {
                    //add default categoryStatus غير ملتزم
                    employeeInstance.categoryStatus = EmployeeStatusCategory.load(EnumEmployeeStatusCategory.UNCOMMITTED.value)

                    //add default status when create employee منتدب لدي
                    EmployeeStatusHistory employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory.employeeStatus = EmployeeStatus.load(EnumEmployeeStatus.LOAN_IN.value)
                    employeeStatusHistory.fromDate = loanRequestRelatedPerson?.effectiveDate
                    employeeStatusHistory.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    employeeInstance.addToEmployeeStatusHistories(employeeStatusHistory)
                } else if (currentEmploymentRecord?.employmentCategory?.id == EnumEmploymentCategory.STUDENT.value) {

                    //add default categoryStatus غير ملتزم
                    EmployeeStatusCategory employeeStatusCategory = EmployeeStatusCategory.get(EnumEmployeeStatusCategory.UNCOMMITTED.value)

                    //add default status when create employee على راس عمله
                    EmployeeStatus employeeStatus = EmployeeStatus.load(EnumEmployeeStatus.STUDYING.value)

                    if (employeeStatusCategory && employeeStatus) {
                        //add default categoryStatus غير ملتزم
                        employeeInstance.categoryStatus = employeeStatusCategory

                        //add default status when create employee يدرس
                        EmployeeStatusHistory employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory.employeeStatus = employeeStatus
                        employeeStatusHistory.fromDate = employeeInstance?.employmentDate
                        employeeStatusHistory.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        employeeInstance.addToEmployeeStatusHistories(employeeStatusHistory)
                    }


                } else {

                    //add default categoryStatus ملتزم
                    EmployeeStatusCategory employeeStatusCategory = EmployeeStatusCategory.get(EnumEmployeeStatusCategory.COMMITTED.value)

                    //add default status when create employee على راس عمله
                    EmployeeStatus employeeStatus = EmployeeStatus.load(EnumEmployeeStatus.WORKING.value)

                    if (employeeStatusCategory && employeeStatus) {
                        //add default categoryStatus ملتزم
                        employeeInstance.categoryStatus = employeeStatusCategory

                        //add default status when create employee على راس عمله
                        EmployeeStatusHistory employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory.employeeStatus = employeeStatus
                        employeeStatusHistory.fromDate = employeeInstance?.employmentDate
                        employeeStatusHistory.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        employeeInstance.addToEmployeeStatusHistories(employeeStatusHistory)
                    }


                }
            }

            //custom validation
            if (employeeInstance?.joinDate < employeeInstance?.employmentDate || (currentEmploymentRecord?.fromDate && currentEmploymentRecord?.fromDate < employeeInstance?.joinDate) || (currentEmployeeMilitaryRank?.actualDueDate && currentEmployeeMilitaryRank?.actualDueDate < employeeInstance?.employmentDate)) {
                employeeInstance.validate()
                if (employeeInstance?.joinDate < employeeInstance?.employmentDate) {
                    employeeInstance.errors.reject("employee.joinDateError.label")
                }

                if (currentEmploymentRecord?.fromDate && currentEmploymentRecord?.fromDate < employeeInstance?.joinDate) {
                    employeeInstance.errors.reject("employee.departmentDateError.label")
                }

                if (currentEmployeeMilitaryRank?.actualDueDate && currentEmployeeMilitaryRank?.actualDueDate < employeeInstance?.employmentDate) {
                    employeeInstance.errors.reject("employee.actualDueDateError.label")
                }

                return employeeInstance
            }

            //get last applicant inserted and accepted
            Applicant lastApplicant = Applicant.createCriteria().get {
                eq('personId', employeeInstance?.personId)
                applicantCurrentStatus {
                    eq('applicantStatus', EnumApplicantStatus.ACCEPTED)
                }
                maxResults(1)
                order("trackingInfo.dateCreatedUTC", "desc")
            }
            employeeInstance.applicant = lastApplicant

            // to be abel to add details object (currentEmploymentRecord,currentEmployeeMilitaryRank)
            employeeInstance.save(flush: true, failOnError: true);

            if (currentEmploymentRecord) {
                employeeInstance.currentEmploymentRecord = currentEmploymentRecord
            }

            if (currentEmployeeMilitaryRank) {
                employeeInstance.currentEmployeeMilitaryRank = currentEmployeeMilitaryRank
            }

            //todo:check flush
            if (currentEmploymentRecord && currentEmployeeMilitaryRank) {
                employeeInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (employeeInstance?.errors?.allErrors?.size() == 0) {
                employeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }

        return employeeInstance
    }

//    /**
//     * to delete model entry.
//     * @param DeleteBean deleteBean.
//     * @return DeleteBean.
//     * @see DeleteBean.
//     */
//    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
//        try {
//            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
//                Employee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
//                deleteBean.status = true
//            } else if (deleteBean.ids) {
//                Employee.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
//                deleteBean.status = true
//            }
//        }
//        catch (Exception ex) {
//            deleteBean.status = false
//            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
//        }
//        return deleteBean
//
//    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return Employee.
     */
    @Transactional(readOnly = true)
    Employee getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id || params.long("personId")) {
            //search for the applicant instance using the passed params (in case of edit/show):
            PagedResultList results = this.search(params)
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to count of employee depends on params.
     * @param GrailsParameterMap params the search map.
     * @return Integer.
     */
    @Transactional(readOnly = true)
    Integer count(GrailsParameterMap params) {
        Long personId = params.long("personId")
        return Employee.createCriteria().count {
            if (personId) {
                eq('personId', personId)
            }
            eq("firm.id", PCPSessionUtils.getValue("firmId"))
        }
    }

    /**
     * this method used to get the person, profession type, location and other remoting details
     * @param GrailsParameterMap params the search map.
     * @return employee instance
     */
    @Transactional(readOnly = true)
    Employee getInstanceWithRemotingValues(GrailsParameterMap params) {

        if (params.emlpoyeeEncodedId) {
            params["encodedId"] = params.emlpoyeeEncodedId
        }

        Boolean isNewEmployee = params.boolean("isNewEmployee")
        Boolean statusAsList = params.boolean("statusAsList")
        Employee employee = getInstance(params)


        Integer loanCount = LoanRequestRelatedPerson.countByRequestedPersonIdAndRecordSource(params.long("personId"), EnumPersonSource.RECEIVED)
        Integer employeeCount = Employee.countByPersonId(params.long("personId"))

        if (isNewEmployee == true && !employee) {
            employee = new Employee()
            employee.personId = params.long("personId")
            employee.computerNumber = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.profile.Employee", "E", 20)

            //set that employee is loaned
            if (loanCount > 0) {
                employee.transientData.isLoanEmployee = true
            }

        }
        if (employee) {

            //return null if employee is loaned
            if (loanCount > 0 && employeeCount > 0) {
                return null
            }

            //fill employee person information from PCORE
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employee?.personId))
            PersonDTO personDTO = personService.getPerson(searchBean)
            employee.transientData.put("personDTO", personDTO)

            //fill employee person marital status from PCORE
            SearchBean searchBean2 = new SearchBean()
            searchBean2.searchCriteria.put("person.id", new SearchConditionCriteriaBean(operand: 'person.id', value1: employee?.personId))
            searchBean2.searchCriteria.put("isCurrent", new SearchConditionCriteriaBean(operand: 'isCurrent', value1: "true"))
            PersonMaritalStatusDTO personMaritalStatusDTO = personMaritalStatusService?.getPersonMaritalStatus(searchBean2)
            employee.transientData.put("personMaritalStatusDTO", personMaritalStatusDTO)

            //fill employee governorate information from PCORE
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employee?.currentEmploymentRecord?.department?.governorateId))
            GovernorateDTO governorateDTO = governorateService?.getGovernorate(governorateSearchBean)
            employee.transientData.put("governorateDTO", governorateDTO)

            if (employee?.currentEmploymentRecord?.employeeExternalAssignations) {
                //fill employee external organization information from PCORE
                EmployeeExternalAssignation externalAssignation = employee?.currentEmploymentRecord?.employeeExternalAssignations?.last()
                SearchBean organizationSearchBean = new SearchBean()
                organizationSearchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: externalAssignation.assignedToOrganizationId))
                OrganizationDTO assignedToOrganizationDTO = organizationService?.getOrganization(organizationSearchBean)
                externalAssignation.transientData.put("assignedToOrganizationDTO", assignedToOrganizationDTO)
            }

            //fill employee bank organization information from PCORE
            if (employee?.bankBranchId) {
                SearchBean bankBranchSearchBean = new SearchBean()
                bankBranchSearchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employee?.bankBranchId))
                OrganizationDTO bankBranchDTO = organizationService?.getOrganization(bankBranchSearchBean)
                employee.transientData.put("bankBranchDTO", bankBranchDTO)
            }

            if (statusAsList == true) {
                List<EmployeeStatusHistory> employeeStatusHistoryList = employee?.employeeStatusHistories?.findAll {
                    !it.toDate || it.toDate == ps.police.common.utils.v1.PCPUtils.DEFAULT_ZONED_DATE_TIME
                }?.sort { it.employeeStatus.descriptionInfo.localName }
                employee.transientData.employeeStatusList = employeeStatusHistoryList?.join(",")
            }
        }
        return employee
    }

    /**
     * this method used to get the person, profession type, location and other remoting details
     * @param GrailsParameterMap params the search map.
     * @return employee instance
     */
    @Transactional(readOnly = true)
    Map getEmployeeInfo(GrailsParameterMap params) {
        Map data = [:]
        Employee employee = getInstanceWithRemotingValues(params)
        if (employee) {
            List<EmployeeStatusHistory> employeeStatusHistoryList = employee?.employeeStatusHistories?.findAll {
                !it.toDate || it.toDate == ps.police.common.utils.v1.PCPUtils.DEFAULT_ZONED_DATE_TIME
            }?.sort { it.employeeStatus.descriptionInfo.localName }

            data.governorate = employee?.transientData?.governorateDTO?.descriptionInfo?.localName
            data.department = employee?.currentEmploymentRecord?.department?.descriptionInfo?.localName
            data.jobTitle = employee?.currentEmploymentRecord?.jobTitle?.descriptionInfo?.localName
            data.categoryStatus = employee?.categoryStatus?.descriptionInfo?.localName
            data.employeeStatusList = employeeStatusHistoryList?.join(",")
        }
        return data
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
        String nameProperty = params["nameProperty"] ?: "transientData.personDTO.localFullName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []

        try {

            grails.gorm.PagedResultList resultList = this.searchWithRemotingValues(params)
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
     * */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * custom search to find the number of disciplinary requests regarding to employee
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    PagedList customSearch(GrailsParameterMap params) {

        final session = sessionFactory.currentSession

        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        String orderByQuery = ""
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))

        String id = params["id"]
        Long firmId = params.long('firmId') ?: PCPSessionUtils.getValue("firmId")
        Firm firm = Firm.read(firmId)
        String personId = params["personId"]
        String militaryNumber = params["militaryNumber"]
        String militaryRankId = params["militaryRank.id"]
        String governorateId = params["governorateId"]
        String departmentId = params["department.id"]
        String financialNumber = params["financialNumber"]
        String recentCardNo = params["recentCardNo"]
        Boolean employeePromotionList = params.boolean("employeePromotionList")
        Boolean eligibleEmployeeList = params.boolean("eligibleEmployeeList")
        Boolean calculateDueDate = params.boolean("calculateDueDate")
        Map sqlParamsMap = [:]

        //This query used to return the employee with his promotion info to be used in promotion list filter for eligible propose
        def sb = new StringBuilder()
        sb.append(" ")
        sb.append("FROM ")
        sb.append("   military_rank r, ")
        sb.append("   employee_promotion p, ")
        sb.append("   employee emp  ")
        sb.append("   LEFT OUTER JOIN ")
        sb.append("   ( ")
        sb.append("   select ")
        sb.append("   employee_id, ")
        sb.append("   current_employee_military_rank_id, ")
        sb.append("   sum(days_to_reduce) as days_to_reduce, ")
        sb.append("   sum(no_of_disciplinary_request) as no_of_disciplinary_request  ")
        sb.append("   from ")
        sb.append("   ( ")
        sb.append("   SELECT ")
        sb.append("   request.employee_id, ")
        sb.append("   request.current_employee_military_rank_id, ")
        sb.append("   ( ")
        sb.append("   Sum(suspension_request.period_in_month) * 30  ")
        sb.append("                  ) ")
        sb.append("                  AS days_to_reduce, ")
        sb.append("                  0 as no_of_disciplinary_request  ")
        sb.append("               FROM ")
        sb.append("                  PUBLIC.suspension_request, ")
        sb.append("                  PUBLIC.request  ")
        sb.append("               WHERE ")
        sb.append("                  request.request_status = '${EnumRequestStatus.APPROVED}'  ")
        sb.append("                  AND request.status = '${GeneralStatus.ACTIVE}'  ")
        sb.append("                  AND suspension_request.id = request.id  ")
        sb.append("               GROUP BY ")
        sb.append("                  request.employee_id, ")
        sb.append("                  request.current_employee_military_rank_id  ")
        sb.append("               UNION ")
        sb.append("               SELECT ")
        sb.append("                  request.employee_id, ")
        sb.append("                  request.current_employee_military_rank_id, ")
        sb.append("                  ( ")
        sb.append("                     Sum(vacation_request.num_of_days) * 1  ")
        sb.append("                  ) ")
        sb.append("                  AS days_to_reduce, ")
        sb.append("                  0 as no_of_disciplinary_request  ")
        sb.append("               FROM ")
        sb.append("                  PUBLIC.vacation_request, ")
        sb.append("                  PUBLIC.vacation_type, ")
        sb.append("                  PUBLIC.request  ")
        sb.append("               WHERE ")
        sb.append("                  vacation_type.excluded_from_service_period = true  ")
        sb.append("                  AND request.request_status = '${EnumRequestStatus.APPROVED}'  ")
        sb.append("                  AND request.status = '${GeneralStatus.ACTIVE}'  ")
        sb.append("                  AND vacation_request.id = request.id  ")
        sb.append("                  AND vacation_request.vacation_type_id = vacation_type.id  ")
        sb.append("               GROUP BY ")
        sb.append("                  request.employee_id, ")
        sb.append("                  request.current_employee_military_rank_id  ")
        sb.append("               UNION ")
        sb.append("               SELECT ")
        sb.append("                  request.employee_id, ")
        sb.append("                  request.current_employee_military_rank_id, ")
        sb.append("                  ( ")
        sb.append("                     Sum(CAST (disciplinary_record_judgment.value as int)  * disciplinary_record_judgment.base_factor) ) ")
        sb.append("                  AS days_to_reduce, ")
        sb.append("                  count(disciplinary_request.id) as no_of_disciplinary_request  ")
        sb.append("               FROM ")
        sb.append("                  PUBLIC.disciplinary_record_judgment, ")
        sb.append("                  PUBLIC.disciplinary_request, ")
        sb.append("                  PUBLIC.request, ")
        sb.append("                  PUBLIC.disciplinary_judgment  ")
        sb.append("               WHERE ")
        sb.append("                  disciplinary_judgment.excluded_from_eligible_promotion = true  ")
        sb.append("                  AND request.status = '${GeneralStatus.ACTIVE}'  ")
        sb.append("                  AND request.request_status in  ")
        sb.append("                  ('${EnumRequestStatus.APPROVED_BY_WORKFLOW}','${EnumRequestStatus.APPROVED}') ")
        sb.append("                  AND disciplinary_record_judgment.disciplinary_request_id = disciplinary_request.id  ")
        sb.append("                  AND disciplinary_record_judgment.disciplinary_judgment_id = disciplinary_judgment.id  ")
        sb.append("                  AND disciplinary_request.id = request.id  ")
        sb.append("               GROUP BY ")
        sb.append("                  request.employee_id, ")
        sb.append("                  request.current_employee_military_rank_id  ")
        sb.append("            ) ")
        sb.append("            a  ")
        sb.append("         group by ")
        sb.append("            employee_id, ")
        sb.append("            current_employee_military_rank_id  ")
        sb.append("      ) ")
        sb.append("      x  ")
        sb.append("      ON emp.id = x.employee_id  ")
        sb.append("      AND emp.current_employee_military_rank_id = x.current_employee_military_rank_id  ")
        sb.append("   WHERE ")
        sb.append("   emp.status = '${GeneralStatus.ACTIVE}'  ")
        sb.append("   and emp.category_status_id = '${EnumEmployeeStatusCategory.COMMITTED.getValue(firm.code)}'  ")
        sb.append("   and exists  ")
        sb.append("   ( ")
        sb.append("      select ")
        sb.append("         1  ")
        sb.append("      from ")
        sb.append("         employee_promotion m  ")
        sb.append("      where ")
        sb.append("         m.promotion_list_employee_id IS NULL  ")
        sb.append("         and emp.current_employee_military_rank_id = m.id  ")
        sb.append("   ) ")
        sb.append("   And emp.current_employee_military_rank_id = p.id  ")
        sb.append("   AND p.military_rank_id = r.id  ")
        sb.append("")

        String query = sb

        //if statements to check the params which are used in search form
        if (sSearch) {
            query = query + " and ( military_number like :militaryNumberSParam or " +
                    "financial_number like :financialNumberSParam or " +
                    "recent_card_no like :recentCardNoSParam ) "
            sqlParamsMap.put("militaryNumberSParam", "%" + sSearch + "%")
            sqlParamsMap.put("financialNumberSParam", "%" + sSearch + "%")
            sqlParamsMap.put("recentCardNoSParam", "%" + sSearch + "%")
        }

        if (id) {
            query = query + " and emp.id = :idParam  "
            sqlParamsMap.put("idParam", id)
        }

        if (firmId) {
            query = query + " and emp.firm_id = :firmIdParam  "
            sqlParamsMap.put("firmIdParam", firmId)
        }

        if (personId) {
            query = query + " and emp.person_id = :personIdParam  "
            sqlParamsMap.put("personIdParam", Integer.parseInt(personId))
        }
        if (militaryRankId) {
            query = query + " and emp.current_employee_military_rank_id in (select m.id from employee_promotion m where m.military_rank_id = :militaryRankIdParam ) "
            sqlParamsMap.put("militaryRankIdParam", militaryRankId)
        }
        if (departmentId) {
            query = query + " and emp.current_employment_record_id in (select rec.id from employment_record rec where rec.department_id = :departmentIdParam ) "
            sqlParamsMap.put("departmentIdParam", departmentId)
        }
        if (governorateId) {
            query = query + " and emp.current_employment_record_id in (select reco.id from employment_record reco where reco.department_id in(select d.id from department d where d.governorate_id = :governorateIdParam )) "
            sqlParamsMap.put("governorateIdParam", Integer.parseInt(governorateId))
        }
        if (militaryNumber) {
            query = query + " and military_number like :militaryNumberParam  "
            sqlParamsMap.put("militaryNumberParam", "%" + militaryNumber + "%")
        }
        if (financialNumber) {
            query = query + " and financial_number like :financialNumberParam  "
            sqlParamsMap.put("financialNumberParam", "%" + financialNumber + "%")
        }

        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("militaryNumber")) {
            orderByQuery += "ORDER BY emp.military_number ${dir}"
        } else if (columnName?.equalsIgnoreCase("financialNumber")) {
            orderByQuery += "ORDER BY emp.financial_number  ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.noOfDisciplinaryRequest")) {
            orderByQuery += "ORDER BY b.no_of_disciplinary_request  ${dir}"
        } else if (columnName?.equalsIgnoreCase("id")) {
            orderByQuery += "ORDER BY emp.date_created  ${dir}"
        } else if (columnName) {
            orderByQuery += "ORDER BY ${columnName} ${dir}"
        } else {
            orderByQuery += "ORDER BY emp.date_created desc"
        }

        if (employeePromotionList) {
            query = query + " and emp.current_employee_military_rank_id in (select m.id from employee_promotion m where m.promotion_list_employee_id IS NULL ) "
        }

        //sql select variables to be returned, it was split here, not in original query
        // to use count for original query
        Query sqlQuery = session.createSQLQuery(
                """
                SELECT 
                    emp.id , 
                    emp.person_id, 
                    emp.current_employee_military_rank_id, 
                    emp.current_employment_record_id, 
                    emp.military_number, 
                    emp.financial_number,                    
                    COALESCE(x.days_to_reduce,0) as days_to_reduce,  
                    p.due_date_datetime, 
                    r.number_of_year_to_promote,
                    COALESCE(x.no_of_disciplinary_request,0) as no_of_disciplinary_request 
                """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //pagination parameters
        sqlQuery.setMaxResults(max)
        sqlQuery.setFirstResult(offset)

        final queryResults = sqlQuery.list()

        //variables used in below for each
        List<Employee> results = []
        List<Employee> finalList = []
        ZonedDateTime dueDate
        Long numberOfYearToPromote
        Long daysToExclude
        Employee employee
        EmploymentRecord employmentRecord
        EmployeePromotion employeePromotion

        /**
         * do loop on sql result to fill the employee result list
         * when calculate employee dueDate, we should take bellow into consideration:
         * 1- current dueDate + numberOfYearToPromote
         * 2- minus (فترة ترك الرتبة + الاستيداع + الاجازة من غير الراتب)
         */
        queryResults.each { resultRow ->

            //retrieve the dueDate for the employee
            dueDate = PCPUtils.convertTimeStampToZonedDateTime(resultRow[7])

            //retrieve the number of years are needed for the current military rank
            numberOfYearToPromote = resultRow[8] as Long

            //add the numberOfYearToPromote value to due date
            if (numberOfYearToPromote) {
                dueDate = dueDate.plusYears(numberOfYearToPromote)
            }
            daysToExclude = resultRow[6] as Long

            //add the excluded from service period which was calculated from vacation, suspension, disciplinary to due date
            if (daysToExclude) {
                dueDate = dueDate.plusDays(daysToExclude)
            }

            //create employee with sql result data
            employee = new Employee(
                    personId: resultRow[1],
                    militaryNumber: resultRow[4],
                    financialNumber: resultRow[5],
                    transientData: [noOfDisciplinaryRequest: resultRow[9], dueDate: dueDate]
            )
            employee.id = resultRow[0]

            //TODO : include employeePromotion in main query to avoid using select per row
            //get the employee Current rank details
            employeePromotion = EmployeePromotion.get(resultRow[2])
            employee.currentEmployeeMilitaryRank = employeePromotion

            //TODO : include EmploymentRecord in main query to avoid using select per row
            //get the employee Current employment record
            employmentRecord = EmploymentRecord.get(resultRow[3])
            employee?.currentEmploymentRecord = employmentRecord

            //if its eligible employee filter, then the calculated due date should be <= current
            if (eligibleEmployeeList) {
                if (dueDate <= ZonedDateTime.now()) {
                    results.add(employee)
                }
            } else if (calculateDueDate) {
                results.add(employee)
            } else {
                //for exceptional add employee into promotion list
                if (dueDate > ZonedDateTime.now()) {
                    results.add(employee)
                }
            }
        }

        //to store the total count of the absence list instances
        Integer totalCount = 0

        //get total count for all records if we have records (results!=null)
        if (results) {
            Query sqlCountQuery = session.createSQLQuery(""" SELECT count(emp.id) """ + query)
            sqlParamsMap?.each {
                sqlCountQuery.setParameter(it.key.toString(), it.value)
            }
            final queryCountResults = sqlCountQuery.list()
            totalCount = new Integer(queryCountResults[0]?.toString())

            String employeeName = params.remove("employeeName")
            SearchBean searchBean = new SearchBean()
            List<PersonDTO> persons
            if (recentCardNo || sSearch || employeeName) {
                if (recentCardNo) {
                    searchBean.searchCriteria.put("recentCardNo", new SearchConditionCriteriaBean(operand: 'recentCardNo', value1: recentCardNo))
                }
                if (sSearch) {
                    searchBean.searchCriteria.put("localFullName", new SearchConditionCriteriaBean(operand: 'localFullName', value1: sSearch))
                }
                if (employeeName) {
                    searchBean.searchCriteria.put("localFullName", new SearchConditionCriteriaBean(operand: 'localFullName', value1: employeeName))
                }
                //fill all employee persons info
                persons = personService.searchPerson(searchBean)?.resultList
                //to prevent get employee info when no PCORE result
                params["personIds[]"] = persons?.id?.toList() ?: [-1L]
            }
            List personIds = results.personId.toList()
            List governorateIds = results?.currentEmploymentRecord?.department?.governorateId.toList()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
            if (!recentCardNo && !sSearch && !employeeName) {
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
                //fill all employee persons info
                persons = personService.searchPerson(searchBean)?.resultList
            }
            //fill all employee governorates
            List<GovernorateDTO> governorates = governorateService.searchGovernorate(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: governorateIds)]))?.resultList


            results.each { Employee remoteEmployee ->
                remoteEmployee?.transientData?.personDTO = persons.find { it.id == remoteEmployee?.personId }
                remoteEmployee?.transientData?.governorateDTO = governorates.find {
                    it.id == remoteEmployee?.currentEmploymentRecord?.department?.governorateId
                }
                if (remoteEmployee?.transientData?.personDTO) {
                    finalList.add(remoteEmployee)
                }
            }
        }
        //return the paged list result
        return new PagedList(resultList: finalList, totalCount: totalCount)
    }

    /***
     * Save profile status change
     * @param params
     * @return
     */
    Employee saveChangeProfileStatus(GrailsParameterMap params) {
        Employee employeeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            employeeInstance = Employee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeInstance.version > version) {
                    employeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employee.label', null, 'employee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employee while you were editing")
                    return employeeInstance
                }
            }
            if (!employeeInstance) {
                employeeInstance = new Employee()
                employeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employee.label', null, 'employee', LocaleContextHolder.getLocale())] as Object[], "This employee with ${params.id} not found")
                return employeeInstance
            }
        } else {
            if (!employeeInstance) {
                employeeInstance = new Employee()
                employeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employee.label', null, 'employee', LocaleContextHolder.getLocale())] as Object[], "This employee with ${params.id} not found")
                return employeeInstance
            }
        }
        try {
            EnumProfileStatus profileStatus = EnumProfileStatus.valueOf(params.profileStatus)
            if (profileStatus == employeeInstance.profileStatus) {
                employeeInstance.errors.reject('default.not.changed.message', [messageSource.getMessage('employee.profileStatus.label', null, 'profileNotice', LocaleContextHolder.getLocale())] as Object[], 'Profile Status')
                return employeeInstance
            }
            employeeInstance.profileStatus = profileStatus

            // add note
            EmployeeProfileStatusHistory profileStatusHistory = new EmployeeProfileStatusHistory()
            profileStatusHistory.note = params.note
            profileStatusHistory.fromDate = ZonedDateTime.now()
            profileStatusHistory.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            profileStatusHistory.employee = employeeInstance
            profileStatusHistory.employeeProfileStatus = profileStatus

            // search for open status and close it
            EmployeeProfileStatusHistory prevEmployeeInstance = employeeInstance.employeeProfileStatusHistories.find {
                it.toDate == null
            }
            if (prevEmployeeInstance) {
                prevEmployeeInstance.toDate = profileStatusHistory.fromDate
                prevEmployeeInstance.save()
            }
            profileStatusHistory.save(failOnError: true)
            employeeInstance.save(failOnError: true, flush: true);
        }
        catch (Exception ex) {
            log.error("Failed to change employee profile status", ex)
            transactionStatus.setRollbackOnly()
            employeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeeInstance
    }

    /**
     * this function will be used by AOC, when the aoc user need to show employee.
     * @param params
     * @return Employee
     */
    public Employee readInstanceWithRemotingValues(GrailsParameterMap params) {
        Employee employee = null
        String employeeId = null

        if (SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value)) {

            employeeId = HashHelper.decode(params.encodedId)
            if (employeeId) {
                params['firm.id'] = Employee.executeQuery("select employee.firm.id from Employee employee where employee.id=:employeeId", [employeeId: employeeId])?.get(0)
            }

            employee = getInstanceWithRemotingValues(params)
        }
        return employee
    }

}