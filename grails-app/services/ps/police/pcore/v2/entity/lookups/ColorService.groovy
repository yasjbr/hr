package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.ColorCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.ColorDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IColor

@Transactional
class ColorService implements  IColor {

    ProxyFactoryService proxyFactoryService

    @Override
    ColorDTO getColor(SearchBean searchBean){
        proxyFactoryService.colorProxySetup()
        return proxyFactoryService.colorProxy.getColor(searchBean)
    }

    @Override
    PagedList<ColorDTO> searchColor(SearchBean searchBean){
        PagedList<ColorDTO> pagedList
        try{
            proxyFactoryService.colorProxySetup()
            pagedList = proxyFactoryService.colorProxy.searchColor(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteColor(SearchBean searchBean){
        proxyFactoryService.colorProxySetup()
        return proxyFactoryService.colorProxy.autoCompleteColor(searchBean)
    }

    @Override
    ColorCommand saveColor(ColorCommand colorCommand){
        proxyFactoryService.colorProxySetup()
        return proxyFactoryService.colorProxy.saveColor(colorCommand)
    }

    @Override
    DeleteBean deleteColor(DeleteBean deleteBean){
        proxyFactoryService.colorProxySetup()
        return proxyFactoryService.colorProxy.deleteColor(deleteBean)
    }
}
