package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.DocumentTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.DocumentTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IDocumentType

@Transactional
class DocumentTypeService implements IDocumentType{

    ProxyFactoryService proxyFactoryService

    @Override
    DocumentTypeDTO getDocumentType(SearchBean searchBean) {
        proxyFactoryService.documentTypeProxySetup()
        return proxyFactoryService.documentTypeProxy.getDocumentType(searchBean)
    }

    @Override
    PagedList<DocumentTypeDTO> searchDocumentType(SearchBean searchBean) {
        proxyFactoryService.documentTypeProxySetup()
        return proxyFactoryService.documentTypeProxy.searchDocumentType(searchBean)
    }

    @Override
    String autoCompleteDocumentType(SearchBean searchBean) {
        proxyFactoryService.documentTypeProxySetup()
        return proxyFactoryService.documentTypeProxy.autoCompleteDocumentType(searchBean)
    }

    @Override
    DocumentTypeCommand saveDocumentType(DocumentTypeCommand documentTypeCommand) {
        proxyFactoryService.documentTypeProxySetup()
        return proxyFactoryService.documentTypeProxy.saveDocumentType(documentTypeCommand)
    }

    @Override
    DeleteBean deleteDocumentType(DeleteBean deleteBean) {
        proxyFactoryService.documentTypeProxySetup()
        return proxyFactoryService.documentTypeProxy.deleteDocumentType(deleteBean)
    }
}
