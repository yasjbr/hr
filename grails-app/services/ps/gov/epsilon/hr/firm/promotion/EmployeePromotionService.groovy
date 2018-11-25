package ps.gov.epsilon.hr.firm.promotion

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
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
class EmployeePromotionService {

    MessageSource messageSource
    def formatService
    PersonService personService

    public static militaryRankFullInfo = { formatService, EmployeePromotion dataRow, object, params ->
        String rankInfo = ""
        if (dataRow) {
            rankInfo = dataRow.militaryRank.toString()
            if (dataRow?.militaryRankClassification) {
                rankInfo = rankInfo + " " + dataRow.militaryRankClassification.toString() + "  "
            }
            if (dataRow?.militaryRankType) {
                rankInfo = rankInfo + " " + dataRow.militaryRankType.toString()
            }
        }
        return rankInfo
    }

    public static getRequestNum = { formatService, EmployeePromotion dataRow, object, params ->
        String link = ""
        if (dataRow?.requestSource?.id) {
            String hoverMessage = formatService.messageSource.getMessage("request.hoverMessage.label", null, LocaleContextHolder.getLocale())
            if (dataRow?.requestSource?.requestType == EnumRequestType.UPDATE_MILITARY_RANK_TYPE || dataRow?.requestSource?.requestType == EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION) {
                link = "<a title='${hoverMessage}' href='../updateMilitaryRankRequest/show?encodedId=${dataRow?.requestSource?.encodedId}'>" + dataRow?.requestSource?.id + "</a>";
            } else {
                link = "<a title='${hoverMessage}' href='../promotionRequest/show?encodedId=${dataRow?.requestSource?.encodedId}'>" + dataRow?.requestSource?.id + "</a>";
            }
        }
        return link
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "managerialRank", type: "MilitaryRank", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "actualDueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "managerialOrderNumber", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "managerialRankDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "militaryRank", type: "MilitaryRank", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "militaryRankType", type: "MilitaryRankType", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "militaryRankTypeDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestSourceId", type: getRequestNum, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "dueReason", type: "enum", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "managerialOrderNumber", type:"String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "militaryRankFullInfo", type: militaryRankFullInfo, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "actualDueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "militaryRankTypeDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "dueReason", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "requestSourceId", type: getRequestNum, source: 'domain'],
    ]

    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "orderDate", type:"ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "managerialOrderNumber", type:"String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.previousMilitaryRank", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.previousMilitaryRankDate", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.militaryRankFullInfo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "actualDueDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "militaryRankTypeDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "transientData.dueReason", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "transientData.requestSourceId", type: "String", source: 'domain'],
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
        ZonedDateTime actualDueDate = PCPUtils.parseZonedDateTime(params['actualDueDate'])
        ZonedDateTime dueDate = PCPUtils.parseZonedDateTime(params['dueDate'])
        ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason dueReason = params["dueReason"] ? ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.valueOf(params["dueReason"]) : null
        String employeeId = params["employee.id"]
        String managerialOrderNumber = params["managerialOrderNumber"]
        String managerialRankId = params["managerialRank.id"]
        ZonedDateTime managerialRankDate = PCPUtils.parseZonedDateTime(params['managerialRankDate'])
        String militaryRankId = params["militaryRank.id"]
        String militaryRankTypeId = params["militaryRankType.id"]
        String note = params["note"]
        String promotionListEmployeeId = params["promotionListEmployee.id"]
        String requestSourceId = params["requestSource.id"]

        return EmployeePromotion.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("managerialOrderNumber", sSearch)
                    ilike("note", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (actualDueDate) {
                    le("actualDueDate", actualDueDate)
                }
                if (dueDate) {
                    le("dueDate", dueDate)
                }
                if (dueReason) {
                    eq("dueReason", dueReason)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (managerialOrderNumber) {
                    ilike("managerialOrderNumber", "%${managerialOrderNumber}%")
                }
                if (managerialRankId) {
                    eq("managerialRank.id", managerialRankId)
                }
                if (managerialRankDate) {
                    le("managerialRankDate", managerialRankDate)
                }
                if (militaryRankId) {
                    eq("militaryRank.id", militaryRankId)
                }
                if (militaryRankTypeId) {
                    eq("militaryRankType.id", militaryRankTypeId)
                }
                if (note) {
                    ilike("note", "%${note}%")
                }
                if (promotionListEmployeeId) {
                    eq("promotionListEmployee.id", promotionListEmployeeId)
                }
                if (requestSourceId) {
                    eq("requestSource.id", requestSourceId)
                }
                eq("firm.id", PCPSessionUtils.getValue("firmId"))
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
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchReport(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)

        String rankInfo = ""
        pagedResultList.each { EmployeePromotion employeePromotion ->
            employeePromotion?.transientData?.requestSourceId = employeePromotion?.requestSource?.id
            employeePromotion?.transientData?.dueReason = messageSource.getMessage('EnumPromotionReason.' + employeePromotion.dueReason.toString(),null,'Due Reason',LocaleContextHolder.getLocale())

            //get military rank full information
            rankInfo = employeePromotion.militaryRank.toString()
            if (employeePromotion?.militaryRankClassification) {
                rankInfo = rankInfo + " " + employeePromotion.militaryRankClassification.toString() + "  "
            }
            if (employeePromotion?.militaryRankType) {
                rankInfo = rankInfo + " " + employeePromotion.militaryRankType.toString()
            }

            employeePromotion?.transientData?.militaryRankFullInfo = rankInfo

            //get previous military rank information
            List<EmployeePromotion> previousEmployeePromotions = EmployeePromotion.createCriteria().list(){
                and {
                    employee{
                        eq("id", employeePromotion?.employee?.id)
                    }

                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                    lt("actualDueDate",employeePromotion?.actualDueDate)
                }
                order("actualDueDate", "desc")
                setMaxResults(1)
            }
            if(previousEmployeePromotions){
                employeePromotion?.transientData?.previousMilitaryRank = previousEmployeePromotions?.get(0)?.militaryRank?.descriptionInfo?.localName
                employeePromotion?.transientData?.previousMilitaryRankDate = previousEmployeePromotions?.get(0)?.actualDueDate
            }
        }
        return pagedResultList
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return EmployeePromotion.
 */
    EmployeePromotion save(GrailsParameterMap params) {
        EmployeePromotion employeePromotionInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            employeePromotionInstance = EmployeePromotion.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeePromotionInstance.version > version) {
                    employeePromotionInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeePromotion.label', null, 'employeePromotion', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeePromotion while you were editing")
                    return employeePromotionInstance
                }
            }
            if (!employeePromotionInstance) {
                employeePromotionInstance = new EmployeePromotion()
                employeePromotionInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeePromotion.label', null, 'employeePromotion', LocaleContextHolder.getLocale())] as Object[], "This employeePromotion with ${params.id} not found")
                return employeePromotionInstance
            }
        } else {
            employeePromotionInstance = new EmployeePromotion()
        }
        try {
            employeePromotionInstance.properties = params;
            employeePromotionInstance.save(flush:true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            employeePromotionInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeePromotionInstance
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
                EmployeePromotion.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))*.delete(flush: true)
                deleteBean.status = true
            } else if (deleteBean.ids) {
                EmployeePromotion.findAllByIdInList(deleteBean?.ids)*.delete(flush: true)
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
 * @return EmployeePromotion.
 */
    @Transactional(readOnly = true)
    EmployeePromotion getInstance(GrailsParameterMap params) {
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
 * @return EmployeePromotion.
 */
    @Transactional(readOnly = true)
    EmployeePromotion getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                EmployeePromotion employeePromotion = results[0]
                SearchBean searchBean = new SearchBean()
                searchBean.searchCriteria.put("id", new SearchConditionCriteriaBean(operand: 'id', value1: employeePromotion?.employee?.personId))
                PersonDTO personDTO = personService.getPerson(searchBean)
                employeePromotion.employee.transientData.put("personDTO", personDTO)
                return employeePromotion
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

}