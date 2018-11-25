package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.EthnicityCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.EthnicityDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IEthnicity

@Transactional
class EthnicityService implements IEthnicity {

    ProxyFactoryService proxyFactoryService

    @Override
    EthnicityDTO getEthnicity(SearchBean searchBean){
        proxyFactoryService.ethnicityProxySetup()
        return proxyFactoryService.ethnicityProxy.getEthnicity(searchBean)
    }

    @Override
    PagedList<EthnicityDTO> searchEthnicity(SearchBean searchBean){
        PagedList<EthnicityDTO> pagedList
        try{
            proxyFactoryService.ethnicityProxySetup()
            pagedList = proxyFactoryService.ethnicityProxy.searchEthnicity(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteEthnicity(SearchBean searchBean){
        proxyFactoryService.ethnicityProxySetup()
        return proxyFactoryService.ethnicityProxy.autoCompleteEthnicity(searchBean)
    }

    @Override
    EthnicityCommand saveEthnicity(EthnicityCommand ethnicityCommand){

        proxyFactoryService.ethnicityProxySetup()
        return proxyFactoryService.ethnicityProxy.saveBlock(ethnicityCommand)
    }

    @Override
    DeleteBean deleteEthnicity(DeleteBean deleteBean){
        proxyFactoryService.ethnicityProxySetup()
        return proxyFactoryService.ethnicityProxy.deleteBlock(deleteBean)
    }
}
