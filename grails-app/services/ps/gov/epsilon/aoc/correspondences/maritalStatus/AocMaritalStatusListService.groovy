package ps.gov.epsilon.aoc.correspondences.maritalStatus

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocMaritalStatusListService implements ICorrespondenceListService{

    MaritalStatusListService maritalStatusListService

    @Override
    List<String> getDomainColumns() {
        return maritalStatusListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return maritalStatusListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return maritalStatusListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return maritalStatusListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return maritalStatusListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        return maritalStatusListService.closeList(params)
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return maritalStatusListService.saveApprovalInfo(((AocMaritalStatusListRecord)aocListRecordInstance)?.maritalStatusListEmployee, params)
    }
}
