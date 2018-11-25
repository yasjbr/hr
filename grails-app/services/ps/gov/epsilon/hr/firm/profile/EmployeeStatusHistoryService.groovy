package ps.gov.epsilon.hr.firm.profile

import grails.gorm.PagedResultList
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Query
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

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
class EmployeeStatusHistoryService {

    MessageSource messageSource
    def formatService
    PersonService personService
    def sessionFactory

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeStatus", type: "EmployeeStatus", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDateTime", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDateTime", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employeeStatus", type: "EmployeeStatus", source: 'domain'],
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
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }


        List<Map<String, String>> orderBy = params.list("orderBy")
        String employeeId = params["employee.id"]
        String employeeStatusId = params["employeeStatus.id"]
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])

        return EmployeeStatusHistory.createCriteria().list(max: max, offset: offset) {
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
                if (employeeStatusId) {
                    eq("employeeStatus.id", employeeStatusId)
                }
                if (fromDate) {
                    le("fromDate", fromDate)
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
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeStatusHistory.
     */
    EmployeeStatusHistory save(GrailsParameterMap params) {
        EmployeeStatusHistory employeeStatusHistoryInstance

        //the save action allowed just for manager
        if (SpringSecurityUtils.ifNotGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_SUPER_ADMIN.value)) {
            employeeStatusHistoryInstance = new EmployeeStatusHistory()
            employeeStatusHistoryInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeStatusHistory.label', null, 'employeeStatusHistory', LocaleContextHolder.getLocale())] as Object[], "This employeeStatusHistory with ${params.id} not found")
            return employeeStatusHistoryInstance
        }

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            employeeStatusHistoryInstance = EmployeeStatusHistory.get(params["id"])
            if (employeeStatusHistoryInstance) {
                if (params.long("version")) {
                    long version = params.long("version")
                    if (employeeStatusHistoryInstance.version > version) {
                        employeeStatusHistoryInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeeStatusHistory.label', null, 'employeeStatusHistory', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeStatusHistory while you were editing")
                        return employeeStatusHistoryInstance
                    }
                }
            } else {
                employeeStatusHistoryInstance = new EmployeeStatusHistory()
                employeeStatusHistoryInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeStatusHistory.label', null, 'employeeStatusHistory', LocaleContextHolder.getLocale())] as Object[], "This employeeStatusHistory with ${params.id} not found")
                return employeeStatusHistoryInstance
            }
        } else {
            employeeStatusHistoryInstance = new EmployeeStatusHistory()
        }
        try {
            employeeStatusHistoryInstance.properties = params;
            employeeStatusHistoryInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            employeeStatusHistoryInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeeStatusHistoryInstance
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
                EmployeeStatusHistory.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                EmployeeStatusHistory.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
     * @return EmployeeStatusHistory.
     */
    @Transactional(readOnly = true)
    EmployeeStatusHistory getInstance(GrailsParameterMap params) {
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
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeStatusHistory.
     */
    @Transactional(readOnly = true)
    EmployeeStatusHistory getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                EmployeeStatusHistory employeeStatusHistory = results[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employeeStatusHistory?.employee?.personId))
                PersonDTO personDTO = personService.getPerson(searchBean)
                employeeStatusHistory.employee.transientData.put("personDTO", personDTO)
                return employeeStatusHistory
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
        String domainColumns = params["domainColumns"]
        if (domainColumns) {
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }


    /**
     * Reflect the employee status changes into the employee
     * Close all open statuses which have different status category.
     * This code is called from EmployeeStatusHistory before insert
     * @param employeeStatusHistory instance.
     * @return void.
     */
    public void reflectCategoryStatusIntoEmployeeProfile(EmployeeStatusHistory employeeStatusHistory) {
        try {
            if (employeeStatusHistory?.employee?.categoryStatus != employeeStatusHistory?.employeeStatus?.employeeStatusCategory) {
                //update the employee category status and date to have the new values of category
                employeeStatusHistory?.employee?.categoryStatus = employeeStatusHistory?.employeeStatus?.employeeStatusCategory
                employeeStatusHistory?.employee?.categoryStatusDate = employeeStatusHistory?.fromDate

                //update all old statuses history to set the endDate (if endDate is null) -> close the old statuses
                final session = sessionFactory.currentSession
                Query query = session.createSQLQuery(""" update employee_status_history eh
                                                    set to_date_datetime = ? , 
                                                    to_date_date_tz = ? 
                                                    where eh.to_date_datetime = '0003-03-03 03:03:03'
                                                    and eh.employee_id = ?
                                                    and eh.employee_status_id in
                                                    (select es.id from employee_status es where es.employee_status_category_id!=?) """);
                //set the sql query params
                query.setParameter(0, java.util.Date?.from(employeeStatusHistory?.fromDate?.toInstant()))
                query.setParameter(1, employeeStatusHistory?.fromDate?.zone.toString())
                query.setParameter(2, employeeStatusHistory?.employee?.id)
                query.setParameter(3, employeeStatusHistory?.employeeStatus?.employeeStatusCategory?.id)
                //execute the sql query
                final queryResults = query?.executeUpdate()
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}