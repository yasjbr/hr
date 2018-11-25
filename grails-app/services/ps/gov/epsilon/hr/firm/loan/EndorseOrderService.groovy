package ps.gov.epsilon.hr.firm.loan

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus

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
class EndorseOrderService {

    MessageSource messageSource
    def formatService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "orderNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "orderDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "note", type: "string", source: 'domain', wrapped: true],
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
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        String note = params["note"]
        ZonedDateTime orderDate = PCPUtils.parseZonedDateTime(params['orderDate'])
        String orderNo = params["orderNo"]

        return EndorseOrder.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("note", sSearch)
                    ilike("orderNo", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (effectiveDate) {
                    le("effectiveDate", effectiveDate)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (orderDate) {
                    le("orderDate", orderDate)
                }
                if (orderNo) {
                    ilike("orderNo", "%${orderNo}%")
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
 * @return EndorseOrder.
 */
    EndorseOrder save(GrailsParameterMap params) {
        EndorseOrder endorseOrderInstance
        LoanNominatedEmployee loanNominatedEmployeeInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }

        if (params.id) {
            endorseOrderInstance = EndorseOrder.get(params["id"])
            if (endorseOrderInstance) {
                if (params.long("version")) {
                    long version = params.long("version")
                    if (endorseOrderInstance.version > version) {
                        endorseOrderInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('endorseOrder.label', null, 'endorseOrder', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this endorseOrder while you were editing")
                        return endorseOrderInstance
                    }
                }
            } else {
                endorseOrderInstance = new EndorseOrder()
                endorseOrderInstance.errors.reject('default.not.found.message', [messageSource.getMessage('endorseOrder.label', null, 'endorseOrder', LocaleContextHolder.getLocale())] as Object[], "This endorseOrder with ${params.id} not found")
                return endorseOrderInstance
            }
        } else {
            endorseOrderInstance = new EndorseOrder()
        }
        try {

            endorseOrderInstance.properties = params;
            endorseOrderInstance.save(flush: true, failOnError: true);

            //set endorse order id in loan nominated employee
            if (!params["id"] && params['loanNominatedEmployeeEncodedId']) {

                String loanNominatedEmployeeId = HashHelper.decode(params.loanNominatedEmployeeEncodedId)
                loanNominatedEmployeeInstance = LoanNominatedEmployee.load(loanNominatedEmployeeId)

                //set the id of endorseOrder in loanNominatedEmployee
                loanNominatedEmployeeInstance.endorseOrder = endorseOrderInstance
                loanNominatedEmployeeInstance.save(flush: true, failOnError: true);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            if (!endorseOrderInstance.hasErrors()) {
                endorseOrderInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
            }
        }
        return endorseOrderInstance
    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return EndorseOrder.
 */
    @Transactional(readOnly = true)
    EndorseOrder getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        boolean withLoanNominatedEmployee = params.boolean("withLoanNominatedEmployee")
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                EndorseOrder endorseOrder = results[0]
                if(withLoanNominatedEmployee) {
                    endorseOrder.transientData.loanNominatedEmployee = LoanNominatedEmployee.findByEndorseOrder(endorseOrder)
                }
                return endorseOrder
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
        String nameProperty = params["nameProperty"] ?: "orderNo"
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