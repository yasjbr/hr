package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.epsilon.attach.AttachmentService
import ps.epsilon.attach.EnumAttachmentMessage
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

import java.sql.Timestamp
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -manage all disciplinary list transactions and get data from domain
 * <h1>Usage</h1>
 * -any service to get disciplinary list info or search about disciplinary
 * <h1>Restriction</h1>
 * -must connect with pcore application to get unit and location information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DisciplinaryListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    AttachmentService attachmentService

    //to get the value of requisition status
    public static currentStatusValue = { cService, DisciplinaryList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, DisciplinaryList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../disciplinaryList/manageDisciplinaryList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: false, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "code", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: getListName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trackingInfo.dateCreatedUTC", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.sendDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "manualOutgoingNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.numberOfCompetitorsValue", type: "Integer", source: 'domain'],
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
        Set disciplinaryRecordJudgmentIds = params.listString("disciplinaryRecordJudgment.id")
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
        //search about list with status:
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus currentStatusCorrespondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"] as String) : null
        ZonedDateTime dateCreated = PCPUtils.parseZonedDateTime(params['dateCreated'])
        ZonedDateTime sendDate = PCPUtils.parseZonedDateTime(params['sendDate'])


        return DisciplinaryList.createCriteria().list(max: max, offset: offset) {
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

                if (disciplinaryRecordJudgmentIds) {
                    disciplinaryRecordJudgment {
                        inList("id", disciplinaryRecordJudgmentIds)
                    }
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
                if (currentStatusCorrespondenceListStatus || sendDate) {
                    currentStatus {
                        if (currentStatusCorrespondenceListStatus) {
                            eq("correspondenceListStatus", currentStatusCorrespondenceListStatus)
                        }
                        if (sendDate) {
                            and {
                                eq("fromDate", sendDate)
                                eq("correspondenceListStatus", EnumCorrespondenceListStatus.SUBMITTED)
                            }
                        }
                    }
                }
                if (dateCreated) {
                    eq("trackingInfo.dateCreatedUTC", dateCreated)
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
     * to search about remoting values from the core
     */
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedResultList = this.customSearch(params)
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return DisciplinaryList.
 */
    DisciplinaryList save(GrailsParameterMap params) {
        DisciplinaryList disciplinaryListInstance
        String resultAttachment
        //to decode encrypted id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            disciplinaryListInstance = DisciplinaryList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (disciplinaryListInstance.version > version) {
                    disciplinaryListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('disciplinaryList.label', null, 'disciplinaryList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this disciplinaryList while you were editing")
                    return disciplinaryListInstance
                }
            }
            if (!disciplinaryListInstance) {
                disciplinaryListInstance = new DisciplinaryList()
                disciplinaryListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('disciplinaryList.label', null, 'disciplinaryList', LocaleContextHolder.getLocale())] as Object[], "This disciplinaryList with ${params.id} not found")
                return disciplinaryListInstance
            }
        } else {
            disciplinaryListInstance = new DisciplinaryList()
        }
        try {

            disciplinaryListInstance.properties = params;
            disciplinaryListInstance.save(flush: true, failOnError: true);

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
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = ZonedDateTime.now()
                correspondenceListStatus.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                correspondenceListStatus.correspondenceListStatus = listStatus
                correspondenceListStatus.receivingParty = EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = disciplinaryListInstance.firm
                correspondenceListStatus.correspondenceList = disciplinaryListInstance
                disciplinaryListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
                disciplinaryListInstance.currentStatus = correspondenceListStatus
            }

            disciplinaryListInstance.save(flush: true, failOnError: true)

            //save attachment after save
            params.parentId = disciplinaryListInstance?.id
            resultAttachment = attachmentService.saveTempFiles(params)
            if(!resultAttachment?.contains(EnumAttachmentMessage.FILE_DONE.value()) && !resultAttachment?.contains(EnumAttachmentMessage.NO_ATTACHMENT.value())){
                throw new Exception("FILES_NOT_SAVE")
            }

        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            if(!resultAttachment?.contains(EnumAttachmentMessage.FILE_DONE.value()) && !resultAttachment?.contains(EnumAttachmentMessage.NO_ATTACHMENT.value())){
                disciplinaryListInstance.errors.reject(resultAttachment)
            }
            if (!disciplinaryListInstance.hasErrors()) {
                disciplinaryListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return disciplinaryListInstance
    }

    /**
     * to add disciplinaryRecordJudgment disciplinaryList
     * @param GrailsParameterMap params
     * @return Map
     */
    DisciplinaryList addDisciplinaryRecordJudgment(GrailsParameterMap params) {

        DisciplinaryList disciplinaryList = DisciplinaryList.load(params["disciplinaryListId"])
        //to get list of request ids
        List checkedRecordJudgmentIdList = params.listString("checked_recordJudgmentIdsList");

        //retrieve the selected judgment:
        List<DisciplinaryRecordJudgment> disciplinaryRecordJudgmentList = DisciplinaryRecordJudgment.executeQuery("from DisciplinaryRecordJudgment drj where drj.id in (:checkedRecordJudgmentIdList)", [checkedRecordJudgmentIdList: checkedRecordJudgmentIdList])

        try {
            disciplinaryRecordJudgmentList.each {DisciplinaryRecordJudgment disciplinaryRecordJudgment ->
                //add to list
                disciplinaryRecordJudgment.judgmentStatus = EnumJudgmentStatus.APPROVED
                disciplinaryRecordJudgment.disciplinaryRecordsList = disciplinaryList
                disciplinaryRecordJudgment.save(flush:true,failOnError: true)
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            disciplinaryList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }


        return disciplinaryList
    }


    /**
     * send disciplinary list
     * @param GrailsParameterMap params
     * @return boolean
     */
    DisciplinaryList sendList(GrailsParameterMap params) {
        //return disciplinaryListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            //return the corsponding list:
            DisciplinaryList disciplinaryList = DisciplinaryList.get(params["id"])

            //return error if no request added to list
            if(disciplinaryList?.disciplinaryRecordJudgment?.size() == 0){
                disciplinaryList.errors.reject("list.sendList.error")
                return disciplinaryList
            }


            if (disciplinaryList) {
                //to change the correspondenceListStatus to submitted when we send the disciplinary list
                // and change the from date to the date of sending disciplinary list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = disciplinaryList
                correspondenceListStatus.receivingParty = disciplinaryList?.receivingParty
                correspondenceListStatus.firm = disciplinaryList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED

                disciplinaryList.addToCorrespondenceListStatuses(correspondenceListStatus)
                disciplinaryList.currentStatus = correspondenceListStatus

                //enter the manualIncomeNo when we send the disciplinary list
                disciplinaryList.manualOutgoingNo = params.manualOutgoingNo

                List<DisciplinaryRecordJudgment> disciplinaryRecordJudgmentList = disciplinaryList?.disciplinaryRecordJudgment?.toList()
                if (disciplinaryRecordJudgmentList) {
                    //loop in all disciplinaryRecordJudgment in disciplinary list to change the status of the request
                    disciplinaryRecordJudgmentList.each { DisciplinaryRecordJudgment disciplinaryRecordJudgment ->
                        disciplinaryRecordJudgment.judgmentStatus = EnumJudgmentStatus.APPROVED
                        disciplinaryRecordJudgment.save(flush: true, failOnError: true);
                    }
                }
                try {
                    //save the disciplinary list changes
                    disciplinaryList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (disciplinaryList?.hasErrors()) {
                        disciplinaryList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return disciplinaryList
            }
        } else {
            DisciplinaryList disciplinaryListInstance = new DisciplinaryList()
            disciplinaryListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('disciplinaryList.label', null, 'disciplinaryList', LocaleContextHolder.getLocale())] as Object[], "This disciplinaryList with ${params.id} not found")
            return disciplinaryListInstance
        }

    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<DisciplinaryList> disciplinaryListList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            disciplinaryListList = DisciplinaryList.findAllByIdInList(ids)
            disciplinaryListList.each { DisciplinaryList disciplinaryList ->
                if (disciplinaryList?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete disciplinaryList
                    disciplinaryList.trackingInfo.status = GeneralStatus.DELETED
                    disciplinaryList.save(flush: true)
                }
            }

            //check that at least on record is set to deleted
            if (disciplinaryListList) {
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return DisciplinaryList.
 */
    @Transactional(readOnly = true)
    DisciplinaryList getInstance(GrailsParameterMap params) {

        //to decode encrypted id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                DisciplinaryList disciplinaryList = results?.resultList[0]
                return disciplinaryList
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
    Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * custom search to find the number of requests in the disciplinary  list in one select statement for performance issue
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
        String id = params["id"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        String code = params["code"]
        String name = params["name"]
        Long firmId = PCPSessionUtils.getValue("firmId")
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus correspondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"]) : null
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String dateCreated = params['dateCreated']
        String sendDate = params['sendDate']
        Integer numberOfCompetitorsValue = params.int("numberOfCompetitorsValue")
        Timestamp fromSendDate = PCPUtils.parseTimestamp(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestamp(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])

        Map sqlParamsMap = [:]

        String query = " FROM disciplinary_list al " +
                " LEFT JOIN (SELECT drj.disciplinary_records_list_id ,count(drj.id) no_of_judgment" +
                " FROM disciplinary_record_judgment drj group by drj.disciplinary_records_list_id ) b" +
                " on al.id= b.disciplinary_records_list_id , correspondence_list_status cls,correspondence_list cl" +
                " LEFT JOIN (SELECT * FROM correspondence_list_status  sb WHERE  sb.correspondence_list_status='${EnumCorrespondenceListStatus.SUBMITTED}') sbl" +
                " ON  sbl.correspondence_List_id=cl.id" +
                " WHERE cl.id = al.id " +
                " AND cls.id = cl.current_status_id " +
                " AND cl.status ='${GeneralStatus.ACTIVE}' "

        String orderByQuery = ""

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
            query = query + " and cl.id = :idParam  "
            sqlParamsMap.put("idParam", id)
        }
        if (firmId) {
            query = query + " and cl.firm_id = :firmIdParam  "
            sqlParamsMap.put("firmIdParam", firmId)
        }
        if (code) {
            query = query + " and cl.code like :codeParam  "
            sqlParamsMap.put("codeParam", "%" + code + "%")
        }
        if (name) {
            query = query + " and cl.name like :nameParam  "
            sqlParamsMap.put("nameParam", "%" + name + "%")
        }
        if (correspondenceListStatus) {
            query = query + " and cls.correspondence_list_status = :correspondenceListStatusParam  "
            sqlParamsMap.put("correspondenceListStatusParam", correspondenceListStatus.toString())
        }
        if (manualIncomeNo) {
            query = query + " and cl.manual_income_no like :manualIncomeNoParam  "
            sqlParamsMap.put("manualIncomeNoParam", "%" + manualIncomeNo + "%")
        }
        if (manualOutgoingNo) {
            query = query + " and cl.manual_outgoing_no like :manualOutgoingNoParam  "
            sqlParamsMap.put("manualOutgoingNoParam", "%" + manualOutgoingNo + "%")
        }
        if (dateCreated) {
            query = query + " and to_char(cl.date_created,'dd/MM/yyyy')  = :trackingInfoDateCreatedUTC "
            sqlParamsMap.put("trackingInfoDateCreatedUTC", dateCreated)
        }
        if (fromDateCreated) {
            query = query + " and cl.date_created >= :fromDateCreated "
            sqlParamsMap.put("fromDateCreated", fromDateCreated)
        }
        if (toDateCreated) {
            query = query + " and cl.date_created <= :toDateCreated "
            sqlParamsMap.put("toDateCreated", toDateCreated)
        }
        if (sendDate) {
            query = query + " and to_char(sbl.from_date_datetime,'dd/MM/yyyy')  = :sendDate "
            sqlParamsMap.put("sendDate", sendDate)
        }
        if (fromSendDate) {
            query = query + " and sbl.from_date_datetime >= :fromSendDate "
            sqlParamsMap.put("fromSendDate", fromSendDate)
        }
        if (toSendDate) {
            query = query + " and sbl.from_date_datetime <= :toSendDate "
            sqlParamsMap.put("toSendDate", toSendDate)
        }
        if (numberOfCompetitorsValue != null) {
            query = query + " and COALESCE(b.no_of_judgment,0) = :numberOfCompetitorsValueParam  "
            sqlParamsMap.put("numberOfCompetitorsValueParam", numberOfCompetitorsValue)
        }
        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("trackingInfo.dateCreatedUTC")) {
            orderByQuery += "ORDER BY cl.date_created ${dir}"
        } else if (columnName?.equalsIgnoreCase("fromDate")) {
            orderByQuery += "ORDER BY cls.from_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("manualincomeno")) {
            orderByQuery += "ORDER BY cl.manual_income_no  ${dir}"
        } else if (columnName?.equalsIgnoreCase("toDate")) {
            orderByQuery += "ORDER BY cls.to_date_datetime ${dir}"
        } else if (columnName?.equalsIgnoreCase("manualOutgoingNo")) {
            orderByQuery += "ORDER BY cl.manual_outgoing_no  ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.numberOfCompetitorsValue")) {
            orderByQuery += "ORDER BY b.no_of_judgment ${dir}"
        } else if (columnName?.equalsIgnoreCase("currentStatus.correspondenceListStatus")) {
            orderByQuery += "ORDER BY cls.correspondence_list_status  ${dir}"
        }else if (columnName?.equalsIgnoreCase("transientData.sendDate")) {
            orderByQuery += "ORDER BY sbl.from_date_datetime  ${dir}"
        }else if (columnName) {
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
                    cl.receiving_party,
                    COALESCE( NULLIF(cl.date_created,'0003-03-03 03:03:03') ) as date_created,
                    COALESCE( NULLIF(cls.to_date_datetime,'0003-03-03 03:03:03') ) as to_date_datetime,
                    COALESCE( NULLIF(cls.from_date_datetime,'0003-03-03 03:03:03') ) as from_date_datetime,
                    COALESCE( NULLIF(sbl.from_date_datetime,'0003-03-03 03:03:03') ) as send_date,
                    cl.manual_outgoing_no,
                    cls.correspondence_list_status,
                    cl.current_status_id,
                    COALESCE(no_of_judgment,0) as no_of_judgment,
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

        List<DisciplinaryList> results = []
        // Transform resulting rows to a map with key organisationName.
        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[4])
            ZonedDateTime lastUpdatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[13])
            ZonedDateTime toDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[5])
            ZonedDateTime fromDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[6])
            ZonedDateTime sendDateZonedDateTime
            if(resultRow[8]) {
                sendDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[9])
            }
            DisciplinaryList disciplinaryList = new DisciplinaryList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[12],
                    receivingParty: resultRow[3],
                    manualOutgoingNo: resultRow[8],
                    transientData: [sendDate: sendDateZonedDateTime,numberOfCompetitorsValue: resultRow[11]],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC],
            )
            disciplinaryList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[9].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[10]

            disciplinaryList.currentStatus = currentStatus
            results.add(disciplinaryList)
        }

        Integer totalCount = 0

        //get total count for all records
        if (results) {
            Query sqlCountQuery = session.createSQLQuery(""" SELECT count(al.id) """ + query)
            sqlParamsMap?.each {
                sqlCountQuery.setParameter(it.key.toString(), it.value)
            }
            final queryCountResults = sqlCountQuery.list()
            totalCount = new Integer(queryCountResults[0]?.toString())
        }

        return new PagedList(resultList: results, totalCount: totalCount)
    }
}