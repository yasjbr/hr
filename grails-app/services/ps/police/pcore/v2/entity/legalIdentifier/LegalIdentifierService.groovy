package ps.police.pcore.v2.entity.legalIdentifier

import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.enums.v1.RelatedPartyEnum
import ps.police.pcore.v2.entity.legalIdentifier.commands.v1.LegalIdentifierCommand
import ps.police.pcore.v2.entity.legalIdentifier.dtos.v1.LegalIdentifierDTO
import ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifier


@Transactional
class LegalIdentifierService implements ILegalIdentifier {

    ProxyFactoryService proxyFactoryService
    FormatService formatService



    public static getOwnerName={formatService,LegalIdentifierDTO dataRow, object, params->
        if(dataRow){
            if(dataRow.documentOwner == RelatedPartyEnum.PERSON){
                return   "<a href ='../person/show/${dataRow?.ownerPerson?.id}'>${dataRow?.ownerPerson?.toString()}</a>";
            }
            if(dataRow.documentOwner == RelatedPartyEnum.ORGANIZATION){
                return   "<a href ='/organization/show/${dataRow?.ownerOrganization?.id}'>${dataRow?.ownerOrganization?.toString()}</a>";
            }
        }
        return  ""
    }


    public static getPersonId={formatService,LegalIdentifierDTO dataRow, object, params->
        if(dataRow) {
            return dataRow?.ownerPerson?.id?.toString()
        }
        return  ""
    }


    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "documentNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "documentOwner", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "ownerName", type: getOwnerName, source: 'domain'],
            [sort: true, search: false, hidden: false, name: "documentTypeClassification", type: "JoinedDocumentTypeClassification", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "legalIdentifierLevel", type: "LegalIdentifierLevel", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "issuedByOrganization", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "issuingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "validFrom", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "validTo", type: "ZonedDate", source: 'domain'],

    ]
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "documentNumber", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "documentTypeClassification", type: "JoinedDocumentTypeClassification", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "legalIdentifierLevel", type: "LegalIdentifierLevel", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "issuedByOrganization", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "issuingDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "validFrom", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "validTo", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: true, name: "personId", type: getPersonId, source: 'domain'],


    ]

    @Override
    LegalIdentifierDTO getLegalIdentifier(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierProxySetup()
        return proxyFactoryService.legalIdentifierProxy.getLegalIdentifier(searchBean)
    }

    @Override
    PagedList<LegalIdentifierDTO> searchLegalIdentifier(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierProxySetup()
        return proxyFactoryService.legalIdentifierProxy.searchLegalIdentifier(searchBean)
    }

    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {
        PagedResultList<LegalIdentifierDTO> pagedResultList = new PagedResultList<LegalIdentifierDTO>()
        pagedResultList.resultList = this.searchLegalIdentifier(PCPUtils.convertParamsToSearchBean(params)).resultList
        return pagedResultList
    }

    @Override
    String autoCompleteLegalIdentifier(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierProxySetup()
        return proxyFactoryService.legalIdentifierProxy.autoCompleteLegalIdentifier(searchBean)
    }

    @Override
    LegalIdentifierCommand saveLegalIdentifier(LegalIdentifierCommand legalIdentifierCommand) {
        proxyFactoryService.legalIdentifierProxySetup()
        return proxyFactoryService.legalIdentifierProxy.saveLegalIdentifier(legalIdentifierCommand)
    }

    @Override
    DeleteBean deleteLegalIdentifier(DeleteBean deleteBean) {
        proxyFactoryService.legalIdentifierProxySetup()
        return proxyFactoryService.legalIdentifierProxy.deleteLegalIdentifier(deleteBean)
    }


    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List<String> DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     * @see ps.police.common.beans.v1.PagedList.
     * @see PagedList.
     */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
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
}