package ps.gov.epsilon.hr.firm.employmentService

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistoryService
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.request.Request
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
 * -This service aims to manage the requests of end of service and return To service-
 * <h1>Usage</h1>
 * -create , manage the list of service-
 * <h1>Restriction</h1>
 * --
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ServiceListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    EmployeeStatusHistoryService employeeStatusHistoryService

    public static currentStatusValue = { cService, ServiceList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, ServiceList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../serviceList/manageServiceList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
     * to search model entries with remoting values.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        PagedList pagedList = customSearch(params)
        if (pagedList) {
            return pagedList
        }
    }

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
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
        ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType serviceListType = params["serviceListType"] ? ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.valueOf(params["serviceListType"]) : null
        //this params used to filter the recallToService records and the end of service records
        List<ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType> serviceListTypeValues = params.list("serviceListTypeValues")



        return ServiceList.createCriteria().list(max: max, offset: offset) {
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
                if (receivingParty) {
                    eq("receivingParty", receivingParty)
                }
                if (serviceListType) {
                    eq("serviceListType", serviceListType)
                }
                if (serviceListTypeValues) {
                    inList("serviceListType", serviceListTypeValues)
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
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ServiceList.
     */
    ServiceList save(GrailsParameterMap params) {
        ServiceList serviceListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            serviceListInstance = ServiceList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (serviceListInstance.version > version) {
                    serviceListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('serviceList.label', null, 'serviceList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this serviceList while you were editing")
                    return serviceListInstance
                }
            }
            if (!serviceListInstance) {
                serviceListInstance = new ServiceList()
                serviceListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('serviceList.label', null, 'serviceList', LocaleContextHolder.getLocale())] as Object[], "This serviceList with ${params.id} not found")
                return serviceListInstance
            }
        } else {
            serviceListInstance = new ServiceList()
        }
        try {
            serviceListInstance.properties = params;

            CorrespondenceListStatus correspondenceListStatus
            if (!params.id) {
                //when create the list , its is CREATED phase:
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.getDEFAULT_ZONED_DATE_TIME(), correspondenceListStatus: EnumCorrespondenceListStatus.CREATED, receivingParty: EnumReceivingParty.SARAYA, firm: serviceListInstance.firm)
                serviceListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }
            serviceListInstance.save(flush: true, failOnError: true);
            //save the current status:
            if (correspondenceListStatus?.id && serviceListInstance?.id) {
                serviceListInstance?.currentStatus = correspondenceListStatus
                serviceListInstance.save(flush: true, failOnError: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            serviceListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return serviceListInstance
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
            ServiceList instance = ServiceList.get(id)
            //to be able to delete an service list when status is created
            if (instance?.currentStatus.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED]) {
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
     * @return ServiceList.
     */
    @Transactional(readOnly = true)
    ServiceList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                ServiceList serviceList = results?.resultList[0]
                return serviceList
            }
        }
        return null
    }

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return ServiceList.
     */
    @Transactional(readOnly = true)
    ServiceList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                ServiceList serviceList = results?.resultList[0]
                return serviceList
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
     * send service list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ServiceList sendList(GrailsParameterMap params) {
        //return serviceListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the corresponding list:
            ServiceList serviceList = ServiceList.get(params["id"])

            //return error if no request added to list
            if (serviceList?.serviceListEmployees?.size() == 0) {
                serviceList.errors.reject("list.sendList.error")
                return serviceList
            }

            if (serviceList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the service list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.correspondenceList = serviceList
                    correspondenceListStatus.receivingParty = serviceList?.receivingParty
                    correspondenceListStatus.firm = serviceList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save()) {
                        serviceList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    serviceList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all childListEmployee in marital status list to change the status of the request
                    serviceList?.serviceListEmployees.each { ServiceListEmployee serviceListEmployee ->
                        //childListEmployee?.childRequest?.childListEmployee = childListEmployee
                        serviceListEmployee?.employmentServiceRequest?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        serviceListEmployee?.employmentServiceRequest?.validate()
                    }

                    //save the disciplinary list changes
                    serviceList.save(failOnError: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (serviceList?.errors?.allErrors?.size() == 0) {
                        serviceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return serviceList
            }
        } else {
            ServiceList serviceList = new ServiceList()
            serviceList.errors.reject('default.not.found.message', [messageSource.getMessage('serviceList.label', null, 'serviceList', LocaleContextHolder.getLocale())] as Object[], "This serviceList with ${params.id} not found")
            return serviceList
        }
    }

    /**
     * receive service list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ServiceList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return ServiceList is
            ServiceList serviceList = ServiceList.load(params["id"])
            if (serviceList) {
                try {
                    //to change the correspondenceListStatus to received when we received the service list
                    // and change the to date to the date of receive ServiceList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['toDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = serviceList
                    correspondenceListStatus.receivingParty = serviceList?.receivingParty
                    correspondenceListStatus.firm = serviceList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        serviceList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we receive the service list
                    serviceList.manualIncomeNo = params.manualIncomeNo
                    //save the service list changes
                    serviceList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (serviceList?.errors?.allErrors?.size() == 0) {
                        serviceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return serviceList
            }
        } else {
            ServiceList serviceList = new ServiceList()
            serviceList.errors.reject('default.not.found.message', [messageSource.getMessage('serviceList.label', null, 'serviceList', LocaleContextHolder.getLocale())] as Object[], "This serviceList with ${params.id} not found")
            return serviceList
        }
    }

    /**
     * close service list
     * @param GrailsParameterMap params
     * @return boolean
     */
    ServiceList closeList(GrailsParameterMap params) {
        //return serviceListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return the correspondence list:
            ServiceList serviceList = ServiceList.get(params["id"])
            if (serviceList) {
                try {
                    //to change the correspondenceListStatus to submitted when we close the service list
                    // and change the from date when closing the list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = serviceList
                    correspondenceListStatus.receivingParty = serviceList?.receivingParty
                    correspondenceListStatus.firm = serviceList?.firm
                    if (ServiceListEmployee.countByServiceListAndRecordStatus(serviceList, EnumListRecordStatus.NEW) > 0) {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                    } else {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                    }
                    if (correspondenceListStatus.save()) {
                        serviceList.currentStatus = correspondenceListStatus
                    }
                    EmploymentServiceRequest employmentServiceRequest
                    serviceList?.serviceListEmployees.each {ServiceListEmployee serviceListEmployee ->
                        employmentServiceRequest = serviceListEmployee?.employmentServiceRequest
                        // set external order numbers
                        List <ListNote> orderNumberNoteList = serviceListEmployee?.serviceListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                        if(orderNumberNoteList?.size() > 0) {
                            ListNote orderNumberNote = orderNumberNoteList?.last()
                            employmentServiceRequest?.externalOrderNumber = orderNumberNote.orderNo
                            employmentServiceRequest?.externalOrderDate = orderNumberNote.noteDate
                            employmentServiceRequest?.expectedDateEffective = serviceListEmployee?.dateEffective

                        }
                        /**
                         * set request status
                         */
                        if (serviceListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            employmentServiceRequest?.requestStatus = EnumRequestStatus.APPROVED

                            // reflect request changes
                            //requestChangesHandlerService.applyRequestChanges(serviceListEmployee)

                        } else {
                            employmentServiceRequest?.requestStatus = EnumRequestStatus.REJECTED
                        }
                        employmentServiceRequest?.validate()
                        employmentServiceRequest?.save(failOnError: true, flush: true)
                    }
                    //save the service list changes
                    serviceList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex?.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (serviceList?.errors?.allErrors?.size() == 0) {
                        serviceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return serviceList
            }
        } else {
            ServiceList serviceList = new ServiceList()
            serviceList.errors.reject('default.not.found.message', [messageSource.getMessage('serviceList.label', null, 'serviceList', LocaleContextHolder.getLocale())] as Object[], "This serviceList with ${params.id} not found")
            return serviceList
        }
    }

    /**
     * to change the service List employee status to APPROVED in the receive service list
     * @param GrailsParameterMap params
     * @return map
     */
    Map changeRequestToApproved(GrailsParameterMap params) {
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        //to get list of ids
        List checkedServiceListEmployeeIdsList = params.listString("checked_serviceListEmployeeIdsList")
        params.remove("checked_serviceListEmployeeIdsList")
        if (checkedServiceListEmployeeIdsList) {
            List<ServiceListEmployee> serviceListEmployees = ServiceListEmployee.executeQuery("from ServiceListEmployee emp where id in (:checkedServiceListEmployeeIdsList)", [checkedServiceListEmployeeIdsList: checkedServiceListEmployeeIdsList])
            if (serviceListEmployees) {
                //loop on each serviceList employee and update the status to be EMPLOYED
                serviceListEmployees?.each { ServiceListEmployee serviceListEmployee ->
                    try {
                        //save the note if its used:
                        //create note instance for serviceListEmployeeNote:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            ServiceListEmployeeNote note = new ServiceListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    serviceListEmployee: serviceListEmployee,
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
                                serviceListEmployee.addToServiceListEmployeeNotes(note);
                            }
                        }//if note

                        if (!errors) {
                           serviceListEmployee = saveApprovalInfo(serviceListEmployee, params)
                            //change the service list employee record status
                            serviceListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                            //change the request status to be approved!
                            serviceListEmployee?.save(flush: true, failOnError: true)
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
     * separated to be used by AOC
     * @param record
     * @param params
     * @return service list employee
     */
    public ServiceListEmployee saveApprovalInfo(ServiceListEmployee record, GrailsParameterMap params){
        //save the request employee instance:
        record?.dateEffective = PCPUtils.parseZonedDateTime(params['dateEffective'])
        return record
    }

    /**
     * to change the service List employee status to NOT_EMPLOYED in the receive service list
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToRejected(GrailsParameterMap params) {
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        //to get list of ids
        List checkedServiceListEmployeeIdsList = params.listString("checked_serviceListEmployeeIdsList")
        params.remove("checked_serviceListEmployeeIdsList")
        if (checkedServiceListEmployeeIdsList) {
            List<ServiceListEmployee> serviceListEmployees = ServiceListEmployee.executeQuery("from ServiceListEmployee emp where id in (:checkedServiceListEmployeeIdsList)", [checkedServiceListEmployeeIdsList: checkedServiceListEmployeeIdsList])
            if (serviceListEmployees) {
                //loop on each serviceList employee and update the status to be EMPLOYED
                serviceListEmployees?.each { ServiceListEmployee serviceListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            ServiceListEmployeeNote note = new ServiceListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    serviceListEmployee: serviceListEmployee,
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
                                serviceListEmployee.addToServiceListEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }
                        if (!errors) {
                            //change the service list employee record status
                            serviceListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                            //add the rejection reason per employee list
                            serviceListEmployee?.save(flush: true, failOnError: true)
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
     * to add service instance to serviceList
     * @param GrailsParameterMap params
     * @return boolean
     */
    ServiceList addEmploymentServiceRequestToList(GrailsParameterMap params) {
        ServiceList serviceList = ServiceList.load(params["serviceListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            ServiceListEmployee serviceListEmployee
            //retrieve the selected services:
            List<EmploymentServiceRequest> requests = Request.executeQuery("from EmploymentServiceRequest d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (requests) {
                serviceListEmployee
                try {
                    requests.each { EmploymentServiceRequest request ->
                        //create new service list employee and add the service Request
                        serviceListEmployee = new ServiceListEmployee()
                        serviceListEmployee.firm = request?.firm
                        serviceListEmployee.employee = request?.employee
                        serviceListEmployee.currentEmployeeMilitaryRank = request?.currentEmployeeMilitaryRank
                        serviceListEmployee.currentEmploymentRecord = request?.currentEmploymentRecord
                        serviceListEmployee.recordStatus = EnumListRecordStatus.NEW
                        serviceListEmployee?.serviceActionReason = request?.serviceActionReason
                        serviceListEmployee.employmentServiceRequest = request
                        serviceListEmployee.serviceList = serviceList
                        serviceListEmployee.validate()

                        if (request?.requestStatusNote) {
                            ServiceListEmployeeNote note = new ServiceListEmployeeNote(
                                    noteDate: request?.requestDate,
                                    note: request?.requestStatusNote,
                                    orderNo: "",
                                    serviceListEmployee: serviceListEmployee,
                            );
                            if (!note?.validate()) {
                                serviceList?.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                serviceListEmployee.addToServiceListEmployeeNotes(note);
                            }
                        }

                        //add the service list employee to list
                        request.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        serviceList.addToServiceListEmployees(serviceListEmployee);
                        request?.save(failOnError: true, flush: true)
                    }
                    //save the service list changes
                    serviceList?.currentStatus?.validate()
                    serviceList?.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (serviceList?.errors?.allErrors?.size() == 0) {
                        serviceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            serviceList.errors.reject("list.request.notSelected.error")
        }
        return serviceList
    }

    /**
     * to add service instance to serviceList
     * @param GrailsParameterMap params
     * @return serviceList instance
     */
    ServiceList addExceptionalToList(GrailsParameterMap params) {
        ServiceList serviceList = ServiceList.load(params["serviceListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            ServiceListEmployee serviceListEmployee
            //retrieve the selected services:
            List<EmploymentServiceRequest> requests = Request.executeQuery("from EmploymentServiceRequest d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (requests) {
                serviceListEmployee
                try {
                    requests.each { EmploymentServiceRequest request ->
                        //create new service list employee and add the service Request
                        serviceListEmployee = new ServiceListEmployee()
                        serviceListEmployee.firm = request?.firm
                        serviceListEmployee.employee = request?.employee
                        serviceListEmployee.currentEmployeeMilitaryRank = request?.currentEmployeeMilitaryRank
                        serviceListEmployee.currentEmploymentRecord = request?.currentEmploymentRecord
                        serviceListEmployee.recordStatus = EnumListRecordStatus.NEW
                        serviceListEmployee?.serviceActionReason = request?.serviceActionReason
                        serviceListEmployee.employmentServiceRequest = request
                        serviceListEmployee.serviceList = serviceList
                        serviceListEmployee.validate()

                        if (request?.requestStatusNote) {
                            ServiceListEmployeeNote note = new ServiceListEmployeeNote(
                                    noteDate: request?.requestDate,
                                    note: request?.requestStatusNote,
                                    orderNo: "",
                                    serviceListEmployee: serviceListEmployee,
                            );
                            if (!note?.validate()) {
                                serviceList?.errors.addAllErrors(note?.errors)
                                throw new Exception("Error occurred while add note to the list employee record.")
                            } else {
                                //add note to join
                                serviceListEmployee.addToServiceListEmployeeNotes(note);
                            }
                        }

                        //add the service list employee to list
                        request.requestStatus = EnumRequestStatus.ADD_TO_LIST
                        serviceList.addToServiceListEmployees(serviceListEmployee);
                        request?.save(failOnError: true, flush: true)
                    }
                    //save the service list changes
                    serviceList?.currentStatus?.validate()
                    serviceList?.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (serviceList?.errors?.allErrors?.size() == 0) {
                        serviceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            serviceList.errors.reject("list.request.notSelected.error")
        }
        return serviceList
    }

    /**
     * to add employment Service Request to service list in case the order date and value are set
     * 1- create new list.
     * 2- add the employee record to it.
     * 3- add the note to record.
     * 4- move request status and record status to approve.
     * 5- close the list.
     * This service used when create list automatically by system and will not be sent to third party
     * since we create the list and change its status to close directly
     * @param employmentServiceRequest
     * @return boolean
     */
    ServiceList addEmploymentServiceRequestToList(EmploymentServiceRequest employmentServiceRequest) {
        //list instance
        ServiceList serviceList
        try {
            //get the max id from the list table
            def maxId = ServiceList.withCriteria {
                projections {
                    max 'id'
                }
            }[0]

            //if the maxId is null set to 0
            if (!maxId) {
                maxId = 0
            }

            //create new list:
            String name = messageSource.getMessage('serviceList.defaultListName.label', null, 'serviceList', LocaleContextHolder.getLocale())
            name += "_" + (maxId + 1) + "_" + ZonedDateTime.now().getYear()
            //create new params to save list
            GrailsParameterMap saveParams = new GrailsParameterMap([:], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
            saveParams.name = name
            saveParams.receivingParty = EnumReceivingParty.SARAYA
            saveParams["firm.id"] = employmentServiceRequest?.firm?.id
            saveParams["serviceListType"] = EnumServiceListType.END_OF_SERVICE
            serviceList = this.save(saveParams);

            //check if the request and list are created:
            if (employmentServiceRequest?.id && serviceList?.id) {
                //create the list employee record and add to list.
                ServiceListEmployee serviceListEmployee = new ServiceListEmployee()
                serviceListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                serviceListEmployee?.dateEffective = employmentServiceRequest?.expectedDateEffective
                serviceListEmployee?.employee = employmentServiceRequest?.employee
                serviceListEmployee?.currentEmployeeMilitaryRank = employmentServiceRequest?.currentEmployeeMilitaryRank
                serviceListEmployee?.currentEmploymentRecord = employmentServiceRequest?.currentEmploymentRecord
                serviceListEmployee?.firm = employmentServiceRequest?.firm
                serviceListEmployee?.serviceActionReason = employmentServiceRequest?.serviceActionReason
                serviceListEmployee?.employmentServiceRequest = employmentServiceRequest
                serviceListEmployee?.serviceList = serviceList
                serviceListEmployee.validate()

                serviceListEmployee.save(failOnError: true, flush: true)

                //create new note and add the order (date, number) to the note instance
                if (employmentServiceRequest.externalOrderDate != PCPUtils.DEFAULT_ZONED_DATE_TIME) {
                    ServiceListEmployeeNote note = new ServiceListEmployeeNote(
                            noteDate: employmentServiceRequest.externalOrderDate,
                            note: employmentServiceRequest?.requestStatusNote,
                            orderNo: employmentServiceRequest.externalOrderNumber,
                            serviceListEmployee: serviceListEmployee
                    )
                    if (!note.validate()) {
                        note.errors.fieldErrors.each { FieldError fieldError ->
                            serviceList.errors.reject(messageSource.getMessage(fieldError?.code, fieldError?.arguments, fieldError?.defaultMessage, LocaleContextHolder.getLocale()))
                        }
                        note.errors.globalErrors.each { ObjectError objectError ->
                            serviceList.errors.reject(messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale()))
                        }
                    } else {
                        //add note to join instance
                        serviceListEmployee.addToServiceListEmployeeNotes(note);
                    }
                } else {
                    serviceList?.errors.reject('correspondenceListNote.require.error.label', [null] as Object[], "")
                    throw new Exception("the order number was not set")
                }

                //change the request status to be approved
                employmentServiceRequest.requestStatus = EnumRequestStatus.APPROVED

                //save the request changes:
                employmentServiceRequest.save(failOnError: true, flush: true)


                //close the list:
                //to change the correspondenceListStatus to submitted when we close the service list
                // and change the from date when closing the list
                CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                correspondenceListStatus.fromDate = ZonedDateTime.now()
                correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                correspondenceListStatus.correspondenceList = serviceList
                correspondenceListStatus.receivingParty = serviceList?.receivingParty
                correspondenceListStatus.firm = serviceList?.firm
                correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED
                if (correspondenceListStatus.save()) {
                    serviceList.currentStatus = correspondenceListStatus
                }

                //save the service list changes:
                serviceList.currentStatus.validate()
                serviceList.save(failOnError: true, flush: true)
            } else {
                if (!serviceList) {
                    serviceList = new ServiceList()
                }
                serviceList?.errors.reject('default.internal.server.error', [null] as Object[], "")
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!serviceList) {
                serviceList = new ServiceList()
            }
            serviceList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return serviceList
    }//end of function

    /**
     * custom search to find the number of applicants in the service  list in one select statement for performance issue
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
        ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType serviceListType = params["serviceListType"] ? ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.valueOf(params["serviceListType"]) : null

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


        Boolean isEndOfServiceType = params.boolean("isEndOfServiceType")
        Boolean isRecallToServiceType = params.boolean("isRecallToServiceType")
        List<String> serviceListTypeValues = []
        if (isEndOfServiceType) {
            //the end of service List types
            serviceListTypeValues = [
                    EnumServiceListType.END_OF_SERVICE,
            ]
        } else if (isRecallToServiceType) {
            //the return to service List types
            serviceListTypeValues = [
                    EnumServiceListType.RETURN_TO_SERVICE
            ]
        }

        Map sqlParamsMap = [:]

        //the query to retrieve the list details, num of services in the list, the send date, and the current list status
        String query = "FROM service_list al  LEFT JOIN " +
                "  (SELECT ale.service_list_id ,count(ale.id) no_of_employee" +
                "  FROM service_list_employee ale " +
                "  group by ale.service_list_id ) b" +
                "  on al.id= b.service_list_id , correspondence_list_status cls,correspondence_list cl" +
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
        if (serviceListType) {
            query = query + " and al.service_list_type = :serviceListTypeParam  "
            sqlParamsMap.put("serviceListTypeParam", serviceListType.toString())
        }

        if (serviceListTypeValues) {
            query += " and position(al.service_list_type in :serviceListTypeValuesParam) > 0 "
            sqlParamsMap.put("serviceListTypeValuesParam", serviceListTypeValues.collect {
                "\'" + it.toString() + "\'"
            }.join(","))
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
                    al.service_list_type,
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

        List<ServiceList> results = []
        // Transform resulting rows to a map with key organisationName.
        queryResults.each { resultRow ->
            ZonedDateTime dateCreatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[3])
            ZonedDateTime lastUpdatedUTC = PCPUtils.convertTimeStampToZonedDateTime(resultRow[15])
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

            ServiceList serviceList = new ServiceList(
                    code: resultRow[1],
                    name: resultRow[2],
                    manualOutgoingNo: resultRow[8],
                    coverLetter: resultRow[16],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    serviceListType: resultRow[14],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            serviceList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            serviceList.currentStatus = currentStatus
            results.add(serviceList)
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

    /*
     * update employee status in case EndOfService
     *    if the request is EndOfService request
     *       - change current employee status (as selected in the request reason configuration) -> under the un-committed category
     *       - end the working employee status by adding endDate
     */

    void updateEmployeeStatusToEndOfService(Firm firm) {
        try {
            //return the list of all serviceListEmployee which are approved but still not reflected into employee status
            List<ServiceListEmployee> serviceListEmployees = ServiceListEmployee.executeQuery("from ServiceListEmployee sle " +
                    "where sle.dateEffective <= :currentDate and sle.recordStatus = :recordStatus and " +
                    "sle.serviceList.id in (select id from ServiceList where serviceListType = :serviceListType and firm.id = :firmId) " +
                    "", [currentDate: ZonedDateTime.now(), recordStatus: EnumListRecordStatus.APPROVED, serviceListType: EnumServiceListType.END_OF_SERVICE, firmId: firm?.id])

            if (serviceListEmployees) {
                EmployeeStatusHistory employeeStatusHistory
                //loop on each employee to update the status history , and update the employee list record status
                serviceListEmployees.each { ServiceListEmployee serviceListEmployee ->
                    //update employee status history to close the working status and open new suspension status
                    if (serviceListEmployee?.serviceActionReason?.employeeStatusResult) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory?.employee = serviceListEmployee?.employee
                        employeeStatusHistory?.fromDate = serviceListEmployee?.dateEffective
                        employeeStatusHistory?.transientData.put("firm", firm);
                        employeeStatusHistory?.employeeStatus = serviceListEmployee?.serviceActionReason?.employeeStatusResult
                        employeeStatusHistory.save(flush: true, failOnError: true)
                        serviceListEmployee?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                    }
                    serviceListEmployee?.recordStatus = EnumListRecordStatus.REFLECTED
                    serviceListEmployee?.validate()
                    serviceListEmployee?.employee?.validate()
                    serviceListEmployee?.save(flush: true, failOnError: true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }

    /*
     * update employee status in case EndOfService
     *    if the request is EndOfService request
     *       - change current employee status (as selected in the request reason configuration) -> under the un-committed category
     *       - end the working employee status by adding endDate
     */

    void updateEmployeeStatusToReturnToService(Firm firm) {
        try {
            //return the list of all serviceListEmployee which are approved but still not reflected into employee status
            List<ServiceListEmployee> serviceListEmployees = ServiceListEmployee.executeQuery("from ServiceListEmployee sle " +
                    "where sle.dateEffective <= :currentDate and sle.recordStatus = :recordStatus and " +
                    "sle.serviceList.id in (select id from ServiceList l where l.serviceListType = :serviceListType and l.firm.id = :firmId and l.currentStatus.id in " +
                    " ( select id from CorrespondenceListStatus c where c.correspondenceList.id = l.id and c.correspondenceListStatus in (:correspondenceListStatus)) )" +
                    ")", [currentDate: ZonedDateTime.now(), recordStatus: EnumListRecordStatus.APPROVED, serviceListType: EnumServiceListType.RETURN_TO_SERVICE, firmId: firm?.id, correspondenceListStatus: [EnumCorrespondenceListStatus.CLOSED, EnumCorrespondenceListStatus.PARTIALLY_CLOSED]])

            if (serviceListEmployees) {
                EmployeeStatusHistory employeeStatusHistory

                //loop on each employee to update the status history , and update the employee list record status
                serviceListEmployees.each { ServiceListEmployee serviceListEmployee ->
                    //update employee status history to close the working status and open new suspension status
                    if (serviceListEmployee?.serviceActionReason?.employeeStatusResult) {
                        employeeStatusHistory = new EmployeeStatusHistory()
                        employeeStatusHistory?.employee = serviceListEmployee?.employee
                        employeeStatusHistory?.fromDate = serviceListEmployee?.dateEffective
                        employeeStatusHistory?.transientData.put("firm", firm);
                        employeeStatusHistory?.employeeStatus = serviceListEmployee?.serviceActionReason?.employeeStatusResult
                        employeeStatusHistory.save(flush: true, failOnError: true)
                        serviceListEmployee?.employee?.addToEmployeeStatusHistories(employeeStatusHistory)
                    }
                    serviceListEmployee?.recordStatus = EnumListRecordStatus.REFLECTED
                    serviceListEmployee?.validate()
                    serviceListEmployee?.employee?.validate()
                    serviceListEmployee?.save(flush: true, failOnError: true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }
    }

}