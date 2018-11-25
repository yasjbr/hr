package ps.gov.epsilon.hr.firm.allowance

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.request.RequestChangesHandlerService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.NotificationService
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create allowance list
 * <h1>Usage</h1>
 * -this service is used to create allowance list
 * <h1>Restriction</h1>
 * -need a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class AllowanceListService {


    MessageSource messageSource
    def formatService
    def sessionFactory
    AllowanceRequestService allowanceRequestService
    AllowanceListEmployeeService allowanceListEmployeeService
    PersonRelationShipsService personRelationShipsService
    RequestService requestService
    NotificationService notificationService
    RequestChangesHandlerService requestChangesHandlerService

    //to get the value of requisition status
    public static currentStatusValue = { cService, AllowanceList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, AllowanceList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../allowanceList/manageAllowanceList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
            [sort: false, search: false, hidden: false, name: "transientData.sendDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualOutgoingNo", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.receiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualIncomeNo", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.numberOfCompetitorsValue", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "currentStatus.correspondenceListStatus", type: "enum", source: 'domain'],
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
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
        Set allowanceListEmployeesIds = params.listString("allowanceListEmployees.id")

        return AllowanceList.createCriteria().list(max: max, offset: offset) {
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
                if (allowanceListEmployeesIds) {
                    allowanceListEmployees {
                        inList("id", allowanceListEmployeesIds)
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
     * custom search to find the number of requests in the allowance  list in one select statement for performance issue
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

        //the query to retrieve the list details, num of allowances in the list, the send date, and the current list status
        String query = "FROM allowance_list al  LEFT JOIN " +
                "  (SELECT ale.allowance_list_id ,count(ale.id) no_of_employee" +
                "  FROM allowance_list_employee ale " +
                "  group by ale.allowance_list_id ) b" +
                "  on al.id= b.allowance_list_id , correspondence_list_status cls,correspondence_list cl" +
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
                    cl.cover_letter
              """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //pagination parameters
        sqlQuery.setFetchSize(max)
        sqlQuery.setFirstResult(offset)


        final queryResults = sqlQuery.list()

        List<AllowanceList> results = []
        // Transform resulting rows to a map with key organisationName.
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy")

        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = ZonedDateTime.ofInstant(new Date(((Timestamp) resultRow[3])?.getTime())?.toInstant(), ZoneId.systemDefault())
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

            AllowanceList allowanceList = new AllowanceList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[14],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate                : sendDateZonedDateTime,
                                    receiveDate             : receiveDateZonedDateTime,
                                    numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC])
            allowanceList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            allowanceList.currentStatus = currentStatus
            results.add(allowanceList)
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
     * @return AllowanceList.
     */
    AllowanceList save(GrailsParameterMap params) {
        AllowanceList allowanceListInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            allowanceListInstance = AllowanceList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (allowanceListInstance.version > version) {
                    allowanceListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('allowanceList.label', null, 'allowanceList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this allowanceList while you were editing")
                    return allowanceListInstance
                }
            }
            if (!allowanceListInstance) {
                allowanceListInstance = new AllowanceList()
                allowanceListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('allowanceList.label', null, 'allowanceList', LocaleContextHolder.getLocale())] as Object[], "This allowanceList with ${params.id} not found")
                return allowanceListInstance
            }
        } else {
            allowanceListInstance = new AllowanceList()
        }
        try {
            allowanceListInstance.properties = params;

            /**
             * in CREATED phase: create new status for the list
             */
            CorrespondenceListStatus correspondenceListStatus
            EnumCorrespondenceListStatus listStatus= params.correspondenceListStatus?EnumCorrespondenceListStatus.valueOf(params.correspondenceListStatus):null
            if (!params.id && !listStatus) {
                //when create the list , its is CREATED phase:
                listStatus= EnumCorrespondenceListStatus.CREATED
            }

            if(listStatus){
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                        correspondenceListStatus: listStatus, receivingParty: EnumReceivingParty.SARAYA, firm: allowanceListInstance.firm)
                allowanceListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            allowanceListInstance.save(flush: true, failOnError: true);

            //save the current status:
            if (correspondenceListStatus?.id && allowanceListInstance?.id) {
                allowanceListInstance?.currentStatus = correspondenceListStatus
                allowanceListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            allowanceListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return allowanceListInstance
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
            AllowanceList instance = AllowanceList.get(id)
            //to be able to delete an allowance list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED]) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo.status = GeneralStatus.DELETED
                    instance.save()
                    deleteBean.status = true
                }
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('AllowanceList.deleteMessage.label')
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
     * @return AllowanceList.
     */
    @Transactional(readOnly = true)
    AllowanceList getInstance(GrailsParameterMap params) {
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
     * @return AllowanceList.
     */
    @Transactional(readOnly = true)
    AllowanceList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = customSearch(params)
            if (results) {
                return results.resultList[0]
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
     * to add allowanceRequests to allowance list
     * @param GrailsParameterMap params
     * @return AllowanceList
     */
    AllowanceList addAllowanceRequests(GrailsParameterMap params) {
        AllowanceList allowanceList = AllowanceList.load(params["id"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");

        if (checkedRequestIdList.size() > 0) {
            AllowanceListEmployee allowanceListEmployee
            //retrieve the selected services:
            List<AllowanceRequest> allowanceRequests = AllowanceRequest.executeQuery("from AllowanceRequest c where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (allowanceRequests) {
                try {
                    allowanceRequests.each { AllowanceRequest allowanceRequest ->
                        //add the service list employee to list
                        allowanceRequest.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        allowanceRequest?.validate()
                        allowanceRequest?.save(failOnError: true, flush: true)

                        //create new service list employee and add the service Request
                        allowanceListEmployee = new AllowanceListEmployee()
                        allowanceListEmployee?.recordStatus = EnumListRecordStatus.NEW
                        allowanceListEmployee?.allowanceRequest = allowanceRequest
                        allowanceListEmployee?.allowanceList = allowanceList
                        allowanceListEmployee?.validate()

                        if (allowanceRequest?.requestStatusNote) {
                            AllowanceListEmployeeNote note = new AllowanceListEmployeeNote(
                                    noteDate: allowanceRequest?.requestDate,
                                    note: allowanceRequest?.requestStatusNote,
                                    orderNo: "",
                                    allowanceListEmployee: allowanceListEmployee,
                            );
                            if (!note.validate()) {
                                allowanceList.errors.addAllErrors(note.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                allowanceListEmployee.addToAllowanceListEmployeeNotes(note);
                            }
                        }
                        allowanceList.addToAllowanceListEmployee(allowanceListEmployee);
                    }
                    //save the service list changes
                    allowanceList?.currentStatus?.validate()
                    allowanceList?.save(failOnError: true, flush: true)

                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (allowanceList?.errors?.allErrors?.size() == 0) {
                        allowanceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            allowanceList.errors.reject("list.request.notSelected.error")
        }
        return allowanceList
    }

    /**
     * send allowance list
     * @param GrailsParameterMap params
     * @return AllowanceList
     */
    AllowanceList sendData(GrailsParameterMap params) {
        //return allowanceListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            AllowanceList allowanceList = AllowanceList.get(params["id"])

            //return error if no request added to list
            if (allowanceList?.allowanceListEmployee?.size() == 0) {
                allowanceList.errors.reject("list.sendList.error")
                return allowanceList
            }
            if (allowanceList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = allowanceList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = allowanceList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save(failOnError:true, flush:true)) {
                        allowanceList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    allowanceList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all allowanceListEmployee in marital status list to change the status of the request
                    allowanceList?.allowanceListEmployee.each { AllowanceListEmployee allowanceListEmployee ->
                        allowanceListEmployee?.allowanceRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        allowanceListEmployee?.allowanceRequest?.validate()
                    }

                    //save the disciplinary list changes
                    allowanceList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (allowanceList?.errors?.allErrors?.size() == 0) {
                        allowanceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return allowanceList
            }
        }
    }

    /**
     * receive allowance list
     * @param GrailsParameterMap params
     * @return AllowanceList
     */
    AllowanceList receiveData(GrailsParameterMap params) {

        CorrespondenceListStatus correspondenceListStatus
        AllowanceList allowanceList = null

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }



        if (params.id) {

            /**
             * get allowance list
             */
            allowanceList = AllowanceList.get(params["id"])

            if (AllowanceList) {
                /**
                 * change allowance list status to RECEIVED
                 */
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.correspondenceList = allowanceList
                correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = allowanceList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                if (correspondenceListStatus.save()) {
                    allowanceList.currentStatus = correspondenceListStatus
                }
                /**
                 * assign manualIncomeNo when we send the list
                 */
                allowanceList.manualIncomeNo = params.manualIncomeNo

                try {
                    allowanceList.save(failOnError: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (allowanceList?.hasErrors()) {
                        allowanceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        }
        return allowanceList
    }

    /**
     * to change the allowance List employee status to EMPLOYED in the receive allowance list
     * @param GrailsParameterMap params
     * @return AllowanceList
     */
    AllowanceList approveAllowanceRequest(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        /**
         * to get list of  ids of allowance request
         */
        List allowanceListEmployeeIds = params?.listString("check_allowanceRequestTableInAllowanceList")
        params.remove("check_allowanceRequestTableInAllowanceList")
        AllowanceListEmployee allowanceListEmployee
        List<AllowanceListEmployee> allowanceListEmployeeList
        AllowanceList allowanceList = null
        /**
         * get selected allowance list employee
         */
        GrailsParameterMap allowanceListEmployeeParam = new GrailsParameterMap(["ids[]": allowanceListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        allowanceListEmployeeList = allowanceListEmployeeService?.search(allowanceListEmployeeParam)


        if (allowanceListEmployeeIds) {

            /**
             * get allowanceList
             */
            allowanceList = AllowanceList.get(params["id"])


            allowanceListEmployeeIds?.each { String id ->
                /**
                 * get allowance list employee by id
                 */
                allowanceListEmployee = allowanceListEmployeeList?.find { it?.id == id }
                allowanceList = allowanceListEmployee?.allowanceList

                /**
                 * change allowance list employee status
                 */
                allowanceListEmployee?.recordStatus = EnumListRecordStatus.APPROVED

                /**
                 * note is required in reject.
                 */
                if (params.note || params.orderNo) {
                    allowanceListEmployee?.addToAllowanceListEmployeeNotes(new AllowanceListEmployeeNote(allowanceListEmployee: allowanceListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                }
            }
            try {

                allowanceList.save(failOnError: true)
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                if (allowanceList?.hasErrors()) {
                    allowanceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        }
        return allowanceList
    }

    /**
     * to change the allowance List employee status to NOT_EMPLOYED in the receive allowance list
     * @param GrailsParameterMap params
     * @return AllowanceList
     */
    AllowanceList changeAllowanceRequestToRejected(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        AllowanceListEmployee allowanceListEmployee
        List<AllowanceListEmployee> allowanceListEmployeeList
        AllowanceList allowanceList = null




        if (params.note || params.orderNo) {

            /**
             * get allowanceList
             */
            allowanceList = AllowanceList.get(params["id"])

            /**
             * to get list of  ids of allowance request
             */
            List allowanceListEmployeeIds = params.listString("check_allowanceRequestTableInAllowanceList")
            params.remove("check_allowanceRequestTableInAllowanceList")

            /**
             * get selected allowance request
             */
            GrailsParameterMap allowanceListEmployeeParam = new GrailsParameterMap(["ids[]": allowanceListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            allowanceListEmployeeList = allowanceListEmployeeService?.search(allowanceListEmployeeParam)

            if (allowanceListEmployeeIds) {
                allowanceListEmployeeIds?.each { String id ->

                    /**
                     * get allowance list employee
                     */
                    allowanceListEmployee = allowanceListEmployeeList?.find { it?.id == id }

                    /**
                     * change allowance list employee status
                     */
                    allowanceListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                    /*
                    * note is required in reject.
                    */
                    if (params.note != null || params.orderNo != null) {
                        allowanceListEmployee?.addToAllowanceListEmployeeNotes(new AllowanceListEmployeeNote(allowanceListEmployee: allowanceListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                    }
                }

                try {
                    allowanceList.save(failOnSave: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (allowanceList?.hasErrors()) {
                        allowanceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        }
        return allowanceList
    }

    /**
     * close allowance list
     * @param GrailsParameterMap params
     * @return AllowanceList
     */
    AllowanceList closeList(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        AllowanceList allowanceList = null
        List<AllowanceListEmployee> allowanceListEmployeeList
        if (params.id) {
            //return the correspondence list:
            allowanceList = AllowanceList?.get(params["id"])

            allowanceListEmployeeList = AllowanceListEmployee.executeQuery("From AllowanceListEmployee allowanceListEmployee where allowanceListEmployee.allowanceList.id= :allowanceListId and  allowanceListEmployee.recordStatus= :recordStatus", [allowanceListId: allowanceList?.id, recordStatus: EnumListRecordStatus.NEW])

            //to change the correspondenceListStatus to submitted when we close the allowance list
            CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
            correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
            correspondenceListStatus.correspondenceList = allowanceList
            correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
            correspondenceListStatus.firm = allowanceList?.firm
            if (allowanceListEmployeeList) {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
            } else {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
            }
            if (allowanceList) {
                if (correspondenceListStatus.save(failOnError: true)) {
                    allowanceList.currentStatus = correspondenceListStatus
                }
                /**
                 * change request status to  list employee status
                 * change relationship status if there is a relationship
                 */
                AllowanceRequest allowanceRequest = null
                List<UserTerm> userTermKeyList = []
                List<String> userTermValueList = []
                List<Map> notificationActionsMap = null
                allowanceList?.allowanceListEmployee?.each { AllowanceListEmployee allowanceListEmployee ->
                    allowanceRequest = allowanceListEmployee.allowanceRequest

                    // set external order numbers
                    List <ListNote> orderNumberNoteList = allowanceListEmployee?.allowanceListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                    if(orderNumberNoteList?.size() > 0){
                        ListNote orderNumberNote = orderNumberNoteList?.last()
                        allowanceRequest.externalOrderNumber= orderNumberNote.orderNo
                        allowanceRequest.externalOrderDate= orderNumberNote.noteDate
                    }

                    /**
                     * set request status
                     */
                    if (allowanceListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                        allowanceRequest?.requestStatus = EnumRequestStatus.APPROVED

                        // reflect request changes
                        requestChangesHandlerService.applyRequestChanges(allowanceRequest)

                    } else {
                        allowanceRequest?.requestStatus = EnumRequestStatus.REJECTED
                    }

                    /**
                     *  change relationship status if there is a relationship
                     */
                    if (allowanceListEmployee?.allowanceRequest?.personRelationShipsId && allowanceListEmployee?.allowanceRequest?.requestStatus == EnumRequestStatus.APPROVED) {
                        PersonRelationShipsCommand personRelationShipsCommand = new PersonRelationShipsCommand()
                        personRelationShipsCommand.id = allowanceListEmployee?.allowanceRequest?.personRelationShipsId
                        personRelationShipsCommand.isDependent = true
                        personRelationShipsService.savePersonRelationShips(personRelationShipsCommand)
                    }

                    allowanceRequest?.validate()

                    if (allowanceRequest?.requestStatus in [EnumRequestStatus.APPROVED, EnumRequestStatus.REJECTED]) {

                        //set notification user.
                        userTermKeyList = []
                        userTermValueList = []
                        userTermKeyList.add(UserTerm.USER)
                        userTermValueList.add("${allowanceRequest?.employee?.personId}")

                        //we add link into notification to manageAllowanceList.
                        notificationActionsMap = [
                                [action            : "manageAllowanceList",
                                 controller        : "allowanceList",
                                 label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                                 icon              : "icon-cog",

                                 notificationParams: [
                                         new NotificationParams(name: "encodedId", value: allowanceList?.encodedId),
                                 ]
                                ]
                        ]

                        //create notification
                        requestService?.createRequestNotification(allowanceRequest?.id,
                                AllowanceRequest.getName(),
                                ZonedDateTime.now()?.minusDays(1),
                                allowanceRequest.requestStatus,
                                userTermKeyList,
                                userTermValueList,
                                null,
                                EnumNotificationType.MY_NOTIFICATION,
                                null)
                    }
                }
                try {
                    allowanceList.save(failOnError: true)
                } catch (Exception ex) {
                    log.error("Failed to close allowance list", ex)
                    transactionStatus.setRollbackOnly()
                    if (allowanceList?.hasErrors()) {
                        allowanceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        }
        return allowanceList
    }
}

