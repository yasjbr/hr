package ps.gov.epsilon.hr.firm.settings

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
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class FirmSupportContactInfoService {

    MessageSource messageSource
    def formatService


    public static getEncodedId = { cService, FirmSupportContactInfo rec, object, params ->
        if(rec?.encodedId) {
            return rec?.encodedId
        }else {
            return rec?.id
        }
    }

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: getEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "name", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "phoneNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "faxNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "email", type: "String", source: 'domain']

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
        List ids = params.list('ids[]')
        String id
        if(isEncrypted && params["id"]) {
            params.id  = HashHelper.decode(params.id as String) ?: -1L
        }else {
            id = params["id"]
        }

        List<Map<String,String>> orderBy = params.list("orderBy")
                    String email = params["email"]
                    String faxNumber = params["faxNumber"]
                    Long firmId = params.long("firm.id")
                    String name = params["name"]
                    String phoneNumber = params["phoneNumber"]

        return FirmSupportContactInfo.createCriteria().list(max: max, offset: offset){
            if(sSearch) {
                or{
                    ilike("email", sSearch) 
                    ilike("faxNumber", sSearch) 
                    ilike("name", sSearch) 
                    ilike("phoneNumber", sSearch) 
                }
            }
            and {
                if(id) {
                    eq("id", id)
                }
                if(ids) {
                    inList("id", ids)
                }
                        if(email){
                            ilike("email", "%${email}%")
                        }
                        if(faxNumber){
                            ilike("faxNumber", "%${faxNumber}%")
                        }
                        if(firmId){
                    eq("firm.id", firmId)
                }
                        if(name){
                            ilike("name", "%${name}%")
                        }
                        if(phoneNumber){
                            ilike("phoneNumber", "%${phoneNumber}%")
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
     * @return FirmSupportContactInfo.
     */
    FirmSupportContactInfo save(GrailsParameterMap params) {
        FirmSupportContactInfo firmSupportContactInfoInstance
        if (params.id) {
            firmSupportContactInfoInstance = FirmSupportContactInfo.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (firmSupportContactInfoInstance.version > version) {
                    firmSupportContactInfoInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('firmSupportContactInfo.label', null, 'firmSupportContactInfo',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this firmSupportContactInfo while you were editing")
                    return firmSupportContactInfoInstance
                }
            }
            if (!firmSupportContactInfoInstance) {
                firmSupportContactInfoInstance = new FirmSupportContactInfo()
                firmSupportContactInfoInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('firmSupportContactInfo.label', null, 'firmSupportContactInfo',LocaleContextHolder.getLocale())] as Object[], "This firmSupportContactInfo with ${params.id} not found")
                return firmSupportContactInfoInstance
            }
        } else {
            firmSupportContactInfoInstance = new FirmSupportContactInfo()
        }
        try {
            firmSupportContactInfoInstance.properties = params;
            firmSupportContactInfoInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            firmSupportContactInfoInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return firmSupportContactInfoInstance
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
                FirmSupportContactInfo.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                FirmSupportContactInfo.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
     * @return FirmSupportContactInfo.
     */
    @Transactional(readOnly = true)
    FirmSupportContactInfo getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {

        if(params.id && isEncrypted){
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.id as String) ?: -1L
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