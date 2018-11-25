package ps.gov.epsilon.aoc.correspondences.loan

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.loan.LoanNominatedEmployee
import ps.gov.epsilon.hr.firm.loan.LoanNominatedEmployeeNote
import ps.gov.epsilon.hr.firm.loan.LoanNominatedEmployeeService
import ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayList
import ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayRequest
import ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayRequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Transactional
class AocLoanNoticeReplayListRecordService implements IListRecordService {

    LoanNominatedEmployeeService loanNominatedEmployeeService
    LoanNoticeReplayRequestService loanNoticeReplayRequestService
    AocCommonService aocCommonService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "loanNominatedEmployee.loanNoticeReplayRequest.encodedId", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNominatedEmployee.loanNoticeReplayRequest.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNominatedEmployee.loanNoticeReplayRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "loanNominatedEmployee.employee", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "loanNominatedEmployee.loanNoticeReplayRequest.transientData.requestedByOrganizationDTO", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanNominatedEmployee.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanNominatedEmployee.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "loanNominatedEmployee.effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNominatedEmployee.loanNoticeReplayRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "loanNominatedEmployee.loanNoticeReplayRequest.requestStatus", type: "enum", source: 'domain'],
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
        return loanNominatedEmployeeService.DOMAIN_COLUMNS
    }

    /**
     * search and include values from core
     * @param pagedResultList
     * @return: List of data meets search criteria
     */
    @Override
    PagedList searchWithRemotingValues(Object resultList) {
        List<OrganizationDTO> organizationDTOList
        List<Long> organizationIdList = resultList?.loanNominatedEmployee?.loanNoticeReplayRequest?.requestedByOrganizationId
        if (!organizationIdList?.isEmpty()) {

            //get remoting values from CORE
            organizationDTOList = aocCommonService.searchOrganizationData(organizationIdList)

            //assign remoting values for each list
            resultList?.loanNominatedEmployee?.each { LoanNominatedEmployee loanNominatedEmployee ->
                loanNominatedEmployee?.loanNoticeReplayRequest?.transientData?.put("requestedByOrganizationDTO",
                        organizationDTOList?.find {
                            it.id == loanNominatedEmployee?.loanNoticeReplayRequest?.requestedByOrganizationId
                        })
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
        Long firmId = params.long('firm.id')?:correspondenceList.hrFirmId
        CorrespondenceList hrCorrespondenceList= rootCorrespondenceList?rootCorrespondenceList.getHrCorrespondenceList(firmId):correspondenceList?.getHrCorrespondenceList(firmId)

        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Map queryParams = [:]

        StringBuilder queryString = new StringBuilder("from ")
        queryString << LoanNominatedEmployee.getName()
        queryString << " hrle where hrle.loanNoticeReplayRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.loanNominatedEmployee.id from AocLoanListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.loanList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.loanNominatedEmployee.id from AocLoanListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id


        String countQuery = "select count(id) " + queryString.toString()
        def hrRecordsCount = LoanNominatedEmployee.executeQuery(countQuery, queryParams)[0]

        List<LoanNominatedEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = LoanNominatedEmployee.executeQuery(queryString.toString(), queryParams)

            List<OrganizationDTO> organizationDTOList = aocCommonService.searchOrganizationData(hrRecords?.loanNoticeReplayRequest?.requestedFromOrganizationId?.unique())
            /**
             * assign organization name for each organization in list
             */
            hrRecords?.each { LoanNominatedEmployee loanNominatedEmployee ->
                loanNominatedEmployee?.loanNoticeReplayRequest?.transientData?.put("requestedByOrganizationDTO", organizationDTOList?.find {
                    it?.id == loanNominatedEmployee?.loanNoticeReplayRequest?.requestedByOrganizationId
                })
            }

        } else {
            hrRecords = []
        }
        PagedList<LoanNominatedEmployee> pagedList = new PagedList<LoanNominatedEmployee>()
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
        return loanNominatedEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    /**
     * returns instance related to encoded id
     * @param params
     * @return LoanNominatedEmployee
     */
    @Override
    Object getInstance(GrailsParameterMap params) {
        return loanNominatedEmployeeService.getInstance(params)
    }

    /**
     * Saves a listEmployee from params
     * @param params
     * @return a subclass
     */
    @Override
    Object save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocLoanNoticeReplayListRecord aocLoanNoticeReplayListRecord = (AocLoanNoticeReplayListRecord) aocListRecord
        /**
         * add loan request to loan list person
         */
        if (params.listEmployeeId) {
            aocLoanNoticeReplayListRecord.loanNominatedEmployee = LoanNominatedEmployee.read(params.listEmployeeId)
            if (!aocLoanNoticeReplayListRecord.loanNominatedEmployee) {
                throw new Exception("loanNominatedEmployee not found for id $params.listEmployeeId")
            }
        } else {
            aocLoanNoticeReplayListRecord.loanNominatedEmployee = new LoanNominatedEmployee()
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest = loanNoticeReplayRequestService.save(params)
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.recordStatus = EnumListRecordStatus.NEW
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.currentEmployeeMilitaryRank = aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest?.employee?.currentEmployeeMilitaryRank
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.currentEmploymentRecord = aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest?.employee?.currentEmploymentRecord
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.effectiveDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.fromDate = aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest?.fromDate
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.toDate = aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest?.toDate
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.periodInMonth = ChronoUnit.MONTHS.between(aocLoanNoticeReplayListRecord.loanNominatedEmployee.fromDate, aocLoanNoticeReplayListRecord.loanNominatedEmployee.toDate)
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.orderDate = PCPUtils.DEFAULT_ZONED_DATE_TIME
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayList = (LoanNoticeReplayList) hrList
            aocLoanNoticeReplayListRecord.loanNominatedEmployee.employee = aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest?.employee
            if (aocLoanNoticeReplayListRecord?.loanNominatedEmployee?.loanNoticeReplayRequest?.hasErrors()) {
                throw new ValidationException("Failed to save loan request",
                        aocLoanNoticeReplayListRecord.loanNominatedEmployee.loanNoticeReplayRequest.errors)
            }

        }
        return aocLoanNoticeReplayListRecord
    }

    /**
     * used to get employee and any other related info necessary for creating request
     * @param params
     * @return LoanNoticeReplayRequest
     */
    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        LoanNoticeReplayRequest loanNoticeReplayRequest = new LoanNoticeReplayRequest()
        if (loanNoticeReplayRequest?.hasErrors()) {
            return [success: false, message: loanNoticeReplayRequest.errors.globalError?.code]
        }
        return [success: true, loanNoticeReplayRequest: loanNoticeReplayRequest]
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }
/**
 * returns a new empty instance
 * @param params
 * @return AocLoanNoticeReplayListRecord
 */
    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocLoanNoticeReplayListRecord record
        if(params.listEmployeeId){
            record= AocLoanNoticeReplayListRecord.createCriteria().get {eq('loanNominatedEmployee.id', params.listEmployeeId)}
        }
        if(!record){
            record= new AocLoanNoticeReplayListRecord()
        }
        return record
    }

    /**
     * updates hr record status and hr request status to rejected or approved after completing workflow
     * @param aocListRecordList
     */
    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {
        List<AocLoanNoticeReplayListRecord> aocLoanNoticeReplayListRecordList = (List<AocLoanNoticeReplayListRecord>) aocListRecordList
        ZonedDateTime now = ZonedDateTime.now()
        aocLoanNoticeReplayListRecordList?.each { aocRecord ->
            if (aocRecord.loanNominatedEmployee.recordStatus == EnumListRecordStatus.NEW) {
                aocRecord.loanNominatedEmployee.recordStatus = aocRecord.recordStatus
                aocRecord.loanNominatedEmployee.addToLoanNominatedEmployeeNotes(new LoanNominatedEmployeeNote(orderNo: orderNumber,
                        noteDate: now, loanNominatedEmployee: aocRecord.loanNominatedEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.loanNominatedEmployee.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria = new DetachedCriteria(AocLoanNoticeReplayListRecord).build {

        }
        return criteria
    }

    /***
     * Checks if employee profile is locked
     * @param listRecord
     * @return
     */
    @Override
    Boolean isEmployeeProfileLocked(AocListRecord listRecord){
        AocLoanNoticeReplayListRecord record= (AocLoanNoticeReplayListRecord) listRecord
        return record?.loanNominatedEmployee?.loanNoticeReplayRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
