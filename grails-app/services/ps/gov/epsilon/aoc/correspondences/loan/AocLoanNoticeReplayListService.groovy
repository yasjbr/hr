package ps.gov.epsilon.aoc.correspondences.loan

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocLoanNoticeReplayListService implements ICorrespondenceListService {

    LoanNoticeReplayListService loanNoticeReplayListService
    @Override
    List<String> getDomainColumns() {
        return loanNoticeReplayListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return loanNoticeReplayListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return loanNoticeReplayListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return loanNoticeReplayListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return loanNoticeReplayListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        return loanNoticeReplayListService.closeList(params)
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return null
    }
}
