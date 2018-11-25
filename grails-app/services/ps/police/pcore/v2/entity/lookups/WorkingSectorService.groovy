package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.WorkingSectorCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.WorkingSectorDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IWorkingSector

@Transactional
class WorkingSectorService implements IWorkingSector{


    ProxyFactoryService proxyFactoryService

    @Override
    WorkingSectorDTO getWorkingSector(SearchBean searchBean){
        proxyFactoryService.workingSectorProxySetup()
        return proxyFactoryService.workingSectorProxy.getWorkingSector(searchBean)
    }

    @Override
    PagedList<WorkingSectorDTO> searchWorkingSector(SearchBean searchBean){
        PagedList<WorkingSectorDTO> pagedList
        try{
            proxyFactoryService.workingSectorProxySetup()
            pagedList = proxyFactoryService.workingSectorProxy.searchWorkingSector(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteWorkingSector(SearchBean searchBean){
        proxyFactoryService.workingSectorProxySetup()
        return proxyFactoryService.workingSectorProxy.autoCompleteWorkingSector(searchBean)
    }

    @Override
    WorkingSectorCommand saveWorkingSector(WorkingSectorCommand OrganizationCommand){
        proxyFactoryService.workingSectorProxySetup()
        return proxyFactoryService.workingSectorProxy.saveWorkingSector(OrganizationCommand)
    }

    @Override
    DeleteBean deleteWorkingSector(DeleteBean deleteBean){
        proxyFactoryService.workingSectorProxySetup()
        return proxyFactoryService.workingSectorProxy.deleteWorkingSector(deleteBean)
    }
}
