package ps.gov.epsilon.hr.firm.lookups

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
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service aim to create province locations.
 * <h1>Usage</h1>
 * -this service used to create province location.
 * <h1>Restriction</h1>
 * -need a province created before.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ProvinceLocationService {

    MessageSource messageSource
    def formatService
    LocationService locationService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "locationId", type: "Long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "province", type: "Province", source: 'domain'],
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
        List<Long> ids = params.listString('ids[]')
        Long id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = Long.parseLong(HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params.long('id')
        }


        List<Map<String, String>> orderBy = params.list("orderBy")
        Long locationId = params.long("locationId")
        Long provinceId = params.long("province.id")

        return ProvinceLocation.createCriteria().list(max: max, offset: offset) {
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
                if (locationId) {
                    eq("locationId", locationId)
                }
                if (provinceId) {
                    eq("province.id", provinceId)
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
 * @return ProvinceLocation.
 */
    ProvinceLocation save(GrailsParameterMap params) {
        ProvinceLocation provinceLocationInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            provinceLocationInstance = ProvinceLocation.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (provinceLocationInstance.version > version) {
                    provinceLocationInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('provinceLocation.label', null, 'provinceLocation', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this provinceLocation while you were editing")
                    return provinceLocationInstance
                }
            }
            if (!provinceLocationInstance) {
                provinceLocationInstance = new ProvinceLocation()
                provinceLocationInstance.errors.reject('default.not.found.message', [messageSource.getMessage('provinceLocation.label', null, 'provinceLocation', LocaleContextHolder.getLocale())] as Object[], "This provinceLocation with ${params.id} not found")
                return provinceLocationInstance
            }
        } else {
            provinceLocationInstance = new ProvinceLocation()
        }
        try {
            provinceLocationInstance.properties = params;
            provinceLocationInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            provinceLocationInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return provinceLocationInstance
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
                ProvinceLocation.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                ProvinceLocation.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return ProvinceLocation.
 */
    @Transactional(readOnly = true)
    ProvinceLocation getInstance(GrailsParameterMap params) {
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
     * @return ProvinceLocation.
     */
    @Transactional(readOnly = true)
    ProvinceLocation getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.remove("encodedId"))
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
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
        String nameProperty = params["nameProperty"] ?: "transientData.locationName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = searchWithRemotingValues(params)
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

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList provinceLocationList = search(params)
        if (provinceLocationList) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: provinceLocationList?.resultList?.locationId?.get(0)))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList
            provinceLocationList?.each { ProvinceLocation provinceLocation ->
                provinceLocation.transientData = [:]
                provinceLocation.transientData.putAt("locationDTO", locationList?.find {
                    it.id == provinceLocation.locationId
                })
                provinceLocation.transientData.putAt("locationName", locationList?.find {
                    it.id == provinceLocation.locationId
                }?.toString())
            }
        }
        return provinceLocationList
    }
}