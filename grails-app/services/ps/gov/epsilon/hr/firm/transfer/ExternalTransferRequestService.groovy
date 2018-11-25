package ps.gov.epsilon.hr.firm.transfer

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.dispatch.DispatchList
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -manage all external transfer requests and get data from domain
 * <h1>Usage</h1>
 * -any service to get external transfer info or search about external transfer
 * <h1>Restriction</h1>
 * -must connect with pcore application to get employee,organization and governorate information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ExternalTransferRequestService {

    MessageSource messageSource
    FormatService formatService
    GovernorateService governorateService
    EmployeeService employeeService
    OrganizationService organizationService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to represent old employment record
     */
    public static oldEmploymentRecordFormat = { formatService, ExternalTransferRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + dataRow?.currentEmploymentRecord?.department?.toString()
        }
        return ""
    }

    /**
     * to represent if allowed to add transfer and clearance
     */
    public static hasClearanceAndTransfer = { formatService, ExternalTransferRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.requestStatus in [EnumRequestStatus.APPROVED_BY_WORKFLOW, EnumRequestStatus.APPROVED, EnumRequestStatus.ADD_TO_LIST, EnumRequestStatus.SENT_BY_LIST]
        }
        return false
    }

    /**
     * to represent if allowed to add transfer and clearance
     */
    public static viewCloseRequest = { formatService, ExternalTransferRequest dataRow, object, params ->
        if (dataRow) {
            return (dataRow?.requestStatus == EnumRequestStatus.APPROVED) && dataRow?.hasClearance && dataRow?.hasTransferPermission
        }
        return false
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "oldEmploymentRecord", type: oldEmploymentRecordFormat, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.organizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "hasClearanceAndTransfer", type: hasClearanceAndTransfer, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "viewCloseRequest", type: viewCloseRequest, source: 'domain'],
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
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "oldEmploymentRecord", type: oldEmploymentRecordFormat, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.organizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> EXTERNAL_TRANSFER_LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "oldEmploymentRecord", type: oldEmploymentRecordFormat, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.organizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.oldEmploymentRecord", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.organizationDTO", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestReason", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatusNote", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.requestStatus", type: "String", source: 'domain'],
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
        ZonedDateTime clearanceDate = PCPUtils.parseZonedDateTime(params['clearanceDate'])
        String clearanceNote = params["clearanceNote"]
        String clearanceOrderNo = params["clearanceOrderNo"]
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime fromEffectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime toEffectiveDate = PCPUtils.parseZonedDateTimeWithBiggestTime(params['effectiveDateTo'])
        String employeeId = params["employee.id"]
        String employeeIdForList = params.long("employee.idForList")
        Boolean hasClearance = params.boolean("hasClearance")
        Boolean hasTransferPermission = params.boolean("hasTransferPermission")
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTimeWithBiggestTime(params['requestDateTo'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String militaryRankId = params["militaryRank.id"]
        String militaryRankIdForList = params["militaryRank.idForList"]
        Long toOrganizationId = params.long("toOrganizationId")
        ZonedDateTime transferPermissionDate = PCPUtils.parseZonedDateTime(params['transferPermissionDate'])
        String transferPermissionNote = params["transferPermissionNote"]
        String transferPermissionOrderNo = params["transferPermissionOrderNo"]
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []
        List<EnumRequestStatus> includeRequestStatusList = params["includeRequestStatusList"] ?: []
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null
        Long fromGovernorateId = params.long("fromGovernorate.id")
        String fromDepartmentId = params["fromDepartment.id"]
        Long firmId = params.long("firmId")
        Boolean isJobToUpdateStatus = params.boolean("isJobToUpdateStatus")
        /**
         * in case: search for add modal
         */
        if (employeeIdForList) {
            employeeId = employeeIdForList
        }
        if (militaryRankIdForList) {
            militaryRankId = militaryRankIdForList
        }

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        List<EnumRequestStatus> requestStatusList = params["requestStatusList"] ? params["requestStatusList"]?.split(",")?.collect { String value -> return EnumRequestStatus.valueOf(value) } : []

        return ExternalTransferRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("clearanceNote", sSearch)
                    ilike("clearanceOrderNo", sSearch)
                    ilike("requestReason", sSearch)
                    ilike("requestStatusNote", sSearch)
                    ilike("transferPermissionNote", sSearch)
                    ilike("transferPermissionOrderNo", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (clearanceDate) {
                    le("clearanceDate", clearanceDate)
                }
                if (clearanceNote) {
                    ilike("clearanceNote", "%${clearanceNote}%")
                }
                if (clearanceOrderNo) {
                    ilike("clearanceOrderNo", "%${clearanceOrderNo}%")
                }

                //to search in currentEmploymentRecord by id or governorate or department
                if (currentEmploymentRecordId || fromGovernorateId || fromDepartmentId) {

                    currentEmploymentRecord {

                        if (fromGovernorateId || fromDepartmentId) {

                            department {

                                if (fromDepartmentId) {
                                    eq("id", fromDepartmentId)
                                }

                                if (fromGovernorateId) {
                                    eq("governorateId", fromGovernorateId)
                                }
                            }
                        }

                        if (currentEmploymentRecordId) {
                            eq("id", currentEmploymentRecordId)
                        }
                    }
                }

                if (currentEmployeeMilitaryRankId || militaryRankId) {
                    currentEmployeeMilitaryRank {

                        if (currentEmployeeMilitaryRankId) {
                            eq("id", currentEmployeeMilitaryRankId)
                        }

                        if (militaryRankId) {
                            eq("militaryRank.id", militaryRankId)
                        }
                    }
                }

                if (currentRequesterEmploymentRecordId) {
                    eq("currentRequesterEmploymentRecord.id", currentRequesterEmploymentRecordId)
                }
                if (effectiveDate) {
                    eq("effectiveDate", effectiveDate)
                }
                if (fromEffectiveDate) {
                    ge("effectiveDate", fromEffectiveDate)
                }
                if (toEffectiveDate) {
                    le("effectiveDate", toEffectiveDate)
                }

                if (isJobToUpdateStatus == true) {
                    le("effectiveDate", ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS))
                }

                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (hasClearance) {
                    eq("hasClearance", hasClearance)
                }
                if (hasTransferPermission) {
                    eq("hasTransferPermission", hasTransferPermission)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
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
                if (excludeRequestStatusList) {
                    not {
                        inList("requestStatus", excludeRequestStatusList)
                    }
                }
                if (includeRequestStatusList) {
                    inList("requestStatus", includeRequestStatusList)
                }
                if (requestStatusList) {
                    inList("requestStatus", requestStatusList)
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
                if (toOrganizationId) {
                    eq("toOrganizationId", toOrganizationId)
                }
                if (transferPermissionDate) {
                    le("transferPermissionDate", transferPermissionDate)
                }
                if (transferPermissionNote) {
                    ilike("transferPermissionNote", "%${transferPermissionNote}%")
                }
                if (transferPermissionOrderNo) {
                    ilike("transferPermissionOrderNo", "%${transferPermissionOrderNo}%")
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
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

            //collect governorateIds from currentEmploymentRecord
            List governorateIds = pagedResultList?.resultList?.currentEmploymentRecord?.department?.governorateId.toList()?.unique()
            List<String> employeeIds = pagedResultList?.resultList?.employee?.id?.toList()
            List organizationIds = pagedResultList?.resultList?.toOrganizationId?.toList()

            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())

            //get employee info
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)

            //get governorate info
            List<GovernorateDTO> governorates = governorateService.searchGovernorate(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: governorateIds)]))?.resultList

            //get organization info
            List<OrganizationDTO> organizations = organizationService.searchOrganization(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds)]))?.resultList

            //fill all remoting information
            pagedResultList.each { ExternalTransferRequest externalTransferRequest ->

                //set governorate info
                externalTransferRequest.currentEmploymentRecord.department.transientData.governorateDTO = governorates.find {
                    it.id == externalTransferRequest?.currentEmploymentRecord?.department?.governorateId
                }

                //set organization info
                externalTransferRequest.transientData.organizationDTO = organizations.find {
                    it.id == externalTransferRequest?.toOrganizationId
                }

                //set employee info
                externalTransferRequest.employee = employees.find { it.id == externalTransferRequest?.employee?.id }

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

        pagedResultList.each { ExternalTransferRequest externalTransferRequest ->
            externalTransferRequest.transientData.oldEmploymentRecord = externalTransferRequest?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + externalTransferRequest?.currentEmploymentRecord?.department?.toString()
            externalTransferRequest.transientData.requestStatus = messageSource.getMessage('EnumRequestStatus.' + externalTransferRequest.requestStatus.toString(), null, 'Request Status', LocaleContextHolder.getLocale())
        }

        return pagedResultList

    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return ExternalTransferRequest.
 */
    ExternalTransferRequest save(GrailsParameterMap params) {
        ExternalTransferRequest externalTransferRequestInstance
        Boolean closeRequest = params.boolean("closeRequest") ?: false

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            externalTransferRequestInstance = ExternalTransferRequest.get(params["id"])

            if (externalTransferRequestInstance) {
                if (params.long("version")) {
                    long version = params.long("version")
                    if (externalTransferRequestInstance.version > version) {
                        externalTransferRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('externalTransferRequest.label', null, 'externalTransferRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this externalTransferRequest while you were editing")
                        return externalTransferRequestInstance
                    }
                }
            } else {
                externalTransferRequestInstance = new ExternalTransferRequest()
                externalTransferRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('externalTransferRequest.label', null, 'externalTransferRequest', LocaleContextHolder.getLocale())] as Object[], "This externalTransferRequest with ${params.id} not found")
                return externalTransferRequestInstance
            }
        } else {
            externalTransferRequestInstance = new ExternalTransferRequest()
        }
        try {
            externalTransferRequestInstance.properties = params;

            /**
             * required fromProvince & toProvince if and only if when fromFirm & toFirm are the same firm
             */
            if (externalTransferRequestInstance?.toOrganizationId == externalTransferRequestInstance?.fromFirm?.coreOrganizationId) {
                if (!externalTransferRequestInstance?.fromProvince) {
                    transactionStatus.setRollbackOnly()
                    externalTransferRequestInstance.errors.reject('externalTransferRequest.error.fromProvince.message')
                    return externalTransferRequestInstance
                } else if (!externalTransferRequestInstance?.toProvince) {
                    transactionStatus.setRollbackOnly()
                    externalTransferRequestInstance.errors.reject('externalTransferRequest.error.toProvince.message')
                    return externalTransferRequestInstance
                }
            }

            //set request date as now without time
            if (externalTransferRequestInstance?.requestDate) {
                externalTransferRequestInstance.requestDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            }

            //set current employment record and currentEmployeeMilitaryRank
            externalTransferRequestInstance.currentEmploymentRecord = externalTransferRequestInstance?.employee?.currentEmploymentRecord
            externalTransferRequestInstance.currentEmployeeMilitaryRank = externalTransferRequestInstance?.employee?.currentEmployeeMilitaryRank

            externalTransferRequestInstance = requestService.saveManagerialOrderForRequest(params, externalTransferRequestInstance)
            externalTransferRequestInstance.save(flush: true, failOnError: true);

            if (externalTransferRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                /**
                 * get  the workflow data
                 */
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        externalTransferRequestInstance?.employee?.id + "",
                        externalTransferRequestInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                        externalTransferRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        externalTransferRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        ExternalTransferRequest.getName(),
                        externalTransferRequestInstance?.id + "",
                        !hasHRRole)

                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }

            }

            //close current employment record to set effective date as to date
            if (closeRequest == true && externalTransferRequestInstance.requestStatus == EnumRequestStatus.APPROVED && externalTransferRequestInstance?.hasTransferPermission && externalTransferRequestInstance?.hasClearance) {
                externalTransferRequestInstance.requestStatus = EnumRequestStatus.FINISHED
                externalTransferRequestInstance.save(flush: true, failOnError: true)
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            externalTransferRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            externalTransferRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!externalTransferRequestInstance.hasErrors()) {
                externalTransferRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return externalTransferRequestInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ExternalTransferRequest> externalTransferRequestList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }
            externalTransferRequestList = ExternalTransferRequest.findAllByIdInList(ids)
            externalTransferRequestList.each { ExternalTransferRequest externalTransferRequest ->
                if (externalTransferRequest?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete externalTransferRequest
                    externalTransferRequest.trackingInfo.status = GeneralStatus.DELETED
                    externalTransferRequest.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (externalTransferRequestList) {
                deleteBean.status = true
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
 * @return ExternalTransferRequest.
 */
    @Transactional(readOnly = true)
    ExternalTransferRequest getInstance(GrailsParameterMap params) {
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
     * to get instance with validation before create .
     * @param GrailsParameterMap params the search map.
     * @return ExternalTransferRequest.
     */
    @Transactional(readOnly = true)
    ExternalTransferRequest getPreCreateInstance(GrailsParameterMap params) {
        ExternalTransferRequest externalTransferRequest = new ExternalTransferRequest(params)

        //CHECK if employee has request in progress or approved
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id"           : params["employeeId"],
                                                                  excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED],
                                                                  firmId                  : params['firmId']],
                WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            if (result?.requestStatus?.contains(ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED)) {
                externalTransferRequest.errors.reject('externalTransferRequest.employeeHasTransferred.label')
            } else {
                externalTransferRequest.errors.reject('externalTransferRequest.employeeHasRequest.label')
            }
        } else {
            GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id': params['firmId'], id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)
            externalTransferRequest.employee = employee
            externalTransferRequest.currentEmploymentRecord = employee?.currentEmploymentRecord
        }

        return externalTransferRequest
    }

    /**
     * this method used to get the person, profession type, location remoting info
     * @param applicant
     * @return
     */
    @Transactional(readOnly = true)
    ExternalTransferRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        Boolean checkClearanceAndTransfer = params.boolean("checkClearanceAndTransfer")
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                ExternalTransferRequest externalTransferRequest = results[0]
                if (checkClearanceAndTransfer && externalTransferRequest.hasClearance && externalTransferRequest.hasTransferPermission) {
                    return null
                }
                return externalTransferRequest
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
        String nameProperty = params["nameProperty"] ?: "employee.id"
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
     * update transferred employee status.
     * @param Firm firm.
     * @return void.
     */
    void updateEmployeeStatusToTransferred(Firm firm) {
        //TODO: to be called by the job!
        try {
            Map map = [
                    "requestStatus"      : EnumRequestStatus.FINISHED.toString(),
                    "firmId"             : firm?.id,
                    "isJobToUpdateStatus": "true",
            ]
            GrailsParameterMap params = new GrailsParameterMap(map, null)

            //return the list of all external transfer request which are finished
            List<ExternalTransferRequest> externalTransferRequestList = this.search(params)

            if (externalTransferRequestList) {

                //loop on each external transfer request to update the employee  status history
                externalTransferRequestList.each { ExternalTransferRequest externalTransferRequestInstance ->

                    //close current employment record to set effective date as to date
                    if (externalTransferRequestInstance.requestStatus == EnumRequestStatus.FINISHED && externalTransferRequestInstance?.hasTransferPermission && externalTransferRequestInstance?.hasClearance) {

                        //set effective date as to date in current employment record
                        EmploymentRecord employmentRecord = externalTransferRequestInstance.employee.currentEmploymentRecord
                        employmentRecord.toDate = externalTransferRequestInstance?.effectiveDate
                        employmentRecord.save(flush: true, failOnError: true)

                        //create new employee status
                        EmployeeStatusHistory employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory.transientData.put("firm", firm);
                        employeeStatusHistory.employee = externalTransferRequestInstance?.employee

                        //close any open history status
                        employeeStatusHistory.employee.employeeStatusHistories.each {
                            if (!it.toDate || it.toDate == PCPUtils.DEFAULT_ZONED_DATE_TIME) {
                                it.toDate = externalTransferRequestInstance?.effectiveDate
                            }
                        }

                        //set employee uncommitted
                        employeeStatusHistory.employee.categoryStatus = EmployeeStatusCategory.get(EnumEmployeeStatusCategory.UNCOMMITTED.getValue(firm?.code))
                        employeeStatusHistory.employee.save(flush: true)

                        //set employee status as transferred
                        employeeStatusHistory.employeeStatus = EmployeeStatus.get(EnumEmployeeStatus.TRANSFERRED.getValue(firm?.code))
                        employeeStatusHistory.fromDate = externalTransferRequestInstance?.effectiveDate
                        employeeStatusHistory.save(flush: true, failOnError: true)

                        //set request as processed
                        externalTransferRequestInstance.requestStatus = EnumRequestStatus.PROCESSED
                        externalTransferRequestInstance.save(flush: true, failOnError: true)
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }

}