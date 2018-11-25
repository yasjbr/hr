package ps.gov.epsilon.hr.firm.employmentService

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceActionReason
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.enums.workflow.v1.EnumWorkFlowOperation
import ps.gov.epsilon.hr.firm.employmentService.lookups.ServiceActionReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.firm.suspension.SuspensionRequest
import ps.gov.epsilon.workflow.OperationWorkflowSetting
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.PersonService

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to create a request for stop or return employee from/to work-
 * <h1>Usage</h1>
 * -create a request for stop or return employee from/to work-
 * <h1>Restriction</h1>
 * --
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class EmploymentServiceRequestService {

    MessageSource messageSource
    def formatService
    PersonService personService
    ServiceListService serviceListService
    EmployeeService employeeService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "expectedDateEffective", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "externalOrderDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "externalOrderNumber", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "serviceActionReason", type: "ServiceActionReason", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canCancelRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEditRequest", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "includedInList", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "expectedDateEffective", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "externalOrderDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "externalOrderNumber", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "serviceActionReason", type: "ServiceActionReason", source: 'domain'],
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
        String parentRequestId = params["parentRequestId"]
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"].toString()) : null
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus filter_requestStatus = params["filter_requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["filter_requestStatus"].toString()) : null

        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"].toString()) : null

        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String serviceActionReasonId = params["serviceActionReason.id"]
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        ZonedDateTime expectedDateEffective = PCPUtils.parseZonedDateTime(params['expectedDateEffective'])
        ZonedDateTime fromExpectedDateEffective = PCPUtils.parseZonedDateTime(params['expectedDateEffectiveFrom'])
        ZonedDateTime toExpectedDateEffective = PCPUtils.parseZonedDateTime(params['expectedDateEffectiveTo'])
        Boolean isListModal = params.boolean("isListModal")
        Long firmId = params.long("firm.id")
        String militaryRankId = params["militaryRank.id"]

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return EmploymentServiceRequest.createCriteria().list(max: max, offset: offset) {
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
                if (expectedDateEffective) {
                    eq("expectedDateEffective", expectedDateEffective)
                }
                //from/to :expectedDateEffective
                if (fromExpectedDateEffective) {
                    ge("expectedDateEffective", fromExpectedDateEffective)
                }
                if (toExpectedDateEffective) {
                    le("expectedDateEffective", toExpectedDateEffective)
                }
                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (requestDate) {
                    eq("requestDate", requestDate)
                }
                //from/to :requestDate
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
                if (filter_requestStatus) {
                    eq("requestStatus", filter_requestStatus)
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
                if (serviceActionReasonId) {
                    eq("serviceActionReason.id", serviceActionReasonId)
                }
                if (excludeRequestStatusList) {
                    not {
                        inList("requestStatus", excludeRequestStatusList)
                    }
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (isListModal) {
                    isNull("externalOrderNumber")
                    eq("externalOrderDate", PCPUtils.DEFAULT_ZONED_DATE_TIME)
                    eq("expectedDateEffective", PCPUtils.DEFAULT_ZONED_DATE_TIME)
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
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)
        if (pagedResultList) {
            List<String> employeeIds = pagedResultList?.resultList?.employee?.id?.toList()
            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            //get employee info
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)
            //fill all remoting information
            pagedResultList.each { EmploymentServiceRequest employmentServiceRequest ->
                //set employee info
                employmentServiceRequest.employee = employees.find { it.id == employmentServiceRequest?.employee?.id }
            }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmploymentServiceRequest.
     */
    EmploymentServiceRequest save(GrailsParameterMap params) {
        EmploymentServiceRequest employmentServiceRequestInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            employmentServiceRequestInstance = EmploymentServiceRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employmentServiceRequestInstance.version > version) {
                    employmentServiceRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employmentServiceRequest.label', null, 'employmentServiceRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employmentServiceRequest while you were editing")
                    return employmentServiceRequestInstance
                }
            }
            if (!employmentServiceRequestInstance) {
                employmentServiceRequestInstance = new EmploymentServiceRequest()
                employmentServiceRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employmentServiceRequest.label', null, 'employmentServiceRequest', LocaleContextHolder.getLocale())] as Object[], "This employmentServiceRequest with ${params.id} not found")
                return employmentServiceRequestInstance
            }
        } else {
            employmentServiceRequestInstance = new EmploymentServiceRequest()
        }
        try {
            employmentServiceRequestInstance.properties = params;

            //save the employee instance and current employment record and military rank in the request
            Employee employee = employmentServiceRequestInstance?.employee
            if (employee?.currentEmploymentRecord) {
                employmentServiceRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            if (employee?.currentEmployeeMilitaryRank) {
                employmentServiceRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }

            employmentServiceRequestInstance = requestService.saveManagerialOrderForRequest(params, employmentServiceRequestInstance)

            if (!employmentServiceRequestInstance?.requestStatus) {
                employmentServiceRequestInstance.requestStatus = EnumRequestStatus.APPROVED
            }
            /**
             * save employmentService request
             */
            employmentServiceRequestInstance.save(failOnError: true);

            if (employmentServiceRequestInstance?.externalOrderNumber && (employmentServiceRequestInstance?.externalOrderDate != PCPUtils.DEFAULT_ZONED_DATE_TIME) && (employmentServiceRequestInstance?.expectedDateEffective != PCPUtils.DEFAULT_ZONED_DATE_TIME)) {
                //when create new request, after status is APPROVED_BY_WORKFLOW : it should be added to list automatically
                println "The request is approved and added to list automatically."
                ServiceList serviceList = serviceListService?.addEmploymentServiceRequestToList(employmentServiceRequestInstance);
                if (serviceList?.hasErrors()) {
                    //return error, if the added to list has issue
                    employmentServiceRequestInstance.errors.reject("addRequestToList.error.label")
                    throw new Exception("error occurred while adding the request to list.")
                }
            }

            if (employmentServiceRequestInstance?.requestStatus == EnumRequestStatus.CREATED) {
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)
                OperationWorkflowSetting runtimeOperationWorkflowSetting = null
                // settings params should be changed
                runtimeOperationWorkflowSetting = OperationWorkflowSetting.findByDomain(EnumWorkFlowOperation.DEFAULT_DOES_NOT_NEED_AOC_APPROVAL.getValue())
                /**
                 * get  the workflow data
                 */
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employmentServiceRequestInstance?.employee?.id + "",
                        employmentServiceRequestInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                        employmentServiceRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employmentServiceRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        EmploymentServiceRequest?.getName(),
                        employmentServiceRequestInstance?.id + "",
                        !hasHRRole,
                        "${employmentServiceRequestInstance.requestType}", runtimeOperationWorkflowSetting)

                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            employmentServiceRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            employmentServiceRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            employmentServiceRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employmentServiceRequestInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<EmploymentServiceRequest> employmentServiceRequests = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }
            employmentServiceRequests = EmploymentServiceRequest.findAllByIdInList(ids)
            employmentServiceRequests.each { EmploymentServiceRequest employmentServiceRequest ->
                if (employmentServiceRequest?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete employmentServiceRequest
                    employmentServiceRequest.trackingInfo.status = GeneralStatus.DELETED
                    employmentServiceRequest.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (employmentServiceRequests) {
                deleteBean.status = true
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
     * @return EmploymentServiceRequest.
     */
    @Transactional(readOnly = true)
    EmploymentServiceRequest getInstance(GrailsParameterMap params) {
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
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmploymentServiceRequest.
     */
    @Transactional(readOnly = true)
    EmploymentServiceRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                EmploymentServiceRequest employmentServiceRequest = results[0]
                return employmentServiceRequest
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

    /**
     * to get instance with validation before create.
     * the employee should be committed in his firm and he does not have any previous active employment service request
     * @param GrailsParameterMap params the search map.
     * @return EmploymentServiceRequest.
     */
    @Transactional(readOnly = true)
    EmploymentServiceRequest getPreCreateInstance(GrailsParameterMap params) {
        EmploymentServiceRequest employmentServiceRequest = new EmploymentServiceRequest(params)
        //CHECK if employee has requests in [progress or approved] requests
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], "requestType": params["requestType"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED, ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())

        PagedResultList result = this.search(searchParams)

        if (result?.resultList?.size() > 0) {
            employmentServiceRequest.errors.reject('request.employeeHasRequest.error.label')
        } else {

            GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)
            ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"].toString()) : null

            if (requestType == EnumRequestType.END_OF_SERVICE) {

                //check if the employee current status category is COMMITTED
                if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
                    employmentServiceRequest.errors.reject('request.employeeUncommitted.error.label')
                } else {
                    employmentServiceRequest.employee = employee
                    employmentServiceRequest?.requestDate = ZonedDateTime.now()
                    employmentServiceRequest.currentEmploymentRecord = employee?.currentEmploymentRecord
                }
            } else {

                println "--------------------------------------------------------"
                //case of return to service
                if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.COMMITTED.value) {
                    employmentServiceRequest.errors.reject('request.employeeCommitted.error.label')
                } else {

                    employmentServiceRequest?.serviceActionReason = ServiceActionReason.load(params["serviceActionReasonId"])
                    employmentServiceRequest.employee = employee
                    employmentServiceRequest?.requestDate = ZonedDateTime.now()
                    employmentServiceRequest.currentEmploymentRecord = employee?.currentEmploymentRecord
                }
            }
        }
        return employmentServiceRequest
    }

    /**
     * generate the code for list.
     * @param String encodedId
     * @param String entityName
     * @param Object domainClass
     * @param Boolean isAttributeInRequest
     * @param String attributeName
     * @return Map
     */
    Map goToList(String encodedId, String manageListName) {
        try {
            String id = HashHelper.decode(encodedId)

            ServiceListEmployee serviceListEmployee = ServiceListEmployee.createCriteria().get {
                eq("employmentServiceRequest.id", id)
            }
            ServiceList serviceList = serviceListEmployee?.serviceList
            if (serviceList) {
                def link = [controller: "serviceList", action: "${manageListName}", params: [encodedId: "${serviceList?.encodedId}"]]
                return link
            }
        } catch (Exception ex) {
            ex.printStackTrace()
        }

        return [:]
    }

    /**
     * this service is used to filter the absences which may be added to list
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
//    @Transactional(readOnly = true)
//    PagedList customListSearch(GrailsParameterMap params) {
//        final session = sessionFactory.currentSession
//
//        String orderByQuery = ""
//        // global setting.
//        Integer max = params.int('max') ?: 10
//        Integer offset = params.int('offset') ?: 0
//        Integer totalCount = 0
//        Integer column = params.int("orderColumn")
//        String dir = params["orderDirection"]
//        String columnName
//        if (column) {
//            columnName = DOMAIN_COLUMNS[column]?.name
//        }
//        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
//        Map sqlParamsMap = [:]
//
//        String id = params["id"]
//
//        String employeeId = params["employee.id"]
//        String militaryRankId = params["militaryRank.id"]
//        ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason absenceReason = params["absenceReason"] ? ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.valueOf(params["absenceReason"]) : null
//        Long numOfDays = params.long("numOfDays")
//
//        String fromDate = params['fromDate']
//        Timestamp fromFromDate = PCPUtils.parseTimestamp(params['fromDateFrom'])
//        Timestamp toFromDate = PCPUtils.parseTimestamp(params['fromDateTo'])
//
//        String toDate = params['toDate']
//        Timestamp fromToDate = PCPUtils.parseTimestamp(params['toDateFrom'])
//        Timestamp toToDate = PCPUtils.parseTimestamp(params['toDateTo'])
//
//        String noticeDate = params['noticeDate']
//        Timestamp fromNoticeDate = PCPUtils.parseTimestamp(params['noticeDateFrom'])
//        Timestamp toNoticeDate = PCPUtils.parseTimestamp(params['noticeDateTo'])
//
//        //search absence with criteria :
//        // - no disciplinaryRecordRequest
//        // - not already added to list
//        String query = "FROM employment_service_request esr where exists ( " +
//                "SELECT "
//        "max(esr.id), " +
//                "esr.employee_id " +
//                "from employment_service_request " +
//                "group by request.employee_id "
//
//        //if statements to check the params
//        if (sSearch) {
//            query = query + " and (employment_service_request_reason like :employment_service_requestReasonSParam ) " +
//                    sqlParamsMap.put("employment_service_requestReasonSParam", "%" + sSearch + "%")
//        }
//
//        if (id) {
//            query = query + " and ab.id = :idParam  "
//            sqlParamsMap.put("idParam", id)
//        }
//        if (employeeId) {
//            query = query + " and ab.employee_id = :employeeIdParam  "
//            sqlParamsMap.put("employeeIdParam", employeeId)
//        }
//        if (militaryRankId) {
//            query = query + " and ab.employee_id in (select id from employee where current_employee_military_rank_id in (select id from employee_promotion where military_rank_id = :militaryRankIdParam) )  "
//            sqlParamsMap.put("militaryRankIdParam", militaryRankId)
//        }
//        if (numOfDays) {
//            query = query + " and ab.num_of_days = :numOfDaysValueParam  "
//            sqlParamsMap.put("numOfDaysValueParam", numOfDays)
//        }
//        if (absenceReason) {
//            query = query + " and ab.absence_reason = :absenceReasonParam  "
//            sqlParamsMap.put("absenceReasonParam", absenceReason.toString())
//        }
//
//        //check 3 cases of send date created > = <
//        if (fromDate) {
//            query = query + " and to_char(ab.from_date_datetime,'dd/MM/yyyy')  = :fromDate "
//            sqlParamsMap.put("fromDate", fromDate)
//        }
//        if (fromFromDate) {
//            query = query + " and ab.from_date_datetime >= :fromFromDate "
//            sqlParamsMap.put("fromFromDate", fromFromDate)
//        }
//        if (toFromDate) {
//            query = query + " and ab.from_date_datetime <= :toFromDate "
//            sqlParamsMap.put("toFromDate", toFromDate)
//        }
//
//        //check 3 cases of send date created > = <
//        if (toDate) {
//            query = query + " and to_char(ab.to_date_datetime,'dd/MM/yyyy')  = :toDate "
//            sqlParamsMap.put("toDate", toDate)
//        }
//        if (fromToDate) {
//            query = query + " and ab.to_date_datetime >= :fromToDate "
//            sqlParamsMap.put("fromToDate", fromToDate)
//        }
//        if (toToDate) {
//            query = query + " and ab.to_date_datetime <= :toToDate "
//            sqlParamsMap.put("toToDate", toToDate)
//        }
//
//        //check 3 cases of send date created > = <
//        if (noticeDate) {
//            query = query + " and to_char(ab.notice_date_datetime,'dd/MM/yyyy')  = :noticeDate "
//            sqlParamsMap.put("noticeDate", noticeDate)
//        }
//        if (fromNoticeDate) {
//            query = query + " and ab.notice_date_datetime >= :fromNoticeDate "
//            sqlParamsMap.put("fromNoticeDate", fromNoticeDate)
//        }
//        if (toNoticeDate) {
//            query = query + " and ab.notice_date_datetime <= :toNoticeDate "
//            sqlParamsMap.put("toNoticeDate", toNoticeDate)
//        }
//
//        //to apply sorting & sorting direction into sql query
//        if (columnName?.equalsIgnoreCase("absenceReason")) {
//            orderByQuery += "ORDER BY ab.absence_reason ${dir}"
//        }//to apply sorting & sorting direction into sql query
//        else if (columnName?.equalsIgnoreCase("employee")) {
//            orderByQuery += "ORDER BY ab.employee_id ${dir}"
//        } else if (columnName?.equalsIgnoreCase("fromDate")) {
//            orderByQuery += "ORDER BY ab.from_date_datetime ${dir}"
//        } else if (columnName?.equalsIgnoreCase("numOfDays")) {
//            orderByQuery += "ORDER BY ab.num_of_days  ${dir}"
//        } else if (columnName?.equalsIgnoreCase("toDate")) {
//            orderByQuery += "ORDER BY ab.to_date_datetime ${dir}"
//        } else if (columnName?.equalsIgnoreCase("noticeDate")) {
//            orderByQuery += "ORDER BY ab.notice_date_datetime  ${dir}"
//        } else if (columnName) {
//            orderByQuery += "ORDER BY ${columnName} ${dir}"
//        } else {
//            orderByQuery += "ORDER BY ab.date_created desc"
//        }
//
//        Query sqlQuery = session.createSQLQuery(
//                """
//                SELECT
//                    ab.id ,
//                    ab.employee_id ,
//                    ab.absence_reason,
//                    ab.num_of_days,
//                    COALESCE( NULLIF(ab.from_date_datetime,'0003-03-03 03:03:03') ) as from_date_datetime,
//                    COALESCE( NULLIF(ab.to_date_datetime,'0003-03-03 03:03:03') ) as to_date_datetime,
//                    COALESCE( NULLIF(ab.notice_date_datetime,'0003-03-03 03:03:03') ) as notice_date_datetime
//              """ + query + orderByQuery)
//
//        sqlParamsMap?.each {
//            sqlQuery.setParameter(it.key.toString(), it.value)
//        }
//        sqlQuery.setMaxResults(max)
//        sqlQuery.setFirstResult(offset)
//        final queryResults = sqlQuery.list()
//
//        List<Absence> results = []
//        // Transform resulting rows to a map with key organisationName.
//
//
//        queryResults.each { resultRow ->
//            Absence absence = new Absence(
//                    absenceReason: resultRow[2],
//                    numOfDays: resultRow[3],
//                    fromDate: PCPUtils.convertTimeStampToZonedDateTime(resultRow[4]),
//                    toDate: PCPUtils.convertTimeStampToZonedDateTime(resultRow[5]),
//                    noticeDate: PCPUtils.convertTimeStampToZonedDateTime(resultRow[6]))
//            absence.id = resultRow[0]
//
//            Employee employee = Employee.get(resultRow[1])
//            absence.employee = employee
//            results.add(absence)
//        }
//
//        //get total count for all records
//        if (results) {
//            Query sqlCountQuery = session.createSQLQuery(""" SELECT count(ab.id) """ + query)
//            sqlParamsMap?.each {
//                sqlCountQuery.setParameter(it.key.toString(), it.value)
//            }
//            final queryCountResults = sqlCountQuery.list()
//            totalCount = new Integer(queryCountResults[0]?.toString())
//
//            //get employee remote details
//            List<String> employeeIds = results?.employee?.id
//            GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
//            List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)
//            results?.each { Absence absence ->
//                absence?.employee = employeeList?.find { it?.id == absence?.employee?.id }
//            }
//
//        }
//        return new PagedList(resultList: results, totalCount: totalCount)
//    }

//    select id, employee_id from public.request r
//    where exists
//    (
//    SELECT
//    max(request.id),
//    request.employee_id
//    FROM
//    public.request
//    group by  request.employee_id
//    )


}