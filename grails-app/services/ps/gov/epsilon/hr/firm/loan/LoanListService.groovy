package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
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
class LoanListService {

    MessageSource messageSource
    def formatService
    def sessionFactory

    //to get the value of requisition status
    public static currentStatusValue = { cService, LoanList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, LoanList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../loanList/manageLoanList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set loanListPersonIds = params.listString("loanListPerson.id")
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return LoanList.createCriteria().list(max: max, offset: offset) {
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
                if (loanListPersonIds) {
                    loanListPerson {
                        inList("id", loanListPersonIds)
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
     * @return LoanList.
     */
    LoanList save(GrailsParameterMap params) {
        LoanList loanListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            loanListInstance = LoanList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanListInstance.version > version) {
                    loanListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('loanList.label', null, 'loanList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanList while you were editing")
                    return loanListInstance
                }
            }
            if (!loanListInstance) {
                loanListInstance = new LoanList()
                loanListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('loanList.label', null, 'loanList', LocaleContextHolder.getLocale())] as Object[], "This loanList with ${params.id} not found")
                return loanListInstance
            }
        } else {
            loanListInstance = new LoanList()
        }
        try {
            loanListInstance.properties = params;
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
                correspondenceListStatus.firm = loanListInstance.firm
                correspondenceListStatus.correspondenceList = loanListInstance
                loanListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            loanListInstance.save(flush: true);
            //save the current status:
            if (correspondenceListStatus?.id && loanListInstance?.id) {
                loanListInstance?.currentStatus = correspondenceListStatus
                loanListInstance.save(flush: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            loanListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return loanListInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<LoanList> loanListList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            loanListList = LoanList.findAllByIdInList(ids)
            loanListList.each { LoanList loanList ->
                if (loanList?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete loanList
                    loanList.trackingInfo.status = GeneralStatus.DELETED
                    loanList.save(flush: true)
                }
            }

            //check that at least on record is set to deleted
            if (loanListList) {
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
     * @return LoanList.
     */
    @Transactional(readOnly = true)
    LoanList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                LoanList loanList = results?.resultList[0]
                return loanList
            }
        }
        return null
    }

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return instance.
     */
    @Transactional(readOnly = true)
    LoanList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                LoanList instance = results?.resultList[0]
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
     * to add loan request instances to loanList
     * @param GrailsParameterMap params
     * @return Map
     */
    Map addRequest(GrailsParameterMap params) {

        Map map = [:]
        List errors = []
        Boolean success = false

        LoanList loanList = LoanList.load(params["loanListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        if (checkedRequestIdList) {
            LoanListPerson loanListPerson = null
            LoanRequest loanRequest = null
            //retrieve the selected loans:
            List<LoanRequest> loanRequestList = LoanRequest.executeQuery("from LoanRequest lr where lr.id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])

            if (loanRequestList) {
                try {
                    loanRequestList.each {
                        loanRequest = it
                        //create new loan list person
                        loanListPerson = new LoanListPerson()
                        loanListPerson.isEmploymentProfileProvided = false
                        loanListPerson.recordStatus = EnumListRecordStatus.NEW
                        loanListPerson.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        loanListPerson.firm = loanRequest?.firm
                        loanListPerson.loanRequest = loanRequest
                        loanListPerson.loanList = loanList
                        loanListPerson.save(flush: true, failOnError: true)

                        if (loanRequest?.description) {
                            LoanListPersonNote note = new LoanListPersonNote(
                                    noteDate: loanRequest?.requestDate,
                                    note: loanRequest?.description,
                                    orderNo: "",
                                    loanListPerson: loanListPerson,
                            );
                            if (!note?.validate()) {
                                loanList?.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                loanListPerson.addToLoanListPersonNotes(note);
                            }
                        }

                        //change request status
                        loanRequest.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        loanRequest.save(flush: true, failOnError: true)

                    }
                    success = true
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (!loanListPerson?.hasErrors() && !loanRequest?.hasErrors()) {
                        loanList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }

                List<String> loanRequestError = []
                List<String> loanListPersonErrors = []

                if (loanRequest?.hasErrors()) {
                    loanRequestError = formatService.formatAllErrors(loanRequest)?.message
                    loanRequestError.each {
                        errors << [field: "global", message: it]
                    }
                }

                if (loanListPerson?.hasErrors()) {
                    loanListPersonErrors = formatService.formatAllErrors(loanListPerson)?.message
                    loanListPersonErrors.each {
                        errors << [field: "global", message: it]
                    }
                }

            }
        } else {
            errors << [field: "global", message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
        }
        map = [errors: errors, success: success]

        return map
    }

    /**
     * send Promotion list
     * @param GrailsParameterMap params
     * @return boolean
     */
    LoanList sendList(GrailsParameterMap params) {
        //return loanListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            LoanList loanList = LoanList.get(params["id"])

            //return error if no request added to list
            if (loanList?.loanListPerson?.size() == 0) {
                loanList.errors.reject("list.sendList.error")
                return loanList
            }

            if (loanList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the Promotion list
                    // and change the from date to the date of sending Promotion list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = loanList
                    correspondenceListStatus.receivingParty = loanList?.receivingParty
                    correspondenceListStatus.firm = loanList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        loanList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualOutgoingNo when we send the Promotion list
                    loanList.manualOutgoingNo = params.manualOutgoingNo
                    List<LoanListPerson> loanListPersons = LoanListPerson.findAllByLoanList(loanList)
                    if (loanListPersons) {
                        //loop in all loanListPerson in loan list to change the status of the request
                        loanListPersons.each { LoanListPerson loanListPerson ->
                            loanListPerson?.loanRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                            loanListPerson?.loanRequest?.validate()
                            loanListPerson.save(flush: true);
                        }
                    }
                    //save the disciplinary list changes
                    loanList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (loanList?.errors?.allErrors?.size() == 0) {
                        loanList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return loanList
            }
        } else {
            LoanList loanList = new LoanList()
            loanList.errors.reject('default.not.found.message', [messageSource.getMessage('loanList.label', null, 'loanList', LocaleContextHolder.getLocale())] as Object[], "This loanList with ${params.id} not found")
            return loanList
        }
    }

    /**
     * receive Promotion list
     * @param GrailsParameterMap params
     * @return boolean
     */
    LoanList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return LoanList is
            LoanList loanList = LoanList.load(params["id"])
            if (loanList) {
                try {
                    //to change the correspondenceListStatus to received when we received the loan list
                    // and change the to date to the date of receive LoanList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['toDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = loanList
                    correspondenceListStatus.receivingParty = loanList?.receivingParty
                    correspondenceListStatus.firm = loanList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        loanList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we receive the loan list
                    loanList.manualIncomeNo = params.manualIncomeNo
                    //save the loan list changes
                    loanList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (loanList?.errors?.allErrors?.size() == 0) {
                        loanList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return loanList
            }
        } else {
            LoanList loanList = new LoanList()
            loanList.errors.reject('default.not.found.message', [messageSource.getMessage('loanList.label', null, 'loanList', LocaleContextHolder.getLocale())] as Object[], "This loanList with ${params.id} not found")
            return loanList
        }
    }

    /**
     * to change the loanList request status to Approved in the loanList and save new statuses in core
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map approveRequest(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_loanPersonIdsList");
        //to get received person ids
        def receivedPersonIdList = params.list("receivedPersonId")

        params.remove("checked_loanPersonIdsList");
        Map dataMap = [:]
        Boolean success = true
        List errors = []

        if (checkedRequestIdList) {

            if (receivedPersonIdList) {

                //retrieve the list of loan requests :
                List<LoanListPerson> loanListPersonList = LoanListPerson.executeQuery("from LoanListPerson d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
                if (loanListPersonList) {
                    loanListPersonList.each { LoanListPerson loanListPerson ->
                        try {

                            //remove previous LoanRequestRelatedPerson
                            LoanRequestRelatedPerson.executeUpdate("delete from LoanRequestRelatedPerson rp where rp.loanRequest.id = :loanRequestId and rp.recordSource = :personRecordSource", [loanRequestId: loanListPerson?.loanRequest?.id, personRecordSource: EnumPersonSource.RECEIVED])

                            //save the note if its entered:
                            //create note instance:
                            if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                                LoanListPersonNote note = new LoanListPersonNote(
                                        noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                        note: params.note,
                                        orderNo: params.orderNo,
                                        loanListPerson: loanListPerson,
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
                                    loanListPerson.addToLoanListPersonNotes(note);
                                }
                            }

                            if (!errors) {
                                loanListPerson = saveApprovalInfo(loanListPerson, params)
                                //change the loan list employee record status
                                loanListPerson.recordStatus = EnumListRecordStatus.APPROVED
                                //save the instant
                                loanListPerson.save(failOnError: true, flush: true)
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
                    errors << [field: "global", message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "no person find in data base", LocaleContextHolder.getLocale())]
                    success = false

                }
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("loanList.noReceivedPerson.error", null as Object[], "no received person to approved", LocaleContextHolder.getLocale())]
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
    public LoanListPerson saveApprovalInfo(LoanListPerson record, GrailsParameterMap params) {
        record.effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])

        //change the original request status to be approved:
        //if the record comes from request, change the original request status to be approved:
        if (record?.loanRequest) {

            //to get received person ids
            def receivedPersonIdList = params.list("receivedPersonId")
            //add received persons
            LoanRequestRelatedPerson loanRequestRelatedPerson
            receivedPersonIdList.eachWithIndex { requestedPersonId, index ->
                loanRequestRelatedPerson = new LoanRequestRelatedPerson(
                        requestedPersonId: (requestedPersonId as long),
                        recordSource: EnumPersonSource.RECEIVED,
                        effectiveDate: record.effectiveDate,
                        firm: Firm.load(PCPSessionUtils.getValue("firmId"))
                )
                record?.loanRequest?.validate()
                record?.loanRequest?.addToLoanRequestRelatedPersons(loanRequestRelatedPerson)
                loanRequestRelatedPerson = null
            }
        }
        return record
    }

    /**
     * to change the loanList request status to rejected in the receive loanList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map rejectRequest(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_loanPersonIdsList");
        params.remove("checked_loanPersonIdsList");
        Map dataMap = [:]
        Boolean success = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of loan requests :
            List<LoanListPerson> loanListPersonList = LoanListPerson.executeQuery("from LoanListPerson p where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (loanListPersonList) {
                loanListPersonList.each { LoanListPerson loanListPerson ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            LoanListPersonNote note = new LoanListPersonNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    loanListPerson: loanListPerson,
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
                                loanListPerson.addToLoanListPersonNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            success = false
                        }

                        //if there are no errors, save the changes
                        if (!errors) {
                            //change the loan list employee record status
                            loanListPerson?.recordStatus = EnumListRecordStatus.REJECTED

                            //save the request employee instance:
                            loanListPerson.save(flush: true, failOnError: true);
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
        //return loanListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        Map dataMap = [:]
        Boolean success = true
        List errors = []
        if (params.id) {
            //return the correspondence list:
            LoanList loanList = LoanList.get(params["id"])
            if (loanList) {

                // to change the correspondenceListStatus to submitted when we close the loan list
                // and change the from date when closing the list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = loanList
                correspondenceListStatus.receivingParty = loanList?.receivingParty
                correspondenceListStatus.firm = loanList?.firm

                if (LoanListPerson.countByLoanListAndRecordStatus(loanList, EnumListRecordStatus.NEW) > 0) {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                } else {
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                }
                try {
                    if (correspondenceListStatus.save()) {
                        loanList.currentStatus = correspondenceListStatus
                    }
                    LoanRequest loanRequest
                    loanList?.loanListPerson?.each { LoanListPerson loanListPerson ->
                        if (loanListPerson.recordStatus in [EnumListRecordStatus.REJECTED, EnumListRecordStatus.APPROVED]) {
                            loanRequest = loanListPerson?.loanRequest
                            // set external order numbers
                            List<ListNote> orderNumberNoteList = loanListPerson?.loanListPersonNotes?.findAll {
                                it.orderNo != null
                            }?.sort { it?.id }
                            if (orderNumberNoteList?.size() > 0) {
                                ListNote orderNumberNote = orderNumberNoteList?.last()
                                loanRequest.externalOrderNumber = orderNumberNote.orderNo
                                loanRequest.externalOrderDate = orderNumberNote.noteDate
                            }
                            /**
                             * set request status
                             */
                            if (loanListPerson?.recordStatus == EnumListRecordStatus.APPROVED) {
                                loanRequest?.requestStatus = EnumRequestStatus.APPROVED

                                // reflect request changes
                                //requestChangesHandlerService.applyRequestChanges(loanRequest)

                            } else if (loanListPerson?.recordStatus == EnumListRecordStatus.REJECTED) {
                                loanRequest?.requestStatus = EnumRequestStatus.REJECTED
                            }


                        }
                        loanListPerson.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME

                        loanRequest?.validate()
                        loanRequest?.save(flush: true, failOnError: true)


                    }
                    loanList.save(failOnError: true, flush: true)
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
            success = false
            errors << [field  : "global",
                       message: messageSource.getMessage('default.not.found.message',
                               [messageSource.getMessage('loanList.label', null, 'loanList', LocaleContextHolder.getLocale())] as Object[],
                               "This loanList is not found", LocaleContextHolder.getLocale())]
        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("success", success)
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
        String query = "FROM loan_list al  LEFT JOIN " +
                "  (SELECT ale.loan_list_id ,count(ale.id) no_of_employee" +
                "  FROM loan_list_person ale " +
                "  group by ale.loan_list_id ) b" +
                "  on al.id= b.loan_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<LoanList> results = []
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

            LoanList loanList = new LoanList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            loanList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            loanList.currentStatus = currentStatus
            results.add(loanList)
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