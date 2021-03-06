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
import ps.police.pcore.v2.entity.person.commands.v1.PersonRelationShipsCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonRelationShips

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
class PersonRelationShipsService implements IPersonRelationShips{

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
        [sort: true, search: false, hidden: false, name: "relatedPerson", type: "Person", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "relationshipType", type: "RelationshipType", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "isDependent", type: "Boolean", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relatedPerson", type: "Person", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relationshipType", type: "RelationshipType", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "isDependent", type: "Boolean", source: 'domain'],
    ]

    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relatedPerson", type: "Person", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relationshipType", type: "RelationshipType", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relatedPerson.dateOfBirth", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relatedPerson.recentCardNo", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "relatedPerson.localMotherName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "isDependent", type: "Boolean", source: 'domain'],
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
    PersonRelationShipsDTO getPersonRelationShips(SearchBean searchBean) {
        proxyFactoryService.personRelationShipsProxySetup()
        return proxyFactoryService.personRelationShipsProxy.getPersonRelationShips(searchBean)
    }

    @Override
    PagedList<PersonRelationShipsDTO> searchPersonRelationShips(SearchBean searchBean) {
        proxyFactoryService.personRelationShipsProxySetup()
        return proxyFactoryService.personRelationShipsProxy.searchPersonRelationShips(searchBean)
    }

    List<PersonRelationShipsDTO> searchReport(GrailsParameterMap params) {
        return this.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(params)).resultList
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonRelationShipsDTO> pagedResultList = new PagedResultList<PersonRelationShipsDTO>()
        pagedResultList.resultList = this.searchPersonRelationShips(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonRelationShips(SearchBean searchBean) {
        proxyFactoryService.personRelationShipsProxySetup()
        return proxyFactoryService.personRelationShipsProxy.autoCompletePersonRelationShips(searchBean)
    }

    @Override
    PersonRelationShipsCommand savePersonRelationShips(PersonRelationShipsCommand personRelationShipsCommand) {
        proxyFactoryService.personRelationShipsProxySetup()
        return proxyFactoryService.personRelationShipsProxy.savePersonRelationShips(personRelationShipsCommand)
    }

    @Override
    DeleteBean deletePersonRelationShips(DeleteBean deleteBean) {
        proxyFactoryService.personRelationShipsProxySetup()
        return proxyFactoryService.personRelationShipsProxy.deletePersonRelationShips(deleteBean)
    }
}