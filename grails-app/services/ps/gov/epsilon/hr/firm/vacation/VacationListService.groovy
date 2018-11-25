package ps.gov.epsilon.hr.firm.vacation

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumApplicationRole
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.request.RequestChangesHandlerService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm
import ps.police.security.dtos.v1.UserDTO
import ps.police.security.remotting.RemoteUserService

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create vacation list
 * <h1>Usage</h1>
 * -this service is used to create vacation list
 * <h1>Restriction</h1>
 * -need a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class VacationListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    VacationRequestService vacationRequestService
    VacationListEmployeeService vacationListEmployeeService
    EmployeeVacationBalanceService employeeVacationBalanceService
    NotificationService notificationService
    RemoteUserService remoteUserService
    RequestService requestService
    RequestChangesHandlerService requestChangesHandlerService

    //to get the value of requisition status
    public static currentStatusValue = { cService, VacationList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, VacationList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../vacationList/manageVacationList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "code", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: getListName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trackingInfo.dateCreatedUTC", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.sendDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualOutgoingNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.receiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualIncomeNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.numberOfCompetitorsValue", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentStatus.correspondenceListStatus", type: "enum", source: 'domain'],
            [sort: false, search: true, hidden: true, name: "currentStatusValue", type: currentStatusValue, source: 'domain'],
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
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        Long firmId = params.long("firm.id")
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
        Set vacationListEmployeesIds = params.listString("vacationListEmployees.id")

        return VacationList.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("code", sSearch)
                    ilike("manualIncomeNo", sSearch)
                    ilike("manualOutgoingNo", sSearch)
                    ilike("name", sSearch)
                    ilike("orderNo", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (code) {
                    ilike("code", "%${code}%")
                }
                if (correspondenceListStatusesIds) {
                    correspondenceListStatuses {
                        inList("id", correspondenceListStatusesIds)
                    }
                }
                if (currentStatusId) {
                    eq("currentStatus.id", currentStatusId)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (manualIncomeNo) {
                    ilike("manualIncomeNo", "%${manualIncomeNo}%")
                }
                if (manualOutgoingNo) {
                    ilike("manualOutgoingNo", "%${manualOutgoingNo}%")
                }
                if (name) {
                    ilike("name", "%${name}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (receivingParty) {
                    eq("receivingParty", receivingParty)
                }
                if (vacationListEmployeesIds) {
                    vacationListEmployees {
                        inList("id", vacationListEmployeesIds)
                    }
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
     * custom search to find the number of requests in the vacation  list in one select statement for performance issue
     * this solution was suggested and approved by Mureed
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    PagedList customSearch(GrailsParameterMap params) {


        final session = sessionFactory.currentSession

        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        String orderByQuery = ""
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))

        String id = params["id"]
        String code = params["code"]
        String name = params["name"]
        Long firmId = PCPSessionUtils.getValue("firmId")
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus correspondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"]) : null
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        Integer numberOfCompetitorsValue = params.int("numberOfCompetitorsValue")
        String trackingInfoDateCreatedUTC = params['trackingInfo.dateCreatedUTC']
        String dateCreated = params['dateCreated']

        ZonedDateTime fromSendDate = PCPUtils.parseZonedDateTime(params['sendDateFrom'])
        ZonedDateTime toSendDate = PCPUtils.parseZonedDateTime(params['sendDateTo'])

        ZonedDateTime fromReceiveDate = PCPUtils.parseZonedDateTime(params['receiveDateFrom'])
        ZonedDateTime toReceiveDate = PCPUtils.parseZonedDateTime(params['receiveDateTo'])

        ZonedDateTime fromDateCreated = PCPUtils.parseZonedDateTime(params['dateCreatedFrom'])
        ZonedDateTime toDateCreated = PCPUtils.parseZonedDateTime(params['dateCreatedTo'])


        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of vacations in the list, the send date, and the current list status
        String query = "FROM vacation_list al  LEFT JOIN " +
                "  (SELECT ale.vacation_list_id ,count(ale.id) no_of_employee" +
                "  FROM vacation_list_employee ale " +
                "  group by ale.vacation_list_id ) b" +
                "  on al.id= b.vacation_list_id , correspondence_list_status cls,correspondence_list cl" +
                " LEFT JOIN (SELECT * FROM correspondence_list_status  sb WHERE  sb.correspondence_list_status='${EnumCorrespondenceListStatus.SUBMITTED}') sbl" +
                " ON  sbl.correspondence_List_id=cl.id" +
                " LEFT JOIN (SELECT * FROM correspondence_list_status  sd WHERE  sd.correspondence_list_status='${EnumCorrespondenceListStatus.RECEIVED}') sdl" +
                " ON  sdl.correspondence_List_id=cl.id" +
                "  where cl.id= al.id and cls.id = cl.current_status_id and cl.status ='${GeneralStatus.ACTIVE}' "

        //if statements to check the params
        if (sSearch) {
            query = query + " and ( code like :codeSParam or " +
                    "manual_income_no like :manualIncomeNoSParam or " +
                    "manual_outgoing_no like :manualOutgoingNoSParam or " +
                    "name like :nameSParam ) "
            sqlParamsMap.put("codeSParam", "%" + sSearch + "%")
            sqlParamsMap.put("manualIncomeNoSParam", "%" + sSearch + "%")
            sqlParamsMap.put("manualOutgoingNoSParam", "%" + sSearch + "%")
            sqlParamsMap.put("nameSParam", "%" + sSearch + "%")
        }

        if (id) {
            query = query + " and al.id = :idParam  "
            sqlParamsMap.put("idParam", id)
        }
        if (firmId) {
            query = query + " and cl.firm_id = :firmIdParam  "
            sqlParamsMap.put("firmIdParam", firmId)
        }
        if (code) {
            query = query + " and code like :codeParam  "
            sqlParamsMap.put("codeParam", "%" + code + "%")
        }
        if (name) {
            query = query + " and name like :nameParam  "
            sqlParamsMap.put("nameParam", "%" + name + "%")
        }
        if (correspondenceListStatus) {
            query = query + " and cls.correspondence_list_status = :correspondenceListStatusParam  "
            sqlParamsMap.put("correspondenceListStatusParam", correspondenceListStatus.toString())
        }
        if (manualIncomeNo) {
            query = query + " and manual_income_no like :manualIncomeNoParam  "
            sqlParamsMap.put("manualIncomeNoParam", "%" + manualIncomeNo + "%")
        }
        if (manualOutgoingNo) {
            query = query + " and manual_outgoing_no like :manualOutgoingNoParam  "
            sqlParamsMap.put("manualOutgoingNoParam", "%" + manualOutgoingNo + "%")
        }
        if (numberOfCompetitorsValue != null) {
            query = query + " and COALESCE(b.no_of_employee,0) = :numberOfCompetitorsValue  "
            sqlParamsMap.put("numberOfCompetitorsValue", numberOfCompetitorsValue)
        }
        if (trackingInfoDateCreatedUTC) {
            query = query + " and to_char(cl.date_created,'dd/MM/yyyy')  = :trackingInfoDateCreatedUTC "
            sqlParamsMap.put("trackingInfoDateCreatedUTC", trackingInfoDateCreatedUTC)
        }

        //check 3 cases of date created > = <
        if (fromDateCreated) {
            query = query + " and cl.date_created >= :fromDateCreated "
            sqlParamsMap.put("fromDateCreated", Date.from(fromDateCreated.toInstant()))
        }
        if (toDateCreated) {
            query = query + " and cl.date_created <= :toDateCreated "
            sqlParamsMap.put("toDateCreated", Date.from(toDateCreated.toInstant()))
        }

        //check 3 cases of send date created > = <
        if (fromSendDate) {
            query = query + " and sbl.from_date_datetime >= :fromSendDate "
            sqlParamsMap.put("fromSendDate", Date.from(fromSendDate.toInstant()))
        }
        if (toSendDate) {
            query = query + " and sbl.from_date_datetime <= :toSendDate "
            sqlParamsMap.put("toSendDate", Date.from(toSendDate.toInstant()))
        }

        //check 3 cases of receive date created > = <
        if (fromReceiveDate) {
            query = query + " and sdl.from_date_datetime >= :fromReceiveDate "
            sqlParamsMap.put("fromReceiveDate", Date.from(fromReceiveDate.toInstant()))
        }
        if (toReceiveDate) {
            query = query + " and sdl.from_date_datetime <= :toReceiveDate "
            sqlParamsMap.put("toReceiveDate", Date.from(toReceiveDate.toInstant()))
        }

        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("trackingInfo.dateCreatedUTC")) {
            orderByQuery += "ORDER BY cl.date_created ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.sendDate")) {
            orderByQuery += "ORDER BY sbl.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.receiveDate")) {
            orderByQuery += "ORDER BY sdl.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("fromDate")) {
            orderByQuery += "ORDER BY cls.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("manualInComeNo")) {
            orderByQuery += "ORDER BY cl.manual_income_no  ${dir}"
        } else if (columnName?.equalsIgnoreCase("toDate")) {
            orderByQuery += "ORDER BY cls.to_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("manualOutgoingNo")) {
            orderByQuery += "ORDER BY cl.manual_outgoing_no  ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.numberOfCompetitorsValue")) {
            orderByQuery += "ORDER BY b.no_of_employee  ${dir}"
        } else if (columnName?.equalsIgnoreCase("currentStatus.correspondenceListStatus")) {
            orderByQuery += "ORDER BY cls.correspondence_list_status  ${dir}"
        } else if (columnName) {
            orderByQuery += "ORDER BY ${columnName} ${dir}"
        } else {
            orderByQuery += "ORDER BY cl.date_created desc"
        }

        Query sqlQuery = session.createSQLQuery(
                """
                SELECT
                    al.id ,
                    cl.code,
                    cl.name,
                    cl.date_created,
                    cls.to_date_datetime,
                    cls.from_date_datetime,
                    sbl.from_date_datetime as send_date,
                    sdl.from_date_datetime as receive_date,
                    cl.manual_outgoing_no,
                    cl.manual_income_no,
                    cls.correspondence_list_status,
                    cl.current_status_id,
                    COALESCE(no_of_employee,0) as no_of_employee,
                    cl.receiving_party,
                    cl.cover_letter,
                    cl.last_updated
              """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //pagination parameters
        sqlQuery.setFetchSize(max)
        sqlQuery.setFirstResult(offset)


        final queryResults = sqlQuery.list()

        List<VacationList> results = []
        // Transform resulting rows to a map with key organisationName.
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy")

        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[3])?.getTime())?.toInstant(), ZoneId.systemDefault())
            ZonedDateTime lastUpdatedUTC = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[15])?.getTime())?.toInstant(), ZoneId.systemDefault())
            ZonedDateTime toDateZonedDateTime = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[4])?.getTime())?.toInstant(), ZoneId.systemDefault())
            ZonedDateTime fromDateZonedDateTime = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[5])?.getTime())?.toInstant(), ZoneId.systemDefault())
            ZonedDateTime sendDateZonedDateTime
            ZonedDateTime receiveDateZonedDateTime
            if (resultRow[6]) {
                sendDateZonedDateTime = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[6])?.getTime())?.toInstant(), ZoneId.systemDefault())
            }
            if (resultRow[7]) {
                receiveDateZonedDateTime = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[7])?.getTime())?.toInstant(), ZoneId.systemDefault())
            }

            VacationList vacationList = new VacationList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[14],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate                : sendDateZonedDateTime,
                                    receiveDate             : receiveDateZonedDateTime,
                                    numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            vacationList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            vacationList.currentStatus = currentStatus
            results.add(vacationList)
        }

        Integer totalCount = 0

        //get total count for all records if we have records (results!=null)
        if (results) {
            Query sqlCountQuery = session.createSQLQuery(""" SELECT count(al.id) """ + query)
            sqlParamsMap?.each {
                sqlCountQuery.setParameter(it.key.toString(), it.value)
            }
            final queryCountResults = sqlCountQuery.list()
            totalCount = new Integer(queryCountResults[0]?.toString())
        }

        //return the paged list result
        return new PagedList(resultList: results, totalCount: totalCount)
    }

    /**
     * to search model entries with remoting values.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedList = customSearch(params)
        if (pagedList) {
            return pagedList
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return VacationList.
     */
    VacationList save(GrailsParameterMap params) {
        VacationList vacationListInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            vacationListInstance = VacationList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (vacationListInstance.version > version) {
                    vacationListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('vacationList.label', null, 'vacationList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this vacationList while you were editing")
                    return vacationListInstance
                }
            }
            if (!vacationListInstance) {
                vacationListInstance = new VacationList()
                vacationListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacationList.label', null, 'vacationList', LocaleContextHolder.getLocale())] as Object[], "This vacationList with ${params.id} not found")
                return vacationListInstance
            }
        } else {
            vacationListInstance = new VacationList()
        }
        try {
            vacationListInstance.properties = params;

            /**
             * in CREATED phase: create new status for the list
             */
            CorrespondenceListStatus correspondenceListStatus
            EnumCorrespondenceListStatus listStatus = params.correspondenceListStatus ? EnumCorrespondenceListStatus.valueOf(params.correspondenceListStatus) : null
            if (!params.id && !listStatus) {
                //when create the list, its is CREATED phase:
                listStatus = EnumCorrespondenceListStatus.CREATED
            }
            if (listStatus) {
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = ZonedDateTime.now()
                correspondenceListStatus.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                correspondenceListStatus.correspondenceListStatus = listStatus
                correspondenceListStatus.receivingParty = EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = vacationListInstance.firm
                correspondenceListStatus.correspondenceList = vacationListInstance
                vacationListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            vacationListInstance.save(flush: true, failOnError: true);

            //save the current status:
            if (correspondenceListStatus?.id && vacationListInstance?.id) {
                vacationListInstance?.currentStatus = correspondenceListStatus
                vacationListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            vacationListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return vacationListInstance
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

            VacationList instance = VacationList.get(id)
            //to be able to delete an vacation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.vacationListEmployees?.size() == 0) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo?.status = GeneralStatus.DELETED
                    instance.save(failOnError: true)
                    deleteBean.status = true
                }
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('vacationList.deleteMessage.label')
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
     * @return VacationList.
     */
    @Transactional(readOnly = true)
    VacationList getInstance(GrailsParameterMap params) {
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
     * to add vacationRequests to vacation list
     * @param GrailsParameterMap params
     * @return VacationList
     */
    VacationList addVacationRequests(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        /**
         *  get list of ids of selected vacation request
         */
        List checkedEncodedVacationRequestIdList = params.listString("checked_requestIdsList")

        VacationList vacationList = null
        GrailsParameterMap vacationRequestMap
        VacationRequest vacationRequest = null
        List<VacationRequest> vacationRequestList
        VacationListEmployee vacationListEmployee

        /**
         * get vacation list
         */
        vacationList = VacationList.load(params["id"])


        if (checkedEncodedVacationRequestIdList) {

            /**
             * get selected vacation request
             */
            vacationRequestMap = new GrailsParameterMap(["ids[]": checkedEncodedVacationRequestIdList], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            vacationRequestList = vacationRequestService?.search(vacationRequestMap)

            /**
             * add all selected vacation request to vacation list
             */
            checkedEncodedVacationRequestIdList?.each { String id ->

                /**
                 * change vacation request status to ADD_TO_LIST
                 */
                vacationRequest = vacationRequestList?.find { it.id == id }
                if (vacationRequest) {
                    vacationRequest?.requestStatus = EnumRequestStatus.ADD_TO_LIST

                    /**
                     * add vacation request to vacation list employee
                     */
                    vacationListEmployee = new VacationListEmployee()
                    vacationListEmployee?.vacationRequest = vacationRequest
                    vacationListEmployee?.vacationRequest.validate()
                    vacationListEmployee?.recordStatus = EnumListRecordStatus.NEW
                    vacationListEmployee?.vacationList = vacationList
                    vacationListEmployee?.currentEmploymentRecord = vacationRequest?.employee?.currentEmploymentRecord
                    vacationListEmployee?.currentEmployeeMilitaryRank = vacationRequest?.employee?.currentEmployeeMilitaryRank

                    if (vacationRequest?.requestStatusNote) {
                        VacationListEmployeeNote note = new VacationListEmployeeNote(
                                noteDate: vacationRequest?.requestDate,
                                note: vacationRequest?.requestStatusNote,
                                orderNo: "",
                                vacationListEmployee: vacationListEmployee,
                        );
                        if (!note?.validate()) {
                            vacationList.errors.addAllErrors(note?.errors)
                            throw new Exception("Error occurred while add note to the list employee record.")
                        } else {
                            //add note to join
                            vacationListEmployee.addToVacationListEmployeeNotes(note);
                        }
                    }

                    /**
                     * add the vacation list employee to vacation list
                     */
                    vacationList?.addToVacationListEmployees(vacationListEmployee)
                }
            }

            try {
                vacationList?.save(failOnError: true)
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                if (vacationList?.hasErrors()) {
                    vacationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        } else {
            vacationList.errors.reject('vacationList.error.not.selected.request.message')
            return vacationList
        }
        return vacationList
    }

    /**
     * send vacation list
     * @param GrailsParameterMap params
     * @return VacationList
     */
    VacationList sendData(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corsponding list:
            VacationList vacationList = VacationList.get(params["id"])
            //return error if no request added to list
            if (vacationList?.vacationListEmployees?.size() == 0) {
                vacationList.errors.reject("list.sendList.error")
                return vacationList
            }
            if (vacationList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = vacationList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = vacationList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        vacationList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    vacationList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all vacationListEmployee in marital status list to change the status of the request
                    vacationList?.vacationListEmployees.each { VacationListEmployee vacationListEmployee ->
                        //vacationListEmployee?.vacationRequest?.vacationListEmployee = vacationListEmployee
                        vacationListEmployee?.vacationRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        vacationListEmployee?.vacationRequest?.validate()
                    }
                    //save the disciplinary list changes
                    vacationList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (vacationList?.errors?.allErrors?.size() == 0) {
                        vacationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return vacationList
            }
        }
    }

    /**
     * receive vacation list
     * @param GrailsParameterMap params
     * @return VacationList
     */
    VacationList receiveData(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        CorrespondenceListStatus correspondenceListStatus
        VacationList vacationList = null
        if (params.id) {

            /**
             * get vacation list
             */
            vacationList = VacationList.get(params["id"])

            if (VacationList) {
                /**
                 * change vacation list status to RECEIVED
                 */
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.correspondenceList = vacationList
                correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = vacationList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                if (correspondenceListStatus.save()) {
                    vacationList.currentStatus = correspondenceListStatus
                }
                /**
                 * assign manualIncomeNo when we send the list
                 */
                vacationList.manualIncomeNo = params.manualIncomeNo

                try {
                    vacationList.save(failOnError: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (vacationList?.hasErrors()) {
                        vacationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        }
        return vacationList
    }

    /**
     * to change the vacation List employee status to EMPLOYED in the receive vacation list
     * @param GrailsParameterMap params
     * @return VacationList
     */
    VacationList approveVacationRequest(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        /**
         * to get list of  ids of vacation request
         */
        List vacationListEmployeeIds = params.listString("check_vacationRequestTableInVacationList")
        params.remove("check_vacationRequestTableInVacationList")
        VacationRequest request
        VacationListEmployee vacationListEmployee
        List<VacationListEmployee> vacationListEmployeeList
        VacationList vacationList = null
        /**
         * get selected vacation list employee
         */
        GrailsParameterMap vacationListEmployeeParam = new GrailsParameterMap(["ids[]": vacationListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        vacationListEmployeeList = vacationListEmployeeService?.search(vacationListEmployeeParam)

        /**
         * get vacationList
         */
        vacationList = VacationList.get(params["id"])



        if (vacationListEmployeeIds) {


            vacationListEmployeeIds?.each { String id ->
                /**
                 * get vacation list employee by id
                 */
                vacationListEmployee = vacationListEmployeeList?.find { it?.id == id }
                vacationList = vacationListEmployee?.vacationList
                request = vacationListEmployee?.vacationRequest

                /**
                 * change request status to approved
                 */
//                request?.requestStatus = ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED

                /**
                 * change vacation list employee status
                 */
                vacationListEmployee?.recordStatus = EnumListRecordStatus.APPROVED

                /**
                 * note is required in reject.
                 */
                if (params.note || params.orderNo) {
                    vacationListEmployee?.addToVacationListEmployeeNotes(new VacationListEmployeeNote(vacationListEmployee: vacationListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                }
            }
            try {

                vacationList.save(failOnError: true)
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                if (vacationList?.hasErrors()) {
                    vacationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        } else {
            vacationList.errors.reject('vacationList.error.not.selected.request.message')
            return vacationList
        }
        return vacationList
    }

    /**
     * to change the vacation List employee status to NOT_EMPLOYED in the receive vacation list
     * @param GrailsParameterMap params
     * @return VacationList
     */
    VacationList changeVacationRequestToRejected(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        VacationRequest request
        VacationListEmployee vacationListEmployee
        List<VacationListEmployee> vacationListEmployeeList
        VacationList vacationList = null

        /**
         * get vacationList
         */
        vacationList = VacationList.get(params["id"])


        if (params.note || params.orderNo) {

            /**
             * to get list of  ids of vacation request
             */
            List vacationListEmployeeIds = params.listString("check_vacationRequestTableInVacationList")
            params.remove("check_vacationRequestTableInVacationList")

            /**
             * get selected vacation request
             */
            GrailsParameterMap vacationListEmployeeParam = new GrailsParameterMap(["ids[]": vacationListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            vacationListEmployeeList = vacationListEmployeeService?.search(vacationListEmployeeParam)

            if (vacationListEmployeeIds) {
                vacationListEmployeeIds?.each { String id ->

                    /**
                     * get vacation list employee
                     */
                    vacationListEmployee = vacationListEmployeeList?.find { it?.id == id }
                    request = vacationListEmployee?.vacationRequest

                    /**
                     * change request status to approved
                     */
//                    request?.requestStatus = ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED

                    /**
                     * change vacation list employee status
                     */
                    vacationListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                    /*
                    * note is required in reject.
                    */
                    if (params.note != null || params.orderNo != null) {
                        vacationListEmployee?.addToVacationListEmployeeNotes(new VacationListEmployeeNote(vacationListEmployee: vacationListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                    }
                }

                try {
                    vacationList.save(failOnSave: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (vacationList?.hasErrors()) {
                        vacationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            } else {
                vacationList.errors.reject('vacationList.error.not.selected.request.message')
                return vacationList
            }
        } else {
            vacationList.errors.reject('vacationList.not.requestRejected.message')
            return vacationList
        }
        return vacationList
    }

    /**
     * close vacation list
     * @param GrailsParameterMap params
     * @return VacationList
     */
    VacationList closeList(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        VacationList vacationList = null
        List<VacationListEmployee> vacationListEmployeeList

        if (params.id) {
            //return the correspondence list:
            vacationList = VacationList?.get(params["id"])

            vacationListEmployeeList = VacationListEmployee.executeQuery("From VacationListEmployee vacationListEmployee where vacationListEmployee.vacationList.id= :vacationListId and vacationListEmployee.recordStatus= :recordStatus", [vacationListId: vacationList?.id, recordStatus: EnumListRecordStatus.NEW])

            //to change the correspondenceListStatus to submitted when we close the vacation list
            CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
            correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
            correspondenceListStatus.correspondenceList = vacationList
            correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
            correspondenceListStatus.firm = vacationList?.firm

            if (vacationListEmployeeList) {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
            } else {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
            }

            if (VacationList) {

                Boolean isRequestUpdated = false
                List<UserTerm> userTermKeyList = []
                List<String> userTermValueList = []
                VacationRequest vacationRequest = null
                vacationList?.vacationListEmployees?.each { VacationListEmployee vacationListEmployee ->
                    vacationRequest = vacationListEmployee?.vacationRequest

                    //reflect status on request itself.
                    if (vacationListEmployee.recordStatus == EnumListRecordStatus.APPROVED) {
                        vacationRequest.requestStatus = EnumRequestStatus.APPROVED
                        requestChangesHandlerService.applyRequestChanges(vacationRequest)
                    } else if (vacationListEmployee.recordStatus == EnumListRecordStatus.REJECTED) {
                        vacationRequest.requestStatus = EnumRequestStatus.REJECTED
                    }

                    // set external order numbers
                    List <ListNote> orderNumberNoteList = vacationListEmployee?.vacationListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                    if(orderNumberNoteList?.size() > 0){
                        ListNote orderNumberNote = orderNumberNoteList?.last()
                        vacationRequest.externalOrderNumber= orderNumberNote.orderNo
                        vacationRequest.externalOrderDate= orderNumberNote.noteDate
                    }

                    if (vacationRequest?.requestStatus in [EnumRequestStatus.APPROVED, EnumRequestStatus.REJECTED]) {
                        userTermKeyList = []
                        userTermValueList = []

                        userTermKeyList.add(UserTerm.USER)
                        userTermValueList.add("${vacationRequest?.employee?.personId}")

                        //call notification method to notify user
                        requestService?.createRequestNotification(vacationRequest?.id,
                                VacationRequest.getName(),
                                ZonedDateTime.now()?.minusDays(1),
                                vacationRequest.requestStatus,
                                userTermKeyList,
                                userTermValueList,
                                null,
                                EnumNotificationType.MY_NOTIFICATION,
                                null)
                    }
                }


                if (correspondenceListStatus.save()) {
                    vacationList.currentStatus = correspondenceListStatus
                }
                try {

                    vacationList.save(failOnError: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (vacationList?.hasErrors()) {
                        vacationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        }
        return vacationList
    }
/*
 * update employee status in case new vacation--------------------------------------------
 *    if the request is vacation request
 *       - change current employee status to be vacation
 *       - change the employee status
 */

    void updateEmployeeStatusToVacation(Firm firm) {
        try {

            //return the list of all vacationListEmployee which are approved but still not reflected into employee status
            //with the follow conditions:
            //request from date < current dat , list is closed, list is the selected firm, record status is approved
            List<VacationListEmployee> vacationListEmployees = VacationListEmployee.executeQuery("from VacationListEmployee vle " +
                    "where vle.vacationRequest.id in ( select id from VacationRequest r where r.fromDate <= :currentDate and r.firm.id = :firmId)" +
                    " and vle.vacationList.id in ( select id from VacationList l where l.currentStatus.id in " +
                    " ( select id from CorrespondenceListStatus c where c.correspondenceList.id = l.id and c.correspondenceListStatus in (:correspondenceListStatus)) )" +
                    " and vle.recordStatus = :recordStatus", [currentDate: ZonedDateTime.now(), recordStatus: EnumListRecordStatus.APPROVED, firmId: firm?.id, correspondenceListStatus: EnumCorrespondenceListStatus.CLOSED])

            //get the employee status : Vacation
            EmployeeStatus employeeStatusVacation = EmployeeStatus.get(EnumEmployeeStatus.IN_VACATION.getValue(firm?.code))
            //get the employee status : working
            EmployeeStatus employeeStatusWorking = EmployeeStatus.get(EnumEmployeeStatus.WORKING.getValue(firm?.code))
            EmployeeStatusHistory employeeStatusHistory
            //loop on each employee to update the status history , and update the employee list record status
            vacationListEmployees.each { VacationListEmployee vacationListEmployee ->
                println "update vacation status"
                //update employee status history and give the vacation status:
                if (employeeStatusVacation) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory?.employee = vacationListEmployee?.vacationRequest?.employee
                    employeeStatusHistory?.fromDate = vacationListEmployee?.vacationRequest?.fromDate
                    employeeStatusHistory?.transientData.put("firm", firm);
                    employeeStatusHistory?.employeeStatus = employeeStatusVacation
                    employeeStatusHistory.save(flush: true, failOnError: true)
                    vacationListEmployee?.vacationRequest?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                }
                if (employeeStatusWorking) {
                    employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory = EmployeeStatusHistory.executeQuery("FROM EmployeeStatusHistory esh where " +
                            " esh.employeeStatus.id=:employeeStatusId and esh.employee.id=:employeeId and esh.toDate=:nullDate", [
                            employeeStatusId: employeeStatusWorking?.id,
                            employeeId      : vacationListEmployee?.vacationRequest?.employee?.id,
                            nullDate        : PCPUtils.DEFAULT_ZONED_DATE_TIME
                    ])[0]
                    if (employeeStatusHistory) {
                        employeeStatusHistory?.toDate = vacationListEmployee?.vacationRequest?.fromDate
                        employeeStatusHistory?.save(flush: true, failOnError: true)
                    }
                }
                vacationListEmployee?.recordStatus = EnumListRecordStatus.REFLECTED
                vacationListEmployee?.validate()
                vacationListEmployee?.vacationRequest?.employee?.validate()
                vacationListEmployee?.save(flush: true, failOnError: true);
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }

}