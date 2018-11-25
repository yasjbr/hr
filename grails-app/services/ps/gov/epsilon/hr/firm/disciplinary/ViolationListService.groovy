package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.dispatch.DispatchRequest
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import java.sql.Timestamp
import java.time.ZonedDateTime


/**
 * <h1>Purpose</h1>
 * -This service aims to manage the violation list for violation which should be sent to third party-
 * <h1>Usage</h1>
 * -insert violation requests to list-
 * - insert new violation to list-
 * - send list-
 * <h1>Restriction</h1>
 * - create should be manually
 * - edit is allowed if the list is NEW state only.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ViolationListService {

    MessageSource messageSource
    def formatService
    def sessionFactory

    //to get the value of requisition status
    public static currentStatusValue = { cService, ViolationList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, ViolationList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../violationList/manageViolationList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set violationListEmployeesIds = params.listString("violationListEmployees.id")
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        //search about list with status:
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus currentStatusCorrespondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"] as String) : null
        ZonedDateTime dateCreated = PCPUtils.parseZonedDateTime(params['dateCreated'])
        ZonedDateTime sendDate = PCPUtils.parseZonedDateTime(params['sendDate'])



        return ViolationList.createCriteria().list(max: max, offset: offset) {
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
                if (violationListEmployeesIds) {
                    violationListEmployees {
                        inList("id", violationListEmployeesIds)
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

    //to search about remoting values from the core
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedList = customSearch(params)
        if (pagedList) {
            return pagedList
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ViolationList.
     */
    ViolationList save(GrailsParameterMap params) {
        ViolationList violationListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            violationListInstance = ViolationList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (violationListInstance.version > version) {
                    violationListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('violationList.label', null, 'violationList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this violationList while you were editing")
                    return violationListInstance
                }
            }
            if (!violationListInstance) {
                violationListInstance = new ViolationList()
                violationListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('violationList.label', null, 'violationList', LocaleContextHolder.getLocale())] as Object[], "This violationList with ${params.id} not found")
                return violationListInstance
            }
        } else {
            violationListInstance = new ViolationList()
        }
        try {

            violationListInstance.properties = params;
            violationListInstance.save(flush: true, failOnError: true);

            /**
             * in CREATED phase: create new status for the list
             */
            CorrespondenceListStatus correspondenceListStatus
            EnumCorrespondenceListStatus listStatus= params.correspondenceListStatus?EnumCorrespondenceListStatus.valueOf(params.correspondenceListStatus):null
            if (!params.id && !listStatus) {
                //when create the list , its is CREATED phase:
                listStatus= EnumCorrespondenceListStatus.CREATED
            }
            if (listStatus) {
                correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = ZonedDateTime.now()
                correspondenceListStatus.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                correspondenceListStatus.correspondenceListStatus = listStatus
                correspondenceListStatus.receivingParty = EnumReceivingParty.SARAYA
                correspondenceListStatus.firm = violationListInstance.firm
                correspondenceListStatus.correspondenceList = violationListInstance
                violationListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
                violationListInstance.currentStatus = correspondenceListStatus
            }
            violationListInstance.save(flush: true, failOnError: true)
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            violationListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return violationListInstance
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
            ViolationList instance = ViolationList.get(id)
            //to be able to delete an violation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.violationListEmployees?.size() == 0) {
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
     * @return ViolationList.
     */
    @Transactional(readOnly = true)
    ViolationList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                ViolationList violationList = results?.resultList[0]
                return violationList
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
     * to add violation instance to violationList
     * @param GrailsParameterMap params
     * @return boolean
     */
    ViolationList addEmployeeViolationToList(GrailsParameterMap params) {
        ViolationList violationList = ViolationList.load(params["violationListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("violationTableCheckBoxValues");
        params.remove("violationTableCheckBoxValues");
        if (checkedRequestIdList.size() > 0) {
            //retrieve the selected violations:
            List<EmployeeViolation> employeeViolations = EmployeeViolation.executeQuery("from EmployeeViolation d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (employeeViolations) {
                ViolationListEmployee violationListEmployee
                try {
                    employeeViolations.each { EmployeeViolation violation ->
                        //create new allowance list employee and add the allowance Request
                        violationListEmployee = new ViolationListEmployee()
                        violationListEmployee.employeeViolation = violation
                        violationListEmployee.violationList = violationList
                        //add the allowance list employee to allowance list
                        violationList.addToViolationListEmployees(violationListEmployee)
                        violationListEmployee.employeeViolation.violationStatus=EnumViolationStatus.ADD_TO_LIST

                        if (violation?.note) {
                            ViolationListEmployeeNote note = new ViolationListEmployeeNote(
                                    noteDate: violation?.violationDate,
                                    note: violation?.note,
                                    orderNo: "",
                                    violationListEmployee: violationListEmployee,
                            );
                            if (!note.validate()) {
                                violationList.errors.addAllErrors(note.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                violationListEmployee.addToViolationListEmployeeNotes(note);
                            }
                        }

                    }
                    //save the violation list changes
                    violationList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (violationList?.errors?.allErrors?.size() == 0) {
                        violationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            violationList.errors.reject("list.request.notSelected.error")
        }
        return violationList
    }

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ViolationList sendList(GrailsParameterMap params) {
        //return violationListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            //return the corresponding list:
            ViolationList violationList = ViolationList.get(params["id"])

            //return error if no request added to list
            if(violationList?.violationListEmployees?.size() == 0){
                violationList.errors.reject("list.sendList.error")
                return violationList
            }

            if (violationList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = violationList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = violationList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        violationList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    violationList.manualOutgoingNo = params.manualOutgoingNo

                    List<ViolationListEmployee> violationListEmployees = ViolationListEmployee.findAllByViolationList(violationList)
                    if (violationListEmployees) {
                        //loop in all violationListEmployee in promotion list to change the status of the request
                        violationListEmployees.each { ViolationListEmployee violationListEmployee ->
                            violationListEmployee?.employeeViolation?.violationStatus = EnumViolationStatus.SENT_BY_LIST
                            violationListEmployee.save(flush: true);
                        }
                    }
                    //save the violation list changes
                    violationList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (violationList?.errors?.allErrors?.size() == 0) {
                        violationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return violationList
            }
        } else {
            ViolationList violationList = new ViolationList()
            violationList.errors.reject('default.not.found.message', [messageSource.getMessage('violationList.label', null, 'violationList', LocaleContextHolder.getLocale())] as Object[], "This violationList with ${params.id} not found")
            return violationList
        }
    }

    /**
     * close child list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ViolationList closeList(GrailsParameterMap params) {
        //return violationListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            ViolationList violationList = ViolationList.get(params["id"])
            //TODO: what is the close restrictions

            //to change the correspondenceListStatus to submitted when we close the child list
            // and change the from date when closing the list
            CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
            correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
            correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
            correspondenceListStatus.correspondenceList = violationList
            correspondenceListStatus.receivingParty = violationList?.receivingParty
            //params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
            correspondenceListStatus.firm = violationList?.firm
            correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
            if (correspondenceListStatus.save(failOnError:true)) {
                violationList.currentStatus = correspondenceListStatus
            }
            try {
                //save the violation list changes
                violationList.save(failOnError: true)
            } catch (Exception ex) {
                ex.printStackTrace()
                transactionStatus.setRollbackOnly()
                if (violationList?.errors?.allErrors?.size() == 0) {
                    violationList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
            return violationList
        } else {
            ViolationList violationList = new ViolationList()
            violationList.errors.reject('default.not.found.message', [messageSource.getMessage('violationList.label', null, 'violationList', LocaleContextHolder.getLocale())] as Object[], "This violationList with ${params.id} not found")
            return violationList
        }
        // }
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
        Long firmId = PCPSessionUtils.getValue("firmId")
        String code = params["code"]
        String name = params["name"]
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus correspondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"]) : null
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String trackingInfoDateCreatedUTC = params['trackingInfo.dateCreatedUTC']
        String dateCreated = params['dateCreated']
        String sendDate = params['sendDate']
        Integer numberOfCompetitorsValue = params.int("numberOfCompetitorsValue")
        Timestamp fromSendDate = PCPUtils.parseTimestamp(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestamp(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])


        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of violations in the list, the send date, and the current list status
        String query = "FROM violation_list al  LEFT JOIN " +
                "  (SELECT ale.violation_list_id ,count(ale.id) no_of_employee" +
                "  FROM violation_list_employee ale " +
                "  group by ale.violation_list_id ) b" +
                "  on al.id= b.violation_list_id , correspondence_list_status cls,correspondence_list cl" +
                " LEFT JOIN (SELECT * FROM correspondence_list_status  sb WHERE  sb.correspondence_list_status='${EnumCorrespondenceListStatus.SUBMITTED}') sbl" +
                " ON  sbl.correspondence_List_id=cl.id" +
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
            sqlParamsMap.put("fromDateCreated", fromDateCreated)
        }
        if (toDateCreated) {
            query = query + " and cl.date_created <= :toDateCreated "
            sqlParamsMap.put("toDateCreated", toDateCreated)
        }
        //check 3 cases of send date created > = <
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
            query = query + " and COALESCE(b.no_of_employee,0) = :numberOfCompetitorsValueParam  "
            sqlParamsMap.put("numberOfCompetitorsValueParam", numberOfCompetitorsValue)
        }

        //to apply sorting & sorting direction into sql query
        if (columnName?.equalsIgnoreCase("trackingInfo.dateCreatedUTC")) {
            orderByQuery += "ORDER BY cl.date_created ${dir}"
        } else if (columnName?.equalsIgnoreCase("transientData.sendDate")) {
            orderByQuery += "ORDER BY sbl.from_date_datetime ${dir}"
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
                    cl.manual_outgoing_no,
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

        List<ViolationList> results = []

        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[3])
            ZonedDateTime lastUpdatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[12])
            ZonedDateTime toDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[4])
            ZonedDateTime fromDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[5])
            ZonedDateTime sendDateZonedDateTime
            if (resultRow[6]) {
                sendDateZonedDateTime = PCPUtils.convertTimeStampToZonedDateTime(resultRow[6])
            }

            ViolationList violationList = new ViolationList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[7],
                    coverLetter: resultRow[13],
                    transientData: [sendDate                : sendDateZonedDateTime,
                                    numberOfCompetitorsValue: resultRow[10]],
                    receivingParty: resultRow[11],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            violationList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[8].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[10]

            violationList.currentStatus = currentStatus
            results.add(violationList)
        }

        //to store the total count of the violation list instances
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


}