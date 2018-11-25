package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.DistrictCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.DistrictDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IDistrict

@Transactional
class DistrictService  implements  IDistrict{

    ProxyFactoryService proxyFactoryService

    @Override
    DistrictDTO getDistrict(SearchBean searchBean){
        proxyFactoryService.districtProxySetup()
        return proxyFactoryService.districtProxy.getDistrict(searchBean)
    }

    @Override
    PagedList<DistrictDTO> searchDistrict(SearchBean searchBean){
        PagedList<DistrictDTO> pagedList
        try{
            proxyFactoryService.districtProxySetup()
            pagedList = proxyFactoryService.districtProxy.searchDistrict(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteDistrict(SearchBean searchBean){
        proxyFactoryService.districtProxySetup()
        return proxyFactoryService.districtProxy.autoCompleteDistrict(searchBean)
    }

    @Override
    DistrictCommand saveDistrict(DistrictCommand OrganizationCommand){
        proxyFactoryService.districtProxySetup()
        return proxyFactoryService.districtProxy.saveDistrict(OrganizationCommand)
    }

    @Override
    DeleteBean deleteDistrict(DeleteBean deleteBean){
        proxyFactoryService.districtProxySetup()
        return proxyFactoryService.districtProxy.deleteDistrict(deleteBean)
    }

}
