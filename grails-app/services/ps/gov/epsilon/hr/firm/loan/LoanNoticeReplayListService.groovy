package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import java.sql.Timestamp
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to create list for loan -
 * <h1>Usage</h1>
 * -create and manage the list-
 * -include employees and requests of loan-
 * <h1>Restriction</h1>
 * -delete and edit the list when its status is NEw-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class LoanNoticeReplayListService {

    MessageSource messageSource
    FormatService formatService
    def sessionFactory

    //to get the value of requisition status
    public static currentStatusValue = { cService, LoanNoticeReplayList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, LoanNoticeReplayList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../loanNoticeReplayList/manageList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        Set loanNominatedEmployeeIds = params.listString("loanNominatedEmployee.id")
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return LoanNoticeReplayList.createCriteria().list(max: max, offset: offset) {
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
                if (loanNominatedEmployeeIds) {
                    loanNominatedEmployee {
                        inList("id", loanNominatedEmployeeIds)
                    }
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
     * to search about remoting values from the core
     */
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedResultList = customSearch(params)
        if (pagedResultList) {
            return pagedResultList

        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return LoanNoticeReplayList.
     */
    LoanNoticeReplayList save(GrailsParameterMap params) {
        LoanNoticeReplayList loanNoticeReplayListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            loanNoticeReplayListInstance = LoanNoticeReplayList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanNoticeReplayListInstance.version > version) {
                    loanNoticeReplayListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('loanNoticeReplayList.label', null, 'loanNoticeReplayList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanNoticeReplayList while you were editing")
                    return loanNoticeReplayListInstance
                }
            }
            if (!loanNoticeReplayListInstance) {
                loanNoticeReplayListInstance = new LoanNoticeReplayList()
                loanNoticeReplayListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('loanNoticeReplayList.label', null, 'loanNoticeReplayList', LocaleContextHolder.getLocale())] as Object[], "This loanNoticeReplayList with ${params.id} not found")
                return loanNoticeReplayListInstance
            }
        } else {
            loanNoticeReplayListInstance = new LoanNoticeReplayList()
        }
        try {
            loanNoticeReplayListInstance.properties = params
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
                correspondenceListStatus.firm = loanNoticeReplayListInstance.firm
                correspondenceListStatus.correspondenceList = loanNoticeReplayListInstance
                loanNoticeReplayListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            loanNoticeReplayListInstance.save(flush: true);
            //save the current status:
            if (correspondenceListStatus?.id && loanNoticeReplayListInstance?.id) {
                loanNoticeReplayListInstance?.currentStatus = correspondenceListStatus
                loanNoticeReplayListInstance.save(flush: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            loanNoticeReplayListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return loanNoticeReplayListInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<LoanNoticeReplayList> loanNoticeReplayListList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            loanNoticeReplayListList = LoanNoticeReplayList.findAllByIdInList(ids)
            loanNoticeReplayListList.each { LoanNoticeReplayList loanNoticeReplayList ->
                if (loanNoticeReplayList?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete loanNoticeReplayList
                    loanNoticeReplayList.trackingInfo.status = GeneralStatus.DELETED
                    loanNoticeReplayList.save(flush: true)
                }
            }

            //check that at least on record is set to deleted
            if (loanNoticeReplayListList) {
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
     * @return LoanNoticeReplayList.
     */
    @Transactional(readOnly = true)
    LoanNoticeReplayList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                LoanNoticeReplayList loanNoticeReplayList = results?.resultList[0]
                return loanNoticeReplayList
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
     * to add loan request instances to loanNoticeReplayList
     * @param GrailsParameterMap params
     * @return Map
     */
    Map addRequest(GrailsParameterMap params) {

        Map map = [:]
        List errors = []
        Boolean success = false

        LoanNoticeReplayList loanNoticeReplayList = LoanNoticeReplayList.load(params["loanNoticeReplayListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        if (checkedRequestIdList) {
            LoanNominatedEmployee loanNominatedEmployee = null
            LoanNoticeReplayRequest loanNoticeReplayRequest = null
            //retrieve the selected loans:
            List<LoanNoticeReplayRequest> loanNoticeReplayRequestList = LoanNoticeReplayRequest.executeQuery("from LoanNoticeReplayRequest lr where lr.id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])

            if (loanNoticeReplayRequestList) {
                try {
                    loanNoticeReplayRequestList.each {
                        loanNoticeReplayRequest = it
                        loanNoticeReplayRequest.validate()
                        //create new loan list person
                        loanNominatedEmployee = new LoanNominatedEmployee()
                        loanNominatedEmployee.recordStatus = EnumListRecordStatus.NEW
                        loanNominatedEmployee.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        loanNominatedEmployee.orderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        loanNominatedEmployee.loanNoticeReplayRequest = loanNoticeReplayRequest
                        loanNominatedEmployee.loanNoticeReplayList = loanNoticeReplayList

                        //add request information
                        loanNominatedEmployee.employee = loanNoticeReplayRequest.employee
                        loanNominatedEmployee.currentEmploymentRecord = loanNoticeReplayRequest.employee?.currentEmploymentRecord
                        loanNominatedEmployee.currentEmployeeMilitaryRank = loanNoticeReplayRequest.employee?.currentEmployeeMilitaryRank
                        loanNominatedEmployee.fromDate = loanNoticeReplayRequest.fromDate
                        loanNominatedEmployee.toDate = loanNoticeReplayRequest.toDate
                        loanNominatedEmployee.periodInMonth = loanNoticeReplayRequest.periodInMonths

                        loanNominatedEmployee.save(flush: true, failOnError: true)

                        if (loanNoticeReplayRequest?.requestStatusNote) {
                            LoanNominatedEmployeeNote note = new LoanNominatedEmployeeNote(
                                    noteDate: loanNoticeReplayRequest?.requestDate,
                                    note: loanNoticeReplayRequest?.requestStatusNote,
                                    orderNo: "",
                                    loanNominatedEmployee: loanNominatedEmployee,
                            );
                            if (!note?.validate()) {
                                loanNoticeReplayList?.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                loanNominatedEmployee.addToLoanNominatedEmployeeNotes(note);
                            }
                        }

                        //change request status
                        loanNoticeReplayRequest.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        loanNoticeReplayRequest.save(flush: true, failOnError: true)

                    }
                    success = true
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (!loanNominatedEmployee?.hasErrors() && !loanNoticeReplayRequest?.hasErrors()) {
                        loanNoticeReplayList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }

                if (loanNoticeReplayRequest?.hasErrors()) {
                    List<String> loanNoticeReplayRequestError = formatService.formatAllErrors(loanNoticeReplayRequest)?.message
                    loanNoticeReplayRequestError.each {
                        errors << [field: "global", message: it]
                    }
                }

                if (loanNominatedEmployee?.hasErrors()) {
                    List<String> loanNominatedEmployeeErrors = formatService.formatAllErrors(loanNominatedEmployee)?.message
                    loanNominatedEmployeeErrors.each {
                        errors << [field: "global", message: it]
                    }
                }

            }
        } else {
            errors << [field  : "global",
                       message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
        }
        map = [errors: errors, success: success]

        return map
    }

    /**
     * send Promotion list
     * @param GrailsParameterMap params
     * @return boolean
     */
    LoanNoticeReplayList sendList(GrailsParameterMap params) {
        //return loanNoticeReplayListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            LoanNoticeReplayList loanNoticeReplayList = LoanNoticeReplayList.get(params["id"])

            //return error if no request added to list
            if (loanNoticeReplayList?.loanNominatedEmployees?.size() == 0) {
                loanNoticeReplayList.errors.reject("list.sendList.error")
                return loanNoticeReplayList
            }

            if (loanNoticeReplayList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the Promotion list
                    // and change the from date to the date of sending Promotion list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = loanNoticeReplayList
                    correspondenceListStatus.receivingParty = loanNoticeReplayList?.receivingParty
                    correspondenceListStatus.firm = loanNoticeReplayList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        loanNoticeReplayList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualOutgoingNo when we send the Promotion list
                    loanNoticeReplayList.manualOutgoingNo = params.manualOutgoingNo
                    List<LoanNominatedEmployee> loanNominatedEmployees = LoanNominatedEmployee.findAllByLoanNoticeReplayList(loanNoticeReplayList)
                    if (loanNominatedEmployees) {
                        //loop in all loanNominatedEmployee in loan list to change the status of the request
                        loanNominatedEmployees.each { LoanNominatedEmployee loanNominatedEmployee ->
                            loanNominatedEmployee?.loanNoticeReplayRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                            loanNominatedEmployee?.loanNoticeReplayRequest?.externalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                            loanNominatedEmployee?.loanNoticeReplayRequest?.internalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                            loanNominatedEmployee.save(flush: true);
                        }
                    }
                    //save the disciplinary list changes
                    loanNoticeReplayList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (loanNoticeReplayList?.errors?.allErrors?.size() == 0) {
                        loanNoticeReplayList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return loanNoticeReplayList
            }
        } else {
            LoanNoticeReplayList loanNoticeReplayList = new LoanNoticeReplayList()
            loanNoticeReplayList.errors.reject('default.not.found.message', [messageSource.getMessage('loanNoticeReplayList.label', null, 'loanNoticeReplayList', LocaleContextHolder.getLocale())] as Object[], "This loanNoticeReplayList with ${params.id} not found")
            return loanNoticeReplayList
        }
    }

    /**
     * receive Promotion list
     * @param GrailsParameterMap params
     * @return boolean
     */
    LoanNoticeReplayList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return LoanNoticeReplayList is
            LoanNoticeReplayList loanNoticeReplayList = LoanNoticeReplayList.load(params["id"])
            if (loanNoticeReplayList) {
                try {
                    //to change the correspondenceListStatus to received when we received the loan list
                    // and change the to date to the date of receive LoanNoticeReplayList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['toDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = loanNoticeReplayList
                    correspondenceListStatus.receivingParty = loanNoticeReplayList?.receivingParty
                    correspondenceListStatus.firm = loanNoticeReplayList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        loanNoticeReplayList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we receive the loan list
                    loanNoticeReplayList.manualIncomeNo = params.manualIncomeNo
                    //save the loan list changes
                    loanNoticeReplayList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (loanNoticeReplayList?.errors?.allErrors?.size() == 0) {
                        loanNoticeReplayList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return loanNoticeReplayList
            }
        } else {
            LoanNoticeReplayList loanNoticeReplayList = new LoanNoticeReplayList()
            loanNoticeReplayList.errors.reject('default.not.found.message', [messageSource.getMessage('loanNoticeReplayList.label', null, 'loanNoticeReplayList', LocaleContextHolder.getLocale())] as Object[], "This loanNoticeReplayList with ${params.id} not found")
            return loanNoticeReplayList
        }
    }

    /**
     * to change the loanNoticeReplayList request status to Approved in the loanNoticeReplayList and save new statuses in core
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map approveRequest(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_loanNominatedEmployeeIdsList");

        params.remove("checked_loanNominatedEmployeeIdsList");
        Map dataMap = [:]
        Boolean success = true
        List errors = []

        if (checkedRequestIdList) {

            //retrieve the list of loan requests :
            List<LoanNominatedEmployee> loanNominatedEmployeeList = LoanNominatedEmployee.executeQuery("from LoanNominatedEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (loanNominatedEmployeeList) {
                loanNominatedEmployeeList.each { LoanNominatedEmployee loanNominatedEmployee ->
                    try {

                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            LoanNominatedEmployeeNote note = new LoanNominatedEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    loanNominatedEmployee: loanNominatedEmployee,
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
                                success = false
                            } else {
                                //add note to join
                                loanNominatedEmployee.addToLoanNominatedEmployeeNotes(note);
                            }
                        }


                        loanNominatedEmployee.loanNoticeReplayRequest.externalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        loanNominatedEmployee.loanNoticeReplayRequest.internalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME

                        if (!errors) {
                            //change the loan list employee record status
                            loanNominatedEmployee.recordStatus = EnumListRecordStatus.APPROVED
                            loanNominatedEmployee = saveApprovalInfo(loanNominatedEmployee, params)
                            //save the instant
                            loanNominatedEmployee.save(failOnError: true, flush: true)
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        transactionStatus.setRollbackOnly()
                        success = false
                        if (!errors) {
                            errors << [field: "global", message: messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())]
                        }
                    }
                }
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                success = false

            }

        } else {
            errors << [field  : "global",
                       message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
            success = false

        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("success", success)
        return dataMap
    }

    /**
     * separated to be used by AOC
     * @param record
     * @param params
     * @return
     */
    public LoanNominatedEmployee saveApprovalInfo(LoanNominatedEmployee loanNominatedEmployee, GrailsParameterMap params) {
        loanNominatedEmployee.effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        //change the original request status to be approved:
        //if the record comes from request, change the original request status to be approved:
        if (loanNominatedEmployee?.loanNoticeReplayRequest) {
            loanNominatedEmployee.loanNoticeReplayRequest.loanNotice.orderDate = PCPUtils.parseZonedDateTime(params['noteDate'])
            loanNominatedEmployee.loanNoticeReplayRequest.loanNotice.orderNo = params.orderNo
            loanNominatedEmployee.loanNoticeReplayRequest.requestStatus = EnumRequestStatus.APPROVED
        }
        return loanNominatedEmployee
    }

    /**
     * to change the loanNoticeReplayList request status to rejected in the receive loanNoticeReplayList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map rejectRequest(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_loanNominatedEmployeeIdsList");
        params.remove("checked_loanNominatedEmployeeIdsList");
        Map dataMap = [:]
        Boolean success = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of loan requests :
            List<LoanNominatedEmployee> loanNominatedEmployeeList = LoanNominatedEmployee.executeQuery("from LoanNominatedEmployee p where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (loanNominatedEmployeeList) {
                loanNominatedEmployeeList.each { LoanNominatedEmployee loanNominatedEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            LoanNominatedEmployeeNote note = new LoanNominatedEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    loanNominatedEmployee: loanNominatedEmployee,
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
                                success = false
                            } else {
                                //add note to join instance
                                loanNominatedEmployee.addToLoanNominatedEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            success = false
                        }

                        //if there are no errors, save the changes
                        if (!errors) {
                            //change the loan list employee record status
                            loanNominatedEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                            //if the record comes from request, change the original request status to be REJECTED:
                            loanNominatedEmployee?.loanNoticeReplayRequest?.requestStatus = EnumRequestStatus.REJECTED

                            //save the request employee instance:
                            loanNominatedEmployee.save(flush: true, failOnError: true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        transactionStatus.setRollbackOnly()
                        success = false
                        if (!errors) {
                            errors << [field: "global", message: messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())]
                        }
                    }
                }
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                success = false
            }
        } else {
            errors << [field  : "global",
                       message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
            success = false

        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("success", success)
        return dataMap
    }

    /**
     * close loan list
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map closeList(GrailsParameterMap params) {
        //return loanNoticeReplayListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        Map dataMap = [:]
        Boolean success = true
        List errors = []
        if (params.id) {
            //return the correspondence list:
            LoanNoticeReplayList loanNoticeReplayList = LoanNoticeReplayList.get(params["id"])
            if (loanNoticeReplayList) {

                // to change the correspondenceListStatus to submitted when we close the loan list
                // and change the from date when closing the list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = loanNoticeReplayList
                correspondenceListStatus.receivingParty = loanNoticeReplayList?.receivingParty
                correspondenceListStatus.firm = loanNoticeReplayList?.firm

                if (LoanNominatedEmployee.countByLoanNoticeReplayListAndRecordStatus(loanNoticeReplayList, EnumListRecordStatus.NEW) > 0) {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                } else {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                }

                try {
                    if (correspondenceListStatus.save()) {
                        loanNoticeReplayList.currentStatus = correspondenceListStatus
                    }
                    LoanNoticeReplayRequest loanNoticeReplayRequest
                    loanNoticeReplayList?.loanNominatedEmployees?.each { LoanNominatedEmployee loanNominatedEmployee ->
                        loanNoticeReplayRequest = loanNominatedEmployee?.loanNoticeReplayRequest

                        // set external order numbers
                        List<ListNote> orderNumberNoteList = loanNominatedEmployee?.loanNominatedEmployeeNotes?.findAll {
                            it.orderNo != null
                        }?.sort { it?.id }
                        if (orderNumberNoteList?.size() > 0) {
                            ListNote orderNumberNote = orderNumberNoteList?.last()
                            loanNoticeReplayRequest.externalOrderNumber = orderNumberNote.orderNo
                            loanNoticeReplayRequest.externalOrderDate = orderNumberNote.noteDate
                        }

                        /**
                         * set request status
                         */
                        if (loanNominatedEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            loanNoticeReplayRequest?.requestStatus = EnumRequestStatus.APPROVED

                            // reflect request changes
                            //requestChangesHandlerService.applyRequestChanges(loanNoticeReplayRequest)

                        } else if (loanNominatedEmployee?.recordStatus == EnumListRecordStatus.REJECTED) {
                            loanNoticeReplayRequest?.requestStatus = EnumRequestStatus.REJECTED
                        }
                        loanNominatedEmployee.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        loanNominatedEmployee.orderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME

                        if (!loanNominatedEmployee.loanNoticeReplayRequest.externalOrderDate) {
                            loanNominatedEmployee.loanNoticeReplayRequest.externalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        }
                        if (!loanNominatedEmployee.loanNoticeReplayRequest.internalOrderDate) {
                            loanNominatedEmployee.loanNoticeReplayRequest.internalOrderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        }

                    }

                    loanNoticeReplayList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    success = false
                    if (!errors) {
                        errors << messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())
                    }
                }
            }
        } else {
            success = false
            errors << [field  : "global",
                       message: messageSource.getMessage('default.not.found.message',
                               [messageSource.getMessage('loanNoticeReplayList.label', null, 'loanNoticeReplayList', LocaleContextHolder.getLocale())] as Object[],
                               "This loanNoticeReplayList is not found", LocaleContextHolder.getLocale())]
        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("success", success)
        if(success){
            dataMap.put("message",   messageSource.getMessage('list.closeList.message',
                    [messageSource.getMessage('loanNoticeReplayList.label', null, 'loanNoticeReplayList', LocaleContextHolder.getLocale())] as Object[],
                    "This loanNoticeReplayList is not found", LocaleContextHolder.getLocale()))

        }
        return dataMap
    }

    /**
     * custom search to find the number of requests in the loan  list in one select statement for performance issue
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
        Timestamp fromSendDate = PCPUtils.parseTimestampWithSmallestTime(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestampWithBiggestTime(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])
        Timestamp fromReceiveDate = PCPUtils.parseTimestampWithSmallestTime(params['receiveDateFrom'])
        Timestamp toReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateTo'])


        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of loans in the list, the send date, and the current list status
        String query = "FROM loan_notice_replay_list al  LEFT JOIN " +
                "  (SELECT ale.loan_notice_replay_list_id ,count(ale.id) no_of_employee" +
                "  FROM loan_nominated_employee ale " +
                "  group by ale.loan_notice_replay_list_id ) b" +
                "  on al.id= b.loan_notice_replay_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<LoanNoticeReplayList> results = []
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

            LoanNoticeReplayList loanNoticeReplayList = new LoanNoticeReplayList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            loanNoticeReplayList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            loanNoticeReplayList.currentStatus = currentStatus
            results.add(loanNoticeReplayList)
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
}