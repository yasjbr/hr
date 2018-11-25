package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.LocalityCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.LocalityDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.ILocality

@Transactional
class LocalityService implements  ILocality {

    ProxyFactoryService proxyFactoryService


    @Override
    LocalityDTO getLocality(SearchBean searchBean){
        proxyFactoryService.localityProxySetup()
        return proxyFactoryService.localityProxy.getLocality(searchBean)
    }

    @Override
    PagedList<LocalityDTO> searchLocality(SearchBean searchBean){
        PagedList<LocalityDTO> pagedList
        try{
            proxyFactoryService.localityProxySetup()
            pagedList = proxyFactoryService.localityProxy.searchLocality(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteLocality(SearchBean searchBean){
        proxyFactoryService.localityProxySetup()
        return proxyFactoryService.localityProxy.autoCompleteLocality(searchBean)
    }

    @Override
    LocalityCommand saveLocality(LocalityCommand OrganizationCommand){
        proxyFactoryService.localityProxySetup()
        return proxyFactoryService.localityProxy.saveLocality(OrganizationCommand)
    }

    @Override
    DeleteBean deleteLocality(DeleteBean deleteBean){
        proxyFactoryService.localityProxySetup()
        return proxyFactoryService.localityProxy.deleteLocality(deleteBean)
    }
}
