package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.AreaClassCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.AreaClassDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IAreaClass

@Transactional
class AreaClassService implements IAreaClass {


    ProxyFactoryService proxyFactoryService

    @Override
    AreaClassDTO getAreaClass(SearchBean searchBean){
        proxyFactoryService.areaClassProxySetup()
        return proxyFactoryService.areaClassProxy.getAreaClass(searchBean)
    }

    @Override
    PagedList<AreaClassDTO> searchAreaClass(SearchBean searchBean){
        PagedList<AreaClassDTO> pagedList
        try{
            proxyFactoryService.areaClassProxySetup()
            pagedList = proxyFactoryService.areaClassProxy.searchAreaClass(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteAreaClass(SearchBean searchBean){
        proxyFactoryService.areaClassProxySetup()
        return proxyFactoryService.areaClassProxy.autoCompleteAreaClass(searchBean)
    }

    @Override
    AreaClassCommand saveAreaClass(AreaClassCommand OrganizationCommand){
        proxyFactoryService.areaClassProxySetup()
        return proxyFactoryService.areaClassProxy.saveAreaClass(OrganizationCommand)
    }

    @Override
    DeleteBean deleteAreaClass(DeleteBean deleteBean){
        proxyFactoryService.areaClassProxySetup()
        return proxyFactoryService.areaClassProxy.deleteAreaClass(deleteBean)
    }
}
