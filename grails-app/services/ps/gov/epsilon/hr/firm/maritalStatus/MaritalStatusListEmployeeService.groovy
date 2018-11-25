package ps.gov.epsilon.hr.firm.maritalStatus

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create marital status employee for list
 * <h1>Usage</h1>
 * -this service is used to create marital status employee for list
 * <h1>Restriction</h1>
 * -need marital status request & list
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class MaritalStatusListEmployeeService {

    MessageSource messageSource
    def formatService
    MaritalStatusRequestService maritalStatusRequestService
    MaritalStatusListService maritalStatusListService

    /**
     * this closure is used to return the maritalStatusRequest id
     */
    public static requestEncodedId = { cService, MaritalStatusListEmployee rec, object, params ->
        if (rec?.maritalStatusRequest) {
            return rec?.maritalStatusRequest?.encodedId
        } else {
            return ""
        }
    }

    /**
     * this closure is used to return the employee name + promotion
     */
    public static getEmployeeToString = { cService, MaritalStatusListEmployee rec, object, params ->
        if (rec?.maritalStatusRequest?.employee) {
            return rec?.maritalStatusRequest?.employee?.toString()
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
            [sort: true, search: false, hidden: false, name: "maritalStatusList", type: "MaritalStatusList", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusRequest", type: "MaritalStatusRequest", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "statusReason", type: "String", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestEncodedId", type: requestEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusRequest.id", type: "Long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeFullName", type: getEmployeeToString, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusRequest.requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusRequest.employee.financialNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusRequest.transientData.oldMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusRequest.transientData.newMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusRequest.maritalStatusDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusRequest.transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
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
        String domainName = params["domainName"]
        String columnName
        if (column) {
            switch (domainName){
                case 'LIST_DOMAIN_COLUMNS' :
                    columnName = LIST_DOMAIN_COLUMNS[column]?.name
                    break
                default:
                    columnName = DOMAIN_COLUMNS[column]?.name
            }
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
        String maritalStatusListId = params["maritalStatusList.id"]
        String maritalStatusRequestId = params["maritalStatusRequest.id"]
        String orderNo = params["orderNo"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String statusReason = params["statusReason"]
        //search for request status
        List<ps.gov.epsilon.hr.enums.v1.EnumRequestStatus> requestStatusList = params.list("requestStatus")
        String employeeId = params["employee.id"]
        Long firmId = params.long("firm.id")
        Long newMaritalStatusId = params.long("newMaritalStatusId")
        Long oldMaritalStatusId = params.long("oldMaritalStatusId")
        Long relatedPersonId = params.long("relatedPersonId")
        String status = params["status"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])

        ZonedDateTime maritalStatusDate = PCPUtils.parseZonedDateTime(params['maritalStatusDate'])
        ZonedDateTime fromMaritalStatusDate = PCPUtils.parseZonedDateTime(params['maritalStatusDateFrom'])
        ZonedDateTime toMaritalStatusDate = PCPUtils.parseZonedDateTime(params['maritalStatusDateTo'])
        String militaryRankId = params["militaryRank.id"]
        String financialNumber = params["financialNumber"]

        return MaritalStatusListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("orderNo", sSearch)
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
                if (maritalStatusListId) {
                    eq("maritalStatusList.id", maritalStatusListId)
                }
                if (maritalStatusRequestId) {
                    eq("maritalStatusRequest.id", maritalStatusRequestId)
                }
                if (requestStatusList) {//check type in list
                    maritalStatusRequest {
                        inList("requestStatus", requestStatusList)
                    }
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (statusReason) {
                    ilike("statusReason", "%${statusReason}%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))

                maritalStatusRequest {
                    if (employeeId || militaryRankId || financialNumber) {
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
                        }
                    }
                    if (maritalStatusDate) {
                        eq("maritalStatusDate", maritalStatusDate)
                    }
                    if (fromMaritalStatusDate) {
                        ge("maritalStatusDate", fromMaritalStatusDate)
                    }
                    if (toMaritalStatusDate) {
                        le("maritalStatusDate", toMaritalStatusDate)
                    }
                    if (newMaritalStatusId) {
                        eq("newMaritalStatusId", newMaritalStatusId)
                    }
                    if (oldMaritalStatusId) {
                        eq("oldMaritalStatusId", oldMaritalStatusId)
                    }
                    if (relatedPersonId) {
                        eq("relatedPersonId", relatedPersonId)
                    }
                    if (requestDate) {
                        eq("requestDate", requestDate)
                    }
                    if (fromRequestDate) {
                        ge("requestDate", fromRequestDate)
                    }
                    if (toRequestDate) {
                        le("requestDate", toRequestDate)
                    }
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                // solution of sorting by id problem after id become string
                switch (columnName) {
                    case 'maritalStatusRequest.id':
                        maritalStatusRequest{
                            order("trackingInfo.dateCreatedUTC", dir)
                        }
                        break
                    default:
                        order(columnName, dir)
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
        pagedResultList?.resultList?.each { MaritalStatusListEmployee maritalStatusListEmployee ->
            GrailsParameterMap requestParam = new GrailsParameterMap([id: maritalStatusListEmployee?.maritalStatusRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            maritalStatusListEmployee?.maritalStatusRequest = maritalStatusRequestService.getInstanceWithRemotingValues(requestParam)
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
            List<MaritalStatusListEmployee> maritalStatusListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  marital status list employee by list of ids
                 */
                maritalStatusListEmployeeList = MaritalStatusListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  marital status list employee by list of ids
                 */
                maritalStatusListEmployeeList = MaritalStatusListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of marital status request & revert status to APPROVED_BY_WORKFLOW
             */
            List<MaritalStatusRequest> maritalStatusRequestList = maritalStatusListEmployeeList?.maritalStatusRequest
            maritalStatusRequestList?.each { MaritalStatusRequest maritalStatusRequest ->
                maritalStatusRequest?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            }
            /**
             * delete list of marital status list employee
             */
            if (maritalStatusListEmployeeList) {
                maritalStatusListEmployeeList*.delete()
                deleteBean.status = true
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
     * @return AllowanceListEmployee.
     */
    @Transactional(readOnly = true)
    MaritalStatusListEmployee getInstance(GrailsParameterMap params) {
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
        String id = params["maritalStatusList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        MaritalStatusList maritalStatusList = maritalStatusListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = maritalStatusList?.code
        map.coverLetter = maritalStatusList?.coverLetter
        map.details = resultList
        return [map]
    }

}