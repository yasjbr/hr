package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.DocumentClassificationCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.DocumentClassificationDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IDocumentClassification

@Transactional
class DocumentClassificationService implements IDocumentClassification{

    ProxyFactoryService proxyFactoryService

    @Override
    DocumentClassificationDTO getDocumentClassification(SearchBean searchBean) {
        proxyFactoryService.documentClassificationProxySetup()
        return proxyFactoryService.documentClassificationProxy.getDocumentClassification(searchBean)
    }

    @Override
    PagedList<DocumentClassificationDTO> searchDocumentClassification(SearchBean searchBean) {
        proxyFactoryService.documentClassificationProxySetup()
        return proxyFactoryService.documentClassificationProxy.searchDocumentClassification(searchBean)
    }

    @Override
    String autoCompleteDocumentClassification(SearchBean searchBean) {
        proxyFactoryService.documentClassificationProxySetup()
        return proxyFactoryService.documentClassificationProxy.autoCompleteDocumentClassification(searchBean)
    }

    @Override
    DocumentClassificationCommand saveDocumentClassification(DocumentClassificationCommand documentClassificationCommand) {
        proxyFactoryService.documentClassificationProxySetup()
        return proxyFactoryService.documentClassificationProxy.saveDocumentClassification(documentClassificationCommand)
    }

    @Override
    DeleteBean deleteDocumentClassification(DeleteBean deleteBean) {
        proxyFactoryService.documentClassificationProxySetup()
        return proxyFactoryService.documentClassificationProxy.deleteDocumentClassification(deleteBean)
    }
}
