package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.hibernate.jdbc.Work
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowActionType
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.lookups.CurrencyService
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.pcore.v2.entity.lookups.dtos.v1.CurrencyDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.UnitOfMeasurementDTO
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -manage all disciplinary transactions and get data from domain
 * <h1>Usage</h1>
 * -any service to get disciplinary info or search about disciplinary
 * <h1>Restriction</h1>
 * -must connect with pcore application to get unit and location information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DisciplinaryRequestService {

    MessageSource messageSource
    FormatService formatService
    ManageLocationService manageLocationService
    EmployeeService employeeService
    CurrencyService currencyService
    UnitOfMeasurementService unitOfMeasurementService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService
    /**
     * to represent disciplinary request status
     */
    public static getDisciplinaryStatus = { formatService, DisciplinaryRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.requestStatus?.toString()
        }
        return ""
    }

    /**
     * to represent disciplinary request status
     */
    public static getDisciplinaryId= { formatService, DisciplinaryRequest dataRow, object, params ->
        if (dataRow?.id) {
            return dataRow?.id
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
            [sort: true, search: true, hidden: true, name: "disciplinaryRequestId", type: getDisciplinaryId, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestStatus", type: "enum", source: 'domain', messagePrefix: 'EnumRequestStatus'],
            [sort: false, search: false, hidden: true, name: "status", type: getDisciplinaryStatus, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetExternalOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    public static final List<String> PREVIOUS_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "disciplinaryRequestId", type: getDisciplinaryId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestStatus", type: "enum", source: 'domain', messagePrefix: 'EnumRequestStatus'],
    ]

    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.violations", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.lastViolationDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.disciplinaryJudgments", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestStatusNote", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestStatus", type: "enum", source: 'domain', messagePrefix: 'EnumRequestStatus'],
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
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String disciplinaryCategoryId = params["disciplinaryCategory.id"]
        String disciplinaryJudgmentId = params["disciplinaryJudgment.id"]
        Set disciplinaryJudgmentsIds = params.listString("disciplinaryJudgments.id")
        String employeeId
        if (params["employeeId"]) {
            employeeId = params["employeeId"]
        } else {
            employeeId = params["employee.id"]
        }
        String departmentId = params["department.id"]
        String militaryRankId = params["militaryRank.id"]
        Long locationId = params.long("locationId")
        String note = params["note"]
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        String requestReason = params["requestReason"]
        EnumRequestStatus requestStatus = params["requestStatus"] ? EnumRequestStatus.valueOf(params["requestStatus"]) : null
        List<EnumRequestStatus> requestStatusList = params["requestStatusList"] ? params["requestStatusList"]?.split(",")?.collect{String value->return EnumRequestStatus.valueOf(value)} : []
        List<EnumRequestStatus> excludedRequestStatusList = params["excludedRequestStatusList"] ? params["excludedRequestStatusList"]?.split(",")?.collect{String value->return EnumRequestStatus.valueOf(value)} : []
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String unstructuredLocation = params["unstructuredLocation"]
        Long firmId = params.long("firm.id")
        String disciplinaryListId = params["disciplinaryList.id"]
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return DisciplinaryRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
                    ilike("unstructuredLocation", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
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
                if (disciplinaryCategoryId) {
                    eq("disciplinaryCategory.id", disciplinaryCategoryId)
                }
                if (disciplinaryJudgmentsIds) {
                    disciplinaryJudgments {
                        inList("id", disciplinaryJudgmentsIds)
                    }
                }
                if (disciplinaryJudgmentId) {
                    disciplinaryJudgments {
                        eq("id", disciplinaryJudgmentId)
                    }
                }
                if (employeeId || militaryRankId || departmentId) {
                    employee {
                        if (employeeId) {
                            eq("id", employeeId)
                        }
                        if (departmentId) {
                            currentEmploymentRecord {
                                eq("department.id", departmentId)
                            }
                        }
                        if (militaryRankId) {
                            currentEmployeeMilitaryRank {
                                eq("militaryRank.id", militaryRankId)
                            }
                        }
                    }
                }

                if (locationId) {
                    eq("locationId", locationId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (requestReason) {
                    ilike("requestReason", "%${requestReason}%")
                }
                if (requestStatus) {
                    eq("requestStatus", requestStatus)
                }
                if (requestStatusList) {
                    inList("requestStatus", requestStatusList)
                }
                if (excludedRequestStatusList) {
                    not {
                        inList("requestStatus", excludedRequestStatusList)
                    }
                }
                if (requestStatusNote) {
                    ilike("requestStatusNote", "%${requestStatusNote}%")
                }
                if (requestType) {
                    eq("requestType", requestType)
                }
                if (requesterId) {
                    eq("requester.id", requesterId)
                }
                if (requesterDepartmentId) {
                    eq("requesterDepartment.id", requesterDepartmentId)
                }

                //requestDate
                if (requestDate) {
                    eq("requestDate", requestDate)
                }

                if (requestDateFrom) {
                    ge("requestDate", requestDateFrom)
                }

                if (requestDateTo) {
                    le("requestDate", requestDateTo)
                }

                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }

                if (disciplinaryListId) {
                    disciplinaryJudgments {
                        eq('disciplinaryRecordsList.id', disciplinaryListId)
                    }
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if(internalOrderNumber){
                    eq('internalOrderNumber', internalOrderNumber)
                }
                if(externalOrderNumber){
                    eq('externalOrderNumber', externalOrderNumber)
                }
                if(internalOrderDate){
                    eq('internalOrderDate', internalOrderDate)
                }
                if(externalOrderDate){
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
        PagedResultList pagedResultList = this.search(params)
        if (pagedResultList.resultList) {
            List<String> employeeIds = pagedResultList?.resultList?.employee?.id?.toList()
            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)
            //fill all employee info
            pagedResultList.resultList.each { DisciplinaryRequest disciplinaryRequest ->
                disciplinaryRequest.employee = employees.find { it.id == disciplinaryRequest?.employee?.id }
            }
        }
        return pagedResultList
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchReport(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.searchWithRemotingValues(params)

        String violations = ""
        ZonedDateTime lastViolationDate = null
        String disciplinaryJudgments = ""

        pagedResultList.each { DisciplinaryRequest disciplinaryRequest ->
            violations = ""
            lastViolationDate = null
            disciplinaryJudgments = ""

            //find violations for this disciplinary request
            disciplinaryRequest?.joinedDisciplinaryEmployeeViolations?.each{JoinedDisciplinaryEmployeeViolation joinedDisciplinaryEmployeeViolation ->
                violations += violations.equals("")?joinedDisciplinaryEmployeeViolation?.employeeViolation?.disciplinaryReason?.descriptionInfo?.localName:" ," + joinedDisciplinaryEmployeeViolation?.employeeViolation?.disciplinaryReason?.descriptionInfo?.localName
            }

            //find last violation date for this disciplinary request
            JoinedDisciplinaryEmployeeViolation joinedDisciplinaryEmployeeViolation = JoinedDisciplinaryEmployeeViolation.createCriteria().list(max: 1) {
                and {
                    if(disciplinaryRequest.id){
                        eq("disciplinaryRequest.id", disciplinaryRequest.id)
                    }
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }
                employeeViolation{
                    order("violationDate", "desc")
                }
            }[0]
            lastViolationDate = joinedDisciplinaryEmployeeViolation?.employeeViolation?.violationDate

            //find disciplinary judgments for this disciplinary request
            disciplinaryRequest?.disciplinaryJudgments?.each{DisciplinaryRecordJudgment disciplinaryRecordJudgment ->
                disciplinaryJudgments += disciplinaryJudgments.equals("")?disciplinaryRecordJudgment?.disciplinaryJudgment?.descriptionInfo?.localName:" ," + disciplinaryRecordJudgment?.disciplinaryJudgment?.descriptionInfo?.localName
            }

            disciplinaryRequest?.transientData?.violations = violations
            disciplinaryRequest?.transientData?.lastViolationDate = lastViolationDate
            disciplinaryRequest?.transientData?.disciplinaryJudgments = disciplinaryJudgments
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return DisciplinaryRequest.
     */
    DisciplinaryRequest save(GrailsParameterMap params) {
        DisciplinaryRequest disciplinaryRequestInstance

        //to decode encrypted id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            disciplinaryRequestInstance = DisciplinaryRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (disciplinaryRequestInstance.version > version) {
                    disciplinaryRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('disciplinaryRequest.label', null, 'disciplinaryRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this disciplinaryRequest while you were editing")
                    return disciplinaryRequestInstance
                }
            }
            if (!disciplinaryRequestInstance) {
                disciplinaryRequestInstance = new DisciplinaryRequest()
                disciplinaryRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('disciplinaryRequest.label', null, 'disciplinaryRequest', LocaleContextHolder.getLocale())] as Object[], "This disciplinaryRequest with ${params.id} not found")
                return disciplinaryRequestInstance
            }
        } else {
            disciplinaryRequestInstance = new DisciplinaryRequest()
        }
        try {
            //when update remove old disciplinary record judgments
            //We need it to remove it manually because grails not remove it automatically based on domain relations
            if (disciplinaryRequestInstance?.id) {
                DisciplinaryRecordJudgment.executeUpdate("delete from DisciplinaryRecordJudgment drj where drj.disciplinaryRequest.id = :disciplinaryRequestId", [disciplinaryRequestId: disciplinaryRequestInstance?.id])
                JoinedDisciplinaryEmployeeViolation.executeUpdate("delete from JoinedDisciplinaryEmployeeViolation jev where jev.disciplinaryRequest.id = :disciplinaryRequestId", [disciplinaryRequestId: disciplinaryRequestInstance?.id])
            }

            //assign reasons and judgments
            List employeeViolationIds = params.listString("employeeViolationIds")
            List disciplinaryJudgments = params.listString("disciplinaryJudgment")

            String value
            ZonedDateTime fromDate
            ZonedDateTime toDate
            Long currencyId
            Long unitId
            String orderNo
            String note

            Firm firm = Firm.load(params.long("firm.id"))

            DisciplinaryRecordJudgment disciplinaryRecordJudgment
            DisciplinaryListNote disciplinaryListNote
            EmployeeViolation employeeViolation
            JoinedDisciplinaryEmployeeViolation joinedDisciplinaryEmployeeViolation
            DisciplinaryJudgment disciplinaryJudgment

            List<DisciplinaryReason> disciplinaryReasonList = []

            //add employeeViolation to joinedDisciplinaryEmployeeViolations
            employeeViolationIds.each { employeeViolationId ->

                if (employeeViolationId) {
                    //add disciplinaryReason to list
                    employeeViolation = EmployeeViolation.load(employeeViolationId)
                    disciplinaryReasonList << employeeViolation?.disciplinaryReason

                    //and employeeViolation to joinedDisciplinaryEmployeeViolations
                    joinedDisciplinaryEmployeeViolation = new JoinedDisciplinaryEmployeeViolation()
                    joinedDisciplinaryEmployeeViolation.firm = firm
                    joinedDisciplinaryEmployeeViolation.employeeViolation = employeeViolation
                    disciplinaryRequestInstance.addToJoinedDisciplinaryEmployeeViolations(joinedDisciplinaryEmployeeViolation)
                }

                employeeViolation = null
            }


            disciplinaryJudgments.each { disciplinaryJudgmentId ->
                if (disciplinaryJudgmentId) {
                    disciplinaryRecordJudgment = new DisciplinaryRecordJudgment()

                    disciplinaryRecordJudgment.firm = firm

                    disciplinaryJudgment = DisciplinaryJudgment.load(disciplinaryJudgmentId)
                    disciplinaryRecordJudgment.disciplinaryJudgment = disciplinaryJudgment

                    //set judgmentStatus by default as ADOPTED
                    disciplinaryRecordJudgment.judgmentStatus = EnumJudgmentStatus.NEW

                    value = params["value_${disciplinaryJudgment?.id}"]
                    if (value) {
                        disciplinaryRecordJudgment.value = value
                    }

                    fromDate = PCPUtils.parseZonedDateTime(params["fromDate_${disciplinaryJudgment?.id}"])
                    if (fromDate) {
                        disciplinaryRecordJudgment.fromDate = fromDate
                    } else {
                        disciplinaryRecordJudgment.fromDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    }

                    toDate = PCPUtils.parseZonedDateTime(params["toDate_${disciplinaryJudgment?.id}"])
                    if (toDate) {
                        disciplinaryRecordJudgment.toDate = toDate
                    } else {
                        disciplinaryRecordJudgment.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    }

                    //if disciplinary judgment has currency then we get currency from params else the unit will get
                    if (disciplinaryJudgment?.isCurrencyUnit) {
                        currencyId = params.long("currencyId_${disciplinaryJudgment?.id}")
                        if (currencyId) {
                            disciplinaryRecordJudgment.currencyId = currencyId
                        }
                    } else {
                        unitId = params.long("unitId_${disciplinaryJudgment?.id}")
                        if (unitId) {
                            disciplinaryRecordJudgment.unitId = unitId
                        }
                    }

                    orderNo = params["orderNo_${disciplinaryJudgment?.id}"]
                    note = params["note_${disciplinaryJudgment?.id}"]

                    //add note and order no to DisciplinaryListNote
                    if (orderNo || note) {
                        disciplinaryListNote = new DisciplinaryListNote()
                        disciplinaryListNote.orderNo = orderNo ?: null
                        disciplinaryListNote.note = note ?: null
                        disciplinaryListNote.noteDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                        disciplinaryRecordJudgment.disciplinaryListNote = disciplinaryListNote
                    }

                    //and employeeViolation to joinedDisciplinaryEmployeeViolations
                    disciplinaryReasonList.each { DisciplinaryReason disciplinaryReason ->
                        disciplinaryRecordJudgment.addToDisciplinaryReasons(disciplinaryReason)
                    }

                    //assign current disciplinaryRecordJudgment to list
                    disciplinaryRequestInstance.addToDisciplinaryJudgments(disciplinaryRecordJudgment)

                    disciplinaryListNote = null
                    orderNo = null
                    fromDate = null
                    toDate = null
                    note = null
                    value = null
                    currencyId = null
                    unitId = null
                    disciplinaryJudgment = null

                }
            }

            disciplinaryRequestInstance.properties = params;

            //validate disciplinaryListNote
            disciplinaryRequestInstance.disciplinaryJudgments.each { DisciplinaryRecordJudgment judgment ->
                if (judgment.disciplinaryListNote) {
                    judgment.disciplinaryListNote.validate()
                    if (judgment.disciplinaryListNote.hasErrors()) {
                        formatService.formatDomainErrors(judgment.disciplinaryListNote).each { Map error ->
                            disciplinaryRequestInstance.errors.reject(error.message)
                        }
                    }
                }
            }

            //return if error
            if (disciplinaryRequestInstance.hasErrors() || !disciplinaryRequestInstance?.disciplinaryJudgments) {
                if (!disciplinaryRequestInstance?.disciplinaryJudgments) {
                    disciplinaryRequestInstance.errors.reject("disciplinaryRequest.noDisciplinaryJudgments.label")
                }
                return disciplinaryRequestInstance
            }

            //set current employee info
            Employee employee = disciplinaryRequestInstance?.employee
            disciplinaryRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord;
            disciplinaryRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank

            //set request date as now if null
            if (!disciplinaryRequestInstance?.requestDate) {
                disciplinaryRequestInstance.requestDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            }

            //TODO:move it with approve request
            //make all employee violation as PUNISHED
            disciplinaryRequestInstance.joinedDisciplinaryEmployeeViolations.employeeViolation.each {
                it.violationStatus = EnumViolationStatus.PUNISHED
                it.save(failOnError: true, flush: true)
            }

            disciplinaryRequestInstance= requestService.saveManagerialOrderForRequest(params, disciplinaryRequestInstance)
            disciplinaryRequestInstance.save(failOnError: true, flush: true);

            if(disciplinaryRequestInstance.requestStatus==EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                /**
                 * get  the workflow data
                 */
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        DisciplinaryRequest.getName(),
                        disciplinaryRequestInstance?.id + "",
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
            disciplinaryRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            disciplinaryRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            if (disciplinaryRequestInstance?.errors?.allErrors?.size() == 0) {
                disciplinaryRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }

        return disciplinaryRequestInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<DisciplinaryRequest> disciplinaryRequestList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }
            disciplinaryRequestList = DisciplinaryRequest.findAllByIdInList(ids)
            disciplinaryRequestList.each { DisciplinaryRequest disciplinaryRequest ->
                if (disciplinaryRequest?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete recordJudgment
                    disciplinaryRequest.disciplinaryJudgments.each { DisciplinaryRecordJudgment recordJudgment ->
                        recordJudgment.trackingInfo.status = GeneralStatus.DELETED
                        recordJudgment.save()
                    }
                    //delete disciplinaryRequest
                    disciplinaryRequest.trackingInfo.status = GeneralStatus.DELETED
                    disciplinaryRequest.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (disciplinaryRequestList) {
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return DisciplinaryRequest.
     */
    @Transactional(readOnly = true)
    DisciplinaryRequest getInstance(GrailsParameterMap params) {

        //to decode encrypted id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                DisciplinaryRequest disciplinaryRequest = results[0]
                return disciplinaryRequest
            }
        }
        return null

    }

    DisciplinaryRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        DisciplinaryRequest disciplinaryRequest = this.getInstance(params)
        if (disciplinaryRequest) {
            GrailsParameterMap parameterMap = new GrailsParameterMap(['firm.id':params['firmId'],'id': disciplinaryRequest?.employee?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(parameterMap)
            //fill all employee info
            disciplinaryRequest.employee = employee
            SearchBean searchBean

            Set unitIds = disciplinaryRequest?.disciplinaryJudgments?.unitId?.findAll { it != null }
            Set currencyIds = disciplinaryRequest?.disciplinaryJudgments?.currencyId?.findAll { it != null }

            List<UnitOfMeasurementDTO> unitList = []
            List<CurrencyDTO> currencyList = []

            //get unit from pcore application
            if (unitIds) {
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: unitIds))
                unitList = unitOfMeasurementService.searchUnitOfMeasurement(searchBean)?.resultList
            }

            //get currency from pcore application
            if (currencyIds) {
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: currencyIds))
                currencyList = currencyService.searchCurrency(searchBean)?.resultList
            }

            //loop to fill all remoting values
            disciplinaryRequest.disciplinaryJudgments.each { DisciplinaryRecordJudgment recordJudgment ->

                //fill all currency info
                if (recordJudgment?.currencyId) {
                    recordJudgment.transientData.currencyDTO = currencyList.find { it.id == recordJudgment?.currencyId }
                }

                //fill all unit info
                if (recordJudgment?.unitId) {
                    recordJudgment.transientData.unitDTO = unitList.find { it.id == recordJudgment?.unitId }
                }
            }

        }
        return disciplinaryRequest
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

}