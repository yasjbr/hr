package ps.gov.epsilon.hr.firm.suspension

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationParams
import ps.police.notifications.enums.UserTerm
import ps.police.pcore.v2.entity.person.PersonService

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service  aims to create suspension extension list
 * <h1>Usage</h1>
 * -this service used to create suspension extension list
 * <h1>Restriction</h1>
 * -need a firm created before.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionExtensionListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    PersonService personService
    SuspensionExtensionListEmployeeService suspensionExtensionListEmployeeService
    RequestService requestService

    //to get the value of requisition status
    public static currentStatusValue = { cService, SuspensionExtensionList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, SuspensionExtensionList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../suspensionExtensionList/manageList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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

        //search about list with status:
        ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus listCurrentStatusValue = params["listCurrentStatusValue"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["listCurrentStatusValue"] as String) : null

        List<Map<String, String>> orderBy = params.list("orderBy")
        Set suspensionExtensionListEmployeesIds = params.listString("suspensionExtensionListEmployees.id")
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return SuspensionExtensionList.createCriteria().list(max: max, offset: offset) {
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
                if (suspensionExtensionListEmployeesIds) {
                    suspensionExtensionListEmployees {
                        inList("id", suspensionExtensionListEmployeesIds)
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
                if (listCurrentStatusValue) {
                    currentStatus {
                        eq("correspondenceListStatus", listCurrentStatusValue)
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
    public PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedResultList = customSearch(params)
        if (pagedResultList) {
            return pagedResultList
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return SuspensionExtensionList.
     */
    SuspensionExtensionList save(GrailsParameterMap params) {
        SuspensionExtensionList suspensionExtensionListInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            suspensionExtensionListInstance = SuspensionExtensionList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (suspensionExtensionListInstance.version > version) {
                    suspensionExtensionListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionExtensionList.label', null, 'suspensionExtensionList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionExtensionList while you were editing")
                    return suspensionExtensionListInstance
                }
            }
            if (!suspensionExtensionListInstance) {
                suspensionExtensionListInstance = new SuspensionExtensionList()
                suspensionExtensionListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionExtensionList.label', null, 'suspensionExtensionList', LocaleContextHolder.getLocale())] as Object[], "This suspensionExtensionList with ${params.id} not found")
                return suspensionExtensionListInstance
            }
        } else {
            suspensionExtensionListInstance = new SuspensionExtensionList()
        }
        try {
            suspensionExtensionListInstance.properties = params;
            suspensionExtensionListInstance.save();
            CorrespondenceListStatus correspondenceListStatus
            if (!params.id) {
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: EnumReceivingParty.SARAYA, firm: suspensionExtensionListInstance.firm)
                suspensionExtensionListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            suspensionExtensionListInstance.save(flush: true, failOnError: true);
            //save the current status:
            if (correspondenceListStatus?.id && suspensionExtensionListInstance?.id) {
                suspensionExtensionListInstance?.currentStatus = correspondenceListStatus
                suspensionExtensionListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            suspensionExtensionListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionExtensionListInstance
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return SuspensionExtensionList.
     */
    @Transactional(readOnly = true)
    SuspensionExtensionList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                SuspensionExtensionList suspensionExtensionList = results?.resultList[0]
                return suspensionExtensionList
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
            SuspensionExtensionList instance = SuspensionExtensionList.get(id)
            //to be able to delete an suspension_extension list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.suspensionExtensionListEmployees?.size() == 0) {
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
     * add suspensionExtension request to the list
     * @param params
     * @return suspensionExtension list instance
     */
    SuspensionExtensionList addRequestToList(GrailsParameterMap params) {

        SuspensionExtensionList suspensionExtensionList = SuspensionExtensionList.load(params["suspensionExtensionListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");
        params.remove("checked_requestIdsList");

        if (checkedRequestIdList.size() > 0) {
            SuspensionExtensionListEmployee suspensionExtensionListEmployee = null
            //retrieve the selected services:
            List<SuspensionExtensionRequest> suspensionExtensionRequests = SuspensionExtensionRequest.executeQuery("from SuspensionExtensionRequest c where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (suspensionExtensionRequests) {
                try {
                    suspensionExtensionRequests.each { SuspensionExtensionRequest suspensionExtensionRequest ->
                        //create new service list employee and add the service Request
                        suspensionExtensionListEmployee = new SuspensionExtensionListEmployee()
                        suspensionExtensionListEmployee?.recordStatus = EnumListRecordStatus.NEW
                        suspensionExtensionListEmployee?.currentEmployeeMilitaryRank = suspensionExtensionRequest?.suspensionRequest?.currentEmployeeMilitaryRank
                        suspensionExtensionListEmployee?.currentEmploymentRecord = suspensionExtensionRequest?.suspensionRequest?.currentEmploymentRecord
                        suspensionExtensionListEmployee?.suspensionExtensionRequest = suspensionExtensionRequest
                        suspensionExtensionListEmployee?.fromDate = suspensionExtensionRequest?.fromDate
                        suspensionExtensionListEmployee?.toDate = suspensionExtensionRequest?.toDate
                        suspensionExtensionListEmployee?.periodInMonth = suspensionExtensionRequest?.periodInMonth
                        suspensionExtensionListEmployee?.suspensionExtensionList = suspensionExtensionList
                        suspensionExtensionListEmployee?.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                        //add the service list employee to list

                        if (suspensionExtensionRequest?.requestStatusNote) {
                            SuspensionExtensionListEmployeeNote note = new SuspensionExtensionListEmployeeNote(
                                    noteDate: suspensionExtensionRequest?.requestDate,
                                    note: suspensionExtensionRequest?.requestStatusNote,
                                    orderNo: "",
                                    suspensionExtensionListEmployee: suspensionExtensionListEmployee,
                            );
                            if (!note?.validate()) {
                                suspensionExtensionList?.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                suspensionExtensionListEmployee.addToSuspensionExtensionListEmployeeNotes(note);
                            }
                        }

                        suspensionExtensionRequest.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        suspensionExtensionList.addToSuspensionExtensionListEmployees(suspensionExtensionListEmployee);
                    }
                    suspensionExtensionList?.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (suspensionExtensionList?.errors?.allErrors?.size() > 0) {
                        suspensionExtensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            suspensionExtensionList.errors.reject("list.request.notSelected.error")
        }
        return suspensionExtensionList
    }//save

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    SuspensionExtensionList sendData(GrailsParameterMap params) {
        //return suspensionExtensionListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //return the corresponding list
        SuspensionExtensionList suspensionExtensionList = SuspensionExtensionList.get(params["id"])

        //return error if no request added to list
        if (suspensionExtensionList?.suspensionExtensionListEmployees?.size() == 0) {
            suspensionExtensionList.errors.reject("list.sendList.error")
            return suspensionExtensionList
        }

        if (params.id) {
            if (suspensionExtensionList) {
                //to change the correspondenceListStatus to submitted when we send the marital status list
                // and change the from date to the date of sending marital status list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = suspensionExtensionList
                correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                correspondenceListStatus.firm = suspensionExtensionList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                if (correspondenceListStatus.save()) {
                    suspensionExtensionList.currentStatus = correspondenceListStatus
                }
                //enter the manualIncomeNo when we send the marital status list
                suspensionExtensionList.manualOutgoingNo = params.manualOutgoingNo
                //loop in all suspensionExtensionListEmployee in marital status list to change the status of the request
                suspensionExtensionList?.suspensionExtensionListEmployees?.each { SuspensionExtensionListEmployee suspensionExtensionListEmployee ->
                    suspensionExtensionListEmployee?.suspensionExtensionRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                    suspensionExtensionListEmployee?.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }


                try {
                    //save the disciplinary list changes
                    suspensionExtensionList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (suspensionExtensionList?.errors?.allErrors?.size() == 0) {
                        suspensionExtensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return suspensionExtensionList
            }
        }
        return suspensionExtensionList
    }

    /**
     * receive marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    SuspensionExtensionList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //return SuspensionExtensionList is
        SuspensionExtensionList suspensionExtensionList = SuspensionExtensionList.load(params["id"])
        if (params.id) {

            if (suspensionExtensionList) {
                //to change the correspondenceListStatus to received when we receive the SuspensionExtensionList
                // and change the to date to the date of receive SuspensionExtensionList
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus(
                        fromDate: ZonedDateTime.now(),
                        toDate: PCPUtils.parseZonedDateTime(params['toDate']),
                        correspondenceList: suspensionExtensionList,
                        receivingParty: params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null,
                        firm: suspensionExtensionList?.firm,
                        correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED)
                suspensionExtensionList.currentStatus = correspondenceListStatus

                //enter the manualIncomeNo when we receive the suspensionExtension list
                suspensionExtensionList.manualIncomeNo = params.manualIncomeNo

                //loop in all SuspensionExtensionListEmployee in SuspensionExtensionList to change the status of the suspensionExtensionRequest
                suspensionExtensionList?.suspensionExtensionListEmployees?.each { SuspensionExtensionListEmployee suspensionExtensionListEmployee ->
                    suspensionExtensionListEmployee?.suspensionExtensionRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                    suspensionExtensionListEmployee?.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }
                try {
                    //save the disciplinary list changes
                    suspensionExtensionList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (suspensionExtensionList?.errors?.allErrors?.size() == 0) {
                        suspensionExtensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return suspensionExtensionList
            }
        }
        return suspensionExtensionList
    }

    /**
     * to change the suspensionExtension List employee status to EMPLOYED in the receive suspensionExtension list
     * @param GrailsParameterMap params
     * @return SuspensionExtensionList
     */
    SuspensionExtensionList approveRequest(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        /**
         * to get list of  ids of suspensionExtension request
         */
        List suspensionExtensionListEmployeeIds = params.listString("check_suspensionExtensionRequestTableInSuspensionExtensionList")
        params.remove("check_suspensionExtensionRequestTableInSuspensionExtensionList")
        SuspensionExtensionListEmployee suspensionExtensionListEmployee
        List<SuspensionExtensionListEmployee> suspensionExtensionListEmployeeList
        SuspensionExtensionList suspensionExtensionList = null
        /**
         * get selected suspensionExtension list employee
         */
        GrailsParameterMap suspensionExtensionListEmployeeParam = new GrailsParameterMap(["ids[]": suspensionExtensionListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        suspensionExtensionListEmployeeList = suspensionExtensionListEmployeeService?.search(suspensionExtensionListEmployeeParam)

        /**
         * get suspensionExtensionList
         */
        suspensionExtensionList = SuspensionExtensionList.get(params["id"])


        if (suspensionExtensionListEmployeeIds) {


            suspensionExtensionListEmployeeIds?.each { String id ->
                /**
                 * get suspensionExtension list employee by id
                 */
                suspensionExtensionListEmployee = suspensionExtensionListEmployeeList?.find { it?.id == id }

                /**
                 * change suspension extension list employee status
                 */
                suspensionExtensionListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                suspensionExtensionListEmployee?.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME

                /**
                 * note is required in reject.
                 */
                if (params.note || params.orderNo) {
                    suspensionExtensionListEmployee?.addToSuspensionExtensionListEmployeeNotes(new SuspensionExtensionListEmployeeNote(suspensionExtensionListEmployee: suspensionExtensionListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                }
            }
            try {

                suspensionExtensionList.save(failOnError: true)
            } catch (Exception ex) {
                ex.printStackTrace()
                transactionStatus.setRollbackOnly()
                if (suspensionExtensionList?.hasErrors()) {
                    suspensionExtensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
        } else {
            suspensionExtensionList.errors.reject('suspensionExtensionList.error.not.selected.request.message')
            return suspensionExtensionList
        }
        return suspensionExtensionList
    }

    /**
     * to change the suspensionExtension List employee status to NOT_EMPLOYED in the receive suspensionExtension list
     * @param GrailsParameterMap params
     * @return SuspensionExtensionList
     */
    SuspensionExtensionList rejectRequest(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        SuspensionExtensionListEmployee suspensionExtensionListEmployee
        List<SuspensionExtensionListEmployee> suspensionExtensionListEmployeeList
        SuspensionExtensionList suspensionExtensionList = null

        /**
         * get suspensionExtensionList
         */
        suspensionExtensionList = SuspensionExtensionList.get(params["id"])


        if (params.note || params.orderNo) {

            /**
             * to get list of  ids of suspensionExtension request
             */
            List suspensionExtensionListEmployeeIds = params.listString("check_suspensionExtensionRequestTableInSuspensionExtensionList")
            params.remove("check_suspensionExtensionRequestTableInSuspensionExtensionList")

            /**
             * get selected suspensionExtension request
             */
            GrailsParameterMap suspensionExtensionListEmployeeParam = new GrailsParameterMap(["ids[]": suspensionExtensionListEmployeeIds], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            suspensionExtensionListEmployeeList = suspensionExtensionListEmployeeService?.search(suspensionExtensionListEmployeeParam)

            if (suspensionExtensionListEmployeeIds) {
                suspensionExtensionListEmployeeIds?.each { String id ->

                    /**
                     * get suspensionExtension list employee
                     */
                    suspensionExtensionListEmployee = suspensionExtensionListEmployeeList?.find { it?.id == id }

                    /**
                     * change suspensionExtension list employee status
                     */
                    suspensionExtensionListEmployee?.recordStatus = EnumListRecordStatus.REJECTED
                    suspensionExtensionListEmployee?.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    /*
                    * note is required in reject.
                    */
                    if (params.note != null || params.orderNo != null) {
                        suspensionExtensionListEmployee?.addToSuspensionExtensionListEmployeeNotes(new SuspensionExtensionListEmployeeNote(suspensionExtensionListEmployee: suspensionExtensionListEmployee, orderNo: params.orderNo, note: params.note, noteDate: PCPUtils.parseZonedDateTime(params['noteDate'])))
                    }
                }

                try {
                    suspensionExtensionList.save(failOnSave: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (suspensionExtensionList?.hasErrors()) {
                        suspensionExtensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            } else {
                suspensionExtensionList.errors.reject('suspensionExtensionList.error.not.selected.request.message')
                return suspensionExtensionList
            }
        } else {
            suspensionExtensionList.errors.reject('suspensionExtensionList.not.requestRejected.message')
            return suspensionExtensionList
        }
        return suspensionExtensionList
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

        //the query to retrieve the list details, num of suspension_extensions in the list, the send date, and the current list status
        String query = "FROM suspension_extension_list al  LEFT JOIN " +
                "  (SELECT ale.suspension_extension_list_id ,count(ale.id) no_of_employee" +
                "  FROM suspension_extension_list_employee ale " +
                "  group by ale.suspension_extension_list_id ) b" +
                "  on al.id= b.suspension_extension_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<SuspensionExtensionList> results = []
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

            SuspensionExtensionList suspensionExtensionList = new SuspensionExtensionList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            suspensionExtensionList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            suspensionExtensionList.currentStatus = currentStatus
            results.add(suspensionExtensionList)
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
     * close suspensionExtension list
     * @param GrailsParameterMap params
     * @return SuspensionExtensionList
     */
    SuspensionExtensionList closeList(GrailsParameterMap params) {

        //return List id
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        SuspensionExtensionList suspensionExtensionList = null
        List<SuspensionExtensionListEmployee> suspensionExtensionListEmployeeList
        if (params.id) {
            //return the correspondence list:
            suspensionExtensionList = SuspensionExtensionList?.get(params["id"])

            suspensionExtensionListEmployeeList = SuspensionExtensionListEmployee.executeQuery("From SuspensionExtensionListEmployee suspensionExtensionListEmployee where suspensionExtensionListEmployee.suspensionExtensionList.id= :suspensionExtensionListId and  suspensionExtensionListEmployee.recordStatus= :recordStatus", [suspensionExtensionListId: suspensionExtensionList?.id, recordStatus: EnumListRecordStatus.NEW])

            //to change the correspondenceListStatus to submitted when we close the suspensionExtension list
            CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
            correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
            correspondenceListStatus.correspondenceList = suspensionExtensionList
            correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA
            correspondenceListStatus.firm = suspensionExtensionList?.firm


            if (suspensionExtensionListEmployeeList) {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED

            } else {
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED

            }
            if (suspensionExtensionList) {
                if (correspondenceListStatus.save()) {
                    suspensionExtensionList.currentStatus = correspondenceListStatus
                }

                /**
                 * change request status to  list employee status
                 */
                SuspensionExtensionRequest suspensionExtensionRequest = null
                List<UserTerm> userTermKeyList = []
                List<String> userTermValueList = []
                List<Map> notificationActionsMap = null
                suspensionExtensionList?.suspensionExtensionListEmployees?.each { SuspensionExtensionListEmployee suspensionExtensionListEmployee ->
                    suspensionExtensionRequest = suspensionExtensionListEmployee.suspensionExtensionRequest

                    /**
                     * set request status
                     */
                    if (suspensionExtensionListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                        suspensionExtensionListEmployee?.suspensionExtensionRequest?.requestStatus = EnumRequestStatus.APPROVED
                    } else {
                        suspensionExtensionListEmployee?.suspensionExtensionRequest?.requestStatus = EnumRequestStatus.REJECTED
                    }

                    suspensionExtensionListEmployee.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME

                    //set notification user.
                    userTermKeyList = []
                    userTermValueList = []
                    userTermKeyList.add(UserTerm.USER)
                    userTermValueList.add("${suspensionExtensionRequest?.employee?.personId}")

                    userTermKeyList.add(UserTerm.FIRM)
                    userTermValueList.add("${PCPSessionUtils.getValue("firmId")}")

                    //we add link into notification to manageSuspensionExtensionList.
                    notificationActionsMap = [
                            [action            : "manageList",
                             controller        : "suspensionExtensionList",
                             label             : "${messageSource.getMessage("default.show.label", [] as Object[], LocaleContextHolder.getLocale())}",
                             icon              : "icon-cog",

                             notificationParams: [
                                     new NotificationParams(name: "encodedId", value: suspensionExtensionList?.encodedId),
                             ]
                            ]
                    ]

                    //create notification
                    requestService?.createRequestNotification(suspensionExtensionRequest?.id,
                            SuspensionExtensionRequest.getName(),
                            ZonedDateTime.now()?.minusDays(1),
                            suspensionExtensionRequest.requestStatus,
                            userTermKeyList,
                            userTermValueList,
                            null,
                            EnumNotificationType.MY_NOTIFICATION,
                            null)

                }

                try {
                    suspensionExtensionList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (suspensionExtensionList?.hasErrors()) {
                        suspensionExtensionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }

        }
        return suspensionExtensionList
    }

}