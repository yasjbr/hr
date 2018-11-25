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
import ps.police.pcore.v2.entity.person.commands.v1.PersonNationalityCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonNationalityDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonNationality

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
class PersonNationalityService implements IPersonNationality{

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
        [sort: true, search: false, hidden: false, name: "granterCountry", type: "Country", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "acquisitionMethod", type: "NationalityAcquisitionMethod", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "acquiredDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "expiryDate", type: "ZonedDate", source: 'domain'],
    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "granterCountry", type: "Country", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "acquisitionMethod", type: "NationalityAcquisitionMethod", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "acquiredDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "expiryDate", type: "ZonedDate", source: 'domain'],
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
    PersonNationalityDTO getPersonNationality(SearchBean searchBean) {
        proxyFactoryService.personNationalityProxySetup()
        return proxyFactoryService.personNationalityProxy.getPersonNationality(searchBean)
    }

    @Override
    PagedList<PersonNationalityDTO> searchPersonNationality(SearchBean searchBean) {
        proxyFactoryService.personNationalityProxySetup()
        return proxyFactoryService.personNationalityProxy.searchPersonNationality(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonNationalityDTO> pagedResultList = new PagedResultList<PersonNationalityDTO>()
        pagedResultList.resultList = this.searchPersonNationality(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonNationality(SearchBean searchBean) {
        proxyFactoryService.personNationalityProxySetup()
        return proxyFactoryService.personNationalityProxy.autoCompletePersonNationality(searchBean)
    }

    @Override
    PersonNationalityCommand savePersonNationality(PersonNationalityCommand personNationalityCommand) {
        proxyFactoryService.personNationalityProxySetup()
        return proxyFactoryService.personNationalityProxy.savePersonNationality(personNationalityCommand)
    }

    @Override
    DeleteBean deletePersonNationality(DeleteBean deleteBean) {
        proxyFactoryService.personNationalityProxySetup()
        return proxyFactoryService.personNationalityProxy.deletePersonNationality(deleteBean)
    }

}