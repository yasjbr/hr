package ps.gov.epsilon.aoc.correspondences.suspension

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.suspension.SuspensionListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocSuspensionListService implements ICorrespondenceListService {

    SuspensionListService suspensionListService

    @Override
    List<String> getDomainColumns() {
        return suspensionListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return suspensionListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return suspensionListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return suspensionListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return suspensionListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        return suspensionListService.closeList(params)
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return null
    }
}
