package ps.gov.epsilon.aoc.correspondences.violation

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocViolationListService implements ICorrespondenceListService{

    ViolationListService violationListService

    @Override
    List<String> getDomainColumns() {
        return violationListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return violationListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return violationListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return violationListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return violationListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        // List is not closed in HR
        return null
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return null
    }
}
