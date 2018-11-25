package ps.gov.epsilon.hr.firm.disciplinary

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

import java.time.temporal.ChronoUnit

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
class EmployeeViolationService {

    MessageSource messageSource
    def formatService
    ManageLocationService manageLocationService
    EmployeeService employeeService
    LocationService locationService

    /**
     * to represent violation status
     */
    public static getStatus = { formatService, EmployeeViolation dataRow, object, params ->
        if (dataRow) {
            return dataRow?.violationStatus?.toString()
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryReason.disciplinaryCategories", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryReason.descriptionInfo", type: "disciplinaryReason", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "violationDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "violationStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "status", type: getStatus, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "firm.code", type: "Map", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "id", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryReason.disciplinaryCategories", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryReason.descriptionInfo", type: "disciplinaryReason", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "violationDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "violationStatus", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "status", type: getStatus, source: 'domain'],
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
        String disciplinaryCategoryId = params["disciplinaryCategoryId"]
        String disciplinaryReasonId = params["disciplinaryReason.id"]
        String employeeId = params["employee.id"]
        Long firmId = params.long("firm.id")
        String informerId = params["informer.id"]
        Set joinedDisciplinaryEmployeeViolationsIds = params.listString("joinedDisciplinaryEmployeeViolations.id")
        Long locationId = params.long("locationId")
        String note = params["note"]
        ZonedDateTime noticeDate = PCPUtils.parseZonedDateTime(params['noticeDate'])
        String unstructuredLocation = params["unstructuredLocation"]
        ZonedDateTime violationDate = PCPUtils.parseZonedDateTime(params['violationDate'])
        ZonedDateTime violationDateFrom = PCPUtils.parseZonedDateTimeWithSmallestTime(params['violationDateFrom'])
        ZonedDateTime violationDateTo = PCPUtils.parseZonedDateTimeWithBiggestTime(params['violationDateTo'])
        ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus violationStatus = params["violationStatus"] ? ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.valueOf(params["violationStatus"]) : null
        List excludedIdsList = params.listString("excludedIds")

        List<ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus> violationStatusList = params.list("violationStatusList")

        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null
        String militaryRankId = params["militaryRank.id"]

        return EmployeeViolation.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
                    ilike("unstructuredLocation", sSearch)
                }
            }
            and {
                if (excludedIdsList) {
                    not {
                        inList("id", excludedIdsList)
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
                if (disciplinaryReasonId || disciplinaryCategoryId) {
                    disciplinaryReason {
                        if (disciplinaryReasonId) {
                            eq("id", disciplinaryReasonId)
                        }
                        if (disciplinaryCategoryId) {
                            eq("disciplinaryCategories.id", disciplinaryCategoryId)
                        }
                    }
                }
                if (employeeId || militaryRankId) {
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
                if (firmId) {
                    eq("firm.id", firmId)
                } else {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }

                if (informerId) {
                    eq("informer.id", informerId)
                }
                if (joinedDisciplinaryEmployeeViolationsIds) {
                    joinedDisciplinaryEmployeeViolations {
                        inList("id", joinedDisciplinaryEmployeeViolationsIds)
                    }
                }
                if (locationId) {
                    eq("locationId", locationId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (noticeDate) {
                    le("noticeDate", noticeDate)
                }
                if (unstructuredLocation) {
                    ilike("unstructuredLocation", "%${unstructuredLocation}%")
                }
                if (violationDate) {
                    eq("violationDate", violationDate)
                }
                if (violationDateFrom) {
                    ge("violationDate", violationDateFrom)
                }
                if (violationDateTo) {
                    le("violationDate", violationDateTo)
                }
                if (violationStatus) {
                    eq("violationStatus", violationStatus)
                }
                if (violationStatusList) {
                    inList("violationStatus", violationStatusList)
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

                if (columnName.contains("disciplinaryReason")) {
                    disciplinaryReason {
                        order(columnName?.replace("disciplinaryReason.", ""), dir)
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
            List<String> employeeIds = pagedResultList?.resultList?.employee?.id?.toList()
            GrailsParameterMap parameterMap = new GrailsParameterMap(['ids[]': employeeIds, max: Integer.MAX_VALUE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            List<Employee> employees = employeeService.searchWithRemotingValues(parameterMap)
            //fill all employee info
            pagedResultList.resultList.each { EmployeeViolation employeeViolation ->
                employeeViolation.employee = employees.find { it.id == employeeViolation?.employee?.id }
            }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeViolation.
     */
    EmployeeViolation save(GrailsParameterMap params) {
        EmployeeViolation employeeViolationInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            employeeViolationInstance = EmployeeViolation.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeViolationInstance.version > version) {
                    employeeViolationInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeeViolation.label', null, 'employeeViolation', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeViolation while you were editing")
                    return employeeViolationInstance
                }
            }
            if (!employeeViolationInstance) {
                employeeViolationInstance = new EmployeeViolation()
                employeeViolationInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeViolation.label', null, 'employeeViolation', LocaleContextHolder.getLocale())] as Object[], "This employeeViolation with ${params.id} not found")
                return employeeViolationInstance
            }
        } else {
            employeeViolationInstance = new EmployeeViolation()
        }
        try {
            employeeViolationInstance.properties = params;

            //set current employee info
            Employee employee = employeeViolationInstance?.employee
            employeeViolationInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord;
            employeeViolationInstance?.currentEmployeeMilitaryRank = employee?.currentEmployeeMilitaryRank

            //save location
            if (params.long("governorateId")) {
                LocationCommand locationCommand
                locationCommand = manageLocationService.saveLocation(params)
                if (locationCommand?.id) {
                    //assign reference id of location from core
                    employeeViolationInstance.locationId = locationCommand?.id
                }
            }

            employeeViolationInstance.save(failOnError: true, flush: true);
        }
        catch (Exception ex) {

            transactionStatus.setRollbackOnly()
            if (employeeViolationInstance?.errors?.allErrors?.size() == 0) {
                employeeViolationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return employeeViolationInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<EmployeeViolation> employeeViolationList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }
            employeeViolationList = EmployeeViolation.findAllByIdInList(ids)
            employeeViolationList.each { EmployeeViolation employeeViolation ->
                if (employeeViolation?.trackingInfo?.status != GeneralStatus.DELETED) {
                    //delete employeeViolation
                    employeeViolation.trackingInfo.status = GeneralStatus.DELETED
                    employeeViolation.save(flush: true)
                }
            }
            //check that at least on record is set to deleted
            if (employeeViolationList) {
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
     * @return EmployeeViolation.
     */
    @Transactional(readOnly = true)
    EmployeeViolation getInstance(GrailsParameterMap params) {
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
     * to get model entry with remoting values.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeViolation.
     */
    EmployeeViolation getInstanceWithRemotingValues(GrailsParameterMap params) {
        EmployeeViolation employeeViolation = this.getInstance(params)
        if (EmployeeViolation) {
            GrailsParameterMap parameterMap = new GrailsParameterMap(['id': employeeViolation?.employee?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Employee employee = employeeService.getInstanceWithRemotingValues(parameterMap)
            //fill all employee info
            employeeViolation.employee = employee


            if (employeeViolation?.informer) {
                GrailsParameterMap informerParameterMap = new GrailsParameterMap(['id': employeeViolation?.informer?.id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                Employee informer = employeeService.getInstanceWithRemotingValues(informerParameterMap)
                //fill all informer employee info
                employeeViolation.informer = informer
            }

            SearchBean searchBean
            //fill all location info
            if (employeeViolation.locationId) {
                searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employeeViolation?.locationId))
                employeeViolation.transientData.locationDTO = locationService.getLocation(searchBean)
            }

        }
        return employeeViolation
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
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}