package ps.gov.epsilon.hr.firm.profile

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmploymentCategory
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime
/**
 *<h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class EmploymentRecordService {

    MessageSource messageSource
    def formatService
    PersonService personService
    OrganizationService organizationService

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */

    public static canEdit ={ formatService, EmploymentRecord dataRow, object, params->
        if(dataRow){
            if(dataRow.toDate == null){
                return true
            }else{
                return false
            }
        }
        return  false
    }

    public static getEmployeeId ={ formatService, EmploymentRecord dataRow, object, params->
        if(dataRow){
            return dataRow?.employee?.id?.toString()
        }
        return  ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "department", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeExternalAssignations", type: "Set", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeInternalAssignations", type: "Set", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentCategory", type: "EmploymentCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "jobDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "jobTitle", type: "JobTitle", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "canEdit", type: canEdit, source: 'domain'],
    ]


    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "department", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentCategory", type: "EmploymentCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "jobTitle", type: "JobTitle", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "jobDescription", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderNumber", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "canEdit", type: canEdit, source: 'domain'],
            [sort: true, search: false, hidden: true, name: "employeeId", type: getEmployeeId, source: 'domain'],
    ]

    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "department", type: "Department", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentCategory", type: "EmploymentCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "jobTitle", type: "JobTitle", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "jobDescription", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internalOrderNumber", type: "String", source: 'domain'],
    ]


    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params){
        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]

        //set domain columns
        String columnName
        List<String> domainColumnsSearch = DOMAIN_COLUMNS
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            domainColumnsSearch = this."${domainColumns}"
        }
        if(column) {
            columnName = domainColumnsSearch[column]?.name
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


        List<Map<String,String>> orderBy = params.list("orderBy")
        String departmentId = params["department.id"]
        String employeeId = params["employee.id"]
        Set employeeExternalAssignationsIds = params.listString("employeeExternalAssignations.id")
        Set employeeInternalAssignationsIds = params.listString("employeeInternalAssignations.id")
        String employmentCategoryId = params["employmentCategory.id"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        String jobDescription = params["jobDescription"]
        String jobTitleId = params["jobTitle.id"]
        String note = params["note"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        Boolean isActive = params.boolean("isActive")

        return EmploymentRecord.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                    ilike("jobDescription", sSearch)
                    ilike("note", sSearch)
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                if(departmentId){
                    eq("department.id", departmentId)
                }
                if(employeeId){
                    eq("employee.id", employeeId)
                }
                if(isActive == true){
                    eq("toDate", PCPUtils.getDEFAULT_ZONED_DATE_TIME())
                }
                if(employeeExternalAssignationsIds){
                    employeeExternalAssignations{
                        inList("id", employeeExternalAssignationsIds)
                    }
                }
                if(employeeInternalAssignationsIds){
                    employeeInternalAssignations{
                        inList("id", employeeInternalAssignationsIds)
                    }
                }
                if(employmentCategoryId){
                    eq("employmentCategory.id", employmentCategoryId)
                }
                if(fromDate){
                    le("fromDate", fromDate)
                }
                if(jobDescription){
                    ilike("jobDescription", "%${jobDescription}%")
                }
                if(jobTitleId){
                    eq("jobTitle.id", jobTitleId)
                }
                if(note){
                    ilike("note", "%${note}%")
                }
                if(toDate){
                    le("toDate", toDate)
                }

                eq("firm.id", PCPSessionUtils.getValue("firmId"))
            }
            if(orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            }else if(columnName){
                order(columnName, dir)
            }else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

        }
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return EmploymentRecord.
 */
    EmploymentRecord save(GrailsParameterMap params) {
        EmploymentRecord employmentRecordInstance
        EmploymentRecord previousEmploymentRecordInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            employmentRecordInstance = EmploymentRecord.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employmentRecordInstance.version > version) {
                    employmentRecordInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('employmentRecord.label', null, 'employmentRecord',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employmentRecord while you were editing")
                    return employmentRecordInstance
                }
            }
            if (!employmentRecordInstance) {
                employmentRecordInstance = new EmploymentRecord()
                employmentRecordInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('employmentRecord.label', null, 'employmentRecord',LocaleContextHolder.getLocale())] as Object[], "This employmentRecord with ${params.id} not found")
                return employmentRecordInstance
            }
        } else {
            employmentRecordInstance = new EmploymentRecord()
        }
        try {

            employmentRecordInstance.properties = params;

            previousEmploymentRecordInstance = employmentRecordInstance?.employee?.currentEmploymentRecord

            previousEmploymentRecordInstance?.validate()

            //set previous  employment record to date from new employment record from date
            if(!employmentRecordInstance?.id) {
                previousEmploymentRecordInstance?.toDate = employmentRecordInstance?.fromDate
            }

            EmploymentRecord currentEmploymentRecord = employmentRecordInstance?.employee?.currentEmploymentRecord
            EmployeeInternalAssignation previousAssignation = currentEmploymentRecord?.employeeInternalAssignations?.max{it.trackingInfo.dateCreatedUTC}

            //add assignationType
            if (params.boolean("transferAssignation")) {
                //get assignation from currentEmploymentRecord
                 if(currentEmploymentRecord?.employeeInternalAssignations?.size() > 0){
                    EmployeeInternalAssignation assignation = new EmployeeInternalAssignation()
                    assignation.assignedToDepartment = previousAssignation?.assignedToDepartment
                    assignation.assignedToDepartmentFromDate = previousAssignation?.assignedToDepartmentFromDate
                    assignation.assignedToDepartmentToDate = previousAssignation?.assignedToDepartmentToDate
                     if(!assignation.assignedToDepartmentToDate) {
                         assignation.assignedToDepartmentToDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                     }
                     employmentRecordInstance.addToEmployeeInternalAssignations(assignation)
                }
            }else if(params.long("assignedToDepartment.id") && params["assignedToDepartmentFromDate"]){
                EmployeeInternalAssignation assignation = new EmployeeInternalAssignation()
                assignation.assignedToDepartment = Department.get(params["assignedToDepartment.id"])
                assignation.assignedToDepartmentFromDate = PCPUtils.parseZonedDateTime(params["assignedToDepartmentFromDate"])
                assignation.assignedToDepartmentToDate = PCPUtils.parseZonedDateTime(params["assignedToDepartmentToDate"])
                if(!assignation.assignedToDepartmentToDate) {
                    assignation.assignedToDepartmentToDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                }
                employmentRecordInstance.addToEmployeeInternalAssignations(assignation)
            }

            //close all assignation for previousEmploymentRecord
            if(previousAssignation && !previousAssignation?.assignedToDepartmentToDate) {
                previousAssignation.assignedToDepartmentToDate = employmentRecordInstance?.fromDate
                previousAssignation.save(flush: true, failOnError: true)
            }

            if(employmentRecordInstance?.fromDate < previousEmploymentRecordInstance?.fromDate) {
                employmentRecordInstance.validate()
                employmentRecordInstance.errors.reject("employmentRecord.cannotInsert.label")
                return employmentRecordInstance
            }


            //reset employee to commited when previous status are student
            if(previousEmploymentRecordInstance?.employmentCategory?.id == EnumEmploymentCategory.STUDENT.value && employmentRecordInstance?.employmentCategory?.id != EnumEmploymentCategory.STUDENT.value){
                //add default categoryStatus ملتزم
                EmployeeStatusCategory employeeStatusCategory = EmployeeStatusCategory.get(EnumEmployeeStatusCategory.COMMITTED.value)

                //add default status when create employee على راس عمله
                EmployeeStatus employeeStatus = EmployeeStatus.load(EnumEmployeeStatus.WORKING.value)

                if (employeeStatusCategory && employeeStatus) {
                    //add default categoryStatus ملتزم
                    employmentRecordInstance.employee.categoryStatus = employeeStatusCategory

                    //close any open history status
                    employmentRecordInstance.employee.employeeStatusHistories.each {
                        if(!it.toDate || it.toDate == PCPUtils.DEFAULT_ZONED_DATE_TIME){
                            it.toDate = employmentRecordInstance?.fromDate
                        }
                    }

                    //add default status when create employee على راس عمله
                    EmployeeStatusHistory employeeStatusHistory = new EmployeeStatusHistory()
                    employeeStatusHistory.employeeStatus = employeeStatus
                    employeeStatusHistory.fromDate = employmentRecordInstance?.fromDate
                    employeeStatusHistory.toDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
                    employmentRecordInstance.employee.addToEmployeeStatusHistories(employeeStatusHistory)

                }
            }

            //validate employee to pass null zoned date time
            employmentRecordInstance.employee.validate()

            employmentRecordInstance.save(flush:true,failOnError:true);

            //set as current employment record
            if(employmentRecordInstance?.id){
                employmentRecordInstance.employee.currentEmploymentRecord = employmentRecordInstance
                employmentRecordInstance.employee.save()
            }
        }
        catch (Exception ex) {
//            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!employmentRecordInstance?.hasErrors()) {
                employmentRecordInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return employmentRecordInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List ids = []
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)){
                ids =  HashHelper.decodeList(deleteBean.ids)
            }else{
                ids = deleteBean.ids
            }

            EmploymentRecord.findAllByIdInList(ids)*.delete(flush: true)
            deleteBean.status = true
        }catch (Exception ex) {
//            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return EmploymentRecord.
 */
    @Transactional(readOnly = true)
    EmploymentRecord getInstance(GrailsParameterMap params) {
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
 * @return EmploymentRecord.
 */
    @Transactional(readOnly = true)
    EmploymentRecord getInstanceWithRemotingValues(GrailsParameterMap params) {
        Boolean isNewInstance = params.boolean("isNewInstance")

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id || (params["employee.id"] && !isNewInstance)) {
            PagedResultList results = search(params)
            if (results) {
                EmploymentRecord employmentRecord = results[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employmentRecord?.employee?.personId))
                PersonDTO personDTO = personService.getPerson(searchBean)
                employmentRecord.employee.transientData.put("personDTO", personDTO)

                if(employmentRecord?.employeeExternalAssignations){
                    EmployeeExternalAssignation externalAssignation = employmentRecord?.employeeExternalAssignations?.max{it.trackingInfo.dateCreatedUTC}
                    SearchBean organizationSearchBean = new SearchBean()
                    organizationSearchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: externalAssignation.assignedToOrganizationId))
                    OrganizationDTO assignedToOrganizationDTO = organizationService.getOrganization(organizationSearchBean)
                    externalAssignation.transientData.put("assignedToOrganizationDTO",assignedToOrganizationDTO)
                }


                return employmentRecord
            }
        }
        if(isNewInstance){
            EmploymentRecord employmentRecord = new EmploymentRecord()
            employmentRecord.previousEmploymentRecords = Employee.get(params["employee.id"])?.currentEmploymentRecord

            if(employmentRecord?.previousEmploymentRecords?.employeeExternalAssignations){
                EmployeeExternalAssignation externalAssignation = employmentRecord?.previousEmploymentRecords?.employeeExternalAssignations?.max{it.trackingInfo.dateCreatedUTC}
                SearchBean organizationSearchBean = new SearchBean()
                organizationSearchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: externalAssignation.assignedToOrganizationId))
                OrganizationDTO assignedToOrganizationDTO = organizationService.getOrganization(organizationSearchBean)
                externalAssignation.transientData.put("assignedToOrganizationDTO",assignedToOrganizationDTO)
            }

            return employmentRecord
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
        String idProperty = params["idProperty"]?:"id"
        String nameProperty = params["nameProperty"]?:"descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo")?:[]
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList,nameProperty,idProperty,autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
    }

/**
 * Convert paged result list to map depends on DOMAINS_COLUMNS.
 * @param def resultList may be PagedResultList or PagedList.
 * @param GrailsParameterMap params the search map
 * @param List<String> DOMAIN_COLUMNS the list of model column names.
 * @return Map.
 * @see PagedResultList.
 * @see PagedList.
 */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList,GrailsParameterMap params,List<String> DOMAIN_COLUMNS = null) {
        if(!DOMAIN_COLUMNS) {
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