package ps.police.pcore.v2.entity.person

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.ProxyFactoryService;
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.enums.v1.ContactInfoClassification
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.person.commands.v1.ContactInfoCommand
import ps.police.pcore.v2.entity.person.dtos.v1.ContactInfoDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IContactInfo

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
class ContactInfoService implements IContactInfo{

    MessageSource messageSource
    def formatService
    ProxyFactoryService proxyFactoryService

    /**
     * to control model columns when processing model operations.
     * @return List<String>.
     */

    public static getOwnerName={ formatService, ContactInfoCommand dataRow, object, params->
        if(dataRow){
            if(dataRow.relatedObjectType == ContactInfoClassification.PERSON){
                return   "<a href ='../person/show/${dataRow?.person?.id}'>${dataRow?.person?.toString()}</a>";
            }
            if(dataRow.relatedObjectType == ContactInfoClassification.ORGANIZATION){
                return   "<a href ='/organization/show/${dataRow?.organization?.id}'>${dataRow?.organization?.toString()}</a>";
            }
        }
        return  ""
    }
    public static getContactInfoDescription={formatService,ContactInfoDTO dataRow, object, params->
        if(dataRow){
            if(dataRow.address){
                if(dataRow.value && !"null".equalsIgnoreCase(dataRow.value)){
                    return   dataRow.address.toString() +" - "+dataRow.value
                }else{
                    return   dataRow.address.toString()

                }
            }else {
                return dataRow.value.toString()
            }
        }
        return  ""
    }
    public static final List<String> DOMAIN_COLUMNS = [
        [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
        [sort: true, search: true, hidden: false, name: "relatedObjectType", type: "enum", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "ownerName", type: getOwnerName, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "contactMethod", type: "ContactMethod", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "contactType", type: 'string', source: 'domain'],
        [sort: true, search: true, hidden: false, name: "contactInfoDescription", type: getContactInfoDescription, source: 'domain'],
        [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
        [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
    ]


    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "contactType", type: 'string', source: 'domain'],
            [sort: true, search: false, hidden: false, name: "contactMethod", type: "ContactMethod", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "contactInfoDescription", type: getContactInfoDescription, source: 'domain'],
//            [sort: true, search: false, hidden: false, name: "fromDate", type: "ZonedDate", source: 'domain'],
//            [sort: true, search: false, hidden: false, name: "toDate", type: "ZonedDate", source: 'domain'],
    ]

    public static final List<String> DOMAIN_TAB_COLUMNS_ADDRESS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "contactType", type: 'string', source: 'domain'],
            [sort: true, search: false, hidden: false, name: "contactMethod", type: "ContactMethod", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "contactInfoDescription", type: getContactInfoDescription, source: 'domain'],
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
    ContactInfoDTO getContactInfo(SearchBean searchBean) {
        proxyFactoryService.contactInfoProxySetup()
        return proxyFactoryService.contactInfoProxy.getContactInfo(searchBean)
    }

    @Override
    PagedList<ContactInfoDTO> searchContactInfo(SearchBean searchBean) {
        proxyFactoryService.contactInfoProxySetup()
        return proxyFactoryService.contactInfoProxy.searchContactInfo(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<ContactInfoDTO> pagedResultList = new PagedResultList<ContactInfoDTO>()
        pagedResultList.resultList = this.searchContactInfo(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompleteContactInfo(SearchBean searchBean) {
        proxyFactoryService.contactInfoProxySetup()
        return proxyFactoryService.contactInfoProxy.autoCompleteContactInfo(searchBean)
    }

    @Override
    ContactInfoCommand saveContactInfo(ContactInfoCommand contactInfoCommand) {
        proxyFactoryService.contactInfoProxySetup()
        return proxyFactoryService.contactInfoProxy.saveContactInfo(contactInfoCommand)
    }

    @Override
    DeleteBean deleteContactInfo(DeleteBean deleteBean) {
        proxyFactoryService.contactInfoProxySetup()
        return proxyFactoryService.contactInfoProxy.deleteContactInfo(deleteBean)
    }

}