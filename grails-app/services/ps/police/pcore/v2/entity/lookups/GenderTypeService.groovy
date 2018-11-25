package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.GenderTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.GenderTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IGenderType

@Transactional
class GenderTypeService implements IGenderType {


    ProxyFactoryService proxyFactoryService

    @Override
    GenderTypeDTO getGenderType(SearchBean searchBean){
        proxyFactoryService.genderTypeProxySetup()
        return proxyFactoryService.genderTypeProxy.getGenderType(searchBean)
    }

    @Override
    PagedList<GenderTypeDTO> searchGenderType(SearchBean searchBean){
        PagedList<GenderTypeDTO> pagedList
        try{
            proxyFactoryService.genderTypeProxySetup()
            pagedList = proxyFactoryService.genderTypeProxy.searchGenderType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteGenderType(SearchBean searchBean){
        proxyFactoryService.genderTypeProxySetup()
        return proxyFactoryService.genderTypeProxy.autoCompleteGenderType(searchBean)
    }

    @Override
    GenderTypeCommand saveGenderType(GenderTypeCommand genderTypeCommand){

        proxyFactoryService.genderTypeProxySetup()
        return proxyFactoryService.genderTypeProxy.saveBlock(genderTypeCommand)
    }

    @Override
    DeleteBean deleteGenderType(DeleteBean deleteBean){
        proxyFactoryService.genderTypeProxySetup()
        return proxyFactoryService.genderTypeProxy.deleteBlock(deleteBean)
    }
}
