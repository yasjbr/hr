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
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
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
class JoinedEmployeeOperationalTasksService {

    MessageSource messageSource
    def formatService
    PersonService personService



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "operationalTask", type: "OperationalTask", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDateTime", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "operationalTask", type: "OperationalTask", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
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
        String employeeId = params["employee.id"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        String operationalTaskId = params["operationalTask.id"]
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        String status = params["status"]

        return JoinedEmployeeOperationalTasks.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                if(employeeId){
                    eq("employee.id", employeeId)
                }
                if(fromDate){
                    le("fromDate", fromDate)
                }
                if(operationalTaskId){
                    eq("operationalTask.id", operationalTaskId)
                }
                if(toDate){
                    le("toDate", toDate)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
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
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return JoinedEmployeeOperationalTasks.
 */
    JoinedEmployeeOperationalTasks save(GrailsParameterMap params) {
        JoinedEmployeeOperationalTasks joinedEmployeeOperationalTasksInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            joinedEmployeeOperationalTasksInstance = JoinedEmployeeOperationalTasks.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedEmployeeOperationalTasksInstance.version > version) {
                    joinedEmployeeOperationalTasksInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('joinedEmployeeOperationalTasks.label', null, 'joinedEmployeeOperationalTasks',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedEmployeeOperationalTasks while you were editing")
                    return joinedEmployeeOperationalTasksInstance
                }
            }
            if (!joinedEmployeeOperationalTasksInstance) {
                joinedEmployeeOperationalTasksInstance = new JoinedEmployeeOperationalTasks()
                joinedEmployeeOperationalTasksInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('joinedEmployeeOperationalTasks.label', null, 'joinedEmployeeOperationalTasks',LocaleContextHolder.getLocale())] as Object[], "This joinedEmployeeOperationalTasks with ${params.id} not found")
                return joinedEmployeeOperationalTasksInstance
            }
        } else {
            joinedEmployeeOperationalTasksInstance = new JoinedEmployeeOperationalTasks()
        }
        try {
            joinedEmployeeOperationalTasksInstance.properties = params;

            if(joinedEmployeeOperationalTasksInstance?.fromDate && joinedEmployeeOperationalTasksInstance?.toDate && joinedEmployeeOperationalTasksInstance?.toDate < joinedEmployeeOperationalTasksInstance?.fromDate){
                joinedEmployeeOperationalTasksInstance.validate()
                joinedEmployeeOperationalTasksInstance.errors.rejectValue("toDate","joinedEmployeeOperationalTasks.toDate.error")
                return joinedEmployeeOperationalTasksInstance
            }

            joinedEmployeeOperationalTasksInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedEmployeeOperationalTasksInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedEmployeeOperationalTasksInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            def id
            //if the id is encrypted
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                id = HashHelper.decode(deleteBean?.ids[0])
            } else {
                id = deleteBean?.ids[0]
            }

            JoinedEmployeeOperationalTasks instance = JoinedEmployeeOperationalTasks.get(id)
            //to apply virtual delete, we change tracking info's status to deleted
            if (instance && instance?.trackingInfo?.status != GeneralStatus.DELETED) {
                instance?.trackingInfo.status = GeneralStatus.DELETED
                instance.save()
                deleteBean.status = true
            } else {
                deleteBean.status = false
                deleteBean.responseMessage << messageSource.getMessage('firm.deleteMessage.label')
            }
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return JoinedEmployeeOperationalTasks.
 */
    @Transactional(readOnly = true)
    JoinedEmployeeOperationalTasks getInstance(GrailsParameterMap params) {
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
    JoinedEmployeeOperationalTasks getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                JoinedEmployeeOperationalTasks joinedEmployeeOperationalTasks = results[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: joinedEmployeeOperationalTasks?.employee?.personId))
                PersonDTO personDTO = personService.getPerson(searchBean)
                joinedEmployeeOperationalTasks.employee.transientData.put("personDTO", personDTO)
                return joinedEmployeeOperationalTasks
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
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}