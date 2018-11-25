package ps.gov.epsilon.hr.firm.absence

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.notifications.NotificationService
import ps.police.security.remotting.RemoteUserService
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -This service used to manage the return from absence lists-
 * <h1>Usage</h1>
 * -create and manage the list: send, receive, add and handle the return from absence lists-
 * <h1>Restriction</h1>
 * -delete when status is created only-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ReturnFromAbsenceListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    EmployeeService employeeService
    NotificationService notificationService
    RemoteUserService remoteUserService

    //to get the value of requisition status
    public static currentStatusValue = { cService, ReturnFromAbsenceList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, ReturnFromAbsenceList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../returnFromAbsenceList/manageReturnFromAbsenceList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        String coverLetter = params["coverLetter"]
        String currentStatusId = params["currentStatus.id"]
        Long firmId = PCPSessionUtils.getValue("firmId")
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
        Set returnFromAbsenceListEmployeesIds = params.listString("returnFromAbsenceListEmployees.id")
        return ReturnFromAbsenceList.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("code", sSearch)
                    ilike("coverLetter", sSearch)
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
                if (coverLetter) {
                    ilike("coverLetter", "%${coverLetter}%")
                }
                if (currentStatusId) {
                    eq("currentStatus.id", currentStatusId)
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
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
                if (returnFromAbsenceListEmployeesIds) {
                    returnFromAbsenceListEmployees {
                        inList("id", returnFromAbsenceListEmployeesIds)
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
     * @return ReturnFromAbsenceList.
     */
    ReturnFromAbsenceList save(GrailsParameterMap params) {
        ReturnFromAbsenceList returnFromAbsenceListInstance
        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            returnFromAbsenceListInstance = ReturnFromAbsenceList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (returnFromAbsenceListInstance.version > version) {
                    returnFromAbsenceListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('returnFromAbsenceList.label', null, 'returnFromAbsenceList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this returnFromAbsenceList while you were editing")
                    return returnFromAbsenceListInstance
                }
            }
            if (!returnFromAbsenceListInstance) {
                returnFromAbsenceListInstance = new ReturnFromAbsenceList()
                returnFromAbsenceListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('returnFromAbsenceList.label', null, 'returnFromAbsenceList', LocaleContextHolder.getLocale())] as Object[], "This returnFromAbsenceList with ${params.id} not found")
                return returnFromAbsenceListInstance
            }
        } else {
            returnFromAbsenceListInstance = new ReturnFromAbsenceList()
        }
        try {
            returnFromAbsenceListInstance.properties = params;

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
                correspondenceListStatus.firm = returnFromAbsenceListInstance.firm
                correspondenceListStatus.correspondenceList = returnFromAbsenceListInstance
                returnFromAbsenceListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            returnFromAbsenceListInstance.save(flush: true)
            //save the current status:
            if (correspondenceListStatus?.id && returnFromAbsenceListInstance?.id) {
                returnFromAbsenceListInstance?.currentStatus = correspondenceListStatus
                returnFromAbsenceListInstance.save(flush: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            returnFromAbsenceListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return returnFromAbsenceListInstance
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
            ReturnFromAbsenceList instance = ReturnFromAbsenceList.get(id)
            //to be able to delete an vacation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.returnFromAbsenceListEmployees?.size() == 0) {
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
     * @return ReturnFromAbsenceList.
     */
    @Transactional(readOnly = true)
    ReturnFromAbsenceList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                ReturnFromAbsenceList returnFromAbsenceList = results?.resultList[0]
                return returnFromAbsenceList
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
    ReturnFromAbsenceList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                ReturnFromAbsenceList instance = results?.resultList[0]
                return instance
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
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * to add request instance to returnFromAbsenceList
     * @param GrailsParameterMap params
     * @return boolean
     */
    ReturnFromAbsenceList addRequestToList(GrailsParameterMap params) {
        ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.load(params["returnFromAbsenceListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee
            //retrieve the selected promotions:
            List<ReturnFromAbsenceRequest> requests = ReturnFromAbsenceRequest.executeQuery("from ReturnFromAbsenceRequest d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (requests) {
                try {
                    requests.each { ReturnFromAbsenceRequest request ->
                        returnFromAbsenceListEmployee = new ReturnFromAbsenceListEmployee()
                        returnFromAbsenceListEmployee?.recordStatus = EnumListRecordStatus.NEW
                        returnFromAbsenceListEmployee?.returnFromAbsenceRequest = request
                        returnFromAbsenceListEmployee?.actualReturnDate = request?.actualReturnDate
                        returnFromAbsenceListEmployee?.actualAbsenceReason = request?.actualAbsenceReason
                        returnFromAbsenceListEmployee?.returnFromAbsenceList = returnFromAbsenceList
                        returnFromAbsenceListEmployee?.validate()
                        request.requestStatus = EnumRequestStatus.ADD_TO_LIST

                        if (request?.requestStatusNote) {
                            ReturnFromAbsenceListEmployeeNote note = new ReturnFromAbsenceListEmployeeNote(
                                    noteDate: request?.requestDate,
                                    note: request?.requestStatusNote,
                                    orderNo: "",
                                    returnFromAbsenceListEmployee: returnFromAbsenceListEmployee,
                            );
                            if (!note?.validate()) {
                                returnFromAbsenceList.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                returnFromAbsenceListEmployee.addToReturnFromAbsenceListEmployeeNotes(note);
                            }
                        }


                        returnFromAbsenceList.addToReturnFromAbsenceListEmployees(returnFromAbsenceListEmployee)
                        request.validate()
                        request?.save(failOnError: true, flush: true)
                    }
                    //save the promotion list changes
                    returnFromAbsenceList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (returnFromAbsenceList?.errors?.allErrors?.size() == 0) {
                        returnFromAbsenceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            returnFromAbsenceList.errors.reject("list.request.notSelected.error")
        }
        return returnFromAbsenceList
    }

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ReturnFromAbsenceList sendData(GrailsParameterMap params) {
        //return returnFromAbsenceListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            //return the corresponding list:
            ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.get(params["id"])
            if (returnFromAbsenceList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['sendDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = returnFromAbsenceList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = returnFromAbsenceList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        returnFromAbsenceList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    returnFromAbsenceList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all returnFromAbsenceListEmployee in marital status list to change the status of the request
                    returnFromAbsenceList?.returnFromAbsenceListEmployees.each { ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee ->
                        returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.validate()
                    }

                    //save the disciplinary list changes
                    returnFromAbsenceList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (returnFromAbsenceList?.errors?.allErrors?.size() == 0) {
                        returnFromAbsenceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return returnFromAbsenceList
            }
        }
    }

    /**
     * receive marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ReturnFromAbsenceList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return ReturnFromAbsenceList is
            ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.load(params["id"])
            if (returnFromAbsenceList) {
                try {
                    //to change the correspondenceListStatus to received when we receive the ReturnFromAbsenceList
                    // and change the to date to the date of receive ReturnFromAbsenceList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus(
                            fromDate: ZonedDateTime.now(),
                            toDate: PCPUtils.parseZonedDateTime(params['receiveDate']),
                            correspondenceList: returnFromAbsenceList,
                            receivingParty: params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null,
                            firm: returnFromAbsenceList?.firm,
                            correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED)
                    returnFromAbsenceList?.currentStatus = correspondenceListStatus

                    //enter the manualIncomeNo when we receive the returnFromAbsence list
                    returnFromAbsenceList?.manualIncomeNo = params.manualIncomeNo

                    //save the disciplinary list changes
                    returnFromAbsenceList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (returnFromAbsenceList?.errors?.allErrors?.size() == 0) {
                        returnFromAbsenceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return returnFromAbsenceList
            }
        }
    }

    /**
     * to change the returnFromAbsence request status to Approved in the returnFromAbsenceList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToApproved(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("check_RequestIdList");
        params.remove("check_RequestIdList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of returnFromAbsence requests :
            List<ReturnFromAbsenceListEmployee> returnFromAbsenceEmployeeList = ReturnFromAbsenceListEmployee.executeQuery("from ReturnFromAbsenceListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (returnFromAbsenceEmployeeList) {
                returnFromAbsenceEmployeeList.each { ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate']) || params.note || params.orderNo) {
                            ReturnFromAbsenceListEmployeeNote note = new ReturnFromAbsenceListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    returnFromAbsenceListEmployee: returnFromAbsenceListEmployee,
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
                                returnFromAbsenceListEmployee.addToReturnFromAbsenceListEmployeeNotes(note);
                            }
                        }
                        if (!errors) {
                            //change the returnFromAbsence list employee record status
                            returnFromAbsenceListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                            //save the request employee instance:
                            returnFromAbsenceListEmployee.save(flush: true, failOnError: true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        transactionStatus.setRollbackOnly()
                        if (!errors) {
                            errors << messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())
                        }
                    }
                }
            }
        } else {
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
     * to change the returnFromAbsenceList request status to rejected in the receive returnFromAbsenceList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToRejected(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("check_RequestIdList");
        params.remove("check_RequestIdList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of returnFromAbsence requests :
            List<ReturnFromAbsenceListEmployee> returnFromAbsenceEmployeeList = ReturnFromAbsenceListEmployee.executeQuery("from ReturnFromAbsenceListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (returnFromAbsenceEmployeeList) {
                returnFromAbsenceEmployeeList.each { ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee ->
                    try {

                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            ReturnFromAbsenceListEmployeeNote note = new ReturnFromAbsenceListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    returnFromAbsenceListEmployee: returnFromAbsenceListEmployee,
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
                                returnFromAbsenceListEmployee.addToReturnFromAbsenceListEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }
                        if (!errors) {
                            //change the returnFromAbsence list employee record status
                            returnFromAbsenceListEmployee?.recordStatus = EnumListRecordStatus.REJECTED
                            //save the request employee instance:
                            returnFromAbsenceListEmployee.save(flush: true, failOnError: true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        transactionStatus.setRollbackOnly()
                        if (!errors) {
                            errors << messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())
                        }
                    }
                }
            }
        } else {
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
     * close returnFromAbsence list
     * @param GrailsParameterMap params
     * @return returnFromAbsence list instance
     */
    ReturnFromAbsenceList closeList(GrailsParameterMap params) {
        //return returnFromAbsenceListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.get(params["id"])
            if (returnFromAbsenceList) {
                try {
                    //to change the correspondenceListStatus to submitted when we close the returnFromAbsence list
                    // and change the from date when closing the list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = returnFromAbsenceList
                    correspondenceListStatus.receivingParty = returnFromAbsenceList?.receivingParty
                    correspondenceListStatus.firm = returnFromAbsenceList?.firm
                    if (ReturnFromAbsenceListEmployee.countByReturnFromAbsenceListAndRecordStatus(returnFromAbsenceList, EnumListRecordStatus.NEW) > 0) {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                    } else {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                    }

                    //loop on all ServiceListEmployee with Approved status to change the employee category status
                    //List<ReturnFromAbsenceListEmployee> returnFromAbsenceListEmployees = ReturnFromAbsenceListEmployee.executeQuery("from ReturnFromAbsenceListEmployee ple where ple.returnFromAbsenceList.id = :returnFromAbsenceListId and ple.recordStatus = :recordStatus", [returnFromAbsenceListId: returnFromAbsenceList?.id, recordStatus: EnumListRecordStatus.APPROVED])

                    //get the employee status : working
                    EmployeeStatus employeeStatusWorking = EmployeeStatus.get(EnumEmployeeStatus.WORKING.value)
                    EmployeeStatus employeeStatusAbsence = EmployeeStatus.get(EnumEmployeeStatus.ABSENCE.value)
                    EmployeeStatusHistory employeeStatusHistory
                    ReturnFromAbsenceRequest returnFromAbsenceRequest
                    //loop on all serviceListEmployees with Approved record_status and set the employee status history
                    returnFromAbsenceList?.returnFromAbsenceListEmployees?.each { ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee ->

                        returnFromAbsenceRequest = returnFromAbsenceListEmployee?.returnFromAbsenceRequest
                        returnFromAbsenceRequest.validate()

                        if (returnFromAbsenceListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            returnFromAbsenceRequest?.requestStatus = EnumRequestStatus.APPROVED
                            returnFromAbsenceRequest?.absence?.violationStatus = EnumViolationStatus.RETURNED
                            returnFromAbsenceRequest?.absence?.validate()

                            // set external order numbers
                            List<ListNote> orderNumberNoteList = returnFromAbsenceListEmployee?.returnFromAbsenceListEmployeeNotes?.findAll {
                                it.orderNo != null
                            }?.sort { it?.id }
                            if (orderNumberNoteList?.size() > 0) {
                                ListNote orderNumberNote = orderNumberNoteList?.last()
                                returnFromAbsenceRequest.externalOrderNumber = orderNumberNote.orderNo
                                returnFromAbsenceRequest.externalOrderDate = orderNumberNote.noteDate
                            }

                            returnFromAbsenceRequest?.absence?.toDate = returnFromAbsenceListEmployee?.actualReturnDate
                            returnFromAbsenceRequest?.absence?.numOfDays = ChronoUnit.DAYS.between(returnFromAbsenceRequest?.absence?.fromDate?.toLocalDate(), returnFromAbsenceRequest?.absence?.toDate?.toLocalDate()) + 1
                            returnFromAbsenceRequest?.absence?.violationStatus = EnumViolationStatus.CLOSED
                            returnFromAbsenceRequest?.absence?.actualAbsenceReason = returnFromAbsenceListEmployee?.actualAbsenceReason
                            returnFromAbsenceRequest?.absence?.validate()

                            //update employee status history and set the absence status:
                            if (employeeStatusWorking) {
                                employeeStatusHistory = new EmployeeStatusHistory()
                                employeeStatusHistory?.employee = returnFromAbsenceRequest?.employee
                                employeeStatusHistory?.fromDate = returnFromAbsenceListEmployee?.actualReturnDate
                                employeeStatusHistory?.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                                employeeStatusHistory?.employeeStatus = employeeStatusWorking
                                employeeStatusHistory.save(flush: true, failOnError: true)
                                returnFromAbsenceRequest?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                            }
                            if (employeeStatusAbsence) {
                                employeeStatusHistory = new EmployeeStatusHistory()
                                employeeStatusHistory = EmployeeStatusHistory.createCriteria().list() {
                                    eq('employeeStatus.id', employeeStatusAbsence.id)
                                    eq('employee.id', returnFromAbsenceRequest?.employee?.id)
                                    eq('toDate', PCPUtils.DEFAULT_ZONED_DATE_TIME)
                                    order("trackingInfo.dateCreatedUTC", "desc")
                                }[0]
                                if (employeeStatusHistory) {
                                    employeeStatusHistory?.toDate = returnFromAbsenceListEmployee?.actualReturnDate
                                    employeeStatusHistory?.save(flush: true, failOnError: true)
                                }
                            }
                        } else {
                            returnFromAbsenceRequest?.requestStatus = EnumRequestStatus.REJECTED
                            returnFromAbsenceRequest?.absence?.violationStatus = EnumViolationStatus.SENT_BY_LIST
                            returnFromAbsenceRequest?.absence?.validate()

                        }
                        returnFromAbsenceRequest?.absence?.save(flush: true, failOnError: true)
                        returnFromAbsenceRequest?.validate()
                        returnFromAbsenceRequest?.save(flush: true, failOnError: true)
                        returnFromAbsenceListEmployee?.save(flush: true, failOnError: true)
                    }

                    if (correspondenceListStatus.save()) {
                        returnFromAbsenceList.currentStatus = correspondenceListStatus
                    }

                    //save the returnFromAbsence list changes
                    returnFromAbsenceList.save(flush: true, failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (returnFromAbsenceList?.errors?.allErrors?.size() == 0) {
                        returnFromAbsenceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return returnFromAbsenceList
            }
        }
    }

    /**
     * custom search to find the number of requests in the promotion  list in one select statement for performance issue
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

        //the query to retrieve the list details, num of promotions in the list, the send date, and the current list status
        String query = "FROM return_from_absence_list al  LEFT JOIN " +
                "  (SELECT ale.return_from_absence_list_id ,count(ale.id) no_of_employee" +
                "  FROM return_from_absence_list_employee ale " +
                "  group by ale.return_from_absence_list_id ) b" +
                "  on al.id= b.return_from_absence_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<ReturnFromAbsenceList> results = []
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

            ReturnFromAbsenceList returnFromAbsenceList = new ReturnFromAbsenceList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            returnFromAbsenceList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            returnFromAbsenceList.currentStatus = currentStatus
            results.add(returnFromAbsenceList)
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