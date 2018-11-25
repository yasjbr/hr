package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.BloodTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.BloodTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IBloodType

@Transactional
class BloodTypeService implements IBloodType {


    ProxyFactoryService proxyFactoryService

    @Override
    BloodTypeDTO getBloodType(SearchBean searchBean){
        proxyFactoryService.bloodTypeProxySetup()
        return proxyFactoryService.bloodTypeProxy.getBloodType(searchBean)
    }

    @Override
    PagedList<BloodTypeDTO> searchBloodType(SearchBean searchBean){
        PagedList<BloodTypeDTO> pagedList
        try{
            proxyFactoryService.bloodTypeProxySetup()
            pagedList = proxyFactoryService.bloodTypeProxy.searchBloodType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteBloodType(SearchBean searchBean){
        proxyFactoryService.bloodTypeProxySetup()
        return proxyFactoryService.bloodTypeProxy.autoCompleteBloodType(searchBean)
    }

    @Override
    BloodTypeCommand saveBloodType(BloodTypeCommand bloodTypeCommand){
        proxyFactoryService.bloodTypeProxySetup()
        return proxyFactoryService.bloodTypeProxy.saveBlock(bloodTypeCommand)
    }

    @Override
    DeleteBean deleteBloodType(DeleteBean deleteBean){
        proxyFactoryService.bloodTypeProxySetup()
        return proxyFactoryService.bloodTypeProxy.deleteBlock(deleteBean)
    }
}
