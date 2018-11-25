package ps.gov.epsilon.hr.firm.child

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create child employee for list
 * <h1>Usage</h1>
 * -this service is used to create child employee for list
 * <h1>Restriction</h1>
 * -need child request & list
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ChildListEmployeeService {

    MessageSource messageSource
    def formatService
    ChildRequestService childRequestService
    ChildListService childListService

    /**
     * this closure is used to return the maritalStatusRequest id
     */
    public static requestEncodedId = { cService, ChildListEmployee rec, object, params ->
        if (rec?.childRequest) {
            return rec?.childRequest?.encodedId
        } else {
            return ""
        }
    }

    /**
     * this closure is used to return the employee name + promotion
     */
    public static getEmployeeToString = { cService, ChildListEmployee rec, object, params ->
        if (rec?.childRequest?.employee) {
            return rec?.childRequest?.employee?.toString()
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
            [sort: true, search: false, hidden: false, name: "childList", type: "ChildList", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "childListEmployeeNotes", type: "Set", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "childRequest", type: "ChildRequest", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "statusReason", type: "String", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            //this column was added to save the original request encoded id
            [sort: true, search: true, hidden: true, name: "requestEncodedId", type: requestEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "childRequest.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeFullName", type: getEmployeeToString, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "childRequest.employee.financialNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "childRequest.transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "childRequest.requestDate", type: "ZonedDate", source: 'domain'],
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
        String childListId = params["childList.id"]
        Set childListEmployeeNotesIds = params.listString("childListEmployeeNotes.id")
        String childRequestId = params["childRequest.id"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String statusReason = params["statusReason"]
        //search for request status
        List<ps.gov.epsilon.hr.enums.v1.EnumRequestStatus> requestStatusList = params.list("requestStatus")

        //search about employee or his military rank :
        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        String financialNumber = params["financialNumber"]
        String status = params["status"]
        //search about related person
        Long relatedPersonId = params.long("relatedPersonId")
        //search on request date
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])

        return ChildListEmployee.createCriteria().list(max: max, offset: offset) {
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
                if (childListId) {
                    eq("childList.id", childListId)
                }
                if (childListEmployeeNotesIds) {
                    childListEmployeeNotes {
                        inList("id", childListEmployeeNotesIds)
                    }
                }
                if (childRequestId) {
                    eq("childRequest.id", childRequestId)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (statusReason) {
                    ilike("statusReason", "%${statusReason}%")
                }
                if (requestStatusList) {//check type in list
                    childRequest{
                        inList("requestStatus", requestStatusList)
                    }
                }
                if (employeeId || militaryRankId || financialNumber) {
                    childRequest {
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
                }
                if (requestDate || fromRequestDate || toRequestDate) {
                    childRequest {
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
                if (relatedPersonId) {
                    childRequest {
                        eq("relatedPersonId", relatedPersonId)
                    }
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
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
                    case 'childRequest.id':
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
        pagedResultList?.resultList?.each { ChildListEmployee childListEmployee ->
            GrailsParameterMap requestParam = new GrailsParameterMap([id: childListEmployee?.childRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            childListEmployee?.childRequest = childRequestService.getInstanceWithRemotingValues(requestParam)
        }
        return pagedResultList
    }

//    /**
//     * to save/update model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return ChildListEmployee.
//     */
//    ChildListEmployee save(GrailsParameterMap params) {
//        ChildListEmployee childListEmployeeInstance
//
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//
//
//        if (params.id) {
//            childListEmployeeInstance = ChildListEmployee.get(params["id"])
//            if (params.long("version")) {
//                long version = params.long("version")
//                if (childListEmployeeInstance.version > version) {
//                    childListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('childListEmployee.label', null, 'childListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this childListEmployee while you were editing")
//                    return childListEmployeeInstance
//                }
//            }
//            if (!childListEmployeeInstance) {
//                childListEmployeeInstance = new ChildListEmployee()
//                childListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('childListEmployee.label', null, 'childListEmployee', LocaleContextHolder.getLocale())] as Object[], "This childListEmployee with ${params.id} not found")
//                return childListEmployeeInstance
//            }
//        } else {
//            childListEmployeeInstance = new ChildListEmployee()
//        }
//        try {
//            childListEmployeeInstance.properties = params;
//            childListEmployeeInstance.save();
//        }
//        catch (Exception ex) {
//            transactionStatus.setRollbackOnly()
//            childListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
//        }
//        return childListEmployeeInstance
//    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ChildListEmployee> childListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  child list employee by list of ids
                 */
                childListEmployeeList = ChildListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  child list employee by list of ids
                 */
                childListEmployeeList = ChildListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of child request & revert status to APPROVED_BY_WORKFLOW
             */
            List<ChildRequest> childRequestList = childListEmployeeList?.childRequest
            childRequestList?.each { ChildRequest childRequest ->
                childRequest?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            }
            /**
             * delete list of child list employee
             */
            if (childListEmployeeList) {
                childListEmployeeList*.delete()
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
     * @return ChildListEmployee.
     */
    @Transactional(readOnly = true)
    ChildListEmployee getInstance(GrailsParameterMap params) {
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
//     * to get model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return MaritalStatusListEmployee.
//     */
//    @Transactional(readOnly = true)
//    ChildListEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//        //if id is not null then return values from search method
//        if (params.id) {
//            PagedResultList results = searchWithRemotingValues(params)
//            if (results) {
//                return results[0]
//            }
//        }
//        return null
//
//    }

//
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
        String id = params["childList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        ChildList childList = childListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = childList?.code
        map.coverLetter = childList?.coverLetter
        map.details = resultList
        return [map]
    }

}