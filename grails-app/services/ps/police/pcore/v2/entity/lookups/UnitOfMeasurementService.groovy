package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.UnitOfMeasurementCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.UnitOfMeasurementDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.IUnitOfMeasurement

@Transactional
class UnitOfMeasurementService implements IUnitOfMeasurement {


    ProxyFactoryService proxyFactoryService

    @Override
    UnitOfMeasurementDTO getUnitOfMeasurement(SearchBean searchBean){
        proxyFactoryService.unitOfMeasurementProxySetup()
        return proxyFactoryService.unitOfMeasurementProxy.getUnitOfMeasurement(searchBean)
    }

    @Override
    PagedList<UnitOfMeasurementDTO> searchUnitOfMeasurement(SearchBean searchBean){
        PagedList<UnitOfMeasurementDTO> pagedList
        try{
            proxyFactoryService.unitOfMeasurementProxySetup()
            pagedList = proxyFactoryService.unitOfMeasurementProxy.searchUnitOfMeasurement(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteUnitOfMeasurement(SearchBean searchBean){
        proxyFactoryService.unitOfMeasurementProxySetup()
        return proxyFactoryService.unitOfMeasurementProxy.autoCompleteUnitOfMeasurement(searchBean)
    }

    @Override
    UnitOfMeasurementCommand saveUnitOfMeasurement(UnitOfMeasurementCommand unitOfMeasurementCommand){
        proxyFactoryService.unitOfMeasurementProxySetup()
        return proxyFactoryService.unitOfMeasurementProxy.saveBlock(unitOfMeasurementCommand)
    }

    @Override
    DeleteBean deleteUnitOfMeasurement(DeleteBean deleteBean){
        proxyFactoryService.unitOfMeasurementProxySetup()
        return proxyFactoryService.unitOfMeasurementProxy.deleteBlock(deleteBean)
    }
}
