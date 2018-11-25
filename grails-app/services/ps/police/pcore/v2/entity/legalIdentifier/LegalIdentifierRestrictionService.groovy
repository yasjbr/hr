package ps.police.pcore.v2.entity.legalIdentifier

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.legalIdentifier.commands.v1.LegalIdentifierRestrictionCommand
import ps.police.pcore.v2.entity.legalIdentifier.dtos.v1.LegalIdentifierRestrictionDTO
import ps.police.pcore.v2.entity.legalIdentifier.interfaces.v1.ILegalIdentifierRestriction


@Transactional
class LegalIdentifierRestrictionService implements ILegalIdentifierRestriction {

    ProxyFactoryService proxyFactoryService

    @Override
    LegalIdentifierRestrictionDTO getLegalIdentifierRestriction(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierRestrictionProxySetup()
        return proxyFactoryService.legalIdentifierRestrictionProxy.getLegalIdentifierRestriction(searchBean)
    }

    @Override
    PagedList<LegalIdentifierRestrictionDTO> searchLegalIdentifierRestriction(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierRestrictionProxySetup()
        return proxyFactoryService.legalIdentifierRestrictionProxy.searchLegalIdentifierRestriction(searchBean)
    }

    @Override
    String autoCompleteLegalIdentifierRestriction(SearchBean searchBean) {
        proxyFactoryService.legalIdentifierRestrictionProxySetup()
        return proxyFactoryService.legalIdentifierRestrictionProxy.autoCompleteLegalIdentifierRestriction(searchBean)
    }

    @Override
    LegalIdentifierRestrictionCommand saveLegalIdentifierRestriction(LegalIdentifierRestrictionCommand legalIdentifierRestrictionCommand) {
        proxyFactoryService.legalIdentifierRestrictionProxySetup()
        return proxyFactoryService.legalIdentifierRestrictionProxy.saveLegalIdentifierRestriction(legalIdentifierRestrictionCommand)
    }

    @Override
    DeleteBean deleteLegalIdentifierRestriction(DeleteBean deleteBean) {
        proxyFactoryService.legalIdentifierRestrictionProxySetup()
        return proxyFactoryService.legalIdentifierRestrictionProxy.deleteLegalIdentifierRestriction(deleteBean)
    }
}