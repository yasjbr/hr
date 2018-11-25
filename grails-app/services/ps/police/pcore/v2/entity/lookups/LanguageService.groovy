package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.LanguageCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.LanguageDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.ILanguage

@Transactional
class LanguageService implements ILanguage {

    ProxyFactoryService proxyFactoryService

    @Override
    LanguageDTO getLanguage(SearchBean searchBean){
        proxyFactoryService.languageProxySetup()
        return proxyFactoryService.languageProxy.getLanguage(searchBean)
    }

    @Override
    PagedList<LanguageDTO> searchLanguage(SearchBean searchBean){
        PagedList<LanguageDTO> pagedList
        try{
            proxyFactoryService.languageProxySetup()
            pagedList = proxyFactoryService.languageProxy.searchLanguage(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteLanguage(SearchBean searchBean){
        proxyFactoryService.languageProxySetup()
        return proxyFactoryService.languageProxy.autoCompleteLanguage(searchBean)
    }

    @Override
    LanguageCommand saveLanguage(LanguageCommand languageCommand){
        proxyFactoryService.languageProxySetup()
        return proxyFactoryService.languageProxy.saveLanguage(languageCommand)
    }

    @Override
    DeleteBean deleteLanguage(DeleteBean deleteBean){
        proxyFactoryService.languageProxySetup()
        return proxyFactoryService.languageProxy.deleteLanguage(deleteBean)
    }
}
