package ps.gov.epsilon.hr.firm.absence

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.disciplinary.EmployeeViolation
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployee
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployeeService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotSavedException
import ps.gov.epsilon.workflow.exceptions.v1.WorkflowNotValidActionException
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.person.PersonService
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -To create/manage new request which aims to announce that the employee is returned back from his previous absence -
 * <h1>Usage</h1>
 * -Used  as to announce that the employee is returned back from his previous absence-
 * <h1>Restriction</h1>
 * -should be created from absence-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ReturnFromAbsenceRequestService {

    MessageSource messageSource
    def formatService
    AbsenceService absenceService
    EmployeeService employeeService
    PersonService personService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static getAbsenceEncodedId = { cService, ReturnFromAbsenceRequest rec, object, params ->
        if (rec?.absence) {
            return rec?.absence?.encodedId
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
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "absenceEncodedId", type: getAbsenceEncodedId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "absence.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualAbsenceReason", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "actualReturnDate", type: "ZonedDate", source: 'domain'],
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
        String absenceId = params["absence.id"]

        ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason actualAbsenceReason = params["actualAbsenceReason"] ? ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.valueOf(params["actualAbsenceReason"]) : null
        ZonedDateTime actualReturnDate = PCPUtils.parseZonedDateTime(params['actualReturnDate'])
        ZonedDateTime actualReturnDateFrom = PCPUtils.parseZonedDateTime(params['actualReturnDateFrom'])
        ZonedDateTime actualReturnDateTo = PCPUtils.parseZonedDateTime(params['actualReturnDateTo'])
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String employeeId = params["employee.id"]
        Long firmId = params.long("firm.id")
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String status = params["status"]
        String militaryRankId = params["militaryRank.id"]

        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return ReturnFromAbsenceRequest.createCriteria().list(max: max, offset: offset) {
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
                if (absenceId) {
                    eq("absence.id", absenceId)
                }
                if (actualAbsenceReason) {
                    eq("actualAbsenceReason", actualAbsenceReason)
                }
                if (actualReturnDate) {
                    le("actualReturnDate", actualReturnDate)
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
                if (actualReturnDateFrom) {
                    ge("actualReturnDate", actualReturnDateFrom)
                }
                if (actualReturnDateTo) {
                    le("actualReturnDate", actualReturnDateTo)
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
                order(columnName, dir)
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
        pagedResultList?.resultList?.each { ReturnFromAbsenceRequest returnFromAbsenceRequest ->
            returnFromAbsenceRequest?.employee = employeeList?.find { it?.id == returnFromAbsenceRequest?.employee?.id }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ReturnFromAbsenceRequest.
     */
    ReturnFromAbsenceRequest save(GrailsParameterMap params) {
        ReturnFromAbsenceRequest returnFromAbsenceRequestInstance
        //in case: id is encoded
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            returnFromAbsenceRequestInstance = ReturnFromAbsenceRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (returnFromAbsenceRequestInstance.version > version) {
                    returnFromAbsenceRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('returnFromAbsenceRequest.label', null, 'returnFromAbsenceRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this returnFromAbsenceRequest while you were editing")
                    return returnFromAbsenceRequestInstance
                }
            }
            if (!returnFromAbsenceRequestInstance) {
                returnFromAbsenceRequestInstance = new ReturnFromAbsenceRequest()
                returnFromAbsenceRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('returnFromAbsenceRequest.label', null, 'returnFromAbsenceRequest', LocaleContextHolder.getLocale())] as Object[], "This returnFromAbsenceRequest with ${params.id} not found")
                return returnFromAbsenceRequestInstance
            }
        } else {
            returnFromAbsenceRequestInstance = new ReturnFromAbsenceRequest()
        }
        try {
            returnFromAbsenceRequestInstance.properties = params;

            if(returnFromAbsenceRequestInstance.actualReturnDate < returnFromAbsenceRequestInstance?.absence?.fromDate){
                returnFromAbsenceRequestInstance?.errors?.reject("returnFromAbsenceRequest.toDate.error.message")
                return returnFromAbsenceRequestInstance
            }
            //save the employee instance and current employment record
            Employee employee = returnFromAbsenceRequestInstance?.employee
            if (employee?.currentEmploymentRecord) {
                returnFromAbsenceRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            //save the employee instance and current military rank in the request
            if (employee?.currentEmployeeMilitaryRank) {
                returnFromAbsenceRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }

            //check if the original absence was not sent to any list, then move the request to approved.
            List<ViolationListEmployee> violationListEmployee = ViolationListEmployee.findAllByEmployeeViolation(returnFromAbsenceRequestInstance?.absence)

            //if the absence was not sent to any list, we reflect the changes on original absence and close it.
            //and we close absence status directly.
            if(violationListEmployee.size()==0) {
                returnFromAbsenceRequestInstance.requestStatus = EnumRequestStatus.APPROVED
                returnFromAbsenceRequestInstance?.absence?.toDate = returnFromAbsenceRequestInstance?.actualReturnDate
                //calculate the numOfDays and set to absence:
                returnFromAbsenceRequestInstance?.absence?.numOfDays = ChronoUnit.DAYS.between(returnFromAbsenceRequestInstance?.absence?.fromDate?.toLocalDate(), returnFromAbsenceRequestInstance?.absence?.toDate?.toLocalDate())
                returnFromAbsenceRequestInstance?.absence?.violationStatus = EnumViolationStatus.CLOSED
                returnFromAbsenceRequestInstance?.absence?.actualAbsenceReason = returnFromAbsenceRequestInstance.actualAbsenceReason

                //close absence status, and the employee will be working again.
                //get the employee status : working
                EmployeeStatus employeeStatusWorking = EmployeeStatus.get(EnumEmployeeStatus.WORKING.value)
                EmployeeStatus employeeStatusAbsence = EmployeeStatus.get(EnumEmployeeStatus.ABSENCE.value)
                EmployeeStatusHistory employeeStatusHistory

                //update employee status history and set the absence status:
                if (employeeStatusWorking) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory?.employee = returnFromAbsenceRequestInstance?.employee
                    employeeStatusHistory?.fromDate = returnFromAbsenceRequestInstance?.actualReturnDate
                    employeeStatusHistory?.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    employeeStatusHistory?.employeeStatus = employeeStatusWorking
                    employeeStatusHistory.save(flush: true, failOnError: true)
                    returnFromAbsenceRequestInstance?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                }
                if (employeeStatusAbsence) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory = EmployeeStatusHistory.createCriteria().list() {
                        eq('employeeStatus.id', employeeStatusAbsence.id)
                        eq('employee.id', returnFromAbsenceRequestInstance?.employee?.id)
                        eq('toDate', PCPUtils.DEFAULT_ZONED_DATE_TIME)
                        order("trackingInfo.dateCreatedUTC", "desc")
                    }[0]
                    if (employeeStatusHistory) {
                        employeeStatusHistory?.toDate = returnFromAbsenceRequestInstance?.actualReturnDate
                        employeeStatusHistory?.save(flush: true, failOnError: true)
                    }
                }
            }else{
                returnFromAbsenceRequestInstance?.absence?.violationStatus = EnumViolationStatus.RETURNED
            }

            returnFromAbsenceRequestInstance = requestService.saveManagerialOrderForRequest(params, returnFromAbsenceRequestInstance)

            returnFromAbsenceRequestInstance.save(failOnError: true, flush: true);

            if(returnFromAbsenceRequestInstance.requestStatus==EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)
                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        ReturnFromAbsenceRequest.getName(),
                        returnFromAbsenceRequestInstance?.id + "",
                        !hasHRRole)
                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            returnFromAbsenceRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            returnFromAbsenceRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            returnFromAbsenceRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return returnFromAbsenceRequestInstance
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
            ReturnFromAbsenceRequest instance = ReturnFromAbsenceRequest.get(id)
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
     * @return ReturnFromAbsenceRequest.
     */
    @Transactional(readOnly = true)
    ReturnFromAbsenceRequest getInstance(GrailsParameterMap params) {
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
    ReturnFromAbsenceRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                ReturnFromAbsenceRequest returnFromAbsenceRequest = results[0]
                return returnFromAbsenceRequest
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
     * to get instance with validation before create.
     * @param GrailsParameterMap params the search map.
     * @return absence.
     */
    @Transactional(readOnly = true)
    ReturnFromAbsenceRequest getPreCreateInstance(GrailsParameterMap params) {
        ReturnFromAbsenceRequest returnFromAbsenceRequest = new ReturnFromAbsenceRequest(params)
        GrailsParameterMap absenceParam = new GrailsParameterMap([id: params["absenceId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Absence absence = absenceService.getInstanceWithRemotingValues(absenceParam)
        if (absence?.violationStatus == EnumViolationStatus.RETURNED) {
            returnFromAbsenceRequest.errors.reject('returnFromAbsenceRequest.returned.exist.error.label')
        } else if (absence?.violationStatus == EnumViolationStatus.CLOSED) {
            returnFromAbsenceRequest.errors.reject('returnFromAbsenceRequest.closed.exist.error.label')
        }else {
            returnFromAbsenceRequest?.absence = absence
            returnFromAbsenceRequest?.employee = absence?.employee
            returnFromAbsenceRequest?.requestDate = ZonedDateTime.now()
            returnFromAbsenceRequest?.currentEmploymentRecord = absence?.employee?.currentEmploymentRecord
        }
        return returnFromAbsenceRequest
    }

}