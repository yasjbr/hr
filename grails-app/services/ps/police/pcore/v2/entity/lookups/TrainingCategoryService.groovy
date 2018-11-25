package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.TrainingCategoryCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.TrainingCategoryDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.ITrainingCategory

@Transactional
class TrainingCategoryService implements  ITrainingCategory {

    ProxyFactoryService proxyFactoryService

    @Override
    TrainingCategoryDTO getTrainingCategory(SearchBean searchBean){
        proxyFactoryService.trainingCategoryProxySetup()
        return proxyFactoryService.trainingCategoryProxy.getTrainingCategory(searchBean)
    }

    @Override
    PagedList<TrainingCategoryDTO> searchTrainingCategory(SearchBean searchBean){
        PagedList<TrainingCategoryDTO> pagedList
        try{
            proxyFactoryService.trainingCategoryProxySetup()
            pagedList = proxyFactoryService.trainingCategoryProxy.searchTrainingCategory(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteTrainingCategory(SearchBean searchBean){
        proxyFactoryService.trainingCategoryProxySetup()
        return proxyFactoryService.trainingCategoryProxy.autoCompleteTrainingCategory(searchBean)
    }

    @Override
    TrainingCategoryCommand saveTrainingCategory(TrainingCategoryCommand trainingCategoryCommand){
        proxyFactoryService.trainingCategoryProxySetup()
        return proxyFactoryService.trainingCategoryProxy.saveTrainingCategory(trainingCategoryCommand)
    }

    @Override
    DeleteBean deleteTrainingCategory(DeleteBean deleteBean){
        proxyFactoryService.trainingCategoryProxySetup()
        return proxyFactoryService.trainingCategoryProxy.deleteTrainingCategory(deleteBean)
    }
}
