package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.ContactMethodCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.ContactMethodDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IContactMethod

@Transactional
class ContactMethodService implements IContactMethod {

    ProxyFactoryService proxyFactoryService

    @Override
    ContactMethodDTO getContactMethod(SearchBean searchBean){
        proxyFactoryService.contactMethodProxySetup()
        return proxyFactoryService.contactMethodProxy.getContactMethod(searchBean)
    }

    @Override
    PagedList<ContactMethodDTO> searchContactMethod(SearchBean searchBean){
        PagedList<ContactMethodDTO> pagedList
        try{
            proxyFactoryService.contactMethodProxySetup()
            pagedList = proxyFactoryService.contactMethodProxy.searchContactMethod(searchBean)
        }
        catch (Exception e){

        }
        return pagedList
    }

    @Override
    String autoCompleteContactMethod(SearchBean searchBean){
        proxyFactoryService.contactMethodProxySetup()
        return proxyFactoryService.contactMethodProxy.autoCompleteContactMethod(searchBean)
    }

    @Override
    ContactMethodCommand saveContactMethod(ContactMethodCommand ContactMethodCommand){
        proxyFactoryService.contactMethodProxySetup()
        return proxyFactoryService.contactMethodProxy.saveContactMethod(ContactMethodCommand)
    }

    @Override
    DeleteBean deleteContactMethod(DeleteBean deleteBean){
        proxyFactoryService.contactMethodProxySetup()
        return proxyFactoryService.contactMethodProxy.deleteContactMethod(deleteBean)
    }
}
