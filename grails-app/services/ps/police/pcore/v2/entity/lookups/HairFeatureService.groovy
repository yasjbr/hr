package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.HairFeatureCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.HairFeatureDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IHairFeature

@Transactional
class HairFeatureService implements  IHairFeature {

    ProxyFactoryService proxyFactoryService

    @Override
    HairFeatureDTO getHairFeature(SearchBean searchBean){
        proxyFactoryService.hairFeatureProxySetup()
        return proxyFactoryService.hairFeatureProxy.getHairFeature(searchBean)
    }

    @Override
    PagedList<HairFeatureDTO> searchHairFeature(SearchBean searchBean){
        PagedList<HairFeatureDTO> pagedList
        try{
            proxyFactoryService.hairFeatureProxySetup()
            pagedList = proxyFactoryService.hairFeatureProxy.searchHairFeature(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteHairFeature(SearchBean searchBean){
        proxyFactoryService.hairFeatureProxySetup()
        return proxyFactoryService.hairFeatureProxy.autoCompleteHairFeature(searchBean)
    }

    @Override
    HairFeatureCommand saveHairFeature(HairFeatureCommand hairFeatureCommand){
        proxyFactoryService.hairFeatureProxySetup()
        return proxyFactoryService.hairFeatureProxy.saveHairFeature(hairFeatureCommand)
    }

    @Override
    DeleteBean deleteHairFeature(DeleteBean deleteBean){
        proxyFactoryService.hairFeatureProxySetup()
        return proxyFactoryService.hairFeatureProxy.deleteHairFeature(deleteBean)
    }
}
