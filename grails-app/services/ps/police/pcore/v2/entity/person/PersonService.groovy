package ps.police.pcore.v2.entity.person

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.interfaces.v1.IPerson

@Transactional
class PersonService implements IPerson {

    ProxyFactoryService proxyFactoryService

    @Override
    PersonDTO getPerson(SearchBean searchBean){
        proxyFactoryService.personProxySetup()
        return proxyFactoryService.personProxy.getPerson(searchBean)

    }

    @Override
    PagedList<PersonDTO> searchPerson(SearchBean searchBean){
        proxyFactoryService.personProxySetup()
        return proxyFactoryService.personProxy.searchPerson(searchBean)
    }

    @Override
    String autoCompletePerson(SearchBean searchBean){
        proxyFactoryService.personProxySetup()
        return proxyFactoryService.personProxy.autoCompletePerson(searchBean)
    }

    @Override
    PersonCommand savePerson(PersonCommand personCommand){
        proxyFactoryService.personProxySetup()
        return proxyFactoryService.personProxy.savePerson(personCommand)
    }

    @Override
    DeleteBean deletePerson(DeleteBean deleteBean){
        proxyFactoryService.personProxySetup()
        return proxyFactoryService.personProxy.deletePerson(deleteBean)
    }
}
