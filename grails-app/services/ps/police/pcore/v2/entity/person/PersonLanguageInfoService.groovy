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
import ps.police.pcore.v2.entity.person.commands.v1.PersonLanguageInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonLanguageInfoDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonLanguageInfo

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
class PersonLanguageInfoService implements IPersonLanguageInfo{

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
        [sort: true, search: false, hidden: false, name: "language", type: "Language", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "readingLevel", type: "EducationLevel", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "verbalLevel", type: "EducationLevel", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "writingLevel", type: "EducationLevel", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "isMother", type: "Boolean", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "language", type: "Language", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "readingLevel", type: "EducationLevel", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "verbalLevel", type: "EducationLevel", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "writingLevel", type: "EducationLevel", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "isMother", type: "Boolean", source: 'domain'],
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
    PersonLanguageInfoDTO getPersonLanguageInfo(SearchBean searchBean) {
        proxyFactoryService.personLanguageInfoProxySetup()
        return proxyFactoryService.personLanguageInfoProxy.getPersonLanguageInfo(searchBean)
    }

    @Override
    PagedList<PersonLanguageInfoDTO> searchPersonLanguageInfo(SearchBean searchBean) {
        proxyFactoryService.personLanguageInfoProxySetup()
        return proxyFactoryService.personLanguageInfoProxy.searchPersonLanguageInfo(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonLanguageInfoDTO> pagedResultList = new PagedResultList<PersonLanguageInfoDTO>()
        pagedResultList.resultList = this.searchPersonLanguageInfo(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonLanguageInfo(SearchBean searchBean) {
        proxyFactoryService.personLanguageInfoProxySetup()
        return proxyFactoryService.personLanguageInfoProxy.autoCompletePersonLanguageInfo(searchBean)
    }

    @Override
    PersonLanguageInfoCommand savePersonLanguageInfo(PersonLanguageInfoCommand personLanguageInfoCommand) {
        proxyFactoryService.personLanguageInfoProxySetup()
        return proxyFactoryService.personLanguageInfoProxy.savePersonLanguageInfo(personLanguageInfoCommand)
    }

    @Override
    DeleteBean deletePersonLanguageInfo(DeleteBean deleteBean) {
        proxyFactoryService.personLanguageInfoProxySetup()
        return proxyFactoryService.personLanguageInfoProxy.deletePersonLanguageInfo(deleteBean)
    }
}