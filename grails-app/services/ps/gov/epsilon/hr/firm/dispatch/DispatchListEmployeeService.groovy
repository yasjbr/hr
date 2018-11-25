package ps.gov.epsilon.hr.firm.dispatch

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationMajorDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service will be used to store the dispatchRequest in the list-
 * <h1>Usage</h1>
 * -use the domain columns to be shown in the list
 * <h1>Restriction</h1>
 * -no save, delete directly, actions will be done by list-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class DispatchListEmployeeService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService
    LocationService locationService
    EducationMajorService educationMajorService
    EmployeeService employeeService
    GovernorateService governorateService
    DispatchListService dispatchListService

    /**
     * to represent old employment record
     */
    public static currentEmploymentRecordFormat = { formatService, DispatchListEmployee dataRow, object, params ->
        if (dataRow?.currentEmploymentRecord) {
            return dataRow?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + dataRow?.currentEmploymentRecord?.department?.toString()
        }
        return ""
    }

    /**
     * return the dispatchRequest id to be used in the create new dispatch extension or stop dispatchRequest
     */
    public static getDispatchEducationMajor = { cService, DispatchListEmployee rec, object, params ->
        if (rec?.educationMajorId) {
            return rec?.transientData.educationMajorDTO.descriptionInfo.localName
        } else {
            return rec?.educationMajorName
        }
    }

    /**
     * return the dispatch request id to be used in the create new dispatch extension or stop request
     */
    public static getDispatchOrganization = { cService, DispatchListEmployee rec, object, params ->
        if (rec?.educationMajorId) {
            return rec?.transientData?.organizationDTO?.descriptionInfo?.localName
        } else {
            return rec?.organizationName
        }
    }

    /**
     * return the dispatch request id to be used in the create new dispatch extension or stop request
     */
    public static getRequestEncodedId = { cService, DispatchListEmployee rec, object, params ->
        if (rec?.dispatchRequest) {
            return rec?.dispatchRequest?.encodedId
        } else {
            return null
        }
    }


    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dispatchRequest.id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "requestEncodedId", type: getRequestEncodedId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dispatchRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dispatchRequest.requestTypeDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "currentEmploymentRecord", type: currentEmploymentRecordFormat, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonths", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "nextVerificationDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "organization", type: getDispatchOrganization, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "educationMajor", type: getDispatchEducationMajor, source: 'domain'],
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
        String dispatchListId = params["dispatchList.id"]
        ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType dispatchType = params["dispatchType"] ? ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType.valueOf(params["dispatchType"]) : null
        String employeeId = params["employee.id"]
        Long locationId = params.long("locationId")
        Long educationMajorId = params.long("educationMajorId")
        String note = params["note"]
        String orderNo = params["orderNo"]
        Long organizationId = params.long("organizationId")
        Short periodInMonths = params.long("periodInMonths")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])
        ZonedDateTime nextVerificationDate = PCPUtils.parseZonedDateTime(params['nextVerificationDate'])
        ZonedDateTime fromNextVerificationDate = PCPUtils.parseZonedDateTime(params['nextVerificationDateFrom'])
        ZonedDateTime toNextVerificationDate = PCPUtils.parseZonedDateTime(params['nextVerificationDateTo'])
        String unstructuredLocation = params["unstructuredLocation"]
        String status = params["status"]
        String militaryRankId = params["militaryRank.id"]

        //search on department and Governorate
        Long governorateIdList = params.long("governorateIdList")
        String departmentIdList = params["departmentIdList"]
        String sSearchNumber = params.int("sSearch")

        return DispatchListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    if (sSearch) {
                        or {
                            ilike("unstructuredLocation", sSearch)
                            if (sSearchNumber) {
                                eq("periodInMonths", sSearchNumber as short)
                                eq("id", sSearchNumber)
                            }
                        }
                    }
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

                if (currentEmploymentRecordId || governorateIdList || departmentIdList) {
                    currentEmploymentRecord {
                        if (currentEmploymentRecordId) {
                            eq("id", currentEmploymentRecordId)
                        }
                        if (governorateIdList || departmentIdList) {
                            department {
                                if (departmentIdList) {
                                    eq("id", departmentIdList)
                                }
                                if (governorateIdList) {
                                    eq("governorateId", governorateIdList)
                                }
                            }
                        }
                    }
                }

                if (dispatchListId) {
                    eq("dispatchList.id", dispatchListId)
                }
                if (dispatchType) {
                    eq("dispatchType", dispatchType)
                }
                if (locationId) {
                    eq("locationId", locationId)
                }
                if (educationMajorId) {
                    eq("educationMajorId", educationMajorId)
                }
                if (nextVerificationDate) {
                    le("nextVerificationDate", nextVerificationDate)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
                }
                if (organizationId) {
                    eq("organizationId", organizationId)
                }
                if (periodInMonths) {
                    eq("periodInMonths", periodInMonths)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                //nextVerificationDate
                if (nextVerificationDate) {
                    eq("nextVerificationDate", nextVerificationDate)
                }
                if (fromNextVerificationDate) {
                    ge("nextVerificationDate", fromNextVerificationDate)
                }
                if (toNextVerificationDate) {
                    le("nextVerificationDate", toNextVerificationDate)
                }
                //fromDate
                if (fromDate) {
                    eq("fromDate", fromDate)
                }
                if (fromFromDate) {
                    ge("fromDate", fromFromDate)
                }
                if (toFromDate) {
                    le("fromDate", toFromDate)
                }
                //toDate
                if (toDate) {
                    ge("toDate", toDate)
                }
                if (fromToDate) {
                    ge("toDate", fromToDate)
                }
                if (toToDate) {
                    le("toDate", toToDate)
                }
                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (employeeId || militaryRankId) {
                    dispatchRequest {
                        employee {
                            if (employeeId) {
                                eq("id", employeeId)
                            }
                            if (militaryRankId) {
                                currentEmployeeMilitaryRank {
                                    eq("militaryRank.id", militaryRankId)
                                }
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
                order(columnName, dir)
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
        //get the remote organization, location and education major values from the pcore:
        List<Long> organizationIds = pagedResultList?.resultList?.organizationId?.toList()
        List<Long> locationIds = pagedResultList?.resultList?.locationId?.toList()
        List educationMajorIds = pagedResultList?.resultList?.educationMajorId?.toList()
        List employeeIds = pagedResultList?.resultList?.dispatchRequest?.employee?.id
        List<Long> governorateIds = pagedResultList?.resultList?.currentEmploymentRecord?.department?.governorateId.toList()?.unique()

        SearchBean searchBean = new SearchBean()

        //get organization DTO
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
        List<OrganizationDTO> organizations = organizationService?.searchOrganization(searchBean)?.resultList

        //get location DTO
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: locationIds));
        List<LocationDTO> locations = locationService?.searchLocation(searchBean)?.resultList

        //get major DTO
        searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: educationMajorIds));
        List<EducationMajorDTO> educationMajors = educationMajorService?.searchEducationMajor(searchBean)?.resultList

        //get employee remote details
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)

        //get governorate info
        List<GovernorateDTO> governorates = governorateService.searchGovernorate(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: governorateIds)]))?.resultList

        pagedResultList?.resultList.each { DispatchListEmployee dispatchListEmployee ->
            dispatchListEmployee?.transientData?.organizationDTO = organizations.find {
                it?.id == dispatchListEmployee?.organizationId
            }
            dispatchListEmployee?.transientData?.locationDTO = locations.find {
                it?.id == dispatchListEmployee?.locationId
            }
            dispatchListEmployee?.transientData?.educationMajorDTO = educationMajors.find {
                it?.id == dispatchListEmployee?.educationMajorId
            }
            dispatchListEmployee?.dispatchRequest?.employee = employeeList?.find { it?.id == dispatchListEmployee?.dispatchRequest?.employee?.id }
            //set governorate info
            dispatchListEmployee.currentEmploymentRecord.department.transientData.governorateDTO = governorates.find {
                it.id == dispatchListEmployee?.currentEmploymentRecord?.department?.governorateId
            }
        }
        return pagedResultList
    }

