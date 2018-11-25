package ps.gov.epsilon.hr.firm.recruitment

import grails.converters.JSON
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class RecruitmentCyclePhaseService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "requisitionAnnouncementStatus", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
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
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            id = params['id']
        }


        List<Map<String, String>> orderBy = params.list("orderBy")
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])

        String recruitmentCycleId
        if (params["recruitmentCycle.id"]) {
            recruitmentCycleId = params["recruitmentCycle.id"]
        } else if (params.encodedRecruitmentCycleId) {
            recruitmentCycleId = (HashHelper.decode(params.encodedRecruitmentCycleId))
        }




        ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus requisitionAnnouncementStatus = params["requisitionAnnouncementStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.valueOf(params["requisitionAnnouncementStatus"]) : null
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        return RecruitmentCyclePhase.createCriteria().list(max: max, offset: offset) {
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
                if (fromDate) {
                    le("fromDate", fromDate)
                }
                if (recruitmentCycleId) {
                    eq("recruitmentCycle.id", recruitmentCycleId)
                }
                if (requisitionAnnouncementStatus) {
                    eq("requisitionAnnouncementStatus", requisitionAnnouncementStatus)
                }
                if (toDate) {
                    le("toDate", toDate)
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
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return RecruitmentCyclePhase.
     */
    @Transactional(readOnly = true)
    RecruitmentCyclePhase getInstance(GrailsParameterMap params) {
        if (params.id) {
            //if the id passed is encrypted:
            params.id = HashHelper.decode(params.id)
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
        String nameProperty = params["nameProperty"] ?: "requisitionAnnouncementStatus"
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