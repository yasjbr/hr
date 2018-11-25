package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.LiveStatusCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.LiveStatusDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.ILiveStatus

@Transactional
class LiveStatusService implements  ILiveStatus {

    ProxyFactoryService proxyFactoryService

    @Override
    LiveStatusDTO getLiveStatus(SearchBean searchBean){
        proxyFactoryService.liveStatusProxySetup()
        return proxyFactoryService.liveStatusProxy.getLiveStatus(searchBean)
    }

    @Override
    PagedList<LiveStatusDTO> searchLiveStatus(SearchBean searchBean){
        PagedList<LiveStatusDTO> pagedList
        try{
            proxyFactoryService.liveStatusProxySetup()
            pagedList = proxyFactoryService.liveStatusProxy.searchLiveStatus(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteLiveStatus(SearchBean searchBean){
        proxyFactoryService.liveStatusProxySetup()
        return proxyFactoryService.liveStatusProxy.autoCompleteLiveStatus(searchBean)
    }

    @Override
    LiveStatusCommand saveLiveStatus(LiveStatusCommand liveStatusCommand){
        proxyFactoryService.liveStatusProxySetup()
        return proxyFactoryService.liveStatusProxy.saveLiveStatus(liveStatusCommand)
    }

    @Override
    DeleteBean deleteLiveStatus(DeleteBean deleteBean){
        proxyFactoryService.liveStatusProxySetup()
        return proxyFactoryService.liveStatusProxy.deleteLiveStatus(deleteBean)
    }
}
