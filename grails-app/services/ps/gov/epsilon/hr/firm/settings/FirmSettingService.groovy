package ps.gov.epsilon.hr.firm.settings

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils

/**
 * <h1>Purpose</h1>
 * this service is aims to active setting for firm
 * <h1>Usage</h1>
 * -used for firm
 * <h1>Restriction</h1>
 * - needs firm created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class FirmSettingService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: false, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "propertyName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "propertyValue", type: "String", source: 'domain']
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
        Long firmId = params.long("firm.id")?:PCPSessionUtils.getValue("firmId")
        String propertyName = params["propertyName"]
        String propertyValue = params["propertyValue"]

        return FirmSetting.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("propertyName", sSearch)
                    ilike("propertyValue", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                eq("firm.id", firmId)
                if (propertyName) {
                    ilike("propertyName", "%${propertyName}%")
                }
                if (propertyValue) {
                    ilike("propertyValue", "%${propertyValue}%")
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                order(columnName, dir)
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return FirmSetting.
     */
    FirmSetting save(GrailsParameterMap params) {
        FirmSetting firmSettingInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            firmSettingInstance = FirmSetting.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (firmSettingInstance.version > version) {
                    firmSettingInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('firmSetting.label', null, 'firmSetting', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this firmSetting while you were editing")
                    return firmSettingInstance
                }
            }
            if (!firmSettingInstance) {
                firmSettingInstance = new FirmSetting()
                firmSettingInstance.errors.reject('default.not.found.message', [messageSource.getMessage('firmSetting.label', null, 'firmSetting', LocaleContextHolder.getLocale())] as Object[], "This firmSetting with ${params.id} not found")
                return firmSettingInstance
            }
        } else {
            firmSettingInstance = new FirmSetting()
        }
        try {
            firmSettingInstance.properties = params;
            firmSettingInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            firmSettingInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return firmSettingInstance
    }

    /**
     * to delete model entry.
     * @param DeleteBean deleteBean.
     * @return DeleteBean.
     * @see DeleteBean.
     */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                FirmSetting.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                FirmSetting.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
     * @return FirmSetting.
     */
    @Transactional(readOnly = true)
    FirmSetting getInstance(GrailsParameterMap params) {
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
     * to auto complete model entry.
     * @param GrailsParameterMap params the search map.
     * @return JSON.
     */
    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "propertyName"
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
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    public String getFirmSettingValue(String propertyName, Long firmId= PCPSessionUtils.getValue("firmId")){
        return FirmSetting.createCriteria().get {
            eq('propertyName', propertyName)
            eq('firm.id', firmId)
            projections{
                property('propertyValue')
            }
        }
    }

}