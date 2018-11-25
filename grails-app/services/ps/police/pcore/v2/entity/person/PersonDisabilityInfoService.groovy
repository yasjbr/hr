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
import ps.police.pcore.v2.entity.person.commands.v1.PersonDisabilityInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonArrestHistoryDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDisabilityInfoDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonDisabilityInfo

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
class PersonDisabilityInfoService implements IPersonDisabilityInfo{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService



    public static getPercentage={ formatService, PersonDisabilityInfoDTO dataRow, object, params->
        if(dataRow){
            if(dataRow?.percentage){
                String percentage = dataRow?.percentage?.toString()
                if(percentage?.contains(".")){
                    return dataRow?.percentage?.toString()?.split("\\.")[0]+ "%"
                }else{
                    return dataRow?.percentage?.toString()+ "%"
                }
            }else {
                return ""
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
            [sort: true, search: false, hidden: false, name: "person", type: "Person", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disabilityType", type: "DisabilityType", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disabilityLevel", type: "DisabilityLevel", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "percentage", type: getPercentage, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "accommodationNeeded", type: "Boolean", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disabilityType", type: "DisabilityType", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disabilityLevel", type: "DisabilityLevel", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "percentage", type: getPercentage, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "accommodationNeeded", type: "Boolean", source: 'domain']



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
    PersonDisabilityInfoDTO getPersonDisabilityInfo(SearchBean searchBean) {
        proxyFactoryService.personDisabilityInfoProxySetup()
        return proxyFactoryService.personDisabilityInfoProxy.getPersonDisabilityInfo(searchBean)
    }

    @Override
    PagedList<PersonDisabilityInfoDTO> searchPersonDisabilityInfo(SearchBean searchBean) {
        proxyFactoryService.personDisabilityInfoProxySetup()
        return proxyFactoryService.personDisabilityInfoProxy.searchPersonDisabilityInfo(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonDisabilityInfoDTO> pagedResultList = new PagedResultList<PersonDisabilityInfoDTO>()
        pagedResultList.resultList = this.searchPersonDisabilityInfo(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonDisabilityInfo(SearchBean searchBean) {
        proxyFactoryService.personDisabilityInfoProxySetup()
        return proxyFactoryService.personDisabilityInfoProxy.autoCompletePersonDisabilityInfo(searchBean)
    }

    @Override
    PersonDisabilityInfoCommand savePersonDisabilityInfo(PersonDisabilityInfoCommand personDisabilityInfoCommand) {
        proxyFactoryService.personDisabilityInfoProxySetup()
        return proxyFactoryService.personDisabilityInfoProxy.savePersonDisabilityInfo(personDisabilityInfoCommand)
    }

    @Override
    DeleteBean deletePersonDisabilityInfo(DeleteBean deleteBean) {
        proxyFactoryService.personDisabilityInfoProxySetup()
        return proxyFactoryService.personDisabilityInfoProxy.deletePersonDisabilityInfo(deleteBean)
    }
}