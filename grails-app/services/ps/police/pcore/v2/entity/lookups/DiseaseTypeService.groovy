package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.DiseaseTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.DiseaseTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IDiseaseType

@Transactional
class DiseaseTypeService implements  IDiseaseType {

    ProxyFactoryService proxyFactoryService

    @Override
    DiseaseTypeDTO getDiseaseType(SearchBean searchBean){
        proxyFactoryService.diseaseTypeProxySetup()
        return proxyFactoryService.diseaseTypeProxy.getDiseaseType(searchBean)
    }

    @Override
    PagedList<DiseaseTypeDTO> searchDiseaseType(SearchBean searchBean){
        PagedList<DiseaseTypeDTO> pagedList
        try{
            proxyFactoryService.diseaseTypeProxySetup()
            pagedList = proxyFactoryService.diseaseTypeProxy.searchDiseaseType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteDiseaseType(SearchBean searchBean){
        proxyFactoryService.diseaseTypeProxySetup()
        return proxyFactoryService.diseaseTypeProxy.autoCompleteDiseaseType(searchBean)
    }

    @Override
    DiseaseTypeCommand saveDiseaseType(DiseaseTypeCommand diseaseTypeCommand){
        proxyFactoryService.diseaseTypeProxySetup()
        return proxyFactoryService.diseaseTypeProxy.saveDiseaseType(diseaseTypeCommand)
    }

    @Override
    DeleteBean deleteDiseaseType(DeleteBean deleteBean){
        proxyFactoryService.diseaseTypeProxySetup()
        return proxyFactoryService.diseaseTypeProxy.deleteDiseaseType(deleteBean)
    }
}
