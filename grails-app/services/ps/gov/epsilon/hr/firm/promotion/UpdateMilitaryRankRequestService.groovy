package ps.gov.epsilon.hr.firm.promotion

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to update the military rank type for employee-
 * <h1>Usage</h1>
 * -for the selected employee, create new request to update his military rank type-
 * <h1>Restriction</h1>
 * -delete for new requests only-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class UpdateMilitaryRankRequestService {

    MessageSource messageSource
    def formatService
    EmployeeService employeeService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static requestStatusValue = { cService, UpdateMilitaryRankRequest rec, object, params ->
        if (rec?.requestStatus) {
            return rec?.requestStatus?.toString()
        } else {
            return ""
        }
    }

    /**
     * return the dispatch request id to be used in the create new dispatch extension or stop request
     */
    public static getRequestId = { cService, UpdateMilitaryRankRequest rec, object, params ->
        if (rec?.id) {
            return rec?.id
        } else {
            return null
        }
    }

    /**
     * return the old rank type or classification depends on request type
     */
    public static getOldRank = { cService, UpdateMilitaryRankRequest rec, object, params ->
        if (rec?.requestType == EnumRequestType.UPDATE_MILITARY_RANK_TYPE) {
            return rec?.oldRankType?.toString()
        } else {
            return rec?.oldRankClassification?.toString()
        }
    }

    /**
     * return the new rank type or classification depends on request type
     */
    public static getNewRank = { cService, UpdateMilitaryRankRequest rec, object, params ->
        if (rec?.requestType == EnumRequestType.UPDATE_MILITARY_RANK_TYPE) {
            return rec?.newRankType?.toString()
        } else {
            return rec?.newRankClassification?.toString()
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestId", type: getRequestId, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "dueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "oldRank", type: getOldRank, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "newRank", type: getNewRank, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestType", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
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
        String dueReason = params["dueReason"]
        String employeeId = params["employee.id"]
        String newRankTypeId = params["newRankType.id"]
        String oldRankTypeId = params["oldRankType.id"]
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String status = params["status"]

        ZonedDateTime dueDate = PCPUtils.parseZonedDateTime(params['dueDate'])
        ZonedDateTime fromDueDate = PCPUtils.parseZonedDateTime(params['dueDateFrom'])
        ZonedDateTime toDueDate = PCPUtils.parseZonedDateTime(params['dueDateTo'])

        String militaryRankId = params["militaryRank.id"]

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        Long firmId = params.long("firm.id")
        List<EnumRequestStatus> excludeRequestStatusList = params["excludeRequestStatusList"] ?: []

        return UpdateMilitaryRankRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("dueReason", sSearch)
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
                if (dueDate) {
                    eq("dueDate", dueDate)
                }
                //from/to :DueDate
                if (fromDueDate) {
                    ge("dueDate", fromDueDate)
                }
                if (toDueDate) {
                    le("dueDate", toDueDate)
                }
                if (dueReason) {
                    ilike("dueReason", "%${dueReason}%")
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }

                if (newRankTypeId) {
                    eq("newRankType.id", newRankTypeId)
                }
                if (oldRankTypeId) {
                    eq("oldRankType.id", oldRankTypeId)
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
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id
        //get employee remote details
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)

        //set the remote values in the transientData map
        pagedResultList?.resultList.each { UpdateMilitaryRankRequest updateMilitaryRankRequest ->
            updateMilitaryRankRequest?.employee = employeeList?.find {
                it?.id == updateMilitaryRankRequest?.employee?.id
            }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return UpdateMilitaryRankRequest.
     */
    UpdateMilitaryRankRequest save(GrailsParameterMap params) {
        UpdateMilitaryRankRequest updateMilitaryRankRequestInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            updateMilitaryRankRequestInstance = UpdateMilitaryRankRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (updateMilitaryRankRequestInstance.version > version) {
                    updateMilitaryRankRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('updateMilitaryRankRequest.label', null, 'updateMilitaryRankRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this updateMilitaryRankRequest while you were editing")
                    return updateMilitaryRankRequestInstance
                }
            }
            if (!updateMilitaryRankRequestInstance) {
                updateMilitaryRankRequestInstance = new UpdateMilitaryRankRequest()
                updateMilitaryRankRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('updateMilitaryRankRequest.label', null, 'updateMilitaryRankRequest', LocaleContextHolder.getLocale())] as Object[], "This updateMilitaryRankRequest with ${params.id} not found")
                return updateMilitaryRankRequestInstance
            }
        } else {
            updateMilitaryRankRequestInstance = new UpdateMilitaryRankRequest()
        }
        try {
            updateMilitaryRankRequestInstance.properties = params;

            //save the employee instance and current employment record and military rank in the request
            Employee employee = updateMilitaryRankRequestInstance?.employee
            if (employee?.currentEmploymentRecord) {
                //save the current employee record
                updateMilitaryRankRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            if (employee?.currentEmployeeMilitaryRank) {
                //save the current employee military rank
                updateMilitaryRankRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }

            updateMilitaryRankRequestInstance= requestService.saveManagerialOrderForRequest(params, updateMilitaryRankRequestInstance)
            updateMilitaryRankRequestInstance.save(flush: true, failOnError: true);

            if(updateMilitaryRankRequestInstance?.requestStatus == EnumRequestStatus.CREATED) {
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
                        UpdateMilitaryRankRequest.getName(),
                        updateMilitaryRankRequestInstance?.id + "",
                        !hasHRRole)

                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            updateMilitaryRankRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            updateMilitaryRankRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            updateMilitaryRankRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return updateMilitaryRankRequestInstance
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
            UpdateMilitaryRankRequest instance = UpdateMilitaryRankRequest.get(id)
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
     * @return UpdateMilitaryRankRequest.
     */
    @Transactional(readOnly = true)
    UpdateMilitaryRankRequest getInstance(GrailsParameterMap params) {
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
    UpdateMilitaryRankRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
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
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return updateMilitaryRankRequest.
     */
    @Transactional(readOnly = true)
    UpdateMilitaryRankRequest getPreCreateInstance(GrailsParameterMap params) {
        UpdateMilitaryRankRequest updateMilitaryRankRequest = new UpdateMilitaryRankRequest(params)
        //CHECK if employee has request in [progress or approved] requests
        GrailsParameterMap searchParams = new GrailsParameterMap(["employee.id": params["employeeId"], requestType: params["requestType"], excludeRequestStatusList: [ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED, EnumRequestStatus.APPROVED]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PagedResultList result = this.search(searchParams)
        if (result?.resultList?.size() > 0) {
            updateMilitaryRankRequest.errors.reject('request.employeeHasRequest.error.label')
        } else {
            GrailsParameterMap employeeParam = new GrailsParameterMap([id: params["employeeId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(employeeParam)

            //check if the employee current status category is COMMITTED
            if (employee?.categoryStatus?.id == EnumEmployeeStatusCategory.UNCOMMITTED.value) {
                updateMilitaryRankRequest.errors.reject('request.employeeUncommitted.error.label')
            } else {
                updateMilitaryRankRequest?.employee = employee
                updateMilitaryRankRequest?.requestDate = ZonedDateTime.now()
                updateMilitaryRankRequest?.oldRankType = employee?.currentEmployeeMilitaryRank?.militaryRankType
                updateMilitaryRankRequest?.oldRankClassification = employee?.currentEmployeeMilitaryRank?.militaryRankClassification
                updateMilitaryRankRequest?.requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
                updateMilitaryRankRequest?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }
        }
        return updateMilitaryRankRequest
    }

}