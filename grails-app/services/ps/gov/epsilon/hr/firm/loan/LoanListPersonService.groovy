package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -This service aims to create new record for the employee loan request in the List-
 * <h1>Usage</h1>
 * -create new record for the employee loan request in the List-
 * <h1>Restriction</h1>
 * --
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class LoanListPersonService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService
    LoanListService loanListService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "loanRequest.encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanRequest.id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanRequest.requestedJob", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "loanRequest.transientData.requestedFromOrganizationDTO", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanRequest.numberOfPositions", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanRequest.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanRequest.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
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
        Long firmId = params.long("firm.id")
        String loanListId = params["loanList.id"]

        Set loanListPersonNotesIds = params.listString("loanListPersonNotes.id")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime effectiveDateFrom = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime effectiveDateTo = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])

        //loanRequest search
        String loanRequestId = params["loanRequest.id"]
        Long requestedFromOrganizationId = params.long("loanRequest.requestedFromOrganizationId")
        String requestedJobId = params["loanRequest.requestedJob.id"]

        EnumRequestStatus loanRequestRequestStatus = params["loanRequest.requestStatus"] ? EnumRequestStatus.valueOf(params["loanRequest.requestStatus"]) : null
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['loanRequest.fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['loanRequest.toDate'])
        ZonedDateTime fromDateFrom = PCPUtils.parseZonedDateTime(params['loanRequest.fromDateFrom'])
        ZonedDateTime fromDateTo = PCPUtils.parseZonedDateTime(params['loanRequest.fromDateTo'])
        ZonedDateTime toDateFrom = PCPUtils.parseZonedDateTime(params['loanRequest.toDateFrom'])
        ZonedDateTime toDateTo = PCPUtils.parseZonedDateTime(params['loanRequest.toDateTo'])
        Short numberOfPositions = params.long("loanRequest.numberOfPositions")


        return LoanListPerson.createCriteria().list(max: max, offset: offset) {
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
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (loanListPersonNotesIds) {
                    loanListPersonNotes {
                        inList("id", loanListPersonNotesIds)
                    }
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
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (loanListId) {
                    eq("loanList.id", loanListId)
                }


                //todo set this condition with role_admin only, all other users just view the active records
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }

                //loan request search
                loanRequest{

                    if (loanRequestId) {
                        eq("id", loanRequestId)
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

                    if (numberOfPositions) {
                        eq("numberOfPositions", numberOfPositions)
                    }

                    if (requestedFromOrganizationId) {
                        eq("requestedFromOrganizationId", requestedFromOrganizationId)
                    }
                    if (requestedJobId) {
                        eq("requestedJob.id", requestedJobId)
                    }

                    if (loanRequestRequestStatus) {
                        eq("requestStatus", loanRequestRequestStatus)
                    }

                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }


            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                if(columnName.contains("loanRequest")){
                    // solution of sorting by id problem after id become string
                    switch (columnName) {
                        case 'loanRequest.id':
                            loanRequest{
                                order("trackingInfo.dateCreatedUTC", dir)
                            }
                            break;
                        default:
                            loanRequest{
                                order(columnName.replace("loanRequest.",""), dir)
                            }
                    }
                }else{
                    // solution of sorting by id problem after id become string
                    switch (columnName) {
                        case 'id':
                            order("trackingInfo.dateCreatedUTC", dir)
                            break;
                        default:
                            order(columnName, dir)
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
        if(pagedResultList.resultList) {
            SearchBean searchBean = new SearchBean()
            //collect organizationIds
            List organizationIds = pagedResultList?.loanRequest?.requestedFromOrganizationId?.toList()

            //send ids in search bean
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
            //fill all organization info
            List<OrganizationDTO> organizationList = organizationService.searchOrganization(searchBean)?.resultList

            //loop to fill all remoting values
            pagedResultList.each {LoanListPerson loanListPerson ->

                //fill all organization info
                if(loanListPerson?.loanRequest?.requestedFromOrganizationId){
                    loanListPerson.loanRequest.transientData.requestedFromOrganizationDTO = organizationList.find{it.id == loanListPerson?.loanRequest?.requestedFromOrganizationId}
                }
            }
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

            List<LoanListPerson> loanListPersonList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            /**
             * get list of  loan list person by list of ids
             */
            loanListPersonList = LoanListPerson.findAllByIdInList(ids)

            /**
             * get list of loan request & revert status to APPROVED_BY_WORKFLOW
             */
            loanListPersonList.loanRequest.each {LoanRequest loanRequest->
                loanRequest.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                loanRequest.save(flush:true)
            }

            /**
             * delete list of loan list person
             */
            if(loanListPersonList) {
                loanListPersonList*.delete()
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return LoanListPerson.
     */
    @Transactional(readOnly = true)
    LoanListPerson getInstance(GrailsParameterMap params) {
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
     * @return LoanListPerson.
     */
    @Transactional(readOnly = true)
    LoanListPerson getInstanceWithRemotingValues(GrailsParameterMap params) {
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

    /**
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["loanList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        LoanList loanList = loanListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = loanList?.code
        map.coverLetter = loanList?.coverLetter
        map.details = resultList
        return [map]
    }

}