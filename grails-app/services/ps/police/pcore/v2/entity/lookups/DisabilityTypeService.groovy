package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.DisabilityTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.DisabilityTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IDisabilityType

@Transactional
class DisabilityTypeService implements  IDisabilityType {

    ProxyFactoryService proxyFactoryService

    @Override
    DisabilityTypeDTO getDisabilityType(SearchBean searchBean){
        proxyFactoryService.disabilityTypeProxySetup()
        return proxyFactoryService.disabilityTypeProxy.getDisabilityType(searchBean)
    }

    @Override
    PagedList<DisabilityTypeDTO> searchDisabilityType(SearchBean searchBean){
        PagedList<DisabilityTypeDTO> pagedList
        try{
            proxyFactoryService.disabilityTypeProxySetup()
            pagedList = proxyFactoryService.disabilityTypeProxy.searchDisabilityType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteDisabilityType(SearchBean searchBean){
        proxyFactoryService.disabilityTypeProxySetup()
        return proxyFactoryService.disabilityTypeProxy.autoCompleteDisabilityType(searchBean)
    }

    @Override
    DisabilityTypeCommand saveDisabilityType(DisabilityTypeCommand disabilityTypeCommand){
        proxyFactoryService.disabilityTypeProxySetup()
        return proxyFactoryService.disabilityTypeProxy.saveDisabilityType(disabilityTypeCommand)
    }

    @Override
    DeleteBean deleteDisabilityType(DeleteBean deleteBean){
        proxyFactoryService.disabilityTypeProxySetup()
        return proxyFactoryService.disabilityTypeProxy.deleteDisabilityType(deleteBean)
    }
}
