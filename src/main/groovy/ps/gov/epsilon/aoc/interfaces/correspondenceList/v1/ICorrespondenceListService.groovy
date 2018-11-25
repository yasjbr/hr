package ps.gov.epsilon.aoc.interfaces.correspondenceList.v1

import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.beans.v1.PagedList

/**
 * Created by Muath on 25/03/18.
 *
 * <h1>Purpose</h1>
 * For each correspondence type, a service should implement this interface
 * <h1>Usage</h1>
 * Used as to render lists, save and show depending on correspondence type
 */
interface ICorrespondenceListService {

    /**
     * @return List of columns to be rendered in dataTable
     */
    List<String> getDomainColumns()

    /**
     * search and include values from core
     * @param params
     * @return: List of data meets search criteria
     */
    PagedList searchWithRemotingValues(GrailsParameterMap params)

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List < String >  DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     */
    Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS)

    /**
     * returns instance related to encoded id
     * @param params
     * @return
     */
    Object getInstance(GrailsParameterMap params)

    /**
     * Saves a correspondence from params
     * @param params
     * @return a subclass from CorrespondenceList
     */
    CorrespondenceList save(GrailsParameterMap params)

    /**
     * closes hr list, reflects changes
     * @param params
     */
    CorrespondenceList closeHrList(GrailsParameterMap params)

    /**
     * Saves approval info
     * @param params
     * @return hrListEmployee
     */
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params);
}
