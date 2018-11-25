package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class LoanNoticeReplayRequestService {

    MessageSource messageSource
    FormatService formatService
    OrganizationService organizationService
    EmployeeService employeeService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to represent request status
     */
    public static getStatus = { formatService, LoanNoticeReplayRequest dataRow, object, params ->
        if (dataRow) {
            return dataRow?.requestStatus?.toString()
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.requestedByOrganizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "status", type: getStatus, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.requestedByOrganizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "status", type: getStatus, source: 'domain'],
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

        String loanNoticeEncodedId = params["loanNotice.encodedId"]
        String loanNoticeId = params["loanNotice.id"]
        if (loanNoticeEncodedId) {
            loanNoticeId = HashHelper.decode(loanNoticeEncodedId)
        }
        List<Map<String, String>> orderBy = params.list("orderBy")
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String description = params["description"]
        String employeeId = params["employee.id"]
        String parentRequestId = params["parentRequestId"]
        Short periodInMonths = params.long("periodInMonths")
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        Long requestedByOrganizationId = params.long("requestedByOrganizationId")
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String militaryRankId = params["militaryRank.id"]

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])

        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['toDateTo'])

        ZonedDateTime effectiveDateFrom = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime effectiveDateTo = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])

        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['requestDateTo'])

        EnumRequestStatus requestStatus

        if (params['requestStatusHidden']) {
            requestStatus = params["requestStatusHidden"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatusHidden"]) : null
        } else if (params["requestStatus"]) {
            requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        }
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return LoanNoticeReplayRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
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
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (currentRequesterEmploymentRecordId) {
                    eq("currentRequesterEmploymentRecord.id", currentRequesterEmploymentRecordId)
                }
                if (description) {
                    ilike("description", "%${description}%")
                }

                //from date
                if (fromDate) {
                    eq("fromDate", fromDate)
                }
                if (fromDateFrom) {
                    ge("fromDate", fromDateFrom)
                }

                if (fromDateTo) {
                    le("fromDate", fromDateTo)
                }

                //to date
                if (toDate) {
                    eq("toDate", toDate)
                }
                if (toDateFrom) {
                    ge("toDate", toDateFrom)
                }
                if (toDateTo) {
                    le("toDate", toDateTo)
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

                if (requestDateFrom) {
                    ge("requestDate", requestDateFrom)
                }
                if (requestDateTo) {
                    le("requestDate", requestDateTo)
                }

                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (loanNoticeId) {
                    eq("loanNotice.id", loanNoticeId)
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
                if (requestedByOrganizationId) {
                    eq("requestedByOrganizationId", requestedByOrganizationId)
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

        if (pagedResultList.resultList) {

            SearchBean searchBean = new SearchBean()

            //collect organizationIds
            List organizationIds = pagedResultList?.requestedByOrganizationId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
            //get all organization info
            List<OrganizationDTO> organizationList = organizationService.searchOrganization(searchBean)?.resultList

            //collect employeeIds
            List<String> employeeIds = pagedResultList?.employee?.id?.toList()

            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            //get employee info
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)

            //loop to fill all remoting values
            pagedResultList.each { LoanNoticeReplayRequest loanNoticeReplayRequest ->

                //fill all employee info
                if (loanNoticeReplayRequest?.employee) {
                    loanNoticeReplayRequest.employee = employees.find { it.id == loanNoticeReplayRequest?.employee?.id }
                }

                //fill all organization info
                if (loanNoticeReplayRequest?.requestedByOrganizationId) {
                    loanNoticeReplayRequest.transientData.requestedByOrganizationDTO = organizationList.find {
                        it.id == loanNoticeReplayRequest.requestedByOrganizationId
                    }
                }

            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return LoanNoticeReplayRequest.
 */
    LoanNoticeReplayRequest save(GrailsParameterMap params) {
        LoanNoticeReplayRequest loanNoticeReplayRequestInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            loanNoticeReplayRequestInstance = LoanNoticeReplayRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanNoticeReplayRequestInstance.version > version) {
                    loanNoticeReplayRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('loanNoticeReplayRequest.label', null, 'loanNoticeReplayRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanNoticeReplayRequest while you were editing")
                    return loanNoticeReplayRequestInstance
                }
            }
            if (!loanNoticeReplayRequestInstance) {
                loanNoticeReplayRequestInstance = new LoanNoticeReplayRequest()
                loanNoticeReplayRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('loanNoticeReplayRequest.label', null, 'loanNoticeReplayRequest', LocaleContextHolder.getLocale())] as Object[], "This loanNoticeReplayRequest with ${params.id} not found")
                return loanNoticeReplayRequestInstance
            }
        } else {
            loanNoticeReplayRequestInstance = new LoanNoticeReplayRequest()
        }
        try {
            loanNoticeReplayRequestInstance.properties = params;

            //prevent change employee info when edit
            if (!loanNoticeReplayRequestInstance?.id) {
                Employee employee
                LoanNotice loanNoticeInstance
                //load loan notice and employee information
                if (params['loanNotice.encodedId'] && params["employee.id"]) {
                    loanNoticeInstance = LoanNotice.load(HashHelper.decode(params['loanNotice.encodedId']))
                } else {
                    loanNoticeReplayRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employee.label', null, 'loanNominatedEmployee', LocaleContextHolder.getLocale())] as Object[], "This loanNominatedEmployee with ${params.id} not found")
                    return loanNoticeReplayRequestInstance
                }

                //get employee info
                employee = Employee.load(params["employee.id"])

                //check that employee not added before
                int count = LoanNoticeReplayRequest.createCriteria().count {
                    eq('employee.id', employee?.id)
                    eq('requestStatus', EnumRequestStatus.CREATED)
                    eq('trackingInfo.status', GeneralStatus.ACTIVE)
                    loanNotice {
                        eq('trackingInfo.status', GeneralStatus.ACTIVE)
                    }
                }

                if (count >= 1) {
                    loanNoticeReplayRequestInstance.errors.reject('loanNoticeReplayRequest.employeeUnique.message')
                    return loanNoticeReplayRequestInstance
                }

                //set current employee info
                loanNoticeReplayRequestInstance.employee = employee
                loanNoticeReplayRequestInstance.currentEmploymentRecord = employee?.currentEmploymentRecord;
                loanNoticeReplayRequestInstance.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank

                //assign request info from notice info
                loanNoticeReplayRequestInstance.requestedByOrganizationId = loanNoticeInstance?.requesterOrganizationId
                loanNoticeReplayRequestInstance.fromDate = loanNoticeInstance?.fromDate
                loanNoticeReplayRequestInstance.toDate = loanNoticeInstance?.toDate
                loanNoticeReplayRequestInstance.periodInMonths = loanNoticeInstance?.periodInMonths
                loanNoticeReplayRequestInstance.description = loanNoticeInstance?.description
                loanNoticeReplayRequestInstance.loanNotice = loanNoticeInstance
            }

            //set request date as now
            loanNoticeReplayRequestInstance.requestDate = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)

            loanNoticeReplayRequestInstance = requestService.saveManagerialOrderForRequest(params, loanNoticeReplayRequestInstance)

            if (!loanNoticeReplayRequestInstance.requestStatus) {
                loanNoticeReplayRequestInstance.requestStatus = EnumRequestStatus.CREATED
            }

            loanNoticeReplayRequestInstance.save(flush: true, failOnError: true);

            if (loanNoticeReplayRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)
                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        null, null, null, null,
                        LoanNoticeReplayRequest.getName(),
                        loanNoticeReplayRequestInstance?.id + "",
                        !hasHRRole)
                // save workflow path details & update request status
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }


        } catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            loanNoticeReplayRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            loanNoticeReplayRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!loanNoticeReplayRequestInstance?.hasErrors()) {
                loanNoticeReplayRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return loanNoticeReplayRequestInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<LoanNoticeReplayRequest> loanNoticeReplayRequestList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            loanNoticeReplayRequestList = LoanNoticeReplayRequest.findAllByIdInList(ids)

            loanNoticeReplayRequestList.each { LoanNoticeReplayRequest loanNoticeReplayRequest ->
                if (loanNoticeReplayRequest?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete loanNoticeReplayRequest
                    loanNoticeReplayRequest.trackingInfo.status = GeneralStatus.DELETED
                    loanNoticeReplayRequest.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (loanNoticeReplayRequestList) {
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
 * @return LoanNoticeReplayRequest.
 */
    @Transactional(readOnly = true)
    LoanNoticeReplayRequest getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting info.
     * @param GrailsParameterMap params the search map.
     * @return LoanNominatedEmployee.
     */
    @Transactional(readOnly = true)
    LoanNoticeReplayRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
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