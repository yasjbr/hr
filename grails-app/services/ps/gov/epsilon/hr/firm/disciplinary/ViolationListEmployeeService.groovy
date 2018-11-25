package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * <h1>Purpose</h1>
 * -this service is aims to create employeeViolation employee list
 * <h1>Usage</h1>
 * -this service is used to create employeeViolation employee list
 * <h1>Restriction</h1>
 * -need employeeViolation request & list
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ViolationListEmployeeService {

    MessageSource messageSource
    def formatService
    EmployeeViolationService employeeViolationService
    ViolationListService violationListService

    /**
     * this closure is used to return the maritalStatusRequest id
     */
    public static employeeViolationEncodedId = { cService, ViolationListEmployee rec, object, params ->
        if (rec?.employeeViolation) {
            return rec?.employeeViolation?.encodedId
        } else {
            return ""
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "employeeViolationEncodedId", type: employeeViolationEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeViolation.id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeViolation.employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeViolation.disciplinaryReason", type: "DisciplinaryReason", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeViolation.violationDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeViolation.violationStatus", type: "Enum", source: 'domain'],

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
        String employeeViolationId = params["employeeViolation.id"]
        String violationListId = params["violationList.id"]
        Set violationListEmployeeNotesIds = params.listString("violationListEmployeeNotes.id")
        ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus violationStatus = params["violationStatus"] ? ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.valueOf(params["violationStatus"]) : null

        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        String employeeId = params["employee.id"]
        String informerId = params["informer.id"]
        String militaryRankId = params["militaryRank.id"]
        String status = params["status"]

        Long locationId = params.long("locationId")
        String note = params["note"]
        String unstructuredLocation = params["unstructuredLocation"]
        ZonedDateTime violationDate = PCPUtils.parseZonedDateTime(params['violationDate'])
        ZonedDateTime violationDateFrom = PCPUtils.parseZonedDateTimeWithSmallestTime(params['violationDateFrom'])
        ZonedDateTime violationDateTo = PCPUtils.parseZonedDateTimeWithBiggestTime(params['violationDateTo'])

        ZonedDateTime noticeDate = PCPUtils.parseZonedDateTime(params['noticeDate'])
        ZonedDateTime fromNoticeDate = PCPUtils.parseZonedDateTime(params['noticeDateFrom'])
        ZonedDateTime toNoticeDate = PCPUtils.parseZonedDateTime(params['noticeDateTo'])
        String disciplinaryReasonId = params["disciplinaryReason.id"]


        return ViolationListEmployee.createCriteria().list(max: max, offset: offset) {
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
                if (employeeViolationId) {
                    eq("employeeViolation.id", employeeViolationId)
                }
                if (violationListId) {
                    eq("violationList.id", violationListId)
                }
                if (violationListEmployeeNotesIds) {
                    violationListEmployeeNotes {
                        inList("id", violationListEmployeeNotesIds)
                    }
                }

                if (currentEmployeeMilitaryRankId) {
                    employeeViolation {
                        eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                    }
                }
                if (currentEmploymentRecordId) {
                    employeeViolation {
                        eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                    }
                }
                if (informerId) {
                    employeeViolation {
                        eq("informer.id", informerId)
                    }
                }
                if (disciplinaryReasonId) {
                    employeeViolation {
                        eq("disciplinaryReason.id", disciplinaryReasonId)
                    }
                }
                if (employeeId || militaryRankId) {
                    employeeViolation {
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
                if (violationStatus) {
                    employeeViolation {
                        eq("violationStatus", violationStatus)
                    }
                }
                //nextVerificationDate
                if(noticeDate){
                    employeeViolation {
                        eq("noticeDate", noticeDate)
                    }
                }
                if(fromNoticeDate){
                    employeeViolation {
                        ge("noticeDate", fromNoticeDate)
                    }
                }
                if(toNoticeDate){
                    employeeViolation {
                        le("noticeDate", toNoticeDate)
                    }
                }
                if (violationDate) {
                    employeeViolation {
                        eq("violationDate", violationDate)
                    }
                }
                if (violationDateFrom) {
                    employeeViolation {
                        ge("violationDate", violationDateFrom)
                    }
                }
                if (violationDateTo) {
                    employeeViolation {
                        le("violationDate", violationDateTo)
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
                 if(columnName.contains(".")){
                    switch (columnName){
                        case "employeeViolation.employee":
                            employeeViolation {
                                employee{
                                    order("id", dir)
                                }
                            }
                            break;
                        case "employeeViolation.employeeViolationReason":
                            employeeViolation {
                                order("employeeViolationReason", dir)
                            }
                            break;
                        case "employeeViolation.id":
                            employeeViolation {
                                order("trackingInfo.dateCreatedUTC", dir)
                            }
                            break;
                        case "employeeViolation.fromDate":
                            employeeViolation {
                                order("fromDate", dir)
                            }
                            break;
                        case "employeeViolation.toDate":
                            employeeViolation {
                                order("toDate", dir)
                            }
                            break;
                        case "employeeViolation.noticeDate":
                            employeeViolation {
                                order("noticeDate", dir)
                            }
                            break;
                        default:
                            order(columnName, dir)
                            break;
                    }
                }else {
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
        List employeeViolationIds = pagedResultList?.resultList?.employeeViolation?.id

        //get employeeViolation remote details
        GrailsParameterMap employeeViolationsParams = new GrailsParameterMap(["ids[]": employeeViolationIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> violationList = employeeViolationService?.searchWithRemotingValues(employeeViolationsParams)

        pagedResultList?.resultList?.each { ViolationListEmployee violationListEmployee ->
            violationListEmployee?.employeeViolation = violationList?.find { it?.id == violationListEmployee?.employeeViolation?.id }
        }
        return pagedResultList
    }

//    /**
//     * to save/update model entry.
//     * @param GrailsParameterMap params the search map.
//     * @return ViolationListEmployee.
//     */
//    ViolationListEmployee save(GrailsParameterMap params) {
//        ViolationListEmployee violationListEmployeeInstance
//
//        if (params.encodedId) {
//            params.id = HashHelper.decode(params.encodedId)
//        }
//
//
//        if (params.id) {
//            violationListEmployeeInstance = ViolationListEmployee.get(params["id"])
//            if (params.long("version")) {
//                long version = params.long("version")
//                if (violationListEmployeeInstance.version > version) {
//                    violationListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('violationListEmployee.label', null, 'violationListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this violationListEmployee while you were editing")
//                    return violationListEmployeeInstance
//                }
//            }
//            if (!violationListEmployeeInstance) {
//                violationListEmployeeInstance = new ViolationListEmployee()
//                violationListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('violationListEmployee.label', null, 'violationListEmployee', LocaleContextHolder.getLocale())] as Object[], "This violationListEmployee with ${params.id} not found")
//                return violationListEmployeeInstance
//            }
//        } else {
//            violationListEmployeeInstance = new ViolationListEmployee()
//        }
//        try {
//            violationListEmployeeInstance.properties = params;
//            violationListEmployeeInstance.save();
//        }
//        catch (Exception ex) {
//            transactionStatus.setRollbackOnly()
//            violationListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
//        }
//        return violationListEmployeeInstance
//    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ViolationListEmployee> violationListEmployeeList = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  violation list employee by list of ids
                 */
                violationListEmployeeList = ViolationListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  violation list employee by list of ids
                 */
                violationListEmployeeList = ViolationListEmployee.findAllByIdInList(deleteBean?.ids)
            }

            /**
             * get list of promotion request & revert status to APPROVED_BY_WORKFLOW
             */
            List<EmployeeViolation> employeeViolations = violationListEmployeeList?.employeeViolation
            employeeViolations?.removeAll(Collections.singleton(null));
            employeeViolations?.each { EmployeeViolation employeeViolation ->
                if (employeeViolation) {
                    employeeViolation.violationStatus = EnumViolationStatus.NEW
                }
            }

            /**
             * delete list of violation list employee
             */
            violationListEmployeeList*.delete()
            deleteBean.status = true
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
     * @return ViolationListEmployee.
     */
    @Transactional(readOnly = true)
    ViolationListEmployee getInstance(GrailsParameterMap params) {
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
        String domainColumns = params["domainColumns"]
        if(domainColumns){
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
        String id = params["violationList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        ViolationList violationList = violationListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = violationList?.code
        map.coverLetter = violationList?.coverLetter
        map.details = resultList
        return [map]
    }

}