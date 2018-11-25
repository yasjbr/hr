package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.EducationDegreeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.EducationDegreeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IEducationDegree

@Transactional
class EducationDegreeService implements IEducationDegree{

    ProxyFactoryService proxyFactoryService

    @Override
    EducationDegreeDTO getEducationDegree(SearchBean searchBean) {
        proxyFactoryService.educationDegreeProxySetup()
        return proxyFactoryService.educationDegreeProxy.getEducationDegree(searchBean)
    }

    @Override
    PagedList<EducationDegreeDTO> searchEducationDegree(SearchBean searchBean) {
        PagedList<EducationDegreeDTO> pagedList
        try{
            proxyFactoryService.educationDegreeProxySetup()
            pagedList = proxyFactoryService.educationDegreeProxy.searchEducationDegree(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteEducationDegree(SearchBean searchBean) {
        proxyFactoryService.educationDegreeProxySetup()
        return proxyFactoryService.educationDegreeProxy.autoCompleteEducationDegree(searchBean)
    }

    @Override
    EducationDegreeCommand saveEducationDegree(EducationDegreeCommand OrganizationCommand) {
        proxyFactoryService.educationDegreeProxySetup()
        return proxyFactoryService.educationDegreeProxy.saveEducationDegree(OrganizationCommand)
    }

    @Override
    DeleteBean deleteEducationDegree(DeleteBean deleteBean) {
        proxyFactoryService.educationDegreeProxySetup()
        return proxyFactoryService.educationDegreeProxy.deleteEducationDegree(deleteBean)
    }
}
