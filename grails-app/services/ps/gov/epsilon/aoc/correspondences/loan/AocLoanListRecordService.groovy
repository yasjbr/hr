package ps.gov.epsilon.aoc.correspondences.loan

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.correspondences.violation.AocViolationListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.loan.LoanList
import ps.gov.epsilon.hr.firm.loan.LoanListPerson
import ps.gov.epsilon.hr.firm.loan.LoanListPersonNote
import ps.gov.epsilon.hr.firm.loan.LoanListPersonService
import ps.gov.epsilon.hr.firm.loan.LoanRequest
import ps.gov.epsilon.hr.firm.loan.LoanRequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO

import java.time.ZonedDateTime

@Transactional
class AocLoanListRecordService implements IListRecordService {

    LoanListPersonService loanListPersonService
    LoanRequestService loanRequestService
    AocCommonService aocCommonService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "id", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanListPerson.loanRequest.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanListPerson.loanRequest.firm.name", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanListPerson.loanRequest.requestedJob", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "loanListPerson.loanRequest.transientData.requestedFromOrganizationDTO", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanListPerson.loanRequest.numberOfPositions", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanListPerson.loanRequest.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanListPerson.loanRequest.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanListPerson.effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]
    /**
     * @return List of columns to be rendered in dataTable
     */
    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    /**
     * @return List of columns to be rendered in dataTable
     */
    @Override
    List<String> getHrDomainColumns() {
        return loanListPersonService.DOMAIN_COLUMNS
    }

    /**
     * search and include values from core
     * @param pagedResultList
     * @return: List of data meets search criteria
     */
    @Override
    PagedList searchWithRemotingValues(Object resultList) {
        List<OrganizationDTO> organizationDTOList
        List<Long> organizationIdList = resultList?.loanListPerson?.loanRequest?.requestedFromOrganizationId
        if (!organizationIdList?.isEmpty()) {

            //get remoting values from CORE
            organizationDTOList = aocCommonService.searchOrganizationData(organizationIdList)

            //assign remoting values for each list
            resultList?.loanListPerson?.each { LoanListPerson loanListPerson ->
                loanListPerson?.loanRequest?.transientData?.put("requestedFromOrganizationDTO",
                        organizationDTOList?.find { it.id == loanListPerson?.loanRequest?.requestedFromOrganizationId })
            }
            PagedList<AocLoanListRecord> pagedList = new PagedList<AocLoanListRecord>()
            pagedList.totalCount = resultList.totalCount
            pagedList.resultList = resultList.resultList
            return pagedList
        }
        return null

    }

    /**
     * search records not included in AOC correspondence
     * @param params
     * @return: List of data meets search criteria
     */
    @Override
    PagedList searchNotIncludedRecords(GrailsParameterMap params) {
        Long aocCorrespondenceListId = params.long('aocCorrespondenceList.id')
        AocCorrespondenceList correspondenceList = AocCorrespondenceList.read(aocCorrespondenceListId)
        AocCorrespondenceList rootCorrespondenceList = correspondenceList?.parentCorrespondenceList
        Long firmId = params.long('firm.id') ?: correspondenceList.hrFirmId
        CorrespondenceList hrCorrespondenceList = rootCorrespondenceList ? rootCorrespondenceList.getHrCorrespondenceList(firmId) : correspondenceList?.getHrCorrespondenceList(firmId)

        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Map queryParams = [:]

        StringBuilder queryString = new StringBuilder("from ")
        queryString << LoanListPerson.getName()
        queryString << " hrle where hrle.loanRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.loanListPerson.id from AocLoanListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.loanList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.loanListPerson.id from AocLoanListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id


        String countQuery = "select count(id) " + queryString.toString()
        def hrRecordsCount = LoanListPerson.executeQuery(countQuery, queryParams)[0]

        List<LoanListPerson> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = LoanListPerson.executeQuery(queryString.toString(), queryParams)

            List<OrganizationDTO> organizationDTOList = aocCommonService.searchOrganizationData(hrRecords?.loanRequest?.requestedFromOrganizationId?.unique())
            /**
             * assign organization name for each organization in list
             */
            hrRecords?.each { LoanListPerson loanListPerson ->
                loanListPerson?.loanRequest?.transientData?.put("requestedFromOrganizationDTO", organizationDTOList?.find {
                    it?.id == loanListPerson?.loanRequest?.requestedFromOrganizationId
                })
            }

        } else {
            hrRecords = []
        }
        PagedList<LoanListPerson> pagedList = new PagedList<LoanListPerson>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount = hrRecordsCount

