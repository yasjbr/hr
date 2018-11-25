package ps.gov.epsilon.hr.firm.recruitment

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
 * <h1>Purpose</h1>
 * -this service is aims to create vacancy advertisement for vacancy
 * <h1>Usage</h1>
 * -this service is used to create advertisement for vacancy
 * <h1>Restriction</h1>
 * -need vacancy and vacancy advertisement created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class JoinedVacancyAdvertisementService {

    MessageSource messageSource
    def formatService
    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy.recruitmentCycle.name", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy.job.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacancy.numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacancy.vacancyStatus", type: "enum", source: 'domain']
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy.recruitmentCycle.name", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacancy.job.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacancy.numberOfPositions", type: "Long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacancy.vacancyStatus", type: "enum", source: 'domain']
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
        List<String> ids = params.list('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }
        List<Map<String, String>> orderBy = params.list("orderBy")
        String vacancyId = params["vacancy.id"]
        String vacancyAdvertisementsId = params["vacancyAdvertisements.id"]
        String sSearchNumber = params["sSearch"]
        return JoinedVacancyAdvertisement.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                vacancy {
                    or {
                        recruitmentCycle {
                            ilike("name", sSearch)
                        }
                        job {
                            descriptionInfo {
                                ilike("localName", sSearch)
                            }
                        }
                        if (sSearchNumber) {
                            eq("numberOfPositions", sSearchNumber)
                        }
                    }
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (vacancyId) {
                    eq("vacancy.id", vacancyId)
                }
                if (vacancyAdvertisementsId) {
                    eq("vacancyAdvertisements.id", vacancyAdvertisementsId)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case "vacancy.recruitmentCycle.name":
                        vacancy {
                            recruitmentCycle {
                                order("name", dir)
                            }
                        }
                        break;
                    case "vacancy.job.descriptionInfo.localName":
                        vacancy {
                            order("job", dir)
                        }
                        break;

                    case "vacancy.numberOfPositions":
                        vacancy {
                            order("numberOfPositions", dir)
                        }
                        break

                    case "vacancy.vacancyStatus":
                        vacancy {
                            order("vacancyStatus", dir)
                        }
                    default:

                        break;
                }
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }
        }
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return JoinedVacancyAdvertisement.
 */
    JoinedVacancyAdvertisement save(GrailsParameterMap params) {
        JoinedVacancyAdvertisement joinedVacancyAdvertisementInstance
        if (params.id) {
            joinedVacancyAdvertisementInstance = JoinedVacancyAdvertisement.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (joinedVacancyAdvertisementInstance.version > version) {
                    joinedVacancyAdvertisementInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('joinedVacancyAdvertisement.label', null, 'joinedVacancyAdvertisement', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this joinedVacancyAdvertisement while you were editing")
                    return joinedVacancyAdvertisementInstance
                }
            }
            if (!joinedVacancyAdvertisementInstance) {
                joinedVacancyAdvertisementInstance = new JoinedVacancyAdvertisement()
                joinedVacancyAdvertisementInstance.errors.reject('default.not.found.message', [messageSource.getMessage('joinedVacancyAdvertisement.label', null, 'joinedVacancyAdvertisement', LocaleContextHolder.getLocale())] as Object[], "This joinedVacancyAdvertisement with ${params.id} not found")
                return joinedVacancyAdvertisementInstance
            }
        } else {
            joinedVacancyAdvertisementInstance = new JoinedVacancyAdvertisement()
        }
        try {
            joinedVacancyAdvertisementInstance.properties = params;
            joinedVacancyAdvertisementInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            joinedVacancyAdvertisementInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return joinedVacancyAdvertisementInstance
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
                JoinedVacancyAdvertisement.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                JoinedVacancyAdvertisement.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return JoinedVacancyAdvertisement.
 */
    @Transactional(readOnly = true)
    JoinedVacancyAdvertisement getInstance(GrailsParameterMap params, Boolean isEncrypted = false) {
        if (params.id && isEncrypted) {
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
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "vacancy.job.descriptionInfo.localName"
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

}