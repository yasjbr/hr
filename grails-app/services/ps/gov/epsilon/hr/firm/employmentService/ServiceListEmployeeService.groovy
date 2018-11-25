package ps.gov.epsilon.hr.firm.employmentService

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service aims to create list employee records and hold the details which will be sent in list-
 * <h1>Usage</h1>
 * -manage the list employee record.-
 * <h1>Restriction</h1>
 * --
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ServiceListEmployeeService {

    MessageSource messageSource
    def formatService
    EmployeeService employeeService
    ServiceListService serviceListService

    /**
     * this closure is used to return the employment service request id
     */
    public static getRequestEncodedId = { cService, ServiceListEmployee rec, object, params ->
        if (rec?.employmentServiceRequest) {
            return rec?.employmentServiceRequest?.encodedId
        } else {
            return ""
        }
    }


    //to return id if the record comes from request else return empty:
    public static getRequestId = { cService, ServiceListEmployee rec, object, params ->
        if(rec?.employmentServiceRequest){
            return rec?.employmentServiceRequest?.id
        }else {
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
            [sort: true, search: true, hidden: false, name: "requestId", type: getRequestId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dateEffective", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "serviceActionReason", type: "ServiceActionReason", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "requestEncodedId", type: getRequestEncodedId, source: 'domain'],
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
        String employeeId = params["employee.id"]
        String militaryRankId = params["militaryRank.id"]
        String employmentServiceRequestId = params["employmentServiceRequest.id"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String serviceActionReasonId = params["serviceActionReason.id"]
        String serviceListId = params["serviceList.id"]
        Set serviceListEmployeeNotesIds = params.listString("serviceListEmployeeNotes.id")
        ZonedDateTime dateEffective = PCPUtils.parseZonedDateTime(params['dateEffective'])
        ZonedDateTime fromDateEffective = PCPUtils.parseZonedDateTime(params['dateEffectiveFrom'])
        ZonedDateTime toDateEffective = PCPUtils.parseZonedDateTime(params['dateEffectiveTo'])
        GeneralStatus generalStatus = params["generalStatus"] ? GeneralStatus.valueOf(params["generalStatus"]) : null

        // solution of sorting by id problem after id become string
        if(columnName.equals("id")){
            columnName = "trackingInfo.dateCreatedUTC"
        }/*else if (columnName.equals("employmentServiceRequest.id")){
            columnName = "employmentServiceRequest.trackingInfo.dateCreatedUTC"
        }*/

        return ServiceListEmployee.createCriteria().list(max: max, offset: offset) {
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
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (dateEffective) {
                    eq("dateEffective", dateEffective)
                }
                if (fromDateEffective) {
                    ge("dateEffective", fromDateEffective)
                }
                if (toDateEffective) {
                    le("dateEffective", toDateEffective)
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
                if (employmentServiceRequestId) {
                    eq("employmentServiceRequest.id", employmentServiceRequestId)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (serviceActionReasonId) {
                    eq("serviceActionReason.id", serviceActionReasonId)
                }
                if (serviceListId) {
                    eq("serviceList.id", serviceListId)
                }
                if (serviceListEmployeeNotesIds) {
                    serviceListEmployeeNotes {
                        inList("id", serviceListEmployeeNotesIds)
                    }
                }
                if (generalStatus) {
                    eq("trackingInfo.status", generalStatus)
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                // solution of sorting by id problem after id become string
                switch (columnName) {
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
                    case 'requestId':
                        employmentServiceRequest{
                            order("trackingInfo.dateCreatedUTC", dir)
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
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id
        //get employee remote details
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)
        pagedResultList?.resultList.each { ServiceListEmployee serviceListEmployee ->
            serviceListEmployee?.employee = employeeList?.find { it?.id == serviceListEmployee?.employee?.id }
        }
        return pagedResultList
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return ServiceListEmployee.
     */
    ServiceListEmployee save(GrailsParameterMap params) {
        ServiceListEmployee serviceListEmployeeInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            serviceListEmployeeInstance = ServiceListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (serviceListEmployeeInstance.version > version) {
                    serviceListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('serviceListEmployee.label', null, 'serviceListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this serviceListEmployee while you were editing")
                    return serviceListEmployeeInstance
                }
            }
            if (!serviceListEmployeeInstance) {
                serviceListEmployeeInstance = new ServiceListEmployee()
                serviceListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('serviceListEmployee.label', null, 'serviceListEmployee', LocaleContextHolder.getLocale())] as Object[], "This serviceListEmployee with ${params.id} not found")
                return serviceListEmployeeInstance
            }
        } else {
            serviceListEmployeeInstance = new ServiceListEmployee()
        }
        try {
            serviceListEmployeeInstance.properties = params;
            serviceListEmployeeInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            serviceListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return serviceListEmployeeInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ServiceListEmployee> serviceListEmployees = null
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                //get list of  service list employee by list of ids
                serviceListEmployees = ServiceListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                //get list of  service list employee by list of ids
                serviceListEmployees = ServiceListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            //get list of service request & revert status to APPROVED_BY_WORKFLOW
            List<Request> requests = serviceListEmployees?.employmentServiceRequest
            requests?.removeAll(Collections.singleton(null));
            requests?.each { Request request ->
                if (request) {
                    request.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                }
            }
            //delete list of service list employee
            serviceListEmployees*.delete()
            deleteBean.status = true
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
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
        String id = params["serviceList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        ServiceList serviceList = serviceListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = serviceList?.code
        map.coverLetter = serviceList?.coverLetter
        map.details = resultList
        return [map]
    }
}