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
import ps.police.pcore.v2.entity.person.commands.v1.PersonEmploymentHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonEmploymentHistoryDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonEmploymentHistory

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
class PersonEmploymentHistoryService implements IPersonEmploymentHistory{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService



    public static getOrganizationName={formatService,PersonEmploymentHistoryDTO dataRow, object, params->
        if(dataRow){
            if(dataRow.organization){
                return   dataRow.organization.toString()
            }else {
                return dataRow.organizationName

            }
        }
        return  ""
    }

    public static getPersonId ={ formatService, PersonEmploymentHistoryDTO dataRow, object, params->
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
        [sort: true, search: true, hidden: false, name: "jobDescription", type: "String", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "professionType", type: "ProfessionType", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "organizationObject", type: getOrganizationName, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "location", type: "Location", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "jobDescription", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "professionType", type: "ProfessionType", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "organizationObject", type: getOrganizationName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "location", type: "Location", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
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
    PersonEmploymentHistoryDTO getPersonEmploymentHistory(SearchBean searchBean) {
        proxyFactoryService.personEmploymentHistoryProxySetup()
        return proxyFactoryService.personEmploymentHistoryProxy.getPersonEmploymentHistory(searchBean)
    }

    @Override
    PagedList<PersonEmploymentHistoryDTO> searchPersonEmploymentHistory(SearchBean searchBean) {
        proxyFactoryService.personEmploymentHistoryProxySetup()
        return proxyFactoryService.personEmploymentHistoryProxy.searchPersonEmploymentHistory(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonEmploymentHistoryDTO> pagedResultList = new PagedResultList<PersonEmploymentHistoryDTO>()
        pagedResultList.resultList = this.searchPersonEmploymentHistory(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonEmploymentHistory(SearchBean searchBean) {
        proxyFactoryService.personEmploymentHistoryProxySetup()
        return proxyFactoryService.personEmploymentHistoryProxy.autoCompletePersonEmploymentHistory(searchBean)
    }

    @Override
    PersonEmploymentHistoryCommand savePersonEmploymentHistory(PersonEmploymentHistoryCommand personEmploymentHistoryCommand) {
        proxyFactoryService.personEmploymentHistoryProxySetup()
        return proxyFactoryService.personEmploymentHistoryProxy.savePersonEmploymentHistory(personEmploymentHistoryCommand)
    }

    @Override
    DeleteBean deletePersonEmploymentHistory(DeleteBean deleteBean) {
        proxyFactoryService.personEmploymentHistoryProxySetup()
        return proxyFactoryService.personEmploymentHistoryProxy.deletePersonEmploymentHistory(deleteBean)
    }
}