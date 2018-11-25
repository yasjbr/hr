package ps.gov.epsilon.hr.firm.suspension

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.enums.UserTerm

import java.sql.Timestamp
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create suspension list
 * <h1>Usage</h1>
 * -this service is used to create suspension list
 * <h1>Restriction</h1>
 * -need request & firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    SuspensionRequestService suspensionRequestService
    SuspensionListEmployeeService suspensionListEmployeeService
    RequestService requestService

    //to get the value of requisition status
    public static currentStatusValue = { cService, SuspensionList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, SuspensionList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../suspensionList/manageSuspensionList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set suspensionListEmployeesIds = params.listString("suspensionListEmployees.id")

        return SuspensionList.createCriteria().list(max: max, offset: offset) {
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
                if (suspensionListEmployeesIds) {
                    suspensionListEmployees {
                        inList("id", suspensionListEmployeesIds)
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
     * custom search to find the number of requests in the suspension  list in one select statement for performance issue
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
        String trackingInfoDateCreatedUTC = params['trackingInfo.dateCreatedUTC']
        Integer numberOfCompetitorsValue = params.int("numberOfCompetitorsValue")
        String dateCreated = params['dateCreated']
        String sendDate = params['sendDate']
        String receiveDate = params['receiveDate']
        Timestamp fromSendDate = PCPUtils.parseTimestamp(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestamp(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])
        Timestamp fromReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateFrom'])
        Timestamp toReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateTo'])


        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of suspensions in the list, the send date, and the current list status
        String query = "FROM suspension_list al  LEFT JOIN " +
                "  (SELECT ale.suspension_list_id ,count(ale.id) no_of_employee" +
                "  FROM suspension_list_employee ale " +
                "  group by ale.suspension_list_id ) b" +
                "  on al.id= b.suspension_list_id , correspondence_list_status cls,correspondence_list cl" +
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
        if (trackingInfoDateCreatedUTC) {
            query = query + " and to_char(cl.date_created,'dd/MM/yyyy')  = :trackingInfoDateCreatedUTC "
            sqlParamsMap.put("trackingInfoDateCreatedUTC", trackingInfoDateCreatedUTC)
        }

        //check 3 cases of date created > = <
        if (dateCreated) {
            query = query + " and to_char(cl.date_created,'dd/MM/yyyy')  = :trackingInfoDateCreatedUTC "
            sqlParamsMap.put("trackingInfoDateCreatedUTC", dateCreated)
        }
        if (fromDateCreated) {
            query = query + " and cl.date_created >= :fromDateCreated "
            sqlParamsMap.put("fromDateCreated", Date.from(fromDateCreated.toInstant()))
        }
        if (toDateCreated) {
            query = query + " and cl.date_created <= :toDateCreated "
            sqlParamsMap.put("toDateCreated", Date.from(toDateCreated.toInstant()))
        }

        //check 3 cases of send date created > = <
        if (sendDate) {
            query = query + " and to_char(sbl.from_date_datetime,'dd/MM/yyyy')  = :sendDate "
            sqlParamsMap.put("sendDate", sendDate)
        }
        if (fromSendDate) {
            query = query + " and sbl.from_date_datetime >= :fromSendDate "
            sqlParamsMap.put("fromSendDate", Date.from(fromSendDate.toInstant()))
        }
        if (toSendDate) {
            query = query + " and sbl.from_date_datetime <= :toSendDate "
            sqlParamsMap.put("toSendDate", Date.from(toSendDate.toInstant()))
        }

        //check 3 cases of receive date created > = <
        if (receiveDate) {
            query = query + " and to_char(sdl.from_date_datetime,'dd/MM/yyyy')  = :receiveDate "
            sqlParamsMap.put("receiveDate", receiveDate)
        }
        if (fromReceiveDate) {
            query = query + " and sdl.from_date_datetime >= :fromReceiveDate "
            sqlParamsMap.put("fromReceiveDate", Date.from(fromReceiveDate.toInstant()))
        }
        if (toReceiveDate) {
            query = query + " and sdl.from_date_datetime <= :toReceiveDate "
            sqlParamsMap.put("toReceiveDate", Date.from(toReceiveDate.toInstant()))
        }
        if (numberOfCompetitorsValue != null) {
            query = query + " and COALESCE(b.no_of_employee,0) = :numberOfCompetitorsValueParam  "
            sqlParamsMap.put("numberOfCompetitorsValueParam", numberOfCompetitorsValue)
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
        } else if (columnName?.equalsIgnoreCase("manualincomeno")) {
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
                    COALESCE( NULLIF(cl.date_created,'0003-03-03 03:03:03') ) as date_created,
                    COALESCE( NULLIF(cls.to_date_datetime,'0003-03-03 03:03:03') ) as to_date_datetime,
                    COALESCE( NULLIF(cls.from_date_datetime,'0003-03-03 03:03:03') ) as from_date_datetime,
                    COALESCE( NULLIF(sbl.from_date_datetime,'0003-03-03 03:03:03') ) as send_date,
                    COALESCE( NULLIF(sdl.from_date_datetime,'0003-03-03 03:03:03') ) as receive_date,
                    cl.manual_outgoing_no,
                    cl.manual_income_no,
                    cls.correspondence_list_status,
                    cl.current_status_id,
                    COALESCE(no_of_employee,0) as no_of_employee,
                    cl.receiving_party,
                     cl.cover_letter,
                    COALESCE( NULLIF(cl.last_updated,'0003-03-03 03:03:03') ) as last_updated
              """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //pagination parameters
        sqlQuery.setMaxResults(max)
        sqlQuery.setFirstResult(offset)


        final queryResults = sqlQuery.list()

        List<SuspensionList> results = []
        // Transform resulting rows to a map with key organisationName.
        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[3])
            ZonedDateTime lastUpdatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[14])
            ZonedDateTime toDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[4])
            ZonedDateTime fromDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[5])
            ZonedDateTime sendDateZonedDateTime
            ZonedDateTime receiveDateZonedDateTime
            if (resultRow[6]) {
                sendDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[6])
            }
            if (resultRow[7]) {
                receiveDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[7])
            }

            SuspensionList suspensionList = new SuspensionList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    coverLetter: resultRow[14],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            suspensionList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            suspensionList.currentStatus = currentStatus
            results.add(suspensionList)
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
     * @return SuspensionList.
     */
    SuspensionList save(GrailsParameterMap params) {
        SuspensionList suspensionListInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            suspensionListInstance = SuspensionList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (suspensionListInstance.version > version) {
                    suspensionListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionList.label', null, 'suspensionList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionList while you were editing")
                    return suspensionListInstance
                }
            }
            if (!suspensionListInstance) {
                suspensionListInstance = new SuspensionList()
                suspensionListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionList.label', null, 'suspensionList', LocaleContextHolder.getLocale())] as Object[], "This suspensionList with ${params.id} not found")
                return suspensionListInstance
            }
        } else {
            suspensionListInstance = new SuspensionList()
        }
        try {
            suspensionListInstance.properties = params;

            /**
             * in CREATED phase: create new status for the list
             */
            CorrespondenceListStatus correspondenceListStatus
            EnumCorrespondenceListStatus listStatus= params.correspondenceListStatus?EnumCorrespondenceListStatus.valueOf(params.correspondenceListStatus):null
            if (!params.id && !listStatus) {
                //when create the list, its is CREATED phase:
                listStatus= EnumCorrespondenceListStatus.CREATED
            }
            if (listStatus) {
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = ZonedDateTime.now()
                correspondenceListStatus.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                correspondenceListStatus.correspondenceListStatus = listStatus
                correspondenceListStatus.receivingParty = EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = suspensionListInstance.firm
                correspondenceListStatus.correspondenceList = suspensionListInstance
                suspensionListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            suspensionListInstance.save(flush: true, failOnError: true);

            //save the current status:
            if (correspondenceListStatus?.id && suspensionListInstance?.id) {
                suspensionListInstance?.currentStatus = correspondenceListStatus
                suspensionListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            suspensionListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionListInstance
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

            SuspensionList instance = SuspensionList.get(id)
            //to be able to delete an suspension list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED]) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo.status = GeneralStatus.DELETED
                    instance.save()
                    deleteBean.status = true
                }
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('SuspensionList.deleteMessage.label')
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
     * @return SuspensionList.
     */
    @Transactional(readOnly = true)
    SuspensionList getInstance(GrailsParameterMap params) {
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
     * to add suspensionRequests to suspension list
     * @param GrailsParameterMap params
     * @return SuspensionList
     */
    SuspensionList addSuspensionRequests(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        /**
         *  get list of ids of selected suspension request
         */
        List checkedEncodedSuspensionRequestIdList = params.listString("checked_requestIdsList")

        SuspensionList suspensionList = null
        GrailsParameterMap suspensionRequestMap
        SuspensionRequest suspensionRequest = null
        List<SuspensionRequest> suspensionRequestList
        SuspensionListEmployee suspensionListEmployee

        /**
         * get suspension list
         */
        suspensionList = SuspensionList.load(params["id"])
        if (checkedEncodedSuspensionRequestIdList) {
            try {
                /**
                 * get selected suspension request
                 */
                suspensionRequestMap = new GrailsParameterMap(["ids[]": checkedEncodedSuspensionRequestIdList], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                suspensionRequestList = suspensionRequestService?.search(suspensionRequestMap)

                /**
                 * add all selected suspension request to suspension list
                 */
                checkedEncodedSuspensionRequestIdList?.each { String id ->

                    /**
                     * change suspension request status to ADD_TO_LIST
                     */
                    suspensionRequest = suspensionRequestList?.find { it.id == id }
                    if (suspensionRequest) {
                        suspensionRequest?.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        suspensionRequest?.validate()
                        suspensionRequest?.save(flush: true, failOnError: true)

                        /**
                         * add suspension request to suspension list employee
                         */
                        suspensionListEmployee = new SuspensionListEmployee()
                        suspensionListEmployee?.suspensionRequest = suspensionRequest
                        suspensionListEmployee?.effectiveDate = suspensionRequest?.fromDate
                        suspensionListEmployee?.employee = suspensionRequest?.employee
                        suspensionListEmployee?.fromDate = suspensionRequest?.fromDate
                        suspensionListEmployee?.periodInMonth = suspensionRequest?.periodInMonth
                        suspensionListEmployee?.suspensionType = suspensionRequest?.suspensionType
                        suspensionListEmployee?.toDate = suspensionRequest?.toDate
                        suspensionListEmployee?.recordStatus = EnumListRecordStatus.NEW
                        suspensionListEmployee?.suspensionList = suspensionList
                        suspensionListEmployee?.currentEmploymentRecord = suspensionRequest?.employee?.currentEmploymentRecord
                        suspensionListEmployee?.currentEmployeeMilitaryRank = suspensionRequest?.employee?.currentEmployeeMilitaryRank

                        if (suspensionRequest?.requestStatusNote) {
                            SuspensionListEmployeeNote note = new SuspensionListEmployeeNote(
                                    noteDate: suspensionRequest?.requestDate,
                                    note: suspensionRequest?.requestStatusNote,
                                    orderNo: "",
                                    suspensionListEmployee: suspensionListEmployee,
                            );
                            if (!note?.validate()) {
                                suspensionList?.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                suspensionListEmployee.addToSuspensionListEmployeeNotes(note);
                            }
                        }

                        /**
                         * add the suspension list employee to suspension list
                         */
                        suspensionList?.addToSuspensionListEmployees(suspensionListEmployee)
                        println "add to list 11111111111111111111111111111111"
                    }
                }
                suspensionList?.save(failOnError: true)
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                if (suspensionList?.hasErrors()) {
                    suspensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        } else {
            suspensionList?.errors?.reject('suspensionList.error.not.selected.request.message')
            return suspensionList
        }
        return suspensionList
    }

    /**
     * send suspension list
     * @param GrailsParameterMap params
     * @return SuspensionList
     */
    SuspensionList sendData(GrailsParameterMap params) {
        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        CorrespondenceListStatus correspondenceListStatus
        SuspensionList suspensionList = null
        if (params.id) {

            /**
             * get suspension list
             */
            suspensionList = SuspensionList.get(params["id"])

            //return error if no request added to list
            if (suspensionList?.suspensionListEmployees?.size() == 0) {
                suspensionList.errors.reject("list.sendList.error")
                return suspensionList
            }

            if (SuspensionList) {
                /**
                 * change suspension list status to SUBMITTED
                 */
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.correspondenceList = suspensionList
                correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = suspensionList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                if (correspondenceListStatus.save()) {
                    suspensionList.currentStatus = correspondenceListStatus
                }
                /**
                 * assign manualIncomeNo when we send the list
                 */
                suspensionList?.manualOutgoingNo = params.manualOutgoingNo
            }
            try {
                suspensionList.save(failOnError: true)
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                if (suspensionList?.hasErrors()) {
                    suspensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        }
        return suspensionList
    }

    /**
     * receive suspension list
     * @param GrailsParameterMap params
     * @return SuspensionList
     */
    SuspensionList receiveData(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        CorrespondenceListStatus correspondenceListStatus
        SuspensionList suspensionList = null
        if (params.id) {

            /**
             * get suspension list
             */
            suspensionList = SuspensionList.get(params["id"])

            if (SuspensionList) {
                /**
                 * change suspension list status to RECEIVED
                 */
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.correspondenceList = suspensionList
                correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = suspensionList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                if (correspondenceListStatus.save()) {
                    suspensionList.currentStatus = correspondenceListStatus
                }
                /**
                 * assign manualIncomeNo when we send the list
                 */
                suspensionList.manualIncomeNo = params.manualIncomeNo

                try {
                    suspensionList.save(failOnError: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (suspensionList?.hasErrors()) {
                        suspensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        }
        return suspensionList
    }

    /**
     * to change the suspension List employee status to EMPLOYED in the receive suspension list
     * @param GrailsParameterMap params
     * @return SuspensionList
     */
    SuspensionList approveSuspensionRequest(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        /**
         * to get list of  ids of suspension request
         */
        List suspensionListEmployeeIds = params.listString("check_suspensionRequestTableInSuspensionList")
        params.remove("check_suspensionRequestTableInSuspensionList")
        SuspensionListEmployee suspensionListEmployee
        List<SuspensionListEmployee> suspensionListEmployeeList
        SuspensionList suspensionList = null
        /**
         * get selected suspension list employee
         */
        GrailsParameterMap suspensionListEmployeeParam = new GrailsParameterMap(["ids[]": suspensionListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        suspensionListEmployeeList = suspensionListEmployeeService?.search(suspensionListEmployeeParam)

        /**
         * get suspensionList
         */
        suspensionList = SuspensionList.get(params["id"])

        if (suspensionListEmployeeIds) {


            suspensionListEmployeeIds?.each { String id ->
                /**
                 * get suspension list employee by id
                 */
                suspensionListEmployee = suspensionListEmployeeList?.find { it?.id == id }
                suspensionList = suspensionListEmployee?.suspensionList

                /**
                 * change suspension list employee status
                 */
                suspensionListEmployee?.recordStatus = EnumListRecordStatus.APPROVED

                /**
                 * note is required in reject.
                 */
                if (params.note || params.orderNo) {
                    suspensionListEmployee?.addToSuspensionListEmployeeNotes(new SuspensionListEmployeeNote(suspensionListEmployee: suspensionListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                }
            }
            try {

                suspensionList.save(failOnError: true)
            } catch (Exception ex) {
                transactionStatus.setRollbackOnly()
                if (suspensionList?.hasErrors()) {
                    suspensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        } else {
            if (!suspensionList) {
                suspensionList = new SuspensionList()
            }
            suspensionList.errors.reject("suspensionList.error.not.selected.request.message")
        }
        return suspensionList
    }

    /**
     * to change the suspension List employee status to NOT_EMPLOYED in the receive suspension list
     * @param GrailsParameterMap params
     * @return SuspensionList
     */
    SuspensionList changeSuspensionRequestToRejected(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        SuspensionListEmployee suspensionListEmployee
        List<SuspensionListEmployee> suspensionListEmployeeList
        SuspensionList suspensionList = null

        /**
         * get suspensionList
         */
        suspensionList = SuspensionList.get(params["id"])


        if (params.note || params.orderNo) {

            /**
             * to get list of  ids of suspension request
             */
            List suspensionListEmployeeIds = params.listString("check_suspensionRequestTableInSuspensionList")
            params.remove("check_suspensionRequestTableInSuspensionList")

            /**
             * get selected suspension request
             */
            GrailsParameterMap suspensionListEmployeeParam = new GrailsParameterMap(["ids[]": suspensionListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            suspensionListEmployeeList = suspensionListEmployeeService?.search(suspensionListEmployeeParam)

            if (suspensionListEmployeeIds) {
                suspensionListEmployeeIds?.each { String id ->

                    /**
                     * get suspension list employee
                     */
                    suspensionListEmployee = suspensionListEmployeeList?.find { it?.id == id }

                    /**
                     * change suspension list employee status
                     */
                    suspensionListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                    /*
                    * note is required in reject.
                    */
                    if (params.note != null || params.orderNo != null) {
                        suspensionListEmployee?.addToSuspensionListEmployeeNotes(new SuspensionListEmployeeNote(suspensionListEmployee: suspensionListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                    }
                }

                try {
                    suspensionList.save(failOnSave: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (suspensionList?.hasErrors()) {
                        suspensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            if (!suspensionList) {
                suspensionList = new SuspensionList()
            }
            suspensionList.errors.reject("suspensionList.error.note.message")
            return suspensionList
        }
        return suspensionList
    }

    /**
     * close suspension list
     * @param GrailsParameterMap params
     * @return SuspensionList
     */
    SuspensionList closeList(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        SuspensionList suspensionList = null
        List<SuspensionListEmployee> suspensionListEmployeeList
        if (params.id) {
            /**
             * return the correspondence list
             */
            suspensionList = SuspensionList?.get(params["id"])

            /**
             * get list of suspension list employee with status NEW
             */
            suspensionListEmployeeList = SuspensionListEmployee.executeQuery("From SuspensionListEmployee suspensionListEmployee where suspensionListEmployee.suspensionList.id= :suspensionListId and  suspensionListEmployee.recordStatus= :recordStatus", [suspensionListId: suspensionList?.id, recordStatus: EnumListRecordStatus.NEW])
            /**
             * create new CLOSE status
             */
            CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
            correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
            correspondenceListStatus.correspondenceList = suspensionList
            correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
            correspondenceListStatus.firm = suspensionList?.firm
            if (suspensionListEmployeeList) {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
            } else {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
            }

            /**
             * in case: all employee list is APPROVED/REJECTED
             */

            List<SuspensionListEmployee> ListEmployeeList

            if (SuspensionList) {

                /**
                 * get list of suspension employee list
                 */
                ListEmployeeList = suspensionList?.suspensionListEmployees?.toList()

                /**
                 * change the request status before close the list
                 */
                List<UserTerm> userTermKeyList = []
                List<String> userTermValueList = []
                List<Map> notificationActionsMap = null
                SuspensionRequest suspensionRequest
                ListEmployeeList?.each { SuspensionListEmployee suspensionListEmployee ->
                    suspensionRequest = suspensionListEmployee.suspensionRequest

                    // set external order numbers
                    List <ListNote> orderNumberNoteList = suspensionListEmployee?.suspensionListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                    if(orderNumberNoteList?.size() > 0){
                        ListNote orderNumberNote = orderNumberNoteList?.last()
                        suspensionRequest.externalOrderNumber= orderNumberNote.orderNo
                        suspensionRequest.externalOrderDate= orderNumberNote.noteDate
                    }

                    /**
                     * approve request when list employee is approved
                     */
                    if (suspensionListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                        suspensionRequest?.requestStatus = EnumRequestStatus.APPROVED
                    } else {
                        /**
                         * reject request when list employee  is rejected
                         */
                        suspensionRequest?.requestStatus = EnumRequestStatus.REJECTED
                    }

                    suspensionRequest?.validate()
                    suspensionRequest?.save(flush:true, failOnError:true)

                    //set notification user.
                    userTermKeyList = []
                    userTermValueList = []
                    userTermKeyList.add(UserTerm.USER)
                    userTermValueList.add("${suspensionRequest?.employee?.personId}")

                    //we add link into notification to manageSuspensionList.
                    notificationActionsMap = [
                            [action            : "manageSuspensionList",
                             controller        : "suspensionList",
                             label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                             icon              : "icon-cog",

                             notificationParams: [
                                     new NotificationParams(name: "encodedId", value: suspensionListEmployee?.encodedId),
                             ]
                            ]
                    ]

                    //create notification.
                    requestService?.createRequestNotification(suspensionRequest?.id,
                            SuspensionRequest.getName(),
                            ZonedDateTime.now()?.minusDays(1),
                            suspensionRequest.requestStatus,
                            userTermKeyList,
                            userTermValueList,
                            null,
                            EnumNotificationType.MY_NOTIFICATION,
                            null)


                }

                /**
                 * assign new CLOSE status to suspension list
                 */
                if (correspondenceListStatus.save()) {
                    suspensionList.currentStatus = correspondenceListStatus
                }
                try {
                    suspensionList.save(failOnError: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (suspensionList?.hasErrors()) {
                        suspensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }

        }
        return suspensionList
    }

    /*
     * update employee status in case new suspension--------------------------------------------
     *    if the request is suspension request
     *       - change current employee status to be Suspended (create new status with type: SUSPENDED) -> un committed category
     *       - end the working employee status by adding endDate
     */

    void updateEmployeeStatusToSuspended(Firm firm) {
        //TODO: to be called by the job!
        try {
            //return the list of all suspensionListEmployee which are approved but still not reflected into employee status
            List<SuspensionListEmployee> suspensionListEmployees = SuspensionListEmployee.executeQuery("from SuspensionListEmployee sle " +
                    "where sle.suspensionList.id in ( select id from SuspensionList l where l.firm.id = :firmId and l.currentStatus.id in " +
                    " ( select id from CorrespondenceListStatus c where c.correspondenceList.id = l.id and c.correspondenceListStatus in (:correspondenceListStatus)) ) and " +
                    "sle.fromDate <= :currentDate and sle.recordStatus = :recordStatus",
                    [firmId: firm?.id, correspondenceListStatus: [EnumCorrespondenceListStatus.CLOSED, EnumCorrespondenceListStatus.PARTIALLY_CLOSED], currentDate: ZonedDateTime.now(), recordStatus: EnumListRecordStatus.APPROVED])
            if (suspensionListEmployees) {
                //get the employee status : suspended
                EmployeeStatus employeeStatusSuspended = EmployeeStatus.get(EnumEmployeeStatus.SUSPENDED.getValue(firm?.code))
                EmployeeStatusHistory employeeStatusHistory

                //loop on each employee to update the status history , and update the employee list record status
                suspensionListEmployees.each { SuspensionListEmployee suspensionListEmployee ->

                    //update employee status history to close the working status and open new suspension status
                    if (employeeStatusSuspended) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory?.employee = suspensionListEmployee?.employee
                        employeeStatusHistory?.fromDate = suspensionListEmployee?.effectiveDate
                        employeeStatusHistory?.transientData.put("firm", firm);
                        employeeStatusHistory?.employeeStatus = employeeStatusSuspended
                        employeeStatusHistory.save(flush: true, failOnError: true)
                        suspensionListEmployee?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                    }
                    suspensionListEmployee?.recordStatus = EnumListRecordStatus.REFLECTED
                    suspensionListEmployee?.validate()
                    suspensionListEmployee?.employee?.validate()
                    suspensionListEmployee?.save(flush: true, failOnError: true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }


}