package ps.gov.epsilon.aoc.interfaces.correspondenceList.v1

import grails.gorm.DetachedCriteria
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.beans.v1.PagedList

/**
 * Created by Muath on 25/03/18.
 *
 * <h1>Purpose</h1>
 * AocRecordList Subclass service should implement this interface
 * <h1>Usage</h1>
 * Used as to render lists depending on record type
 */
interface IListRecordService {

    /**
     * @return List of columns to be rendered in dataTable
     */
    List<String> getDomainColumns()

    /**
     * @return List of columns to be rendered in dataTable
     */
    List<String> getHrDomainColumns()

    /**
     * search and include values from core
     * @param pagedResultList
     * @return: List of data meets search criteria
     */
    PagedList searchWithRemotingValues(def resultList)

    /**
     * search records not included in AOC correspondence
     * @param params
     * @return: List of data meets search criteria
     */
    PagedList searchNotIncludedRecords(GrailsParameterMap params)

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
     * Saves a listEmployee from params
     * @param params
     * @return a subclass
     */
    Object save(AocListRecord aocListRecord, CorrespondenceList hrCorrespondenceList, GrailsParameterMap params)

    /**
     * used to get employee and any other related info necessary for creating request
     * @param params
     * @return
     */
    Map getEmployeeRequestInfo(GrailsParameterMap params)

    /**
     * used to get operation info and any other related info necessary for creating request
     * @param params
     * @return
     */
    Map getOperationFormInfo(GrailsParameterMap params)

    /**
     * returns a new empty instance
     * @param params
     * @return
     */
    Object getNewInstance(GrailsParameterMap params)

    /**
     * updates hr record status and hr request status to rejected or approved after completing workflow
     * @param aocListRecordList
     */
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber)

    /**
     * search for params
     * @param params
     * @return: Detached criteria
     */
    DetachedCriteria search(GrailsParameterMap params)

    /**
     * Checks if employee profile is locked
     * @param listRecord
     * @return true or false
     */
    Boolean isEmployeeProfileLocked(AocListRecord listRecord)
}
