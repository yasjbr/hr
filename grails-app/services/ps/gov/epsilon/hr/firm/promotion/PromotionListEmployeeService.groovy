package ps.gov.epsilon.hr.firm.promotion

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to create new record for the employee promotion request in the List-
 * <h1>Usage</h1>
 * -create new record for the employee promotion request in the List-
 * <h1>Restriction</h1>
 * --
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class PromotionListEmployeeService {

    MessageSource messageSource
    def formatService
    EmployeeService employeeService
    PromotionListService promotionListService

    //to get the Employee
    public static getEmployee = { cService, PromotionListEmployee rec, object, params ->
        if (rec?.employee) {
            return rec?.employee.toString()
        } else {
            return ""
        }
    }

    //to get the link of each request:
    public static getRequestLink = { cService, PromotionListEmployee rec, object, params ->
        String link = ""
        if (rec?.promotionReason == EnumPromotionReason.UPDATE_MILITARY_RANK_CLASSIFICATION || rec?.promotionReason == EnumPromotionReason.UPDATE_MILITARY_RANK_TYPE) {
            UpdateMilitaryRankRequest updateMilitaryRankRequest = (UpdateMilitaryRankRequest) rec?.request
            link = "../updateMilitaryRankRequest/show?encodedId=${updateMilitaryRankRequest?.encodedId}";
        } else {
            PromotionRequest request = (PromotionRequest) rec?.request
            link = "../promotionRequest/show?encodedId=${request?.encodedId}"
        }
        return link
    }

    //to get the militaryRankType or militaryRankClassification of each request:
    public static getMilitaryRankType = { cService, PromotionListEmployee rec, object, params ->
        if (rec?.promotionReason == EnumPromotionReason.UPDATE_MILITARY_RANK_CLASSIFICATION && rec?.militaryRankClassification) {
            return rec?.militaryRankClassification.toString()
        } else if (rec?.promotionReason == EnumPromotionReason.UPDATE_MILITARY_RANK_TYPE && rec?.militaryRankType){
            return rec?.militaryRankType?.toString()
        }else {
            return ""
        }
    }

    //to get the militaryRank if the record is not exceptional or added by exceptional request:
    public static getMilitaryRank = { cService, PromotionListEmployee rec, object, params ->
        if (rec?.promotionReason == EnumPromotionReason.EXCEPTIONAL) {
            return ""
        } else {
            return rec?.militaryRank?.toString()
        }
    }

    //to return if the record comes from request or not:
    public static isRequest = { cService, PromotionListEmployee rec, object, params ->
        if (rec?.request) {
            return true
        } else {
            return false
        }
    }

    //to return id if the record comes from request else return empty:
    public static getRequestId = { cService, PromotionListEmployee rec, object, params ->
        if (rec?.request) {
            return rec?.request?.id
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "request.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employeeDetails", type: getEmployee, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee.employmentDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee.financialNumber", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee.currentEmploymentRecord.department", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "promotionReason", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "militaryRank", type: getMilitaryRank, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "militaryRankType", type: getMilitaryRankType, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "showLink", type: getRequestLink, source: 'domain'],
            [sort: true, search: true, hidden: true, name: "isRequest", type: isRequest, source: 'domain'],
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
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        Long firmId = params.long("firm.id")
        String newMilitaryRankId = params["militaryRank.id"]
        String militaryRankTypeId = params["militaryRankType.id"]
        String promotionListId = params["promotionList.id"]
        Set promotionListEmployeeNotesIds = params.listString("promotionListEmployeeNotes.id")
        String militaryRankClassificationId = params["militaryRankClassification.id"]
        ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason promotionReason = params["promotionReason"] ? ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.valueOf(params["promotionReason"]) : null
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String statusReason = params["statusReason"]
        String status = params["status"]
        //search about employee or his military rank :
        String employeeId = params["employee.id"]
        String requestId = params["request.id"]
        String militaryRankId = params["militaryRankId"]
        String financialNumber = params["financialNumber"]

        //search on department:
        String departmentIdList = params["departmentIdList"]

        //search about actualDueDate range
        ZonedDateTime actualDueDate = PCPUtils.parseZonedDateTime(params['actualDueDate'])
        ZonedDateTime actualDueDateFrom = PCPUtils.parseZonedDateTime(params['actualDueDateFrom'])
        ZonedDateTime actualDueDateTo = PCPUtils.parseZonedDateTime(params['actualDueDateTo'])

        //search about employmentDate range
        ZonedDateTime employmentDate = PCPUtils.parseZonedDateTime(params['employmentDate'])
        ZonedDateTime employmentDateFrom = PCPUtils.parseZonedDateTime(params['employmentDateFrom'])
        ZonedDateTime employmentDateTo = PCPUtils.parseZonedDateTime(params['employmentDateTo'])

        return PromotionListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("statusReason", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (requestId) {
                    eq("request.id", requestId)
                }
                if (newMilitaryRankId) {
                    eq("militaryRank.id", newMilitaryRankId)
                }
                if (militaryRankTypeId) {
                    eq("militaryRankType.id", militaryRankTypeId)
                }
                if (militaryRankClassificationId) {
                    eq("militaryRankClassification.id", militaryRankClassificationId)
                }
                if (promotionListId) {
                    eq("promotionList.id", promotionListId)
                }
                if (promotionListEmployeeNotesIds) {
                    promotionListEmployeeNotes {
                        inList("id", promotionListEmployeeNotesIds)
                    }
                }
                if (promotionReason) {
                    eq("promotionReason", promotionReason)
                }
                //actualDueDate
                if (actualDueDate) {
                    eq("actualDueDate", actualDueDate)
                }
                if (actualDueDateFrom) {
                    ge("actualDueDate", actualDueDateFrom)
                }
                if (actualDueDateTo) {
                    le("actualDueDate", actualDueDateTo)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (statusReason) {
                    ilike("statusReason", "%${statusReason}%")
                }
                employee {
                    if (employeeId) {
                        eq("id", employeeId)
                    }
                    if (militaryRankId) {
                        currentEmployeeMilitaryRank {
                            eq("militaryRank.id", militaryRankId)
                        }
                    }
                    if (financialNumber) {
                        ilike("financialNumber", financialNumber)
                    }
                    //employmentDate
                    if (employmentDate) {
                        eq("employmentDate", employmentDate)
                    }
                    if (employmentDateFrom) {
                        ge("employmentDate", employmentDateFrom)
                    }
                    if (employmentDateTo) {
                        le("employmentDate", employmentDateTo)
                    }
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (currentEmploymentRecordId || departmentIdList) {
                    currentEmploymentRecord {
                        if (currentEmploymentRecordId) {
                            eq("id", currentEmploymentRecordId)
                        }
                        if (departmentIdList) {
                            department {
                                eq("id", departmentIdList)
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

                if(columnName.contains(".")) {
                    switch (columnName) {
                        case "employee.employmentDate":
                            employee {
                                order("employmentDate", dir)
                            }
                            break;
                        case "employee.financialNumber":
                            employee {
                                order("financialNumber", dir)
                            }
                            break;
                        case "employee.currentEmploymentRecord.department":
                            employee {
                                currentEmploymentRecord{
                                    order("department", dir)
                                }
                            }
                            break;
                        case 'request.id':
                            request{

                            }
                            break;
                        default:
                            order(columnName, dir)
                            break;
                    }
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)
        //get the employees ids
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id

        //get employee remote details
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)

        pagedResultList?.resultList.each { PromotionListEmployee promotionListEmployee ->
            promotionListEmployee?.employee = employeeList?.find { it?.id == promotionListEmployee?.employee?.id }
        }
        return pagedResultList
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<PromotionListEmployee> promotionListEmployees = null

            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  promotion list employee by list of ids
                 */
                promotionListEmployees = PromotionListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  promotion list employee by list of ids
                 */
                promotionListEmployees = PromotionListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of promotion request & revert status to APPROVED_BY_WORKFLOW
             */
            List<Request> requests = promotionListEmployees?.request
            requests?.removeAll(Collections.singleton(null));
            requests?.each { Request request ->
                if (request) {
                    request?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                }
            }

            /**
             * remove the list reference from employee current promotion record
             */
            List<Employee> employees = promotionListEmployees?.employee
            employees?.removeAll(Collections.singleton(null));
            employees?.each { Employee employee ->
                if (employee) {
                    employee?.currentEmployeeMilitaryRank?.promotionListEmployee = null
                    employee.validate()
                    employee.save()
                }
            }

            /**
             * delete list of promotion list employee
             */
            promotionListEmployees*.delete()
            deleteBean.status = true
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

//
//
//    /**
//     * to save/update model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return PromotionListEmployee.
//     */
/*    PromotionListEmployee save(GrailsParameterMap params) {
        PromotionListEmployee promotionListEmployeeInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            promotionListEmployeeInstance = PromotionListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (promotionListEmployeeInstance.version > version) {
                    promotionListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('promotionListEmployee.label', null, 'promotionListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this promotionListEmployee while you were editing")
                    return promotionListEmployeeInstance
                }
            }
            if (!promotionListEmployeeInstance) {
                promotionListEmployeeInstance = new PromotionListEmployee()
                promotionListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('promotionListEmployee.label', null, 'promotionListEmployee', LocaleContextHolder.getLocale())] as Object[], "This promotionListEmployee with ${params.id} not found")
                return promotionListEmployeeInstance
            }
        } else {
            promotionListEmployeeInstance = new PromotionListEmployee()
        }
        try {
            promotionListEmployeeInstance.properties = params;
            Employee employee = promotionListEmployeeInstance?.employee
            if (employee?.currentEmploymentRecord) {
                //save the current employee record
                promotionListEmployeeInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }
            if (employee?.currentEmployeeMilitaryRank) {
                //save the current employee military rank
                promotionListEmployeeInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank
            }
            promotionListEmployeeInstance.save(failOnError:true, flush:true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            promotionListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return promotionListEmployeeInstance
    }*/
//
//    /**
//     * to delete model entry.
//     * @param DeleteBean deleteBean.
//     * @return DeleteBean.
//     * @see DeleteBean.
//     */
//    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
//        try {
//            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
//                PromotionListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
//                deleteBean.status = true
//            } else if (deleteBean.ids) {
//                PromotionListEmployee.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
//                deleteBean.status = true
//            }
//        }
//        catch (Exception ex) {
//            deleteBean.status = false
//            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
//        }
//        return deleteBean
//
//    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return PromotionListEmployee.
     */
    @Transactional(readOnly = true)
    PromotionListEmployee getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return PromotionListEmployee.
     */
    @Transactional(readOnly = true)
    PromotionListEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                return results[0]
            }
        }
        return null

    }

//    /**
//     * to auto complete model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return JSON.
//     */
//    @Transactional(readOnly = true)
//    JSON autoComplete(GrailsParameterMap params) {
//        List<Map> dataList = []
//        String idProperty = params["idProperty"] ?: "id"
//        String nameProperty = params["nameProperty"] ?: "descriptionInfo.localName"
//        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
//        try {
//            grails.gorm.PagedResultList resultList = this.search(params)
//            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
//        } catch (Exception ex) {
//            ex.printStackTrace()
//        }
//        return dataList as JSON
//    }

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
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["promotionList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PromotionList promotionList = promotionListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = promotionList?.code
        map.coverLetter = promotionList?.coverLetter
        map.details = resultList
        return [map]
    }

}