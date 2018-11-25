package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.EducationLevelCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationLevelDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationLevel

@Transactional
class EducationLevelService implements IEducationLevel{

    ProxyFactoryService proxyFactoryService

    @Override
    EducationLevelDTO getEducationLevel(SearchBean searchBean) {
        proxyFactoryService.educationLevelProxySetup()
        return proxyFactoryService.educationLevelProxy.getEducationLevel(searchBean)
    }

    @Override
    PagedList<EducationLevelDTO> searchEducationLevel(SearchBean searchBean) {
        PagedList<EducationLevelDTO> pagedList
        try{
            proxyFactoryService.educationLevelProxySetup()
            pagedList = proxyFactoryService.educationLevelProxy.searchEducationLevel(searchBean)
        }
        catch (Exception e){
        }
        return pagedList

    }

    @Override
    String autoCompleteEducationLevel(SearchBean searchBean) {
        proxyFactoryService.educationLevelProxySetup()
        return proxyFactoryService.educationLevelProxy.autoCompleteEducationLevel(searchBean)
    }

    @Override
    EducationLevelCommand saveEducationLevel(EducationLevelCommand OrganizationCommand) {
        proxyFactoryService.educationLevelProxySetup()
        return proxyFactoryService.educationLevelProxy.saveEducationLevel(OrganizationCommand)
    }

    @Override
    DeleteBean deleteEducationLevel(DeleteBean deleteBean) {
        proxyFactoryService.educationLevelProxySetup()
        return proxyFactoryService.educationLevelProxy.deleteEducationLevel(deleteBean)
    }
}
