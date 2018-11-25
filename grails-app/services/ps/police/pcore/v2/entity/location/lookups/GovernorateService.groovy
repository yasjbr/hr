package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.GovernorateCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IGovernorate

@Transactional
class GovernorateService implements IGovernorate {


    ProxyFactoryService proxyFactoryService

    @Override
    GovernorateDTO getGovernorate(SearchBean searchBean){
        proxyFactoryService.governorateProxySetup()
        return proxyFactoryService.governorateProxy.getGovernorate(searchBean)
    }

    @Override
    PagedList<GovernorateDTO> searchGovernorate(SearchBean searchBean){
        PagedList<GovernorateDTO> pagedList
        try{
            proxyFactoryService.governorateProxySetup()
            pagedList = proxyFactoryService.governorateProxy.searchGovernorate(searchBean)
        }
        catch (Exception e){

        }
        return pagedList
    }

    @Override
    String autoCompleteGovernorate(SearchBean searchBean){
        proxyFactoryService.governorateProxySetup()
        return proxyFactoryService.governorateProxy.autoCompleteGovernorate(searchBean)
    }

    @Override
    GovernorateCommand saveGovernorate(GovernorateCommand OrganizationCommand){
        proxyFactoryService.governorateProxySetup()
        return proxyFactoryService.governorateProxy.saveGovernorate(OrganizationCommand)
    }

    @Override
    DeleteBean deleteGovernorate(DeleteBean deleteBean){
        proxyFactoryService.governorateProxySetup()
        return proxyFactoryService.governorateProxy.deleteGovernorate(deleteBean)
    }
}
