package ps.police.pcore.v2.entity.person

import com.gs.collections.api.tuple.primitive.BooleanLongPair
import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.commands.v1.PersonArrestHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonArrestHistoryDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonArrestHistory

/**
 *<h1>Purpose</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Usage</h1>
 * -TO BE FILLED BY DEVELOPER-
 * <h1>Restriction</h1>
 * -TO BE FILLED BY DEVELOPER-
 *@see MessageSource
 *@see FormatService
 **/
@Transactional
class PersonArrestHistoryService implements IPersonArrestHistory{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService


    public static getJailName= { formatService, PersonArrestHistoryDTO dataRow, object, params ->
        if (dataRow) {
            if (dataRow.jail) {
                return dataRow.jail.toString()
            } else {
                return dataRow.jailName
            }
        }
        return ""
    }

    public static getPersonId={ formatService, PersonArrestHistoryDTO dataRow, object, params->
        if(dataRow){
            if(dataRow.person) {
                return dataRow?.person?.id?.toString()
            }
        }
        return  ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
        [sort: false, search: false, hidden: false, name: "person", type: "Person", source: 'domain'],
        [sort: false, search: true, hidden: false, name: "arrestingClassification", type: "enum", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "arrestingParty", type: "enum", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "accusation", type: "String", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "arrestDate", type: "ZonedDate", source: 'domain'],
        [sort: false, search: true, hidden: false, name: "isJudgementForEver", type: "Boolean", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "jailObject", type: getJailName, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "releaseDate", type: "ZonedDate", source: 'domain'],
    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "arrestingClassification", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "arrestingParty", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "accusation", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "arrestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "releaseDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "periodInMonths", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "personId", type: getPersonId, source: 'domain'],
    ]

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List<String> DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     * @see PagedResultList.
     * @see PagedList.
     */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList,GrailsParameterMap params,List<String> DOMAIN_COLUMNS = null) {
        if(!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        String domainColumns = params["domainColumns"]
        if(domainColumns){
            DOMAIN_COLUMNS = this."${domainColumns}"
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    @Override
    PersonArrestHistoryDTO getPersonArrestHistory(SearchBean searchBean) {
        proxyFactoryService.personArrestHistoryProxySetup()
        return proxyFactoryService.personArrestHistoryProxy.getPersonArrestHistory(searchBean)
    }

    @Override
    PagedList<PersonArrestHistoryDTO> searchPersonArrestHistory(SearchBean searchBean) {
        proxyFactoryService.personArrestHistoryProxySetup()
        return proxyFactoryService.personArrestHistoryProxy.searchPersonArrestHistory(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonArrestHistoryDTO> pagedResultList = new PagedResultList<PersonArrestHistoryDTO>()
        pagedResultList.resultList = this.searchPersonArrestHistory(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonArrestHistory(SearchBean searchBean) {
        proxyFactoryService.personArrestHistoryProxySetup()
        return proxyFactoryService.personArrestHistoryProxy.autoCompletePersonArrestHistory(searchBean)
    }

    @Override
    PersonArrestHistoryCommand savePersonArrestHistory(PersonArrestHistoryCommand personArrestHistoryCommand) {
        proxyFactoryService.personArrestHistoryProxySetup()
        return proxyFactoryService.personArrestHistoryProxy.savePersonArrestHistory(personArrestHistoryCommand)
    }

    @Override
    DeleteBean deletePersonArrestHistory(DeleteBean deleteBean) {
        proxyFactoryService.personArrestHistoryProxySetup()
        return proxyFactoryService.personArrestHistoryProxy.deletePersonArrestHistory(deleteBean)
    }
}