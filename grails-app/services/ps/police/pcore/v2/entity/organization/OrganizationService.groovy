package ps.police.pcore.v2.entity.organization

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.organization.commands.v1.OrganizationCommand
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.organization.interfaces.v1.IOrganization

@Transactional
class OrganizationService implements IOrganization {

    ProxyFactoryService proxyFactoryService
    
    @Override
    OrganizationDTO getOrganization(SearchBean searchBean) {
        proxyFactoryService.organizationProxySetup()
        return proxyFactoryService.organizationProxy.getOrganization(searchBean)
    }

    @Override
    PagedList<OrganizationDTO> searchOrganization(SearchBean searchBean) {
        PagedList<OrganizationDTO> pagedList
        try{
            proxyFactoryService.organizationProxySetup()
            pagedList = proxyFactoryService.organizationProxy.searchOrganization(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteOrganization(SearchBean searchBean) {
        proxyFactoryService.organizationProxySetup()
        return proxyFactoryService.organizationProxy.autoCompleteOrganization(searchBean)
    }

    @Override
    OrganizationCommand saveOrganization(OrganizationCommand organizationCommand) {
        proxyFactoryService.organizationProxySetup()
        return proxyFactoryService.organizationProxy.saveOrganization(organizationCommand)
    }

    @Override
    DeleteBean deleteOrganization(DeleteBean deleteBean) {
        proxyFactoryService.organizationProxySetup()
        return proxyFactoryService.organizationProxy.deleteOrganization(deleteBean)
    }
}

