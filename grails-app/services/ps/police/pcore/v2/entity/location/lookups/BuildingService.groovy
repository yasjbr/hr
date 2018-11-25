package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.BuildingCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.BuildingDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IBuilding

@Transactional
class BuildingService implements IBuilding {


    ProxyFactoryService proxyFactoryService

    @Override
    BuildingDTO getBuilding(SearchBean searchBean){
        proxyFactoryService.buildingProxySetup()
        return proxyFactoryService.buildingProxy.getBuilding(searchBean)
    }

    @Override
    PagedList<BuildingDTO> searchBuilding(SearchBean searchBean){
        PagedList<BuildingDTO> pagedList
        try{
            proxyFactoryService.buildingProxySetup()
            pagedList = proxyFactoryService.buildingProxy.searchBuilding(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteBuilding(SearchBean searchBean){
        proxyFactoryService.buildingProxySetup()
        return proxyFactoryService.buildingProxy.autoCompleteBuilding(searchBean)
    }

    @Override
    BuildingCommand saveBuilding(BuildingCommand buildingCommand){
        proxyFactoryService.buildingProxySetup()
        return proxyFactoryService.buildingProxy.saveBuilding(buildingCommand)
    }

    @Override
    DeleteBean deleteBuilding(DeleteBean deleteBean){
        proxyFactoryService.buildingProxySetup()
        return proxyFactoryService.buildingProxy.deleteBuilding(deleteBean)
    }


}