        return pagedList

    }

    /**
     * Convert paged result list to map depends on DOMAINS_COLUMNS.
     * @param def resultList may be PagedResultList or PagedList.
     * @param GrailsParameterMap params the search map
     * @param List < String >  DOMAIN_COLUMNS the list of model column names.
     * @return Map.
     */
    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return loanListPersonService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    /**
     * returns instance related to encoded id
     * @param params
     * @return LoanListPerson
     */
    @Override
    Object getInstance(GrailsParameterMap params) {
        return loanListPersonService.getInstance(params)
    }

    /**
     * Saves a listEmployee from params
     * @param params
     * @return a subclass
     */
    @Override
    Object save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocLoanListRecord aocLoanListRecord = (AocLoanListRecord) aocListRecord
        /**
         * add loan request to loan list person
         */
        if (params.listEmployeeId) {
            aocLoanListRecord.loanListPerson = LoanListPerson.read(params.listEmployeeId)
            if (!aocLoanListRecord.loanListPerson) {
                throw new Exception("loanListPerson not found for id $params.listEmployeeId")
            }
        } else {
            // save loan request & add them to aoc loan list
            aocLoanListRecord.loanListPerson = new LoanListPerson()
            aocLoanListRecord.loanListPerson.loanRequest = loanRequestService.save(params)
            aocLoanListRecord.loanListPerson.isEmploymentProfileProvided = false
            aocLoanListRecord.loanListPerson.recordStatus = EnumListRecordStatus.NEW
            aocLoanListRecord.loanListPerson.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            aocLoanListRecord.loanListPerson.loanList = (LoanList) hrList
            aocLoanListRecord.loanListPerson.firm = aocLoanListRecord.loanListPerson.loanList?.firm


            if (aocLoanListRecord?.loanListPerson?.loanRequest?.hasErrors()) {
                throw new ValidationException("Failed to save loan request",
                        aocLoanListRecord.loanListPerson.loanRequest.errors)
            }


        }
        return aocLoanListRecord
    }

    /**
     * used to get employee and any other related info necessary for creating request
     * @param params
     * @return LoanRequest
     */
    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        LoanRequest loanRequest = new LoanRequest()
        if (loanRequest?.hasErrors()) {
            return [success: false, message: loanRequest.errors.globalError?.code]
        }
        return [success: true, loanRequest: loanRequest]
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }
/**
 * returns a new empty instance
 * @param params
 * @return AocLoanListRecord
 */
    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocLoanListRecord record
        if (params.listEmployeeId) {
            record = AocLoanListRecord.createCriteria().get { eq('loanListPerson.id', params.listEmployeeId) }
        }
        if(!record){
            record = new AocLoanListRecord()
        }
        return record
    }

    /**
     * updates hr record status and hr request status to rejected or approved after completing workflow
     * @param aocListRecordList
     */
    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {
        List<AocLoanListRecord> aocLoanListRecordList = (List<AocLoanListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()
        aocLoanListRecordList?.each { aocRecord ->
            if (aocRecord.loanListPerson.recordStatus == EnumListRecordStatus.NEW) {
                aocRecord.loanListPerson.recordStatus = aocRecord.recordStatus
                aocRecord.loanListPerson.addToLoanListPersonNotes(new LoanListPersonNote(orderNo: orderNumber,
                        noteDate: now, loanListPerson: aocRecord.loanListPerson))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.loanListPerson.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria = new DetachedCriteria(AocLoanListRecord).build {

        }
        return criteria
    }

    /***
     * Checks if employee profile is locked
     * @param listRecord
     * @return
     */
    @Override
    Boolean isEmployeeProfileLocked(AocListRecord listRecord) {
        false
    }
}
