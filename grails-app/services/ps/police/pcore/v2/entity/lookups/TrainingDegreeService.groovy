package ps.police.pcore.v2.entity.lookups

import grails.transaction.Transactional
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.pcore.v2.entity.lookups.commands.v1.TrainingDegreeCommand
import ps.police.pcore.v2.entity.lookups.dtos.v1.TrainingDegreeDTO
import ps.police.pcore.v2.entity.lookups.interfaces.v1.ITrainingDegree

@Transactional
class TrainingDegreeService implements  ITrainingDegree {

    ProxyFactoryService proxyFactoryService

    @Override
    TrainingDegreeDTO getTrainingDegree(SearchBean searchBean){
        proxyFactoryService.trainingDegreeProxySetup()
        return proxyFactoryService.trainingDegreeProxy.getTrainingDegree(searchBean)
    }

    @Override
    PagedList<TrainingDegreeDTO> searchTrainingDegree(SearchBean searchBean){
        PagedList<TrainingDegreeDTO> pagedList
        try{
            proxyFactoryService.trainingDegreeProxySetup()
            pagedList = proxyFactoryService.trainingDegreeProxy.searchTrainingDegree(searchBean)
        }
        catch (Exception e){

        }
        return pagedList

    }

    @Override
    String autoCompleteTrainingDegree(SearchBean searchBean){
        proxyFactoryService.trainingDegreeProxySetup()
        return proxyFactoryService.trainingDegreeProxy.autoCompleteTrainingDegree(searchBean)
    }

    @Override
    TrainingDegreeCommand saveTrainingDegree(TrainingDegreeCommand trainingDegreeCommand){
        proxyFactoryService.trainingDegreeProxySetup()
        return proxyFactoryService.trainingDegreeProxy.saveTrainingDegree(trainingDegreeCommand)
    }

    @Override
    DeleteBean deleteTrainingDegree(DeleteBean deleteBean){
        proxyFactoryService.trainingDegreeProxySetup()
        return proxyFactoryService.trainingDegreeProxy.deleteTrainingDegree(deleteBean)
    }
}
