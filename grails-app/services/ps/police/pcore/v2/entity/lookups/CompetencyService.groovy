package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.person.lookups.commands.v1.CompetencyCommand
import ps.police.pcore.v2.entity.person.lookups.dtos.v1.CompetencyDTO
import ps.police.pcore.v2.entity.person.lookups.interfaces.v1.ICompetency

@Transactional
class CompetencyService implements ICompetency{

    ProxyFactoryService proxyFactoryService

    @Override
    CompetencyDTO getCompetency(SearchBean searchBean){
        proxyFactoryService.competencyProxySetup()
        return proxyFactoryService.competencyProxy.getCompetency(searchBean)
    }

    @Override
    PagedList<CompetencyDTO> searchCompetency(SearchBean searchBean){
        PagedList<CompetencyDTO> pagedList
        try{
            proxyFactoryService.competencyProxySetup()
            pagedList = proxyFactoryService.competencyProxy.searchCompetency(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteCompetency(SearchBean searchBean){
        proxyFactoryService.competencyProxySetup()
        return proxyFactoryService.competencyProxy.autoCompleteCompetency(searchBean)
    }

    @Override
    CompetencyCommand saveCompetency(CompetencyCommand OrganizationCommand){
        proxyFactoryService.competencyProxySetup()
        return proxyFactoryService.competencyProxy.saveCompetency(OrganizationCommand)
    }

    @Override
    DeleteBean deleteCompetency(DeleteBean deleteBean){
        proxyFactoryService.competencyProxySetup()
        return proxyFactoryService.competencyProxy.deleteCompetency(deleteBean)
    }

}
