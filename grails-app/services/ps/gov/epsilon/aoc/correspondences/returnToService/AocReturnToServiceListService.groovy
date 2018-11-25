package ps.gov.epsilon.aoc.correspondences.returnToService

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType
import ps.gov.epsilon.hr.firm.employmentService.ServiceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.beans.v1.PagedList

@Transactional
class AocReturnToServiceListService implements ICorrespondenceListService{

    ServiceListService serviceListService

    @Override
    List<String> getDomainColumns() {
        return serviceListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        params.isRecallToServiceType = true
        return serviceListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return serviceListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return serviceListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        params.serviceListType = EnumServiceListType.RETURN_TO_SERVICE
        return serviceListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        return serviceListService.closeList(params)
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return null
    }
}
