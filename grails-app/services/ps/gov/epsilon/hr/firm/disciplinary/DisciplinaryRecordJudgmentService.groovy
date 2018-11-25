package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.lookups.CurrencyService
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.pcore.v2.entity.lookups.dtos.v1.CurrencyDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.UnitOfMeasurementDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -manage all disciplinary record judgment transactions and get data from domain
 * <h1>Usage</h1>
 * -any service to get disciplinary record judgment info or search about disciplinary
 * <h1>Restriction</h1>
 * -must connect with pcore application to get unit and currency information
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DisciplinaryRecordJudgmentService {

    MessageSource messageSource
    FormatService formatService
    UnitOfMeasurementService unitOfMeasurementService
    CurrencyService currencyService
    DisciplinaryRequestService disciplinaryRequestService
    EmployeeService employeeService
    DisciplinaryListService disciplinaryListService

    /**
     * to represent disciplinary reasons
     */
    public static getDisciplinaryReasons = { formatService, DisciplinaryRecordJudgment dataRow, object, params ->
        if (dataRow) {
            if (dataRow.disciplinaryReasons) {
                return dataRow?.disciplinaryReasons?.toList()?.descriptionInfo?.join(",")
            }
        }
        return ""
    }

    /**
     * to represent unit or currency
     */
    public static getUnitAndCurrency = { formatService, DisciplinaryRecordJudgment dataRow, object, params ->
        if (dataRow) {
            if (dataRow?.unitId) {
                return dataRow?.transientData?.unitDTO?.toString()
            } else if (dataRow?.currencyId) {
                return dataRow?.transientData?.currencyDTO?.toString()
            } else {
                return ""
            }
        }
        return ""
    }

    /**
     * to represent unit or currency
     */
    public static getStatus = { formatService, DisciplinaryRecordJudgment dataRow, object, params ->
        if (dataRow) {
            return dataRow?.judgmentStatus?.toString()
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryJudgment", type: "DisciplinaryJudgment", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryReasons", type: getDisciplinaryReasons, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryRequest.disciplinaryCategory", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "value", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "unitAndCurrency", type: getUnitAndCurrency, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "disciplinaryListNote.orderNo", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "disciplinaryListNote.note", type: "String", source: 'domain', wrapped: true],
    ]

    public static final List<String> DOMAIN_LIST_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "disciplinaryRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRequest.disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryJudgment", type: "DisciplinaryJudgment", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "judgmentStatus", type: "enum", source: 'domain', messagePrefix: 'EnumJudgmentStatus'],
            [sort: true, search: false, hidden: true, name: "status", type: getStatus, source: 'domain'],
    ]

    public static final List<String> DOMAIN_LIST_FOR_ADD_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "disciplinaryRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRequest.disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryJudgment", type: "DisciplinaryJudgment", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "value", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "unitAndCurrency", type: getUnitAndCurrency, source: 'domain'],
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
        String domainColumns = params["domainColumns"]
        List listOfColumns = DOMAIN_COLUMNS
        if (domainColumns) {
            listOfColumns = this."${domainColumns}"
        }
        if (column) {
            columnName = listOfColumns[column]?.name
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
        String disciplinaryJudgmentId = params["disciplinaryJudgment.id"]
        Set disciplinaryReasonsIds = params.listString("disciplinaryReasons.id")
        String disciplinaryRecordsListId = params["disciplinaryRecordsList.id"]
        ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus judgmentStatus = params["judgmentStatus"] ? ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus.valueOf(params["judgmentStatus"]) : null
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String note = params["note"]
        String orderNo = params["orderNo"]
        Long unitId = params.long("unitId")
        Long currencyId = params.long("currencyId")
        String value = params["value"]
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        //disciplinary request
        String disciplinaryRequestId = params["disciplinaryRequest.id"]
        String disciplinaryCategoryId = params["disciplinaryCategory.id"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime requestDateFrom = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime requestDateTo = PCPUtils.parseZonedDateTime(params['requestDateTo'])
        ps.gov.epsilon.hr.enums.v1.EnumRequestType requestType = params["requestType"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestType.valueOf(params["requestType"]) : null
        String employeeId = params["employee.id"]
        String departmentId = params["department.id"]
        String militaryRankId = params["militaryRank.id"]

        Boolean excludedFromEligiblePromotion = params.boolean("excludedFromEligiblePromotion")

        return DisciplinaryRecordJudgment.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    disciplinaryJudgment {
                        ilike("localName", sSearch)
                    }

                    disciplinaryRequest {
                        disciplinaryCategory {
                            ilike("localName", sSearch)
                        }
                    }
                    ilike("value", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (disciplinaryJudgmentId) {
                    eq("disciplinaryJudgment.id", disciplinaryJudgmentId)
                }
                if (disciplinaryReasonsIds) {
                    disciplinaryReasons {
                        inList("id", disciplinaryReasonsIds)
                    }
                }
                if (disciplinaryRecordsListId) {
                    eq("disciplinaryRecordsList.id", disciplinaryRecordsListId)
                }
                if (disciplinaryRequestId) {
                    eq("disciplinaryRequest.id", disciplinaryRequestId)
                }
                if (judgmentStatus) {
                    eq("judgmentStatus", judgmentStatus)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }

                if (unitId) {
                    eq("unitId", unitId)
                }
                if (currencyId) {
                    eq("currencyId", currencyId)
                }
                if (value) {
                    ilike("value", "%${value}%")
                }
                disciplinaryRequest {
                    if (disciplinaryRequestId) {
                        eq("id", disciplinaryRequestId)
                    }

                    if(requestStatus){
                        eq("requestStatus",requestStatus)
                    }

                    if (disciplinaryCategoryId) {
                        eq("disciplinaryCategory.id", disciplinaryCategoryId)
                    }
                    if (employeeId || militaryRankId || departmentId) {
                        employee {
                            if (employeeId) {
                                eq("id", employeeId)
                            }
                            if (departmentId) {
                                currentEmploymentRecord {
                                    eq("department.id", departmentId)
                                }
                            }
                            if (militaryRankId) {
                                currentEmployeeMilitaryRank {
                                    eq("militaryRank.id", militaryRankId)
                                }
                            }
                        }
                    }
                    //requestDate
                    if (requestDate) {
                        eq("requestDate", requestDate)
                    }
                    if (requestDateFrom) {
                        ge("requestDate", requestDateFrom)
                    }

                    if (requestDateTo) {
                        le("requestDate", requestDateTo)
                    }
                }
                if (fromDate) {
                    eq("fromDate", fromDate)
                }
                if (toDate) {
                    eq("toDate", toDate)
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (excludedFromEligiblePromotion) {
                    disciplinaryJudgment {
                        eq("excludedFromEligiblePromotion", excludedFromEligiblePromotion)
                    }
                }

                eq("firm.id", PCPSessionUtils.getValue("firmId"))

            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                if (columnName.contains("disciplinaryRequest")) {
                    // solution of sorting by id problem after id become string
                    switch (columnName) {
                        case 'disciplinaryRequest.id':
                            disciplinaryRequest {
                                order("trackingInfo.dateCreatedUTC", dir)
                            }
                            break;
                        default:
                            disciplinaryRequest {
                                order(columnName?.replace("disciplinaryRequest.", ""), dir)
                            }
                    }
                } else {
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
                disciplinaryJudgment {
                    order("localName", "desc")
                }
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
        List<Long> unitIds = pagedResultList?.resultList?.unitId?.findAll { it != null }?.unique()
        List<Long> currencyIds = pagedResultList?.resultList?.currencyId?.findAll { it != null }?.unique()
        List<String> employeeIds = pagedResultList?.resultList?.disciplinaryRequest?.employee?.id?.toList()

        GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)



        List<CurrencyDTO> currencyDTOList = []
        List<UnitOfMeasurementDTO> unitOfMeasurementDTOList = []

        if (currencyIds) {
            SearchBean currencySearchBean = new SearchBean()
            currencySearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: currencyIds))
            currencyDTOList = currencyService.searchCurrency(currencySearchBean)?.resultList
        }

        if (unitIds) {
            SearchBean unitSearchBean = new SearchBean()
            unitSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: unitIds))
            unitOfMeasurementDTOList = unitOfMeasurementService.searchUnitOfMeasurement(unitSearchBean)?.resultList
        }

        if (pagedResultList?.resultList) {
            pagedResultList?.resultList.each { DisciplinaryRecordJudgment judgment ->
                if (judgment?.currencyId) {
                    judgment.transientData.currencyDTO = currencyDTOList?.find { it.id == judgment?.currencyId }
                }
                if (judgment?.unitId) {
                    judgment.transientData.unitDTO = unitOfMeasurementDTOList?.find { it.id == judgment?.unitId }
                }

                //fill all employee info
                judgment.disciplinaryRequest.employee = employees.find {
                    it.id == judgment?.disciplinaryRequest?.employee?.id
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

            List<DisciplinaryRecordJudgment> disciplinaryRecordJudgmentList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }

            /**
             * get list of  data by ids
             */
            disciplinaryRecordJudgmentList = DisciplinaryRecordJudgment.findAllByIdInList(ids)

            //remove records from list
            disciplinaryRecordJudgmentList.each {
                it.disciplinaryRecordsList = null
                it.judgmentStatus = EnumJudgmentStatus.NEW
                it.save(flush: true)
            }

            if (disciplinaryRecordJudgmentList) {
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
     * @return DisciplinaryRecordJudgment.
     */
    @Transactional(readOnly = true)
    DisciplinaryRecordJudgment getInstance(GrailsParameterMap params) {
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
     * @return DisciplinaryRecordJudgment.
     */
    @Transactional(readOnly = true)
    DisciplinaryRecordJudgment getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                DisciplinaryRecordJudgment disciplinaryRecordJudgment = results[0]
                GrailsParameterMap disciplinaryRequestParams = new GrailsParameterMap([id: disciplinaryRecordJudgment?.disciplinaryRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                disciplinaryRecordJudgment.disciplinaryRequest = disciplinaryRequestService.getInstanceWithRemotingValues(disciplinaryRequestParams)
                return disciplinaryRecordJudgment
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
        String id = params["disciplinaryRecordsList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        DisciplinaryList disciplinaryList = disciplinaryListService.getInstance(parameterMap)
        // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = disciplinaryList?.code
        map.coverLetter = disciplinaryList?.coverLetter
        map.details = resultList
        return [map]
    }

}