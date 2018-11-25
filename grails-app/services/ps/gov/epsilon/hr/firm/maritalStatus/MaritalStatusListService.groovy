package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.core.personMaritalStatus.ManagePersonMaritalStatusService
import ps.gov.epsilon.core.personRelationShips.ManagePersonRelationShipsService
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.allowance.v1.EnumAllowanceType
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.allowance.AllowanceRequest
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceType
import ps.gov.epsilon.hr.firm.child.ChildRequest
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.request.RequestChangesHandlerService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.enums.v1.GenderType
import ps.police.pcore.enums.v1.MaritalStatusEnum
import ps.police.pcore.enums.v1.RelationshipTypeEnum
import ps.police.pcore.v2.entity.lookups.commands.v1.RelationshipTypeCommand
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonMaritalStatusCommand
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO

import java.sql.Timestamp
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to manage the marital status list for requests which should be sent to third party-
 * <h1>Usage</h1>
 * -insert marital status requests to list-
 * - send, recieve and close list-
 * - approve and reject the requests-
 * - insert the new relation to core
 * - update marital status in core
 * <h1>Restriction</h1>
 * - create should be automatically
 * - edit is allowed if the list is NEW state only.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class MaritalStatusListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    ManagePersonRelationShipsService managePersonRelationShipsService
    ManagePersonMaritalStatusService managePersonMaritalStatusService
    PersonRelationShipsService personRelationShipsService
    PersonService personService
    RequestChangesHandlerService requestChangesHandlerService

    //to get the value of requisition status
    public static currentStatusValue = { cService, MaritalStatusList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, MaritalStatusList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../maritalStatusList/manageMaritalStatusList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        String code = params["code"]
        Set correspondenceListStatusesIds = params.listString("correspondenceListStatuses.id")
        String currentStatusId = params["currentStatus.id"]
        String manualIncomeNo = params["manualIncomeNo"]
        String manualOutgoingNo = params["manualOutgoingNo"]
        Set maritalStatusListEmployeesIds = params.listString("maritalStatusListEmployees.id")
        String name = params["name"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return MaritalStatusList.createCriteria().list(max: max, offset: offset) {
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
                if (maritalStatusListEmployeesIds) {
                    maritalStatusListEmployees {
                        inList("id", maritalStatusListEmployeesIds)
                    }
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

    //to search about remoting values from the core
    public PagedList searchWithRemotingValues(GrailsParameterMap params) {
        //use the search method to return all values in list
        PagedList pagedResultList = this.customSearch(params)
        if (pagedResultList) {
            return pagedResultList
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return MaritalStatusList.
     */
    MaritalStatusList save(GrailsParameterMap params) {
        MaritalStatusList maritalStatusListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            maritalStatusListInstance = MaritalStatusList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (maritalStatusListInstance.version > version) {
                    maritalStatusListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('maritalStatusList.label', null, 'maritalStatusList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this maritalStatusList while you were editing")
                    return maritalStatusListInstance
                }
            }
            if (!maritalStatusListInstance) {
                maritalStatusListInstance = new MaritalStatusList()
                maritalStatusListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('maritalStatusList.label', null, 'maritalStatusList', LocaleContextHolder.getLocale())] as Object[], "This maritalStatusList with ${params.id} not found")
                return maritalStatusListInstance
            }
        } else {
            maritalStatusListInstance = new MaritalStatusList()
        }
        try {
            maritalStatusListInstance.properties = params;

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
                        correspondenceListStatus: listStatus, receivingParty: maritalStatusListInstance.receivingParty, firm: maritalStatusListInstance.firm)
                maritalStatusListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            maritalStatusListInstance.save(flush: true, failOnError: true);

            //save the current status:
            if (correspondenceListStatus?.id && maritalStatusListInstance?.id) {
                maritalStatusListInstance?.currentStatus = correspondenceListStatus
                maritalStatusListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            maritalStatusListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return maritalStatusListInstance
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return MaritalStatusList.
     */
    @Transactional(readOnly = true)
    MaritalStatusList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                MaritalStatusList maritalStatusList = results?.resultList[0]
                return maritalStatusList
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
    MaritalStatusList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                MaritalStatusList instance = results?.resultList[0]
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
            MaritalStatusList instance = MaritalStatusList.get(id)
            //to be able to delete an vacation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.maritalStatusListEmployees?.size() == 0) {
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
     * to add requestStatusRequest to maritalStatusList
     * @param GrailsParameterMap params
     * @return list instance
     */
    MaritalStatusList addMaritalStatusRequestToList(GrailsParameterMap params) {

        MaritalStatusList maritalStatusList = MaritalStatusList.load(params["maritalStatusListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            MaritalStatusListEmployee maritalStatusListEmployee
            //retrieve the selected services:
            List<MaritalStatusRequest> maritalStatusRequests = MaritalStatusRequest.executeQuery("from MaritalStatusRequest c where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (maritalStatusRequests) {
                maritalStatusListEmployee
                try {
                    maritalStatusRequests.each { MaritalStatusRequest maritalStatusRequest ->
                        //create new service list employee and add the service Request
                        maritalStatusListEmployee = new MaritalStatusListEmployee()
                        maritalStatusListEmployee?.recordStatus = EnumListRecordStatus.NEW
                        maritalStatusListEmployee?.maritalStatusRequest = maritalStatusRequest
                        maritalStatusListEmployee?.maritalStatusList = maritalStatusList
                        maritalStatusListEmployee?.firm = maritalStatusList?.firm
                        maritalStatusListEmployee?.validate()

                        if (maritalStatusRequest?.requestStatusNote) {
                            MaritalStatusEmployeeNote note = new MaritalStatusEmployeeNote(
                                    noteDate: maritalStatusRequest?.requestDate,
                                    note: maritalStatusRequest?.requestStatusNote,
                                    orderNo: "",
                                    maritalStatusListEmployee: maritalStatusListEmployee,
                            );
                            if (!note.validate()) {
                                maritalStatusList.errors.addAllErrors(note.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                maritalStatusListEmployee.addToMaritalStatusEmployeeNotes(note);
                            }
                        }

                        //add the service list employee to list
                        maritalStatusRequest?.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        maritalStatusList?.addToMaritalStatusListEmployees(maritalStatusListEmployee);
                        maritalStatusRequest?.validate()
                        maritalStatusRequest?.save(failOnError: true, flush: true)
                    }
                    //save the service list changes
                    maritalStatusList?.currentStatus?.validate()
                    maritalStatusList?.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (maritalStatusList?.errors?.allErrors?.size() == 0) {
                        maritalStatusList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            maritalStatusList.errors.reject("list.request.notSelected.error")
        }
        return maritalStatusList
    }

    /**
     * send marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    MaritalStatusList sendList(GrailsParameterMap params) {
        //return maritalStatusListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            //return the corsponding list:
            MaritalStatusList maritalStatusList = MaritalStatusList.get(params["id"])

            //return error if no request added to list
            if (maritalStatusList?.maritalStatusListEmployees?.size() == 0) {
                maritalStatusList.errors.reject("list.sendList.error")
                return maritalStatusList
            }

            if (maritalStatusList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = maritalStatusList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = maritalStatusList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        maritalStatusList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    maritalStatusList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all maritalStatusListEmployee in marital status list to change the status of the request
                    maritalStatusList?.maritalStatusListEmployees.each { MaritalStatusListEmployee maritalStatusListEmployee ->
                        //maritalStatusListEmployee?.maritalStatusRequest?.maritalStatusListEmployee = maritalStatusListEmployee
                        maritalStatusListEmployee?.maritalStatusRequest?.validate()
                        maritalStatusListEmployee?.maritalStatusRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                    }

                    //save the disciplinary list changes
                    maritalStatusList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (maritalStatusList?.errors?.allErrors?.size() == 0) {
                        maritalStatusList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return maritalStatusList
            }
        }
    }

    /**
     * receive marital status list
     * @param GrailsParameterMap params
     * @return boolean
     */
    MaritalStatusList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return MaritalStatusList is
            MaritalStatusList maritalStatusList = MaritalStatusList.load(params["id"])
            if (maritalStatusList) {
                try {
                    //to change the correspondenceListStatus to received when we receive the MaritalStatusList
                    // and change the to date to the date of receive MaritalStatusList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus(
                            fromDate: ZonedDateTime.now(),
                            toDate: PCPUtils.parseZonedDateTime(params['toDate']),
                            correspondenceList: maritalStatusList,
                            receivingParty: params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null,
                            firm: maritalStatusList?.firm,
                            correspondenceListStatus: EnumCorrespondenceListStatus.RECEIVED)
                    maritalStatusList.currentStatus = correspondenceListStatus

                    //enter the manualIncomeNo when we receive the maritalStatus list
                    maritalStatusList.manualIncomeNo = params.manualIncomeNo

                    //save the disciplinary list changes
                    maritalStatusList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (maritalStatusList?.errors?.allErrors?.size() == 0) {
                        maritalStatusList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return maritalStatusList
            }
        }
    }

    /**
     * to change the maritalStatusList request status to Approved in the maritalStatusList and save new statuses in core
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToApproved(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("check_requestTableInList");
        params.remove("check_requestTableInList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of maritalStatus requests :
            List<MaritalStatusListEmployee> maritalStatusEmployeeList = MaritalStatusListEmployee.executeQuery("from MaritalStatusListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (maritalStatusEmployeeList) {
                maritalStatusEmployeeList.each { MaritalStatusListEmployee maritalStatusListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate']) || params.note || params.orderNo) {
                            MaritalStatusEmployeeNote note = new MaritalStatusEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    maritalStatusListEmployee: maritalStatusListEmployee,
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
                                maritalStatusListEmployee.addToMaritalStatusEmployeeNotes(note);
                            }
                        }
                        if (!errors) {
                            //change the maritalStatus list employee record status
                            maritalStatusListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                            //save the request employee instance:
                            maritalStatusListEmployee = saveApprovalInfo(maritalStatusListEmployee, params)
                            maritalStatusListEmployee.save(flush: true, failOnError: true);
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
     * separated to be used by AOC
     * @param record
     * @param params
     * @return
     */
    public MaritalStatusListEmployee saveApprovalInfo(MaritalStatusListEmployee record, GrailsParameterMap params) {
        //save the request employee instance:
        record?.hasAllowance = params.boolean("hasAllowance");
        record?.effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        if (!record?.hasAllowance) {
            record?.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
        }
        return record
    }

    /**
     * to change the maritalStatusList request status to rejected in the receive maritalStatusList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToRejected(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("check_maritalStatusRequestTableInMaritalStatusList");
        params.remove("check_maritalStatusRequestTableInMaritalStatusList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of maritalStatus requests :
            List<MaritalStatusListEmployee> maritalStatusEmployeeList = MaritalStatusListEmployee.executeQuery("from MaritalStatusListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (maritalStatusEmployeeList) {
                maritalStatusEmployeeList.each { MaritalStatusListEmployee maritalStatusListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            MaritalStatusEmployeeNote note = new MaritalStatusEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    maritalStatusListEmployee: maritalStatusListEmployee,
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
                                maritalStatusListEmployee.addToMaritalStatusEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }
                        if (!errors) {
                            //change the maritalStatus list employee record status
                            maritalStatusListEmployee?.recordStatus = EnumListRecordStatus.REJECTED
                            //save the request employee instance:
                            maritalStatusListEmployee.save(flush: true, failOnError: true);
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
     * close maritalStatus list
     * @param GrailsParameterMap params
     * @return boolean
     */
    MaritalStatusList closeList(GrailsParameterMap params) {
        //return maritalStatusListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            MaritalStatusList maritalStatusList = MaritalStatusList.get(params["id"])
            try {
                if (maritalStatusList) {

                    //to change the correspondenceListStatus to submitted when we close the maritalStatus list
                    // and change the from date when closing the list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = maritalStatusList
                    correspondenceListStatus.receivingParty = maritalStatusList?.receivingParty
                    correspondenceListStatus.firm = maritalStatusList?.firm
                    if (MaritalStatusListEmployee.countByMaritalStatusListAndRecordStatus(maritalStatusList, EnumListRecordStatus.NEW) > 0) {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                    } else {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                    }

                    AllowanceRequest allowanceRequest

                    List relatedPersonIds = maritalStatusList?.maritalStatusListEmployees?.maritalStatusRequest?.relatedPersonId?.toList()
                    SearchBean searchBean = new SearchBean()
                    searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: relatedPersonIds))
                    PersonDTO relatedPersonDTO
                    MaritalStatusRequest maritalStatusRequest

                    maritalStatusList?.maritalStatusListEmployees?.each { MaritalStatusListEmployee maritalStatusListEmployee ->
                        maritalStatusRequest = maritalStatusListEmployee?.maritalStatusRequest

                        // set external order numbers
                        List<ListNote> orderNumberNoteList = maritalStatusListEmployee?.maritalStatusEmployeeNotes?.findAll {
                            it.orderNo != null
                        }?.sort { it?.id }
                        if (orderNumberNoteList?.size() > 0) {
                            ListNote orderNumberNote = orderNumberNoteList?.last()
                            maritalStatusRequest.externalOrderNumber = orderNumberNote.orderNo
                            maritalStatusRequest.externalOrderDate = orderNumberNote.noteDate
                        }

                        if (maritalStatusListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            maritalStatusRequest?.requestStatus = EnumRequestStatus.APPROVED
                            // reflect request changes
                            requestChangesHandlerService.applyRequestChanges(maritalStatusRequest)
                            //check if hasAllowance checked, then create allowance request for this employee.
                            if (maritalStatusListEmployee?.hasAllowance) {
                                allowanceRequest = new AllowanceRequest()
                                allowanceRequest?.firm = maritalStatusListEmployee?.maritalStatusRequest?.firm
                                allowanceRequest?.employee = maritalStatusListEmployee?.maritalStatusRequest?.employee
                                allowanceRequest?.currentEmployeeMilitaryRank = maritalStatusListEmployee?.maritalStatusRequest?.currentEmployeeMilitaryRank
                                allowanceRequest?.currentEmploymentRecord = maritalStatusListEmployee?.maritalStatusRequest?.currentEmploymentRecord
                                allowanceRequest?.requestDate = maritalStatusListEmployee?.maritalStatusRequest?.requestDate
                                allowanceRequest?.requestStatus = EnumRequestStatus.APPROVED
                                allowanceRequest?.requestType = EnumRequestType.ALLOWANCE_REQUEST
                                allowanceRequest?.effectiveDate = maritalStatusListEmployee?.effectiveDate
                                allowanceRequest?.requestStatusNote = "created When receive maritalStatus list"
                                if (relatedPersonDTO?.genderType?.id == GenderType.FEMALE.value()) {
                                    allowanceRequest?.allowanceType = AllowanceType.get(EnumAllowanceType.WIFE.value)
                                } else {
                                    allowanceRequest?.allowanceType = AllowanceType.get(EnumAllowanceType.HUSBAND.value)
                                }
                                allowanceRequest?.save(failOnError: true, flush: true)
                            }
                        } else {
                            maritalStatusRequest?.requestStatus = EnumRequestStatus.REJECTED
                        }
                    }
                    if (correspondenceListStatus.save()) {
                        maritalStatusList.currentStatus = correspondenceListStatus
                    }

                    maritalStatusRequest?.validate()
                    maritalStatusRequest?.save(failOnError: true, flush: true)
                    //save the disciplinary list changes
                    maritalStatusList.save(failOnError: true, flush: true)
                }
            } catch (Exception ex) {
                ex.printStackTrace()
                transactionStatus.setRollbackOnly()
                if (maritalStatusList?.errors?.allErrors?.size() == 0) {
                    maritalStatusList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                }
            }
            return maritalStatusList
        }
    }

    /**
     * custom search to find the number of requests in the maritalStatus  list in one select statement for performance issue
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
        Timestamp fromSendDate = PCPUtils.parseTimestamp(params['sendDateFrom'])
        Timestamp toSendDate = PCPUtils.parseTimestamp(params['sendDateTo'])
        Timestamp fromDateCreated = PCPUtils.parseTimestampWithSmallestTime(params['dateCreatedFrom'])
        Timestamp toDateCreated = PCPUtils.parseTimestampWithBiggestTime(params['dateCreatedTo'])
        Timestamp fromReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateFrom'])
        Timestamp toReceiveDate = PCPUtils.parseTimestampWithBiggestTime(params['receiveDateTo'])


        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of maritalStatus in the list, the send date, and the current list status
        String query = "FROM marital_status_list al  LEFT JOIN " +
                "  (SELECT ale.marital_status_list_id ,count(ale.id) no_of_employee" +
                "  FROM marital_status_list_employee ale " +
                "  group by ale.marital_status_list_id ) b" +
                "  on al.id= b.marital_status_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<MaritalStatusList> results = []
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

            MaritalStatusList maritalStatusList = new MaritalStatusList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[15],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])

            maritalStatusList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            maritalStatusList.currentStatus = currentStatus
            results.add(maritalStatusList)
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