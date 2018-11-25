package ps.gov.epsilon.aoc.correspondences.disciplinary

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocDisciplinaryListService implements ICorrespondenceListService{

    DisciplinaryListService disciplinaryListService

    @Override
    List<String> getDomainColumns() {
        return disciplinaryListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return disciplinaryListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return disciplinaryListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return disciplinaryListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return disciplinaryListService.save(params)
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
