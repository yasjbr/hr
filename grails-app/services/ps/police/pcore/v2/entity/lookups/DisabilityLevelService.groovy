package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.DisabilityLevelCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.DisabilityLevelDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IDisabilityLevel

@Transactional
class DisabilityLevelService implements  IDisabilityLevel {

    ProxyFactoryService proxyFactoryService

    @Override
    DisabilityLevelDTO getDisabilityLevel(SearchBean searchBean){
        proxyFactoryService.disabilityLevelProxySetup()
        return proxyFactoryService.disabilityLevelProxy.getDisabilityLevel(searchBean)
    }

    @Override
    PagedList<DisabilityLevelDTO> searchDisabilityLevel(SearchBean searchBean){
        PagedList<DisabilityLevelDTO> pagedList
        try{
            proxyFactoryService.disabilityLevelProxySetup()
            pagedList = proxyFactoryService.disabilityLevelProxy.searchDisabilityLevel(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteDisabilityLevel(SearchBean searchBean){
        proxyFactoryService.disabilityLevelProxySetup()
        return proxyFactoryService.disabilityLevelProxy.autoCompleteDisabilityLevel(searchBean)
    }

    @Override
    DisabilityLevelCommand saveDisabilityLevel(DisabilityLevelCommand disabilityLevelCommand){
        proxyFactoryService.disabilityLevelProxySetup()
        return proxyFactoryService.disabilityLevelProxy.saveDisabilityLevel(disabilityLevelCommand)
    }

    @Override
    DeleteBean deleteDisabilityLevel(DeleteBean deleteBean){
        proxyFactoryService.disabilityLevelProxySetup()
        return proxyFactoryService.disabilityLevelProxy.deleteDisabilityLevel(deleteBean)
    }
}