//    /**
//     * to save/update model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return DispatchListEmployee.
//     */
//    DispatchListEmployee save(GrailsParameterMap params) {
//        DispatchListEmployee dispatchListEmployeeInstance
//
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//
//        if (params.id) {
//            dispatchListEmployeeInstance = DispatchListEmployee.get(params["id"])
//            if (params.long("version")) {
//                long version = params.long("version")
//                if (dispatchListEmployeeInstance.version > version) {
//                    dispatchListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('dispatchListEmployee.label', null, 'dispatchListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this dispatchListEmployee while you were editing")
//                    return dispatchListEmployeeInstance
//                }
//            }
//            if (!dispatchListEmployeeInstance) {
//                dispatchListEmployeeInstance = new DispatchListEmployee()
//                dispatchListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('dispatchListEmployee.label', null, 'dispatchListEmployee', LocaleContextHolder.getLocale())] as Object[], "This dispatchListEmployee with ${params.id} not found")
//                return dispatchListEmployeeInstance
//            }
//        } else {
//            dispatchListEmployeeInstance = new DispatchListEmployee()
//        }
//        try {
//            dispatchListEmployeeInstance.properties = params;
//            dispatchListEmployeeInstance.save();
//        }
//        catch (Exception ex) {
//            transactionStatus.setRollbackOnly()
//            dispatchListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
//        }
//        return dispatchListEmployeeInstance
//    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<DispatchListEmployee> dispatchListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                //get list of  dispatch list employee by list of ids
                dispatchListEmployeeList = DispatchListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                //get list of  dispatch list employee by list of ids
                dispatchListEmployeeList = DispatchListEmployee.findAllByIdInList(deleteBean?.ids)
            }

            /**
             * get list of promotion request & revert status to APPROVED_BY_WORKFLOW
             */
            List<DispatchRequest> requests = dispatchListEmployeeList?.dispatchRequest
            requests?.removeAll(Collections.singleton(null));
            requests?.each { DispatchRequest request ->
                if (request) {
                    request?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                    request?.validate()
                }
            }

            //delete list of dispatch list employee
            if (dispatchListEmployeeList) {
                dispatchListEmployeeList*.delete()
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

//    *
//     * to get model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return DispatchListEmployee.
//
    @Transactional(readOnly = true)
    DispatchListEmployee getInstance(GrailsParameterMap params) {
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

//    /**
//     * to auto complete model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return JSON.
//     */
//    @Transactional(readOnly = true)
//    JSON autoComplete(GrailsParameterMap params) {
//        List<Map> dataList = []
//        String idProperty = params["idProperty"] ?: "id"
//        String nameProperty = params["nameProperty"] ?: "id"
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
        String id = params["dispatchList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        DispatchList dispatchList = dispatchListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = dispatchList?.code
        map.coverLetter = dispatchList?.coverLetter
        map.details = resultList
        return [map]
    }

}