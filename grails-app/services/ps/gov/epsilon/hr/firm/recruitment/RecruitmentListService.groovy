package ps.gov.epsilon.hr.firm.recruitment

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create recruitment list
 * <h1>Usage</h1>
 * -this service is used to create recruitment list
 * <h1>Restriction</h1>
 * -need a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class RecruitmentListService {

    MessageSource messageSource
    def formatService
    def sessionFactory

    //to get the value of requisition status
    public static currentStatusValue = { cService, RecruitmentList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, RecruitmentList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../recruitmentList/manageRecruitmentList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set recruitmentListEmployeeIds = params.listString("RecruitmentListEmployee.id")
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return RecruitmentList.createCriteria().list(max: max, offset: offset) {
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
                if (recruitmentListEmployeeIds) {
                    RecruitmentListEmployee {
                        inList("id", recruitmentListEmployeeIds)
                    }
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
     * to search model entries with remoting values.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
     PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedList = customSearch(params)
        if (pagedList) {
            return pagedList
        }
    }



    /**
     * custom search to find the number of applicants in the recruitment  list in one select statement for performance issue
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
        Long firmId = PCPSessionUtils.getValue("firmId")
        String code = params["code"]
        String name = params["name"]
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus correspondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"]) : null
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String trackingInfoDateCreatedUTC = params['trackingInfo.dateCreatedUTC']

        Integer numberOfCompetitorsValue = params.int("numberOfCompetitorsValue")
        String dateCreated = params['dateCreated']
        String sendDate = params['sendDate']
        String receiveDate = params['receiveDate']
        Timestamp fromSendDate = PCPUtils.parseTimestampWithSmallestTime(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestampWithBiggestTime(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])
        Timestamp fromReceiveDate = PCPUtils.parseTimestampWithSmallestTime(params['receiveDateFrom'])
        Timestamp toReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateTo'])
        Map sqlParamsMap = [:]


        //the query to retrieve the list details, num of applicants in the list, the send date, and the current list status
        String query = "FROM recruitment_list al  LEFT JOIN " +
                "  (SELECT ale.recruitment_list_id ,count(ale.id) no_of_employee" +
                "  FROM recruitment_list_employee ale " +
                "  group by ale.recruitment_list_id ) b" +
                "  on al.id= b.recruitment_list_id , correspondence_list_status cls,correspondence_list cl" +
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
                    COALESCE( NULLIF(cl.last_updated,'0003-03-03 03:03:03') ) as last_updated, 
                    cl.cover_letter
              """ + query + orderByQuery)

        sqlParamsMap?.each {
            sqlQuery.setParameter(it.key.toString(), it.value)
        }

        //pagination parameters
        sqlQuery.setMaxResults(max)
        sqlQuery.setFirstResult(offset)


        final queryResults = sqlQuery.list()

        List<RecruitmentList> results = []
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

            RecruitmentList recruitmentList = new RecruitmentList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[15],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])

            recruitmentList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            recruitmentList.currentStatus = currentStatus
            results.add(recruitmentList)
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
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return RecruitmentList.
     */
    RecruitmentList save(GrailsParameterMap params) {
        RecruitmentList recruitmentListInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            recruitmentListInstance = RecruitmentList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (recruitmentListInstance.version > version) {
                    recruitmentListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('RecruitmentList.label', null, 'RecruitmentList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this RecruitmentList while you were editing")
                    return recruitmentListInstance
                }
            }
            if (!recruitmentListInstance) {
                recruitmentListInstance = new RecruitmentList()
                recruitmentListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('RecruitmentList.label', null, 'RecruitmentList', LocaleContextHolder.getLocale())] as Object[], "This RecruitmentList with ${params.id} not found")
                return recruitmentListInstance
            }
        } else {
            recruitmentListInstance = new RecruitmentList()
        }
        try {
            recruitmentListInstance.properties = params;
            CorrespondenceListStatus correspondenceListStatus
            if (!params.id) {
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME, correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: EnumReceivingParty.SARAYA, firm: recruitmentListInstance.firm)
                recruitmentListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            recruitmentListInstance.save(flush: true, failOnError: true);
            //save the current status:
            if (correspondenceListStatus?.id && recruitmentListInstance?.id) {
                recruitmentListInstance?.currentStatus = correspondenceListStatus
                recruitmentListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            recruitmentListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return recruitmentListInstance
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
            RecruitmentList instance = RecruitmentList.get(id)
            //to be able to delete an recruitment list when status is created
            if (instance?.currentStatus.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED]) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo.status = GeneralStatus.DELETED
                    instance.save()
                    deleteBean.status = true
                }
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
     * @return RecruitmentList.
     */
    @Transactional(readOnly = true)
    RecruitmentList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                RecruitmentList recruitmentList = results?.resultList[0]
                return recruitmentList
            }
        }
        return null
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return RecruitmentList.
     */
    @Transactional(readOnly = true)
    RecruitmentList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                RecruitmentList instance = results?.resultList[0]
                return instance
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
     * send recruitment list
     * @param GrailsParameterMap params
     * @return boolean
     */
    RecruitmentList sendList(GrailsParameterMap params) {
        //return recruitmentListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            RecruitmentList recruitmentList = RecruitmentList.get(params["id"])

            //return error if no request added to list
            if(recruitmentList?.recruitmentListEmployees?.size() == 0){
                recruitmentList.errors.reject("list.sendList.error")
                return recruitmentList
            }

            if (recruitmentList) {
                //to change the correspondenceListStatus to submitted when we send the recruitment list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.correspondenceList = recruitmentList
                correspondenceListStatus.receivingParty = recruitmentList?.receivingParty
                correspondenceListStatus.firm = recruitmentList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                if (correspondenceListStatus.save()) {
                    recruitmentList.currentStatus = correspondenceListStatus
                }
                //enter the manualIncomeNo when we send the  recruitment list
                recruitmentList.manualOutgoingNo = params.manualOutgoingNo

                try {
                    recruitmentList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (recruitmentList?.errors?.allErrors?.size() == 0) {
                        recruitmentList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return recruitmentList
            }
        } else {
            RecruitmentList recruitmentList = new RecruitmentList()
            recruitmentList.errors.reject('default.not.found.message', [messageSource.getMessage('recruitmentList.label', null, 'recruitmentList', LocaleContextHolder.getLocale())] as Object[], "This recruitmentList with ${params.id} not found")
            return recruitmentList
        }
    }

    /**
     * receive recruitment list
     * @param GrailsParameterMap params
     * @return boolean
     */
    RecruitmentList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return RecruitmentList is
            RecruitmentList recruitmentList = RecruitmentList.load(params["id"])
            if (recruitmentList) {
                try {
                    //to change the correspondenceListStatus to received when we received the recruitment list
                    // and change the to date to the date of receive RecruitmentList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['toDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = recruitmentList
                    correspondenceListStatus.receivingParty = recruitmentList?.receivingParty
                    correspondenceListStatus.firm = recruitmentList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        recruitmentList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we receive the recruitment list
                    recruitmentList.manualIncomeNo = params.manualIncomeNo
                    //save the recruitment list changes
                    recruitmentList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (recruitmentList?.errors?.allErrors?.size() == 0) {
                        recruitmentList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return recruitmentList
            }
        } else {
            RecruitmentList recruitmentList = new RecruitmentList()
            recruitmentList.errors.reject('default.not.found.message', [messageSource.getMessage('recruitmentList.label', null, 'recruitmentList', LocaleContextHolder.getLocale())] as Object[], "This recruitmentList with ${params.id} not found")
            return recruitmentList
        }
    }

    /**
     * close dispatch list
     * @param GrailsParameterMap params
     * @return boolean
     */
    RecruitmentList closeList(GrailsParameterMap params) {
        //return recruitmentListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            RecruitmentList recruitmentList = RecruitmentList.get(params["id"])
            if (recruitmentList) {
                //to change the correspondenceListStatus to submitted when we close the dispatch list
                // and change the from date when closing the list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = recruitmentList
                correspondenceListStatus.receivingParty = recruitmentList?.receivingParty
                correspondenceListStatus.firm = recruitmentList?.firm

                if (RecruitmentListEmployee.countByRecruitmentListAndRecordStatus(recruitmentList, EnumListRecordStatus.NEW) > 0) {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                }else {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                }


                try {
                    correspondenceListStatus.save(failOnError: true, flush: true)
                    recruitmentList?.currentStatus = correspondenceListStatus
                    //save the disciplinary list changes
                    recruitmentList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (recruitmentList?.errors?.allErrors?.size() == 0) {
                        recruitmentList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return recruitmentList
            }
        } else {
            RecruitmentList recruitmentList = new RecruitmentList()
            recruitmentList.errors.reject('default.not.found.message', [messageSource.getMessage('recruitmentList.label', null, 'recruitmentList', LocaleContextHolder.getLocale())] as Object[], "This recruitmentList with ${params.id} not found")
            return recruitmentList
        }
    }

    /**
     * to change the recruitment List employee status to EMPLOYED in the receive recruitment list
     * @param GrailsParameterMap params
     * @return map
     */
    Map changeApplicantToEmployed(GrailsParameterMap params) {
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        //to get list of applicant ids
        List checkedApplicantIdList = params.listString("check_applicantTableInRecruitmentList")
        params.remove("check_applicantTableInRecruitmentList")
        if (checkedApplicantIdList) {
            List<RecruitmentListEmployee> recruitmentListEmployees = RecruitmentListEmployee.executeQuery("from RecruitmentListEmployee emp where applicant_id in (:checkedApplicantIdList)", [checkedApplicantIdList: checkedApplicantIdList])
            if (recruitmentListEmployees) {
                //loop on each recruitmentList employee and update the status to be EMPLOYED
                recruitmentListEmployees?.each { RecruitmentListEmployee recruitmentListEmployee ->
                    try {
                        //save the note if its used:
                        //create note instance for recruitmentListEmployeeNote:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            RecruitmentListEmployeeNote note = new RecruitmentListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    recruitmentListEmployee: recruitmentListEmployee,
                            );
                            if (!note.validate()) {
                                note.errors.fieldErrors.each { FieldError fieldError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(fieldError?.code, fieldError?.arguments, fieldError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                note.errors.globalErrors.each { ObjectError objectError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                saved = false
                            } else {
                                //add note to join
                                recruitmentListEmployee.addToRecruitmentListEmployeeNotes(note);
                            }
                        }//if note
                        if (!errors) {
                            //change the recruitment list employee record status
                            recruitmentListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                            //create new status history for applicant with Employed status
                            ApplicantStatusHistory applicantStatusHistory = new ApplicantStatusHistory(applicant: recruitmentListEmployee?.applicant, fromDate: PCPUtils.parseZonedDateTime(params['noteDate']) ?: ZonedDateTime.now(), applicantStatus: EnumApplicantStatus.EMPLOYED)
                            if (!applicantStatusHistory.validate()) {
                                applicantStatusHistory.errors.fieldErrors.each { FieldError fieldError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(fieldError?.code, fieldError?.arguments, fieldError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                applicantStatusHistory.errors.globalErrors.each { ObjectError objectError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                saved = false
                            } else {
                                //set the current applicant status to be Employed.
                                recruitmentListEmployee?.applicant?.applicantCurrentStatus = applicantStatusHistory
                                recruitmentListEmployee?.save(flush: true, failOnError: true)
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        transactionStatus.setRollbackOnly()
                        saved = false
                        if (!errors) {
                            errors << messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())
                        }
                    }
                }
            } else {//second if to check if applicants are found
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                saved = false
            }
        } else {//first if to check if ids passed
            errors << [field  : "global",
                       message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
            saved = false
        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("saved", saved)
        return dataMap
    }


    /**
     * to change the recruitment List employee status to NOT_EMPLOYED in the receive recruitment list
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeApplicantToRejected(GrailsParameterMap params) {
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        //to get list of applicant ids
        List checkedApplicantIdList = params.listString("check_applicantTableInRecruitmentList")
        params.remove("check_applicantTableInRecruitmentList")

        if (checkedApplicantIdList) {
            List<RecruitmentListEmployee> recruitmentListEmployees = RecruitmentListEmployee.executeQuery("from RecruitmentListEmployee emp where applicant_id in (:checkedApplicantIdList)", [checkedApplicantIdList: checkedApplicantIdList])
            if (recruitmentListEmployees) {
                //loop on each recruitmentList employee and update the status to be EMPLOYED
                recruitmentListEmployees?.each { RecruitmentListEmployee recruitmentListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            RecruitmentListEmployeeNote note = new RecruitmentListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    recruitmentListEmployee: recruitmentListEmployee,
                            );
                            if (!note.validate()) {
                                note.errors.fieldErrors.each { FieldError fieldError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(fieldError?.code, fieldError?.arguments, fieldError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                note.errors.globalErrors.each { ObjectError objectError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                saved = false
                            } else {
                                //add note to join instance
                                recruitmentListEmployee.addToRecruitmentListEmployeeNotes(note);

                                //add the rejection reason per employee list
                                recruitmentListEmployee?.applicant?.rejectionReason = note?.note? note?.note : note?.orderNo
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }
                        if (!errors) {
                            //change the recruitment list employee record status
                            recruitmentListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                            //create new status history for applicant with Employed status
                            ApplicantStatusHistory applicantStatusHistory = new ApplicantStatusHistory(applicant: recruitmentListEmployee?.applicant, fromDate: PCPUtils.parseZonedDateTime(params['noteDate']) ?: ZonedDateTime.now(), applicantStatus: EnumApplicantStatus.NOT_EMPLOYED)
                            if (!applicantStatusHistory.validate()) {
                                applicantStatusHistory.errors.fieldErrors.each { FieldError fieldError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(fieldError?.code, fieldError?.arguments, fieldError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                applicantStatusHistory.errors.globalErrors.each { ObjectError objectError ->
                                    errors << [field  : "global",
                                               message: messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale())]
                                }
                                saved = false
                            } else {
                                //set the current applicant status to be not employed
                                recruitmentListEmployee?.applicant?.applicantCurrentStatus = applicantStatusHistory

                                if (!recruitmentListEmployee?.applicant?.validate()) {
                                    recruitmentListEmployee?.applicant?.errors.fieldErrors.each { FieldError fieldError ->
                                        errors << [field  : "global",
                                                   message: messageSource.getMessage(fieldError?.code, fieldError?.arguments, fieldError?.defaultMessage, LocaleContextHolder.getLocale())]
                                    }
                                    recruitmentListEmployee?.applicant?.errors.globalErrors.each { ObjectError objectError ->
                                        errors << [field  : "global",
                                                   message: messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale())]
                                    }
                                    saved = false
                                }else {
                                    recruitmentListEmployee?.save(flush: true, failOnError: true)
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        transactionStatus.setRollbackOnly()
                        saved = false
                        if (!errors) {
                            errors << messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())
                        }
                    }
                }
            } else {//second if to check if applicants are found
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                saved = false
            }
        } else {//first if to check if ids passed
            errors << [field  : "global",
                       message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
            saved = false
        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("saved", saved)
        return dataMap
    }



    /**
     * to add applicants to recruitment list
     * @param GrailsParameterMap params
     * @return boolean
     */
    RecruitmentList addApplicants(GrailsParameterMap params) {
        RecruitmentList recruitmentList = RecruitmentList.load(params["recruitmentListId"])
        //to get list of applicant ids
        List checkedApplicantIdList = params.listString("checked_applicantIdsList");
        params.remove("checked_applicantIdsList");
        if (checkedApplicantIdList.size() > 0) {
            //retrieve the selected recruitments:
            List<Applicant> applicants = Applicant.executeQuery("from Applicant emp where id in (:checkedApplicantIdList)", [checkedApplicantIdList: checkedApplicantIdList])
            if (applicants) {
                RecruitmentListEmployee recruitmentListEmployee
                ApplicantStatusHistory applicantStatusHistory
                try {
                    applicants.each { Applicant applicant ->
                        //create new recruitment list employee and add the applicant to it as the relation between recruitment list employee and applicant is one to one
                        recruitmentListEmployee = new RecruitmentListEmployee()

                        // set toDate current applicant status
                        applicant.applicantCurrentStatus.toDate = ZonedDateTime.now();
                        applicant.applicantCurrentStatus.save(flush: true, failOnError: true)

                        // change applicant status
                        applicantStatusHistory = new ApplicantStatusHistory(applicant: applicant, fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), applicantStatus: EnumApplicantStatus.ADD_TO_LIST)
                        applicant.applicantCurrentStatus = applicantStatusHistory

                        recruitmentListEmployee.applicant = applicant
                        recruitmentListEmployee.recordStatus = EnumListRecordStatus.NEW
                        recruitmentListEmployee.recruitmentList = recruitmentList
                        //add the recruitment list employee to recruitment list
                        recruitmentList.addToRecruitmentListEmployees(recruitmentListEmployee)
                    }
                    //save the recruitment list changes
                    recruitmentList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (recruitmentList?.errors?.allErrors?.size() == 0) {
                        recruitmentList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            recruitmentList.errors.reject("recruitmentList.notSelectedApplicant.error")
        }
        return recruitmentList
    }
}
