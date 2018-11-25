package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.ContactTypeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.ContactTypeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IContactType

@Transactional
class ContactTypeService implements IContactType {

        ProxyFactoryService proxyFactoryService

        @Override
        ContactTypeDTO getContactType(SearchBean searchBean){
            proxyFactoryService.contactTypeProxySetup()
            return proxyFactoryService.contactTypeProxy.getContactType(searchBean)
        }

        @Override
        PagedList<ContactTypeDTO> searchContactType(SearchBean searchBean){
            PagedList<ContactTypeDTO> pagedList
            try{
                proxyFactoryService.contactTypeProxySetup()
                pagedList = proxyFactoryService.contactTypeProxy.searchContactType(searchBean)
            }
            catch (Exception e){

            }
            return pagedList
        }

        @Override
        String autoCompleteContactType(SearchBean searchBean){
            proxyFactoryService.contactTypeProxySetup()
            return proxyFactoryService.contactTypeProxy.autoCompleteContactType(searchBean)
        }

        @Override
        ContactTypeCommand saveContactType(ContactTypeCommand ContactTypeCommand){
            proxyFactoryService.contactTypeProxySetup()
            return proxyFactoryService.contactTypeProxy.saveContactType(ContactTypeCommand)
        }

        @Override
        DeleteBean deleteContactType(DeleteBean deleteBean){
            proxyFactoryService.contactTypeProxySetup()
            return proxyFactoryService.contactTypeProxy.deleteContactType(deleteBean)
        }
    }

