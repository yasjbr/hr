package ps.gov.epsilon.hr.firm.correspondenceList

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

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
class CorrespondenceListStatusService {

    MessageSource messageSource
    def formatService


    public static getEncodedId = { cService, CorrespondenceListStatus rec, object, params ->
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
        [sort: true, search: false, hidden: false, name: "correspondenceList", type: "CorrespondenceList", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "correspondenceListStatus", type: "enum", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "firm", type: "Firm", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDateTime", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "receivingParty", type: "enum", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDateTime", source: 'domain'],
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
        List<String> ids = params.listString('ids[]')
        String id
        if (isEncrypted && params.id) {
            id = (HashHelper.decode(params.id))
        } else {
            id = params['id']
        }

        List<Map<String,String>> orderBy = params.list("orderBy")
                    String correspondenceListId = params["correspondenceList.id"]
                    ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus correspondenceListStatus = params["correspondenceListStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.valueOf(params["correspondenceListStatus"]) : null
                    Long firmId = params.long("firm.id")
                    ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])

                    ps.gov.epsilon.hr.enums.v1.EnumReceivingParty receivingParty = params["receivingParty"] ? ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.valueOf(params["receivingParty"]) : null
                    ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        return CorrespondenceListStatus.createCriteria().list(max: max, offset: offset){
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
                        if(correspondenceListId){
                    eq("correspondenceList.id", correspondenceListId)
                }
                        if(correspondenceListStatus){
                    eq("correspondenceListStatus", correspondenceListStatus)
                }
                        if(firmId){
                    eq("firm.id", firmId)
                }
                        if(fromDate){
                            le("fromDate", fromDate)
                        }
                        if(receivingParty){
                    eq("receivingParty", receivingParty)
                }
                        if(toDate){
                            le("toDate", toDate)
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
     * @return CorrespondenceListStatus.
     */
    CorrespondenceListStatus save(GrailsParameterMap params) {
        CorrespondenceListStatus correspondenceListStatusInstance
        if (params.id) {
            correspondenceListStatusInstance = CorrespondenceListStatus.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (correspondenceListStatusInstance.version > version) {
                    correspondenceListStatusInstance.errors.reject('default.optimistic.locking.failure' ,[messageSource.getMessage('correspondenceListStatus.label', null, 'correspondenceListStatus',LocaleContextHolder.getLocale())] as Object[], "Another user has updated this correspondenceListStatus while you were editing")
                    return correspondenceListStatusInstance
                }
            }
            if (!correspondenceListStatusInstance) {
                correspondenceListStatusInstance = new CorrespondenceListStatus()
                correspondenceListStatusInstance.errors.reject('default.not.found.message' ,[messageSource.getMessage('correspondenceListStatus.label', null, 'correspondenceListStatus',LocaleContextHolder.getLocale())] as Object[], "This correspondenceListStatus with ${params.id} not found")
                return correspondenceListStatusInstance
            }
        } else {
            correspondenceListStatusInstance = new CorrespondenceListStatus()
        }
        try {
            correspondenceListStatusInstance.properties = params;
            correspondenceListStatusInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            correspondenceListStatusInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0,ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return correspondenceListStatusInstance
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
                CorrespondenceListStatus.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            }else if (deleteBean.ids){
                CorrespondenceListStatus.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
     * @return CorrespondenceListStatus.
     */
    @Transactional(readOnly = true)
    CorrespondenceListStatus getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {
        if(params.id && isEncrypted){
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.id) ?: -1L
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
        String nameProperty = params["nameProperty"]?:"correspondenceListStatus"
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