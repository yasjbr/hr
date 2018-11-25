package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.RelationshipTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.RelationshipTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IRelationshipType

@Transactional
class RelationshipTypeService implements IRelationshipType {

    ProxyFactoryService proxyFactoryService

    @Override
    RelationshipTypeDTO getRelationshipType(SearchBean searchBean) {
        proxyFactoryService.relationshipTypeProxySetup()
        return proxyFactoryService.relationshipTypeProxy.getRelationshipType(searchBean)
    }

    @Override
    PagedList<RelationshipTypeDTO> searchRelationshipType(SearchBean searchBean) {
        PagedList<RelationshipTypeDTO> pagedList
        try{
            proxyFactoryService.relationshipTypeProxySetup()
            pagedList = proxyFactoryService.relationshipTypeProxy.searchRelationshipType(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteRelationshipType(SearchBean searchBean) {
        proxyFactoryService.relationshipTypeProxySetup()
        return proxyFactoryService.relationshipTypeProxy.autoCompleteRelationshipType(searchBean)
    }

    @Override
    RelationshipTypeCommand saveRelationshipType(RelationshipTypeCommand relationshipTypeCommandCommand) {
        proxyFactoryService.relationshipTypeProxySetup()
        return proxyFactoryService.relationshipTypeProxy.saveRelationshipType(relationshipTypeCommandCommand)
    }

    @Override
    DeleteBean deleteRelationshipType(DeleteBean deleteBean) {
        proxyFactoryService.relationshipTypeProxySetup()
        return proxyFactoryService.relationshipTypeProxy.deleteRelationshipType(deleteBean)
    }
}
