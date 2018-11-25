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
import ps.police.pcore.v2.entity.person.commands.v1.PersonCharacteristicsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonCharacteristicsDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonCharacteristics

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
class PersonCharacteristicsService implements IPersonCharacteristics{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "person", type: "Person", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "eyeColor", type: "Color", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "hairColor", type: "Color", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "skinColor", type: "Color", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "hairFeature", type: "HairFeature", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "informationMarker", type: "String", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "personVoice", type: "String", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "specialSkills", type: "String", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "eyeColor", type: "Color", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hairColor", type: "Color", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "hairFeature", type: "HairFeature", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "skinColor", type: "Color", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "informationMarker", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "personVoice", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "specialSkills", type: "String", source: 'domain'],
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
    PersonCharacteristicsDTO getPersonCharacteristics(SearchBean searchBean) {
        proxyFactoryService.personCharacteristicsProxySetup()
        return proxyFactoryService.personCharacteristicsProxy.getPersonCharacteristics(searchBean)
    }

    @Override
    PagedList<PersonCharacteristicsDTO> searchPersonCharacteristics(SearchBean searchBean) {
        proxyFactoryService.personCharacteristicsProxySetup()
        return proxyFactoryService.personCharacteristicsProxy.searchPersonCharacteristics(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonCharacteristicsDTO> pagedResultList = new PagedResultList<PersonCharacteristicsDTO>()
        pagedResultList.resultList = this.searchPersonCharacteristics(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonCharacteristics(SearchBean searchBean) {
        proxyFactoryService.personCharacteristicsProxySetup()
        return proxyFactoryService.personCharacteristicsProxy.autoCompletePersonCharacteristics(searchBean)
    }

    @Override
    PersonCharacteristicsCommand savePersonCharacteristics(PersonCharacteristicsCommand personCharacteristicsCommand) {
        proxyFactoryService.personCharacteristicsProxySetup()
        return proxyFactoryService.personCharacteristicsProxy.savePersonCharacteristics(personCharacteristicsCommand)
    }

    @Override
    DeleteBean deletePersonCharacteristics(DeleteBean deleteBean) {
        proxyFactoryService.personCharacteristicsProxySetup()
        return proxyFactoryService.personCharacteristicsProxy.deletePersonCharacteristics(deleteBean)
    }
}