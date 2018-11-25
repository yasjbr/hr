package ps.gov.epsilon.hr.firm.dispatch

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
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.request.RequestExtendExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.request.IRequestChangesReflect
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationMajorDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to hold/manage the dispatch requests.
 * <h1>Usage</h1>
 * -used to create, edit, extend, stop and cancel any dispatch request-
 * <h1>Restriction</h1>
 * -delete when status = NEW
 * -edit when status = NEW
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DispatchRequestService implements IRequestChangesReflect {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService
    LocationService locationService
    EducationMajorService educationMajorService
    ManageLocationService manageLocationService
    EmployeeService employeeService
    WorkFlowProcessService workFlowProcessService
    def sessionFactory
    NotificationService notificationService
    RequestService requestService
    PersonService personService
    GovernorateService governorateService

    /**
     * return the dispatch request id to be used in the create new dispatch extension or stop request
     */
    public static getDispatchRequestId = { cService, DispatchRequest rec, object, params ->
        if (rec?.id) {
            return rec?.id
        } else {
            return null
        }
    }

    /**
     * return the dispatch request id to be used in the create new dispatch extension or stop request
     */
    public static getDispatchEducationMajor = { cService, DispatchRequest rec, object, params ->
        if (rec?.educationMajorId) {
            return rec?.transientData.educationMajorDTO.descriptionInfo.localName
        } else if (rec?.educationMajorName) {
            return rec?.educationMajorName
        } else {
            return rec?.trainingName
        }
    }

    /**
     * return the dispatch request id to be used in the create new dispatch extension or stop request
     */
    public static getDispatchOrganization = { cService, DispatchRequest rec, object, params ->
        if (rec?.organizationId) {
            return rec?.transientData.organizationDTO.descriptionInfo.localName
        } else {
            return rec?.organizationName
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
            [sort: true, search: true, hidden: true, name: "dispatchRequestId", type: getDispatchRequestId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "periodInMonths", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "nextVerificationDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "organization", type: getDispatchOrganization, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "educationMajorOrTraining", type: getDispatchEducationMajor, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canCancelRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canStopRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canExtendRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEditRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "includedInList", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "dispatchType", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "periodInMonths", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "organization", type: getDispatchOrganization, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "educationMajorOrTraining", type: getDispatchEducationMajor, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.locationDTO", type: "String", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LITE_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "dispatchType", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "periodInMonths", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "organization", type: getDispatchOrganization, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "educationMajorOrTraining", type: getDispatchEducationMajor, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.locationDTO", type: "String", source: 'domain'],
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
        String dispatchListEmployeeId = params["dispatchListEmployee.id"]
        ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType dispatchType = params["dispatchType"] ? ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType.valueOf(params["dispatchType"]) : null
        String employeeId = params["employee.id"]
        Long locationId = params.long("locationId")
        Long educationMajorId = params.long("educationMajorId")
        Long organizationId = params.long("organizationId")
        String parentRequestId = params["parentRequestId"]
        Short periodInMonths = params.long("periodInMonths")
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        Long firmId = params.long("firm.id")
        String unstructuredLocation = params["unstructuredLocation"]
        String status = params["status"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])
        ZonedDateTime nextVerificationDate = PCPUtils.parseZonedDateTime(params['nextVerificationDate'])
        ZonedDateTime fromNextVerificationDate = PCPUtils.parseZonedDateTime(params['nextVerificationDateFrom'])
        ZonedDateTime toNextVerificationDate = PCPUtils.parseZonedDateTime(params['nextVerificationDateTo'])
        String sSearchNumber = params["sSearch"]
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        String militaryRankId = params["militaryRank.id"]
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []
        String threadId = params["threadId"]
        List<EnumRequestType> requestTypeList = params.list("requestType[]")?.collect { EnumRequestType.valueOf(it) }
        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])
        return DispatchRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("requestReason", sSearch)
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
                if (dispatchListEmployeeId) {
                    eq("dispatchListEmployee.id", dispatchListEmployeeId)
                }
                if (dispatchType) {
                    eq("dispatchType", dispatchType)
                }
                if (locationId) {
                    eq("locationId", locationId)
                }
                if (educationMajorId) {
                    eq("educationMajorId", educationMajorId)
                }

                if (organizationId) {
                    eq("organizationId", organizationId)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (periodInMonths) {
                    eq("periodInMonths", periodInMonths)
                }
                if (requestDate) {
                    le("requestDate", requestDate)
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
                if (requestType) {
                    eq("requestType", requestType)
                }
                if (requesterId) {
                    eq("requester.id", requesterId)
                }
                if (requesterDepartmentId) {
                    eq("requesterDepartment.id", requesterDepartmentId)
                }
                //nextVerificationDate
                if (nextVerificationDate) {
                    eq("nextVerificationDate", nextVerificationDate)
                }
                if (fromNextVerificationDate) {
                    ge("nextVerificationDate", fromNextVerificationDate)
                }
                if (toNextVerificationDate) {
                    le("nextVerificationDate", toNextVerificationDate)
                }


                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
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
                    ge("toDate", toDate)
                }
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    le("toDate", toToDate)
                }

                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
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
                if (excludeRequestStatusList) {
                    not {
                        inList("requestStatus", excludeRequestStatusList)
                    }
                }
                if (requestTypeList) {
                    inList('requestType', requestTypeList)
                }
                if (threadId) {
                    eq('threadId', threadId)
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
        return injectRemotingValues(pagedResultList)
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedList searchCanHaveOperation(GrailsParameterMap params) {

        String dispatchTypeId = params['dispatchType.id']

        StringBuilder sbWhereStatement = new StringBuilder()
        Map queryParams = [:]

        if (dispatchTypeId) {
            sbWhereStatement << "and r.dispatchType.id = :dispatchTypeId "
            queryParams['dispatchTypeId'] = dispatchTypeId
        }

        params.sbWhereStatement = sbWhereStatement
        params.queryParams = queryParams

        // set request types
        params[EnumRequestCategory.ORIGINAL.name()] = EnumRequestType.DISPATCH_REQUEST.name()
        params[EnumRequestCategory.EDIT.name()] = EnumRequestType.DISPATCH_EDIT_REQUEST.name()
        params[EnumRequestCategory.EXTEND.name()] = EnumRequestType.DISPATCH_EXTEND_REQUEST.name()
        params[EnumRequestCategory.STOP.name()] = EnumRequestType.DISPATCH_STOP_REQUEST.name()
        params[EnumRequestCategory.CANCEL.name()] = EnumRequestType.DISPATCH_CANCEL_REQUEST.name()

        params['domainName'] = DispatchRequest.class.name

        PagedList dispatchRequestList = requestService.searchCanHaveOperation(params)
        return injectRemotingValues(dispatchRequestList)
    }

    /**
     * inject remoting values into list
     * List without remoting values
     * @return PagedResultList including remoting values
     */
    def injectRemotingValues(def requestList) {
        SearchBean searchBean
        List<PersonDTO> personList
        List<OrganizationDTO> organizationDTOList
        List<EducationMajorDTO> educationMajorDTOList
        List<LocationDTO> locationDTOList
        List<GovernorateDTO> governorateDTOList
        List<DispatchRequest> dispatchRequestList = (List<DispatchRequest>) requestList?.resultList

        if (dispatchRequestList) {

            /**
             * get list of organizationId from list of  organization
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: dispatchRequestList?.organizationId))
            organizationDTOList = organizationService?.searchOrganization(searchBean)?.resultList

            /**
             * get list of educationMajorId from list of  educationMajor
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: dispatchRequestList?.educationMajorId))
            educationMajorDTOList = educationMajorService?.searchEducationMajor(searchBean)?.resultList

            /**
             * get list of educationMajorId from list of  educationMajor
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: dispatchRequestList?.locationId))
            locationDTOList = locationService?.searchLocation(searchBean)?.resultList

            /**
             * to get governorate DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: dispatchRequestList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(searchBean)?.resultList

            /**
             * to get person DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: dispatchRequestList?.employee?.personId))
            personList = personService?.searchPerson(searchBean)?.resultList

            dispatchRequestList?.each { DispatchRequest dispatchRequest ->

                /**
                 * assign personDTO to each employee
                 */
                dispatchRequest.employee.transientData.put("personDTO", personList?.find {
                    it?.id == dispatchRequest?.employee?.personId
                })

                /**
                 * assign governorateDTO to each employee
                 */
                dispatchRequest.employee.transientData.put("governorateDTO", governorateDTOList?.find {
                    it?.id == dispatchRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })

                dispatchRequest?.transientData?.organizationDTO = organizationDTOList?.find {
                    it?.id == dispatchRequest?.organizationId
                }
                dispatchRequest?.transientData?.locationDTO = locationDTOList?.find {
                    it?.id == dispatchRequest?.locationId
                }
                dispatchRequest?.transientData?.educationMajorDTO = educationMajorDTOList?.find {
                    it?.id == dispatchRequest?.educationMajorId
                }
            }
        }
        return requestList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return DispatchRequest.
     */
    DispatchRequest save(GrailsParameterMap params) {

        DispatchRequest dispatchRequestInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            dispatchRequestInstance = DispatchRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (dispatchRequestInstance.version > version) {
                    dispatchRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('dispatchRequest.label', null, 'dispatchRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this dispatchRequest while you were editing")
                    return dispatchRequestInstance
                }
            }
            if (!dispatchRequestInstance) {
                dispatchRequestInstance = new DispatchRequest()
                dispatchRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchRequest.label', null, 'dispatchRequest', LocaleContextHolder.getLocale())] as Object[], "This dispatchRequest with ${params.id} not found")
                return dispatchRequestInstance
            }
        } else {
            dispatchRequestInstance = new DispatchRequest()
        }
        try {

            dispatchRequestInstance.properties = params;

            if (dispatchRequestInstance?.fromDate <= dispatchRequestInstance?.toDate) {
                //in case the toDate is not set, use the default time.
                if (params.toDate == "") {
                    dispatchRequestInstance?.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }
                //save the employee instance and current employment record
                Employee employee = dispatchRequestInstance?.employee
                if (employee?.currentEmploymentRecord) {
                    dispatchRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
                }
                //save the employee instance and current military rank in the request
                if (employee?.currentEmployeeMilitaryRank) {
                    dispatchRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
                }
                final session = sessionFactory.currentSession
                //this query used to check if there are any overlaps between new absence and old absences.
                Query query = session.createSQLQuery("""SELECT  count(*)
                                                    FROM 
                                                    dispatch_request dr,
                                                    request r  
                                                    where r.status=:activeStatusParam and 
                                                    r.request_status!= :requestStatusParams and 
                                                    r.employee_id= :employeeIdParam and 
                                                    (dr.from_date_datetime, 
                                                    (CASE WHEN dr.to_date_datetime = '0003-03-03 03:03:03' THEN current_date else dr.to_date_datetime end))  
                                                    overlaps ( :fromDateParam, :toDateParam )  
                                                    and   dr.id=r.id 
                                                    ${
                    dispatchRequestInstance?.id ? (" and dr.id != :dispatchRequestIdParam") : ("")
                }
                                                 """)
                //get from date in zoneDate format
                ZonedDateTime fromDateParam = dispatchRequestInstance?.fromDate
                //get to date in zoneDate format
                ZonedDateTime toDateParam = dispatchRequestInstance?.toDate


                //in case toDate is null set as current date.
                if (toDateParam == null || toDateParam == PCPUtils.DEFAULT_ZONED_DATE_TIME) {
                    toDateParam = ZonedDateTime.now()
                }

                Map sqlParamsMap = [:]

                //set the query params:
                sqlParamsMap.put("employeeIdParam", dispatchRequestInstance?.employee?.id)
                sqlParamsMap.put("activeStatusParam", GeneralStatus.ACTIVE.name())
                sqlParamsMap.put("requestStatusParams", EnumRequestStatus.REJECTED.name())
                sqlParamsMap.put("fromDateParam", java.util.Date?.from(fromDateParam?.toInstant()))
                sqlParamsMap.put("toDateParam", java.util.Date?.from(toDateParam?.toInstant()))

                if (dispatchRequestInstance?.id) {
                    sqlParamsMap.put("dispatchRequestIdParam", dispatchRequestInstance?.id)
                }

                sqlParamsMap?.each {
                    query.setParameter(it.key.toString(), it.value)
                }

                final queryResults = query?.list()

                //get the count of overlapping as integer
                Integer countOverLapping = new Integer(queryResults[0]?.toString())

                //in case there are any overlapping, return error message
                if (countOverLapping > 0) {
                    dispatchRequestInstance.errors.reject("dispatchRequest.overlap.error")
                    dispatchRequestInstance.discard()
                    return dispatchRequestInstance
                }

                //save the related location of dispatch Request
                if (params.long("location.country.id")) {
                    params["location.withWrapper"] = true
                    LocationCommand locationCommand = manageLocationService?.saveLocation(params["location"])
                    if (locationCommand?.id) {
                        dispatchRequestInstance?.locationId = locationCommand?.id
                        //assign reference id of location from core
                    } else {
                        dispatchRequestInstance.errors.reject("location.save.error");
                        return dispatchRequestInstance
                    }
                }

                dispatchRequestInstance= requestService.saveManagerialOrderForRequest(params, dispatchRequestInstance)
                dispatchRequestInstance.save(flush: true, failOnError: true);


                if(dispatchRequestInstance?.requestStatus== EnumRequestStatus.CREATED) {
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
                            DispatchRequest.getName(),
                            dispatchRequestInstance?.id + "",
                            !hasHRRole)

                    //save workflow process
                    if (hasHRRole) {
                        workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                    }
                }

            } else {
                dispatchRequestInstance.errors.reject('request.dateRangeError.label')
                return dispatchRequestInstance
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            dispatchRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            dispatchRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            dispatchRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return dispatchRequestInstance
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return DispatchRequest.
     */
    DispatchRequest saveOperation(GrailsParameterMap params) {
        DispatchRequest dispatchRequestInstance
        DispatchRequest parentRequestInstance
        if (params.parentRequestId) {
            parentRequestInstance = DispatchRequest.get(params["parentRequestId"])
            if (!parentRequestInstance) {
                parentRequestInstance = new DispatchRequest()
                parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchRequest.label', null, 'dispatchRequest', LocaleContextHolder.getLocale())] as Object[], "This dispatchRequest with ${params.parentRequestId} not found")
                return parentRequestInstance
            }
        } else {
            parentRequestInstance = new DispatchRequest()
            parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchRequest.label', null, 'dispatchRequest', LocaleContextHolder.getLocale())] as Object[], "This dispatchRequest with ${params.parentRequestId} not found")
            return parentRequestInstance
        }
        try {
            // create a clone from parent request
            dispatchRequestInstance = parentRequestInstance.clone()

            // update data from client
            dispatchRequestInstance.properties = params

            if (parentRequestInstance.requestType == EnumRequestType.DISPATCH_CANCEL_REQUEST ||
                    (dispatchRequestInstance.requestType == EnumRequestType.DISPATCH_CANCEL_REQUEST && !parentRequestInstance.canCancelRequest) ||
                    (dispatchRequestInstance.requestType != EnumRequestType.DISPATCH_CANCEL_REQUEST && !parentRequestInstance.canHaveOperation)) {
                throw new Exception("Cannot make any operation on request " + parentRequestInstance.id)
            }

            /**
             * assign employee for request
             */
            Employee employee = parentRequestInstance?.employee
            params["employee.id"] = employee?.id

            if (dispatchRequestInstance.requestType in [EnumRequestType.DISPATCH_EDIT_REQUEST]) {
                params["doSave"] = false
                dispatchRequestInstance = save(params)
                dispatchRequestInstance.employee = parentRequestInstance?.employee
                dispatchRequestInstance.currentEmploymentRecord = parentRequestInstance?.employee?.currentEmploymentRecord
                dispatchRequestInstance.currentEmployeeMilitaryRank = parentRequestInstance?.employee?.currentEmployeeMilitaryRank
                dispatchRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            } else if (dispatchRequestInstance.requestType == EnumRequestType.DISPATCH_EXTEND_REQUEST) {

                params["doSave"] = false
                params.putAll(dispatchRequestInstance.properties)

                params.remove("encodedId")

                dispatchRequestInstance = this.save(params)

                // extra info should be instance of extensionInfo
                dispatchRequestInstance.extraInfo = new RequestExtendExtraInfo(params.extraInfoData)
                ((RequestExtendExtraInfo) dispatchRequestInstance.extraInfo).fromDate = parentRequestInstance?.actualStartDate
                dispatchRequestInstance.toDate = parentRequestInstance?.toDate
                if (dispatchRequestInstance?.toDate?.isBefore(parentRequestInstance?.toDate)) {
                    dispatchRequestInstance.errors.reject("dispatchRequest.toDate.less.than.effectiveDate.message")
                    throw new Exception("to date must be more than effectiveDate")
                }
            } else if (dispatchRequestInstance.requestType == EnumRequestType.DISPATCH_STOP_REQUEST) {
                // extra info should be instance of stopInfo
                dispatchRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
                if (dispatchRequestInstance?.toDate.isAfter(parentRequestInstance?.toDate)) {
                    dispatchRequestInstance.errors.reject("dispatchRequest.toDate.more.than.effectiveDate.message")
                    throw new Exception("to date must be less than effectiveDate")
                }
            } else if (dispatchRequestInstance.requestType == EnumRequestType.DISPATCH_CANCEL_REQUEST) {
                // extra info should be instance of cancelInfo
                dispatchRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            }

            dispatchRequestInstance?.extraInfo?.request = dispatchRequestInstance
            if (!dispatchRequestInstance.extraInfo.reason) {
                dispatchRequestInstance.extraInfo.reason = dispatchRequestInstance?.requestReason
            }
            if (!dispatchRequestInstance?.extraInfo?.managerialOrderDate) {
                dispatchRequestInstance?.extraInfo?.managerialOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            dispatchRequestInstance= requestService.saveManagerialOrderForRequest(params, dispatchRequestInstance)


            dispatchRequestInstance?.save(flush: true, failOnError: true);

            if (dispatchRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        DispatchRequest.getName(),
                        dispatchRequestInstance?.id + "",
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
        return dispatchRequestInstance
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
            DispatchRequest instance = DispatchRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && (instance.requestStatus == EnumRequestStatus.CREATED) && (instance?.trackingInfo?.status != GeneralStatus.DELETED)) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save(flush: true)
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('interview.deleteMessage.label')
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
     * @return DispatchRequest.
     */
    @Transactional(readOnly = true)
    DispatchRequest getInstance(GrailsParameterMap params) {
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
            grails.gorm.PagedResultList resultList = this.searchWithRemotingValues(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

    /**
     * this method used to get the person, profession type, location remoting info
     * @param applicant
     * @return
     */
    @Transactional(readOnly = true)
    DispatchRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * this method used to get the employee current status
     * @param params
     * @return List < EmployeeStatusHistory >
     */
    @Transactional(readOnly = true)
    List<EmployeeStatusHistory> getCurrentStatus(GrailsParameterMap params) {
        Employee employee = Employee.get(params["employeeId"])
        EmployeeStatusCategory employeeStatusCategory = EmployeeStatusCategory.createCriteria().get {
            eq('id', ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.value)
            eq('firm.id', employee?.firm?.id)
        }
        //get the dispatch status
        DescriptionInfo descriptionInfo = new DescriptionInfo(localName: messageSource.getMessage("dispatchRequest.employee.status", null, 'dispatchRequest', LocaleContextHolder.getLocale()), latinName: "Dispatched")
        //search for employee status with dispatch status
        EmployeeStatus employeeStatus = EmployeeStatus.createCriteria().get {
            eq('latinName', descriptionInfo?.latinName)
            eq('firm.id', employee?.firm?.id)
            eq('employeeStatusCategory', employeeStatusCategory)
        }
        //get the employee status histories, with dispatched status and with no toDate
        List<EmployeeStatusHistory> employeeStatusHistories = EmployeeStatusHistory.createCriteria().list {
            eq('employeeStatus.id', employeeStatus?.id)
            eq('toDate', PCPUtils.getDEFAULT_ZONED_DATE_TIME())
            eq('employee.id', employee.id)
        }
        //return the list
        return employeeStatusHistories
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

        //to be used in tabs
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }

        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return dispatchRequest.
     */
    @Transactional(readOnly = true)
    DispatchRequest getPreCreateInstance(GrailsParameterMap params) {
        DispatchRequest dispatchRequest = new DispatchRequest(params)
        //CHECK if employee has request in [progress or approved] requests
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED, EnumRequestStatus.APPROVED, EnumRequestStatus.CANCELED, EnumRequestStatus.OVERRIDEN]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            dispatchRequest.errors.reject('request.employeeHasRequest.error.label')
        } else {
            GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id':params['firmId'],id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

            //check if the employee current status category is COMMITTED or not
            if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
                dispatchRequest.errors.reject('request.employeeUncommitted.error.label')
            } else {
                dispatchRequest?.employee = employee
                dispatchRequest?.currentEmploymentRecord = employee?.currentEmploymentRecord
                dispatchRequest?.requestDate = ZonedDateTime.now()
            }
        }
        return dispatchRequest
    }

    /**
     * get list of dispatch & extension request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return list of dispatch request
     */
    private List getListOfDispatchRequest(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {
        final session = sessionFactory.currentSession
        def list = []
        /**
         * write query to get dispatch & dispatch extension request that will
         * be expire within date interval
         */
        String query = " SELECT b.parentId, " +
                "       b.status, " +
                "       Max(b.to_date_datetime) AS to_date, " +
                "       b.request_type, " +
                "       b.request_status " +
                "FROM   (SELECT dispatch_request.id AS parentId, " +
                "               dispatch_request.to_date_datetime, " +
                "               request.status, " +
                "               request.request_type, " +
                "               request.request_status " +
                "        FROM   dispatch_request, " +
                "               request " +
                "        WHERE  request.id = dispatch_request.id " +
                "               AND request.firm_id = :firmId " +
                "        )b " +
                "WHERE  b.status = '${GeneralStatus.ACTIVE}' " +
                "       AND NOT EXISTS (SELECT object_source_id AS id " +
                "                       FROM   notification " +
                "                       WHERE  notification_type_id = ${EnumNotificationType.MY_NOTIFICATION.value} " +
                "                              AND object_source_reference IN ( '${DispatchRequest.getName()}') " +
                "                              AND object_source_id = b.parentId) " +
                "       AND b.request_status = '${EnumRequestStatus.APPROVED}' " +
                "GROUP  BY b.parentId, " +
                "          b.status, " +
                "          b.request_type, " +
                "          b.request_status " +
                "HAVING Max(b.to_date_datetime) BETWEEN :fromDate AND :toDate  "

        /**
         * fill map parameter
         */
        Map sqlParamsMap = [:]
        sqlParamsMap = [firmId  : firm?.id,
                        fromDate: PCPUtils.convertZonedDateTimeToTimeStamp(fromDate),
                        toDate  : PCPUtils.convertZonedDateTimeToTimeStamp(toDate)]

        /**
         * create query to get dispatch & dispatch extension request that will
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
     * get list of dispatch request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return boolean
     */
    public Boolean createDispatchRequestNotification(Firm firm, ZonedDateTime fromDate, ZonedDateTime toDate) {

        /**
         * get dispatch & extension request for selected date's interval
         */
        List resultList = getListOfDispatchRequest(firm, fromDate, toDate).toList()

        GrailsParameterMap notificationParams
        /**
         * create notification for each dispatch request in list
         */
        MessageSource messageSource = formatService.messageSource
        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []
        List<String> messageParamList = []
        List<Map> notificationActionsMap = null

        resultList?.eachWithIndex { entry, index ->


            userTermKeyList = []
            userTermValueList = []

            if (entry[3]) {

                //notification action.
                notificationActionsMap = [
                        [action            : "show",
                         controller        : "dispatchExtensionRequest",
                         label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                         icon              : "icon-eye",

                         notificationParams: [
                                 new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[3]}")),
                         ]
                        ]
                ]
                //params for notification text.
                messageParamList = ["${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.dateTime?.date}"]

                // set notification role
                userTermKeyList.add(UserTerm.ROLE)
                userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

                //save notification.
                requestService?.createRequestNotification("${entry[3]}",
                        DispatchRequest.getName(),
                        ZonedDateTime.now()?.minusDays(1),
                        null,
                        userTermKeyList,
                        userTermValueList,
                        notificationActionsMap,
                        EnumNotificationType.MY_NOTIFICATION,
                        "dispatchRequest.notification.message", messageParamList,
                        [withEmployeeName: true])


            } else {

                //notification actions.
                notificationActionsMap = [
                        [action            : "show",
                         controller        : "dispatchRequest",
                         label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                         icon              : "icon-eye",

                         notificationParams: [
                                 new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[0]}")),
                         ]
                        ]
                ]

                //params for notification text.
                messageParamList = ["${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.dateTime?.date}"]

                //set notification role
                // .
                userTermKeyList.add(UserTerm.ROLE)
                userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

                //save notification.
                requestService?.createRequestNotification("${entry[0]}",
                        DispatchRequest.getName(),
                        ZonedDateTime.now()?.minusDays(1),
                        null,
                        userTermKeyList,
                        userTermValueList,
                        notificationActionsMap,
                        EnumNotificationType.MY_NOTIFICATION,
                        "dispatchRequest.notification.message",
                        messageParamList,
                        [withEmployeeName: true])
            }
        }
        return true
    }

    @Override
    void applyRequestChanges(Request dispatchRequest) {
        // no changes will be applied
    }

    @Override
    void revertRequestChanges(Request requestInstance) {

        println "Do Revert Request Changes!!!"
        //revert employee status (dispatch) and make it working again:

        DispatchRequest dispatchRequest = (DispatchRequest)requestInstance
        //get the employee status : Dispatch
        EmployeeStatus employeeStatusDispatched = EmployeeStatus.get(EnumEmployeeStatus.DISPATCHED.getValue(dispatchRequest?.firm?.code))
        //get the employee status : working
        EmployeeStatus employeeStatusWorking = EmployeeStatus.get(EnumEmployeeStatus.WORKING.getValue(dispatchRequest?.firm?.code))
        EmployeeStatusHistory employeeStatusHistory

        //update employee status history to close the dispatch status and open new working status
        if (employeeStatusWorking) {
            employeeStatusHistory = new EmployeeStatusHistory()
            employeeStatusHistory?.employee = dispatchRequest?.employee
            employeeStatusHistory?.fromDate = dispatchRequest?.toDate
            //the date of close(stop) the dispatch
            employeeStatusHistory?.employeeStatus = employeeStatusWorking
            employeeStatusHistory?.transientData.put("firm", dispatchRequest?.firm);
            employeeStatusHistory.save(flush: true, failOnError: true)
            dispatchRequest?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
        }
        if (employeeStatusDispatched) {
            employeeStatusHistory = new EmployeeStatusHistory()
            employeeStatusHistory = EmployeeStatusHistory.createCriteria().list() {
                eq('employeeStatus.id', employeeStatusDispatched.id)
                eq('employee.id', dispatchRequest?.employee?.id)
                eq('toDate', PCPUtils.DEFAULT_ZONED_DATE_TIME)
                order("trackingInfo.dateCreatedUTC", "desc")
            }[0]
            if (employeeStatusHistory) {
                employeeStatusHistory?.toDate = dispatchRequest?.toDate
                // the date of close(stop) the dispatch
                employeeStatusHistory?.save(flush: true, failOnError: true)
            }
        }
    }


    @Transactional(readOnly = true)
    List<DispatchRequest> getThreadWithRemotingValues(GrailsParameterMap params) {

        //if id is not null then return values from search method
        if (params.threadId) {
            // if there is any specific params, can be used here
            DetachedCriteria criteria = new DetachedCriteria(DispatchRequest).build {

            }
            def requestList = requestService.getThreadWithRemotingValues(criteria, params)
            println "${requestList}"
            return injectRemotingValues(requestList)
        }
        return null
    }

}