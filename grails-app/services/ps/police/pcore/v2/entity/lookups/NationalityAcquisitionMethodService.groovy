package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.NationalityAcquisitionMethodCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.NationalityAcquisitionMethodDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.INationalityAcquisitionMethod

@Transactional
class NationalityAcquisitionMethodService implements  INationalityAcquisitionMethod {

    ProxyFactoryService proxyFactoryService

    @Override
    NationalityAcquisitionMethodDTO getNationalityAcquisitionMethod(SearchBean searchBean){
        proxyFactoryService.nationalityAcquisitionMethodProxySetup()
        return proxyFactoryService.nationalityAcquisitionMethodProxy.getNationalityAcquisitionMethod(searchBean)
    }

    @Override
    PagedList<NationalityAcquisitionMethodDTO> searchNationalityAcquisitionMethod(SearchBean searchBean){
        PagedList<NationalityAcquisitionMethodDTO> pagedList
        try{
            proxyFactoryService.nationalityAcquisitionMethodProxySetup()
            pagedList = proxyFactoryService.nationalityAcquisitionMethodProxy.searchNationalityAcquisitionMethod(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteNationalityAcquisitionMethod(SearchBean searchBean){
        proxyFactoryService.nationalityAcquisitionMethodProxySetup()
        return proxyFactoryService.nationalityAcquisitionMethodProxy.autoCompleteNationalityAcquisitionMethod(searchBean)
    }

    @Override
    NationalityAcquisitionMethodCommand saveNationalityAcquisitionMethod(NationalityAcquisitionMethodCommand nationalityAcquisitionMethodCommand){
        proxyFactoryService.nationalityAcquisitionMethodProxySetup()
        return proxyFactoryService.nationalityAcquisitionMethodProxy.saveNationalityAcquisitionMethod(nationalityAcquisitionMethodCommand)
    }

    @Override
    DeleteBean deleteNationalityAcquisitionMethod(DeleteBean deleteBean){
        proxyFactoryService.nationalityAcquisitionMethodProxySetup()
        return proxyFactoryService.nationalityAcquisitionMethodProxy.deleteNationalityAcquisitionMethod(deleteBean)
    }
}
