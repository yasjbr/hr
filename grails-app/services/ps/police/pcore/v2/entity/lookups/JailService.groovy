package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.JailCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.JailDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IJail

@Transactional
class JailService implements IJail{

    ProxyFactoryService proxyFactoryService

    @Override
    JailDTO getJail(SearchBean searchBean) {
        proxyFactoryService.jailProxySetup()
        return proxyFactoryService.jailProxy.getJail(searchBean)
    }

    @Override
    PagedList<JailDTO> searchJail(SearchBean searchBean) {
        proxyFactoryService.jailProxySetup()
        return proxyFactoryService.jailProxy.searchJail(searchBean)
    }

    @Override
    String autoCompleteJail(SearchBean searchBean) {
        proxyFactoryService.jailProxySetup()
        return proxyFactoryService.jailProxy.autoCompleteJail(searchBean)
    }

    @Override
    JailCommand saveJail(JailCommand jailCommand) {
        proxyFactoryService.jailProxySetup()
        return proxyFactoryService.jailProxy.saveJail(jailCommand)
    }

    @Override
    DeleteBean deleteJail(DeleteBean deleteBean) {
        proxyFactoryService.jailProxySetup()
        return proxyFactoryService.jailProxy.deleteJail(deleteBean)
    }
}
