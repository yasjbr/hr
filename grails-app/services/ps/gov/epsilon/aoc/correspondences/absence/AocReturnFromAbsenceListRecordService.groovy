package ps.gov.epsilon.aoc.correspondences.absence

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.allowance.AocAllowanceListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.correspondences.promotion.AocPromotionListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.absence.Absence
import ps.gov.epsilon.hr.firm.absence.AbsenceService
import ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceListEmployee
import ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceListEmployeeNote
import ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceListEmployeeService
import ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceRequest
import ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceRequestService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocReturnFromAbsenceListRecordService implements IListRecordService {

    ReturnFromAbsenceListEmployeeService returnFromAbsenceListEmployeeService
    ReturnFromAbsenceRequestService returnFromAbsenceRequestService
    EmployeeService employeeService
    PersonService personService
    AocCommonService aocCommonService
    AbsenceService absenceService

    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "returnFromAbsenceListEmployee.returnFromAbsenceRequest.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "returnFromAbsenceListEmployee.returnFromAbsenceRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "returnFromAbsenceListEmployee.actualAbsenceReason", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "returnFromAbsenceListEmployee.actualReturnDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return returnFromAbsenceListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocReturnFromAbsenceRecordList) {
        List<PersonDTO> personDTOList
        if (!aocReturnFromAbsenceRecordList?.resultList?.isEmpty()) {
            /**
             * to employee name from core
             */
            personDTOList = aocCommonService.searchPersonData(aocReturnFromAbsenceRecordList?.resultList?.returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee?.personId)

            /**
             * assign employeeName for each employee in list
             */
            aocReturnFromAbsenceRecordList?.resultList?.each { AocReturnFromAbsenceListRecord aocReturnFromAbsenceListRecord ->
                ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee = aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee
                returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee?.personId
                })
            }
        }

        if (aocReturnFromAbsenceRecordList instanceof PagedResultList) {
            PagedList<AocReturnFromAbsenceListRecord> pagedList = new PagedList<AocReturnFromAbsenceListRecord>()
            pagedList.totalCount = aocReturnFromAbsenceRecordList.totalCount
            pagedList.resultList = aocReturnFromAbsenceRecordList.resultList
            return pagedList
        } else {
            return aocReturnFromAbsenceRecordList
        }
    }

    /**
     * search for returnFromAbsenceListEmployee records that already exist but not added to AOC Correspondence list
     * @param params
     * @return paged list
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

        StringBuilder queryString = new StringBuilder()
        queryString << "from ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceListEmployee hrle "
        queryString << " where hrle.returnFromAbsenceRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is returnFromAbsence, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.returnFromAbsenceListEmployee.id from AocReturnFromAbsenceListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.returnFromAbsenceList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.returnFromAbsenceListEmployee.id from AocReturnFromAbsenceListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :returnFromAbsenceListId )"

        queryParams['returnFromAbsenceListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id

//        println(queryString.toString())
//        println(queryParams)

        String countquery = "select count(id) " + queryString.toString()
        def hrRecordsCount = ReturnFromAbsenceListEmployee.executeQuery(countquery, queryParams)[0]

        List<ReturnFromAbsenceListEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = ReturnFromAbsenceListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(hrRecords?.returnFromAbsenceRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { ReturnFromAbsenceListEmployee returnFromAbsenceListEmployee ->
                returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee?.personId
                })
            }
        } else {
            hrRecords = []
        }

        PagedList<ReturnFromAbsenceListEmployee> pagedList = new PagedList<ReturnFromAbsenceListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount = hrRecordsCount

        return pagedList
    }


    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return returnFromAbsenceListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return returnFromAbsenceListEmployeeService.getInstance(params)
    }

    @Override
    AocReturnFromAbsenceListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocReturnFromAbsenceListRecord aocReturnFromAbsenceListRecord = (AocReturnFromAbsenceListRecord) aocListRecord

        /**
         * add returnFromAbsence request to returnFromAbsence list employee
         */
        if (params.listEmployeeId) {
            aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee = ReturnFromAbsenceListEmployee.read(params.listEmployeeId)
            if (!aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee) {
                throw new Exception("returnFromAbsenceListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            println "------------------------------------------------------------------------------------------------"
            aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee = new ReturnFromAbsenceListEmployee()
            aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.returnFromAbsenceList = hrList
            aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.recordStatus = EnumListRecordStatus.NEW
            // save returnFromAbsence request
            aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.returnFromAbsenceRequest = returnFromAbsenceRequestService.save(params)
            aocReturnFromAbsenceListRecord?.returnFromAbsenceListEmployee?.actualReturnDate = aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.returnFromAbsenceRequest?.actualReturnDate
            aocReturnFromAbsenceListRecord?.returnFromAbsenceListEmployee?.actualAbsenceReason = aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.returnFromAbsenceRequest?.actualAbsenceReason

            if (aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.returnFromAbsenceRequest.hasErrors()) {
                throw new ValidationException("Failed to save returnFromAbsence request", aocReturnFromAbsenceListRecord.returnFromAbsenceListEmployee.returnFromAbsenceRequest.errors)
            }
        }
        return aocReturnFromAbsenceListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        if (params["absenceId"]) {
            GrailsParameterMap mapParam
            ReturnFromAbsenceRequest returnFromAbsenceRequest = new ReturnFromAbsenceRequest()
            /**
             * get selected employee
             */
            mapParam = new GrailsParameterMap([id: params["absenceId"], firmId: params['firmId']], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            Absence absence = absenceService?.getInstanceWithRemotingValues(mapParam)
            returnFromAbsenceRequest?.absence = absence
            returnFromAbsenceRequest?.employee = absence?.employee
            returnFromAbsenceRequest?.requestDate = ZonedDateTime.now()
            return [success: true, returnFromAbsenceRequest: returnFromAbsenceRequest]
        } else {
            String failMessage = 'returnFromAbsenceRequest.absence.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocReturnFromAbsenceListRecord record
        if(params.listEmployeeId){
            record= AocReturnFromAbsenceListRecord.createCriteria().get {eq('returnFromAbsenceListEmployee.id', params.listEmployeeId)}
        }
        if(!record){
            record= new AocReturnFromAbsenceListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocReturnFromAbsenceListRecord> returnFromAbsenceListRecordList = (List<AocReturnFromAbsenceListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()

        returnFromAbsenceListRecordList?.each { aocRecord ->
            //TODO : Discuss effect of AOC decision on returnFromAbsences in HR system
//            if(aocRecord.returnFromAbsenceListEmployee.recordStatus== EnumListRecordStatus.NEW){
//                aocRecord.returnFromAbsenceListEmployee.recordStatus= aocRecord.recordStatus
            aocRecord.returnFromAbsenceListEmployee.addToReturnFromAbsenceListEmployeeNotes(new ReturnFromAbsenceListEmployeeNote(orderNo: orderNumber,
                    noteDate: now, returnFromAbsenceListEmployee: aocRecord.returnFromAbsenceListEmployee))
            // flush is true to make changes visible to next phases through transaction
            aocRecord.returnFromAbsenceListEmployee.save(flush: true)
//            }
        }

    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for return from absence
         */
        DetachedCriteria criteria= new DetachedCriteria(AocReturnFromAbsenceListRecord).build {

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
        AocReturnFromAbsenceListRecord record= (AocReturnFromAbsenceListRecord) listRecord
        return record?.returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
