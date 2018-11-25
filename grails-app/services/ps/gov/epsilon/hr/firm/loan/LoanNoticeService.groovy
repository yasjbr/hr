package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
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
class LoanNoticeService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService

    /**
     * to represent request status
     */
    public static getStatus = { formatService, LoanNotice dataRow, object, params ->
        if (dataRow) {
            return dataRow?.loanNoticeStatus?.toString()
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
            [sort: true, search: false, hidden: false, name: "requestedJob", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.requesterOrganizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "numberOfPositions", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanNoticeStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "status", type: getStatus, source: 'domain'],
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
        String description = params["description"]
        Long firmId = params.long("firm.id")
        String jobTitle = params["jobTitle"]
        Short numberOfPositions = params.long("numberOfPositions")
        ZonedDateTime orderDate = PCPUtils.parseZonedDateTime(params['orderDate'])
        String orderNo = params["orderNo"]
        Short periodInMonths = params.long("periodInMonths")
        String requestedJobId = params["requestedJob.id"]
        Long requesterOrganizationId = params.long("requesterOrganizationId")
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null
        EnumLoanNoticeStatus loanNoticeStatus = params["loanNoticeStatus"] ? EnumLoanNoticeStatus.valueOf(params["loanNoticeStatus"]) : null


        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['toDateTo'])

        return LoanNotice.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("description", sSearch)
                    ilike("jobTitle", sSearch)
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
                if (description) {
                    ilike("description", "%${description}%")
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))

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


                if (jobTitle) {
                    ilike("jobTitle", "%${jobTitle}%")
                }
                if (numberOfPositions) {
                    eq("numberOfPositions", numberOfPositions)
                }
                if (orderDate) {
                    le("orderDate", orderDate)
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (periodInMonths) {
                    eq("periodInMonths", periodInMonths)
                }
                if (requestedJobId) {
                    eq("requestedJob.id", requestedJobId)
                }
                if (requesterOrganizationId) {
                    eq("requesterOrganizationId", requesterOrganizationId)
                }
                if (loanNoticeStatus) {
                    eq("loanNoticeStatus", loanNoticeStatus)
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                // solution of sorting by id problem after id become string
                switch (columnName) {
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
            List organizationIds = pagedResultList?.requesterOrganizationId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
            //fill all organization info
            List<OrganizationDTO> organizationList = organizationService.searchOrganization(searchBean)?.resultList

            //loop to fill all remoting values
            pagedResultList.each { LoanNotice loanNotice ->

                //fill all organization info
                if (loanNotice?.requesterOrganizationId) {
                    loanNotice.transientData.requesterOrganizationDTO = organizationList.find {
                        it.id == loanNotice?.requesterOrganizationId
                    }
                }
            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return LoanNotice.
 */
    LoanNotice save(GrailsParameterMap params) {
        LoanNotice loanNoticeInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            loanNoticeInstance = LoanNotice.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (loanNoticeInstance.version > version) {
                    loanNoticeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('loanNotice.label', null, 'loanNotice', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this loanNotice while you were editing")
                    return loanNoticeInstance
                }
            }
            if (!loanNoticeInstance) {
                loanNoticeInstance = new LoanNotice()
                loanNoticeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('loanNotice.label', null, 'loanNotice', LocaleContextHolder.getLocale())] as Object[], "This loanNotice with ${params.id} not found")
                return loanNoticeInstance
            }
        } else {
            loanNoticeInstance = new LoanNotice()
        }
        try {
            loanNoticeInstance.properties = params;

            //set the status DONE_NOMINATION
            if (params.boolean('endNomination')) {

                //check if loan notice has requests
                int numberOfLoanNoticeRequest = LoanNoticeReplayRequest.countByLoanNotice(loanNoticeInstance)
                if (numberOfLoanNoticeRequest > 0) {
                    loanNoticeInstance.loanNoticeStatus = EnumLoanNoticeStatus.DONE_NOMINATION
                } else {
                    //if no request reject change status
                    loanNoticeInstance.errors.reject("loanNotice.noRequests.label")
                    return loanNoticeInstance
                }
            }

            //set the status COMPLETED and update all employee info
            if (params.boolean('closeNomination')) {

                //collect all employee related to loan notice
                List<LoanNominatedEmployee> loanNominatedEmployeeList = LoanNominatedEmployee.executeQuery("from LoanNominatedEmployee le where le.loanNoticeReplayRequest.loanNotice.id = :loanNoticeId and le.recordStatus in :recordStatus", [loanNoticeId: loanNoticeInstance?.id, recordStatus: [EnumListRecordStatus.APPROVED, EnumListRecordStatus.REJECTED]])


                if (!loanNominatedEmployeeList) {
                    loanNoticeInstance.errors.reject("loanNotice.endorseOrderError.label")
                    return loanNoticeInstance
                }

                //check that all employee hasR endorse order
                int count = LoanNominatedEmployee.createCriteria().count {
                    loanNoticeReplayRequest {
                        eq('loanNotice.id', loanNoticeInstance?.id)
                    }
                    inList("recordStatus", [EnumListRecordStatus.REJECTED, EnumListRecordStatus.APPROVED])
                }


                if (count != loanNominatedEmployeeList?.size()) {
                    loanNoticeInstance.errors.reject("loanNotice.noEndorseOrder.label")
                    return loanNoticeInstance
                } else {
                    //check if loan notice is COMPLETED to change status and update employee info

                    //update loan notice status and set to COMPLETED
                    loanNoticeInstance.loanNoticeStatus = EnumLoanNoticeStatus.COMPLETED

                    //set true if all employee has endorse order
                    Employee employeeInstance
                    loanNominatedEmployeeList?.each { LoanNominatedEmployee loanNominatedEmployee ->

                        if (loanNominatedEmployee?.recordStatus == EnumListRecordStatus.APPROVED) {
                            //set employee instance
                            employeeInstance = loanNominatedEmployee?.employee

                            //set all employee as loan out
                            //add default categoryStatus ملتزم
                            employeeInstance.categoryStatus = EmployeeStatusCategory.load(EnumEmployeeStatusCategory.COMMITTED.value)

                            //close all active employee status with effectiveDate
                            List<EmployeeStatusHistory> employeeStatusHistories = employeeInstance?.employeeStatusHistories?.findAll {
                                (!it.toDate || it.toDate == ps.police.common.utils.v1.PCPUtils.DEFAULT_ZONED_DATE_TIME)
                            }?.sort { it.employeeStatus.descriptionInfo.localName }

                            //close status with effectiveDate
                            employeeStatusHistories.each { EmployeeStatusHistory employeeStatusHistory ->
                                employeeStatusHistory.toDate = loanNominatedEmployee?.effectiveDate
                                employeeStatusHistory.save(flush: true, failOnError: true)
                            }

                            //set the effective date of request as loanNominatedEmployee
                            loanNominatedEmployee.loanNoticeReplayRequest.effectiveDate = loanNominatedEmployee?.effectiveDate
                            loanNominatedEmployee.loanNoticeReplayRequest.save(flush: true, failOnError: true)

                            //add default status when create employee منتدب لدى جهاز اخر
                            EmployeeStatusHistory employeeStatusHistory = new EmployeeStatusHistory()
                            employeeStatusHistory.employeeStatus = EmployeeStatus.load(EnumEmployeeStatus.LOAN_OUT.value)
                            employeeStatusHistory.fromDate = loanNominatedEmployee?.effectiveDate
                            employeeStatusHistory.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                            employeeInstance.currentEmploymentRecord.validate()
                            employeeInstance.addToEmployeeStatusHistories(employeeStatusHistory)
                            employeeInstance.save(flush: true, failOnError: true)

                            employeeInstance = null
                        }
                    }
                }
            }
            loanNoticeInstance.orderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            loanNoticeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            loanNoticeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return loanNoticeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<LoanNotice> loanNoticeList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            loanNoticeList = LoanNotice.findAllByIdInList(ids)

            loanNoticeList.each { LoanNotice loanNotice ->
                if (loanNotice?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete loanNotice
                    loanNotice.trackingInfo.status = GeneralStatus.DELETED
                    loanNotice.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (loanNoticeList) {
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
 * @return LoanNotice.
 */
    @Transactional(readOnly = true)
    LoanNotice getInstance(GrailsParameterMap params) {
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
     * @return LoanNotice.
     */
    @Transactional(readOnly = true)
    LoanNotice getInstanceWithRemotingValues(GrailsParameterMap params) {
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

}