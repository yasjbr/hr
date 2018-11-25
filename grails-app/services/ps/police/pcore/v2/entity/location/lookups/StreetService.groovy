package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.StreetCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.StreetDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IStreet

@Transactional
class StreetService implements IStreet {

    ProxyFactoryService proxyFactoryService

    @Override
    StreetDTO getStreet(SearchBean searchBean){
        proxyFactoryService.streetProxySetup()
        return proxyFactoryService.streetProxy.getStreet(searchBean)
    }

    @Override
    PagedList<StreetDTO> searchStreet(SearchBean searchBean){
        PagedList<StreetDTO> pagedList
        try{
            proxyFactoryService.streetProxySetup()
            pagedList = proxyFactoryService.streetProxy.searchStreet(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteStreet(SearchBean searchBean){
        proxyFactoryService.streetProxySetup()
        return proxyFactoryService.streetProxy.autoCompleteStreet(searchBean)
    }

    @Override
    StreetCommand saveStreet(StreetCommand OrganizationCommand){
        proxyFactoryService.streetProxySetup()
        return proxyFactoryService.streetProxy.saveStreet(OrganizationCommand)
    }

    @Override
    DeleteBean deleteStreet(DeleteBean deleteBean){
        proxyFactoryService.streetProxySetup()
        return proxyFactoryService.streetProxy.deleteStreet(deleteBean)
    }

}
