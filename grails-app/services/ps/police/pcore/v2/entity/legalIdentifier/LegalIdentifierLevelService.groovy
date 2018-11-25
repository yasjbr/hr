package ps.police.pcore.v2.entity.legalIdentifier

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.legalIdentifier.commands.v1.LegalIdentifierLevelCommand
import ps.police.pcore.v2.entity.legalIdentifier.dtos.v1.LegalIdentifierLevelDTO
import ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifierLevel


@Transactional
class LegalIdentifierLevelService implements ILegalIdentifierLevel {

    ProxyFactoryService proxyFactoryService
   
    @Override
    LegalIdentifierLevelDTO getLegalIdentifierLevel(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierLevelProxySetup()
        return proxyFactoryService.legalIdentifierLevelProxy.getLegalIdentifierLevel(searchBean)
    }

    @Override
    PagedList<LegalIdentifierLevelDTO> searchLegalIdentifierLevel(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierLevelProxySetup()
        return proxyFactoryService.legalIdentifierLevelProxy.searchLegalIdentifierLevel(searchBean)
    }

    @Override
    String autoCompleteLegalIdentifierLevel(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierLevelProxySetup()
        return proxyFactoryService.legalIdentifierLevelProxy.autoCompleteLegalIdentifierLevel(searchBean)
    }

    @Override
    LegalIdentifierLevelCommand saveLegalIdentifierLevel(LegalIdentifierLevelCommand legalIdentifierLevelCommand) {
        proxyFactoryService.legalIdentifierLevelProxySetup()
        return proxyFactoryService.legalIdentifierLevelProxy.saveLegalIdentifierLevel(legalIdentifierLevelCommand)
    }

    @Override
    DeleteBean deleteLegalIdentifierLevel(DeleteBean deleteBean) {
        proxyFactoryService.legalIdentifierLevelProxySetup()
        return proxyFactoryService.legalIdentifierLevelProxy.deleteLegalIdentifierLevel(deleteBean)
    }
}