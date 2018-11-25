package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.CountryCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.CountryDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.ICountry

@Transactional
class CountryService  implements ICountry{


    ProxyFactoryService proxyFactoryService

    @Override
    CountryDTO getCountry(SearchBean searchBean){
        proxyFactoryService.countryProxySetup()
        return proxyFactoryService.countryProxy.getCountry(searchBean)
    }

    @Override
    PagedList<CountryDTO> searchCountry(SearchBean searchBean){
        PagedList<CountryDTO> pagedList
        try{
            proxyFactoryService.countryProxySetup()
            pagedList = proxyFactoryService.countryProxy.searchCountry(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteCountry(SearchBean searchBean){
        proxyFactoryService.countryProxySetup()
        return proxyFactoryService.countryProxy.autoCompleteCountry(searchBean)
    }

    @Override
    CountryCommand saveCountry(CountryCommand OrganizationCommand){
        proxyFactoryService.countryProxySetup()
        return proxyFactoryService.countryProxy.saveCountry(OrganizationCommand)
    }

    @Override
    DeleteBean deleteCountry(DeleteBean deleteBean){
        proxyFactoryService.countryProxySetup()
        return proxyFactoryService.countryProxy.deleteCountry(deleteBean)
    }
}
