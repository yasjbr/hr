package ps.gov.epsilon.hr.firm.allowance

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.profile.Employee
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
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * --this service is aims to create allowance request for employee
 * <h1>Usage</h1>
 * --this service is used to create allowance request for employee
 * <h1>Restriction</h1>
 * -need a firm & employee created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class AllowanceRequestService implements IRequestChangesReflect{

    MessageSource messageSource
    def formatService
    PersonService personService
    GovernorateService governorateService
    PersonRelationShipsService personRelationShipsService
    WorkFlowProcessService workFlowProcessService
    def sessionFactory
    RequestService requestService

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
            [sort: true, search: true, hidden: false, name: "allowanceType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.personRelationShipsName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canCancelRequest", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canStopRequest", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canExtendRequest", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canEditRequest", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "includedInList", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
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
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "allowanceType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.personRelationShipsName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualStartDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualEndDate", type: "ZonedDate", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LITE_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "allowanceType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "allowanceType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestReason", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatusNote", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.personRelationShipsName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualStartDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualEndDate", type: "ZonedDate", source: 'domain'],
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
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }
        List<Map<String, String>> orderBy = params.list("orderBy")
        String allowanceTypeId = params["allowanceType.id"]
        String allowanceTypeIdRequest = params["allowanceTypeId"]
        String allowanceTypeIdReject = params["allowanceType.idReject"]
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime effectiveDateReject = PCPUtils.parseZonedDateTime(params['effectiveDateReject'])
        ZonedDateTime effectiveDateDate = PCPUtils.parseZonedDateTime(params['effectiveDateDate'])
        String threadId = params["threadId"]
        String employeeId = params["employee.id"]
        String employeeIdRequest = params["employeeId"]
        String employeeIdReject = params["employee.idReject"]
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatusReject = params["requestStatusReject"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatusReject"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        ZonedDateTime sSearchDate = PCPUtils.parseZonedDateTime(params["sSearch"])


        ZonedDateTime fromEffectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime toEffectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])

        ZonedDateTime fromEffectiveDateList = PCPUtils.parseZonedDateTime(params['fromEffectiveDateList'])
        ZonedDateTime toEffectiveDateList = PCPUtils.parseZonedDateTime(params['toEffectiveDateList'])

        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])

        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        String militaryRankId = params["militaryRank.id"]
        String status = params["status"]

        String sSearchNumber = params["sSearch"]

        Long firmId= params.long('firm.id')?:PCPSessionUtils.getValue("firmId")

        List<EnumRequestType> requestTypeList= params.list("requestType[]")?.collect{EnumRequestType.valueOf(it)}

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return AllowanceRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    allowanceType {
                        descriptionInfo {
                            ilike("localName", sSearch)
                        }
                    }

                    if (sSearchDate) {
                        eq('requestDate', sSearchDate)
                        eq('effectiveDate', sSearchDate)
                    }

                    if (sSearchNumber) {
                        eq("id", sSearchNumber)
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
                if (allowanceTypeId) {
                    eq("allowanceType.id", allowanceTypeId)
                }
                if (allowanceTypeIdRequest) {
                    eq("allowanceType.id", allowanceTypeIdRequest)
                }
                if (allowanceTypeIdReject) {
                    eq("allowanceType.id", allowanceTypeIdReject)
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
                if (effectiveDate) {
                    eq("effectiveDate", effectiveDate)
                }
                if (effectiveDateDate) {
                    eq("effectiveDate", effectiveDateDate)
                }
                if (effectiveDateReject) {
                    eq("effectiveDate", effectiveDateReject)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (employeeIdRequest) {
                    eq("employee.id", employeeIdRequest)
                }
                if (employeeIdReject) {
                    eq("employee.id", employeeIdReject)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
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
                if (requestStatusReject) {
                    eq("requestStatus", requestStatusReject)
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

                //effectiveDate
                if (fromEffectiveDate) {
                    ge("effectiveDate", fromEffectiveDate)
                }
                if (toEffectiveDate) {
                    le("effectiveDate", toEffectiveDate)
                }
                //effectiveDate
                if (fromEffectiveDateList) {
                    ge("effectiveDate", fromEffectiveDateList)
                }
                if (toEffectiveDateList) {
                    le("effectiveDate", toEffectiveDate)
                }
                //requestDate
                if (fromRequestDate) {
                    ge("requestDate", fromRequestDate)
                }
                if (toRequestDate) {
                    le("requestDate", toRequestDate)
                }
                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }

                if (toToDate) {
                    le("toDate", toToDate)
                }

                if (militaryRankId) {
                    currentEmployeeMilitaryRank {
                        militaryRank {
                            eq("id", militaryRankId)
                        }
                    }
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

                eq("firm.id", firmId)

                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                eq("firm.id", firmId)

                if(requestTypeList){
                    inList('requestType', requestTypeList)
                }
                if(threadId){
                    eq('threadId', threadId)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "allowanceType.descriptionInfo.localName":
                        allowanceType {
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
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList allowanceRequestList = search(params)
        return injectRemotingValues(allowanceRequestList)
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedList searchCanHaveOperation(GrailsParameterMap params) {

        String allowanceTypeId= params['allowanceType.id']

        StringBuilder sbWhereStatement= new StringBuilder()
        Map queryParams= [:]

        if(allowanceTypeId) {
            sbWhereStatement << "and r.allowanceType.id = :allowanceTypeId "
            queryParams['allowanceTypeId']=allowanceTypeId
        }

        params.sbWhereStatement= sbWhereStatement
        params.queryParams= queryParams

        // set request types
        params[EnumRequestCategory.ORIGINAL.name()]= EnumRequestType.ALLOWANCE_REQUEST.name()
        params[EnumRequestCategory.EDIT.name()]= EnumRequestType.ALLOWANCE_EDIT_REQUEST.name()
        params[EnumRequestCategory.EXTEND.name()]= EnumRequestType.ALLOWANCE_CONTINUE_REQUEST.name()
        params[EnumRequestCategory.STOP.name()]= EnumRequestType.ALLOWANCE_STOP_REQUEST.name()
        params[EnumRequestCategory.CANCEL.name()]= EnumRequestType.ALLOWANCE_CANCEL_REQUEST.name()

        params['domainName']= AllowanceRequest.class.name

        PagedList allowanceRequestList = requestService.searchCanHaveOperation(params)
        return injectRemotingValues(allowanceRequestList)
    }

    /**
     * inject remoting values into list
     * List without remoting values
     * @return PagedResultList including remoting values
     */
    def injectRemotingValues(def requestList) {
        SearchBean searchBean
        List<PersonDTO> personList
        List<GovernorateDTO> governorateDTOList
        List<PersonRelationShipsDTO> personRelationShipsDTOList
        List<AllowanceRequest> allowanceRequestList= (List<AllowanceRequest>)requestList?.resultList
        if (allowanceRequestList) {

            /**
             * get list of relatedPersonId from list of  relationShipType
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: allowanceRequestList?.personRelationShipsId))
            personRelationShipsDTOList = personRelationShipsService?.searchPersonRelationShips(searchBean)?.resultList

            /**
             * to get person DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: allowanceRequestList?.employee?.personId + personRelationShipsDTOList?.relatedPerson?.id))
            personList = personService?.searchPerson(searchBean)?.resultList

            /**
             * to get governorate DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: allowanceRequestList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(searchBean)?.resultList


            allowanceRequestList?.each { AllowanceRequest allowanceRequest ->

                /**
                 * assign personDTO to each employee
                 */
                allowanceRequest.employee.transientData.put("personDTO", personList?.find {
                    it?.id == allowanceRequest?.employee?.personId
                })

                /**
                 * assign governorateDTO to each employee
                 */
                allowanceRequest.employee.transientData.put("governorateDTO", governorateDTOList?.find {
                    it?.id == allowanceRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })

                /**
                 * assign personRelationShip name
                 */
                allowanceRequest.transientData.put("personRelationShipsName", personList?.find {
                    it?.id == personRelationShipsDTOList?.find {
                        it.id == allowanceRequest?.personRelationShipsId
                    }?.relatedPerson?.id
                }?.localFullName)
            }
        }
        return requestList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return AllowanceRequest.
     */
    AllowanceRequest save(GrailsParameterMap params) {
        AllowanceRequest allowanceRequestInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            allowanceRequestInstance = AllowanceRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (allowanceRequestInstance.version > version) {
                    allowanceRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('allowanceRequest.label', null, 'allowanceRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this allowanceRequest while you were editing")
                    return allowanceRequestInstance
                }
            }
            if (!allowanceRequestInstance) {
                allowanceRequestInstance = new AllowanceRequest()
                allowanceRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('allowanceRequest.label', null, 'allowanceRequest', LocaleContextHolder.getLocale())] as Object[], "This allowanceRequest with ${params.id} not found")
                return allowanceRequestInstance
            }
        } else {
            allowanceRequestInstance = new AllowanceRequest()
        }
        try {

            allowanceRequestInstance.properties = params
            final session = sessionFactory.currentSession
            Map sqlParamsMap

            if (allowanceRequestInstance?.allowanceType?.relationshipTypeId) {
                /**
                 * string sql query
                 * use 0003-03-03 03:03:03 to represent the null in the zone date time
                 */
                String query = "SELECT count(*) " +
                        "FROM  " +
                        "allowance_request, request " +
                        "where request.id=allowance_request.id " +
                        "and request.employee_id= :employeeId " +
                        "and allowance_request.allowance_type_id= :allowanceTypeId " +
                        "and allowance_request.person_relation_ships_id= :personRelationShipsId " +
                        "and ((CASE WHEN allowance_request.to_date_datetime = '0003-03-03 03:03:03' THEN current_date else allowance_request.to_date_datetime end),allowance_request.effective_date_datetime) overlaps ( :toDate , :effectiveDate)  " +
                        "and request.status ='${GeneralStatus.ACTIVE}'  " +
                        "and request.id != :requestId"

                /**
                 * fill map
                 */
                sqlParamsMap = [employeeId           : params["employee.id"],
                                allowanceTypeId      : params["allowanceType.id"],
                                personRelationShipsId: params.long("personRelationShipsId"),
                                toDate               : PCPUtils.convertZonedDateTimeToTimeStamp(allowanceRequestInstance?.toDate ?: PCPUtils.DEFAULT_ZONED_DATE_TIME),
                                effectiveDate        : PCPUtils.convertZonedDateTimeToTimeStamp(allowanceRequestInstance?.effectiveDate),
                                requestId            : allowanceRequestInstance?.id ?: "-1"
                ]

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

                /**
                 * in case: there is over lap with another request
                 */
                if (queryResults[0] > 0) {
                    allowanceRequestInstance.errors.reject("allowanceRequest.error.overlap.message")
                    return allowanceRequestInstance
                }
            }

            /**
             * validation relatedRelationship when allowance type has relationship
             */
            if (allowanceRequestInstance?.allowanceType?.relationshipTypeId && !allowanceRequestInstance?.personRelationShipsId) {
                allowanceRequestInstance.errors.reject("allowanceRequest.error.person.relation.ships.message")
                return allowanceRequestInstance
            }

            /**
             * set to date for old request = effective date for new request -1
             */
            if (params["oldRequestId"]) {
                AllowanceRequest allowanceRequest = AllowanceRequest.load(params["oldRequestId"])
                allowanceRequest?.toDate = allowanceRequestInstance?.effectiveDate?.minusDays(1)

                if (!allowanceRequest.save(failOnError: true)) {
                    new Exception("allowanceRequest.error.update.to.date.message")
                }
            }

            /**
             * assign employee for request
             */
            Employee employee = allowanceRequestInstance?.employee

            /**
             * assign currentEmploymentRecord of employee for request
             */
            if (employee?.currentEmploymentRecord) {
                allowanceRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            /**
             * assign currentEmployeeMilitaryRank of employee for request
             */
            if (employee?.currentEmployeeMilitaryRank) {
                allowanceRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }

            allowanceRequestInstance= requestService.saveManagerialOrderForRequest(params, allowanceRequestInstance)

            allowanceRequestInstance?.save(flush: true, failOnError:true);

            if(allowanceRequestInstance.requestStatus==EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        AllowanceRequest.getName(),
                        allowanceRequestInstance?.id + "",
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
            allowanceRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            allowanceRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            log.error("Failed to save allowance", ex)
            transactionStatus.setRollbackOnly()
            allowanceRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return allowanceRequestInstance
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return AllowanceRequest.
     */
    AllowanceRequest saveOperation(GrailsParameterMap params) {
        AllowanceRequest allowanceRequestInstance
        AllowanceRequest parentRequestInstance
        if (params.parentRequestId) {
            parentRequestInstance = AllowanceRequest.get(params["parentRequestId"])
            if (!parentRequestInstance) {
                parentRequestInstance = new AllowanceRequest()
                parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('allowanceRequest.label', null, 'allowanceRequest', LocaleContextHolder.getLocale())] as Object[], "This allowanceRequest with ${params.parentRequestId} not found")
                return parentRequestInstance
            }
        } else {
            parentRequestInstance = new AllowanceRequest()
            parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('allowanceRequest.label', null, 'allowanceRequest', LocaleContextHolder.getLocale())] as Object[], "This allowanceRequest with ${params.parentRequestId} not found")
            return parentRequestInstance
        }
        try {
            // create a clone from parent request
            allowanceRequestInstance= parentRequestInstance.clone()

            // update data from client
            allowanceRequestInstance.properties= params

            if(parentRequestInstance.requestType==EnumRequestType.ALLOWANCE_CANCEL_REQUEST ||
                    (allowanceRequestInstance.requestType== EnumRequestType.ALLOWANCE_CANCEL_REQUEST && !parentRequestInstance.canCancelRequest)  ||
                    (allowanceRequestInstance.requestType!= EnumRequestType.ALLOWANCE_CANCEL_REQUEST && !parentRequestInstance.canHaveOperation)){
                throw new Exception("Cannot make any operation on request " + parentRequestInstance.id)
            }

            /**
             * assign employee for request
             */
            Employee employee = parentRequestInstance?.employee

            if(allowanceRequestInstance.requestType in [EnumRequestType.ALLOWANCE_EDIT_REQUEST]){
                allowanceRequestInstance.extraInfo= new RequestExtraInfo(params.extraInfoData)
            } else if(allowanceRequestInstance.requestType == EnumRequestType.ALLOWANCE_CONTINUE_REQUEST){
                // extra info should be instance of extensionInfo
                allowanceRequestInstance.extraInfo= new RequestExtendExtraInfo(params.extraInfoData)
                ((RequestExtendExtraInfo)allowanceRequestInstance.extraInfo).fromDate= parentRequestInstance?.actualStartDate
                allowanceRequestInstance.effectiveDate= parentRequestInstance?.toDate
                if(allowanceRequestInstance?.toDate.isBefore(parentRequestInstance?.toDate)){
                    allowanceRequestInstance.errors.rejectValue('toDate', "allowanceRequest.toDate.less.than.effectiveDate.message")
                    throw new ValidationException("to date must be more than effectiveDate", allowanceRequestInstance.errors)
                }
            } else if(allowanceRequestInstance.requestType == EnumRequestType.ALLOWANCE_STOP_REQUEST){
                // extra info should be instance of stopInfo
                allowanceRequestInstance.extraInfo= new AllowanceStopExtraInfo(params.extraInfoData)
                if(allowanceRequestInstance?.toDate.isAfter(parentRequestInstance?.toDate)){
                    allowanceRequestInstance.errors.rejectValue('toDate', "allowanceRequest.toDate.more.than.effectiveDate.message")
                    throw new ValidationException("to date must be less than effectiveDate", allowanceRequestInstance.errors)
                }
            } else if(allowanceRequestInstance.requestType == EnumRequestType.ALLOWANCE_CANCEL_REQUEST){
                // extra info should be instance of cancelInfo
                allowanceRequestInstance.extraInfo= new RequestExtraInfo(params.extraInfoData)
            }

            allowanceRequestInstance?.extraInfo?.request= allowanceRequestInstance
            if(!allowanceRequestInstance.extraInfo.reason){
                allowanceRequestInstance.extraInfo.reason= allowanceRequestInstance?.requestReason
            }
            if(!allowanceRequestInstance?.extraInfo?.managerialOrderDate){
                allowanceRequestInstance?.extraInfo?.managerialOrderDate= PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            allowanceRequestInstance= requestService.saveManagerialOrderForRequest(params, allowanceRequestInstance)

            allowanceRequestInstance?.save(flush: true, failOnError:true);

            if(allowanceRequestInstance.requestStatus==EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        AllowanceRequest.getName(),
                        allowanceRequestInstance?.id + "",
                        !hasHRRole)

                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        } catch (ValidationException ve){
            log.error("request is not valid : " + ve.formatErrors(ve.errors))
            transactionStatus.setRollbackOnly()
            if(!allowanceRequestInstance.hasErrors())
                allowanceRequestInstance.errors.addAllErrors(ve.errors)
        } catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            allowanceRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            allowanceRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        } catch (Exception ex) {
            log.error("Failed to save request operation", ex)
            transactionStatus.setRollbackOnly()
            allowanceRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return allowanceRequestInstance
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

            AllowanceRequest instance = AllowanceRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
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
     * @return AllowanceRequest.
     */
    @Transactional(readOnly = true)
    AllowanceRequest getInstance(GrailsParameterMap params) {
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
    AllowanceRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
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
     * to auto complete model entry.
     * @param GrailsParameterMap params the search map.
     * @return JSON.
     */
    /*does not include in unit & integration testing because does not used until 09/08/2017 */

    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "employee.transientData.personDTO.localFullName"
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

        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }

        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    @Override
    void applyRequestChanges(Request allowanceRequest) {
        // no changes will be applied
    }

    @Override
    void revertRequestChanges(Request requestInstance) {
        // no changes will be reverted
    }

    @Transactional(readOnly = true)
    List<AllowanceRequest> getThreadWithRemotingValues(GrailsParameterMap params) {
        //if id is not null then return values from search method
        if (params.threadId) {
            // if there is any specific params, can be used here
            DetachedCriteria criteria= new DetachedCriteria(AllowanceRequest).build {

            }

            return injectRemotingValues(requestService.getThreadWithRemotingValues(criteria, params))
        }
        return null
    }
}