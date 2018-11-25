package ps.gov.epsilon.hr.firm.transfer

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.apache.tomcat.jni.Local
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeInternalAssignation
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
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
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -manage all internal transfer requests and get data from domain
 * <h1>Usage</h1>
 * -any service to get internal transfer info or search about internal transfer
 * <h1>Restriction</h1>
 * -must connect with pcore application to get employee and governorate information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class InternalTransferRequestService {

    MessageSource messageSource
    FormatService formatService
    GovernorateService governorateService
    EmployeeService employeeService
    ExternalTransferRequestService externalTransferRequestService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to represent old employment record
     */
    public static oldEmploymentRecordFormat = { formatService, InternalTransferRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + dataRow?.currentEmploymentRecord?.department?.toString()
        }
        return ""
    }

    /**
     * to represent new employment record
     */
    public static newEmploymentRecordFormat = { formatService, InternalTransferRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + dataRow?.department?.toString()
        }
        return ""
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
            [sort: false, search: false, hidden: false, name: "newEmploymentRecord", type: newEmploymentRecordFormat, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "oldEmploymentRecord", type: oldEmploymentRecordFormat, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "newEmploymentRecord", type: newEmploymentRecordFormat, source: 'domain'],
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
            [sort: true, search: true, hidden: false, name: "internalOrderNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.oldEmploymentRecord", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.newEmploymentRecord", type: "String", source: 'domain'],
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
        String alternativeEmployeeId = params["alternativeEmployee.id"]
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String employeeId = params["employee.id"]
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        String requestReason = params["requestReason"]
        EnumRequestStatus requestStatus = params["requestStatus"] ? EnumRequestStatus.valueOf(params["requestStatus"]) : null
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime effectiveDateFrom = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime effectiveDateTo = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])
        Long fromGovernorateId = params.long("fromGovernorate.id")
        Long toGovernorateId = params.long("toGovernorate.id")
        Long fromDepartmentId = params.long("fromDepartment.id")
        String toDepartmentId = params["toDepartment.id"]
        String militaryRankId = params["militaryRank.id"]
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        String internalOrderNumber = params["internalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])

        return InternalTransferRequest.createCriteria().list(max: max, offset: offset) {
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
                if (alternativeEmployeeId) {
                    eq("alternativeEmployee.id", alternativeEmployeeId)
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

                if (toGovernorateId || toDepartmentId) {

                    department {

                        if (toDepartmentId) {
                            eq("id", toDepartmentId)
                        }

                        if (toGovernorateId) {
                            eq("governorateId", toGovernorateId)
                        }


                    }
                }

                if (currentRequesterEmploymentRecordId) {
                    eq("currentRequesterEmploymentRecord.id", currentRequesterEmploymentRecordId)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (fromRequestDate) {
                    ge("requestDate", fromRequestDate)
                }
                if (toRequestDate) {
                    le("requestDate", toRequestDate)
                }

                if (requestDate) {
                    eq("requestDate", requestDate)
                }

                //effectiveDate
                if (effectiveDate) {
                    eq("effectiveDate", effectiveDate)
                }
                if (effectiveDateFrom) {
                    ge("effectiveDate", effectiveDateFrom)
                }

                if (effectiveDateTo) {
                    le("effectiveDate", effectiveDateTo)
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
                if (requestStatusNote) {
                    ilike("requestStatusNote", "%${requestStatusNote}%")
                }
                if (requestType) {
                    eq("requestType", requestType)
                    isNotNull()
                    isNull()
                }
                if (requesterId) {
                    eq("requester.id", requesterId)
                }
                if (requesterDepartmentId) {
                    eq("requesterDepartment.id", requesterDepartmentId)
                }

                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if(internalOrderNumber){
                    eq('internalOrderNumber', internalOrderNumber)
                }
                if(internalOrderDate){
                    eq('internalOrderDate', internalOrderDate)
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

        //block to filling remoting information from PCORE

        if (pagedResultList) {

            //collect governorateIds from currentEmploymentRecord and department
            List governorateIds = []
            governorateIds.addAll(pagedResultList.resultList?.currentEmploymentRecord?.department?.governorateId.toList())
            governorateIds.addAll(pagedResultList.resultList?.department?.governorateId.toList())
            governorateIds = governorateIds.unique()
            List<GovernorateDTO> governorates = governorateService.searchGovernorate(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: governorateIds)]))?.resultList

            //collect employee ids
            List<String> employeeIds = pagedResultList?.resultList?.employee?.id?.toList()
            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)

            //fill all remoting information
            pagedResultList.each { InternalTransferRequest internalTransferRequest ->

                //set governorate info

                //for currentEmploymentRecord
                internalTransferRequest.currentEmploymentRecord.department.transientData.governorateDTO = governorates.find {
                    it.id == internalTransferRequest?.currentEmploymentRecord?.department?.governorateId
                }

                //for department
                internalTransferRequest.department.transientData.governorateDTO = governorates.find {
                    it.id == internalTransferRequest?.department?.governorateId
                }

                //set employee info
                internalTransferRequest.employee = employees.find { it.id == internalTransferRequest?.employee?.id }

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

        pagedResultList.each { InternalTransferRequest internalTransferRequest ->
            internalTransferRequest.transientData.oldEmploymentRecord = internalTransferRequest?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + internalTransferRequest?.currentEmploymentRecord?.department?.toString()
            internalTransferRequest.transientData.newEmploymentRecord = internalTransferRequest?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + internalTransferRequest?.department?.toString()
            internalTransferRequest.transientData.requestStatus = messageSource.getMessage('EnumRequestStatus.' + internalTransferRequest.requestStatus.toString(),null,'Request Status',LocaleContextHolder.getLocale())
        }

        return pagedResultList

    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return InternalTransferRequest.
 */
    InternalTransferRequest save(GrailsParameterMap params) {
        InternalTransferRequest internalTransferRequestInstance
        Boolean closeRequest = params.boolean("closeRequest") ?: false


        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            internalTransferRequestInstance = InternalTransferRequest.get(params["id"])
            if (internalTransferRequestInstance) {
                if (params.long("version")) {
                    long version = params.long("version")
                    if (internalTransferRequestInstance.version > version) {
                        internalTransferRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('internalTransferRequest.label', null, 'internalTransferRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this internalTransferRequest while you were editing")
                        return internalTransferRequestInstance
                    }
                }
            } else {
                internalTransferRequestInstance = new InternalTransferRequest()
                internalTransferRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('internalTransferRequest.label', null, 'internalTransferRequest', LocaleContextHolder.getLocale())] as Object[], "This internalTransferRequest with ${params.id} not found")
                return internalTransferRequestInstance
            }

        } else {
            internalTransferRequestInstance = new InternalTransferRequest()
        }
        try {
            internalTransferRequestInstance.properties = params;

            //set request date as now without time
            if (!internalTransferRequestInstance?.requestDate) {
                internalTransferRequestInstance.requestDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            }

            //set current employment record and currentEmployeeMilitaryRank
            internalTransferRequestInstance.currentEmploymentRecord = internalTransferRequestInstance?.employee?.currentEmploymentRecord
            internalTransferRequestInstance.currentEmployeeMilitaryRank = internalTransferRequestInstance?.employee?.currentEmployeeMilitaryRank

            //close internal request and make employee transferred to new department and close old employment record
            if (closeRequest && internalTransferRequestInstance?.effectiveDate && internalTransferRequestInstance.requestStatus == EnumRequestStatus.APPROVED_BY_WORKFLOW) {
                //close previous employment record
                internalTransferRequestInstance.employee.currentEmploymentRecord.toDate = internalTransferRequestInstance?.effectiveDate
                internalTransferRequestInstance.employee.currentEmploymentRecord.save(flush: true, failOnError: true)

                internalTransferRequestInstance.requestStatus = EnumRequestStatus.FINISHED

                //close all employee internal assignations
                internalTransferRequestInstance.employee.currentEmploymentRecord.employeeInternalAssignations?.toList()?.each { EmployeeInternalAssignation internalAssignations ->
                    if (!internalAssignations?.assignedToDepartmentToDate) {
                        internalAssignations.assignedToDepartmentToDate = internalTransferRequestInstance?.effectiveDate
                        internalAssignations.save(flush: true, failOnError: true)
                    }
                }

                //create bew employment record
                EmploymentRecord newEmploymentRecord = new EmploymentRecord()
                newEmploymentRecord.employee = internalTransferRequestInstance?.employee
                newEmploymentRecord.department = internalTransferRequestInstance?.department
                newEmploymentRecord.employmentCategory = internalTransferRequestInstance?.employmentCategory
                newEmploymentRecord.jobTitle = internalTransferRequestInstance?.jobTitle
                newEmploymentRecord.fromDate = internalTransferRequestInstance?.effectiveDate
                newEmploymentRecord.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                newEmploymentRecord.firm = internalTransferRequestInstance?.firm
                newEmploymentRecord.save(flush: true, failOnError: true)

                //set new employment record as current
                internalTransferRequestInstance.employee.currentEmploymentRecord = newEmploymentRecord
                internalTransferRequestInstance.employee.save(flush: true, failOnError: true)

            }
            internalTransferRequestInstance= requestService.saveManagerialOrderForRequest(params, internalTransferRequestInstance)
            internalTransferRequestInstance.save(flush: true, failOnError: true);


            //close request
            if (!closeRequest) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                /**
                 * get  the workflow data
                 */
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        internalTransferRequestInstance?.employee?.id + "",
                        internalTransferRequestInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                        internalTransferRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        internalTransferRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        InternalTransferRequest.getName(),
                        internalTransferRequestInstance?.id + "",
                        !hasHRRole)

                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }


        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            internalTransferRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            internalTransferRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!internalTransferRequestInstance.hasErrors()) {
                internalTransferRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return internalTransferRequestInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<InternalTransferRequest> internalTransferRequestList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            internalTransferRequestList = InternalTransferRequest.findAllByIdInList(ids)

            internalTransferRequestList.each { InternalTransferRequest internalTransferRequest ->
                if (internalTransferRequest?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete internalTransferRequest
                    internalTransferRequest.trackingInfo.status = GeneralStatus.DELETED
                    internalTransferRequest.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (internalTransferRequestList) {
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
 * @return InternalTransferRequest.
 */
    @Transactional(readOnly = true)
    InternalTransferRequest getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = this.search(params)
            if (results) {
                return results[0]
            }
        }
        return null
    }

/**
 * to get instance with validation before create .
 * @param GrailsParameterMap params the search map.
 * @return InternalTransferRequest.
 */
    @Transactional(readOnly = true)
    InternalTransferRequest getPreCreateInstance(GrailsParameterMap params) {
        InternalTransferRequest internalTransferRequest = new InternalTransferRequest(params)

        //CHECK if employee has request in progress
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED, ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            internalTransferRequest.errors.reject('internalTransferRequest.employeeHasRequest.label')
        } else {
            //prevent add internal request if employee has external approved request
            GrailsParameterMap searchParamsExternalTransfer = new GrailsParameterMap(["employee.id": params["employeeId"], requestStatus: ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED.toString()], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            PagedResultList resultExternalTransfer = externalTransferRequestService.search(searchParamsExternalTransfer)
            if (resultExternalTransfer?.resultList?.size() > 0) {
                internalTransferRequest.errors.reject('externalTransferRequest.employeeHasTransferred.label')
            } else {
                //return empty request (success check)
                GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)
                internalTransferRequest.employee = employee
                internalTransferRequest.currentEmploymentRecord = employee?.currentEmploymentRecord
            }
        }
        return internalTransferRequest
    }

    /**
     * this method used to get the person, profession type, location remoting info
     * @param applicant
     * @return
     */
    @Transactional(readOnly = true)
    InternalTransferRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                InternalTransferRequest internalTransferRequest = results[0]

                //set alternative employee info
                if (internalTransferRequest?.alternativeEmployee) {
                    GrailsParameterMap parameterMap = new GrailsParameterMap(['id': internalTransferRequest?.alternativeEmployee?.id, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                    internalTransferRequest.alternativeEmployee = employeeService.getInstanceWithRemotingValues(parameterMap)
                }
                return internalTransferRequest
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

}