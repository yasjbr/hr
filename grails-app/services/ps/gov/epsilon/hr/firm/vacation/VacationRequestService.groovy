package ps.gov.epsilon.hr.firm.vacation

import grails.databinding.SimpleMapDataBindingSource
import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.enums.workflow.v1.EnumWorkFlowOperation
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.request.RequestExtendExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationTypeService
import ps.gov.epsilon.hr.request.IRequestChangesReflect
import ps.gov.epsilon.workflow.OperationWorkflowSetting
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.CommandParamsMap
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.enums.v1.ContactInfoClassificationEnum
import ps.police.pcore.enums.v1.ContactMethod
import ps.police.pcore.enums.v1.ContactType
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.BorderCrossingPointService
import ps.police.pcore.v2.entity.lookups.DocumentTypeService
import ps.police.pcore.v2.entity.lookups.dtos.v1.BorderCrossingPointDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.DocumentTypeDTO
import ps.police.pcore.v2.entity.person.ContactInfoService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * --this service is aims to create vacation request
 * <h1>Usage</h1>
 * -this service is used to create vacation request
 * <h1>Restriction</h1>
 * -need employee & firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class VacationRequestService implements IRequestChangesReflect {

    MessageSource messageSource
    def formatService
    EmployeeService employeeService
    PersonService personService
    ContactInfoService contactInfoService
    BorderCrossingPointService borderCrossingPointService
    VacationTypeService vacationTypeService
    EmployeeVacationBalanceService employeeVacationBalanceService
    def grailsWebDataBinder
    VacationConfigurationService vacationConfigurationService
    def sessionFactory
    DocumentTypeService documentTypeService
    GovernorateService governorateService
    NotificationService notificationService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService
    FirmSettingService firmSettingService

    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static requestStatusValue = { cService, VacationRequest rec, object, params ->
        if (rec?.requestStatus) {
            return rec?.requestStatus?.toString()
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numOfDays", type: "integer", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "external", type: "boolean", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canCancelRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canStopRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canExtendRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEditRequest", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestStatusValue", type: requestStatusValue, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],

    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numOfDays", type: "integer", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "external", type: "boolean", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
    ]

    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.vacationLocation", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestReason", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numOfDays", type: "integer", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "external", type: "boolean", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
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
        //set domain columns
        String columnName
        List<String> domainColumnsSearch = DOMAIN_COLUMNS
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            domainColumnsSearch = this."${domainColumns}"
        }
        if (column) {
            columnName = domainColumnsSearch[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
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
        Set contactInfos = params.listLong("contactInfos")
        Double currentBalance = params.long("currentBalance")
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String employeeId = params["employee.id"]
        String employeeIdToAddInVacationList = params['employeeIdList']
        ZonedDateTime expectedReturnDate = PCPUtils.parseZonedDateTime(params['expectedReturnDate'])
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime fromDateToAddInVacationList = PCPUtils.parseZonedDateTime(params['fromDateList'])
        Boolean external = params.boolean("external")
        Boolean externalToAddInVacationList = params.boolean("externalList")
        Boolean isStopped = params.boolean("isStopped")
        Integer numOfDays = params.long("numOfDays")
        Integer numOfDaysToAddInVacationList = params.long("numOfDaysList")
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime requestDateToAddInVacationList = PCPUtils.parseZonedDateTime(params['requestDateList'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        List<EnumRequestStatus> requestStatusList = params["requestStatusList"] ? params["requestStatusList"]?.split(",")?.collect { String value -> return EnumRequestStatus.valueOf(value) } : []
        String requestStatusNote = params["requestStatusNote"]
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        ZonedDateTime returnDate = PCPUtils.parseZonedDateTime(params['returnDate'])
        ZonedDateTime returnDateToAddInVacationList = PCPUtils.parseZonedDateTime(params['returnDateList'])
        String securityCoordinationId = params["securityCoordination.id"]
        String stoppedById = params["stoppedBy.id"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime toDateToAddInVacationList = PCPUtils.parseZonedDateTime(params['toDateList'])
        String vacationTypeId = params["vacationType.id"]
        String vacationTypeIdToAddInVacationList = params["vacationTypeIdList"]
        Integer sSearchNumber = params.int("sSearch")
        String sSearchId = params["sSearch"]
        String idList = params.long("idList")
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        ZonedDateTime fromToDateList = PCPUtils.parseZonedDateTime(params['toDateListFrom'])
        ZonedDateTime toToDateList = PCPUtils.parseZonedDateTime(params['toDateListTo'])
        ZonedDateTime fromFromDateList = PCPUtils.parseZonedDateTime(params['fromDateListFrom'])
        ZonedDateTime toFromDateList = PCPUtils.parseZonedDateTime(params['fromDateListTo'])
        String militaryRankId = params["militaryRank.id"] ?: params.long("militaryRankIdList")
        String status = params["status"]
        Long firmId = params.long("firm.id")
        //search for vacations with type : excludedFromServicePeriod
        Boolean excludedFromServicePeriod = params.boolean("excludedFromServicePeriod")

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        //used in case the domain used to support multi request
        String requestType


        List<EnumRequestType> requestTypeList = params.list("requestType[]")?.collect { EnumRequestType.valueOf(it) }




        String threadId = params["threadId"]

        return VacationRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    vacationType {
                        ilike("localName", sSearch)
                    }
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)

                    if (sSearchNumber) {
                        eq("numOfDays", sSearchNumber)
                        eq("id", sSearchId)
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
                if (contactInfos) {
                    contactInfos {
                        inList("id", contactInfos)
                    }
                }
                if (excludedFromServicePeriod) {
                    vacationType {
                        eq("excludedFromServicePeriod", excludedFromServicePeriod)
                    }
                }

                if (idList) {
                    eq("id", idList)
                }
                if (currentBalance) {
                    eq("currentBalance", currentBalance)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (currentRequesterEmploymentRecordId) {
                    eq("currentRequesterEmploymentRecord.id", currentRequesterEmploymentRecordId)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (employeeIdToAddInVacationList) {
                    eq("employee.id", employeeIdToAddInVacationList)
                }
                if (expectedReturnDate) {
                    le("expectedReturnDate", expectedReturnDate)
                }
                if (fromDate) {
                    le("fromDate", fromDate)
                }
                if (fromDateToAddInVacationList) {
                    le("fromDate", fromDateToAddInVacationList)
                }
                if (external) {
                    eq("external", external)
                }
                if (requestTypeList) {
                    inList('requestType', requestTypeList)
                }

                if (externalToAddInVacationList) {
                    eq("external", externalToAddInVacationList)
                }
                if (isStopped) {
                    eq("isStopped", isStopped)
                }
                if (numOfDays) {
                    eq("numOfDays", numOfDays)
                }
                if (numOfDaysToAddInVacationList) {
                    eq("numOfDays", numOfDaysToAddInVacationList)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (requestDate) {
                    le("requestDate", requestDate)
                }
                if (requestDateToAddInVacationList) {
                    le("requestDate", requestDateToAddInVacationList)
                }
                if (requestReason) {
                    ilike("requestReason", "%${requestReason}%")
                }
                if (requestStatus) {
                    eq("requestStatus", requestStatus)
                }

                if (requestStatusNote) {
                    ilike("requestStatusNote", "%${requestStatusNote}%")
                }

                if (requesterId) {
                    eq("requester.id", requesterId)
                }
                if (requesterDepartmentId) {
                    eq("requesterDepartment.id", requesterDepartmentId)
                }
                if (returnDate) {
                    le("returnDate", returnDate)
                }
                if (returnDateToAddInVacationList) {
                    le("returnDate", returnDateToAddInVacationList)
                }
                if (securityCoordinationId) {
                    eq("securityCoordination.id", securityCoordinationId)
                }
                if (stoppedById) {
                    eq("stoppedBy.id", stoppedById)
                }
                if (toDate) {
                    le("toDate", toDate)
                }
                if (toDateToAddInVacationList) {
                    le("toDate", toDateToAddInVacationList)
                }
                if (vacationTypeId) {
                    eq("vacationType.id", vacationTypeId)
                }
                if (vacationTypeIdToAddInVacationList) {
                    eq("vacationType.id", vacationTypeIdToAddInVacationList)
                }

                if (requestStatusList) {
                    not {
                        inList("requestStatus", requestStatusList)
                    }
                }
                //fromDate
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                //fromDate
                if (fromFromDateList) {
                    ge("fromDate", fromFromDateList)
                }
                if (toFromDate) {
                    le("fromDate", toFromDate)
                }
                if (toFromDateList) {
                    le("fromDate", toFromDateList)
                }
                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (fromToDateList) {
                    ge("toDate", fromToDateList)
                }
                if (toToDate) {
                    le("toDate", toToDate)
                }
                if (toToDateList) {
                    le("toDate", toToDateList)
                }
                if (militaryRankId) {
                    currentEmployeeMilitaryRank {
                        militaryRank {
                            eq("id", militaryRankId)
                        }
                    }
                }

                if (threadId) {
                    eq('threadId', threadId)
                }

                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (internalOrderNumber) {
                    eq('internalOrderNumber', internalOrderNumber)
                }
                if (externalOrderNumber) {
                    eq('externalOrderNumber', externalOrderNumber)
                }
                if (internalOrderDate) {
                    eq('internalOrderDate', internalOrderDate)
                }
                if (externalOrderDate) {
                    eq('externalOrderDate', externalOrderDate)
                }

                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }


            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {

                switch (columnName) {
                    case 'vacationType.descriptionInfo.localName':
                        vacationType {
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
                }

            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
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

        String vacationLocation = ""
        SearchBean searchBean
        PagedList pagedReportResultList
        pagedResultList.each { VacationRequest vacationRequest ->
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("max", new SearchConditionCriteriaBean(operand: 'max', value1: "1"))
            searchBean.searchCriteria.put("person.id", new SearchConditionCriteriaBean(operand: 'person.id', value1: vacationRequest?.employee?.personId))
            searchBean.searchCriteria.put("justAddress", new SearchConditionCriteriaBean(operand: 'justAddress', value1: true))
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: vacationRequest?.contactInfos?.toList()))
            pagedReportResultList = contactInfoService.searchContactInfo(searchBean)
            if (pagedReportResultList) {
                vacationLocation = pagedReportResultList?.resultList[0]?.address?.country?.descriptionInfo?.localName
            } else {
                vacationLocation = ""
            }
            vacationRequest?.transientData?.vacationLocation = vacationLocation
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return vacationRequest.
 */
    VacationRequest save(GrailsParameterMap params) {
        VacationRequest vacationRequestInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            vacationRequestInstance = VacationRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (vacationRequestInstance.version > version) {
                    vacationRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('vacationRequest.label', null, 'vacationRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this vacationRequest while you were editing")
                    return vacationRequestInstance
                }
            }
            if (!vacationRequestInstance) {
                vacationRequestInstance = new VacationRequest()
                vacationRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacationRequest.label', null, 'vacationRequest', LocaleContextHolder.getLocale())] as Object[], "This vacationRequest with ${params.id} not found")
                return vacationRequestInstance
            }
        } else {
            vacationRequestInstance = new VacationRequest()
        }
        try {
            vacationRequestInstance.properties = params
            EmployeeVacationBalance employeeVacationBalance = null
            final session = sessionFactory.currentSession
            PagedResultList pagedResultList
            Map sqlParamsMap = [:]

            ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
            ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])

            /**
             * validate the request interval with one year
             */
            if (fromDate?.year != toDate?.year) {
                vacationRequestInstance.errors.reject("vacationRequest.error.date.year.message")
                return vacationRequestInstance
            }

            /**
             * validate the request interval date after join date
             */
            if (fromDate < vacationRequestInstance?.employee?.joinDate) {
                vacationRequestInstance.errors.reject("vacationRequest.error.dateInterval.message")
                return vacationRequestInstance
            }

            /***
             * get the employee form the request instance
             */
            GrailsParameterMap employeeParam = new GrailsParameterMap([id: vacationRequestInstance?.employee?.id, "firm.id": params["firm.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService?.getInstanceWithRemotingValues(employeeParam)
            vacationRequestInstance.employee = employee

            /**
             * validate there is no overlap between vacation requests & check the employee status
             * when take the vacation should be in  working
             */
            String query = "select  " +
                    "a.request_overlap ,  b.status_overlap " +
                    "from  " +
                    "( " +
                    "SELECT  " +
                    "  (count(1)=0) as request_overlap " +
                    "FROM  " +
                    "  request,  " +
                    "  vacation_request " +
                    "WHERE  " +
                    "  vacation_request.id = request.id AND request.request_status!= :requestStatus AND  " +
                    "  request.employee_id = :employeeId AND request.status= :trackingInfoStatus AND " +
                    "  (vacation_request.from_date_datetime,(CASE WHEN vacation_request.return_date_datetime = '0003-03-03 03:03:03' THEN current_date else vacation_request.return_date_datetime end)) overlaps ( :fromDate, :toDate ) " +
                    " ) a, " +
                    " ( " +
                    "  SELECT  " +
                    "  (count(1)=0) status_overlap " +
                    "  FROM  " +
                    "  employee_status,  " +
                    "  employee_status_history " +
                    "  WHERE  " +
                    "  (employee_status_history.from_date_datetime, (CASE WHEN employee_status_history.to_date_datetime = '0003-03-03 03:03:03' THEN current_date else employee_status_history.to_date_datetime end)) overlaps ( :fromDate, :toDate ) and " +
                    "  employee_status_history.employee_status_id = employee_status.id and " +
                    "  employee_status_history.employee_id= :employeeId and " +
                    "  employee_status.id!= :employeeStatusId " +
                    "  ) b   "




            sqlParamsMap = [fromDate     : java.util.Date?.from(fromDate?.toInstant()), toDate: java.util.Date?.from(toDate?.toInstant()),
                            employeeId   : params["employee.id"], employeeStatusId: EnumEmployeeStatus.WORKING.value,
                            requestStatus: EnumRequestStatus.REJECTED.toString(), trackingInfoStatus: GeneralStatus.ACTIVE.toString()]

            /**
             * assign values for parameters
             */
            Query sqlQuery = session?.createSQLQuery(query)
            sqlParamsMap?.each {
                sqlQuery.setParameter(it?.key?.toString(), it?.value)
            }

            /**
             * execute query
             */
            final queryResults = sqlQuery?.list()?.get(0)

            /**
             * in case: there is over lap with another request
             */
            if (!queryResults[0] && params.action != "update") {
                vacationRequestInstance.errors.reject("vacationRequest.error.overlap.message", ["${vacationRequestInstance?.employee}"] as Object[], "")
                return vacationRequestInstance
            }

            /**
             * in case: employee does not on work
             */
            if (!queryResults[1]) {
                vacationRequestInstance.errors.reject("vacationRequest.error.notOnWork.message", ["${vacationRequestInstance?.employee}"] as Object[], "")
                return vacationRequestInstance
            }

            /**
             * to set expected return date and it will be toDate+1
             */
            vacationRequestInstance.returnDate = vacationRequestInstance?.toDate?.plusDays(1)
            vacationRequestInstance.expectedReturnDate = vacationRequestInstance?.toDate?.plusDays(1)

            /**
             * to calculate number of vacation days using fromDate & toDate
             */
            vacationRequestInstance.numOfDays = ChronoUnit.DAYS.between(PCPUtils.parseZonedDateTime(params["fromDate"])?.dateTime?.date, PCPUtils.parseZonedDateTime(params["toDate"])?.dateTime?.date) + 1

            /**
             * to validate request fromDate less than/equal toDate
             */
            if (vacationRequestInstance?.fromDate <= vacationRequestInstance?.toDate) {

                /**
                 * get employee balance
                 */
                Double balance = employeeBalance(params["employee.id"] + "", params["vacationType.id"] + "", vacationRequestInstance?.fromDate?.year + "", GeneralStatus.ACTIVE)
                vacationRequestInstance.currentBalance = balance


                Boolean checkBalance = params.boolean("checkBalance", true)
                if (checkBalance) {
                    /**
                     * assign employee vacation balance
                     */
                    if (balance < vacationRequestInstance?.numOfDays) {
                        vacationRequestInstance.errors.reject('vacationRequest.error.balance.numOfDays.message', ["${vacationRequestInstance?.employee}", "${vacationRequestInstance?.vacationType?.descriptionInfo?.localName}", "${balance}"] as Object[], "")
                        return vacationRequestInstance
                    }

                    /**
                     * check if vacation days less than allowed value for one vacation request
                     */
                    if ((employeeVacationBalance?.vacationConfiguration?.allowedValue > 0) && (employeeVacationBalance?.vacationConfiguration?.allowedValue < vacationRequestInstance.numOfDays)) {
                        vacationRequestInstance.errors.reject('vacationRequest.error.vacation.numberOfDays.greater.than.allowedValue.message')
                        return vacationRequestInstance
                    }
                }

                /**
                 * assign current military rank
                 */
                vacationRequestInstance.currentEmployeeMilitaryRank = vacationRequestInstance?.employee?.currentEmployeeMilitaryRank


                /**
                 * assign current employment record
                 */
                vacationRequestInstance.currentEmploymentRecord = vacationRequestInstance?.employee?.currentEmploymentRecord

                /**
                 * save address
                 */
                params["contactType.id"] = ContactType.PERSONAL.value()
                params["contactMethod.id"] = ContactMethod.OTHER_ADDRESS.value()
                ContactInfoCommand contactInfoCommand = new ContactInfoCommand()
                grailsWebDataBinder.bind contactInfoCommand, params as SimpleMapDataBindingSource
                contactInfoCommand.relatedObjectType = ContactInfoClassificationEnum.PERSON.toString()


                if (params["location"]) {
                    contactInfoCommand.address = new LocationCommand()
                    grailsWebDataBinder.bind contactInfoCommand.address, params["location"] as SimpleMapDataBindingSource
                    contactInfoCommand.paramsMap.put("address", new CommandParamsMap(nameOfParameterKeyInService: "location", nameOfValueInCommand: "address"))

                }
                contactInfoCommand = contactInfoService?.saveContactInfo(contactInfoCommand)
                /**
                 * validate save address for employee
                 */
                if (contactInfoCommand.validate() && contactInfoCommand?.id > 0) {
                    vacationRequestInstance.addToContactInfos(contactInfoCommand?.id)
                }
//                else {
//                    if (params.action != "update") {
//                        vacationRequestInstance.errors.reject('vacationRequest.error.save.contactInfo.message', ["${vacationRequestInstance?.employee}"] as Object[], "")
//                        return vacationRequestInstance
//                    }
//                }
                params.remove("contactMethod.id")

                /**
                 * save phone
                 */
                ContactInfoCommand contactInfoPhoneCommand = new ContactInfoCommand()
                params["contactMethod.id"] = ContactMethod.MOBILE_NUMBER.value()
                params.value = params.phoneNumber
                grailsWebDataBinder.bind contactInfoPhoneCommand, params as SimpleMapDataBindingSource
                contactInfoPhoneCommand.relatedObjectType = ContactInfoClassificationEnum.PERSON.toString()
                contactInfoPhoneCommand = contactInfoService?.saveContactInfo(contactInfoPhoneCommand)

                /**
                 * validate save phone for employee
                 */
                if (contactInfoPhoneCommand.validate() && contactInfoCommand?.id > 0) {
                    vacationRequestInstance.addToContactInfos(contactInfoPhoneCommand?.id)
                }

//                else {
//                    if (params.action != "update") {
//                        vacationRequestInstance.errors.reject('vacationRequest.error.save.contactInfo.message', ["${vacationRequestInstance?.employee}"] as Object[], "")
//                        return vacationRequestInstance
//                    }
//                }

                /**
                 * remove borders Security coordination in case update request
                 */
                if (!vacationRequestInstance?.external) {

                    vacationRequestInstance.securityCoordination = null
                }

                /**
                 * save vacation request
                 */
                Boolean doSave = params.boolean("doSave", true)
                if (doSave) {
                    vacationRequestInstance = requestService.saveManagerialOrderForRequest(params, vacationRequestInstance)
                    vacationRequestInstance.save(failOnError: true, flush: true);
                }


                if (vacationRequestInstance?.requestStatus == EnumRequestStatus.CREATED && params.boolean("saveWithWorkflow")) {
                    /**
                     * in case: if the user has HR role, get the suitable workflow
                     */
                    boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)
                    OperationWorkflowSetting runtimeOperationWorkflowSetting = null
                    if (!vacationRequestInstance.external && !vacationRequestInstance.vacationType.needsExternalApproval) {
                        // settings params should be changed
                        runtimeOperationWorkflowSetting = OperationWorkflowSetting.findByDomain(EnumWorkFlowOperation.DEFAULT_DOES_NOT_NEED_AOC_APPROVAL.getValue())
                    }
                    /**
                     * get  the workflow data
                     */
                    WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                            employee?.id + "", employee?.currentEmploymentRecord?.department?.id + "",
                            employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                            employee?.currentEmploymentRecord?.jobTitle?.id + "",
                            VacationRequest.getName(), vacationRequestInstance?.id + "", !hasHRRole, null, runtimeOperationWorkflowSetting)

                    /**
                     * save workflow path details & update request status
                     */
                    if (hasHRRole) {
                        workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                    }
                }

            } else {
                vacationRequestInstance.errors.reject('vacationRequest.error.date.interval.message')
                return vacationRequestInstance
            }
        } catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            vacationRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            vacationRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            vacationRequestInstance.errors.reject('default.external.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return vacationRequestInstance
    }

    /**
     * to save many model entry.
     * @param GrailsParameterMap params the search map.
     * @return vacationRequest.
     */
    VacationRequest saveAll(GrailsParameterMap params) {
        List employeeIdList = params.listString("employeeIdList")
        VacationRequest vacationRequest = null
        Employee employee = null
        VacationRequest.withTransaction {
            try {
                employeeIdList?.each { String id ->
                    employee = Employee.load(id)
                    params.remove("employee.id")
                    params.remove("person.id")
                    params["employee.id"] = employee?.id
                    params["person.id"] = employee?.personId
                    vacationRequest = save(params)

                    //check if request has error
                    if (vacationRequest?.errors?.hasErrors()) {
                        throw new Exception("${vacationRequest?.errors}");
                    }
                }
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
            }

        }
        return vacationRequest
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return VacationRequest.
     */
    VacationRequest saveOperation(GrailsParameterMap params) {
        VacationRequest vacationRequestInstance
        VacationRequest parentRequestInstance
        if (params.parentRequestId) {
            parentRequestInstance = VacationRequest.get(params["parentRequestId"])
            if (!parentRequestInstance) {
                parentRequestInstance = new VacationRequest()
                parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacationRequest.label', null, 'vacationRequest', LocaleContextHolder.getLocale())] as Object[], "This vacationRequest with ${params.parentRequestId} not found")
                return parentRequestInstance
            }
        } else {
            parentRequestInstance = new VacationRequest()
            parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacationRequest.label', null, 'vacationRequest', LocaleContextHolder.getLocale())] as Object[], "This vacationRequest with ${params.parentRequestId} not found")
            return parentRequestInstance
        }
        try {
            // create a clone from parent request
            vacationRequestInstance = parentRequestInstance.clone()

            // update data from client
            vacationRequestInstance.properties = params

            if (parentRequestInstance.requestType == EnumRequestType.REQUEST_FOR_VACATION_CANCEL ||
                    (vacationRequestInstance.requestType == EnumRequestType.REQUEST_FOR_VACATION_CANCEL && !parentRequestInstance.canCancelRequest) ||
                    (vacationRequestInstance.requestType != EnumRequestType.REQUEST_FOR_VACATION_CANCEL && !parentRequestInstance.canHaveOperation)) {
                throw new Exception("Cannot make any operation on request " + parentRequestInstance.id)
            }

            /**
             * assign employee for request
             */
            Employee employee = parentRequestInstance?.employee
            params["employee.id"] = employee?.id
            if (vacationRequestInstance.requestType in [EnumRequestType.REQUEST_FOR_EDIT_VACATION]) {
                params["doSave"] = false
                vacationRequestInstance = save(params)
                vacationRequestInstance.employee = parentRequestInstance?.employee
                vacationRequestInstance.currentEmploymentRecord = parentRequestInstance?.employee?.currentEmploymentRecord
                vacationRequestInstance.currentEmployeeMilitaryRank = parentRequestInstance?.employee?.currentEmployeeMilitaryRank
                vacationRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            } else if (vacationRequestInstance.requestType == EnumRequestType.REQUEST_FOR_VACATION_EXTENSION) {
                params["doSave"] = false
                params["fromDate"] = params["requestDate"]

                vacationRequestInstance = save(params)
                vacationRequestInstance.employee = parentRequestInstance?.employee
                vacationRequestInstance.vacationType = parentRequestInstance?.vacationType
                vacationRequestInstance.currentEmploymentRecord = parentRequestInstance?.employee?.currentEmploymentRecord
                vacationRequestInstance.currentEmployeeMilitaryRank = parentRequestInstance?.employee?.currentEmployeeMilitaryRank
                // extra info should be instance of extensionInfo
                vacationRequestInstance.extraInfo = new RequestExtendExtraInfo(params.extraInfoData)
                ((RequestExtendExtraInfo) vacationRequestInstance.extraInfo).fromDate = parentRequestInstance?.actualStartDate
                vacationRequestInstance.toDate = parentRequestInstance?.toDate
                if (vacationRequestInstance?.toDate?.isBefore(parentRequestInstance?.toDate)) {
                    vacationRequestInstance.errors.reject("vacationRequest.toDate.less.than.effectiveDate.message")
                    throw new Exception("to date must be more than effectiveDate")
                }

            } else if (vacationRequestInstance.requestType == EnumRequestType.REQUEST_FOR_VACATION_STOP) {
                // extra info should be instance of stopInfo
                vacationRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
                if (vacationRequestInstance?.toDate.isAfter(parentRequestInstance?.toDate)) {
                    vacationRequestInstance.errors.reject("vacationRequest.toDate.more.than.effectiveDate.message")
                    throw new Exception("to date must be less than effectiveDate")
                }
            } else if (vacationRequestInstance.requestType == EnumRequestType.REQUEST_FOR_VACATION_CANCEL) {
                // extra info should be instance of cancelInfo
                vacationRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            }

            vacationRequestInstance?.extraInfo?.request = vacationRequestInstance
            if (!vacationRequestInstance.extraInfo.reason) {
                vacationRequestInstance.extraInfo.reason = vacationRequestInstance?.requestReason
            }
            if (!vacationRequestInstance?.extraInfo?.managerialOrderDate) {
                vacationRequestInstance?.extraInfo?.managerialOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            vacationRequestInstance?.save(flush: true, failOnError: true);

            if (vacationRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        VacationRequest.getName(),
                        vacationRequestInstance?.id + "",
                        !hasHRRole)

                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            parentRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            parentRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            log.error("Failed to save request operation", ex)
            transactionStatus.setRollbackOnly()
            parentRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return vacationRequestInstance
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

            VacationRequest instance = VacationRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo?.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
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
 * @return vacationRequest.
 */
    @Transactional(readOnly = true)
    VacationRequest getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                return results[0]
            }
        }
        return null

    }

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return vacationRequest.
     */
    @Transactional(readOnly = true)
    VacationRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
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
 * to search model entries with remoting value
 * @param GrailsParameterMap params the search map.
 * @return PagedResultList.
 */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList vacationRequestList = search(params)
        return injectRemotingValues(vacationRequestList)
    }

    /**
     *  check configuration setting for selected employee & vacation type
     * @param GrailsParameterMap params the search map.
     * @return vacationRequest
     */
    VacationRequest selectEmployee(GrailsParameterMap params) {
        GrailsParameterMap employeeParam
        GrailsParameterMap vacationConfigurationParam
        GrailsParameterMap employeeVacationBalanceParam
        GrailsParameterMap vacationRequestParam
        GrailsParameterMap vacationTypeParam
        Employee employee = null
        VacationConfiguration vacationConfiguration = null
        EmployeeVacationBalance employeeVacationBalance = null
        VacationType vacationType = null
        VacationRequest vacationRequest = null
        PagedResultList pagedResultList
        Double sumOfDays = 0
        Double balance = 0
        Double vacationTakenCount = 0

        /**
         * create new vacationRequest
         */
        vacationRequest = new VacationRequest()

        /**
         * get selected employee
         */
        employeeParam = new GrailsParameterMap([id: params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        employee = employeeService?.getInstance(employeeParam)
        vacationRequest.employee = employee

        /**
         * get vacation configuration for selected vacation type
         */
        vacationConfigurationParam = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        vacationConfigurationParam["vacationType.id"] = params["vacationType.id"]
        vacationConfigurationParam["militaryRank.id"] = employee?.currentEmployeeMilitaryRank?.militaryRank?.id
        pagedResultList = vacationConfigurationService?.search(vacationConfigurationParam)

        if (pagedResultList?.resultList?.size() > 0) {
            vacationConfiguration = (VacationConfiguration) pagedResultList?.resultList?.get(0)
        }

        /**
         * check if configuration is external to set request external
         */
        if (vacationConfiguration?.isExternal) {
            vacationRequest.external = true
        }

        /**
         * validate if there is a priority for annual leave before taken selected vacation type by vacation configuration
         */
        if (vacationConfiguration?.checkForAnnualLeave) {
            pagedResultList = null
            /**
             * get annual vacation type
             */
            vacationTypeParam = new GrailsParameterMap(['descriptionInfo.localName': "إجازة سنوية"], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            pagedResultList = vacationTypeService?.search(vacationTypeParam)

            if (pagedResultList?.resultList?.size() > 0) {
                vacationType = (VacationType) pagedResultList?.resultList?.get(0)
                vacationRequest.vacationType = vacationType
            }
        } else {
            /**
             * get selected vacation type
             */
            vacationTypeParam = new GrailsParameterMap(['id': params["vacationType.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            vacationRequest.vacationType = vacationTypeService?.getInstance(vacationTypeParam)
        }

        /**
         * get employee vacation balance for annual leave
         */
        balance = employeeBalance(params["employee.id"] + "", params["vacationType.id"] + "", 0 + "", GeneralStatus.ACTIVE)

        if (balance > 0 && (vacationType && vacationType?.id != params["vacationType.id"])) {
            vacationRequest.errors.reject("vacationRequest.error.annualLeavePriority.message")
            return vacationRequest
        }

        /**
         * to get sum of days from all request that is not accepted/rejected for selected employee
         */
        String requestStatuss = EnumRequestStatus.APPROVED.toString() + "," + EnumRequestStatus.REJECTED.toString()
        vacationRequestParam = new GrailsParameterMap([requestStatusList: requestStatuss, "employee.id": params["employee.id"], "vacationType.id": params["vacationType.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        sumOfDays = search(vacationRequestParam)?.resultList?.numOfDays?.sum() ?: 0

        /**
         * calculate current balance for selected employee
         */
        if ((balance - sumOfDays) > 0) {
            vacationRequest.currentBalance = balance - sumOfDays
        } else {
            vacationRequest.errors.reject("vacationRequest.employee.vacationType.selected.balance.message")
            return vacationRequest
        }

        return vacationRequest
    }

    /**
     * create vacation request for selected employee
     * @param GrailsParameterMap params the search map.
     * @return vacationRequest
     */
    VacationRequest getVacationRequest(GrailsParameterMap params) {
        /*
           * create new vacation request
           */
        VacationRequest vacationRequest = new VacationRequest(params)

        /**
         * assign employee object to vacation request
         */
        GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id': params['firmId'], id: params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Employee employee = employeeService?.getInstanceWithRemotingValues(employeeParam)
        vacationRequest.employee = employee

        /**
         * assign vacation type for vacation request
         */
        GrailsParameterMap vacationTypeParam = new GrailsParameterMap([id: params["vacationType.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        VacationType vacationType = vacationTypeService?.getInstance(vacationTypeParam)
        vacationRequest.vacationType = vacationType



        return vacationRequest
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
        String nameProperty = params["nameProperty"] ?: "employee.transientData.personDTO.localFullName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = searchWithRemotingValues(params)
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

        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * get employee vacation balance
     * @param String employee id
     * @param String vacation type id
     * @param String year
     * @return Double
     */
    public Double employeeBalance(String employeeId, String vacationTypeId, String year = 0, GeneralStatus trackingInfoStatus = GeneralStatus.ACTIVE) {

        Short vacationDueYear = Short.parseShort(year)

        /**
         * get employee vacation balance
         */
        List<EmployeeVacationBalance> result = EmployeeVacationBalance.createCriteria().list {
            and {

                /**
                 * get for specify employee
                 */
                eq("employee.id", employeeId)

                /**
                 * vacation Type
                 */
                vacationConfiguration {
                    vacationType {
                        eq("id", vacationTypeId)
                    }
                }

                /**
                 * year
                 */
                if (vacationDueYear > 0) {
                    eq("vacationDueYear", vacationDueYear)
                }

                /**
                 * isCurrent
                 */
                eq("isCurrent", true)

                /**
                 * tracking info  status
                 */
                trackingInfo {
                    eq('status', trackingInfoStatus)
                }
            }
        }

        /**
         * get number of vacation days are still not  approved, rejected or finished.
         */
        final session = sessionFactory.currentSession
        Map sqlParamsMap = [:]

        /**
         * SQL Query
         */
        String query = "select (b.days) as sum from  " +
                "( select  vacation_request.num_of_days as days  , request.status as status  , request.request_status as requestStatus , vacation_request.from_date_datetime as fromDate " +
                "FROM vacation_request,  request  " +
                "  where  request.id=vacation_request.id and   " +
                "  request.employee_id = :employeeId  and vacation_request.vacation_type_id= :vacationTypeId " +
                ")b  " +
                "where b.status= '${GeneralStatus.ACTIVE}' " +
                "and b.requestStatus not in ('${EnumRequestStatus.APPROVED}'," +
                "'${EnumRequestStatus.REJECTED}'," +
                "'${EnumRequestStatus.FINISHED}')  "

        sqlParamsMap = [employeeId    : employeeId,
                        vacationTypeId: vacationTypeId]


        if (vacationDueYear > 0) {
            query += " and b.fromDate between :fromDate  and :toDate "
            sqlParamsMap.putAt("fromDate", PCPUtils.parseDate("01/01/" + year))
            sqlParamsMap.putAt("toDate", PCPUtils.parseDate("31/12/" + year))
        }

        /**
         * assign values for parameters
         */
        Query sqlQuery = session?.createSQLQuery(query)
        sqlParamsMap?.each {
            sqlQuery.setParameter(it?.key?.toString(), it?.value)
        }

        /**
         * execute query
         */
        final queryResults = sqlQuery?.list()


        return ((result && result.size() > 0) ? result[0]?.balance : 0 - queryResults.size() > 0 ? queryResults.get(0) : 0)
    }

    /**
     * get list of vacation & extension request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return list of vacation request
     */

    private List getListOfVacationRequest(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {

        final session = sessionFactory.currentSession
        def list = []
        /**
         *  query to get vacation & vacation extension request that will
         * be expired within date interval
         */
        String query = "  select vacation_request.id as parentId, max(vacation_request .to_date_datetime)  as to_date , " +
                " request.status ,request.request_type ,request.request_status " +
                "  from vacation_request , request   " +
                "  where request.id =vacation_request.id " +
                "   and request.firm_id=:firmId " +
                "  AND request.status= '${GeneralStatus.ACTIVE}' and NOT EXISTS  ( " +
                "  select object_source_id as id " +
                "  from notification " +
                "   where notification_type_id=${EnumNotificationType.MY_NOTIFICATION.value} " +
                "   and object_source_reference in ('${VacationRequest.getName()}')  " +
                "   and object_source_id=request.id " +
                " ) and request.request_status='${EnumRequestStatus.APPROVED}' " +
                "  group by request.id , request.status," +
                "request.request_type,request.request_status ," +
                "vacation_request.id        " +
                "  having max(vacation_request.to_date_datetime)  between :fromDate and :toDate "

        /**
         * fill map parameter
         */
        Map sqlParamsMap = [:]
        sqlParamsMap = [fromDate: PCPUtils.convertZonedDateTimeToTimeStamp(fromDate),
                        toDate  : PCPUtils.convertZonedDateTimeToTimeStamp(toDate),
                        firmId  : firm?.id]

        /**
         * create query to get vacation
         * be expire within date interval
         */
        Query sqlQuery = session.createSQLQuery(query)

        /**
         * assign value to each parameter
         */
        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        /**
         * execute query
         */
        final queryResults = sqlQuery?.list()

        /**
         * fill result into list
         */
        queryResults?.eachWithIndex { def entry, int i ->
            list.add(entry)
        }

        /**
         * return query list
         */
        return list
    }

    /**
     * get list of vacation request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return boolean
     */
    public Boolean createNotification(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {

        //get vacation & extension request for selected date's interval
        List resultList = getListOfVacationRequest(firm, fromDate, toDate).toList()

        // create notification for each vacation request in list
        MessageSource messageSource = formatService.messageSource

        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []
        List<String> messageParamList = []
        List<Map> notificationActionsMap = null
        resultList?.eachWithIndex { entry, index ->

            userTermKeyList = []
            userTermValueList = []

            //notification actions.
            notificationActionsMap = [
                    [action            : "show",
                     controller        : "vacationRequest",
                     label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                     icon              : "icon-eye",

                     notificationParams: [
                             new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[0]}")),
                     ]
                    ]
            ]

            //params for notification text.
            messageParamList = ["${entry[0]}", "${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.dateTime?.date}"]

            //set notification role.
            userTermKeyList.add(UserTerm.ROLE)
            userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

            //save notification.
            requestService?.createRequestNotification("${entry[0]}",
                    VacationRequest.getName(),
                    ZonedDateTime.now()?.minusDays(1),
                    null,
                    userTermKeyList,
                    userTermValueList,
                    notificationActionsMap,
                    EnumNotificationType.MY_NOTIFICATION,
                    "vacationRequest.notification.message",
                    messageParamList)
        }
        return true
    }


    @Override
    void applyRequestChanges(Request requestInstance) {

        VacationRequest vacationRequest = (VacationRequest) requestInstance
        Boolean isCentralizedData = firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.name() + "", requestInstance?.firm?.id)?.toBoolean()

        /**
         * update employee balance
         */
        if (isCentralizedData) {
            Boolean isRequestUpdated = employeeVacationBalanceService.updateEmployeeVacationBalance(
                    vacationRequest?.employee?.id,
                    vacationRequest?.vacationType?.id,
                    vacationRequest?.fromDate?.year?.shortValue(),
                    vacationRequest?.numOfDays?.shortValue()
            )

            if (!isRequestUpdated) {
                log.error("Failed to update balance for vacation $vacationRequest.id")
                throw new Exception("failed to update balance for " + vacationRequest?.id)
            }
        }
    }

    @Override
    void revertRequestChanges(Request requestInstance) {
        VacationRequest vacationRequest = (VacationRequest) requestInstance
        Boolean isCentralizedData = firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.name() + "", requestInstance?.firm?.id)?.toBoolean()
        /**
         * update employee balance
         */
        if (isCentralizedData) {
            Boolean isRequestUpdated = employeeVacationBalanceService.updateEmployeeVacationBalance(
                    vacationRequest?.employee?.id,
                    vacationRequest?.vacationType?.id,
                    vacationRequest?.fromDate?.year?.shortValue(),
                    (-1 * vacationRequest?.numOfDays)?.shortValue()  // multiplied by -1 to revert balance changes
            )
            if (!isRequestUpdated) {
                log.error("Failed to update balance for vacation $vacationRequest.id")
                throw new Exception("failed to update balance for " + vacationRequest?.id)
            }
        }
    }

    @Transactional(readOnly = true)
    List<VacationRequest> getThreadWithRemotingValues(GrailsParameterMap params) {
        //if id is not null then return values from search method
        if (params.threadId) {
            // if there is any specific params, can be used here
            DetachedCriteria criteria = new DetachedCriteria(VacationRequest).build {

            }

            return injectRemotingValues(requestService.getThreadWithRemotingValues(criteria, params))
        }
        return null
    }

    /**
     * inject remoting values into list
     * List without remoting values
     * @return PagedResultList including remoting values
     */
    def injectRemotingValues(def requestList) {
        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<BorderCrossingPointDTO> borderCrossingPointDTOList
        List<DocumentTypeDTO> documentTypeDTOList
        List<GovernorateDTO> governorateDTOList
        List<VacationRequest> vacationRequestList = (List<VacationRequest>) requestList?.resultList


        if (vacationRequestList) {
            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacationRequestList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * to get  border crossing point name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacationRequestList?.securityCoordination?.borderLocationId))
            borderCrossingPointDTOList = borderCrossingPointService?.searchBorderCrossingPoint(searchBean)?.resultList

            /**
             * to get legal Identifier  name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacationRequestList?.securityCoordination?.legalIdentifierId))
            documentTypeDTOList = documentTypeService?.searchDocumentType(searchBean)?.resultList

            //fill employee governorate information from core
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: vacationRequestList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(governorateSearchBean)?.resultList

            vacationRequestList?.each { VacationRequest vacationRequest ->
                vacationRequest.transientData = [:]

                /**
                 * assign personDTO for request
                 */
                vacationRequest.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == vacationRequest?.employee?.personId
                })

                /**
                 * assign borderCrossingPointDTO for request
                 */
                vacationRequest?.securityCoordination?.transientData?.put("borderCrossingPointDTO", borderCrossingPointDTOList?.find {
                    it?.id == vacationRequest?.securityCoordination?.borderLocationId
                })

                /**
                 * assign documentTypeDTO for request
                 */
                vacationRequest?.securityCoordination?.transientData?.put("documentTypeDTO", documentTypeDTOList?.find {
                    it?.id == vacationRequest?.securityCoordination?.legalIdentifierId
                })

                /**
                 * assign for governorateDTO  for employee
                 */
                vacationRequest?.employee?.transientData?.put("governorateDTO", governorateDTOList.find {
                    it.id == vacationRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })
            }
        }
        return requestList
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedList searchCanHaveOperation(GrailsParameterMap params) {

        String vacationTypeId = params['vacationType.id']

        StringBuilder sbWhereStatement = new StringBuilder()
        Map queryParams = [:]

        if (vacationTypeId) {
            sbWhereStatement << "and r.vacationType.id = :vacationTypeId "
            queryParams['vacationTypeId'] = vacationTypeId
        }

        params.sbWhereStatement = sbWhereStatement
        params.queryParams = queryParams

        // set request types
        params[EnumRequestCategory.ORIGINAL.name()] = EnumRequestType.VACATION_REQUEST.name()
        params[EnumRequestCategory.EDIT.name()] = EnumRequestType.REQUEST_FOR_EDIT_VACATION.name()
        params[EnumRequestCategory.EXTEND.name()] = EnumRequestType.REQUEST_FOR_VACATION_EXTENSION.name()
        params[EnumRequestCategory.STOP.name()] = EnumRequestType.REQUEST_FOR_VACATION_STOP.name()
        params[EnumRequestCategory.CANCEL.name()] = EnumRequestType.REQUEST_FOR_VACATION_CANCEL.name()

        params['domainName'] = VacationRequest.class.name

        PagedList vacationRequestList = requestService.searchCanHaveOperation(params)
        return injectRemotingValues(vacationRequestList)
    }


}