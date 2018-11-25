package ps.gov.epsilon.hr.firm.dispatch

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchListType
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.request.RequestChangesHandlerService
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm
import ps.police.security.dtos.v1.UserDTO
import ps.police.security.remotting.RemoteUserService
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import ps.gov.epsilon.hr.firm.request.Request

/**
 * <h1>Purpose</h1>
 * -this service aims to manage the dispatch list which will carry the dispatch requests
 * <h1>Usage</h1>
 * - create, edit, manage the list
 * - send, receive the lists
 * - approved, reject the requests
 * <h1>Restriction</h1>
 * - create should be automatically
 * - edit is allowed if the list is NEW state only.
 * @see MessageSource
 * @see FormatService
 * @see SessionFactory
 * */
@Transactional
class DispatchListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    RemoteUserService remoteUserService
    NotificationService notificationService
    RequestChangesHandlerService requestChangesHandlerService
    RequestService requestService

    //to get the value of list status
    public static currentStatusValue = { cService, DispatchList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, DispatchList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../dispatchList/manageDispatchList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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

        Long firmId = params.long("firm.id") ? params.long("firm.id") : PCPSessionUtils.getValue("firmId")

        List<Map<String, String>> orderBy = params.list("orderBy")
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        Set dispatchListEmployeesIds = params.listString("dispatchListEmployees.id")

        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        //search about list with status:
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus currentStatusCorrespondenceListStatus = params["currentStatus.correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["currentStatus.correspondenceListStatus"] as String) : null
        ZonedDateTime sendDate = PCPUtils.parseZonedDateTime(params['sendDate'])

        //filter used in search about new list
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus listCurrentStatusValue = params["listCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["listCurrentStatusValue"] as String) : null

        return DispatchList.createCriteria().list(max: max, offset: offset) {
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
                if (dispatchListEmployeesIds) {
                    dispatchListEmployees {
                        inList("id", dispatchListEmployeesIds)
                    }
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
                //filter used in search about new list
                if (listCurrentStatusValue) {
                    currentStatus {
                        eq("correspondenceListStatus", listCurrentStatusValue)
                    }
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
     * @return DispatchList.
     */
    DispatchList save(GrailsParameterMap params) {
        DispatchList dispatchListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            dispatchListInstance = DispatchList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (dispatchListInstance.version > version) {
                    dispatchListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('dispatchList.label', null, 'dispatchList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this dispatchList while you were editing")
                    return dispatchListInstance
                }
            }
            if (!dispatchListInstance) {
                dispatchListInstance = new DispatchList()
                dispatchListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchList.label', null, 'dispatchList', LocaleContextHolder.getLocale())] as Object[], "This dispatchList with ${params.id} not found")
                return dispatchListInstance
            }
        } else {
            dispatchListInstance = new DispatchList()
        }
        try {
            dispatchListInstance.properties = params;

            /**
             * in CREATED phase: create new status for the list
             */
            CorrespondenceListStatus correspondenceListStatus
            EnumCorrespondenceListStatus listStatus = params.correspondenceListStatus ? EnumCorrespondenceListStatus.valueOf(params.correspondenceListStatus) : null
            if (!params.id && !listStatus) {
                //when create the list , its is CREATED phase:
                listStatus = EnumCorrespondenceListStatus.CREATED
            }

            if (listStatus) {
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                        correspondenceListStatus: listStatus, receivingParty: EnumReceivingParty.SARAYA, firm: dispatchListInstance.firm)
                dispatchListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            dispatchListInstance.save(flush: true, failOnError: true);

            //save the current status:
            if (correspondenceListStatus?.id && dispatchListInstance?.id) {
                dispatchListInstance?.currentStatus = correspondenceListStatus
                dispatchListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            dispatchListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return dispatchListInstance
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return DispatchList.
     */
    @Transactional(readOnly = true)
    DispatchList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                DispatchList dispatchList = results?.resultList[0]
                return dispatchList
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
    DispatchList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                DispatchList instance = results?.resultList[0]
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
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see ps.police.common.beans.v1.DeleteBean.
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
            DispatchList instance = DispatchList.get(id)
            //to be able to delete an vacation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.dispatchListEmployees?.size() == 0) {
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
     * to add any dispatch Request to dispatchList
     * @param GrailsParameterMap params
     * @return list instance
     */
    DispatchList addRequestToList(GrailsParameterMap params) {
        DispatchList dispatchList = DispatchList.load(params["dispatchListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");
        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            DispatchListEmployee dispatchListEmployee
            //retrieve the selected services:
            List<DispatchRequest> dispatchRequests = DispatchRequest.executeQuery("from DispatchRequest c where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (dispatchRequests) {
                try {
                    dispatchRequests.each { DispatchRequest dispatchRequest ->
                        dispatchRequest?.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        dispatchRequest?.validate()
                        dispatchRequest.save(flush: true, failOnError: true)
                        //create new dispatchList employee
                        dispatchListEmployee = createDispatchListEmployeeFromRequest(dispatchRequest, dispatchList)
                        dispatchList?.addToDispatchListEmployees(dispatchListEmployee)
                        //add the dispatchList employee to dispatchList
                    }
                    dispatchList.save(flush: true, failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (dispatchList?.errors?.allErrors?.size() == 0) {
                        dispatchList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            if (!dispatchList) {
                dispatchList = new DispatchList()
            }
            dispatchList?.errors.reject('default.internal.server.error', [null] as Object[], "")
        }
        return dispatchList
    }//end of function

    /**
     * Create a dispatchListEmployee from request
     * @param request
     * @param firm
     * @return dispatchListEmployee instance
     */
    DispatchListEmployee createDispatchListEmployeeFromRequest(def dispatchRequest, DispatchList dispatchList) {
        //create new dispatchList employee
        DispatchListEmployee dispatchListEmployee = new DispatchListEmployee()
        dispatchListEmployee?.recordStatus = EnumListRecordStatus.NEW
        dispatchListEmployee?.transientData.put("firm", dispatchList?.firm);

        dispatchListEmployee?.periodInMonths = dispatchRequest?.periodInMonths
        dispatchListEmployee?.fromDate = dispatchRequest?.fromDate
        dispatchListEmployee?.toDate = dispatchRequest?.toDate
        dispatchListEmployee?.nextVerificationDate = dispatchRequest?.nextVerificationDate

        dispatchListEmployee?.organizationId = dispatchRequest?.organizationId
        dispatchListEmployee?.organizationName = dispatchRequest?.organizationName
        dispatchListEmployee?.locationId = dispatchRequest?.locationId
        dispatchListEmployee?.educationMajorId = dispatchRequest?.educationMajorId
        dispatchListEmployee?.educationMajorName = dispatchRequest?.educationMajorName
        dispatchListEmployee?.unstructuredLocation = dispatchRequest?.unstructuredLocation

        dispatchListEmployee?.dispatchList = dispatchList
        dispatchListEmployee?.dispatchRequest = dispatchRequest
        dispatchListEmployee?.currentEmployeeMilitaryRank = dispatchRequest?.currentEmployeeMilitaryRank
        dispatchListEmployee?.currentEmploymentRecord = dispatchRequest?.currentEmploymentRecord
        if (dispatchRequest?.requestStatusNote) {
            DispatchListEmployeeNote note = new DispatchListEmployeeNote(
                    noteDate: dispatchRequest?.requestDate,
                    note: dispatchRequest?.requestStatusNote,
                    orderNo: "",
                    dispatchListEmployee: dispatchListEmployee,
            );
            if (!note?.validate()) {
                dispatchList.errors.addAllErrors(note?.errors)
                throw new Exception("Error occurred while add note to the list employee record.")
            } else {
                //add note to join
                dispatchListEmployee.addToDispatchListEmployeeNotes(note);
            }
        }
        return dispatchListEmployee
    }

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    DispatchList sendList(GrailsParameterMap params) {
        //return dispatchListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            DispatchList dispatchList = DispatchList.get(params["id"])
            //return error if no request added to list
            if (dispatchList?.dispatchListEmployees?.size() == 0) {
                dispatchList.errors.reject("list.sendList.error")
                return dispatchList
            }
            if (dispatchList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = dispatchList
                    correspondenceListStatus.receivingParty = dispatchList?.receivingParty
                    correspondenceListStatus.firm = dispatchList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        dispatchList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we send the marital status list
                    dispatchList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all childListEmployee in marital status list to change the status of the request
                    dispatchList?.dispatchListEmployees?.each { DispatchListEmployee dispatchListEmployee ->
                        //childListEmployee?.childRequest?.childListEmployee = childListEmployee
                        dispatchListEmployee?.dispatchRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        dispatchListEmployee?.dispatchRequest?.validate()
                    }

                    //save the disciplinary list changes
                    dispatchList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (dispatchList?.errors?.allErrors?.size() == 0) {
                        dispatchList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return dispatchList
            }
        } else {
            DispatchList dispatchList = new DispatchList()
            dispatchList.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchList.label', null, 'dispatchList', LocaleContextHolder.getLocale())] as Object[], "This dispatchList with ${params.id} not found")
            return dispatchList
        }
    }

    /**
     * receive marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    DispatchList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return DispatchList is
            DispatchList dispatchList = DispatchList.load(params["id"])
            if (dispatchList) {
                try {
                    //to change the correspondenceListStatus to received when we received the dispatch list
                    // and change the to date to the date of receive DispatchList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['toDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = dispatchList
                    correspondenceListStatus.receivingParty = dispatchList?.receivingParty
                    correspondenceListStatus.firm = dispatchList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        dispatchList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we receive the dispatch list
                    dispatchList.manualIncomeNo = params.manualIncomeNo
                    //save the dispatch list changes
                    dispatchList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (dispatchList?.errors?.allErrors?.size() == 0) {
                        dispatchList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return dispatchList
            }
        } else {
            DispatchList dispatchList = new DispatchList()
            dispatchList.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchList.label', null, 'dispatchList', LocaleContextHolder.getLocale())] as Object[], "This dispatchList with ${params.id} not found")
            return dispatchList
        }
    }

    /**
     * to change the dispatchList request status to Approved in the dispatchList and save new statuses in core
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToApproved(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("check_dispatchRequestTableInDispatchList");
        params.remove("check_dispatchRequestTableInDispatchList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of dispatch requests :
            List<DispatchListEmployee> dispatchListEmployeeList = DispatchListEmployee.executeQuery("from DispatchListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (dispatchListEmployeeList) {
                dispatchListEmployeeList.each { DispatchListEmployee dispatchListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate']) || params.note || params.orderNo) {
                            DispatchListEmployeeNote note = new DispatchListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    dispatchListEmployee: dispatchListEmployee,
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
                                dispatchListEmployee.addToDispatchListEmployeeNotes(note);
                            }
                        }

                        if (!errors) {
                            //change the dispatch list employee record status
                            dispatchListEmployee?.recordStatus = EnumListRecordStatus.APPROVED

                            //save the instant
                            dispatchListEmployee.save(failOnError: true, flush: true)
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
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                saved = false

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
     * to change the dispatchList request status to rejected in the receive dispatchList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToRejected(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("check_dispatchRequestTableInDispatchList");
        params.remove("check_dispatchRequestTableInDispatchList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of dispatch requests :
            List<DispatchListEmployee> dispatchListEmployeeList = DispatchListEmployee.executeQuery("from DispatchListEmployee p where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (dispatchListEmployeeList) {
                dispatchListEmployeeList.each { DispatchListEmployee dispatchListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            DispatchListEmployeeNote note = new DispatchListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    dispatchListEmployee: dispatchListEmployee,
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
                                dispatchListEmployee.addToDispatchListEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }

                        //if there are no errors, save the changes
                        if (!errors) {
                            //change the dispatch list employee record status
                            dispatchListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                            //save the request employee instance:
                            dispatchListEmployee.save(flush: true, failOnError: true);
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
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                saved = false
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
     * close dispatch list
     * when close the list, we do loop on all approved requests to reflect the changes on employee profile.
     * @param GrailsParameterMap params
     * @return boolean
     */
    DispatchList closeList(GrailsParameterMap params) {
        //return dispatchListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            DispatchList dispatchList = DispatchList.get(params["id"])
            if (dispatchList) {
                try {
                    //to change the correspondenceListStatus to submitted when we close the dispatch list
                    // and change the from date when closing the list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = dispatchList
                    correspondenceListStatus.receivingParty = dispatchList?.receivingParty
                    //params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = dispatchList?.firm

                    if (DispatchListEmployee.countByDispatchListAndRecordStatus(dispatchList, EnumListRecordStatus.NEW) > 0) {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                    } else {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                    }

                    if (correspondenceListStatus.save()) {
                        dispatchList.currentStatus = correspondenceListStatus
                    }
                    DispatchRequest dispatchRequest
                    dispatchList?.dispatchListEmployees?.each { DispatchListEmployee dispatchListEmployee ->
                        dispatchRequest = dispatchListEmployee?.dispatchRequest

                        // set external order numbers
                        List <ListNote> orderNumberNoteList = dispatchListEmployee?.dispatchListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                        if(orderNumberNoteList?.size() > 0){
                            ListNote orderNumberNote = orderNumberNoteList?.last()
                            dispatchRequest.externalOrderNumber= orderNumberNote.orderNo
                            dispatchRequest.externalOrderDate= orderNumberNote.noteDate
                        }


                        if (dispatchListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            dispatchRequest?.requestStatus = EnumRequestStatus.APPROVED

                            // reflect request changes
                            requestChangesHandlerService.applyRequestChanges(dispatchRequest)

                        } else {
                            dispatchRequest?.requestStatus = EnumRequestStatus.REJECTED
                        }
                        dispatchRequest?.validate()
                        dispatchRequest?.save(failOnError: true, flush: true)
                    }
                    //save the disciplinary list changes
                    dispatchList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (dispatchList?.errors?.allErrors?.size() == 0) {
                        dispatchList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return dispatchList
            }
        } else {
            DispatchList dispatchList = new DispatchList()
            dispatchList.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchList.label', null, 'dispatchList', LocaleContextHolder.getLocale())] as Object[], "This dispatchList with ${params.id} not found")
            return dispatchList
        }
    }

    /*
     * update employee status in case new dispatch--------------------------------------------
     *    if the request is dispatch request
     *       - change current employee status to be dispatched (create new status with type: DISPATCH)
     *       - end the working employee status by adding endDate
     * update employee status to end the dispatch in case stop dispatch--------------------------------------------
     *    if the request is stop dispatch request
     *       - change current employee status to be Working (create new status with type: WORKING)
     *       - end the dispatch employee status by adding endDate
     */

    void updateEmployeeStatusToDispatch(Firm firm) {
        //TODO: to be called by the job!
        try {
            //return the list of all dispatchListEmployee which are approved but still not reflected into employee status
            List<DispatchListEmployee> dispatchListEmployees = DispatchListEmployee.executeQuery("from DispatchListEmployee dle where " +
                    "dle.fromDate <= :currentDate and dle.recordStatus = :recordStatus and " +
                    " dle.dispatchList.id in " +
                    "(select id from DispatchList l where l.firm.id = :firmId and l.currentStatus.id in " +
                    " ( select id from CorrespondenceListStatus c where c.correspondenceList.id = l.id and c.correspondenceListStatus in ( :correspondenceListStatusList)) )" +
                    ") ",
                    [currentDate: ZonedDateTime.now(), recordStatus: EnumListRecordStatus.APPROVED, firmId: firm?.id, correspondenceListStatusList: [EnumCorrespondenceListStatus.CLOSED, EnumCorrespondenceListStatus.PARTIALLY_CLOSED]])

            //get the employee status : Dispatch
            EmployeeStatus employeeStatusDispatched = EmployeeStatus.get(EnumEmployeeStatus.DISPATCHED.getValue(firm?.code))
            //get the employee status : working
            EmployeeStatus employeeStatusWorking = EmployeeStatus.get(EnumEmployeeStatus.WORKING.getValue(firm?.code))
            EmployeeStatusHistory employeeStatusHistory

            //loop on each employee to update the status history , and update the employee list record status
            dispatchListEmployees.each { DispatchListEmployee dispatchListEmployee ->

                if ((dispatchListEmployee?.dispatchRequest?.requestType == EnumRequestType.DISPATCH_REQUEST)) {
                    //update employee status history to close the working status and open new dispatch status
                    if (employeeStatusDispatched) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory?.employee = dispatchListEmployee?.dispatchRequest?.employee
                        employeeStatusHistory?.fromDate = dispatchListEmployee?.fromDate
                        employeeStatusHistory?.employeeStatus = employeeStatusDispatched
                        employeeStatusHistory?.transientData.put("firm", firm);
                        employeeStatusHistory.save(flush: true, failOnError: true)
                        dispatchListEmployee?.dispatchRequest?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                    }
                    if (employeeStatusWorking) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory = EmployeeStatusHistory.executeQuery("FROM EmployeeStatusHistory esh where " +
                                " esh.employeeStatus.id=:employeeStatusId and esh.employee.id=:employeeId and esh.toDate=:nullDate", [
                                employeeStatusId: employeeStatusWorking?.id,
                                employeeId      : dispatchListEmployee?.dispatchRequest?.employee?.id,
                                nullDate        : PCPUtils.DEFAULT_ZONED_DATE_TIME
                        ])[0]
                        if (employeeStatusHistory) {
                            employeeStatusHistory?.toDate = dispatchListEmployee?.fromDate
                            employeeStatusHistory?.save(flush: true, failOnError: true)
                        }
                    }
                }
                if ((dispatchListEmployee?.dispatchRequest?.requestType == EnumRequestType.DISPATCH_STOP_REQUEST)) {
                    //update employee status history to close the dispatch status and open new working status
                    if (employeeStatusWorking) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory?.employee = dispatchListEmployee?.dispatchRequest?.employee
                        employeeStatusHistory?.fromDate = dispatchListEmployee?.toDate
                        //the date of close(stop) the dispatch
                        employeeStatusHistory?.employeeStatus = employeeStatusWorking
                        employeeStatusHistory?.transientData.put("firm", firm);
                        employeeStatusHistory.save(flush: true, failOnError: true)
                        dispatchListEmployee?.dispatchRequest?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                    }
                    if (employeeStatusDispatched) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory = EmployeeStatusHistory.createCriteria().list() {
                            eq('employeeStatus.id', employeeStatusDispatched.id)
                            eq('employee.id', dispatchListEmployee?.dispatchRequest?.employee?.id)
                            eq('toDate', PCPUtils.DEFAULT_ZONED_DATE_TIME)
                            order("trackingInfo.dateCreatedUTC", "desc")
                        }[0]
                        if (employeeStatusHistory) {
                            employeeStatusHistory?.toDate = dispatchListEmployee?.toDate
                            // the date of close(stop) the dispatch
                            employeeStatusHistory?.save(flush: true, failOnError: true)
                        }
                    }
                }//if statement
                //end change employee status--------------------------------------------

                dispatchListEmployee?.recordStatus = EnumListRecordStatus.REFLECTED
                dispatchListEmployee?.validate()
                dispatchListEmployee?.dispatchRequest?.employee?.validate()
                dispatchListEmployee?.save(flush: true, failOnError: true);
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }

    /**
     * get list of dispatch request which its (next verification date will be after 3 days)
     * @params from date.
     * @params to date.
     * @return list of dispatchRequest.
     */
    private List getListOfDispatchRequest(Firm firm) {
        //TODO: to be called by the job!
        try {
            final session = sessionFactory.currentSession
            def list = []

            //query to get all recruitmentCycle where phase status is open.
            String query = "from dispatch_request dr, request r, employee e " +
                    " where  r.status= '${GeneralStatus.ACTIVE}' " +
                    " and r.firm_id=:firmId " +
                    " and r.id =dr.id " +
                    " and r.request_status = '${EnumRequestStatus.APPROVED}' " +
                    " and dr.next_verification_date_datetime <= :nextVerification " +
                    " and r.employee_id = e.id " +
                    " and  NOT EXISTS ( " +
                    " select * from notification " +
                    " where notification_type_id=${EnumNotificationType.MY_NOTIFICATION.toString()} " +
                    " and object_source_id=dr.id " +
                    " and object_source_reference='${DispatchRequest.getName()}' )" +
                    " group by dr.id, dr.next_verification_date_datetime, r.employee_id, e.person_id "

            //create sqlQuery
            Query sqlQuery = session.createSQLQuery("select  " +
                    "dr.id, " +
                    "r.employee_id, " +
                    "dr.next_verification_date_datetime, " +
                    "e.person_id "
                    + query)

            //fill map parameter
            Map sqlParamsMap = [firmId          : firm?.id,
                                nextVerification: PCPUtils.convertZonedDateTimeToTimeStamp(ZonedDateTime.now().plusDays(3))]

            //assign value to each parameter
            sqlParamsMap?.each {
                sqlQuery.setParameter(it.key.toString(), it.value)
            }

            //execute query
            final queryResults = sqlQuery?.list()

            //fill result into list
            queryResults?.eachWithIndex { def entry, int i ->
                list.add(entry)
            }
            //return query list
            return list


        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }

    Boolean sendDispatchRequestNotification(Firm firm) {
        // variables for notifications
        Boolean isRequestUpdated = false
        Map notificationParams = [:]
        GrailsParameterMap notificationGrailsParams
        SearchBean searchBean = null
        UserDTO userDTO
        Map<String, Map<Integer, String>> notificationTermsMap = [:]
        Map<Integer, String> notificationKeys = [:]
        Map<Integer, String> notificationValues = [:]

        //get list of recruitmentCycle
        List resultList = getListOfDispatchRequest(firm).toList()

        resultList?.eachWithIndex { entry, index ->
            try {
                //fill notification params and save notification
                notificationParams["objectSourceId"] = "${entry[0]}"
                notificationParams.objectSourceReference = DispatchRequest.getName()
                notificationParams.title = "${messageSource.getMessage("dispatchRequest.label", [] as Object[], new Locale("ar"))}"
                notificationParams.notificationDate = ZonedDateTime.now()
                notificationParams["notificationType"] = NotificationType.findByTopic("myNotification")

                notificationTermsMap = [:]
                notificationKeys = [:]
                notificationValues = [:]

                //get employee username
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("personId", new SearchConditionCriteriaBean(operand: 'personId', value1: entry[3]))
                searchBean.searchCriteria.put("firmId", new SearchConditionCriteriaBean(operand: 'firmId', value1: firm?.id))
                userDTO = remoteUserService.getUser(searchBean)

                //set role
                notificationKeys.put(new Integer(1), UserTerm.USER.value())
                notificationValues.put(new Integer(1), "${userDTO?.username}")

                notificationParams.text = "${messageSource.getMessage("dispatchRequest.notification.nextDispatchWarning.message", ["${userDTO?.personName}", "${entry[2]}"] as Object[], new Locale("ar"))}"

                notificationTermsMap.put("key", notificationKeys)
                notificationTermsMap.put("value", notificationValues)
                notificationParams["notificationTerms"] = notificationTermsMap

                //create empty grails parameter map
                notificationGrailsParams = new GrailsParameterMap(notificationParams, null)

                //save notification
                notificationService.save(notificationGrailsParams)
            } catch (Exception ex) {
                ex.printStackTrace()
                return false
            }
        }
    }

    /**
     * custom search to find the number of requests in the dispatch  list in one select statement for performance issue
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
        ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchListType dispatchListType = params["dispatchListType"] ? ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchListType.valueOf(params["dispatchListType"]) : null
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

        //the query to retrieve the list details, num of dispatchs in the list, the send date, and the current list status
        String query = "FROM dispatch_list al  LEFT JOIN " +
                "  (SELECT ale.dispatch_list_id ,count(ale.id) no_of_employee" +
                "  FROM dispatch_list_employee ale " +
                "  group by ale.dispatch_list_id ) b" +
                "  on al.id= b.dispatch_list_id , correspondence_list_status cls,correspondence_list cl" +
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
        }  else if (columnName) {
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

        List<DispatchList> results = []
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

            DispatchList dispatchList = new DispatchList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[15],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            dispatchList.id = resultRow[0]
            //dispatchList.dispatchListType = resultRow[14]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            dispatchList.currentStatus = currentStatus
            results.add(dispatchList)
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