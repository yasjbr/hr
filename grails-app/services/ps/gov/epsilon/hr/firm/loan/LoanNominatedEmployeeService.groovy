package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class LoanNominatedEmployeeService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService
    EmployeeService employeeService
    LoanNoticeReplayListService loanNoticeReplayListService

    /**
     * to represent if employee has endorse order
     */
    public static allowEditEndorseOrder = { formatService, LoanNominatedEmployee dataRow, object, params ->
        if (dataRow) {
            return dataRow?.endorseOrder?.id && dataRow?.loanNoticeReplayRequest?.loanNotice?.loanNoticeStatus == EnumLoanNoticeStatus.DONE_NOMINATION
        }
        return false
    }

    /**
     * to represent endorse order id
     */
    public static getEndorseOrderId = { formatService, LoanNominatedEmployee dataRow, object, params ->
        if (dataRow) {
            return dataRow?.endorseOrder?.encodedId
        }
        return ""
    }

    /**
     * to represent loan nominated employee id
     */
    public static getLoanNominatedEmployeeId = { formatService, LoanNominatedEmployee dataRow, object, params ->
        if (dataRow) {
            return dataRow?.encodedId
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "loanNoticeReplayRequest.encodedId", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNoticeReplayRequest.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "loanNoticeReplayRequest.transientData.requestedByOrganizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNoticeReplayRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNoticeReplayRequest.requestStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "allowEditEndorseOrder", type: allowEditEndorseOrder, source: 'domain'],
            [sort: true, search: true, hidden: true, name: "endorseOrderEncodedId", type: getEndorseOrderId, source: 'domain'],
            [sort: true, search: true, hidden: true, name: "loanNominatedEmployeeEncodedId", type: getLoanNominatedEmployeeId, source: 'domain'],
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
        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        String loanNoticeReplayListId = params["loanNoticeReplayList.id"]
        String loanNoticeReplayRequestId = params["loanNoticeReplayRequest.id"]
        String note = params["note"]
        String orderNo = params["orderNo"]
        Short periodInMonth = params.long("periodInMonth")
        String endorseOrderId = params["endorseOrder.id"]
        Boolean isEndorseOrder = params.boolean("isEndorseOrder")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])

        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['toDateTo'])

        ZonedDateTime effectiveDateFrom = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime effectiveDateTo = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])

        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['loanNoticeReplayRequest.requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['loanNoticeReplayRequest.requestDateTo'])

        EnumRequestStatus requestStatus = params["loanNoticeReplayRequest.requestStatus"] ? EnumRequestStatus.valueOf(params["loanNoticeReplayRequest.requestStatus"]) : null
        Long requestedByOrganizationId = params.long("loanNoticeReplayRequest.requestedByOrganizationId")

        Boolean justApprovedEmployee = params.boolean('justApprovedEmployee')

        List loanNominatedEmployeeIds = []
        if (justApprovedEmployee == true) {
            loanNominatedEmployeeIds = LoanNominatedEmployee.executeQuery("select le.id from LoanNominatedEmployee le where le.loanNoticeReplayList.currentStatus.correspondenceListStatus = :listStatus and le.loanNoticeReplayRequest.requestStatus = :status", [listStatus: EnumCorrespondenceListStatus.CLOSED, status: EnumRequestStatus.APPROVED])
            if (!loanNominatedEmployeeIds) {
                loanNominatedEmployeeIds = ["-1"]
            }
        }

        return LoanNominatedEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
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


                if (justApprovedEmployee == true) {
                    inList('id', loanNominatedEmployeeIds)
                }


                if (currentEmployeeMilitaryRankId || militaryRankId) {

                    currentEmployeeMilitaryRank {

                        if (currentEmployeeMilitaryRankId) {
                            eq("id", currentEmployeeMilitaryRankId)
                        }

                        if (militaryRankId) {
                            eq("militaryRank.id", militaryRankId)
                        }

                    }

                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }

                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (loanNoticeReplayListId) {
                    eq("loanNoticeReplayList.id", loanNoticeReplayListId)
                }
                if (loanNoticeReplayRequestId || requestedByOrganizationId || requestDateFrom || requestDateTo || requestStatus) {
                    loanNoticeReplayRequest {

                        if (loanNoticeReplayRequestId) {
                            eq("id", loanNoticeReplayRequestId)
                        }

                        if (requestedByOrganizationId) {
                            eq("requestedByOrganizationId", requestedByOrganizationId)
                        }

                        if (requestDateFrom) {
                            ge("requestDate", requestDateFrom)
                        }
                        if (requestDateTo) {
                            le("requestDate", requestDateTo)
                        }

                        if (requestStatus) {
                            eq("requestStatus", requestStatus)
                        }

                    }
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (periodInMonth) {
                    eq("periodInMonth", periodInMonth)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }

                //from date
                if (fromDate) {
                    eq("fromDate", fromDate)
                }
                if (fromDateFrom) {
                    ge("fromDate", fromDateFrom)
                }

                if (fromDateTo) {
                    le("fromDate", fromDateTo)
                }

                //to date
                if (toDate) {
                    eq("toDate", toDate)
                }
                if (toDateFrom) {
                    ge("toDate", toDateFrom)
                }
                if (toDateTo) {
                    le("toDate", toDateTo)
                }

                //effectiveDate
                if (effectiveDate) {
                    eq("effectiveDate", effectiveDate)
                }
                if (effectiveDateFrom) {
                    ge("effectiveDate", effectiveDateFrom)
                }
                if (effectiveDateTo) {
                    le("effectiveDate", effectiveDateTo)
                }

                if (endorseOrderId) {
                    eq('endorseOrder.id', endorseOrderId)
                }
                employee{
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }

            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case 'loanNoticeReplayRequest.requestDate':
                        loanNoticeReplayRequest {
                            order("requestDate", dir)
                        }
                        break;
                    case 'loanNoticeReplayRequest.requestStatus':
                        loanNoticeReplayRequest {
                            order("requestStatus", dir)
                        }
                        break;
                    case 'loanNoticeReplayRequest.id':
                        loanNoticeReplayRequest{
                            order("trackingInfo.dateCreatedUTC", dir)
                        }
                        break;
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
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

        if (pagedResultList.resultList) {

            SearchBean searchBean = new SearchBean()

            //collect organizationIds
            List organizationIds = pagedResultList?.loanNoticeReplayRequest?.requestedByOrganizationId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
            //get all organization info
            List<OrganizationDTO> organizationList = organizationService.searchOrganization(searchBean)?.resultList

            //collect employeeIds
            List<String> employeeIds = pagedResultList?.employee?.id?.toList()

            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            //get employee info
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)

            //loop to fill all remoting values
            pagedResultList.each { LoanNominatedEmployee loanNominatedEmployee ->

                //fill all employee info
                if (loanNominatedEmployee?.employee) {
                    loanNominatedEmployee.employee = employees.find { it.id == loanNominatedEmployee?.employee?.id }
                }

                //fill all organization info
                if (loanNominatedEmployee.loanNoticeReplayRequest?.requestedByOrganizationId) {
                    loanNominatedEmployee.loanNoticeReplayRequest.transientData.requestedByOrganizationDTO = organizationList.find {
                        it.id == loanNominatedEmployee.loanNoticeReplayRequest.requestedByOrganizationId
                    }
                }

            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return LoanNominatedEmployee.
 */
    LoanNominatedEmployee save(GrailsParameterMap params) {
        LoanNominatedEmployee loanNominatedEmployeeInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            loanNominatedEmployeeInstance = LoanNominatedEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanNominatedEmployeeInstance.version > version) {
                    loanNominatedEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('loanNominatedEmployee.label', null, 'loanNominatedEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanNominatedEmployee while you were editing")
                    return loanNominatedEmployeeInstance
                }
            }
            if (!loanNominatedEmployeeInstance) {
                loanNominatedEmployeeInstance = new LoanNominatedEmployee()
                loanNominatedEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('loanNominatedEmployee.label', null, 'loanNominatedEmployee', LocaleContextHolder.getLocale())] as Object[], "This loanNominatedEmployee with ${params.id} not found")
                return loanNominatedEmployeeInstance
            }
        } else {
            loanNominatedEmployeeInstance = new LoanNominatedEmployee()
        }
        try {
            loanNominatedEmployeeInstance.properties = params;
            loanNominatedEmployeeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!loanNominatedEmployeeInstance?.hasErrors()) {
                loanNominatedEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return loanNominatedEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<LoanNominatedEmployee> loanNominatedEmployeeList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            /**
             * get list of  promotion list employee by list of ids
             */
            loanNominatedEmployeeList = LoanNominatedEmployee.findAllByIdInList(ids)

            /**
             * get list of loan request & revert status to APPROVED_BY_WORKFLOW
             */
            loanNominatedEmployeeList.loanNoticeReplayRequest.each { LoanNoticeReplayRequest loanNoticeReplayRequest ->
                if (loanNoticeReplayRequest) {
                    loanNoticeReplayRequest.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                    loanNoticeReplayRequest.save(flush: true)
                }
            }

            /**
             * delete list of promotion list employee
             */
            if (loanNominatedEmployeeList) {
                loanNominatedEmployeeList*.delete()
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
 * @return LoanNominatedEmployee.
 */
    @Transactional(readOnly = true)
    LoanNominatedEmployee getInstance(GrailsParameterMap params) {
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
 * to get model entry with remoting info.
 * @param GrailsParameterMap params the search map.
 * @return LoanNominatedEmployee.
 */
    @Transactional(readOnly = true)
    LoanNominatedEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
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
        String id = params["loanNoticeReplayList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        LoanNoticeReplayList loanNoticeReplayList = loanNoticeReplayListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = loanNoticeReplayList?.code
        map.coverLetter = loanNoticeReplayList?.coverLetter
        map.details = resultList
        return [map]
    }

}