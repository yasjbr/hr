package ps.gov.epsilon.aoc.correspondences.suspension

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.correspondences.suspension.AocSuspensionListRecord
import ps.gov.epsilon.aoc.correspondences.violation.AocViolationListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.suspension.*
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocSuspensionListRecordService implements IListRecordService {

    SuspensionListEmployeeService suspensionListEmployeeService
    AocCommonService aocCommonService
    SuspensionRequestService suspensionRequestService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "suspensionListEmployee.suspensionRequest.id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "suspensionListEmployee.employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "suspensionListEmployee.suspensionType", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "suspensionListEmployee.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "suspensionListEmployee.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "suspensionListEmployee.periodInMonth", type: "Short", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "suspensionListEmployee.recordStatus", type: "enum", source: 'domain'],
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
        return suspensionListEmployeeService.DOMAIN_COLUMNS
    }

    /**
     * search and include values from core
     * @param pagedResultList
     * @return: List of data meets search criteria
     */
    @Override
    PagedList searchWithRemotingValues(Object resultList) {
        List<PersonDTO> personDTOList
        List<Long> personIdList = resultList?.suspensionListEmployee?.suspensionRequest?.employee?.personId
        if (!personIdList?.isEmpty()) {

            //get remoting values from CORE
            personDTOList = aocCommonService.searchPersonData(personIdList)

            //assign remoting values for each list
            resultList?.suspensionListEmployee?.each { SuspensionListEmployee suspensionListEmployee ->
                suspensionListEmployee?.suspensionRequest?.employee?.transientData?.put("personDTO",
                        personDTOList?.find { it.id == suspensionListEmployee?.suspensionRequest?.employee?.personId })
            }
            PagedList<AocSuspensionListRecord> pagedList = new PagedList<AocSuspensionListRecord>()
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
        queryString << SuspensionListEmployee.getName()
        queryString << " hrle where hrle.suspensionRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.suspensionListEmployee.id from AocSuspensionListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.suspensionList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.suspensionListEmployee.id from AocSuspensionListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id


        String countQuery = "select count(id) " + queryString.toString()
        def hrRecordsCount = SuspensionListEmployee.executeQuery(countQuery, queryParams)[0]

        List<SuspensionListEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = SuspensionListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(hrRecords?.suspensionRequest?.employee?.personId?.unique())
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { SuspensionListEmployee suspensionListEmployee ->
                suspensionListEmployee?.suspensionRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == suspensionListEmployee?.suspensionRequest?.employee?.personId
                })
            }
        } else {
            hrRecords = []
        }

        PagedList<SuspensionListEmployee> pagedList = new PagedList<SuspensionListEmployee>()
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
        return suspensionListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    /**
     * returns instance related to encoded id
     * @param params
     * @return SuspensionListEmployee
     */
    @Override
    Object getInstance(GrailsParameterMap params) {
        return suspensionListEmployeeService.getInstance(params)
    }

    /**
     * Saves a listEmployee from params
     * @param params
     * @return a subclass
     */
    @Override
    Object save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocSuspensionListRecord aocSuspensionListRecord = (AocSuspensionListRecord) aocListRecord
        /**
         * add allowance request to allowance list employee
         */
        if (params.listEmployeeId) {
            aocSuspensionListRecord.suspensionListEmployee = SuspensionListEmployee.read(params.listEmployeeId)
            if (!aocSuspensionListRecord.suspensionListEmployee) {
                throw new Exception("suspensionListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            aocSuspensionListRecord.suspensionListEmployee = new SuspensionListEmployee()
            aocSuspensionListRecord.suspensionListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocSuspensionListRecord.suspensionListEmployee.suspensionList = hrList

            // save suspension request
            aocSuspensionListRecord.suspensionListEmployee.suspensionRequest = suspensionRequestService.save(params)

            if (aocSuspensionListRecord?.suspensionListEmployee?.suspensionRequest?.hasErrors()) {
                throw new ValidationException("Failed to save suspension request",
                        aocSuspensionListRecord.suspensionListEmployee.suspensionRequest.errors)
            }
            aocSuspensionListRecord.suspensionListEmployee.currentEmploymentRecord = aocSuspensionListRecord.suspensionListEmployee?.suspensionRequest?.employee?.currentEmploymentRecord
            aocSuspensionListRecord.suspensionListEmployee.currentEmployeeMilitaryRank = aocSuspensionListRecord.suspensionListEmployee?.suspensionRequest?.employee?.currentEmployeeMilitaryRank
        }
        return aocSuspensionListRecord
    }

    /**
     * used to get employee and any other related info necessary for creating request
     * @param params
     * @return SuspensionRequest
     */
    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        if (params["employee.id"]) {
            params["employeeId"] = params["employee.id"]
        }
        if (params["employeeId"]) {
            SuspensionRequest suspensionRequest = suspensionRequestService.getSuspensionRequest(params)
            if (suspensionRequest?.hasErrors()) {
                return [success: false, message: suspensionRequest.errors.globalError?.code]
            }
            return [success: true, suspensionRequest: suspensionRequest]
        } else {
            String failMessage = 'allowanceRequest.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }
/**
     * returns a new empty instance
     * @param params
     * @return AocSuspensionListRecord
     */
    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocSuspensionListRecord record
        if(params.listEmployeeId){
            record= AocSuspensionListRecord.createCriteria().get {eq('suspensionListEmployee.id', params.listEmployeeId)}
        }
        if(!record){
            record= new AocSuspensionListRecord()
        }
        return record
    }

    /**
     * updates hr record status and hr request status to rejected or approved after completing workflow
     * @param aocListRecordList
     */
    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {
        List<AocSuspensionListRecord> aocSuspensionListRecordList = (List<AocSuspensionListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()
        aocSuspensionListRecordList?.each { aocRecord ->
            if (aocRecord.suspensionListEmployee.recordStatus == EnumListRecordStatus.NEW) {
                aocRecord.suspensionListEmployee.recordStatus = aocRecord.recordStatus
                aocRecord.suspensionListEmployee.addToSuspensionListEmployeeNotes(new SuspensionListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, suspensionListEmployee: aocRecord.suspensionListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.suspensionListEmployee.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria= new DetachedCriteria(AocSuspensionListRecord).build {

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
        AocSuspensionListRecord record= (AocSuspensionListRecord) listRecord
        return record?.suspensionListEmployee?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
