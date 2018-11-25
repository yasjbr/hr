package ps.gov.epsilon.hr.firm.lookups

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.core.location.ManageLocationService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.LocationAddressUtil
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * - this service aims to create province.
 * <h1>Usage</h1>
 * -this service used to create province.
 * <h1>Restriction</h1>
 * -no restriction.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ProvinceService {

    MessageSource messageSource
    def formatService
    ManageLocationService manageLocationService
    LocationService locationService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "descriptionInfo.localName", type: "DescriptionInfo", source: 'domain'],
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
        List<Long> ids = params.listLong('ids[]')
        Long id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = Long.parseLong(HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params.long('id')
        }
        List<Map<String, String>> orderBy = params.list("orderBy")
        String localName = params["descriptionInfo.localName"]
        String latinName = params["descriptionInfo.latinName"]
        String note = params["note"]
        Set provinceFirmsIds = params.listLong("provinceFirms.id")
        Set provinceLocationsIds = params.listLong("provinceLocations.id")
        String universalCode = params["universalCode"]
        return Province.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike('localName', sSearch)
                    ilike('latinName', sSearch)
                    ilike("note", sSearch)
                    ilike("universalCode", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (localName) {
                    ilike('localName', "%$localName%")
                }
                if (latinName) {
                    ilike('latinName', "%$latinName%")
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (provinceFirmsIds) {
                    provinceFirms {
                        inList("id", provinceFirmsIds)
                    }
                }
                if (provinceLocationsIds) {
                    provinceLocations {
                        inList("id", provinceLocationsIds)
                    }
                }
                if (universalCode) {
                    ilike("universalCode", "%${universalCode}%")
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
 * @return Province.
 */
    Province save(GrailsParameterMap params) {
        Province provinceInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            provinceInstance = Province.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (provinceInstance.version > version) {
                    provinceInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('province.label', null, 'province', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this province while you were editing")
                    return provinceInstance
                }
            }
            if (!provinceInstance) {
                provinceInstance = new Province()
                provinceInstance.errors.reject('default.not.found.message', [messageSource.getMessage('province.label', null, 'province', LocaleContextHolder.getLocale())] as Object[], "This province with ${params.id} not found")
                return provinceInstance
            }
        } else {
            provinceInstance = new Province()
        }
        try {

            /**
             * remove previous location when a user update province.
             */
            if (provinceInstance?.id) {
                ProvinceLocation?.executeUpdate("delete from ProvinceLocation pl where pl.province.id = :provinceId", [provinceId: provinceInstance?.id])
            }

            provinceInstance.properties = params;

            /**
             * save selected location into core.
             */
            List<Long> countryIdList = params.listLong("countryIdList")
            GrailsParameterMap locationParams = null
            LocationCommand locationCommand = null
            String entityName = null
            countryIdList?.eachWithIndex { Long id, int index ->
                try {
                entityName = index + "-" + id + "-governorateId"
                locationParams = new GrailsParameterMap([countryId: id, governorateId: params.long(entityName)], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                locationCommand = manageLocationService.saveLocation(locationParams)
                provinceInstance.addToProvinceLocations(new ProvinceLocation(locationId: locationCommand?.id, province: provinceInstance))
                } catch (Exception ex) {
                    provinceInstance.errors.reject('province.error.createLocation.message',[] as Object[], "")
                    return provinceInstance
            }
            }
            provinceInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            provinceInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return provinceInstance
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
                Province.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                Province.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return Province.
 */
    @Transactional(readOnly = true)
    Province getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params["encodedId"])
        }

        //if id is not null then return values from search method
        if (params.id || params.universalCode) {
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
 * @return Province.
 */
    @Transactional(readOnly = true)
    Province getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params["encodedId"])
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
        PagedResultList provinceList = search(params)
        if (provinceList) {
            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: provinceList?.resultList?.provinceLocations?.locationId?.get(0)))
            List<LocationDTO> locationList = locationService?.searchLocation(searchBean)?.resultList
            List<LocationDTO> locationDTOList = null
            provinceList?.each { Province province ->
                locationDTOList = locationList?.findAll { it.id in province?.provinceLocations?.locationId }
                if (locationDTOList) {
                    province.transientData = [:]
                    province.transientData.putAt("locationDTOList", locationDTOList)
                }
            }

        }
        return provinceList
    }


}