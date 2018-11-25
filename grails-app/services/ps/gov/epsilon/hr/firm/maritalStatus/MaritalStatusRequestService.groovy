package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.sitemesh.Grails5535Factory
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.personMaritalStatus.ManagePersonMaritalStatusService
import ps.gov.epsilon.core.personRelationShips.ManagePersonRelationShipsService
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.request.RequestExtraInfo
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.request.IRequestChangesReflect
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.enums.v1.GenderType
import ps.police.pcore.enums.v1.MaritalStatusEnum
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.commands.v1.MaritalStatusCommand
import ps.police.pcore.v2.entity.lookups.commands.v1.RelationshipTypeCommand
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.dtos.v1.MaritalStatusDTO
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonMaritalStatusDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO

import javax.servlet.http.HttpServletRequest
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * --this service manage employee request to update the marital status
 * <h1>Usage</h1>
 * --this service is used manage employee request to update the marital status
 * <h1>Restriction</h1>
 * -need employee to be created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class MaritalStatusRequestService implements IRequestChangesReflect {

    MessageSource messageSource
    def formatService
    PersonService personService
    MaritalStatusService maritalStatusService
    GovernorateService governorateService
    EmployeeService employeeService
    PersonMaritalStatusService personMaritalStatusService
    PersonRelationShipsService personRelationShipsService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.oldMaritalStatusName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.newMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canCancelRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEditRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "includedInList", type: "string", source: 'domain'],
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
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.oldMaritalStatusName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.newMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LITE_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "threadId", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.oldMaritalStatusName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.newMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
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
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String employeeId = params["employee.id"]
        Long newMaritalStatusId = params.long("newMaritalStatusId")
        Long oldMaritalStatusId = params.long("oldMaritalStatusId")
        String parentRequestId = params["parentRequestId"]
        Long personRelationShipId = params.long("personRelationShipId")
        Long relatedPersonId = params.long("relatedPersonId")
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String status = params["status"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        ZonedDateTime maritalStatusDate = PCPUtils.parseZonedDateTime(params['maritalStatusDate'])
        ZonedDateTime fromMaritalStatusDate = PCPUtils.parseZonedDateTime(params['maritalStatusDateFrom'])
        ZonedDateTime toMaritalStatusDate = PCPUtils.parseZonedDateTime(params['maritalStatusDateTo'])
        String militaryRankId = params["militaryRank.id"]
        Long firmId = params.long("firm.id")
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []
        String threadId = params["threadId"]
        List<EnumRequestType> requestTypeList = params.list("requestType[]")?.collect { EnumRequestType.valueOf(it) }
        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return MaritalStatusRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
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

                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }
                if (maritalStatusDate) {
                    eq("maritalStatusDate", maritalStatusDate)
                }
                if (fromMaritalStatusDate) {
                    ge("maritalStatusDate", fromMaritalStatusDate)
                }
                if (toMaritalStatusDate) {
                    le("maritalStatusDate", toMaritalStatusDate)
                }
                if (newMaritalStatusId) {
                    eq("newMaritalStatusId", newMaritalStatusId)
                }
                if (oldMaritalStatusId) {
                    eq("oldMaritalStatusId", oldMaritalStatusId)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (personRelationShipId) {
                    eq("personRelationShipId", personRelationShipId)
                }
                if (relatedPersonId) {
                    eq("relatedPersonId", relatedPersonId)
                }
                if (requestDate) {
                    eq("requestDate", requestDate)
                }
                if (fromRequestDate) {
                    ge("requestDate", fromRequestDate)
                }
                if (toRequestDate) {
                    le("requestDate", toRequestDate)
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
    PagedList searchCanHaveOperation(GrailsParameterMap params) {

        String maritalStatusTypeId = params['maritalStatusType.id']

        StringBuilder sbWhereStatement = new StringBuilder()
        Map queryParams = [:]

        if (maritalStatusTypeId) {
            sbWhereStatement << "and r.maritalStatusType.id = :maritalStatusTypeId "
            queryParams['maritalStatusTypeId'] = maritalStatusTypeId
        }

        params.sbWhereStatement = sbWhereStatement
        params.queryParams = queryParams

        // set request types
        params[EnumRequestCategory.ORIGINAL.name()] = EnumRequestType.MARITAL_STATUS_REQUEST.name()
        params[EnumRequestCategory.EDIT.name()] = EnumRequestType.MARITAL_STATUS_EDIT_REQUEST.name()
        params[EnumRequestCategory.CANCEL.name()] = EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST.name()

        params['domainName'] = MaritalStatusRequest.class.name

        PagedList maritalStatusRequestList = requestService.searchCanHaveOperation(params)
        return injectRemotingValues(maritalStatusRequestList)
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
        List<MaritalStatusDTO> maritalStatusDTOList
        List<MaritalStatusRequest> maritalStatusRequestList = (List<MaritalStatusRequest>) requestList?.resultList
        if (maritalStatusRequestList) {
            /**
             * to get person DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: maritalStatusRequestList?.employee?.personId + maritalStatusRequestList?.relatedPersonId))
            personList = personService?.searchPerson(searchBean)?.resultList

            /**
             * to get governorate DTO
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: maritalStatusRequestList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(searchBean)?.resultList


            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: maritalStatusRequestList?.oldMaritalStatusId + maritalStatusRequestList?.newMaritalStatusId))
            maritalStatusDTOList = maritalStatusService?.searchMaritalStatus(searchBean)?.resultList

            maritalStatusRequestList?.each { MaritalStatusRequest maritalStatusRequest ->
                maritalStatusRequest?.transientData?.relatedPersonDTO = personList.find {
                    it?.id == maritalStatusRequest?.relatedPersonId
                }
                maritalStatusRequest?.transientData?.oldMaritalStatusName = maritalStatusDTOList?.find {
                    it.id == maritalStatusRequest?.oldMaritalStatusId
                }?.descriptionInfo?.localName
                maritalStatusRequest?.transientData?.newMaritalStatusName = maritalStatusDTOList?.find {
                    it.id == maritalStatusRequest?.newMaritalStatusId
                }?.operationLocalName
                maritalStatusRequest.employee.transientData.put("personDTO", personList?.find {
                    it?.id == maritalStatusRequest?.employee?.personId
                })
                maritalStatusRequest.employee.transientData.put("governorateDTO", governorateDTOList?.find {
                    it?.id == maritalStatusRequest?.employee?.currentEmploymentRecord?.department?.governorateId
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
    PagedResultList searchWithRemotingValues1(GrailsParameterMap params) {

        //get the related person name
        PagedResultList pagedResultList = this.search(params)
        List relatedPersonIds = pagedResultList?.resultList?.relatedPersonId?.toList()
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: relatedPersonIds))
        List<PersonDTO> relatedPersons = personService?.searchPerson(searchBean)?.resultList

        //get employee remote details:
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)

        //get the MaritalStatus names (old and new marital status)
        List oldMaritalStatusIds = pagedResultList?.resultList?.oldMaritalStatusId?.toList()
        List newMaritalStatusIds = pagedResultList?.resultList?.newMaritalStatusId?.toList()
        oldMaritalStatusIds.addAll(newMaritalStatusIds)

        searchBean = new SearchBean()
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: oldMaritalStatusIds))
        List<MaritalStatusDTO> maritalStatuses = maritalStatusService?.searchMaritalStatus(searchBean)?.resultList

        //loop on paged result list, and map the remoting values:
        pagedResultList?.resultList?.each { MaritalStatusRequest maritalStatusRequest ->
            maritalStatusRequest?.transientData?.oldMaritalStatusName = maritalStatuses?.find {
                it.id == maritalStatusRequest?.oldMaritalStatusId
            }?.descriptionInfo?.localName
            maritalStatusRequest?.transientData?.newMaritalStatusName = maritalStatuses?.find {
                it.id == maritalStatusRequest?.newMaritalStatusId
            }?.operationLocalName
            maritalStatusRequest?.employee = employeeList?.find { it?.id == maritalStatusRequest?.employee?.id }
            maritalStatusRequest?.transientData?.relatedPersonDTO = relatedPersons?.find {
                it.id == maritalStatusRequest?.relatedPersonId
            }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return MaritalStatusRequest.
     */
    MaritalStatusRequest save(GrailsParameterMap params) {
        MaritalStatusRequest maritalStatusRequestInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            maritalStatusRequestInstance = MaritalStatusRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (maritalStatusRequestInstance.version > version) {
                    maritalStatusRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('maritalStatusRequest.label', null, 'maritalStatusRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this maritalStatusRequest while you were editing")
                    return maritalStatusRequestInstance
                }
            }
            if (!maritalStatusRequestInstance) {
                maritalStatusRequestInstance = new MaritalStatusRequest()
                maritalStatusRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('maritalStatusRequest.label', null, 'maritalStatusRequest', LocaleContextHolder.getLocale())] as Object[], "This maritalStatusRequest with ${params.id} not found")
                return maritalStatusRequestInstance
            }
        } else {
            maritalStatusRequestInstance = new MaritalStatusRequest()
            maritalStatusRequestInstance.requestDate = ZonedDateTime.now()
        }
        try {
            maritalStatusRequestInstance.properties = params;
            Employee employee = maritalStatusRequestInstance?.employee


            if (!params.id) {
                /* check the employee gender:
                 *  1. if employee is female, then check if the newMaritalStatus
                 *   a. if new status is married, and old status is (single, widowed, divorced) -> success
                 *   b. if new status id married, and old status is (married) -> fail
                 *  2. if employee is male, then check if the newMaritalStatus
                 *   a. if new status is married, and old status is (single, widowed, divorced) -> success
                 *   b. if new status id married, and old status is (married) -> check the number of wives <=3 success, else fail
                 */
                PersonDTO person = personService.getPerson(PCPUtils.convertParamsToSearchBean(new GrailsParameterMap([id: employee?.personId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())))

                //get employee current marital status
                GrailsParameterMap filterParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                filterParams["person.id"] = employee?.personId
                filterParams["isCurrent"] = true
                PersonMaritalStatusDTO personMaritalStatusDTO = personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(filterParams))

                //if the employee is female, check what is the current and new status of her?
                if (person?.genderType?.id == ps.police.pcore.enums.v1.GenderType.FEMALE.value() && personMaritalStatusDTO?.maritalStatus?.id == MaritalStatusEnum.MARRIED.value()) {
                    if (maritalStatusRequestInstance?.newMaritalStatusId == MaritalStatusEnum.MARRIED.value()) {
                        maritalStatusRequestInstance?.errors?.reject('maritalStatusRequest.addHusband.error')
                        return maritalStatusRequestInstance
                    }
                }

                //if the employee is male, check if his current wives >= 4
                if (person?.genderType?.id == ps.police.pcore.enums.v1.GenderType.MALE.value() && personMaritalStatusDTO?.maritalStatus?.id == MaritalStatusEnum.MARRIED.value()) {
                    if (maritalStatusRequestInstance.newMaritalStatusId == MaritalStatusEnum.MARRIED.value()) {
                        //get the list of current wives to be check if they exceeded 4
                        filterParams["relationshipType.id"] = [RelationshipTypeEnum.WIFE.value()]
                        PagedList personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))

                        if (personRelationShipsDTOPagedList.resultList.size() >= 4) {
                            maritalStatusRequestInstance.errors.reject('maritalStatusRequest.addWives.error')
                            return maritalStatusRequestInstance
                        }
                    }
                }
            }

            //save the employee current records
            maritalStatusRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord;
            maritalStatusRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank

            maritalStatusRequestInstance = requestService.saveManagerialOrderForRequest(params, maritalStatusRequestInstance)
            //save the request instance
            maritalStatusRequestInstance?.save(flush: true, failOnError: true);

            if (maritalStatusRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                /**
                 * get  the workflow data
                 */
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        maritalStatusRequestInstance?.employee?.id + "",
                        maritalStatusRequestInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                        maritalStatusRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        maritalStatusRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        MaritalStatusRequest.getName(),
                        maritalStatusRequestInstance?.id + "",
                        !hasHRRole)

                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }

        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            maritalStatusRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            maritalStatusRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            maritalStatusRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return maritalStatusRequestInstance
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return MaritalStatusRequest.
     */
    MaritalStatusRequest saveOperation(GrailsParameterMap params) {
        MaritalStatusRequest maritalStatusRequestInstance
        MaritalStatusRequest parentRequestInstance
        if (params.parentRequestId) {
            parentRequestInstance = MaritalStatusRequest.get(params["parentRequestId"])
            if (!parentRequestInstance) {
                parentRequestInstance = new MaritalStatusRequest()
                parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('maritalStatusRequest.label', null, 'maritalStatusRequest', LocaleContextHolder.getLocale())] as Object[], "This maritalStatusRequest with ${params.parentRequestId} not found")
                return parentRequestInstance
            }
        } else {
            parentRequestInstance = new MaritalStatusRequest()
            parentRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('maritalStatusRequest.label', null, 'maritalStatusRequest', LocaleContextHolder.getLocale())] as Object[], "This maritalStatusRequest with ${params.parentRequestId} not found")
            return parentRequestInstance
        }
        try {
            // create a clone from parent request
            maritalStatusRequestInstance = parentRequestInstance.clone()

            // update data from client
            maritalStatusRequestInstance.properties = params

            if (parentRequestInstance.requestType == EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST ||
                    (maritalStatusRequestInstance.requestType == EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST && !parentRequestInstance.canCancelRequest) ||
                    (maritalStatusRequestInstance.requestType != EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST && !parentRequestInstance.canHaveOperation)) {
                throw new Exception("Cannot make any operation on request " + parentRequestInstance.id)
            }

            /**
             * assign employee for request
             */
            Employee employee = parentRequestInstance?.employee
            params["employee.id"] = employee?.id

            if (maritalStatusRequestInstance.requestType in [EnumRequestType.MARITAL_STATUS_EDIT_REQUEST]) {
                params["doSave"] = false
                maritalStatusRequestInstance = save(params)
                maritalStatusRequestInstance.employee = parentRequestInstance?.employee
                maritalStatusRequestInstance.currentEmploymentRecord = parentRequestInstance?.employee?.currentEmploymentRecord
                maritalStatusRequestInstance.currentEmployeeMilitaryRank = parentRequestInstance?.employee?.currentEmployeeMilitaryRank
                maritalStatusRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            } else if (maritalStatusRequestInstance.requestType == EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST) {
                // extra info should be instance of cancelInfo
                maritalStatusRequestInstance.extraInfo = new RequestExtraInfo(params.extraInfoData)
            }

            maritalStatusRequestInstance?.extraInfo?.request = maritalStatusRequestInstance
            if (!maritalStatusRequestInstance.extraInfo.reason) {
                maritalStatusRequestInstance.extraInfo.reason = maritalStatusRequestInstance?.requestReason
            }
            if (!maritalStatusRequestInstance?.extraInfo?.managerialOrderDate) {
                maritalStatusRequestInstance?.extraInfo?.managerialOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            }

            maritalStatusRequestInstance = requestService.saveManagerialOrderForRequest(params, maritalStatusRequestInstance)

            maritalStatusRequestInstance?.save(flush: true, failOnError: true);

            if (maritalStatusRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        MaritalStatusRequest.getName(),
                        maritalStatusRequestInstance?.id + "",
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
        return maritalStatusRequestInstance
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
            MaritalStatusRequest instance = MaritalStatusRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && (instance.requestStatus == EnumRequestStatus.CREATED) && (instance?.trackingInfo?.status != GeneralStatus.DELETED)) {
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
     * @return MaritalStatusRequest.
     */
    @Transactional(readOnly = true)
    MaritalStatusRequest getInstance(GrailsParameterMap params) {
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
    MaritalStatusRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
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
    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "descriptionInfo.localName"
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
     * return the list of marital status for each case
     * @param params
     * @return
     */
    List<ps.police.pcore.enums.v1.MaritalStatusEnum> maritalStatusAutocomplete(GrailsParameterMap params) {
        List<ps.police.pcore.enums.v1.MaritalStatusEnum> maritalStatusList = []
        switch (params["oldMaritalStatusId"].toString()) {
            case ps.police.pcore.enums.v1.MaritalStatusEnum.SINGLE.value().toString():
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value())
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.DIVORCED.value())
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.WIDOWED.value())
                break
            case ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value().toString():
                //if the candidate is male, add married option
                if (params["employeeGenderType"] == ps.police.pcore.enums.v1.GenderType.FEMALE.value().toString()) {
                    maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.DIVORCED.value())
                    maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.WIDOWED.value())
                } else {
                    //in case male or gender is returned from core as null
                    maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.DIVORCED.value())
                    maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.WIDOWED.value())
                    maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value())
                }
                break
            case ps.police.pcore.enums.v1.MaritalStatusEnum.DIVORCED.value().toString():
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value())
                break
            case ps.police.pcore.enums.v1.MaritalStatusEnum.WIDOWED.value().toString():
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value())
                break
            default:
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.SINGLE.value())
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value())
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.DIVORCED.value())
                maritalStatusList.push(ps.police.pcore.enums.v1.MaritalStatusEnum.WIDOWED.value())
                break
        }
        return maritalStatusList
    }

    /**
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return maritalStatusRequest.
     */
    @Transactional(readOnly = true)
    MaritalStatusRequest getPreCreateInstance(GrailsParameterMap params) {
        MaritalStatusRequest maritalStatusRequest = new MaritalStatusRequest(params)
        //CHECK if employee has request in [progress or approved] requests
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED, EnumRequestStatus.APPROVED, EnumRequestStatus.CANCELED, EnumRequestStatus.OVERRIDEN]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            maritalStatusRequest.errors.reject('request.employeeHasRequest.error.label')
        } else {
            GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id': params['firmId'], id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

            //check if the employee current status category is COMMITTED or not
            if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
                maritalStatusRequest.errors.reject('request.employeeUncommitted.error.label')
            } else {
                maritalStatusRequest?.employee = employee
                maritalStatusRequest?.requestDate = ZonedDateTime.now()
                maritalStatusRequest?.currentEmploymentRecord = employee?.currentEmploymentRecord
                //get employee current marital status
                GrailsParameterMap filterParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                filterParams["person.id"] = employee?.personId
                filterParams["isCurrent"] = true
                filterParams["nullToDate"] = true
                PersonMaritalStatusDTO personMaritalStatusDTO = personMaritalStatusService.getPersonMaritalStatus(PCPUtils.convertParamsToSearchBean(filterParams))
                maritalStatusRequest?.oldMaritalStatusId = personMaritalStatusDTO?.maritalStatus?.id
                maritalStatusRequest?.transientData?.oldMaritalStatusName = personMaritalStatusDTO?.maritalStatus?.descriptionInfo?.localName

                //get the list of current wife/husband to be shown in the table:
                //show current wife/husband only
                filterParams["relationshipType.id"] = [RelationshipTypeEnum.HUSBAND.value(), RelationshipTypeEnum.WIFE.value()]
                PagedList personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))
                filterParams["ids[]"] = personRelationShipsDTOPagedList?.resultList?.relatedPerson?.id.toList()

                PagedList relatedPersonList = personService.searchPerson(PCPUtils.convertParamsToSearchBean(filterParams))
                maritalStatusRequest?.transientData.put("relatedPersonList", relatedPersonList?.resultList)
            }
        }
        return maritalStatusRequest
    }

    /**
     * to auto complete person entry with custom params depends on marital status.
     * @param GrailsParameterMap params the search map.
     * get the person autocomplete list depends on
     *  1. new marital status selection.
     *  2. gender type (show the opposite gender type).
     * @return String.
     */
    @Transactional(readOnly = true)
    String autoCompletePerson(GrailsParameterMap params) {
        String data = ""
        if (params.employeeId) {
            Employee employee = employeeService.getInstanceWithRemotingValues(new GrailsParameterMap(['firm.id': params['firmId'], id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest()))
            GrailsParameterMap filterParams = new GrailsParameterMap([:], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            filterParams["person.id"] = employee?.personId
            PagedList personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))

            //show the opposite gender type
            List<Long> genderTypeList = ps.police.pcore.enums.v1.GenderType.values()?.findAll() {
                it.value != employee?.transientData?.personDTO?.genderType?.id
            }
            filterParams["genderType.id"] = genderTypeList.collect { it?.value }


            switch (params["newMaritalStatusId"].toString()) {
                case ps.police.pcore.enums.v1.MaritalStatusEnum.MARRIED.value().toString():
                    //2. Hide the current wife
                    if (personRelationShipsDTOPagedList?.resultList?.relatedPerson?.id.toList().size() > 0) {
                        filterParams["notIncludedIds"] = personRelationShipsDTOPagedList?.resultList?.relatedPerson?.id.toList()
                    }
                    break
                case ps.police.pcore.enums.v1.MaritalStatusEnum.DIVORCED.value().toString():
                    //show current wife/husband only
                    filterParams["relationshipType.id"] = [RelationshipTypeEnum.HUSBAND.value(), RelationshipTypeEnum.WIFE.value()]
                    filterParams["nullToDate"] = true
                    personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))
                    filterParams["ids[]"] = personRelationShipsDTOPagedList?.resultList?.relatedPerson?.id.toList()
                    break
                case ps.police.pcore.enums.v1.MaritalStatusEnum.WIDOWED.value().toString():
                    //show current wife/husband only
                    filterParams["relationshipType.id"] = [RelationshipTypeEnum.HUSBAND.value(), RelationshipTypeEnum.WIFE.value()]
                    filterParams["nullToDate"] = true
                    personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))
                    filterParams["ids[]"] = personRelationShipsDTOPagedList?.resultList?.relatedPerson?.id.toList()
                    break
                default:
                    break
            }
            filterParams["sSearch"] = params["sSearch"]
            data = personService.autoCompletePerson(PCPUtils.convertParamsToSearchBean(filterParams))
        }

        return data
    }

    @Override
    void applyRequestChanges(Request request) {
        MaritalStatusRequest maritalStatusRequest = (MaritalStatusRequest) request
        GrailsParameterMap relationShipParams
        PersonRelationShipsCommand personRelationShipsCommand
        PersonRelationShipsDTO personRelationShipsDTO
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: maritalStatusRequest?.relatedPersonId))
        PersonDTO relatedPersonDTO = personService?.getPerson(searchBean)

        //reflect the changes of relation to core
        //retrieve new person relationShips status in pcore, if not exist, save new relation and set the personRelationShipId in request
        //retrieve the personRelationShipId from pcore and set the "toDate" value -and save the relationship id into request.
        relationShipParams = new GrailsParameterMap([:], null)
        relationShipParams["person.id"] = maritalStatusRequest?.employee?.personId
        relationShipParams["relatedPerson.id"] = maritalStatusRequest?.relatedPersonId
        personRelationShipsDTO = personRelationShipsService.getPersonRelationShips(PCPUtils.convertParamsToSearchBean(relationShipParams));

        /**
         * check if the request type is MARITAL_STATUS_CANCEL_REQUEST, then the marital status must be removed from CORE and HR
         * otherwise, the relationship will be added to CORE and refrenced in HR.
         */
        if (maritalStatusRequest.requestType == EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST) {
            GrailsParameterMap parameterMap = new GrailsParameterMap([id: personRelationShipsDTO?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            try {
                DeleteBean deleteBean = personRelationShipsService.deletePersonRelationShips(PCPUtils.convertParamsToDeleteBean(parameterMap))
                if (!deleteBean.status) {
                    throw Exception("can not delete person relationShip for employee ${relatedPersonDTO?.localFullName} with employee id: ${maritalStatusRequest?.employee?.id}")
                }
            } catch (Exception ex) {
                throw Exception("can not delete person relationShip for employee +${ex}")
            }

        } else {
            if (!personRelationShipsDTO?.id) {
                try {
                    personRelationShipsCommand = new PersonRelationShipsCommand()
                    personRelationShipsCommand.fromDate = maritalStatusRequest?.maritalStatusDate
                    personRelationShipsCommand.person = new PersonCommand(id: maritalStatusRequest?.employee?.personId)
                    personRelationShipsCommand.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    personRelationShipsCommand.relatedPerson = new PersonCommand(id: maritalStatusRequest?.relatedPersonId);
                    personRelationShipsCommand.isDependent = maritalStatusRequest?.isDependent

                    if (relatedPersonDTO?.genderType?.id == GenderType.FEMALE.value()) {
                        personRelationShipsCommand.relationshipType = new RelationshipTypeCommand(id: RelationshipTypeEnum.WIFE.value())
                    } else {
                        personRelationShipsCommand.relationshipType = new RelationshipTypeCommand(id: RelationshipTypeEnum.HUSBAND.value())
                    }

                    //if command.validate
                    if (personRelationShipsCommand.validate()) {
                        personRelationShipsCommand = personRelationShipsService.savePersonRelationShips(personRelationShipsCommand)
                        maritalStatusRequest?.personRelationShipId = personRelationShipsCommand?.id
                    } else {
                        throw new Exception("error occurred while updating person RelationShips in core:" + personRelationShipsCommand.errors);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace()
                }
            } else {
                try {
                    //create new command and set all data to save the "toDate" value of old relation ship record
                    personRelationShipsCommand = new PersonRelationShipsCommand(id: personRelationShipsDTO?.id)
                    personRelationShipsCommand.person = new PersonCommand(id: personRelationShipsDTO?.person?.id)
                    personRelationShipsCommand.relatedPerson = new PersonCommand(id: personRelationShipsDTO?.relatedPerson?.id)
                    personRelationShipsCommand.relationshipType = new RelationshipTypeCommand(id: personRelationShipsDTO?.relationshipType?.id)
                    personRelationShipsCommand.fromDate = personRelationShipsDTO?.fromDate
                    personRelationShipsCommand.toDate = maritalStatusRequest?.maritalStatusDate
                    personRelationShipsCommand.isDependent = maritalStatusRequest?.isDependent
                    personRelationShipsCommand = personRelationShipsService.savePersonRelationShips(personRelationShipsCommand)
                    maritalStatusRequest?.personRelationShipId = personRelationShipsCommand?.id
                } catch (Exception ex) {
                    ex.printStackTrace()
                }
            }

            //--------------------------------------------------------------------------------------------------------------------------

            if (maritalStatusRequest?.oldMaritalStatusId != maritalStatusRequest?.newMaritalStatusId) {
                // save the new marital status pf the employee in PersonMaritalStatus at core
                PersonMaritalStatusCommand personMaritalStatusCommand = new PersonMaritalStatusCommand()
                try {
                    //PCPUtils.bindZonedDateTimeFields(personMaritalStatusCommand,request?.properties)
                    personMaritalStatusCommand?.fromDate = maritalStatusRequest?.maritalStatusDate
                    personMaritalStatusCommand.maritalStatus = new MaritalStatusCommand(id: maritalStatusRequest?.newMaritalStatusId);
                    //need to check in case of male, and has current wives:
                    PersonDTO personDTO = personService.getPerson(PCPUtils.convertParamsToSearchBean(new GrailsParameterMap([id: maritalStatusRequest?.employee?.personId], null)))
                    personMaritalStatusCommand?.isCurrent = true
                    //if the employee is male, check the number of his current wives
                    if (personDTO?.genderType?.id == ps.police.pcore.enums.v1.GenderType.MALE.value() && maritalStatusRequest?.newMaritalStatusId in [MaritalStatusEnum.WIDOWED.value(), MaritalStatusEnum.DIVORCED.value()]) {
                        GrailsParameterMap filterParams = new GrailsParameterMap([:], null)
                        filterParams["person.id"] = maritalStatusRequest?.employee?.personId
                        filterParams["isCurrent"] = true
                        filterParams["relationshipType.id"] = [RelationshipTypeEnum.WIFE.value()]
                        PagedList personRelationShipsDTOPagedList = personRelationShipsService.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(filterParams))
                        if (personRelationShipsDTOPagedList?.resultList?.size() > 1) {
                            personMaritalStatusCommand?.isCurrent = false
                        }
                    }
                    personMaritalStatusCommand.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    personMaritalStatusCommand.person = new PersonCommand(id: maritalStatusRequest?.employee?.personId)
                    if (personMaritalStatusCommand.validate()) {
                        personMaritalStatusCommand = personMaritalStatusService.savePersonMaritalStatus(personMaritalStatusCommand)
                        maritalStatusRequest?.personMaritalStatusId = personMaritalStatusCommand?.id
                    } else {
                        throw new Exception(personMaritalStatusCommand.errors)
                    }
                } catch (Exception ex) {
                    ex.printStackTrace()
                }
            }
        }


        maritalStatusRequest?.save(flush: true, failOnError: true)
    }

    @Override
    void revertRequestChanges(Request request) {
        MaritalStatusRequest maritalStatusRequest = (MaritalStatusRequest) request
        GrailsParameterMap relationShipParams
        GrailsParameterMap personMaritalStatusParams
        DeleteBean deleteBean

        if (maritalStatusRequest?.personRelationShipId) {
            //check if they new marital status = marriage
            if (maritalStatusRequest?.newMaritalStatusId == MaritalStatusEnum?.MARRIED) {
                relationShipParams = new GrailsParameterMap([:], null)
                relationShipParams["id"] = maritalStatusRequest?.personRelationShipId
                //delete the relation ship:
                deleteBean = personRelationShipsService.deletePersonRelationShips(PCPUtils.convertParamsToDeleteBean(relationShipParams))
            } else {
                //reflect the changes of relation to core
                //retrieve new person relationShips status in pcore, if not exist, save new relation and set the personRelationShipId in request
                //retrieve the personRelationShipId from pcore and set the "toDate" value -and save the relationship id into request.
                relationShipParams = new GrailsParameterMap([:], null)
                relationShipParams["person.id"] = maritalStatusRequest?.employee?.personId
                relationShipParams["relatedPerson.id"] = maritalStatusRequest?.relatedPersonId
                PersonRelationShipsDTO personRelationShipsDTO = personRelationShipsService.getPersonRelationShips(PCPUtils.convertParamsToSearchBean(relationShipParams));
                if (personRelationShipsDTO?.id) {
                    //create new command and set all data to save the "toDate" value of old relation ship record
                    PersonRelationShipsCommand personRelationShipsCommand = new PersonRelationShipsCommand(id: personRelationShipsDTO?.id)
                    personRelationShipsCommand.person = new PersonCommand(id: personRelationShipsDTO?.person?.id)
                    personRelationShipsCommand.relatedPerson = new PersonCommand(id: personRelationShipsDTO?.relatedPerson?.id)
                    personRelationShipsCommand.relationshipType = new RelationshipTypeCommand(id: personRelationShipsDTO?.relationshipType?.id)
                    personRelationShipsCommand.fromDate = personRelationShipsDTO?.fromDate
                    personRelationShipsCommand.toDate = PCPUtils?.DEFAULT_ZONED_DATE_TIME
                    personRelationShipsCommand.isDependent = maritalStatusRequest?.isDependent
                    personRelationShipsCommand = personRelationShipsService.savePersonRelationShips(personRelationShipsCommand)
                }
            }

        }

        if (maritalStatusRequest?.personMaritalStatusId) {
            personMaritalStatusParams = new GrailsParameterMap([:], null)
            personMaritalStatusParams["id"] = maritalStatusRequest?.personMaritalStatusId
            //delete the person Marital Status
            deleteBean = personMaritalStatusService.deletePersonMaritalStatus(PCPUtils.convertParamsToDeleteBean(personMaritalStatusParams))
        }
    }

    @Transactional(readOnly = true)
    List<MaritalStatusRequest> getThreadWithRemotingValues(GrailsParameterMap params) {

        //if id is not null then return values from search method
        if (params.threadId) {
            // if there is any specific params, can be used here
            DetachedCriteria criteria = new DetachedCriteria(MaritalStatusRequest).build {

            }
            def requestList = requestService.getThreadWithRemotingValues(criteria, params)
            println "${requestList}"
            return injectRemotingValues(requestList)
        }
        return null
    }
}