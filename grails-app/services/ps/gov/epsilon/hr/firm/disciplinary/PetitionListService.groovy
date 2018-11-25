package ps.gov.epsilon.hr.firm.disciplinary

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
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notifications.NotificationService
import java.sql.Timestamp
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to manage the petition list for petition requests which should be sent to third party-
 * <h1>Usage</h1>
 * - insert petition requests to list-
 * - send, receive and close list-
 * - approve and reject the requests-
 * - cancel disciplinary request on close the list.
 * <h1>Restriction</h1>
 * - create should be automatically
 * - edit is allowed if the list is NEW state only.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class PetitionListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    EmployeeService employeeService
    NotificationService notificationService

    //to get the value of requisition status
    public static currentStatusValue = { cService, PetitionList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, PetitionList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../petitionList/managePetitionList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set petitionListEmployeesIds = params.listString("petitionListEmployees.id")
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return PetitionList.createCriteria().list(max: max, offset: offset) {
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
                if (petitionListEmployeesIds) {
                    petitionListEmployees {
                        inList("id", petitionListEmployeesIds)
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
     * @return PetitionList.
     */
    PetitionList save(GrailsParameterMap params) {
        PetitionList petitionListInstance
        //in case: id is encoded
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            petitionListInstance = PetitionList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (petitionListInstance.version > version) {
                    petitionListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('petitionList.label', null, 'petitionList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this petitionList while you were editing")
                    return petitionListInstance
                }
            }
            if (!petitionListInstance) {
                petitionListInstance = new PetitionList()
                petitionListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('petitionList.label', null, 'petitionList', LocaleContextHolder.getLocale())] as Object[], "This petitionList with ${params.id} not found")
                return petitionListInstance
            }
        } else {
            petitionListInstance = new PetitionList()
        }
        try {
            petitionListInstance.properties = params;
            //create new status for the list:
            CorrespondenceListStatus correspondenceListStatus
            if (!params.id) {
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus(
                        fromDate: ZonedDateTime.now(),
                        toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                        correspondenceListStatus: EnumCorrespondenceListStatus.CREATED,
                        correspondenceList: petitionListInstance,
                        receivingParty: EnumReceivingParty.SARAYA,
                        firm: petitionListInstance?.firm)
                petitionListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            petitionListInstance.save(flush: true);
            //save the current status:
            if (correspondenceListStatus?.id && petitionListInstance?.id) {
                petitionListInstance?.currentStatus = correspondenceListStatus
                petitionListInstance.save(flush: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            petitionListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return petitionListInstance
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
            PetitionList instance = PetitionList.get(id)
            //to be able to delete an vacation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.petitionListEmployees?.size() == 0) {
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
     * @return PetitionList.
     */
    @Transactional(readOnly = true)
    PetitionList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                PetitionList petitionList = results?.resultList[0]
                return petitionList
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
    PetitionList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                PetitionList instance = results?.resultList[0]
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
     * to add request instance to petitionList
     * @param GrailsParameterMap params
     * @return boolean
     */
    PetitionList addRequestToList(GrailsParameterMap params) {
        PetitionList petitionList = PetitionList.load(params["petitionListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            PetitionListEmployee petitionListEmployee
            //retrieve the selected promotions:
            List<PetitionRequest> requests = PetitionRequest.executeQuery("from PetitionRequest d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (requests) {
                try {
                    requests.each { PetitionRequest request ->
                        petitionListEmployee = new PetitionListEmployee()
                        petitionListEmployee?.recordStatus = EnumListRecordStatus.NEW
                        petitionListEmployee?.petitionRequest = request
                        petitionListEmployee?.petitionList = petitionList
                        petitionListEmployee?.validate()
                        request.requestStatus = EnumRequestStatus.ADD_TO_LIST

                        if (request?.requestStatusNote) {
                            PetitionListEmployeeNote note = new PetitionListEmployeeNote(
                                    noteDate: request?.requestDate,
                                    note: request?.requestStatusNote,
                                    orderNo: "",
                                    petitionListEmployee: petitionListEmployee,
                            );
                            if (!note?.validate()) {
                                petitionList.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                petitionListEmployee.addToPetitionListEmployeeNotes(note);
                            }
                        }

                        petitionList.addToPetitionListEmployees(petitionListEmployee)
                        request.validate()
                        request?.save(failOnError: true, flush: true)
                    }
                    //save the promotion list changes
                    petitionList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    transactionStatus.setRollbackOnly()
                    if (petitionList?.errors?.allErrors?.size() == 0) {
                        petitionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            petitionList.errors.reject("list.request.notSelected.error")
        }
        return petitionList
    }

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    PetitionList sendData(GrailsParameterMap params) {
        //return petitionListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            PetitionList petitionList = PetitionList.get(params["id"])

            //return error if no request added to list
            if (petitionList?.petitionListEmployees?.size() == 0) {
                petitionList.errors.reject("list.sendList.error")
                return petitionList
            }
            if (petitionList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['sendDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = petitionList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = petitionList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        petitionList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    petitionList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all petitionListEmployee in marital status list to change the status of the request
                    petitionList?.petitionListEmployees.each { PetitionListEmployee petitionListEmployee ->
                        //petitionListEmployee?.petitionRequest?.petitionListEmployee = petitionListEmployee
                        petitionListEmployee?.petitionRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        petitionListEmployee?.petitionRequest?.validate()
                    }

                    //save the disciplinary list changes
                    petitionList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (petitionList?.errors?.allErrors?.size() == 0) {
                        petitionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return petitionList
            }
        }
    }

    /**
     * receive marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    PetitionList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return PetitionList is
            PetitionList petitionList = PetitionList.load(params["id"])
            if (petitionList) {
                try {
                    //to change the correspondenceListStatus to received when we receive the PetitionList
                    // and change the to date to the date of receive PetitionList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus(
                            fromDate: ZonedDateTime.now(),
                            toDate: PCPUtils.parseZonedDateTime(params['receiveDate']),
                            correspondenceList: petitionList,
                            receivingParty: params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null,
                            firm: petitionList?.firm,
                            correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED)
                    petitionList?.currentStatus = correspondenceListStatus

                    //enter the manualIncomeNo when we receive the petition list
                    petitionList?.manualIncomeNo = params.manualIncomeNo
                    //save the disciplinary list changes
                    petitionList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (petitionList?.errors?.allErrors?.size() == 0) {
                        petitionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return petitionList
            }
        }
    }

    /**
     * to change the petition request status to Approved in the petitionList
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
            //retrieve the list of petition requests :
            List<PetitionListEmployee> petitionEmployeeList = PetitionListEmployee.executeQuery("from PetitionListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (petitionEmployeeList) {
                petitionEmployeeList.each { PetitionListEmployee petitionListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate']) || params.note || params.orderNo) {
                            PetitionListEmployeeNote note = new PetitionListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    petitionListEmployee: petitionListEmployee,
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
                                petitionListEmployee.addToPetitionListEmployeeNotes(note);
                            }
                        }
                        if (!errors) {
                            //change the petition list employee record status
                            petitionListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                            //save the request employee instance:
                            petitionListEmployee.save(flush: true, failOnError: true);
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
     * to change the petitionList request status to rejected in the receive petitionList
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
            //retrieve the list of petition requests :
            List<PetitionListEmployee> petitionEmployeeList = PetitionListEmployee.executeQuery("from PetitionListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (petitionEmployeeList) {
                petitionEmployeeList.each { PetitionListEmployee petitionListEmployee ->
                    try {

                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            PetitionListEmployeeNote note = new PetitionListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    petitionListEmployee: petitionListEmployee,
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
                                petitionListEmployee.addToPetitionListEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }
                        if (!errors) {
                            //change the petition list employee record status
                            petitionListEmployee?.recordStatus = EnumListRecordStatus.REJECTED
                            //save the request employee instance:
                            petitionListEmployee.save(flush: true, failOnError: true);
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
     * close petition list
     * @param GrailsParameterMap params
     * @return petition list instance
     */
    PetitionList closeList(GrailsParameterMap params) {
        //return petitionListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            PetitionList petitionList = PetitionList.get(params["id"])
            if (petitionList) {
                try {
                    //to change the correspondenceListStatus to submitted when we close the petition list
                    // and change the from date when closing the list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = petitionList
                    correspondenceListStatus.receivingParty = petitionList?.receivingParty
                    correspondenceListStatus.firm = petitionList?.firm

                    if (PetitionListEmployee.countByPetitionListAndRecordStatus(petitionList, EnumListRecordStatus.NEW) > 0) {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                    } else {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                    }

                    PetitionRequest petitionRequest
                    petitionList?.petitionListEmployees?.each { PetitionListEmployee petitionListEmployee ->
                        petitionRequest = petitionListEmployee?.petitionRequest

                        // set external order numbers
                        List <ListNote> orderNumberNoteList = petitionListEmployee?.petitionListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                        if(orderNumberNoteList?.size() > 0){
                            ListNote orderNumberNote = orderNumberNoteList?.last()
                            petitionRequest.externalOrderNumber= orderNumberNote.orderNo
                            petitionRequest.externalOrderDate= orderNumberNote.noteDate
                        }

                        if (petitionListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            petitionRequest.requestStatus = EnumRequestStatus.APPROVED
                            petitionRequest?.disciplinaryRequest?.requestStatus = EnumRequestStatus.CANCELED
                        } else {
                            petitionRequest.requestStatus = EnumRequestStatus.REJECTED
                            petitionListEmployee?.petitionRequest?.disciplinaryRequest?.requestStatus = EnumRequestStatus.APPROVED
                        }
                        petitionRequest?.disciplinaryRequest?.validate()
                        petitionRequest?.validate()
                        petitionRequest.save(flush: true, failOnError: true)
                    }
                    if (correspondenceListStatus.save()) {
                        petitionList.currentStatus = correspondenceListStatus
                    }

                    //save the petition list changes
                    petitionList.save(flush: true, failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (petitionList?.errors?.allErrors?.size() == 0) {
                        petitionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return petitionList
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
        String query = "FROM petition_list al  LEFT JOIN " +
                "  (SELECT ale.petition_list_id ,count(ale.id) no_of_employee" +
                "  FROM petition_list_employee ale " +
                "  group by ale.petition_list_id ) b" +
                "  on al.id= b.petition_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<PetitionList> results = []
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

            PetitionList petitionList = new PetitionList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            petitionList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            petitionList.currentStatus = currentStatus
            results.add(petitionList)
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