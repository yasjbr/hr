package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import java.time.ZonedDateTime
import grails.transaction.Transactional

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
class PetitionListEmployeeService {

    MessageSource messageSource
    def formatService
    PetitionRequestService petitionRequestService
    PetitionListService petitionListService

    /**
     * this closure is used to return the maritalStatusRequest id
     */
    public static requestEncodedId = { cService, PetitionListEmployee rec, object, params ->
        if (rec?.petitionRequest) {
            return rec?.petitionRequest?.encodedId
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
            [sort: true, search: false, hidden: false, name: "petitionRequest.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "petitionRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "petitionRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],

    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "requestEncodedId", type: requestEncodedId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "petitionRequest.id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "petitionRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "petitionRequest.requestDate", type: "ZonedDate", source: 'domain'],
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
        String petitionListId = params["petitionList.id"]
        Set petitionListEmployeeNotesIds = params.listString("petitionListEmployeeNotes.id")
        String petitionRequestId = params["petitionRequest.id"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null

        //search about employee or his military rank :
        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        String status = params["status"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromRequestDate = PCPUtils.parseZonedDateTime(params['requestDateFrom'])
        ZonedDateTime toRequestDate = PCPUtils.parseZonedDateTime(params['requestDateTo'])

        return PetitionListEmployee.createCriteria().list(max: max, offset: offset) {
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
                if (petitionListId) {
                    eq("petitionList.id", petitionListId)
                }
                if (petitionListEmployeeNotesIds) {
                    petitionListEmployeeNotes {
                        inList("id", petitionListEmployeeNotesIds)
                    }
                }
                if (petitionRequestId) {
                    eq("petitionRequest.id", petitionRequestId)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (employeeId || militaryRankId) {
                    petitionRequest {
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
                if (requestDate || fromRequestDate || toRequestDate) {
                    petitionRequest {
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
                    case 'petitionRequest.id':
                        petitionRequest{
                            order("id", dir)
                        }
                        break;
                    case 'petitionRequest.requestDate':
                        petitionRequest{
                            order("requestDate", dir)
                        }
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
        pagedResultList?.resultList?.each { PetitionListEmployee petitionListEmployee ->
            GrailsParameterMap requestParam = new GrailsParameterMap([id: petitionListEmployee?.petitionRequest?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            petitionListEmployee?.petitionRequest = petitionRequestService.getInstanceWithRemotingValues(requestParam)
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return PetitionListEmployee.
     */
//    PetitionListEmployee save(GrailsParameterMap params) {
//        PetitionListEmployee petitionListEmployeeInstance
//
//        /**
//         * in case: id is encoded
//         */
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//
//
//        if (params.id) {
//            petitionListEmployeeInstance = PetitionListEmployee.get(params["id"])
//            if (params.long("version")) {
//                long version = params.long("version")
//                if (petitionListEmployeeInstance.version > version) {
//                    petitionListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('petitionListEmployee.label', null, 'petitionListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this petitionListEmployee while you were editing")
//                    return petitionListEmployeeInstance
//                }
//            }
//            if (!petitionListEmployeeInstance) {
//                petitionListEmployeeInstance = new PetitionListEmployee()
//                petitionListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('petitionListEmployee.label', null, 'petitionListEmployee', LocaleContextHolder.getLocale())] as Object[], "This petitionListEmployee with ${params.id} not found")
//                return petitionListEmployeeInstance
//            }
//        } else {
//            petitionListEmployeeInstance = new PetitionListEmployee()
//        }
//        try {
//            petitionListEmployeeInstance.properties = params;
//            petitionListEmployeeInstance.save(failOnError: true);
//        }
//        catch (Exception ex) {
//            transactionStatus.setRollbackOnly()
//            petitionListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
//        }
//        return petitionListEmployeeInstance
//    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<PetitionListEmployee> petitionListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  petition list employee by list of ids
                 */
                petitionListEmployeeList = PetitionListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  petition list employee by list of ids
                 */
                petitionListEmployeeList = PetitionListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of petition request & revert status to APPROVED_BY_WORKFLOW
             */
            List<PetitionRequest> petitionRequestList = petitionListEmployeeList?.petitionRequest
            petitionRequestList?.each { PetitionRequest petitionRequest ->
                petitionRequest?.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
            }
            /**
             * delete list of petition list employee
             */
            if (petitionListEmployeeList) {
                petitionListEmployeeList*.delete()
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
     * @return PetitionListEmployee.
     */
//    @Transactional(readOnly = true)
//    PetitionListEmployee getInstance(GrailsParameterMap params) {
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//        //if id is not null then return values from search method
//        if (params.id) {
//            PagedResultList results = search(params)
//            if (results) {
//                return results[0]
//            }
//        }
//        return null
//
//    }

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
        String id = params["petitionList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        PetitionList petitionList = petitionListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = petitionList?.code
        map.coverLetter = petitionList?.coverLetter
        map.details = resultList
        return [map]
    }

}