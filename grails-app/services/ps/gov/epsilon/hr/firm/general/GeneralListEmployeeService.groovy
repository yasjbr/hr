package ps.gov.epsilon.hr.firm.general

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
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
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service aims to create general list employee.
 * <h1>Usage</h1>
 * -this service used to create general list employee.
 * <h1>Restriction</h1>
 * -need general list & employee created before.
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class GeneralListEmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    GovernorateService governorateService
    GeneralListService generalListService


    public static getEmployeeEncodedId = { cService, GeneralListEmployee rec, object, params ->
        return rec?.employee?.encodedId;
    }
    public static getEmployeeId = { cService, GeneralListEmployee rec, object, params ->
        return rec?.employee?.id;
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "emlpoyeeEncodedId", type: getEmployeeEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeId", type: getEmployeeId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]
    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "emlpoyeeEncodedId", type: getEmployeeEncodedId, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "employeeId", type: getEmployeeId, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
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
        String employeeId = params["employee.id"] ? params["employee.id"] : params["employeeId"]
        String militaryRankId = params["militaryRank.id"]
        String generalListId = params["generalList.id"]
        Set generalListEmployeeNotesIds = params.listString("generalListEmployeeNotes.id")
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null

        return GeneralListEmployee.createCriteria().list(max: max, offset: offset) {
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
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (militaryRankId) {
                    employeeMilitaryRank {
                        militaryRank {
                            eq("id", militaryRankId)
                        }
                    }
                }

                if (generalListId) {
                    eq("generalList.id", generalListId)
                }
                if (generalListEmployeeNotesIds) {
                    generalListEmployeeNotes {
                        inList("id", generalListEmployeeNotesIds)
                    }
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                switch (columnName) {
                    case 'employeeId':
                        order("employee.id", dir)
                        break
                    default:
                        order(columnName, dir)
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
 * @return GeneralListEmployee.
 */
    GeneralListEmployee save(GrailsParameterMap params) {
        GeneralListEmployee generalListEmployeeInstance

        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            generalListEmployeeInstance = GeneralListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (generalListEmployeeInstance.version > version) {
                    generalListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('generalListEmployee.label', null, 'generalListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this generalListEmployee while you were editing")
                    return generalListEmployeeInstance
                }
            }
            if (!generalListEmployeeInstance) {
                generalListEmployeeInstance = new GeneralListEmployee()
                generalListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('generalListEmployee.label', null, 'generalListEmployee', LocaleContextHolder.getLocale())] as Object[], "This generalListEmployee with ${params.id} not found")
                return generalListEmployeeInstance
            }
        } else {
            generalListEmployeeInstance = new GeneralListEmployee()
        }
        try {
            generalListEmployeeInstance.properties = params;
            generalListEmployeeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            generalListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return generalListEmployeeInstance
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
                GeneralListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                GeneralListEmployee.findAllByIdInList([deleteBean?.ids])*.delete(flush: true)
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
 * @return GeneralListEmployee.
 */
    @Transactional(readOnly = true)
    GeneralListEmployee getInstance(GrailsParameterMap params) {
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
        PagedResultList generalListEmployeeList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<GovernorateDTO> governorateDTOList

        if (generalListEmployeeList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: generalListEmployeeList?.resultList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            //fill employee governorate information from core
            SearchBean governorateSearchBean = new SearchBean()
            governorateSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: generalListEmployeeList?.resultList?.employee?.currentEmploymentRecord?.department?.governorateId))
            governorateDTOList = governorateService?.searchGovernorate(governorateSearchBean)?.resultList

            generalListEmployeeList?.each { GeneralListEmployee generalListEmployee ->
                /**
                 * assign personDTO for request
                 */
                generalListEmployee.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == generalListEmployee?.employee?.personId
                })
                /**
                 * assign for governorateDTO  for employee
                 */
                generalListEmployee?.employee?.transientData?.put("governorateDTO", governorateDTOList.find {
                    it.id == generalListEmployee?.employee?.currentEmploymentRecord?.department?.governorateId
                })


            }

        }
        return generalListEmployeeList
    }

    /**
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["generalList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        GeneralList generalList = generalListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = generalList?.code
        map.coverLetter = generalList?.coverLetter
        map.details = resultList
        return [map]
    }

}