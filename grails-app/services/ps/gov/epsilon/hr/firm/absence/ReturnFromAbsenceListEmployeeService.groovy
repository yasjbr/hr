package ps.gov.epsilon.hr.firm.absence

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -employee record in returnFromAbsence list-
 * <h1>Usage</h1>
 * -to hold the record of the employee in the list-
 * <h1>Restriction</h1>
 * -no direct create/edit-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ReturnFromAbsenceListEmployeeService {

    MessageSource messageSource
    def formatService
    ReturnFromAbsenceRequestService returnFromAbsenceRequestService
    ReturnFromAbsenceListService returnFromAbsenceListService

    /**
     * this closure is used to return the maritalStatusRequest id
     */
    public static requestEncodedId = { cService, ReturnFromAbsenceListEmployee rec, object, params ->
        if (rec?.returnFromAbsenceRequest) {
            return rec?.returnFromAbsenceRequest?.encodedId
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
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestEncodedId", type: requestEncodedId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "returnFromAbsenceRequest.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "returnFromAbsenceRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualAbsenceReason", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "actualReturnDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],

    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "returnFromAbsenceRequest.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "returnFromAbsenceRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "actualAbsenceReason", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "actualReturnDate", type: "ZonedDate", source: 'domain'],
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
        ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason actualAbsenceReason = params["actualAbsenceReason"] ? ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.valueOf(params["actualAbsenceReason"]) : null
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String returnFromAbsenceListId = params["returnFromAbsenceList.id"]
        Set returnFromAbsenceListEmployeeNotesIds = params.listString("returnFromAbsenceListEmployeeNotes.id")
        String returnFromAbsenceRequestId = params["returnFromAbsenceRequest.id"]


        //search about employee or his military rank :
        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        String status = params["status"]
        ZonedDateTime actualReturnDate = PCPUtils.parseZonedDateTime(params['actualReturnDate'])
        ZonedDateTime fromActualReturnDate = PCPUtils.parseZonedDateTime(params['actualReturnDateFrom'])
        ZonedDateTime toActualReturnDate = PCPUtils.parseZonedDateTime(params['actualReturnDateTo'])



        return ReturnFromAbsenceListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (actualAbsenceReason) {
                    eq("actualAbsenceReason", actualAbsenceReason)
                }
                if (actualReturnDate) {
                    eq("actualReturnDate", actualReturnDate)
                }
                if (fromActualReturnDate) {
                    ge("actualReturnDate", fromActualReturnDate)
                }
                if (toActualReturnDate) {
                    le("actualReturnDate", toActualReturnDate)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (returnFromAbsenceListId) {
                    eq("returnFromAbsenceList.id", returnFromAbsenceListId)
                }
                if (returnFromAbsenceListEmployeeNotesIds) {
                    returnFromAbsenceListEmployeeNotes {
                        inList("id", returnFromAbsenceListEmployeeNotesIds)
                    }
                }
                if (returnFromAbsenceRequestId) {
                    eq("returnFromAbsenceRequest.id", returnFromAbsenceRequestId)
                }
                if (employeeId || militaryRankId) {
                    returnFromAbsenceRequest {
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
        pagedResultList?.resultList?.each { ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee ->
            GrailsParameterMap requestParam = new GrailsParameterMap([id: returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            returnFromAbsenceListEmployee?.returnFromAbsenceRequest = returnFromAbsenceRequestService.getInstanceWithRemotingValues(requestParam)
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ReturnFromAbsenceListEmployee.
     */
    ReturnFromAbsenceListEmployee save(GrailsParameterMap params) {
        ReturnFromAbsenceListEmployee returnFromAbsenceListEmployeeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            returnFromAbsenceListEmployeeInstance = ReturnFromAbsenceListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (returnFromAbsenceListEmployeeInstance.version > version) {
                    returnFromAbsenceListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('returnFromAbsenceListEmployee.label', null, 'returnFromAbsenceListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this returnFromAbsenceListEmployee while you were editing")
                    return returnFromAbsenceListEmployeeInstance
                }
            }
            if (!returnFromAbsenceListEmployeeInstance) {
                returnFromAbsenceListEmployeeInstance = new ReturnFromAbsenceListEmployee()
                returnFromAbsenceListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('returnFromAbsenceListEmployee.label', null, 'returnFromAbsenceListEmployee', LocaleContextHolder.getLocale())] as Object[], "This returnFromAbsenceListEmployee with ${params.id} not found")
                return returnFromAbsenceListEmployeeInstance
            }
        } else {
            returnFromAbsenceListEmployeeInstance = new ReturnFromAbsenceListEmployee()
        }
        try {
            returnFromAbsenceListEmployeeInstance.properties = params;
            returnFromAbsenceListEmployeeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            returnFromAbsenceListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return returnFromAbsenceListEmployeeInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ReturnFromAbsenceListEmployee> returnFromAbsenceListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  returnFromAbsence list employee by list of ids
                 */
                returnFromAbsenceListEmployeeList = ReturnFromAbsenceListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  returnFromAbsence list employee by list of ids
                 */
                returnFromAbsenceListEmployeeList = ReturnFromAbsenceListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of returnFromAbsence request & revert status to APPROVED_BY_WORKFLOW
             */
            List<ReturnFromAbsenceRequest> returnFromAbsenceRequestList = returnFromAbsenceListEmployeeList?.returnFromAbsenceRequest
            returnFromAbsenceRequestList?.each { ReturnFromAbsenceRequest returnFromAbsenceRequest ->
                returnFromAbsenceRequest?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            }
            /**
             * delete list of returnFromAbsence list employee
             */
            if (returnFromAbsenceListEmployeeList) {
                returnFromAbsenceListEmployeeList*.delete()
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
     * @return ReturnFromAbsenceListEmployee.
     */
    @Transactional(readOnly = true)
    ReturnFromAbsenceListEmployee getInstance(GrailsParameterMap params) {
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
        String id = params["returnFromAbsenceList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        ReturnFromAbsenceList returnFromAbsenceList = returnFromAbsenceListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = returnFromAbsenceList?.code
        map.coverLetter = returnFromAbsenceList?.coverLetter
        map.details = resultList
        return [map]
    }

}