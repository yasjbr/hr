package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.ReligionCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.ReligionDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IReligion

@Transactional
class ReligionService implements IReligion {


    ProxyFactoryService proxyFactoryService

    @Override
    ReligionDTO getReligion(SearchBean searchBean){
        proxyFactoryService.religionProxySetup()
        return proxyFactoryService.religionProxy.getReligion(searchBean)
    }

    @Override
    PagedList<ReligionDTO> searchReligion(SearchBean searchBean){
        PagedList<ReligionDTO> pagedList
        try{
            proxyFactoryService.religionProxySetup()
            pagedList = proxyFactoryService.religionProxy.searchReligion(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteReligion(SearchBean searchBean){

        proxyFactoryService.religionProxySetup()
        return proxyFactoryService.religionProxy.autoCompleteReligion(searchBean)
    }

    @Override
    ReligionCommand saveReligion(ReligionCommand religionCommand){

        proxyFactoryService.religionProxySetup()
        return proxyFactoryService.religionProxy.saveBlock(religionCommand)
    }

    @Override
    DeleteBean deleteReligion(DeleteBean deleteBean){
        proxyFactoryService.religionProxySetup()
        return proxyFactoryService.religionProxy.deleteBlock(deleteBean)
    }
}
