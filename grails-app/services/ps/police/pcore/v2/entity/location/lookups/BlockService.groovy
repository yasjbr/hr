package ps.police.pcore.v2.entity.location.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.location.lookups.commands.v1.BlockCommand
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.BlockDTO
import ps.police.pcore.v2.entity.location.lookups.interfaces.v1.IBlock

@Transactional
class BlockService implements IBlock {


    ProxyFactoryService proxyFactoryService

    @Override
    BlockDTO getBlock(SearchBean searchBean){
        proxyFactoryService.blockProxySetup()
        return proxyFactoryService.blockProxy.getBlock(searchBean)
    }

    @Override
    PagedList<BlockDTO> searchBlock(SearchBean searchBean){
        PagedList<BlockDTO> pagedList
        try{
            proxyFactoryService.blockProxySetup()
            pagedList = proxyFactoryService.blockProxy.searchBlock(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteBlock(SearchBean searchBean){
        proxyFactoryService.blockProxySetup()
        return proxyFactoryService.blockProxy.autoCompleteBlock(searchBean)
    }

    @Override
    BlockCommand saveBlock(BlockCommand OrganizationCommand){
        proxyFactoryService.blockProxySetup()
        return proxyFactoryService.blockProxy.saveBlock(OrganizationCommand)
    }

    @Override
    DeleteBean deleteBlock(DeleteBean deleteBean){
        proxyFactoryService.blockProxySetup()
        return proxyFactoryService.blockProxy.deleteBlock(deleteBean)
    }
}
