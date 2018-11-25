package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.ProfessionTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.ProfessionTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IProfessionType

@Transactional
class ProfessionTypeService implements IProfessionType{

    ProxyFactoryService proxyFactoryService

    @Override
    ProfessionTypeDTO getProfessionType(SearchBean searchBean) {
        proxyFactoryService.professionTypeProxySetup()
        return proxyFactoryService.professionTypeProxy.getProfessionType(searchBean)
    }

    @Override
    PagedList<ProfessionTypeDTO> searchProfessionType(SearchBean searchBean) {
        PagedList<ProfessionTypeDTO> pagedList
        try{
            proxyFactoryService.professionTypeProxySetup()
            pagedList = proxyFactoryService.professionTypeProxy.searchProfessionType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteProfessionType(SearchBean searchBean) {
        proxyFactoryService.professionTypeProxySetup()
        return proxyFactoryService.professionTypeProxy.autoCompleteProfessionType(searchBean)
    }

    @Override
    ProfessionTypeCommand saveProfessionType(ProfessionTypeCommand OrganizationCommand) {
        proxyFactoryService.professionTypeProxySetup()
        return proxyFactoryService.professionTypeProxy.saveProfessionType(OrganizationCommand)
    }

    @Override
    DeleteBean deleteProfessionType(DeleteBean deleteBean) {
        proxyFactoryService.professionTypeProxySetup()
        return proxyFactoryService.professionTypeProxy.deleteProfessionType(deleteBean)
    }
}
