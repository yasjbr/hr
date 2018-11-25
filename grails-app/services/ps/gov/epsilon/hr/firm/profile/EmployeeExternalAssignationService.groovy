package ps.gov.epsilon.hr.firm.profile

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

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
class EmployeeExternalAssignationService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */

    public static canEdit ={ formatService, EmployeeExternalAssignation dataRow, object, params->
        if(dataRow){
            if(dataRow.employmentRecord.toDate == null){
                return true
            }
            else{
                return false
            }
        }
        return  false
    }

    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToOrganizationFromDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "assignedToOrganizationId", type: "Long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentRecord", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEdit", type: canEdit, source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentRecord", type: "EmploymentRecord", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.organizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "assignedToOrganizationFromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "canEdit", type: canEdit, source: 'domain'],
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
        String columnName
        if(column) {
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


        List<Map<String,String>> orderBy = params.list("orderBy")
        ZonedDateTime assignedToOrganizationFromDate = PCPUtils.parseZonedDateTime(params['assignedToOrganizationFromDate'])
        Long assignedToOrganizationId = params.long("assignedToOrganizationId")
        String employmentRecordId = params["employmentRecord.id"]
        String note = params["note"]

        return EmployeeExternalAssignation.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
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
                if(assignedToOrganizationFromDate){
                    le("assignedToOrganizationFromDate", assignedToOrganizationFromDate)
                }
                if(assignedToOrganizationId){
                    eq("assignedToOrganizationId", assignedToOrganizationId)
                }
                if(employmentRecordId){
                    eq("employmentRecord.id", employmentRecordId)
                }
                if(note){
                    ilike("note", "%${note}%")
                }
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
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)
        List organizationIds = pagedResultList.resultList.assignedToOrganizationId.toList()
        SearchBean searchBean = new SearchBean()
        searchBean.searchCriteria.put("ids[]",new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
        List<OrganizationDTO> organizations = organizationService.searchOrganization(searchBean)?.resultList
        pagedResultList.resultList.each { EmployeeExternalAssignation assignation ->
            assignation.transientData.organizationDTO = organizations.find { it.id == assignation.assignedToOrganizationId }
        }
        return pagedResultList
    }


/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return EmployeeExternalAssignation.
 */
    EmployeeExternalAssignation save(GrailsParameterMap params) {

        EmployeeExternalAssignation employeeExternalAssignationInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            employeeExternalAssignationInstance = EmployeeExternalAssignation.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeExternalAssignationInstance.version > version) {
                    employeeExternalAssignationInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('employeeExternalAssignation.label', null, 'employeeExternalAssignation',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeExternalAssignation while you were editing")
                    return employeeExternalAssignationInstance
                }
            }
            if (!employeeExternalAssignationInstance) {
                employeeExternalAssignationInstance = new EmployeeExternalAssignation()
                employeeExternalAssignationInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('employeeExternalAssignation.label', null, 'employeeExternalAssignation',LocaleContextHolder.getLocale())] as Object[], "This employeeExternalAssignation with ${params.id} not found")
                return employeeExternalAssignationInstance
            }
        } else {
            employeeExternalAssignationInstance = new EmployeeExternalAssignation()
        }
        try {
            employeeExternalAssignationInstance.properties = params;

            if(!employeeExternalAssignationInstance?.id){
                employeeExternalAssignationInstance.employmentRecord = EmploymentRecord.findByToDateAndEmployee(PCPUtils.getDEFAULT_ZONED_DATE_TIME(),Employee.load(params["employee.id"]))
            }

            employeeExternalAssignationInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            employeeExternalAssignationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeeExternalAssignationInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)){
                EmployeeExternalAssignation.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                EmployeeExternalAssignation.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return EmployeeExternalAssignation.
 */
    @Transactional(readOnly = true)
    EmployeeExternalAssignation getInstance(GrailsParameterMap params) {
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
     * @return EmployeeExternalAssignation.
     */
    @Transactional(readOnly = true)
    EmployeeExternalAssignation getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                EmployeeExternalAssignation assignation = results[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("id",new SearchConditionCriteriaBean(operand: 'id', value1: assignation?.assignedToOrganizationId))
                OrganizationDTO organization = organizationService.getOrganization(searchBean)
                assignation.transientData.organizationDTO = organization
                return assignation
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
        if(domainColumns){
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}