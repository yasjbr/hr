package ps.gov.epsilon.aoc.correspondences.transfer

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.ICorrespondenceListService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListService
import ps.police.common.beans.v1.PagedList

@Transactional
class AocExternalTransferListService implements ICorrespondenceListService{

    ExternalTransferListService externalTransferListService

    @Override
    List<String> getDomainColumns() {
        return externalTransferListService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(GrailsParameterMap params) {
        return externalTransferListService.searchWithRemotingValues(params)
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return externalTransferListService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return externalTransferListService.getInstance(params)
    }

    @Override
    CorrespondenceList save(GrailsParameterMap params) {
        return externalTransferListService.save(params)
    }

    @Override
    CorrespondenceList closeHrList(GrailsParameterMap params) {
        return externalTransferListService.closeList(params)
    }

    @Override
    Object saveApprovalInfo(AocListRecord aocListRecordInstance, GrailsParameterMap params) {
        return externalTransferListService.saveApprovalInfo(((AocExternalTransferListRecord)aocListRecordInstance)?.externalTransferListEmployee, params)
    }
}
