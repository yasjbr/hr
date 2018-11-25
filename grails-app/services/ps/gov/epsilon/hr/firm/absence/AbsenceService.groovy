package ps.gov.epsilon.hr.firm.absence

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumDisciplinaryReason
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequest
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequestService
import ps.gov.epsilon.hr.firm.disciplinary.JoinedDisciplinaryEmployeeViolation
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeInternalAssignation
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistoryService
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.firm.settings.FirmSetting
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm

import java.sql.Timestamp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -to manage the employee absence-
 * <h1>Usage</h1>
 * -create new (add), edit and manage the absence-
 * <h1>Restriction</h1>
 * -no delete when the "toDate" is set-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class AbsenceService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    EmployeeService employeeService
    DisciplinaryRequestService disciplinaryRequestService
    NotificationService notificationService
    RequestService requestService

    /**
     * return the number of days for the absence
     */
    public static getNumOfDays = { formatService, Absence rec, object, params ->
        if (rec?.numOfDays > 0) {
            return rec?.numOfDays
        } else {
            def duration = ChronoUnit.DAYS.between(ZonedDateTime.now().toLocalDate().atStartOfDay(), rec?.fromDate.toLocalDate().atStartOfDay())
            String message = formatService.messageSource.getMessage("absence.untilNow.label", null, LocaleContextHolder.getLocale())
            return message + duration.abs()
        }
    }

    /**
     * return the number of days for the absence
     */
    public static getViolationStatusValue = { formatService, Absence rec, object, params ->
        if (rec?.violationStatus) {
            return rec?.violationStatus?.toString()
        } else {
            return ""
        }
    }

    /**
     * return the number of days for the absence
     */
    public static getAbsenceId = { formatService, Absence rec, object, params ->
        if (rec?.id) {
            return rec?.id
        } else {
            return ""
        }
    }

    /**s
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "absenceId", type:getAbsenceId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "absenceReason", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualAbsenceReason", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numOfDays", type: getNumOfDays, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "noticeDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "violationStatus", type: "Enum", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "violationStatusValue", type: getViolationStatusValue, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "absenceReason", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualAbsenceReason", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numOfDays", type: getNumOfDays, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "noticeDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "violationStatus", type: "Enum", source: 'domain'],
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
        List<String> ids = params.listString('ids[]')
        List<String> idsToExclude = params.listString('idsToExclude[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }

        List<Map<String, String>> orderBy = params.list("orderBy")

        ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason absenceReason = params["absenceReason"] ? ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.valueOf(params["absenceReason"]) : null
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String disciplinaryRecordRequestId = params["disciplinaryRecordRequest.id"]
        String employeeId = params["employee.id"]
        String informerId = params["informer.id"]
        Long numOfDays = params.long("numOfDays")
        Long fromNumOfDays = params.long("fromNumOfDays")
        Long toNumOfDays = params.long("toNumOfDays")
        String reasonDescription = params["reasonDescription"]
        String militaryRankId = params["militaryRank.id"]
        String status = params["status"]

        Long firmId = params.long("firm.id")
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        ZonedDateTime noticeDate = PCPUtils.parseZonedDateTime(params['noticeDate'])
        ZonedDateTime fromNoticeDate = PCPUtils.parseZonedDateTime(params['noticeDateFrom'])
        ZonedDateTime toNoticeDate = PCPUtils.parseZonedDateTime(params['noticeDateTo'])
        ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus violationStatus = params["violationStatus"] ? ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.valueOf(params["violationStatus"]) : null

        List<EnumViolationStatus> excludedStatusList = params["excludedStatusList"] ? params["excludedStatusList"]?.split(",")?.collect{String value->return EnumViolationStatus.valueOf(value)} : []

        return Absence.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("reasonDescription", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (idsToExclude) {
                    not {
                        inList("id", idsToExclude)
                    }
                }
                if (absenceReason) {
                    eq("absenceReason", absenceReason)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (disciplinaryRecordRequestId) {
                    eq("disciplinaryRecordRequest.id", disciplinaryRecordRequestId)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (informerId) {
                    eq("informer.id", informerId)
                }
                if (numOfDays) {
                    eq("numOfDays", numOfDays)
                }
                if (fromNumOfDays) {
                    ge("numOfDays", fromNumOfDays)
                }
                if (toNumOfDays) {
                    le("numOfDays", toNumOfDays)
                }
                if (reasonDescription) {
                    ilike("reasonDescription", "%${reasonDescription}%")
                }
                if (employeeId || militaryRankId) {
                    employee {
                        if (employeeId) {
                            eq("id", employeeId)
                        }
                        if (militaryRankId) {
                            currentEmployeeMilitaryRank {
                                eq("militaryRank.id", militaryRankId)
                            }
                        }
                    }
                }
                //nextVerificationDate
                if (noticeDate) {
                    eq("noticeDate", noticeDate)
                }
                if (fromNoticeDate) {
                    ge("noticeDate", fromNoticeDate)
                }
                if (toNoticeDate) {
                    le("noticeDate", toNoticeDate)
                }
                //fromDate
                if (fromDate) {
                    eq("fromDate", fromDate)
                }
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                if (toFromDate) {
                    le("fromDate", toFromDate)
                }
                //toDate
                if (toDate) {
                    eq("toDate", toDate)
                }
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    le("toDate", toToDate)
                }

                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (violationStatus) {
                    eq("violationStatus", violationStatus)
                }
                if(excludedStatusList){
                    not {
                        inList("violationStatus", excludedStatusList)
                    }
                }

            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                // solution of sorting by id problem after id become string
                switch (columnName) {
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
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = search(params)

        //get employee remote details
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)

        //loop on search result and map the employee, disciplinary remote values
        pagedResultList?.resultList?.each { Absence absence ->
            absence?.employee = employeeList?.find { it?.id == absence?.employee?.id }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return Absence.
     */
    Absence save(GrailsParameterMap params) {
        Absence absenceInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            absenceInstance = Absence.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (absenceInstance.version > version) {
                    absenceInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('absence.label', null, 'absence', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this absence while you were editing")
                    return absenceInstance
                }
            }
            if (!absenceInstance) {
                absenceInstance = new Absence()
                absenceInstance.errors.reject('default.not.found.message', [messageSource.getMessage('absence.label', null, 'absence', LocaleContextHolder.getLocale())] as Object[], "This absence with ${params.id} not found")
                return absenceInstance
            }
        } else {
            absenceInstance = new Absence()
        }
        try {
            absenceInstance?.properties = params;

            //in this case, the violationDate is equal to absence from date
            absenceInstance.violationDate = absenceInstance.fromDate

            //in case the toDate is not set, use the default time.
            if (params.toDate == "") {
                absenceInstance?.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            //save the employee instance and current employment record
            Employee employee = absenceInstance?.employee
            if (employee?.currentEmploymentRecord) {
                absenceInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            //save the employee instance and current military rank in the request
            if (employee?.currentEmployeeMilitaryRank) {
                absenceInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }

            final session = sessionFactory.currentSession

            //this query used to check if there are any overlaps between new absence and old absences.
            Query query = session.createSQLQuery("""SELECT  count(*)
                                                    FROM 
                                                    absence ab,
                                                    employee_violation ev  
                                                    where ev.status=:activeStatusParam and 
                                                    ev.employee_id= :employeeIdParam and 
                                                    (ab.from_date_datetime, 
                                                    (CASE WHEN ab.to_date_datetime = '0003-03-03 03:03:03' THEN current_date else ab.to_date_datetime end))  
                                                    overlaps ( :fromDateParam, :toDateParam )  
                                                    and   ab.id=ev.id 
                                                    ${absenceInstance?.id ? (" and ab.id != :absenceIdParam") : ("")} 
                                                 """)
            //get from date in zoneDate format
            ZonedDateTime fromDateParam = PCPUtils.parseZonedDateTime(params["fromDate"])
            //get to date in zoneDate format
            ZonedDateTime toDateParam = PCPUtils.parseZonedDateTime(params["toDate"])


            //incase toDate is null set to current date.
            if (toDateParam == null) {
                toDateParam = ZonedDateTime.now()
            }

            Map sqlParamsMap = [:]

            //set the query params:
            sqlParamsMap.put("employeeIdParam", absenceInstance?.employee?.id)
            sqlParamsMap.put("activeStatusParam", GeneralStatus.ACTIVE.name())
            sqlParamsMap.put("fromDateParam", java.util.Date?.from(fromDateParam?.toInstant()))
            sqlParamsMap.put("toDateParam", java.util.Date?.from(toDateParam?.toInstant()))

            if (absenceInstance?.id) {
                sqlParamsMap.put("absenceIdParam", absenceInstance?.id)
            }

            sqlParamsMap?.each {
                query.setParameter(it.key.toString(), it.value)
            }

            final queryResults = query?.list()

            //get the count of overlapping as integer
            Integer countOverLapping = new Integer(queryResults[0]?.toString())

            //in case there are any overlapping, return error message
            if (countOverLapping > 0) {
                absenceInstance.errors.reject("absence.overlap.error")
                absenceInstance.discard()
                return absenceInstance
            }

            //get the employee status : working
            EmployeeStatus employeeStatusWorking = EmployeeStatus.get(EnumEmployeeStatus.WORKING.value)
            EmployeeStatus employeeStatusAbsence = EmployeeStatus.get(EnumEmployeeStatus.ABSENCE.value)
            EmployeeStatusHistory employeeStatusHistory

            // create new history and update employee status to be absence (new absence)
            if (!params.id) {
                //update employee status history and set the absence status:
                if (employeeStatusAbsence) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory?.employee = absenceInstance?.employee
                    employeeStatusHistory?.fromDate = absenceInstance?.fromDate
                    employeeStatusHistory?.toDate = absenceInstance?.toDate
                    employeeStatusHistory?.employeeStatus = employeeStatusAbsence
                    employeeStatusHistory.save(flush: true, failOnError: true)
                    absenceInstance?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                }
                if (employeeStatusWorking) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory = EmployeeStatusHistory.createCriteria().list() {
                        eq('employeeStatus.id', employeeStatusWorking.id)
                        eq('employee.id', absenceInstance?.employee?.id)
                        eq('toDate', PCPUtils.DEFAULT_ZONED_DATE_TIME)
                        order("trackingInfo.dateCreatedUTC", "desc")
                    }[0]
                    if (employeeStatusHistory) {
                        employeeStatusHistory?.toDate = absenceInstance?.fromDate
                        employeeStatusHistory?.save(flush: true, failOnError: true)
                    }
                }

            } else {
                if (employeeStatusAbsence) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory = EmployeeStatusHistory.createCriteria().list() {
                        eq('employeeStatus.id', employeeStatusAbsence.id)
                        eq('employee.id', absenceInstance?.employee?.id)
                        order("trackingInfo.dateCreatedUTC", "desc")
                    }[0]
                    if (employeeStatusHistory) {
                        employeeStatusHistory?.fromDate = absenceInstance?.fromDate
                        employeeStatusHistory?.toDate = absenceInstance?.toDate
                        employeeStatusHistory?.save(flush: true, failOnError: true)
                    }
                }
            }

            //save the disciplinary judgments if there are any:
            absenceInstance?.disciplinaryReason = DisciplinaryReason.get(EnumDisciplinaryReason.ABSENCE_REASON.value);

            //validate the employee to except the date null value
            absenceInstance?.employee?.validate()
            absenceInstance.save(flush: true, failOnError: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            absenceInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return absenceInstance
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
            Absence instance = Absence.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && (instance?.violationStatus == EnumViolationStatus.NEW) && (instance?.trackingInfo?.status != GeneralStatus.DELETED)) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save(flush: true)
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('virtualDelete.error.fail.delete.label')
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
     * @return Absence.
     */
    @Transactional(readOnly = true)
    Absence getInstance(GrailsParameterMap params) {
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
     * this method used to get the person, profession type, location remoting info
     * @param applicant
     * @return
     */
    @Transactional(readOnly = true)
    Absence getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                Absence absence = results[0]

                JoinedDisciplinaryEmployeeViolation record = JoinedDisciplinaryEmployeeViolation.findByEmployeeViolation(absence)
                if (record) {
                    //get the disciplinary remote values
                    GrailsParameterMap disciplinaryParams = new GrailsParameterMap(["id": record?.disciplinaryRequest?.id], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
                    DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService?.getInstanceWithRemotingValues(disciplinaryParams)
                    absence?.transientData.put("disciplinaryRequest", disciplinaryRequest)
                }
                return absence
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
        String nameProperty = params["nameProperty"] ?: "employee.transientData.personDTO.localFullName"

        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: ["id","employee.transientData.personDTO.localFullName"]
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
     * this service is used to filter the absences which may be added to list
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedList customListSearch(GrailsParameterMap params) {
        final session = sessionFactory.currentSession

        String orderByQuery = ""
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer totalCount = 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        Map sqlParamsMap = [:]

        String id = params["id"]

        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason absenceReason = params["absenceReason"] ? ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.valueOf(params["absenceReason"]) : null
        Long numOfDays = params.long("numOfDays")
        Long firmId = PCPSessionUtils.getValue("firmId")

        String fromDate = params['fromDate']
        Timestamp fromFromDate = PCPUtils.parseTimestamp(params['fromDateFrom'])
        Timestamp toFromDate = PCPUtils.parseTimestamp(params['fromDateTo'])

        String toDate = params['toDate']
        Timestamp fromToDate = PCPUtils.parseTimestamp(params['toDateFrom'])
        Timestamp toToDate = PCPUtils.parseTimestamp(params['toDateTo'])

        String noticeDate = params['noticeDate']
        Timestamp fromNoticeDate = PCPUtils.parseTimestamp(params['noticeDateFrom'])
        Timestamp toNoticeDate = PCPUtils.parseTimestamp(params['noticeDateTo'])

        //search absence with criteria :
        // - no disciplinaryRecordRequest
        // - not already added to list
        String query = "FROM absence ab where ab.id not " +
                "in (select absence_id from absence_list_employee ) " +
                "and ab.disciplinary_record_request_id IS NULL " +
                "and ab.status ='${GeneralStatus.ACTIVE}' "

        //if statements to check the params
        if (sSearch) {
            query = query + " and (absence_reason like :absenceReasonSParam ) " +
                    sqlParamsMap.put("absenceReasonSParam", "%" + sSearch + "%")
        }

        if (id) {
            query = query + " and ab.id = :idParam  "
            sqlParamsMap.put("idParam", id)
        }
        if (firmId) {
            query = query + " and ab.firm_id = :firmIdParam  "
            sqlParamsMap.put("firmIdParam", firmId)
        }
        if (employeeId) {
            query = query + " and ab.employee_id = :employeeIdParam  "
            sqlParamsMap.put("employeeIdParam", employeeId)
        }
        if (militaryRankId) {
            query = query + " and ab.employee_id in (select id from employee where current_employee_military_rank_id in (select id from employee_promotion where military_rank_id = :militaryRankIdParam) )  "
            sqlParamsMap.put("militaryRankIdParam", militaryRankId)
        }
        if (numOfDays) {
            query = query + " and ab.num_of_days = :numOfDaysValueParam  "
            sqlParamsMap.put("numOfDaysValueParam", numOfDays)
        }
        if (absenceReason) {
            query = query + " and ab.absence_reason = :absenceReasonParam  "
            sqlParamsMap.put("absenceReasonParam", absenceReason.toString())
        }

        //check 3 cases of send date created > = <
        if (fromDate) {
            query = query + " and to_char(ab.from_date_datetime,'dd/MM/yyyy')  = :fromDate "
            sqlParamsMap.put("fromDate", fromDate)
        }
        if (fromFromDate) {
            query = query + " and ab.from_date_datetime >= :fromFromDate "
            sqlParamsMap.put("fromFromDate", fromFromDate)
        }
        if (toFromDate) {
            query = query + " and ab.from_date_datetime <= :toFromDate "
            sqlParamsMap.put("toFromDate", toFromDate)
        }

        //check 3 cases of send date created > = <
        if (toDate) {
            query = query + " and to_char(ab.to_date_datetime,'dd/MM/yyyy')  = :toDate "
            sqlParamsMap.put("toDate", toDate)
        }
        if (fromToDate) {
            query = query + " and ab.to_date_datetime >= :fromToDate "
            sqlParamsMap.put("fromToDate", fromToDate)
        }
        if (toToDate) {
            query = query + " and ab.to_date_datetime <= :toToDate "
            sqlParamsMap.put("toToDate", toToDate)
        }

        //check 3 cases of send date created > = <
        if (noticeDate) {
            query = query + " and to_char(ab.notice_date_datetime,'dd/MM/yyyy')  = :noticeDate "
            sqlParamsMap.put("noticeDate", noticeDate)
        }
        if (fromNoticeDate) {
            query = query + " and ab.notice_date_datetime >= :fromNoticeDate "
            sqlParamsMap.put("fromNoticeDate", fromNoticeDate)
        }
        if (toNoticeDate) {
            query = query + " and ab.notice_date_datetime <= :toNoticeDate "
            sqlParamsMap.put("toNoticeDate", toNoticeDate)
        }

        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("absenceReason")) {
            orderByQuery += "ORDER BY ab.absence_reason ${dir}"
        }//to apply sorting & sorting direction into sql query
        else if (columnName?.equalsIgnoreCase("employee")) {
            orderByQuery += "ORDER BY ab.employee_id ${dir}"
        } else if (columnName?.equalsIgnoreCase("fromDate")) {
            orderByQuery += "ORDER BY ab.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("numOfDays")) {
            orderByQuery += "ORDER BY ab.num_of_days  ${dir}"
        } else if (columnName?.equalsIgnoreCase("toDate")) {
            orderByQuery += "ORDER BY ab.to_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("noticeDate")) {
            orderByQuery += "ORDER BY ab.notice_date_datetime  ${dir}"
        } else if (columnName) {
            orderByQuery += "ORDER BY ${columnName} ${dir}"
        } else {
            orderByQuery += "ORDER BY ab.date_created desc"
        }

        Query sqlQuery = session.createSQLQuery(
                """
                SELECT
                    ab.id ,
                    ab.employee_id ,
                    ab.absence_reason,
                    ab.num_of_days,
                    COALESCE( NULLIF(ab.from_date_datetime,'0003-03-03 03:03:03') ) as from_date_datetime,
                    COALESCE( NULLIF(ab.to_date_datetime,'0003-03-03 03:03:03') ) as to_date_datetime,
                    COALESCE( NULLIF(ab.notice_date_datetime,'0003-03-03 03:03:03') ) as notice_date_datetime
              """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }
        sqlQuery.setMaxResults(max)
        sqlQuery.setFirstResult(offset)
        final queryResults = sqlQuery.list()

        List<Absence> results = []
        // Transform resulting rows to a map with key organisationName.


        queryResults.each { resultRow ->
            Absence absence = new Absence(
                    absenceReason: resultRow[2],
                    numOfDays: resultRow[3],
                    fromDate: PCPUtils.convertTimeStampToZonedDateTime(resultRow[4]),
                    toDate: PCPUtils.convertTimeStampToZonedDateTime(resultRow[5]),
                    noticeDate: PCPUtils.convertTimeStampToZonedDateTime(resultRow[6]))
            absence.id = resultRow[0]

            Employee employee = Employee.get(resultRow[1])
            absence.employee = employee
            results.add(absence)
        }

        //get total count for all records
        if (results) {
            Query sqlCountQuery = session.createSQLQuery(""" SELECT count(ab.id) """ + query)
            sqlParamsMap?.each {
                sqlCountQuery.setParameter(it.key.toString(), it.value)
            }
            final queryCountResults = sqlCountQuery.list()
            totalCount = new Integer(queryCountResults[0]?.toString())

            //get employee remote details
            List<String> employeeIds = results?.employee?.id
            GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
            List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)
            results?.each { Absence absence ->
                absence?.employee = employeeList?.find { it?.id == absence?.employee?.id }
            }

        }
        return new PagedList(resultList: results, totalCount: totalCount)
    }

    /**
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return absence.
     */
    @Transactional(readOnly = true)
    Absence getPreCreateInstance(GrailsParameterMap params) {
        Absence absence = new Absence(params)
        //CHECK if employee has request in [progress or approved] requests

        GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id':params['firmId'],id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

        //check if the employee current status category is COMMITTED or not
        if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
            absence.errors.reject('request.employeeUncommitted.error.label')
        } else {
            absence?.employee = employee
            absence?.noticeDate = ZonedDateTime.now()
            absence?.currentEmploymentRecord = employee?.currentEmploymentRecord
        }
        return absence
    }

    /**
     * get list of absence that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return list of absence
     */
    private List getListOfAbsence(Firm firm) {
        final session = sessionFactory.currentSession
        def list = []
        //write query to get all absences that exceed max days of absence

        //get the max days of absence which is allowed in the firm:
        Integer maxAbsenceDays = FirmSetting.findByPropertyNameAndFirm(EnumFirmSetting.MAX_ABSENCE_DAYS.value, firm)?.propertyValue?.toInteger()

        if (maxAbsenceDays) {
            String query = "" +
                    "SELECT   ab.id, " +
                    "         ev.status, " +
                    "         COALESCE( NULLIF(ab.to_date_datetime,'0003-03-03 03:03:03') ) AS to_date_datetime " +
                    "FROM     absence ab, " +
                    "         employee_violation ev " +
                    "WHERE     " +
                    "ab.id = ev.id and " +
                    "ev.firm_id = :firmId and " +
                    "ev.status= '${GeneralStatus.ACTIVE}' " +
                    "AND " +
                    "         ( " +
                    "                SELECT Extract(day FROM ( " +
                    "                       CASE " +
                    "                              WHEN ab.to_date_datetime = '0003-03-03 03:03:03' THEN CURRENT_DATE " +
                    "                              ELSE ab.to_date_datetime " +
                    "                       END)-ab.from_date_datetime) AS diff_days) >= :maxAbsenceDays " +
                    "       AND NOT EXISTS (SELECT object_source_id AS id " +
                    "                       FROM   notification " +
                    "                       WHERE  notification_type_id = ${EnumNotificationType.MY_NOTIFICATION.value} " +
                    "                              AND object_source_reference = '${Absence.getName()}' " +
                    "                              AND object_source_id = ev.id ) " +
                    "GROUP BY ab.id , " +
                    "         ev.status"

            Query sqlQuery = session.createSQLQuery(query)

            //fill map parameter
            Map sqlParamsMap = [maxAbsenceDays: maxAbsenceDays, firmId: firm?.id]
            //assign value to each parameter
            sqlParamsMap?.each {
                sqlQuery.setParameter(it.key.toString(), it.value)
            }

            //execute query
            final queryResults = sqlQuery?.list()
            //fill result into list
            queryResults?.eachWithIndex { def entry, int i ->
                list.add(entry)
            }
        }else{
            throw new Exception('EnumFirmSetting.MAX_ABSENCE_DAYS not set')
        }
        //return query list
        return list
    }

    /**
     * get list of absence that exceed the limit of absence max days in firm
     * @params from date
     * @params to date
     * @return boolean
     */
    public Boolean createAbsenceNotification(Firm firm) {

        //get absence for selected date's interval

        List resultList = getListOfAbsence(firm).toList()

        GrailsParameterMap notificationParams
        //create notification for each absence in list

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
                     controller        : "absence",
                     label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                     icon              : "icon-eye",

                     notificationParams: [
                             new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[0]}")),
                     ]
                    ]
            ]

            //set notification role
            userTermKeyList.add(UserTerm.ROLE)
            userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

            //save notification.
            requestService?.createRequestNotification("${entry[0]}",
                    Absence.getName(),
                    ZonedDateTime.now()?.minusDays(1),
                    null,
                    userTermKeyList,
                    userTermValueList,
                    notificationActionsMap,
                    EnumNotificationType.MY_NOTIFICATION,
                    "absence.notification.message",
                    messageParamList,
                    [withEmployeeName: true])
        }

        return true
    }
}