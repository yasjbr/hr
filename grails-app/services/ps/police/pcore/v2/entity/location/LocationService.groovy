package ps.police.pcore.v2.entity.location

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.commands.v1.LocationCommand
import ps.police.pcore.v2.entity.location.dtos.v1.LocationDTO
import ps.police.pcore.v2.entity.location.interfaces.v1.ILocation

@Transactional
class LocationService implements ILocation{

    ProxyFactoryService proxyFactoryService

    @Override
    LocationDTO getLocation(SearchBean searchBean){
        proxyFactoryService.locationProxySetup()
        return proxyFactoryService.locationProxy.getLocation(searchBean)
    }

    @Override
    PagedList<LocationDTO> searchLocation(SearchBean searchBean){
        PagedList<LocationDTO> pagedList
        try{
            proxyFactoryService.locationProxySetup()
            pagedList = proxyFactoryService.locationProxy.searchLocation(searchBean)
        }
        catch (Exception e){

        }
        return pagedList
    }

    @Override
    String autoCompleteLocation(SearchBean searchBean){
        proxyFactoryService.locationProxySetup()
        return proxyFactoryService.locationProxy.autoCompleteLocation(searchBean)
    }

    @Override
    LocationCommand saveLocation(LocationCommand locationCommand){

        if(locationCommand.validate()){
            proxyFactoryService.locationProxySetup()
            return proxyFactoryService.locationProxy.saveLocation(locationCommand)
        }
        else{
            return locationCommand.errors
        }
    }

    @Override
    DeleteBean deleteLocation(DeleteBean deleteBean){
        proxyFactoryService.locationProxySetup()
        return proxyFactoryService.locationProxy.deleteLocation(deleteBean)
    }
}
