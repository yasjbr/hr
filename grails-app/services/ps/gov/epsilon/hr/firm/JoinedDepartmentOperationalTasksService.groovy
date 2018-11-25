package ps.gov.epsilon.hr.firm

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * this service is aims to create operational task for department
 * <h1>Usage</h1>
 * -used for department's operational task
 * <h1>Restriction</h1>
 * - needs  department created before
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class JoinedDepartmentOperationalTasksService {

    MessageSource messageSource
    def formatService


    public static getEncodedId = { cService, JoinedDepartmentOperationalTasks rec, object, params ->
        if(rec?.encodedId) {
            return rec?.encodedId
        }else {
            return rec?.id
        }
    }

    public static operationalTaskLocalName = { cService, JoinedDepartmentOperationalTasks  joinedDepartmentOperationalTasks, object, params ->
        if(joinedDepartmentOperationalTasks.operationalTask) {
            return joinedDepartmentOperationalTasks.operationalTask.descriptionInfo.localName
        }else {
            return ""
        }
    }



    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: getEncodedId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "operationalTask", type: operationalTaskLocalName, source: 'domain']
    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: getEncodedId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "operationalTask", type: operationalTaskLocalName, source: 'domain']
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params, Boolean isEncrypted = false){
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
        List<Long> ids = params.list('ids[]')
        String id
        if(isEncrypted && params.id) {
            id = HashHelper.decode(params.id as String)?:-1L
        }else {
            id = params.id
        }


        List<Map<String,String>> orderBy = params.list("orderBy")
        String departmentId = params['department.id']
        String operationalTaskId = params['operationalTask.id']

        return JoinedDepartmentOperationalTasks.createCriteria().list(max: max, offset: offset){
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
                if(departmentId){
                    eq("department.id", departmentId)
                }
                if(operationalTaskId){
                    eq("operationalTask.id", operationalTaskId)
                }
            }
            if(orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            }else if(columnName){
                order(columnName, dir)
            }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return JoinedDepartmentOperationalTasks.
     */
    JoinedDepartmentOperationalTasks save(GrailsParameterMap params) {
        JoinedDepartmentOperationalTasks joinedDepartmentOperationalTasksInstance
        if (params.id) {
            joinedDepartmentOperationalTasksInstance = JoinedDepartmentOperationalTasks.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedDepartmentOperationalTasksInstance.version > version) {
                    joinedDepartmentOperationalTasksInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('joinedDepartmentOperationalTasks.label', null, 'joinedDepartmentOperationalTasks',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedDepartmentOperationalTasks while you were editing")
                    return joinedDepartmentOperationalTasksInstance
                }
            }
            if (!joinedDepartmentOperationalTasksInstance) {
                joinedDepartmentOperationalTasksInstance = new JoinedDepartmentOperationalTasks()
                joinedDepartmentOperationalTasksInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('joinedDepartmentOperationalTasks.label', null, 'joinedDepartmentOperationalTasks',LocaleContextHolder.getLocale())] as Object[], "This joinedDepartmentOperationalTasks with ${params.id} not found")
                return joinedDepartmentOperationalTasksInstance
            }
        } else {
            joinedDepartmentOperationalTasksInstance = new JoinedDepartmentOperationalTasks()
        }
        try {
            joinedDepartmentOperationalTasksInstance.properties = params;
            joinedDepartmentOperationalTasksInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedDepartmentOperationalTasksInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedDepartmentOperationalTasksInstance
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
                JoinedDepartmentOperationalTasks.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                JoinedDepartmentOperationalTasks.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
                deleteBean.status = true
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
     * @return JoinedDepartmentOperationalTasks.
     */
    @Transactional(readOnly = true)
    JoinedDepartmentOperationalTasks getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {
        if(params.id && isEncrypted){
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.id as String)?:-1L
        }
        def results = this.search(params)
        if (results) {
            return results[0]
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
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

}