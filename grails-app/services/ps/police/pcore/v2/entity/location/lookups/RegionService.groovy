package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.RegionCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.RegionDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IRegion

@Transactional
class RegionService  implements  IRegion{
    ProxyFactoryService proxyFactoryService

    @Override
    RegionDTO getRegion(SearchBean searchBean){
        proxyFactoryService.regionProxySetup()
        return proxyFactoryService.regionProxy.getRegion(searchBean)
    }

    @Override
    PagedList<RegionDTO> searchRegion(SearchBean searchBean){
        PagedList<RegionDTO> pagedList
        try{
            proxyFactoryService.regionProxySetup()
            pagedList = proxyFactoryService.regionProxy.searchRegion(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteRegion(SearchBean searchBean){
        proxyFactoryService.regionProxySetup()
        return proxyFactoryService.regionProxy.autoCompleteRegion(searchBean)
    }

    @Override
    RegionCommand saveRegion(RegionCommand OrganizationCommand){
        proxyFactoryService.regionProxySetup()
        return proxyFactoryService.regionProxy.saveRegion(OrganizationCommand)
    }

    @Override
    DeleteBean deleteRegion(DeleteBean deleteBean){
        proxyFactoryService.regionProxySetup()
        return proxyFactoryService.regionProxy.deleteRegion(deleteBean)
    }
}
