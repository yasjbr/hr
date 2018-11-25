package ps.gov.epsilon.hr.firm.suspension

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
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
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.Notification
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

import java.time.temporal.ChronoUnit

import static ps.gov.epsilon.hr.firm.suspension.SuspensionRequest.*
import static ps.police.common.utils.v1.PCPSessionUtils.getValue

/**
 * <h1>Purpose</h1>
 * -this service is aims to create suspension request
 * <h1>Usage</h1>
 * -this service is used to create suspension request
 * <h1>Restriction</h1>
 * -need employee & firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionRequestService {

    MessageSource messageSource
    def formatService
    PersonService personService
    EmployeeService employeeService
    GovernorateService governorateService
    def sessionFactory
    NotificationService notificationService
    WorkFlowProcessService workFlowProcessService
    RequestService requestService

    /**
     * to return employee id
     */
    public static getEmplomeyeeId = { formatService, SuspensionRequest dataRow, object, params ->
        return dataRow?.employee?.id
    }

    /**
     * to return service action name to used it in stop for suspension request
     */
    public static getServiceActionName = { formatService, SuspensionRequest dataRow, object, params ->
        return "SUSPENSION"
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "suspensionType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonth", type: "Short", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "employeeId", type: getEmplomeyeeId, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "serviceActionReasonName", type: getServiceActionName, source: 'domain'],
            [sort: false, search: false, hidden: true, name: "canSetOrderInfo", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestStatusValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestTypeValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonth", type: "Short", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "suspensionType", type: "enum", source: 'domain'],
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
        /**
         * get firm from session
         */
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        String parentRequestId = params["parentRequestId"]
        String militaryRankId = params["militaryRank.id"]
        Short periodInMonth = params.long("periodInMonth")

        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String suspensionListEmployeeId = params["suspensionListEmployee.id"]
        ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType suspensionType = params["suspensionType"] ? ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.valueOf(params["suspensionType"]) : null
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])
        Long firmId = params.long("firm.id")



        String internalOrderNumber = params["internalOrderNumber"]
        String externalOrderNumber = params["externalOrderNumber"]
        ZonedDateTime internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        ZonedDateTime externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

        return SuspensionRequest.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {

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
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (fromDate) {
                    le("fromDate", fromDate)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (periodInMonth) {
                    eq("periodInMonth", periodInMonth)
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
                if (suspensionListEmployeeId) {
                    eq("suspensionListEmployee.id", suspensionListEmployeeId)
                }
                if (suspensionType) {
                    eq("suspensionType", suspensionType)
                }
                if (toDate) {
                    le("toDate", toDate)
                }
                //fromDate
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                if (toFromDate) {
                    lte("fromDate", toFromDate)
                }
                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    lte("toDate", toToDate)
                }

                if (militaryRankId) {
                    employee {
                        currentEmployeeMilitaryRank {
                            militaryRank {
                                eq("id", militaryRankId)
                            }
                        }
                    }
                }


                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }


                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
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
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return SuspensionRequest.
 */
    SuspensionRequest save(GrailsParameterMap params) {
        SuspensionRequest suspensionRequestInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            suspensionRequestInstance = SuspensionRequest.get(params["id"])

            if (suspensionRequestInstance) {
                if (params.long("version")) {
                    long version = params.long("version")
                    if (suspensionRequestInstance?.version > version) {
                        suspensionRequestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionRequest.label', null, 'suspensionRequest', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionRequest while you were editing")
                        return suspensionRequestInstance
                    }
                }
                /**
                 * prevent edit when status does not CREATED
                 */
                if (suspensionRequestInstance.requestStatus && suspensionRequestInstance.requestStatus != EnumRequestStatus.CREATED) {
                    suspensionRequestInstance.errors.reject('suspensionRequest.error.edit.message')
                    return suspensionRequestInstance
                }
            } else {
                suspensionRequestInstance = new SuspensionRequest()
                suspensionRequestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionRequest.label', null, 'suspensionRequest', LocaleContextHolder.getLocale())] as Object[], "This suspensionRequest with ${params.id} not found")
                return suspensionRequestInstance
            }

        } else {
            suspensionRequestInstance = new SuspensionRequest()
        }

        try {

            suspensionRequestInstance.properties = params;


            final session = sessionFactory.currentSession

            Map sqlParamsMap = [:]

            /**
             * validate there is no overlap between suspension requests & check the employee status
             * when take the suspension should be in  working
             * use 0003-03-03 03:03:03 to represent the null in the zone date time
             */
            String query = "select  " +
                    "a.request_overlap ,  b.status_overlap " +
                    "from  " +
                    "( " +
                    "SELECT  " +
                    "  (count(1)=0) as request_overlap " +
                    "FROM  " +
                    "  request,  " +
                    "  suspension_request " +
                    "WHERE  " +
                    "  suspension_request.id = request.id AND request.request_status!= :requestStatus AND  " +
                    "  request.employee_id = :employeeId AND request.status= :trackingInfoStatus AND " +
                    "  (suspension_request.from_date_datetime,(CASE WHEN suspension_request.to_date_datetime = '0003-03-03 03:03:03' THEN current_date else suspension_request.to_date_datetime end)) overlaps ( :fromDate, :toDate ) " +
                    " ) a, " +
                    " ( " +
                    "  SELECT  " +
                    "  (count(1)=0) status_overlap " +
                    "  FROM  " +
                    "  employee_status,  " +
                    "  employee_status_history " +
                    "  WHERE  " +
                    "  (employee_status_history.from_date_datetime, (CASE WHEN employee_status_history.to_date_datetime = '0003-03-03 03:03:03' THEN current_date else employee_status_history.to_date_datetime end)) overlaps ( :fromDate, :toDate ) and " +
                    "  employee_status_history.employee_status_id = employee_status.id and " +
                    "  employee_status_history.employee_id= :employeeId and " +
                    "  employee_status.id!= :employeeStatusId " +
                    "  ) b   "

            /**
             * fill map parameter
             */
            sqlParamsMap = [fromDate          : PCPUtils.convertZonedDateTimeToTimeStamp(suspensionRequestInstance?.fromDate),
                            toDate            : PCPUtils.convertZonedDateTimeToTimeStamp(suspensionRequestInstance?.toDate),
                            employeeId        : params["employee.id"],
                            employeeStatusId  : EnumEmployeeStatus.WORKING.value,
                            requestStatus     : EnumRequestStatus.REJECTED.toString(),
                            trackingInfoStatus: GeneralStatus.ACTIVE.toString()]

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
            final queryResults = sqlQuery?.list()?.get(0)

            /**
             * in case: there is over lap with another request
             */
            if (!queryResults[0] && params.action != "update") {
                suspensionRequestInstance.errors.reject("suspensionRequest.error.overlap.message")
                return suspensionRequestInstance
            }

            /**
             * in case: employee does not on work
             */
            if (!queryResults[1]) {
                suspensionRequestInstance.errors.reject("suspensionRequest.error.not.on.work.message")
                return suspensionRequestInstance
            }

            /**
             * assign currentEmploymentRecord for employee
             */
            suspensionRequestInstance.currentEmploymentRecord = suspensionRequestInstance?.employee?.currentEmploymentRecord

            /**
             * assign current currentEmployeeMilitaryRank for employee
             */
            suspensionRequestInstance.currentEmployeeMilitaryRank = suspensionRequestInstance?.employee?.currentEmployeeMilitaryRank

            /**
             * calculate the periodInMonth using from date & to date
             */
            suspensionRequestInstance.periodInMonth = ChronoUnit.MONTHS.between(PCPUtils.parseZonedDateTime(params["fromDate"])?.dateTime?.date, PCPUtils.parseZonedDateTime(params["toDate"])?.dateTime?.date)

            suspensionRequestInstance= requestService.saveManagerialOrderForRequest(params, suspensionRequestInstance)
            suspensionRequestInstance.save(failOnError: true);

            if (suspensionRequestInstance.requestStatus == EnumRequestStatus.CREATED) {
                //check if user has HR Role
                boolean hasHRRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_HR_DEPARTMENT.value)

                /**
                 * get  the workflow data
                 */
                WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                        suspensionRequestInstance?.employee?.id + "",
                        suspensionRequestInstance?.employee?.currentEmploymentRecord?.department?.id + "",
                        suspensionRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.jobCategory?.id + "",
                        suspensionRequestInstance?.employee?.currentEmploymentRecord?.jobTitle?.id + "",
                        SuspensionRequest.getName(),
                        suspensionRequestInstance?.id + "",
                        !hasHRRole)

                if (hasHRRole) {
                    workFlowProcessService.updateWorkflowPathDetails(params, workflowPathHeader)
                }
            }
        }
        catch (WorkflowNotValidActionException nve) {
            log.error("workflow user action is not valid", nve)
            transactionStatus.setRollbackOnly()
            suspensionRequestInstance.errors.reject('workflow.not.valid.action.error', [nve?.cause?.localizedMessage?.substring(0, nve?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        } catch (WorkflowNotSavedException nse) {
            log.error("Workflow not saved", nse)
            transactionStatus.setRollbackOnly()
            suspensionRequestInstance.errors.reject('default.external.server.error', [nse?.cause?.localizedMessage] as Object[], "")
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            suspensionRequestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionRequestInstance
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

            SuspensionRequest instance = SuspensionRequest.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save(failOnError: true)
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
 * @return SuspensionRequest.
 */
    @Transactional(readOnly = true)
    SuspensionRequest getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return SuspensionRequest.
     */
    @Transactional(readOnly = true)
    SuspensionRequest getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        /**
         * identify searchBean
         * identify list for PersonDTO
         * indetify list for governorateDTO
         */
        SearchBean searchBean = null
        List<PersonDTO> personDTOList = null
        List<GovernorateDTO> governorateDTOList = null

        /**
         * get list of objects without remoting values
         */
        PagedResultList suspensionRequestList = search(params)

        /**
         * assign remoting values for list of objects
         */
        if (suspensionRequestList) {

            /**
             * create new search bean
             * put list of ids of person into search bean
             * fill personDTO list using person's remoting service
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: suspensionRequestList?.resultList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * create new search bean
             * put list of ids of governorate into search bean
             * fill governorateDTO list using governorate's remoting service
             */
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: suspensionRequestList?.resultList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(governorateSearchBean)?.resultList

            /**
             * assign remoting values for each objects in list
             */
            suspensionRequestList?.each { SuspensionRequest suspensionRequest ->
                /**
                 * assign personDTO for employee
                 */
                suspensionRequest.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == suspensionRequest?.employee?.personId
                })

                /**
                 * assign  governorateDTO  for employee
                 */
                suspensionRequest?.employee?.transientData?.put("governorateDTO", governorateDTOList.find {
                    it.id == suspensionRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })
            }
            /**
             * return list of objects with remoting values
             */
            return suspensionRequestList
        }

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
     *  check employee status and validate the employee status is WORKING
     * @param GrailsParameterMap params the search map.
     * @return SuspensionRequest
     */
    public SuspensionRequest selectEmployee(GrailsParameterMap params) {
        GrailsParameterMap employeeParam

        /**
         * create new suspension request
         */
        SuspensionRequest suspensionRequest = new SuspensionRequest(params)

        /**
         * get selected employee
         */
        employeeParam = new GrailsParameterMap([id: params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Employee employee = employeeService?.getInstance(employeeParam)

        /**
         * assign employee for suspension request
         */
        if (employee) {
            suspensionRequest.employee = employee
        } else {
            suspensionRequest.errors.reject("suspensionRequest.error.employee.not.found.message")
            return suspensionRequest
        }
        return suspensionRequest
    }

    /**
     * create suspension request for selected employee
     * @param GrailsParameterMap params the search map.
     * @return suspensionRequest
     */
    public SuspensionRequest getSuspensionRequest(GrailsParameterMap params) {

        /**
         * create new suspension request
         */
        SuspensionRequest suspensionRequest = new SuspensionRequest(params)

        /**
         *  get employee then assign it  to suspension request
         */
        GrailsParameterMap employeeParam = new GrailsParameterMap(['firm.id':params['firmId'],id: params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        Employee employee = employeeService?.getInstanceWithRemotingValues(employeeParam)
        suspensionRequest.employee = employee

        /**
         * return suspension request
         */
        return suspensionRequest
    }

    /**
     * get list of suspension & extension request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return list of suspension request
     */

    private List getListOfSuspensionRequest(ZonedDateTime fromDate, ZonedDateTime toDate) {

        final session = sessionFactory.currentSession
        def list = []
        /**
         * write query to get suspension & suspension extension request that will
         * be expire within date interval
         */
        String query = " select b.parentId , b.status, max(b.to_date_datetime)  as to_date ,b.childId ,b.request_type ,b.request_status  from " +
                "( " +
                "select suspension_request.id as parentId, null as childId,suspension_request .to_date_datetime,  request.status ,request.request_type ,request.request_status " +
                "from suspension_request , request " +
                "where request.id =suspension_request.id   " +
                "union " +
                "select suspension_extension_request.suspension_request_id as parentId , suspension_extension_request.id as childId , " +
                " suspension_extension_request.to_date_datetime, request.status,request.request_type ,request.request_status  " +
                "from suspension_extension_request ,request " +
                "where  request.id = suspension_extension_request.id " +
                ")b " +
                "where b.status= 'ACTIVE' and NOT EXISTS  ( " +
                " " +
                "select object_source_id as id from notification where notification_type_id=${EnumNotificationType.MY_NOTIFICATION.toString()} " +
                ") and b.request_status='${EnumRequestStatus.APPROVED}' " +
                "group by b.parentId , b.status,b.childId,b.request_type,b.request_status " +
                "having max(b.to_date_datetime)  between :fromDate and :toDate "

        /**
         * fill map parameter
         */
        Map sqlParamsMap = [:]
        sqlParamsMap = [fromDate: PCPUtils.convertZonedDateTimeToTimeStamp(fromDate),
                        toDate  : PCPUtils.convertZonedDateTimeToTimeStamp(toDate)]

        /**
         * create query to get suspension & suspension extension request that will
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
     * get list of suspension request that does not extension/stop between date interval
     * @params from date
     * @params to date
     * @return boolean
     */
    public Boolean createNotification(ZonedDateTime fromDate, ZonedDateTime toDate) {

        /**
         * get suspension & extension request for selected date's interval
         */
        List resultList = getListOfSuspensionRequest(fromDate, toDate).toList()


        GrailsParameterMap notificationParams
        /**
         * create notification for each suspension request in list
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
                         controller        : "suspensionExtensionRequest",
                         label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                         icon              : "icon-eye",

                         notificationParams: [
                                 new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[3]}")),
                         ]
                        ]
                ]
                //params for notification text.
                messageParamList = ["${entry[3]}", "${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.dateTime?.date}"]

                // set notification role
                userTermKeyList.add(UserTerm.ROLE)
                userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

                //save notification.
                requestService?.createRequestNotification("${entry[3]}",
                        SuspensionExtensionRequest.getName(),
                        ZonedDateTime.now()?.minusDays(1),
                        null,
                        userTermKeyList,
                        userTermValueList,
                        notificationActionsMap,
                        EnumNotificationType.MY_NOTIFICATION,
                        "suspensionRequest.notification.message", messageParamList)


            } else {

                //notification actions.
                notificationActionsMap = [
                        [action            : "show",
                         controller        : "suspensionRequest",
                         label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                         icon              : "icon-eye",

                         notificationParams: [
                                 new NotificationParams(name: "encodedId", value: HashHelper.encode("${entry[0]}")),
                         ]
                        ]
                ]

                //params for notification text.
                messageParamList = ["${entry[0]}", "${PCPUtils.convertTimeStampToZonedDateTime(entry[2])?.dateTime?.date}"]

                //set notification role.
                userTermKeyList.add(UserTerm.ROLE)
                userTermValueList.add("${EnumApplicationRole.ROLE_HR_DEPARTMENT.value}")

                //save notification.
                requestService?.createRequestNotification("${entry[0]}",
                        SuspensionRequest.getName(),
                        ZonedDateTime.now()?.minusDays(1),
                        null,
                        userTermKeyList,
                        userTermValueList,
                        notificationActionsMap,
                        EnumNotificationType.MY_NOTIFICATION,
                        "suspensionRequest.notification.message",
                        messageParamList)
            }
        }
        return true
    }

}