package ps.police.pcore.v2.entity.organization.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.organization.lookups.commands.v1.OrganizationActivityCommand
import ps.police.pcore.v2.entity.organization.lookups.dtos.v1.OrganizationActivityDTO
import ps.police.pcore.v2.entity.organization.lookups.interfaces.v1.IOrganizationActivity

@Transactional
class OrganizationActivityService  implements  IOrganizationActivity{
    ProxyFactoryService proxyFactoryService

    @Override
    OrganizationActivityDTO getOrganizationActivity(SearchBean searchBean){
        proxyFactoryService.organizationActivityProxySetup()
        return proxyFactoryService.organizationActivityProxy.getOrganizationActivity(searchBean)
    }

    @Override
    PagedList<OrganizationActivityDTO> searchOrganizationActivity(SearchBean searchBean){
        PagedList<OrganizationActivityDTO> pagedList
        try{
            proxyFactoryService.organizationActivityProxySetup()
            pagedList = proxyFactoryService.organizationActivityProxy.searchOrganizationActivity(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteOrganizationActivity(SearchBean searchBean){
        proxyFactoryService.organizationActivityProxySetup()
        return proxyFactoryService.organizationActivityProxy.autoCompleteOrganizationActivity(searchBean)
    }

    @Override
    OrganizationActivityCommand saveOrganizationActivity(OrganizationActivityCommand OrganizationCommand){
        proxyFactoryService.organizationActivityProxySetup()
        return proxyFactoryService.organizationActivityProxy.saveOrganizationActivity(OrganizationCommand)
    }

    @Override
    DeleteBean deleteOrganizationActivity(DeleteBean deleteBean){
        proxyFactoryService.organizationActivityProxySetup()
        return proxyFactoryService.organizationActivityProxy.deleteOrganizationActivity(deleteBean)
    }

}
