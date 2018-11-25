package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
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

/**
 * <h1>Purpose</h1>
 * -this service aims to add and manage petition request for any disciplinary request-
 * <h1>Usage</h1>
 * -this service is used to create/update/delete and get petition request for employee-
 * <h1>Restriction</h1>
 * -need disciplinary request to be created and in APPROVED status
 * -delete in case of new only-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class PetitionRequestService {

    MessageSource messageSource
    def formatService
    DisciplinaryRequestService disciplinaryRequestService
    EmployeeService employeeService
    PersonService personService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static getDisciplinaryEncodedId = { cService, PetitionRequest rec, object, params ->
        if (rec?.disciplinaryRequest) {
            return rec?.disciplinaryRequest?.encodedId
        } else {
            return ""
        }
    }


    /**
     * this closure is used to return the current phase, which will be used in show/hide columns
     */
    public static getDisciplinaryLink = { cService, PetitionRequest rec, object, params ->
        if (rec?.disciplinaryRequest) {
            return "<a href ='../disciplinaryRequest/show?encodedId=${rec?.disciplinaryRequest?.encodedId}'>${rec?.disciplinaryRequest?.id}</a>"

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
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRequestId", type: getDisciplinaryLink, source: 'domain'],
            [sort: true, search: false, hidden: true, name: "disciplinaryEncodedId", type: getDisciplinaryEncodedId, source: 'domain'],
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
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String currentRequesterEmploymentRecordId = params["currentRequesterEmploymentRecord.id"]
        String disciplinaryRequestId = params["disciplinaryRequest.id"]
        String employeeId = params["employee.id"]
        Long firmId = params.long("firm.id")
        String parentRequestId = params["parentRequestId"]
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
        String militaryRankId = params["militaryRank.id"]
        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]

        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return PetitionRequest.createCriteria().list(max: max, offset: offset) {
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
                if (disciplinaryRequestId) {
                    eq("disciplinaryRequest.id", disciplinaryRequestId)
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
        pagedResultList?.resultList?.each { PetitionRequest petitionRequest ->
            petitionRequest?.employee = employeeList?.find { it?.id == petitionRequest?.employee?.id }
        }
        return pagedResultList
    }


    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return PetitionRequest.
     */
    PetitionRequest save(GrailsParameterMap params) {
        PetitionRequest petitionRequestInstance
        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            petitionRequestInstance = PetitionRequest.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (petitionRequestInstance.version > version) {
                    petitionRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('petitionRequest.label', null, 'petitionRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this petitionRequest while you were editing")
                    return petitionRequestInstance
                }
            }
            if (!petitionRequestInstance) {
                petitionRequestInstance = new PetitionRequest()
                petitionRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('petitionRequest.label', null, 'petitionRequest', LocaleContextHolder.getLocale())] as Object[], "This petitionRequest with ${params.id} not found")
                return petitionRequestInstance
            }
        } else {
            petitionRequestInstance = new PetitionRequest()
        }
        try {
            petitionRequestInstance.properties = params;

            //save the employee instance and current employment record
            Employee employee = petitionRequestInstance?.employee
            if (employee?.currentEmploymentRecord) {
                petitionRequestInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            //save the employee instance and current military rank in the request
            if (employee?.currentEmployeeMilitaryRank) {
                petitionRequestInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }

            //check if the original absence was not sent to any list, then move the request to approved.
            List<DisciplinaryRecordJudgment> disciplinaryRecordJudgmentList = DisciplinaryRecordJudgment.findAllByDisciplinaryRequestAndDisciplinaryRecordsListIsNotNull(petitionRequestInstance?.disciplinaryRequest)

            //if the absence was not sent to any list, we reflect the changes on original absence and close it.
            //and we close absence status directly.
            if(disciplinaryRecordJudgmentList.size()==0) {
                petitionRequestInstance.requestStatus = EnumRequestStatus.APPROVED
                petitionRequestInstance?.disciplinaryRequest?.requestStatus = EnumRequestStatus.CANCELED
                petitionRequestInstance?.disciplinaryRequest?.validate()
            }else{
                petitionRequestInstance?.disciplinaryRequest?.requestStatus = EnumRequestStatus.ADD_PETITION_REQUEST
                petitionRequestInstance?.disciplinaryRequest?.validate()
            }

            petitionRequestInstance = requestService.saveManagerialOrderForRequest(params, petitionRequestInstance)

            petitionRequestInstance.save(failOnError: true, flush: true);

            if(petitionRequestInstance.requestStatus==EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)
                // get  the workflow data
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        employee?.id + "",
                        employee?.currentEmploymentRecord?.department?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        PetitionRequest.getName(),
                        petitionRequestInstance?.id + "",
                        !hasHRRole)
                //save workflow process
                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            petitionRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            petitionRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            petitionRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return petitionRequestInstance
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
            PetitionRequest instance = PetitionRequest.get(id)
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
     * @return PetitionRequest.
     */
    @Transactional(readOnly = true)
    PetitionRequest getInstance(GrailsParameterMap params) {
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
    PetitionRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                PetitionRequest petitionRequest = results[0]
                return petitionRequest
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
    PetitionRequest getPreCreateInstance(GrailsParameterMap params) {
        PetitionRequest petitionRequest = new PetitionRequest(params)
        GrailsParameterMap disciplinaryRequestParam = new GrailsParameterMap([id: params["disciplinaryRequestId"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        DisciplinaryRequest disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(disciplinaryRequestParam)
        if (disciplinaryRequest?.requestStatus == EnumRequestStatus.CANCELED) {
            petitionRequest.errors.reject('petitionRequest.canceled.exist.error.label')
        }else if (disciplinaryRequest?.requestStatus != EnumRequestStatus.APPROVED) {
            petitionRequest.errors.reject('petitionRequest.notApproved.error.label')
        } else {
            petitionRequest?.disciplinaryRequest = disciplinaryRequest
            petitionRequest?.employee = disciplinaryRequest?.employee
            petitionRequest?.requestDate = ZonedDateTime.now()
            petitionRequest?.currentEmploymentRecord = disciplinaryRequest?.employee?.currentEmploymentRecord
        }
        return petitionRequest
    }

}