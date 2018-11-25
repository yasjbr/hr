package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.CurrencyCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.CurrencyDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.ICurrency

@Transactional
class CurrencyService implements  ICurrency {

    ProxyFactoryService proxyFactoryService

    @Override
    CurrencyDTO getCurrency(SearchBean searchBean){
        proxyFactoryService.currencyProxySetup()
        return proxyFactoryService.currencyProxy.getCurrency(searchBean)
    }

    @Override
    PagedList<CurrencyDTO> searchCurrency(SearchBean searchBean){
        PagedList<CurrencyDTO> pagedList
        try{
            proxyFactoryService.currencyProxySetup()
            pagedList = proxyFactoryService.currencyProxy.searchCurrency(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteCurrency(SearchBean searchBean){
        proxyFactoryService.currencyProxySetup()
        return proxyFactoryService.currencyProxy.autoCompleteCurrency(searchBean)
    }

    @Override
    CurrencyCommand saveCurrency(CurrencyCommand currencyCommand){
        proxyFactoryService.currencyProxySetup()
        return proxyFactoryService.currencyProxy.saveCurrency(currencyCommand)
    }

    @Override
    DeleteBean deleteCurrency(DeleteBean deleteBean){
        proxyFactoryService.currencyProxySetup()
        return proxyFactoryService.currencyProxy.deleteCurrency(deleteBean)
    }
}
