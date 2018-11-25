package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.BorderCrossingPointCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.BorderCrossingPointDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IBorderCrossingPoint

@Transactional
class BorderCrossingPointService implements IBorderCrossingPoint{

    ProxyFactoryService proxyFactoryService

    @Override
    BorderCrossingPointDTO getBorderCrossingPoint(SearchBean searchBean) {
        proxyFactoryService.borderCrossingPointProxySetup()
        return proxyFactoryService.borderCrossingPointProxy.getBorderCrossingPoint(searchBean)
    }

    @Override
    PagedList<BorderCrossingPointDTO> searchBorderCrossingPoint(SearchBean searchBean) {
        proxyFactoryService.borderCrossingPointProxySetup()
        return proxyFactoryService.borderCrossingPointProxy.searchBorderCrossingPoint(searchBean)
    }

    @Override
    String autoCompleteBorderCrossingPoint(SearchBean searchBean) {
        proxyFactoryService.borderCrossingPointProxySetup()
        return proxyFactoryService.borderCrossingPointProxy.autoCompleteBorderCrossingPoint(searchBean)
    }

    @Override
    BorderCrossingPointCommand saveBorderCrossingPoint(BorderCrossingPointCommand borderCrossingPointCommand) {
        proxyFactoryService.borderCrossingPointProxySetup()
        return proxyFactoryService.borderCrossingPointProxy.saveBorderCrossingPoint(borderCrossingPointCommand)
    }

    @Override
    DeleteBean deleteBorderCrossingPoint(DeleteBean deleteBean) {
        proxyFactoryService.borderCrossingPointProxySetup()
        return proxyFactoryService.borderCrossingPointProxy.deleteBorderCrossingPoint(deleteBean)
    }
}
