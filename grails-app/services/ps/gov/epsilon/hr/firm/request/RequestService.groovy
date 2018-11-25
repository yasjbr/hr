package ps.gov.epsilon.hr.firm.request

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.util.Environment
import grails.util.Holders
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.loan.LoanRequest
import ps.gov.epsilon.hr.firm.loan.LoanRequestRelatedPerson
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.workflow.WorkflowPathDetails
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.lookups.BorderCrossingPointService
import ps.police.pcore.v2.entity.lookups.DocumentTypeService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.security.dtos.v1.UserDTO
import ps.police.security.remotting.RemoteUserService

import java.time.ZonedDateTime

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
class RequestService {

    MessageSource messageSource
    def formatService
    PersonService personService
    EmployeeService employeeService
    RemoteUserService remoteUserService
    NotificationService notificationService
    DocumentTypeService documentTypeService
    BorderCrossingPointService borderCrossingPointService

    /**
     * return controller name by request type
     */
    public static getControllerName = { cService, Request request, object, params ->
        return request?.requestType?.domain
    }

/**
 * to control model columns when processing model operations.
 * @return List < String > .
 */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestType", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "controllerName", type: getControllerName, source: 'domain'],

    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestType", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestReason", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
    ]

    public static final List<String> DOMAIN_WORKFLOW_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "controllerName", type: getControllerName, source: 'domain'],
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
        String currentRequestId = params["currentRequest.id"]
        String currentRequesterRequestId = params["currentRequesterRequest.id"]
        String employeeId = params["employee.id"]
        Long firmId = PCPSessionUtils.getValue("firmId")
        String parentRequestId = params["parentRequestId"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        String requestReason = params["requestReason"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String requestStatusNote = params["requestStatusNote"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        List<EnumRequestType> requestTypeList = params.list("requestTypeList")
        String requesterId = params["requester.id"]
        String requesterDepartmentId = params["requesterDepartment.id"]
        String status = params["status"]
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['fromRequestDate'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['toRequestDate'])
        String militaryRankId = params["militaryRank.id"]


        return Request.createCriteria().list(max: max, offset: offset) {
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
                if (currentRequestId) {
                    eq("currentRequest.id", currentRequestId)
                }
                if (currentRequesterRequestId) {
                    eq("currentRequesterRequest.id", currentRequesterRequestId)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (parentRequestId) {
                    eq("parentRequestId", parentRequestId)
                }
                if (requestDate) {
                    le("requestDate", requestDate)
                }
                //from/to :RequestDate
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
                if (requestTypeList) {//check type in list
                    inList("requestType", requestTypeList)
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
        pagedResultList?.resultList.each { Request request ->
            request?.employee = employeeList?.find { it?.id == request?.employee?.id }
        }
        return pagedResultList
    }

/**
 * to search model entries for workflow
 * @param GrailsParameterMap params.
 * @return PagedList.
 */
    PagedList getRequestWaitingForApproval(GrailsParameterMap params) {

        /**
         * global settings
         */
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"] ?: 'desc'
        String columnName
        if (column) {
            columnName = DOMAIN_WORKFLOW_COLUMNS[column]?.name
        } else {
            columnName = "id"
            dir = "desc"
        }

        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }

        String employeeId = params["employee.id"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        String status = params["status"]
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String militaryRankId = params["militaryRank.id"]
        Map hqlMap = [:]
        String hqlQueryOrderBy = null

        /**
         * HQL query
         */
        String hqlQuery = " From Request r  " +
                " where r.requestStatus='${EnumRequestStatus.IN_PROGRESS}' " +
                " AND r.firm.id = :firmId  " +
                " AND (r.id) in ( " +
                " select wfd.workflowPathHeader.objectId from WorkflowPathDetails wfd " +
                " where wfd.toJobTitle= :toJobTitle  " +
                " AND wfd.toNode= :toNode  " +
                " AND wfd.workflowStatus in ('${EnumWorkflowStatus.WAIT_FOR_APPROVAL}','${EnumWorkflowStatus.NOT_SEEN}') )"

        /**
         * this parameter used every run of hql query.
         */
        hqlMap.put('toJobTitle', PCPSessionUtils.getValue("jobTitleId"))
        hqlMap.put('toNode', PCPSessionUtils.getValue("departmentId"))
        hqlMap.put('firmId', PCPSessionUtils.getValue("firmId"))
        hqlMap.put('offset', offset)
        hqlMap.put('max', max)

        /**
         * add the search parameters to hql query when it is exist.
         */
        if (ids) {
            hqlQuery += " AND r.id in :ids "
            hqlMap.put("ids", ids)
        }
        if (id) {
            hqlQuery += " AND r.id = :id "
            hqlMap.put("id", id)
        }

        if (employeeId) {
            hqlQuery += " AND r.employee.id = :employeeId "
            hqlMap.put("employeeId", employeeId)
        }

        if (requestType) {
            hqlQuery += " AND r.requestType = :requestType "
            hqlMap.put("requestType", requestType)
        }

        if (requestDateFrom) {
            hqlQuery += " AND r.requestDate >= :requestDateFrom "
            hqlMap.put("requestDateFrom", requestDateFrom)
        }

        if (requestDateTo) {
            hqlQuery += " AND r.requestDate <= :requestDateTo "
            hqlMap.put("requestDateTo", requestDateTo)
        }

        if (requestStatus) {
            hqlQuery += " AND r.requestStatus = :requestStatus "
            hqlMap.put("requestStatus", requestStatus)
        }
        if (militaryRankId) {
            hqlQuery += " AND r.employee.currentEmployeeMilitaryRank.militaryRank.id = :militaryRankId "
            hqlMap.put("militaryRankId", militaryRankId)
        }

        /**
         * tracking info status,
         * by default Active
         */
        if (status) {
            hqlQuery += " AND r.trackingInfo.status = :status "
            hqlMap.put("status", status)
        } else {
            hqlQuery += " AND r.trackingInfo.status = '${GeneralStatus.ACTIVE}' "
        }

        /**
         * sort by column name with direction
         */
        hqlQueryOrderBy = " order by " + columnName + " " + dir

        /**
         * get the all requests need approval
         */
        List<Request> requestList = Request.executeQuery(hqlQuery + hqlQueryOrderBy, hqlMap)

        if (requestList) {
            /**
             * get the employee remoting value
             */
            List<Long> employeeIds = requestList?.employee?.id
            GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
            List<Employee> employeeList
            employeeList = employeeService?.searchWithRemotingValues(employeesParams)

            /**
             * assign the employee with remoting value for each request
             */
            requestList?.each { Request request ->
                request?.employee = employeeList?.find { it?.id == request?.employee?.id }
            }
        }

        //get the count of records
        ArrayList<Long> countInfo = Request.executeQuery("select count(*) " + hqlQuery, hqlMap)

        /**
         * return the paged result list
         */
        return new PagedList(resultList: requestList, totalCount: countInfo?.get(0))
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return Request.
 */
    Request save(GrailsParameterMap params) {
        Request requestInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            requestInstance = Request.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (requestInstance.version > version) {
                    requestInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('request.label', null, 'request', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this request while you were editing")
                    return requestInstance
                }
            }
            if (!requestInstance) {
                requestInstance = new Request()
                requestInstance.errors.reject('default.not.found.message', [messageSource.getMessage('request.label', null, 'request', LocaleContextHolder.getLocale())] as Object[], "This request with ${params.id} not found")
                return requestInstance
            }
        } else {
            requestInstance = new Request()
        }
        try {
            requestInstance.properties = params;
            requestInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            requestInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return requestInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                Request.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                Request.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
 * @return Request.
 */
    @Transactional(readOnly = true)
    Request getInstance(GrailsParameterMap params) {
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
 * @return Request.
 */
    @Transactional(readOnly = true)
    Request getInstanceWithRemotingValues(GrailsParameterMap params) {
        SearchBean searchBean = new SearchBean()
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                Request request = results[0]
                if (Request.isInstance(BordersSecurityCoordination)) {
                    request = BordersSecurityCoordination.load(request?.id)
                    /**
                     * to get legal Identifier  name from core
                     */
                    searchBean = new SearchBean()
                    searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: bordersSecurityCoordinationList?.resultList?.legalIdentifierId))
                    request?.transientData?.put("documentTypeDTO", documentTypeService.getDocumentType(searchBean))
                    /**
                     * to get  border crossing point name from core
                     */
                    searchBean = new SearchBean()
                    searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: bordersSecurityCoordinationList?.resultList?.borderLocationId))
                    request?.transientData?.put("borderCrossingPointDTO", borderCrossingPointService.getBorderCrossingPoint(searchBean))
                } else if (Request.isInstance(LoanRequest)) {
                    LoanRequest loanRequest = (LoanRequest) request
                    searchBean = new SearchBean()
                    //collect personIds
                    List personIds = loanRequest?.loanRequestRelatedPersons?.requestedPersonId?.toList()

                    //send ids in search bean
                    searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: personIds))
                    //fill all persons info
                    List<PersonDTO> personList = personService.searchPerson(searchBean)?.resultList

                    //loop to fill all remoting values
                    loanRequest.loanRequestRelatedPersons.each { LoanRequestRelatedPerson loanRequestRelatedPerson ->

                        //fill all person info
                        if (loanRequestRelatedPerson?.requestedPersonId) {
                            loanRequestRelatedPerson.transientData.requestedPersonDTO = personList.find {
                                it.id == loanRequestRelatedPerson?.requestedPersonId
                            }
                        }
                    }
                }
                if (request?.employee) {
                    searchBean = new SearchBean()
                    searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: request?.employee?.personId))
                    PersonDTO personDTO = personService.getPerson(searchBean)
                    request.employee.transientData.put("personDTO", personDTO)
                }
                return request
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
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * Get the workflow path header that is related to request
     * @param params
     * @return workflowPathHeader
     */
    public WorkflowPathHeader getWorkflowPathHeader(GrailsParameterMap params) {
        /**
         * get workflow path header by object id
         */
        WorkflowPathHeader workflowPathHeader = WorkflowPathHeader.findByObjectId(params.objectId)
        /**
         * get list of job title by list of id
         */
        List<JobTitle> jobTitleList = JobTitle.findAllByIdInList(workflowPathHeader?.workflowPathDetails?.toJobTitle?.toList())

        /**
         * get person dto remoting by id
         */
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: workflowPathHeader?.workflowPathDetails?.processedBy?.unique()))
        List<PersonDTO> personDTOList = personService.searchPerson(searchBean)?.resultList

        /**
         * assign  job title name for each workflow path details
         */
        workflowPathHeader?.workflowPathDetails?.each { WorkflowPathDetails workflowPathDetails ->

            workflowPathDetails?.transientData?.put("toJobTitleName", jobTitleList.find {
                it.id == workflowPathDetails?.toJobTitle
            })

            if (workflowPathDetails?.processedBy) {
                workflowPathDetails?.transientData?.put("personDTO", personDTOList?.find {
                    it.id == Long.parseLong(workflowPathDetails?.processedBy)
                })
            }
        }

        /**
         * return workflow path header
         */
        return workflowPathHeader
    }

    /**
     * Create a dynamic notification for request
     * @param objectSourceId
     * @param objectSourceReference
     * @param notificationDate
     * @param requestStatus
     * @param userTermKeyList
     * @param userTermValueList
     * @param notificationActionsMap
     * @param notificationType
     * @param notificationTextPrefix
     */
    public void createRequestNotification(String objectSourceId,
                                          String objectSourceReference,
                                          ZonedDateTime notificationDate,
                                          EnumRequestStatus requestStatus,
                                          List<UserTerm> userTermKeyList = null,
                                          List<String> userTermValueList = null,
                                          List<Map> notificationActionsList = null,
                                          EnumNotificationType notificationType = EnumNotificationType.MY_NOTIFICATION,
                                          String notificationTextCode = '',
                                          List<String> messageParamList = [],
                                          Map notificationMap = [:]) {
        GrailsParameterMap notificationParams
        Map notificationTermsMap
        Map notificationKeys
        Map notificationValues

        Boolean developmentModeWithServiceCatalog = Holders.grailsApplication.config.grails.developmentModeWithServiceCatalog

        try {

            /**
             *
             */
            String controllerName = extractControllerName(objectSourceReference)
            Request request = Request.read(objectSourceId)


            notificationParams = new GrailsParameterMap([:], null)

            //fill notification params and save notification
            notificationParams["objectSourceId"] = objectSourceId
            notificationParams.objectSourceReference = objectSourceReference
            notificationParams.title = "${messageSource.getMessage("${controllerName}" + ".label", [] as Object[], new Locale("ar"))}"
            notificationParams.notificationDate = notificationDate
            notificationParams["notificationType"] = NotificationType.read(notificationType.value)



            notificationTermsMap = [:]
            notificationKeys = [:]
            notificationValues = [:]


            SearchBean searchBean
            UserDTO userDTO = null
            userTermKeyList?.eachWithIndex { key, index ->
                switch (key) {
                    case UserTerm.USER:
                        searchBean = new SearchBean()
                        searchBean.searchCriteria.put("personId", new SearchConditionCriteriaBean(operand: 'personId', value1: userTermValueList?.get(index)))
                        searchBean.searchCriteria.put("firmId", new SearchConditionCriteriaBean(operand: 'firmId', value1: request?.firm?.id))
                        if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {
                            userDTO = remoteUserService.getUser(searchBean)
                            notificationKeys.put(new Integer(index + 1), UserTerm.USER.value())
                            notificationValues.put(new Integer(index + 1), "${userDTO?.username}")
                        } else if (Environment.current == Environment.DEVELOPMENT) {
                            notificationKeys.put(new Integer(index + 1), UserTerm.USER.value())
                            notificationValues.put(new Integer(index + 1), "admin")
                        }
                        break
                    default:
                        notificationKeys.put(new Integer(index + 1), userTermKeyList?.get(index)?.value())
                        notificationValues.put(new Integer(index + 1), "${userTermValueList?.get(index)}")
                        break
                }
            }

            // set firm term by default
            notificationKeys.put(notificationKeys?.size() + 1, UserTerm.FIRM.value())
            notificationValues.put(notificationKeys?.size(), "${request?.firm?.id}")

            if (notificationMap["withEmployeeName"]) {
                if (!userDTO) {
                    searchBean = new SearchBean()
                    searchBean.searchCriteria.put("personId", new SearchConditionCriteriaBean(operand: 'personId', value1: request?.employee?.personId))
                    searchBean.searchCriteria.put("firmId", new SearchConditionCriteriaBean(operand: 'firmId', value1: request?.firm?.id))
                    if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {
                        userDTO = remoteUserService.getUser(searchBean)
                        messageParamList.push(userDTO?.personName)
                    } else if (Environment.current == Environment.DEVELOPMENT) {
                        messageParamList.push("admin")
                    }
                }
            }
            if (notificationTextCode) {
                notificationParams.text = "${messageSource.getMessage("${notificationTextCode}", messageParamList as Object[], new Locale("ar"))}"
            } else {
                if (requestStatus == EnumRequestStatus.REJECTED) {
                    notificationParams.text = "${messageSource.getMessage("request.notification.rejectRequest.message", ["${objectSourceId}"] as Object[], new Locale("ar"))}"
                } else if (requestStatus == EnumRequestStatus.APPROVED) {
                    notificationParams.text = "${messageSource.getMessage("request.notification.approveRequest.message", ["${objectSourceId}"] as Object[], new Locale("ar"))}"
                } else if (requestStatus == EnumRequestStatus.APPROVED_BY_WORKFLOW) {
                    notificationParams.text = "${messageSource.getMessage("request.notification.approveRequest.message", ["${objectSourceId}"] as Object[], new Locale("ar"))}"
                }
            }

            notificationTermsMap.put("key", notificationKeys)
            notificationTermsMap.put("value", notificationValues)
            notificationParams["notificationTerms"] = notificationTermsMap
            notificationParams["notificationActions"] = notificationActionsList

            //save notification
            notificationService?.save(notificationParams)
        } catch (Exception ex) {
            ex.printStackTrace()
            throw new Exception("error create notification for request ", ex)
        }
    }

    /**
     * extracts controller name from domain path
     * @param domainName
     * @return controller name
     */
    public String extractControllerName(String domainName) {
        // Extract controller name from objectSourceReference
        String controllerName = domainName.substring(domainName.lastIndexOf('.') + 1)
        controllerName = "${Character.toLowerCase(controllerName.charAt(0))}" + controllerName.substring(1)
        return controllerName

    }

    /**
     * lists all requests into one thread ordered by date
     * @param criteria
     * @param params
     * @return
     */
    public PagedResultList getThreadWithRemotingValues(DetachedCriteria criteria, GrailsParameterMap params) {
        String threadId = params.threadId
        Integer max = params.int('max') ?: Integer.MAX_VALUE
        Integer offset = params.int('offset') ?: 0

        return criteria.list(max: max, offset: offset) {
            eq('threadId', threadId)
            order('trackingInfo.dateCreatedUTC', 'desc')
        }
    }

    public int countChildRequests(String parentRequestId, List excludedStatusList) {
        int count = Request.createCriteria().get {
            eq('parentRequestId', parentRequestId)
            not { inList('requestStatus', excludedStatusList) }
            projections {
                count('id')
            }
        }
        return count
    }

    PagedList searchCanHaveOperation(GrailsParameterMap params) {
        String employeeId = params.employeeId
        EnumRequestCategory requestCategory = params.requestCategory

        EnumRequestType originalRequestType = EnumRequestType.valueOf(params.ORIGINAL)
        EnumRequestType editRequestType = params.EDIT ? EnumRequestType.valueOf(params.EDIT) : null
        EnumRequestType cancelRequestType = params.CANCEL ? EnumRequestType.valueOf(params.CANCEL) : null
        EnumRequestType stopRequestType = params.STOP ? EnumRequestType.valueOf(params.STOP) : null
        EnumRequestType extendRequestType = params.EXTEND ? EnumRequestType.valueOf(params.EXTEND) : null

        StringBuilder sbCountQuery = new StringBuilder("select count(r.id) ")
        StringBuilder sbSelectQuery = new StringBuilder("select r  ")
        StringBuilder sbFromQuery = new StringBuilder(" from ")
        sbFromQuery << params['domainName']
        sbFromQuery << " r where 1=1 "

        StringBuilder sbWhereStatement = new StringBuilder(params.sbWhereStatement?.toString())
        Map queryParams = params.queryParams

        sbWhereStatement << " and r.requestStatus = :approvedRequestStatus"
        queryParams['approvedRequestStatus'] = EnumRequestStatus.APPROVED

        if (employeeId) {
            sbWhereStatement << " and r.employee.id = :employeeId"
            queryParams['employeeId'] = employeeId
        }

        List requestTypes = [originalRequestType]
        if (cancelRequestType && requestCategory == EnumRequestCategory.CANCEL) {
            if (editRequestType) requestTypes << editRequestType;
            if (stopRequestType) requestTypes << stopRequestType;
            if (extendRequestType) requestTypes << extendRequestType;
        } else if (editRequestType && requestCategory == EnumRequestCategory.EDIT) {
            requestTypes << editRequestType;
        } else if (stopRequestType && requestCategory == EnumRequestCategory.STOP) {
            if (editRequestType) requestTypes << editRequestType;
            if (extendRequestType) requestTypes << extendRequestType;
        } else if (extendRequestType && requestCategory == EnumRequestCategory.EXTEND) {
            requestTypes << extendRequestType;
            if (editRequestType) requestTypes << editRequestType;
        }

        sbWhereStatement << " and r.requestType in (:requestTypes)"
        queryParams['requestTypes'] = requestTypes

        // include only requests that are not already handled
        sbWhereStatement << " and not exists (select cr.id from "
        sbWhereStatement << params['domainName']
        sbWhereStatement << " cr where cr.parentRequestId= r.id  "
        sbWhereStatement << " and cr.requestStatus not in (:excludedStatusList)) "
        queryParams['excludedStatusList'] = [EnumRequestStatus.REJECTED, EnumRequestStatus.CANCELED]

        PagedList<Request> pagedList = new PagedList<Request>()

        log.info("count query = " + sbCountQuery.toString() + sbFromQuery.toString() + sbWhereStatement.toString())
        pagedList.totalCount = Request.executeQuery(sbCountQuery.toString() + sbFromQuery.toString() + sbWhereStatement.toString(), queryParams)?.get(0) as Integer
        log.info("count result = " + pagedList.totalCount)

        if (pagedList.totalCount > 0) {
            log.info("select query = " + sbSelectQuery.toString() + sbFromQuery.toString() + sbWhereStatement.toString())
            queryParams['max'] = params.int('max') ?: Integer.MAX_VALUE
            queryParams['offset'] = params.int('offset') ?: 0
            pagedList.resultList = Request.executeQuery(sbSelectQuery.toString() + sbFromQuery.toString() + sbWhereStatement.toString(), queryParams)
        }

        return pagedList
    }

    /**
     * saves managerial info for a request
     * @param params
     * @return
     */
    Request saveManagerialOrderInfo(GrailsParameterMap params) {
        Request requestInstance
        try {
            requestInstance = getInstance(params)
            if (requestInstance.internalOrderNumber && params.internalOrderNumber) {
                requestInstance.errors.rejectValue('internalOrderNumber', 'request.orderNumber.already.exists.error.message')
                throw new ValidationException("invalid", requestInstance.errors)
            }
            if (requestInstance.externalOrderNumber && params.externalOrderNumber) {
                requestInstance.errors.rejectValue('externalOrderNumber', 'request.orderNumber.already.exists.error.message')
                throw new ValidationException("invalid", requestInstance.errors)
            }
            if ((!params.internalOrderNumber || !params.internalOrderDate) && (!params.externalOrderNumber || !params.externalOrderDate)) {
                requestInstance.errors.reject('default.validation.required.label')
                throw new ValidationException("invalid", requestInstance.errors)
            }
            requestInstance = saveManagerialOrderForRequest(params, requestInstance)

            //in case the status of request is changed when set the order info.
            ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatusValue = params["requestStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatusValue"]) : null
            if (requestStatusValue) {
                requestInstance?.requestStatus = requestStatusValue
            }

            requestInstance.save(failOnError: true)
        } catch (ValidationException vex) {
            log.error("Failed to set order number due to validation errors : ${ValidationException.formatErrors(vex.errors)}")
            if (!requestInstance?.hasErrors()) {
                requestInstance.errors.addAllErrors(vex.errors)
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            log.error("Failed to set order number due to un expected error ", ex)
            requestInstance.errors.reject('default.internal.server.error', [ex.message] as Object[], "")
        }
        return requestInstance
    }

    /**
     * saves managerial info for a request
     * @param params
     * @return
     */
    Request saveManagerialOrderForRequest(GrailsParameterMap params, Request requestInstance) {


        if (!requestInstance.requestStatus) {
            requestInstance.requestStatus = EnumRequestStatus.CREATED
        }

        if (params?.internalOrderNumber) {
            requestInstance.internalOrderNumber = params?.internalOrderNumber
            requestInstance.internalOrderDate = PCPUtils.parseZonedDateTime(params['internalOrderDate'])
        } else if (!requestInstance.internalOrderNumber) {
            requestInstance.internalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
        }
        if (params?.externalOrderNumber) {
            requestInstance.externalOrderNumber = params.externalOrderNumber
            requestInstance.externalOrderDate = PCPUtils.parseZonedDateTime(params['externalOrderDate'])

            //TODO make sure this rule is valid, since a request might be created with rejection external order number
            if (params.requestStatus) {
                EnumRequestStatus requestStatus = EnumRequestStatus.valueOf(params.requestStatus)
                if (requestStatus in [EnumRequestStatus.APPROVED, EnumRequestStatus.REJECTED]) {
                    requestInstance.requestStatus = requestStatus
                } else {
                    throw new Exception("invalid status " + requestStatus?.name())
                }
            } else {
                requestInstance.requestStatus = EnumRequestStatus.APPROVED
            }
        } else if (!requestInstance.externalOrderNumber) {
            requestInstance.externalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
        }

        return requestInstance
    }
}