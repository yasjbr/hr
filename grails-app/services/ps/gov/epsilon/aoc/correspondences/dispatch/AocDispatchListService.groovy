package ps.gov.epsilon.aoc.correspondences.dispatch

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.dispatch.DispatchListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocDispatchListService implements ICorrespondenceListService{

    DispatchListService dispatchListService

    @Override
    List<String> getDomainColumns() {
        return dispatchListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return dispatchListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return dispatchListService.resultListToMap(resultList, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return dispatchListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        params["dispatchListType"] = "DISPATCH"
        return dispatchListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        return dispatchListService.closeList(params)
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return null
    }
}
