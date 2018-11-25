package ps.gov.epsilon.hr.firm.suspension

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service is aims to create suspension extension list employee
 * <h1>Usage</h1>
 * -this service is used to create suspension extension list employee
 * <h1>Restriction</h1>
 * -need suspension extension request & list created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionExtensionListEmployeeService {

    MessageSource messageSource
    def formatService
    GovernorateService governorateService
    PersonService personService
    SuspensionExtensionListService suspensionExtensionListService

    //to get the value of requisition status
    public
    static suspensionExtensionRequestEncodedId = { cService, SuspensionExtensionListEmployee rec, object, params ->
        return HashHelper.decode(rec?.suspensionExtensionRequest?.id)
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "suspensionExtensionRequest.encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "suspensionExtensionRequest.id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "suspensionExtensionRequest.employee", type: "Employee", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "suspensionExtensionRequest.suspensionRequest.suspensionType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonth", type: "Short", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "suspensionExtensionRequest.encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "suspensionExtensionRequest.id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "suspensionExtensionRequest.employee", type: "Employee", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "suspensionExtensionRequest.suspensionRequest.suspensionType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonth", type: "Short", source: 'domain'],
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
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        Short periodInMonth = params.long("periodInMonth")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String suspensionExtensionListId = params["suspensionExtensionList.id"]
        Set suspensionExtensionListEmployeeNotesIds = params.listLong("suspensionExtensionListEmployeeNotes.id")
        String suspensionExtensionRequestId = params["suspensionExtensionRequest.id"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType suspensionType = params["suspensionType"] ? ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.valueOf(params["suspensionType"]) : null
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        return SuspensionExtensionListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {

                //fromDate
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                if (toFromDate) {
                    lte("fromDate", toFromDate)
                }
                //toDate
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    lte("toDate", toToDate)
                }

                suspensionExtensionRequest {
                    suspensionRequest {

                        if (employeeId) {
                            eq('employee.id', employeeId)
                        }

                        if(suspensionType){
                            eq("suspensionType",suspensionType)
                        }

                    }


                }
                if (militaryRankId) {
                    currentEmployeeMilitaryRank {
                        militaryRank {
                            eq("id", militaryRankId)
                        }
                    }
                }




                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (effectiveDate) {
                    le("effectiveDate", effectiveDate)
                }
                if (fromDate) {
                    le("fromDate", fromDate)
                }
                if (periodInMonth) {
                    eq("periodInMonth", periodInMonth)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (suspensionExtensionListId) {
                    eq("suspensionExtensionList.id", suspensionExtensionListId)
                }
                if (suspensionExtensionListEmployeeNotesIds) {
                    suspensionExtensionListEmployeeNotes {
                        inList("id", suspensionExtensionListEmployeeNotesIds)
                    }
                }
                if (suspensionExtensionRequestId) {
                    eq("suspensionExtensionRequest.id", suspensionExtensionRequestId)
                }
                if (toDate) {
                    le("toDate", toDate)
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
 * @return SuspensionExtensionListEmployee.
 */
    SuspensionExtensionListEmployee save(GrailsParameterMap params) {
        SuspensionExtensionListEmployee suspensionExtensionListEmployeeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            suspensionExtensionListEmployeeInstance = SuspensionExtensionListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (suspensionExtensionListEmployeeInstance.version > version) {
                    suspensionExtensionListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionExtensionListEmployee.label', null, 'suspensionExtensionListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionExtensionListEmployee while you were editing")
                    return suspensionExtensionListEmployeeInstance
                }
            }
            if (!suspensionExtensionListEmployeeInstance) {
                suspensionExtensionListEmployeeInstance = new SuspensionExtensionListEmployee()
                suspensionExtensionListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionExtensionListEmployee.label', null, 'suspensionExtensionListEmployee', LocaleContextHolder.getLocale())] as Object[], "This suspensionExtensionListEmployee with ${params.id} not found")
                return suspensionExtensionListEmployeeInstance
            }
        } else {
            suspensionExtensionListEmployeeInstance = new SuspensionExtensionListEmployee()
        }
        try {
            suspensionExtensionListEmployeeInstance.properties = params;
            suspensionExtensionListEmployeeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            suspensionExtensionListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionExtensionListEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {

            List<SuspensionExtensionListEmployee> suspensionExtensionListEmployeeList = null

            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  suspension list employee by list of ids
                 */
                suspensionExtensionListEmployeeList = SuspensionExtensionListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  suspension list employee by list of ids
                 */
                suspensionExtensionListEmployeeList = SuspensionExtensionListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of suspension request & revert status to APPROVED_BY_WORKFLOW
             */
            List<SuspensionExtensionRequest> suspensionExtensionRequestsList = suspensionExtensionListEmployeeList?.suspensionExtensionRequest
            suspensionExtensionRequestsList?.each { SuspensionExtensionRequest suspensionExtensionRequest ->
                suspensionExtensionRequest?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                suspensionExtensionRequest.save(failOnError: true, flush: true)
            }

            /**
             * delete list of suspension list employee
             */
            if (suspensionExtensionListEmployeeList) {
                suspensionExtensionListEmployeeList*.delete()
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
 * @return SuspensionExtensionListEmployee.
 */
    @Transactional(readOnly = true)
    SuspensionExtensionListEmployee getInstance(GrailsParameterMap params) {
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
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList suspensionExtensionListEmployeeList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<GovernorateDTO> governorateDTOList

        if (suspensionExtensionListEmployeeList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: suspensionExtensionListEmployeeList?.resultList?.suspensionExtensionRequest?.suspensionRequest?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            //fill employee governorate information from core
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: suspensionExtensionListEmployeeList?.resultList?.suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(governorateSearchBean)?.resultList




            suspensionExtensionListEmployeeList?.each { SuspensionExtensionListEmployee suspensionExtensionListEmployee ->

                /**
                 * assign for personDTO  for employee
                 */
                suspensionExtensionListEmployee?.suspensionExtensionRequest?.suspensionRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == suspensionExtensionListEmployee?.suspensionExtensionRequest?.suspensionRequest?.employee?.personId
                })

                /**
                 * assign for governorateDTO  for employee
                 */
                suspensionExtensionListEmployee?.suspensionExtensionRequest?.suspensionRequest?.employee?.transientData?.put("governorateDTO", governorateDTOList.find {
                    it.id == suspensionExtensionListEmployee?.suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.department?.governorateId
                })
            }
        }
        return suspensionExtensionListEmployeeList
    }

    /**
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["suspensionExtensionList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        SuspensionExtensionList suspensionExtensionList = suspensionExtensionListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = suspensionExtensionList?.code
        map.coverLetter = suspensionExtensionList?.coverLetter
        map.details = resultList
        return [map]
    }

}