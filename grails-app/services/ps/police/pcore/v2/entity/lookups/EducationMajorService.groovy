package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.EducationMajorCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationMajorDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationMajor

@Transactional
class EducationMajorService implements IEducationMajor{

    ProxyFactoryService proxyFactoryService

    @Override
    EducationMajorDTO getEducationMajor(SearchBean searchBean){
        proxyFactoryService.educationMajorProxySetup()
        return proxyFactoryService.educationMajorProxy.getEducationMajor(searchBean)
    }

    @Override
    PagedList<EducationMajorDTO> searchEducationMajor(SearchBean searchBean){
        PagedList<EducationMajorDTO> pagedList
        try{
            proxyFactoryService.educationMajorProxySetup()
            pagedList = proxyFactoryService.educationMajorProxy.searchEducationMajor(searchBean)
        }
        catch (Exception e){

        }
        return pagedList
    }

    @Override
    String autoCompleteEducationMajor(SearchBean searchBean){
        proxyFactoryService.educationMajorProxySetup()
        return proxyFactoryService.educationMajorProxy.autoCompleteEducationMajor(searchBean)
    }

    @Override
    EducationMajorCommand saveEducationMajor(EducationMajorCommand OrganizationCommand){
        proxyFactoryService.educationMajorProxySetup()
        return proxyFactoryService.educationMajorProxy.saveEducationMajor(OrganizationCommand)
    }

    @Override
    DeleteBean deleteEducationMajor(DeleteBean deleteBean){
        proxyFactoryService.educationMajorProxySetup()
        return proxyFactoryService.educationMajorProxy.deleteEducationMajor(deleteBean)
    }


}
