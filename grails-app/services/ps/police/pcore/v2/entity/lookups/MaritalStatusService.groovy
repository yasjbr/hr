package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.MaritalStatusCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.MaritalStatusDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IMaritalStatus

@Transactional
class MaritalStatusService implements IMaritalStatus {

    ProxyFactoryService proxyFactoryService

    @Override
    MaritalStatusDTO getMaritalStatus(SearchBean searchBean) {
        proxyFactoryService.maritalStatusProxySetup()
        return proxyFactoryService.maritalStatusProxy.getMaritalStatus(searchBean)
    }

    @Override
    PagedList<MaritalStatusDTO> searchMaritalStatus(SearchBean searchBean) {
        PagedList<MaritalStatusDTO> pagedList
        try{
            proxyFactoryService.maritalStatusProxySetup()
            pagedList = proxyFactoryService.maritalStatusProxy.searchMaritalStatus(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteMaritalStatus(SearchBean searchBean) {
        proxyFactoryService.maritalStatusProxySetup()
        return proxyFactoryService.maritalStatusProxy.autoCompleteMaritalStatus(searchBean)
    }

    @Override
    MaritalStatusCommand saveMaritalStatus(MaritalStatusCommand maritalStatusCommandCommand) {
        proxyFactoryService.maritalStatusProxySetup()
        return proxyFactoryService.maritalStatusProxy.saveMaritalStatus(maritalStatusCommandCommand)
    }

    @Override
    DeleteBean deleteMaritalStatus(DeleteBean deleteBean) {
        proxyFactoryService.maritalStatusProxySetup()
        return proxyFactoryService.maritalStatusProxy.deleteMaritalStatus(deleteBean)
    }
}
