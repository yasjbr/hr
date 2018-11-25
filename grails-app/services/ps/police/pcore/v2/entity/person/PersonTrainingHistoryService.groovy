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
import ps.police.pcore.v2.entity.person.commands.v1.PersonTrainingHistoryCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonTrainingHistoryDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPersonTrainingHistory

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
class PersonTrainingHistoryService implements IPersonTrainingHistory{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService


    public static getTrainerName={ formatService, PersonTrainingHistoryDTO dataRow, object, params->
        if(dataRow){
            if(dataRow.trainer){
                return  dataRow.trainer.toString()
            }else{
                return dataRow.trainerName
            }
        }
        return  ""
    }
   public static getOrganizationName={formatService,PersonTrainingHistoryDTO dataRow, object, params->
        if(dataRow){
            if(dataRow.organization){
                return  dataRow.organization.toString()
            }else{
                return dataRow.instituteName
            }
        }
        return  ""
    }

    public static getPersonId ={ formatService, PersonTrainingHistoryDTO dataRow, object, params->
        if(dataRow){
            return dataRow?.trainer?.id?.toString()
        }
        return  ""
    }

    public static getCountryName ={ formatService, PersonTrainingHistoryDTO dataRow, object, params->
        if(dataRow){
            return dataRow?.location?.country?.descriptionInfo?.localName?.toString()
        }
        return  ""
    }


    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "trainee", type: "Person", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "trainingCategory", type: "TrainingCategory", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "trainingDegree", type: "TrainingDegree", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "trainingName", type: "String", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "trainerObject", type: getTrainerName, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "organizationObject", type: getOrganizationName, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "trainingFromDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "trainingToDate", type: "ZonedDate", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingCategory", type: "TrainingCategory", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "trainingName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingFromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingToDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "organizationObject", type: getOrganizationName, source: 'domain'],
            [sort: true, search: true, hidden: false, name: "trainerObject", type: getTrainerName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingDegree", type: "TrainingDegree", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "personId", type: getPersonId, source: 'domain'],
    ]
    public static final List<String> DOMAIN_REPORT_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "location.country.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingCategory", type: "TrainingCategory", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "note", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "trainingName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingFromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingToDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "instituteName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "trainerName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "trainingDegree", type: "TrainingDegree", source: 'domain'],
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
    PersonTrainingHistoryDTO getPersonTrainingHistory(SearchBean searchBean) {
        proxyFactoryService.personTrainingHistoryProxySetup()
        return proxyFactoryService.personTrainingHistoryProxy.getPersonTrainingHistory(searchBean)
    }

    @Override
    PagedList<PersonTrainingHistoryDTO> searchPersonTrainingHistory(SearchBean searchBean) {
        proxyFactoryService.personTrainingHistoryProxySetup()
        return proxyFactoryService.personTrainingHistoryProxy.searchPersonTrainingHistory(searchBean)
    }

    List<PersonTrainingHistoryDTO> searchReport(GrailsParameterMap params) {
        List<PersonTrainingHistoryDTO> resultList  = this.searchPersonTrainingHistory(PCPUtils.convertParamsToSearchBean(params)).resultList
        resultList.each { PersonTrainingHistoryDTO personTrainingHistoryDTO ->

            if(personTrainingHistoryDTO.trainer){
                personTrainingHistoryDTO.trainerName = personTrainingHistoryDTO.trainer.toString()
            }
            if(personTrainingHistoryDTO.organization){
                personTrainingHistoryDTO.instituteName = personTrainingHistoryDTO.organization.toString()
            }

        }
        return resultList
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<PersonTrainingHistoryDTO> pagedResultList = new PagedResultList<PersonTrainingHistoryDTO>()
        pagedResultList.resultList = this.searchPersonTrainingHistory(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompletePersonTrainingHistory(SearchBean searchBean) {
        proxyFactoryService.personTrainingHistoryProxySetup()
        return proxyFactoryService.personTrainingHistoryProxy.autoCompletePersonTrainingHistory(searchBean)
    }

    @Override
    PersonTrainingHistoryCommand savePersonTrainingHistory(PersonTrainingHistoryCommand personTrainingHistoryCommand) {
        proxyFactoryService.personTrainingHistoryProxySetup()
        return proxyFactoryService.personTrainingHistoryProxy.savePersonTrainingHistory(personTrainingHistoryCommand)
    }

    @Override
    DeleteBean deletePersonTrainingHistory(DeleteBean deleteBean) {
        proxyFactoryService.personTrainingHistoryProxySetup()
        return proxyFactoryService.personTrainingHistoryProxy.deletePersonTrainingHistory(deleteBean)
    }
}