package ps.police.pcore.v2.entity.person

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.person.commands.v1.PersonHealthHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonHealthHistoryDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonHealthHistory

import java.time.ZonedDateTime
import ps.police.common.domains.v1.DescriptionInfo

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
class PersonHealthHistoryService implements IPersonHealthHistory{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService


    public static getPersonId ={ formatService, PersonHealthHistoryDTO dataRow, object, params->
        if(dataRow){
            return dataRow?.person?.id?.toString()
        }
        return  ""
    }


    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "person", type: "Person", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "diseaseType", type: "DiseaseType", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "diseaseName", type: "String", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "affictionDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "description", type: "String", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "affictionLocation", type: "Location", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "diseaseType", type: "DiseaseType", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "diseaseName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "affictionDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "description", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "affictionLocation", type: "Location", source: 'domain'],
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
    PersonHealthHistoryDTO getPersonHealthHistory(SearchBean searchBean) {
        proxyFactoryService.personHealthHistoryProxySetup()
        return proxyFactoryService.personHealthHistoryProxy.getPersonHealthHistory(searchBean)
    }

    @Override
    PagedList<PersonHealthHistoryDTO> searchPersonHealthHistory(SearchBean searchBean) {
        proxyFactoryService.personHealthHistoryProxySetup()
        return proxyFactoryService.personHealthHistoryProxy.searchPersonHealthHistory(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonHealthHistoryDTO> pagedResultList = new PagedResultList<PersonHealthHistoryDTO>()
        pagedResultList.resultList = this.searchPersonHealthHistory(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonHealthHistory(SearchBean searchBean) {
        proxyFactoryService.personHealthHistoryProxySetup()
        return proxyFactoryService.personHealthHistoryProxy.autoCompletePersonHealthHistory(searchBean)
    }

    @Override
    PersonHealthHistoryCommand savePersonHealthHistory(PersonHealthHistoryCommand personHealthHistoryCommand) {
        proxyFactoryService.personHealthHistoryProxySetup()
        return proxyFactoryService.personHealthHistoryProxy.savePersonHealthHistory(personHealthHistoryCommand)
    }

    @Override
    DeleteBean deletePersonHealthHistory(DeleteBean deleteBean) {
        proxyFactoryService.personHealthHistoryProxySetup()
        return proxyFactoryService.personHealthHistoryProxy.deletePersonHealthHistory(deleteBean)
    }
}