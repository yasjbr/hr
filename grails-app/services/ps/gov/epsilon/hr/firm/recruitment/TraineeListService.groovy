package ps.gov.epsilon.hr.firm.recruitment

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.lookups.TrainingRejectionReason
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.LocationAddressUtil
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create trainee list
 * <h1>Usage</h1>
 * -this service is used to create trainee list
 * <h1>Restriction</h1>
 * -need a firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class TraineeListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    ManageLocationService manageLocationService
    LocationService locationService

    //to get the value of requisition status
    public static currentStatusValue = { cService, TraineeList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, TraineeList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../traineeList/manageTraineeList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set allowanceListEmployeeIds = params.listString("traineeListEmployee.id")
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return TraineeList.createCriteria().list(max: max, offset: offset) {
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
                if (allowanceListEmployeeIds) {
                    traineeListEmployee {
                        inList("id", allowanceListEmployeeIds)
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
        PagedList pagedList = customSearch(params)
        if (pagedList) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: pagedList?.resultList?.trainingLocationId))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList
            LocationDTO locationDTO
            pagedList?.resultList?.each { TraineeList traineeList ->
                locationDTO = locationList?.find { it?.id == traineeList?.trainingLocationId }
                traineeList.transientData.put("locationDTO", locationDTO)
            }
            if (pagedList) {
                return pagedList
            }
        }
    }

    /**
     * custom search to find the number of applicants in the trainee  list in one select statement for performance issue
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

        //the query to retrieve the list details, num of trainees in the list, the send date, and the current list status
        String query = "FROM trainee_list al  LEFT JOIN " +
                "  (SELECT ale.trainee_list_id ,count(ale.id) no_of_employee" +
                "  FROM trainee_list_employee ale " +
                "  group by ale.trainee_list_id ) b" +
                "  on al.id= b.trainee_list_id , correspondence_list_status cls,correspondence_list cl" +
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
                    al.training_location_id,
                    al.unstructured_location,
                    COALESCE( NULLIF(al.from_date_datetime,'0003-03-03 03:03:03') ) as end_date,
                    COALESCE( NULLIF(al.to_date_datetime,'0003-03-03 03:03:03') ) as start_date, 
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

        List<TraineeList> results = []
        // Transform resulting rows to a map with key organisationName.
        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[3])
            ZonedDateTime lastUpdatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[19])
            ZonedDateTime toDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[4])
            ZonedDateTime fromDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[5])

            ZonedDateTime startDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[16])
            ZonedDateTime endDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[17])

            ZonedDateTime sendDateZonedDateTime
            ZonedDateTime receiveDateZonedDateTime
            if (resultRow[6]) {
                sendDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[6])
            }
            if (resultRow[7]) {
                receiveDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[7])
            }

            TraineeList traineeList = new TraineeList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[18],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trainingLocationId: resultRow[14],
                    unstructuredLocation: resultRow[15],
                    fromDate: startDateZonedDateTime,
                    toDate: endDateZonedDateTime,
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            traineeList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            traineeList.currentStatus = currentStatus
            results.add(traineeList)
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
     * @return traineeList.
     */
    TraineeList save(GrailsParameterMap params) {
        TraineeList traineeListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            traineeListInstance = TraineeList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (traineeListInstance.version > version) {
                    traineeListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('traineeList.label', null, 'traineeList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this traineeList while you were editing")
                    return traineeListInstance
                }
            }
            if (!traineeListInstance) {
                traineeListInstance = new TraineeList()
                traineeListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('traineeList.label', null, 'traineeList', LocaleContextHolder.getLocale())] as Object[], "This traineeList with ${params.id} not found")
                return traineeListInstance
            }
        } else {
            traineeListInstance = new TraineeList()
        }
        try {
            traineeListInstance.properties = params;

            CorrespondenceListStatus correspondenceListStatus
            if (!params.id) {
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: EnumReceivingParty.SARAYA, firm: traineeListInstance.firm)
                traineeListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            LocationCommand locationCommand = manageLocationService?.saveLocation(params)
            if (locationCommand?.id) {
                traineeListInstance.trainingLocationId = locationCommand?.id //assign reference id of location from core
            }

            traineeListInstance.save(flush: true, failOnError: true);
            //save the current status:
            if (correspondenceListStatus?.id && traineeListInstance?.id) {
                traineeListInstance?.currentStatus = correspondenceListStatus
                traineeListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            traineeListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return traineeListInstance
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

            TraineeList instance = TraineeList.get(id)
            //to be able to delete an trainee list when status is created
            if (instance?.currentStatus.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED]) {
                //to apply virtual delete, we change tracking info's status to deleted
                if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                    instance?.trackingInfo.status = GeneralStatus.DELETED
                    instance.save()
                    deleteBean.status = true
                }
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('traineeList.deleteMessage.label')
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
     * @return traineeList.
     */
    @Transactional(readOnly = true)
    TraineeList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                //get remoting value of location:
                TraineeList traineeList = results[0]
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return TraineeList.
     */
    @Transactional(readOnly = true)
    TraineeList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                TraineeList traineeList = results?.resultList[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: traineeList?.trainingLocationId))
                List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList
                LocationDTO locationDTO = locationList?.find { it?.id == traineeList?.trainingLocationId }
                traineeList?.transientData.put("locationDTO", locationDTO)
                traineeList?.transientData.put("location", LocationAddressUtil.renderLocation(locationDTO, traineeList.unstructuredLocation));
                return traineeList
            }
        }
        return null
    }

    /**
     * send trainee list
     * @param GrailsParameterMap params
     * @return boolean
     */
    TraineeList sendList(GrailsParameterMap params) {
        //return traineeListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            TraineeList traineeList = TraineeList.get(params["id"])

            //return error if no request added to list
            if (traineeList?.traineeListEmployees?.size() == 0) {
                traineeList.errors.reject("list.sendList.error")
                return traineeList
            }

            if (traineeList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the trainee list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.correspondenceList = traineeList
                    correspondenceListStatus.receivingParty = traineeList?.receivingParty
                    correspondenceListStatus.firm = traineeList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        traineeList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the  trainee list
                    traineeList.manualOutgoingNo = params.manualOutgoingNo

                    traineeList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (traineeList?.errors?.allErrors?.size() == 0) {
                        traineeList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return traineeList
            }
        } else {
            TraineeList traineeList = new TraineeList()
            traineeList.errors.reject('default.not.found.message', [messageSource.getMessage('traineeList.label', null, 'traineeList', LocaleContextHolder.getLocale())] as Object[], "This traineeList with ${params.id} not found")
            return traineeList
        }
    }

    /**
     * receive trainee list
     * @param GrailsParameterMap params
     * @return boolean
     */
    TraineeList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return TraineeList is
            TraineeList traineeList = TraineeList.load(params["id"])
            if (traineeList) {
                try {
                    //to change the correspondenceListStatus to received when we received the trainee list
                    // and change the to date to the date of receive TraineeList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['toDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = traineeList
                    correspondenceListStatus.receivingParty = traineeList?.receivingParty
                    correspondenceListStatus.firm = traineeList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        traineeList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we receive the trainee list
                    traineeList.manualIncomeNo = params.manualIncomeNo
                    //save the trainee list changes
                    traineeList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (traineeList?.errors?.allErrors?.size() == 0) {
                        traineeList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return traineeList
            }
        } else {
            TraineeList traineeList = new TraineeList()
            traineeList.errors.reject('default.not.found.message', [messageSource.getMessage('traineeList.label', null, 'traineeList', LocaleContextHolder.getLocale())] as Object[], "This traineeList with ${params.id} not found")
            return traineeList
        }
    }

    /**
     * close trainee list
     * @param GrailsParameterMap params
     * @return boolean
     */
    TraineeList closeList(GrailsParameterMap params) {
        //return traineeListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            TraineeList traineeList = TraineeList.get(params["id"])
            if (traineeList) {

                //to change the correspondenceListStatus to submitted when we close the trainee list
                // and change the from date when closing the list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = traineeList
                correspondenceListStatus.receivingParty = traineeList?.receivingParty
                correspondenceListStatus.firm = traineeList?.firm

                if (TraineeListEmployee.countByTraineeListAndRecordStatus(traineeList, EnumListRecordStatus.NEW) > 0) {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED

                } else {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                }
                try {

                    if (correspondenceListStatus.save()) {
                        traineeList.currentStatus = correspondenceListStatus
                    }

                    //save the trainee list changes
                    traineeList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (traineeList?.errors?.allErrors?.size() == 0) {
                        traineeList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return traineeList
            }
        } else {
            TraineeList traineeList = new TraineeList()
            traineeList.errors.reject('default.not.found.message', [messageSource.getMessage('traineeList.label', null, 'traineeList', LocaleContextHolder.getLocale())] as Object[], "This traineeList with ${params.id} not found")
            return traineeList
        }
    }

    /**
     * to change the trainee List employee status to TRAINING_PASSED in the receive trainee list
     * @param GrailsParameterMap params
     * @return map
     */
    Map changeApplicantToTrainingPassed(GrailsParameterMap params) {
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        //to get list of applicant ids
        List checkedApplicantIdList = params.listString("check_applicantTableInTraineeList")
        params.remove("check_applicantTableInTraineeList")
        if (checkedApplicantIdList) {
            List<TraineeListEmployee> traineeListEmployees = TraineeListEmployee.executeQuery("from TraineeListEmployee emp where id in (:checkedApplicantIdList)", [checkedApplicantIdList: checkedApplicantIdList])
            if (traineeListEmployees) {
                //loop on each traineeList employee and update the status to be EMPLOYED
                traineeListEmployees?.each { TraineeListEmployee traineeListEmployee ->
                    try {
                        //save the note if its used:
                        //create note instance for traineeListEmployeeNote:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            TrainingListEmployeeNote note = new TrainingListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    traineeListEmployee: traineeListEmployee,
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
                                traineeListEmployee.addToTrainingListEmployeeNotes(note);
                            }
                        }//if note
                        if (!errors) {

                            //create new status history for applicant with Employed status
                            ApplicantStatusHistory applicantStatusHistory = new ApplicantStatusHistory(applicant: traineeListEmployee?.applicant, fromDate: PCPUtils.parseZonedDateTime(params['noteDate']) ?: ZonedDateTime.now(), applicantStatus: EnumApplicantStatus.TRAINING_PASSED)
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
                                traineeListEmployee?.applicant?.applicantCurrentStatus = applicantStatusHistory
                                //change the trainee list employee record status
                                traineeListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                                traineeListEmployee?.mark = params["mark"]
                                traineeListEmployee?.trainingEvaluation = params["trainingEvaluation"] ? ps.gov.epsilon.hr.enums.v1.EnumTrainingEvaluation.valueOf(params["trainingEvaluation"].toString()) : null
                                traineeListEmployee.save(flush: true, failOnError: true)
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
     * to change the trainee List employee status to NOT_EMPLOYED in the receive trainee list
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeApplicantToRejected(GrailsParameterMap params) {
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        //to get list of applicant ids
        List checkedApplicantIdList = params.listString("check_applicantTableInTraineeList")
        params.remove("check_applicantTableInTraineeList")

        if (checkedApplicantIdList) {
            List<TraineeListEmployee> traineeListEmployees = TraineeListEmployee.executeQuery("from TraineeListEmployee emp where id in (:checkedApplicantIdList)", [checkedApplicantIdList: checkedApplicantIdList])
            if (traineeListEmployees) {
                TrainingRejectionReason trainingRejectionReason = TrainingRejectionReason.get(params["trainingRejectionReason"])
                //loop on each traineeList employee and update the status to be EMPLOYED
                traineeListEmployees?.each { TraineeListEmployee traineeListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            TrainingListEmployeeNote note = new TrainingListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    traineeListEmployee: traineeListEmployee,
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
                                traineeListEmployee.addToTrainingListEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }
                        if (!errors) {
                            //change the trainee list employee record status
                            traineeListEmployee?.recordStatus = EnumListRecordStatus.REJECTED
                            //add the rejection reason per employee list
                            traineeListEmployee?.trainingRejectionReason = trainingRejectionReason
                            //create new status history for applicant with Employed status
                            ApplicantStatusHistory applicantStatusHistory = new ApplicantStatusHistory(applicant: traineeListEmployee?.applicant, fromDate: PCPUtils.parseZonedDateTime(params['noteDate']) ?: ZonedDateTime.now(), applicantStatus: EnumApplicantStatus.TRAINING_FAILED)
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
                                //set the current applicant status to be training passed.
                                traineeListEmployee?.applicant?.applicantCurrentStatus = applicantStatusHistory
                                traineeListEmployee?.save(flush: true, failOnError: true)
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
     * to add applicants to trainee list
     * @param GrailsParameterMap params
     * @return boolean
     */
    TraineeList addApplicants(GrailsParameterMap params) {
        TraineeList traineeList = TraineeList.load(params["traineeListId"])
        //to get list of applicant ids
        List checkedApplicantIdList = params.listString("checked_applicantIdsList");
        params.remove("checked_applicantIdsList");
        if (checkedApplicantIdList.size() > 0) {
            //retrieve the selected trainees:
            List<Applicant> applicants = Applicant.executeQuery("from Applicant emp where id in (:checkedApplicantIdList)", [checkedApplicantIdList: checkedApplicantIdList])
            if (applicants) {
                TraineeListEmployee traineeListEmployee
                ApplicantStatusHistory applicantStatusHistory
                try {
                    applicants.each { Applicant applicant ->
                        //create new trainee list employee and add the applicant to it as the relation between trainee list employee and applicant is one to one
                        traineeListEmployee = new TraineeListEmployee()

                        // set toDate current applicant status
                        applicant.applicantCurrentStatus.toDate = ZonedDateTime.now();
                        applicant.applicantCurrentStatus.save(flush: true, failOnError: true)

                        // change applicant status
                        applicantStatusHistory = new ApplicantStatusHistory(applicant: applicant, fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), applicantStatus: EnumApplicantStatus.ADD_TO_LIST)
                        applicant.applicantCurrentStatus = applicantStatusHistory

                        traineeListEmployee.applicant = applicant
                        traineeListEmployee?.applicant.traineeListEmployee = traineeListEmployee
                        traineeListEmployee.recordStatus = EnumListRecordStatus.NEW
                        traineeListEmployee.traineeList = traineeList
                        //add the trainee list employee to trainee list
                        traineeList?.addToTraineeListEmployees(traineeListEmployee)
                    }

                    //save the trainee list changes
                    traineeList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (traineeList?.errors?.allErrors?.size() == 0) {
                        traineeList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            traineeList.errors.reject("list.request.notSelected.error");
        }
        return traineeList
    }

}
