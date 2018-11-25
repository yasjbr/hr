package ps.police.pcore.v2.entity.organization.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.organization.lookups.commands.v1.CorporationClassificationCommand
import ps.police.pcore.v2.entity.organization.lookups.dtos.v1.CorporationClassificationDTO
import ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.ICorporationClassification

@Transactional
class CorporationClassificationService implements ICorporationClassification {


    ProxyFactoryService proxyFactoryService

    @Override
    CorporationClassificationDTO getCorporationClassification(SearchBean searchBean){
        proxyFactoryService.corporationClassificationProxySetup()
        return proxyFactoryService.corporationClassificationProxy.getCorporationClassification(searchBean)
    }

    @Override
    PagedList<CorporationClassificationDTO> searchCorporationClassification(SearchBean searchBean){
        PagedList<CorporationClassificationDTO> pagedList
        try{
            proxyFactoryService.corporationClassificationProxySetup()
            pagedList = proxyFactoryService.corporationClassificationProxy.searchCorporationClassification(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteCorporationClassification(SearchBean searchBean){
        proxyFactoryService.corporationClassificationProxySetup()
        return proxyFactoryService.corporationClassificationProxy.autoCompleteCorporationClassification(searchBean)
    }

    @Override
    CorporationClassificationCommand saveCorporationClassification(CorporationClassificationCommand OrganizationCommand){
        proxyFactoryService.corporationClassificationProxySetup()
        return proxyFactoryService.corporationClassificationProxy.saveCorporationClassification(OrganizationCommand)
    }

    @Override
    DeleteBean deleteCorporationClassification(DeleteBean deleteBean){
        proxyFactoryService.corporationClassificationProxySetup()
        return proxyFactoryService.corporationClassificationProxy.deleteCorporationClassification(deleteBean)
    }
}
