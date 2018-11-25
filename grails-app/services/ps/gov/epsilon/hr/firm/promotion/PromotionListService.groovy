package ps.gov.epsilon.hr.firm.promotion

import grails.gorm.PagedResultList
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.hibernate.Query
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.domains.v1.ListNote
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankClassification
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankType
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.vacation.EmployeeVacationBalance
import ps.gov.epsilon.hr.firm.vacation.EmployeeVacationBalanceService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm
import ps.police.security.dtos.v1.UserDTO
import ps.police.security.remotting.RemoteUserService
import java.sql.Timestamp
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to create list for promotion -
 * <h1>Usage</h1>
 * -create and manage the list-
 * -include employees and requests of promotion-
 * <h1>Restriction</h1>
 * -delete and edit the list when its status is NEW-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class PromotionListService {

    MessageSource messageSource
    def formatService
    def sessionFactory
    EmployeeService employeeService
    NotificationService notificationService
    RemoteUserService remoteUserService
    EmployeeVacationBalanceService employeeVacationBalanceService

    //to get the value of requisition status
    public static currentStatusValue = { cService, PromotionList rec, object, params ->
        return rec?.currentStatus?.correspondenceListStatus?.toString()
    }

    // to make name of list as link
    public static getListName = { formatService, PromotionList dataRow, object, params ->
        if (dataRow) {
            return "<a href ='../promotionList/managePromotionList?encodedId=${dataRow?.encodedId}'>${dataRow?.name?.toString()}</a>";
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
        Set promotionListEmployeeIds = params.listString("promotionListEmployee.id")
        ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null

        return PromotionList.createCriteria().list(max: max, offset: offset) {
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
                if (promotionListEmployeeIds) {
                    promotionListEmployee {
                        inList("id", promotionListEmployeeIds)
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
     * @return PromotionList.
     */
    PromotionList save(GrailsParameterMap params) {
        PromotionList promotionListInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            promotionListInstance = PromotionList.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (promotionListInstance.version > version) {
                    promotionListInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('promotionList.label', null, 'promotionList', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this promotionList while you were editing")
                    return promotionListInstance
                }
            }
            if (!promotionListInstance) {
                promotionListInstance = new PromotionList()
                promotionListInstance.errors.reject('default.not.found.message', [messageSource.getMessage('promotionList.label', null, 'promotionList', LocaleContextHolder.getLocale())] as Object[], "This promotionList with ${params.id} not found")
                return promotionListInstance
            }
        } else {
            promotionListInstance = new PromotionList()
        }
        try {
            promotionListInstance.properties = params;
            //create new status for the list:
            CorrespondenceListStatus correspondenceListStatus
            /**
             * in CREATED phase: create new status for the list
             */
            EnumCorrespondenceListStatus listStatus = params.correspondenceListStatus ? EnumCorrespondenceListStatus.valueOf(params.correspondenceListStatus) : null
            if (!params.id && !listStatus) {
                //when create the list , its is CREATED phase:
                listStatus = EnumCorrespondenceListStatus.CREATED
            }

            if (listStatus) {
                correspondenceListStatus = new CorrespondenceListStatus(fromDate: ZonedDateTime.now(), toDate: PCPUtils.DEFAULT_ZONED_DATE_TIME,
                        correspondenceListStatus: listStatus, receivingParty: EnumReceivingParty.SARAYA, firm: promotionListInstance.firm)
                promotionListInstance.addToCorrespondenceListStatuses(correspondenceListStatus)
            }

            promotionListInstance.save(flush: true);
            //save the current status:
            if (correspondenceListStatus?.id && promotionListInstance?.id) {
                promotionListInstance?.currentStatus = correspondenceListStatus
                promotionListInstance.save(flush: true)
            }
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            promotionListInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return promotionListInstance
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
            PromotionList instance = PromotionList.get(id)
            //to be able to delete an vacation list when status is created
            if (instance?.currentStatus?.correspondenceListStatus in [EnumCorrespondenceListStatus.CREATED] && instance?.promotionListEmployee?.size() == 0) {
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
     * @return PromotionList.
     */
    @Transactional(readOnly = true)
    PromotionList getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                PromotionList promotionList = results?.resultList[0]
                return promotionList
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
    PromotionList getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedList results = this.customSearch(params)
            if (results) {
                PromotionList instance = results?.resultList[0]
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
     * when calculate employee dueDate, we should take bellow into consideration:
     * 1- current dueDate + numberOfYearToPromote
     * 2- minus (فترة ترك الرتبة + الاستيداع + الاجازة من غير الراتب)
     */
    ZonedDateTime calculateDueDate(Employee employee) {
        GrailsParameterMap searchParams = new GrailsParameterMap([id: employee?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        searchParams["calculateDueDate"] = true
        searchParams["firmId"] = employee?.firm?.id
        Employee promoteEmployee = employeeService.customSearch(searchParams).resultList[0]
        ZonedDateTime nextDueDate = promoteEmployee?.transientData?.dueDate
        return nextDueDate
    }

    /**
     * to add promotion instance to promotionList
     * @param GrailsParameterMap params
     * @return boolean
     */
    PromotionList addPromotionRequestToList(GrailsParameterMap params) {
        PromotionList promotionList = PromotionList.load(params["promotionListId"])
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_requestIdsList");

        params.remove("checked_requestIdsList");
        if (checkedRequestIdList.size() > 0) {
            PromotionListEmployee promotionListEmployee
            //retrieve the selected promotions:
            List<Request> requests = Request.executeQuery("from Request d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (requests) {
                try {
                    requests.each { Request request ->

                        promotionListEmployee = createPromotionListEmployeeFromRequest(request, promotionList)

                        if (promotionListEmployee.hasErrors()) {
                            promotionList.errors.addAllErrors(promotionListEmployee.errors)
                            throw new Exception("Error occurred while creating promotionListemployee")
                        }

                        if (request?.requestStatusNote) {
                            PromotionListEmployeeNote note = new PromotionListEmployeeNote(
                                    noteDate: request?.requestDate,
                                    note: request?.requestStatusNote,
                                    orderNo: "",
                                    promotionListEmployee: promotionListEmployee,
                            );
                            if (!note.validate()) {
                                promotionList.errors.addAllErrors(note.errors)
                                throw new Exception("Error occurred while addinf note to the list employee record.")
                            } else {
                                //add note to join
                                promotionListEmployee.addToPromotionListEmployeeNotes(note);
                            }
                        }

                        //add the promotion list employee to list
                        promotionList.addToPromotionListEmployee(promotionListEmployee)

                        request.requestStatus = EnumRequestStatus.ADD_TO_LIST
                    }
                    //save the promotion list changes
                    promotionList.save(failOnError: true, flush: true)
                } catch (ValidationException ve) {
                    transactionStatus.setRollbackOnly()
                    log.error("Fail to add requests to promotion list", ve)
                    if (ve.errors.errorCount > 0) {
                        promotionList.errors = ve.errors
                    } else {
                        promotionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    //formatService.reAssignErrors(promotionList,null)
                    if (promotionList?.errors?.allErrors?.code) {
                        //clear all previous errors
                        promotionList?.clearErrors()
                        promotionList?.errors.reject("promotionListEmployee.employee.unique.error")
                    }
                    if (promotionList?.errors?.allErrors?.size() == 0) {
                        promotionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            promotionList.errors.reject("list.request.notSelected.error")
        }
        return promotionList
    }

    /**
     * Create a promotionListemployee from request
     * @param request
     * @param firm
     * @return
     */
    PromotionListEmployee createPromotionListEmployeeFromRequest(Request request, PromotionList promotionList) {
        UpdateMilitaryRankRequest updateMilitaryRankRequest

        //create new promotion list employee and add the promotion Request
        PromotionListEmployee promotionListEmployee = new PromotionListEmployee()

        //this code was added to return error message in case the employee has another promotion in the current employee-promotion
        if (request?.requestType != EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD && request?.requestType != EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST) {
            if (request?.employee?.currentEmployeeMilitaryRank?.promotionListEmployee != null) {
                promotionListEmployee.errors.reject("employee.alreadyPromoted.error.message")
                log.error("--->> This employee (${request?.employee?.id}) has already promoted in this currentEmployeeMilitaryRank peroid.")
                throw new ValidationException("--->> This employee (${request?.employee?.id}) has already promoted in this currentEmployeeMilitaryRank peroid.", promotionListEmployee.errors)
            }
        }

        MilitaryRank militaryRank = MilitaryRank.findByOrderNoAndFirm(request?.employee?.currentEmployeeMilitaryRank?.militaryRank.orderNo + 1, promotionList?.firm)

        //set the firm
        promotionListEmployee.firm = request?.firm
        //set the employee who own the request.
        promotionListEmployee.employee = request?.employee
        //set the employee current MilitaryRank
        promotionListEmployee.currentEmployeeMilitaryRank = request?.currentEmployeeMilitaryRank
        //set the employee current employmentRecord
        promotionListEmployee.currentEmploymentRecord = request?.currentEmploymentRecord
        //set the status to NEW as default on create
        promotionListEmployee.recordStatus = EnumListRecordStatus.NEW
        //get the request status value as string
        String requestType = request?.requestType.toString()
        //using the request status value as string, set the list employee promotion reason
        promotionListEmployee?.promotionReason = requestType ? ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.valueOf(requestType) : null

        /*
         * if the request is update military rank/classification :
         *   1- set the record due date
         *   2- set the new value of military rank type/classification
         * else if (the request will update the employment date)
         *   1- no need to set the (promotionListEmployee) inside the employeePromotion current object.
         * else:
         *   1- set the next expected military rank depends on configuration
         */
        if (request?.requestType == EnumRequestType.UPDATE_MILITARY_RANK_TYPE || request?.requestType == EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION) {
            updateMilitaryRankRequest = (UpdateMilitaryRankRequest) request
            promotionListEmployee.militaryRankType = updateMilitaryRankRequest?.newRankType
            promotionListEmployee.militaryRankClassification = updateMilitaryRankRequest?.newRankClassification
            //set the dueDate which was inserted when create the request.
            promotionListEmployee?.militaryRankTypeDate = updateMilitaryRankRequest?.dueDate
        } else if (request?.requestType == EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD || request?.requestType == EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST) {
            promotionListEmployee?.employmentDate = request?.employee?.employmentDate
        } else {
            if (militaryRank) {
                //set the list employee (next) military rank : calculated from the configuration of military rank setup
                if (request?.requestType != EnumRequestType.EXCEPTIONAL_REQUEST) {
                    promotionListEmployee?.militaryRank = militaryRank
                }
                //calculate the dueDate for the employee to get the next rank using military rank setup
                promotionListEmployee?.dueDate = this.calculateDueDate(request?.employee)
                if (!promotionListEmployee?.dueDate) {
                    promotionListEmployee.errors.reject("promotionList.calculateDueDate.error")
                    log.error("error occur while calculate the DueDate")
                    throw new ValidationException("error occur while calculate the DueDate", promotionListEmployee.errors)
                }
            }
        }
        promotionListEmployee?.request = request

        //add the promotion list employee to list
        promotionListEmployee.promotionList = promotionList

        //4- validate the promotionListEmployee
        promotionListEmployee.validate()
        promotionListEmployee?.request?.validate()

        //if the request will update the employment date -> no need to set the (promotionListEmployee) inside the currentEmployeeMilitaryRank object.
        if (request?.requestType != EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD && request?.requestType != EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST) {
            promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.promotionListEmployee = promotionListEmployee
        }

        promotionListEmployee?.employee?.validate()

        return promotionListEmployee
    }

    /**
     * to add promotion instance to promotionList
     * @param GrailsParameterMap params
     * @return boolean
     */
    PromotionList addEmployeeToList(GrailsParameterMap params) {
        PromotionList promotionList = PromotionList.load(params["promotionListId"])
        //to get list of request ids
        List checkedEmployeeIdList = params.listString("checked_employeeIdsList");
        params.remove("checked_employeeIdsList");
        if (checkedEmployeeIdList.size() > 0) {

            //retrieve the selected promotions:
            List<Employee> employees = Employee.executeQuery("from Employee emp where id in (:checkedEmployeeIdList)", [checkedEmployeeIdList: checkedEmployeeIdList])

            if (employees) {

                try {
                    PromotionListEmployee promotionListEmployee
                    MilitaryRank militaryRank
                    PromotionRequest promotionRequest

                    employees.each { Employee employee ->
                        promotionRequest = new PromotionRequest()
                        promotionRequest?.firm = promotionList?.firm
                        //check if it is eligible case or not to set the request type
                        if (params["eligible"]) {
                            promotionRequest?.requestType = EnumRequestType?.ELIGIBLE_REQUEST
                        } else {
                            promotionRequest?.requestType = EnumRequestType?.EXCEPTIONAL_REQUEST
                        }
                        promotionRequest?.requestStatus = EnumRequestStatus?.ADD_TO_LIST
                        promotionRequest?.employee = employee
                        promotionRequest?.requestDate = ZonedDateTime.now()
                        promotionRequest?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
                        promotionRequest?.currentEmploymentRecord = employee?.currentEmploymentRecord
                        promotionRequest.save(failOnError: true, flush: true)

                        //create new promotion list employee and add the promotion Request
                        promotionListEmployee = new PromotionListEmployee()
                        promotionListEmployee.firm = employee?.firm
                        promotionListEmployee.employee = employee
                        promotionListEmployee.request = promotionRequest
                        promotionListEmployee.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
                        promotionListEmployee.currentEmploymentRecord = employee?.currentEmploymentRecord
                        promotionListEmployee.recordStatus = EnumListRecordStatus.NEW

                        //get the next military rank of employee
                        militaryRank = MilitaryRank.findByOrderNoAndFirm(employee?.currentEmployeeMilitaryRank?.militaryRank.orderNo + 1, promotionList?.firm)

                        if (militaryRank && params["eligible"]) {
                            //set the list employee (next) military rank
                            promotionListEmployee?.militaryRank = militaryRank
                        }

                        //check if it is eligible case or not to set the record type
                        if (params["eligible"]) {
                            promotionListEmployee.promotionReason = EnumPromotionReason.ELIGIBLE
                        } else {
                            promotionListEmployee?.promotionReason = EnumPromotionReason.EXCEPTIONAL
                        }
                        //calculate the due date depends on military rank configuration.
                        promotionListEmployee?.dueDate = this.calculateDueDate(employee)
                        if (!promotionListEmployee?.dueDate) {
                            promotionList.errors.reject("promotionList.calculateDueDate.error")
                            throw new Exception("error occur while calculate the DueDate")
                        }

                        promotionListEmployee.promotionList = promotionList
                        promotionListEmployee.validate()
                        //add the promotion list employee to list
                        promotionList.addToPromotionListEmployee(promotionListEmployee)
                        if (!promotionListEmployee?.validate()) {
                            promotionListEmployee.errors.globalErrors.each { ObjectError objectError ->
                                promotionList.errors.reject(message: messageSource.getMessage(objectError?.code, objectError?.arguments, objectError?.defaultMessage, LocaleContextHolder.getLocale()))
                            }
                        }

                        promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.promotionListEmployee = promotionListEmployee
                        promotionListEmployee?.employee?.validate()
                    }
                    //save the promotion list changes
                    promotionList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (promotionList?.errors?.allErrors?.size() == 0) {
                        promotionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
            }
        } else {
            promotionList.errors.reject("list.request.notSelected.error")
        }
        return promotionList
    }

    /**
     * send Promotion list
     * @param GrailsParameterMap params
     * @return boolean
     */
    PromotionList sendList(GrailsParameterMap params) {
        //return promotionListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            //return the corsponding list:
            PromotionList promotionList = PromotionList.get(params["id"])

            //return error if no request added to list
            if (promotionList?.promotionListEmployee?.size() == 0) {
                promotionList.errors.reject("list.sendList.error")
                return promotionList
            }

            if (promotionList) {
                try {
                    //to change the correspondenceListStatus to submitted when we send the marital status list
                    // and change the from date to the date of sending marital status list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['sendDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = promotionList
                    correspondenceListStatus.receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    correspondenceListStatus.firm = promotionList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.SUBMITTED
                    if (correspondenceListStatus.save(flush:true, failOnError:true)) {
                        promotionList.currentStatus = correspondenceListStatus
                    }
                    //enter the manualIncomeNo when we send the marital status list
                    promotionList.manualOutgoingNo = params.manualOutgoingNo
                    //loop in all promotionListEmployee in marital status list to change the status of the request
                    promotionList?.promotionListEmployee.each { PromotionListEmployee promotionListEmployee ->
                        //promotionListEmployee?.promotionRequest?.promotionListEmployee = promotionListEmployee
                        promotionListEmployee?.request?.requestStatus = EnumRequestStatus.SENT_BY_LIST
                        promotionListEmployee?.request?.validate()
                        promotionListEmployee?.request?.save(flush:true, failOnError:true)
                    }

                    //save the disciplinary list changes
                    promotionList.save(flush:true, failOnError:true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (promotionList?.errors?.allErrors?.size() == 0) {
                        promotionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return promotionList
            }
        }
    }

    /**
     * receive Promotion list
     * @param GrailsParameterMap params
     * @return boolean
     */
    PromotionList receiveList(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            //return PromotionList is
            PromotionList promotionList = PromotionList.load(params["id"])
            if (promotionList) {
                try {
                    //to change the correspondenceListStatus to received when we received the promotion list
                    // and change the to date to the date of receive PromotionList
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['receiveDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = promotionList
                    correspondenceListStatus.receivingParty = promotionList?.receivingParty
                    correspondenceListStatus.firm = promotionList?.firm
                    correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.RECEIVED
                    if (correspondenceListStatus.save()) {
                        promotionList.currentStatus = correspondenceListStatus
                    }

                    //enter the manualIncomeNo when we receive the promotion list
                    promotionList.manualIncomeNo = params.manualIncomeNo
                    //save the promotion list changes
                    promotionList.save(failOnError: true, flush: true)
                } catch (Exception ex) {
                    ex.printStackTrace()
                    transactionStatus.setRollbackOnly()
                    if (promotionList?.errors?.allErrors?.size() == 0) {
                        promotionList.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
                    }
                }
                return promotionList
            }
        } else {
            PromotionList promotionList = new PromotionList()
            promotionList.errors.reject('default.not.found.message', [messageSource.getMessage('promotionList.label', null, 'promotionList', LocaleContextHolder.getLocale())] as Object[], "This promotionList with ${params.id} not found")
            return promotionList
        }
    }

    /**
     * to change the promotionList request status to Approved in the promotionList and save new statuses in core
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToApproved(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_promotionEmployeeIdsList");
        params.remove("checked_promotionEmployeeIdsList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of promotion requests :
            List<PromotionListEmployee> promotionListEmployeeList = PromotionListEmployee.executeQuery("from PromotionListEmployee d where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (promotionListEmployeeList) {
                promotionListEmployeeList.each { PromotionListEmployee promotionListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['orderDate'])) {
                            PromotionListEmployeeNote note = new PromotionListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['orderDate']),
                                    note: "added when approve the record.",
                                    orderNo: params.orderNo,
                                    promotionListEmployee: promotionListEmployee,
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
                                promotionListEmployee.addToPromotionListEmployeeNotes(note);
                            }
                        }

                        if (!errors) {
                            //change the promotion list employee record status
                            promotionListEmployee = saveApprovalInfo(promotionListEmployee, params)
                            promotionListEmployee?.recordStatus = EnumListRecordStatus.APPROVED
                            promotionListEmployee?.managerialOrderNumber = params["orderNo"]
                            promotionListEmployee?.orderDate = PCPUtils.parseZonedDateTime(params['orderDate'])
                            //save the instant
                            promotionListEmployee.save(failOnError: true, flush: true)
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
     * separated to be used by AOC
     * @param record
     * @param params
     * @return
     */
    public PromotionListEmployee saveApprovalInfo(PromotionListEmployee promotionListEmployee, GrailsParameterMap params) {
        promotionListEmployee?.actualDueDate = params['actualDueDate'] ? PCPUtils.parseZonedDateTime(params['actualDueDate']) :
                promotionListEmployee?.actualDueDate ?: PCPUtils.DEFAULT_ZONED_DATE_TIME
        promotionListEmployee?.militaryRank = MilitaryRank.get(params["militaryRank"])
        promotionListEmployee?.militaryRankClassification = MilitaryRankClassification.get(params["militaryRankClassification"])
        promotionListEmployee?.militaryRankType = MilitaryRankType.get(params["militaryRankType"])
        promotionListEmployee?.militaryRankTypeDate = params['militaryRankTypeDate'] ? PCPUtils.parseZonedDateTime(params['militaryRankTypeDate']) :
                promotionListEmployee?.militaryRankTypeDate ?: PCPUtils.DEFAULT_ZONED_DATE_TIME
        promotionListEmployee?.employmentDate = params['employmentDate'] ? PCPUtils.parseZonedDateTime(params['employmentDate']) :
                promotionListEmployee?.employmentDate ?: PCPUtils.DEFAULT_ZONED_DATE_TIME
        promotionListEmployee.validate()
        promotionListEmployee?.dueDate = promotionListEmployee?.dueDate ?: PCPUtils.DEFAULT_ZONED_DATE_TIME
        return promotionListEmployee
    }

    /**
     * to change the promotionList request status to rejected in the receive promotionList
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map changeRequestToRejected(GrailsParameterMap params) {
        //to get list of request ids
        List checkedRequestIdList = params.listString("checked_promotionEmployeeIdsList");
        params.remove("checked_promotionEmployeeIdsList");
        Map dataMap = [:]
        Boolean saved = true
        List errors = []

        if (checkedRequestIdList.size() > 0) {
            //retrieve the list of promotion requests :
            List<PromotionListEmployee> promotionListEmployeeList = PromotionListEmployee.executeQuery("from PromotionListEmployee p where id in (:checkedRequestIdList)", [checkedRequestIdList: checkedRequestIdList])
            if (promotionListEmployeeList) {
                promotionListEmployeeList.each { PromotionListEmployee promotionListEmployee ->
                    try {
                        //save the note if its entered:
                        //create note instance:
                        if (PCPUtils.parseZonedDateTime(params['noteDate'])) {
                            PromotionListEmployeeNote note = new PromotionListEmployeeNote(
                                    noteDate: PCPUtils.parseZonedDateTime(params['noteDate']),
                                    note: params.note,
                                    orderNo: params.orderNo,
                                    promotionListEmployee: promotionListEmployee,
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
                                promotionListEmployee.addToPromotionListEmployeeNotes(note);
                            }
                        } else {
                            errors << [field  : "global",
                                       message: messageSource.getMessage("correspondenceListNote.require.error.label", null as Object[], "Note is required if you want to reject the requests.", LocaleContextHolder.getLocale())]
                            saved = false
                        }

                        //if there are no errors, save the changes
                        if (!errors) {
                            //change the promotion list employee record status
                            promotionListEmployee?.recordStatus = EnumListRecordStatus.REJECTED

                            //save the request employee instance:
                            promotionListEmployee.save(flush: true, failOnError: true);
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
     * close promotion list
     * loop on all approved records and reflect the changes to employee profile (employee promotion, profile)
     * @param GrailsParameterMap params
     * @return boolean
     */
    Map closeList(GrailsParameterMap params) {
        //return promotionListId
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        if (params.id) {
            //return the correspondence list:
            PromotionList promotionList = PromotionList.get(params["id"])
            if (promotionList) {
                try {
                    // to change the correspondenceListStatus to submitted when we close the promotion list
                    // and change the from date when closing the list
                    CorrespondenceListStatus correspondenceListStatus = new CorrespondenceListStatus()
                    correspondenceListStatus.fromDate = PCPUtils.parseZonedDateTime(params['closeDate'])
                    correspondenceListStatus.toDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                    correspondenceListStatus.correspondenceList = promotionList
                    correspondenceListStatus.receivingParty = promotionList?.receivingParty
                    correspondenceListStatus.firm = promotionList?.firm
                    if (PromotionListEmployee.countByPromotionListAndRecordStatus(promotionList, EnumListRecordStatus.NEW) > 0) {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.PARTIALLY_CLOSED
                    } else {
                        correspondenceListStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CLOSED

                    }

                    if (correspondenceListStatus.save(flush:true, failOnError:true)) {
                        promotionList.currentStatus = correspondenceListStatus
                    }

                    promotionList?.promotionListEmployee?.each { PromotionListEmployee promotionListEmployee ->
                        Request promotionRequest = promotionListEmployee?.request
                        if (promotionListEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            promotionRequest?.requestStatus = EnumRequestStatus.APPROVED

                            // reflect request changes
                            //requestChangesHandlerService.applyRequestChanges(promotionRequest)

                        } else {
                            promotionRequest?.requestStatus = EnumRequestStatus.REJECTED
                        }

                        // set external order numbers
                        List <ListNote> orderNumberNoteList = promotionListEmployee?.promotionListEmployeeNotes?.findAll {it.orderNo != null}?.sort{it?.id}
                        if(orderNumberNoteList?.size() > 0){
                            ListNote orderNumberNote = orderNumberNoteList?.last()
                            promotionRequest.externalOrderNumber= orderNumberNote.orderNo
                            promotionRequest.externalOrderDate= orderNumberNote.noteDate
                        }
                        promotionRequest?.validate()
                        promotionRequest?.save(flush:true, failOnError:true)

                    }

                    promotionList.save(failOnError: true, flush: true)
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
            saved = false
            errors << [field  : "global",
                       message: messageSource.getMessage('default.not.found.message',
                               [messageSource.getMessage('promotionList.label', null, 'promotionList', LocaleContextHolder.getLocale())] as Object[],
                               "This promotionList is not found", LocaleContextHolder.getLocale())]
        }
        //add the errors and the status to map
        dataMap.put("errors", errors)
        dataMap.put("saved", saved)
        return dataMap
    }

    /**
     * create EmployeePromotion for MilitaryRankType Or Classification
     * @param PromotionListEmployee promotionListEmployee
     * @return EmployeePromotion
     */
    EmployeePromotion changeMilitaryRankTypeOrClassification(PromotionListEmployee promotionListEmployee) {
        EmployeePromotion employeePromotion = new EmployeePromotion()
        employeePromotion?.employee = promotionListEmployee?.employee
        employeePromotion?.militaryRank = promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRank
        employeePromotion?.dueDate = promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.dueDate
        employeePromotion?.actualDueDate = promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.actualDueDate
        employeePromotion?.note = messageSource.getMessage("EnumPromotionReason.${promotionListEmployee?.promotionReason?.toString()}", null, promotionListEmployee?.promotionReason?.toString(), LocaleContextHolder.getLocale())
        employeePromotion?.firm = promotionListEmployee?.firm

        //set the new values:
        employeePromotion?.militaryRankType = promotionListEmployee?.militaryRankType ? promotionListEmployee?.militaryRankType : promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRankType
        employeePromotion?.militaryRankClassification = promotionListEmployee?.militaryRankClassification ? promotionListEmployee?.militaryRankClassification : promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRankClassification
        employeePromotion?.militaryRankTypeDate = promotionListEmployee?.militaryRankTypeDate
        employeePromotion?.militaryRankTypeDate = promotionListEmployee?.militaryRankTypeDate
        employeePromotion?.dueReason = promotionListEmployee?.promotionReason
        employeePromotion?.managerialOrderNumber = promotionListEmployee?.managerialOrderNumber
        employeePromotion?.orderDate = promotionListEmployee?.orderDate
        employeePromotion?.requestSource = promotionListEmployee?.request

        return employeePromotion
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
        String query = "FROM promotion_list al  LEFT JOIN " +
                "  (SELECT ale.promotion_list_id ,count(ale.id) no_of_employee" +
                "  FROM promotion_list_employee ale " +
                "  group by ale.promotion_list_id ) b" +
                "  on al.id= b.promotion_list_id , correspondence_list_status cls,correspondence_list cl" +
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

        List<PromotionList> results = []
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

            PromotionList promotionList = new PromotionList(
                    code: resultRow[1],
                    name: resultRow[2],
                    coverLetter: resultRow[15],
                    manualOutgoingNo: resultRow[8],
                    manualIncomeNo: resultRow[9],
                    transientData: [sendDate: sendDateZonedDateTime, receiveDate: receiveDateZonedDateTime, numberOfCompetitorsValue: resultRow[12]],
                    receivingParty: resultRow[13],
                    trackingInfo: [dateCreatedUTC: dateCreatedUTC, lastUpdatedUTC: lastUpdatedUTC])
            promotionList.id = resultRow[0]

            CorrespondenceListStatus currentStatus = new CorrespondenceListStatus(
                    correspondenceListStatus: EnumCorrespondenceListStatus.valueOf(resultRow[10].toString()),
                    fromDate: fromDateZonedDateTime,
                    toDate: toDateZonedDateTime)
            currentStatus.id = resultRow[11]

            promotionList.currentStatus = currentStatus
            results.add(promotionList)
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
     * update employee promotion records in case new promotion
     *       - Update Military Rank: Update employee military rank current details based on military rank type/classification date
     *       (it should be <= current date )
     *       - Period & Situation Settlement Update settlement changes , military rank, employment date, etc based on (actualDueDate) date
     *       (it should be <= current date )
     */

    void updateEmployeePromotionRecord(Firm firm) {
        try {
            //return all PromotionListEmployee with Approved status and in same firm and with closed list
            List<PromotionListEmployee> promotionListEmployees = PromotionListEmployee.executeQuery("from PromotionListEmployee ple where " +
                    "ple.recordStatus = :recordStatus and ple.promotionList.id in (select id from PromotionList l where l.firm.id = :firmId " +
                    "and l.currentStatus.id in " +
                    " (select id from CorrespondenceListStatus c where c.correspondenceList.id = l.id and correspondenceListStatus in (:correspondenceListStatus)) ) and " +
                    " ( ple.militaryRankTypeDate <= :currentDate or ple.actualDueDate <= :currentDate or ple.employmentDate <= :currentDate ) ",
                    [firmId: firm?.id, correspondenceListStatus: [EnumCorrespondenceListStatus.CLOSED, EnumCorrespondenceListStatus.PARTIALLY_CLOSED], recordStatus: EnumListRecordStatus.APPROVED, currentDate: ZonedDateTime.now()])

            if (promotionListEmployees) {
                EmployeePromotion employeePromotion

                // variables for notifications
                Boolean isRequestUpdated = false
                Map notificationParams = [:]
                GrailsParameterMap notificationGrailsParams
                SearchBean searchBean = null
                UserDTO userDTO
                Map<String, Map<Integer, String>> notificationTermsMap = [:]
                Map<Integer, String> notificationKeys = [:]
                Map<Integer, String> notificationValues = [:]
                String notificationPromotedValue
                String notificationPromotionReason
                promotionListEmployees.each { PromotionListEmployee promotionListEmployee ->
                    notificationPromotionReason = messageSource.getMessage("EnumPromotionReason.${promotionListEmployee?.promotionReason}", null as Object[], new Locale("ar"))
                    //switch on type to update the employee promotion:
                    switch (promotionListEmployee?.promotionReason) {
                        case EnumPromotionReason.UPDATE_MILITARY_RANK_TYPE:
                            employeePromotion = changeMilitaryRankTypeOrClassification(promotionListEmployee)
                            notificationPromotedValue = promotionListEmployee?.militaryRankType?.descriptionInfo?.localName
                            notificationParams.text = "${messageSource.getMessage("promotionListEmployee.notification.approveMilitaryRankRequest.message", ["${notificationPromotionReason}", "${notificationPromotedValue}"] as Object[], new Locale("ar"))}"
                            break
                        case EnumPromotionReason.UPDATE_MILITARY_RANK_CLASSIFICATION:
                            employeePromotion = changeMilitaryRankTypeOrClassification(promotionListEmployee)
                            notificationPromotedValue = promotionListEmployee?.militaryRankClassification?.descriptionInfo?.localName
                            notificationParams.text = "${messageSource.getMessage("promotionListEmployee.notification.approveMilitaryRankClassificationRequest.message", ["${notificationPromotionReason}", "${notificationPromotedValue}"] as Object[], new Locale("ar"))}"

                            break

                        case EnumPromotionReason.PERIOD_SETTLEMENT_OLD_ARREST:
                            promotionListEmployee?.employee?.employmentDate = promotionListEmployee?.employmentDate
                            employeePromotion = promotionListEmployee?.employee?.currentEmployeeMilitaryRank
                            notificationPromotedValue = promotionListEmployee?.employmentDate?.toLocalDate()?.toString()
                            notificationParams.text = "${messageSource.getMessage("promotionListEmployee.notification.approveEmploymentDateRequest.message", ["${notificationPromotionReason}", "${notificationPromotedValue}"] as Object[], new Locale("ar"))}"
                            break
                        case EnumPromotionReason.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD:
                            promotionListEmployee?.employee?.employmentDate = promotionListEmployee?.employmentDate
                            employeePromotion = promotionListEmployee?.employee?.currentEmployeeMilitaryRank
                            notificationPromotedValue = promotionListEmployee?.employmentDate?.toLocalDate()?.toString()
                            notificationParams.text = "${messageSource.getMessage("promotionListEmployee.notification.approveEmploymentDateRequest.message", ["${notificationPromotionReason}", "${notificationPromotedValue}"] as Object[], new Locale("ar"))}"
                            break

                        default:
                            employeePromotion = new EmployeePromotion()
                            employeePromotion?.employee = promotionListEmployee?.employee
                            employeePromotion?.note = messageSource.getMessage("EnumPromotionReason.${promotionListEmployee?.promotionReason?.toString()}", null, promotionListEmployee?.promotionReason?.toString(), LocaleContextHolder.getLocale())
                            employeePromotion?.militaryRankClassification = promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRankClassification
                            employeePromotion?.militaryRankType = promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRankType
                            employeePromotion?.militaryRankTypeDate = promotionListEmployee?.employee?.currentEmployeeMilitaryRank?.militaryRankTypeDate
                            employeePromotion?.firm = promotionListEmployee?.firm

                            //set the new values:
                            employeePromotion?.dueDate = promotionListEmployee?.dueDate
                            employeePromotion?.actualDueDate = promotionListEmployee?.actualDueDate
                            employeePromotion?.militaryRank = promotionListEmployee?.militaryRank
                            employeePromotion?.dueReason = promotionListEmployee?.promotionReason
                            employeePromotion?.managerialOrderNumber = promotionListEmployee?.managerialOrderNumber
                            employeePromotion?.orderDate = promotionListEmployee?.orderDate
                            employeePromotion?.requestSource = promotionListEmployee?.request
                            notificationPromotedValue = promotionListEmployee?.militaryRank?.descriptionInfo?.localName
                            notificationParams.text = "${messageSource.getMessage("promotionListEmployee.notification.approveMilitaryRankRequest.message", ["${notificationPromotionReason}", "${notificationPromotedValue}"] as Object[], new Locale("ar"))}"

                            break
                    }

                    //to set the date-timezone default values for internal objects and has-many list
                    promotionListEmployee?.employee?.validate()
                    //save the new employee promotion
                    employeePromotion?.save(failOnError: true, flush: true)

                    //re-calculate employee vacation balance after approve employee promotion.
                    List <EmployeeVacationBalance> employeeVacationBalanceList = EmployeeVacationBalance.findAllByEmployee(employeePromotion?.employee)
                    if(employeeVacationBalanceList.size() > 0) {
                        EmployeeVacationBalance employeeVacationBalance = employeeVacationBalanceList.last()
                        if (employeeVacationBalance?.vacationConfiguration?.militaryRank != employeePromotion?.employee?.currentEmployeeMilitaryRank?.militaryRank) {
                            employeeVacationBalanceService.calculateEmployeeYearlyBalanceById(employeePromotion?.employee?.id, Short.parseShort(employeePromotion?.actualDueDate?.year + ""), true)
                        }
                    }

                    //update the employee current military rank
                    promotionListEmployee?.employee?.currentEmployeeMilitaryRank = employeePromotion
                    promotionListEmployee?.recordStatus = EnumListRecordStatus.REFLECTED

                    //to set the date-timezone default values for internal objects and has-many list
                    promotionListEmployee?.validate()
                    promotionListEmployee?.save(flush: true, failOnError: true)

                    //create notification

                    //fill notification params and save notification
                    notificationParams["objectSourceId"] = "${promotionListEmployee?.id}"
                    notificationParams.objectSourceReference = PromotionListEmployee.getName()
                    notificationParams.title = "${messageSource.getMessage("promotionList.label", [] as Object[], new Locale("ar"))}"
                    notificationParams.notificationDate = ZonedDateTime.now()
                    notificationParams["notificationType"] = NotificationType.findByTopic("myNotification")

                    notificationTermsMap = [:]
                    notificationKeys = [:]
                    notificationValues = [:]

                    //get employee username
                    searchBean = new SearchBean()
                    searchBean.searchCriteria.put("personId", new SearchConditionCriteriaBean(operand: 'personId', value1: promotionListEmployee?.employee?.personId))
                    searchBean.searchCriteria.put("firmId", new SearchConditionCriteriaBean(operand: 'firmId', value1: firm?.id))
                    userDTO = remoteUserService.getUser(searchBean)

                    //set role
                    notificationKeys.put(new Integer(1), UserTerm.USER.value())
                    notificationValues.put(new Integer(1), "${userDTO?.username}")

                    notificationTermsMap.put("key", notificationKeys)
                    notificationTermsMap.put("value", notificationValues)
                    notificationParams["notificationTerms"] = notificationTermsMap

                    //create empty grails parameter map
                    notificationGrailsParams = new GrailsParameterMap(notificationParams, null)

                    //save notification
                    notificationService.save(notificationGrailsParams)

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
        }

    }


}