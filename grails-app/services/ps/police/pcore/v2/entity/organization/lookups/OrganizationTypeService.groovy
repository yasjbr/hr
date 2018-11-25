package ps.police.pcore.v2.entity.organization.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.organization.lookups.commands.v1.OrganizationTypeCommand
import ps.police.pcore.v2.entity.organization.lookups.dtos.v1.OrganizationTypeDTO
import ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.IOrganizationType

@Transactional
class OrganizationTypeService implements  IOrganizationType{

    ProxyFactoryService proxyFactoryService

    @Override
    OrganizationTypeDTO getOrganizationType(SearchBean searchBean){
        proxyFactoryService.organizationTypeProxySetup()
        return proxyFactoryService.organizationTypeProxy.getOrganizationType(searchBean)
    }

    @Override
    PagedList<OrganizationTypeDTO> searchOrganizationType(SearchBean searchBean){
        PagedList<OrganizationTypeDTO> pagedList
        try{
            proxyFactoryService.organizationTypeProxySetup()
            pagedList = proxyFactoryService.organizationTypeProxy.searchOrganizationType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteOrganizationType(SearchBean searchBean){
        proxyFactoryService.organizationTypeProxySetup()
        return proxyFactoryService.organizationTypeProxy.autoCompleteOrganizationType(searchBean)
    }

    @Override
    OrganizationTypeCommand saveOrganizationType(OrganizationTypeCommand OrganizationCommand){
        proxyFactoryService.organizationTypeProxySetup()
        return proxyFactoryService.organizationTypeProxy.saveOrganizationType(OrganizationCommand)
    }

    @Override
    DeleteBean deleteOrganizationType(DeleteBean deleteBean){
        proxyFactoryService.organizationTypeProxySetup()
        return proxyFactoryService.organizationTypeProxy.deleteOrganizationType(deleteBean)
    }
}
