package ps.gov.epsilon.hr.firm.suspension

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create suspension list employee
 * <h1>Usage</h1>
 * -this service is aims to create suspension list employee
 * <h1>Restriction</h1>
 * -need a suspension request created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class SuspensionListEmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    SuspensionListService suspensionListService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "suspensionType", type: "enum", source: 'domain'],
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
        String employeeId = params["employee.id"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        Short periodInMonth = params.long("periodInMonth")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String suspensionListId = params["suspensionList.id"]
        Set suspensionListEmployeeNotesIds = params.listString("suspensionListEmployeeNotes.id")
        String suspensionRequestId = params["suspensionRequest.id"]
        ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType suspensionType = params["suspensionType"] ? ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.valueOf(params["suspensionType"]) : null
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        String idList = params.long("idList")
        String employeeIdList = params.long("employeeIdList")
        String militaryRankIdList = params["militaryRankIdList"]
        ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType suspensionTypeList = params["suspensionTypeList"] ? ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.valueOf(params["suspensionTypeList"]) : null
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateListFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateListTo'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateListFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateListTo'])
        Short periodInMonthList = params.long("periodInMonthList")

        return SuspensionListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {
                if (id || idList) {
                    eq("id", id ?: idList)
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
                if (employeeId || employeeIdList) {
                    eq("employee.id", employeeId ?: employeeIdList)
                }
                if (fromDate) {
                    le("fromDate", fromDate)
                }
                if (periodInMonth || periodInMonthList) {
                    eq("periodInMonth", periodInMonth ?: periodInMonthList)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (suspensionListId) {
                    eq("suspensionList.id", suspensionListId)
                }
                if (suspensionListEmployeeNotesIds) {
                    suspensionListEmployeeNotes {
                        inList("id", suspensionListEmployeeNotesIds)
                    }
                }
                if (suspensionRequestId) {
                    eq("suspensionRequest.id", suspensionRequestId)
                }
                if (suspensionType || suspensionTypeList) {
                    eq("suspensionType", suspensionType ?: suspensionTypeList)
                }
                if (toDate) {
                    le("toDate", toDate)
                }

                if (militaryRankIdList) {
                    currentEmployeeMilitaryRank {
                        militaryRank {
                            eq("id", militaryRankIdList)
                        }
                    }
                }

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
 * @return SuspensionListEmployee.
 */
    SuspensionListEmployee save(GrailsParameterMap params) {
        SuspensionListEmployee suspensionListEmployeeInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            suspensionListEmployeeInstance = SuspensionListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (suspensionListEmployeeInstance.version > version) {
                    suspensionListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('suspensionListEmployee.label', null, 'suspensionListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this suspensionListEmployee while you were editing")
                    return suspensionListEmployeeInstance
                }
            }
            if (!suspensionListEmployeeInstance) {
                suspensionListEmployeeInstance = new SuspensionListEmployee()
                suspensionListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('suspensionListEmployee.label', null, 'suspensionListEmployee', LocaleContextHolder.getLocale())] as Object[], "This suspensionListEmployee with ${params.id} not found")
                return suspensionListEmployeeInstance
            }
        } else {
            suspensionListEmployeeInstance = new SuspensionListEmployee()
        }
        try {
            suspensionListEmployeeInstance.properties = params;
            suspensionListEmployeeInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            suspensionListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return suspensionListEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {

            List<SuspensionListEmployee> suspensionListEmployeeList = null

            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  suspension list employee by list of ids
                 */
                suspensionListEmployeeList = SuspensionListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  suspension list employee by list of ids
                 */
                suspensionListEmployeeList = SuspensionListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of suspension request & revert status to APPROVED_BY_WORKFLOW
             */
            List<SuspensionRequest> suspensionRequestList = suspensionListEmployeeList?.suspensionRequest
            suspensionRequestList?.each { SuspensionRequest suspensionRequest ->
                suspensionRequest?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                if(suspensionRequest?.internalOrderDate == null){
                    suspensionRequest?.internalOrderDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                }
                if(suspensionRequest?.externalOrderDate == null){
                    suspensionRequest?.externalOrderDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                }
            }
            /**
             * delete list of suspension list employee
             */
            if (suspensionListEmployeeList) {
                suspensionListEmployeeList*.delete()
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
 * @return SuspensionListEmployee.
 */
    @Transactional(readOnly = true)
    SuspensionListEmployee getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return SuspensionListEmployee.
     */
    @Transactional(readOnly = true)
    SuspensionListEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList suspensionListEmployeeList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList


        if (suspensionListEmployeeList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: suspensionListEmployeeList?.resultList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * assign employeeName for each employee in list
             */
            suspensionListEmployeeList?.each { SuspensionListEmployee suspensionListEmployee ->
                suspensionListEmployee?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == suspensionListEmployee?.employee?.personId
                })
            }
        }
        return suspensionListEmployeeList
    }

    /**
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["suspensionList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        SuspensionList suspensionList = suspensionListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = suspensionList?.code
        map.coverLetter = suspensionList?.coverLetter
        map.details = resultList
        return [map]
    }


}