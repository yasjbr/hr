package ps.gov.epsilon.aoc.correspondences.violation

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.disciplinary.EmployeeViolation
import ps.gov.epsilon.hr.firm.disciplinary.EmployeeViolationService
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployee
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployeeNote
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocViolationListRecordService implements IListRecordService {

    ViolationListEmployeeService violationListEmployeeService
    EmployeeViolationService employeeViolationService
    EmployeeService employeeService
    PersonService personService
    AocCommonService aocCommonService

    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "violationListEmployee.employeeViolation.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "violationListEmployee.employeeViolation.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "violationListEmployee.employeeViolation.disciplinaryReason", type: "DisciplinaryReason", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "violationListEmployee.employeeViolation.violationDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "violationListEmployee.employeeViolation.violationStatus", type: "Enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return violationListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocViolationRecordList) {
        if (aocViolationRecordList?.getTotalCount()>0) {
            /**
             * to employee name from core
             */
            List<AocViolationListRecord> resultList= (List<AocViolationListRecord>) aocViolationRecordList?.resultList
            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(resultList?.violationListEmployee?.employeeViolation?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocViolationListRecord aocPromotionListRecord ->

                EmployeeViolation employeeViolation = aocPromotionListRecord.violationListEmployee.employeeViolation?.refresh()
                employeeViolation?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == employeeViolation?.employee?.personId
                })
                log.info("getting person name from core: " + employeeViolation?.employee)
            }
        }
        if(aocViolationRecordList instanceof PagedResultList){
            PagedList<AocViolationListRecord> pagedList= new PagedList<AocViolationListRecord>()
            pagedList.totalCount= aocViolationRecordList.totalCount
            pagedList.resultList= aocViolationRecordList.resultList
            return pagedList
        }else{
            return aocViolationRecordList
        }
    }

    /**
     * search for violationListEmployee records that already exist but not added to AOC Correspondence list
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
        queryString << "from ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployee hrle "
        queryString << " where hrle.employeeViolation.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.violationListEmployee.id from AocViolationListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.violationList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.violationListEmployee.id from AocViolationListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id

//        println(queryString.toString())
//        println(queryParams)

        String countquery = "select count(id) " + queryString.toString()
        def hrRecordsCount = ViolationListEmployee.executeQuery(countquery, queryParams)[0]

        List<ViolationListEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = ViolationListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(hrRecords?.employeeViolation?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { ViolationListEmployee violationListEmployee ->
                violationListEmployee?.employeeViolation?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == violationListEmployee?.employeeViolation?.employee?.personId
                })
            }
        } else {
            hrRecords = []
        }

        PagedList<ViolationListEmployee> pagedList = new PagedList<ViolationListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount = hrRecordsCount

        return pagedList
    }


    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return violationListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return violationListEmployeeService.getInstance(params)
    }

    @Override
    AocViolationListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocViolationListRecord aocViolationListRecord = (AocViolationListRecord) aocListRecord

        /**
         * add violation request to violation list employee
         */
        if (params.listEmployeeId) {
            aocViolationListRecord.violationListEmployee = ViolationListEmployee.read(params.listEmployeeId)
            if (!aocViolationListRecord.violationListEmployee) {
                throw new Exception("violationListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            aocViolationListRecord.violationListEmployee = new ViolationListEmployee()
            aocViolationListRecord.violationListEmployee.violationList = hrList

            // save violation request
            params.violationStatus = EnumViolationStatus.ADD_TO_LIST
            aocViolationListRecord.violationListEmployee.employeeViolation = employeeViolationService.save(params)

            if (aocViolationListRecord.violationListEmployee.employeeViolation.hasErrors()) {
                throw new ValidationException("Failed to save employee violation", aocViolationListRecord.violationListEmployee.employeeViolation.errors)
            }
        }
        return aocViolationListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"]) {
            GrailsParameterMap mapParam

            EmployeeViolation employeeViolation = new EmployeeViolation()
            /**
             * get selected employee
             */
            mapParam = new GrailsParameterMap([id: params["employeeId"], 'firm.id': params['firmId']], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            employeeViolation.employee = employeeService?.getInstanceWithRemotingValues(mapParam)

            return [success: true, employeeViolation: employeeViolation]
        } else {
            String failMessage = 'employeeViolation.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocViolationListRecord record
        if(params.listEmployeeId){
            record= AocViolationListRecord.createCriteria().get {
                eq('violationListEmployee.id',params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocViolationListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocViolationListRecord> violationListRecordList = (List<AocViolationListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()

        violationListRecordList?.each { aocRecord ->
            //TODO : Discuss effect of AOC decision on violations in HR system
//            if(aocRecord.violationListEmployee.recordStatus== EnumListRecordStatus.NEW){
//                aocRecord.violationListEmployee.recordStatus= aocRecord.recordStatus
            aocRecord.violationListEmployee.addToViolationListEmployeeNotes(new ViolationListEmployeeNote(orderNo: orderNumber,
                    noteDate: now, allowanceListEmployee: aocRecord.violationListEmployee))
            // flush is true to make changes visible to next phases through transaction
            aocRecord.violationListEmployee.save(flush: true)
//            }
        }

    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for violation
         */
        DetachedCriteria criteria= new DetachedCriteria(AocViolationListRecord).build {

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
        AocViolationListRecord record= (AocViolationListRecord) listRecord
        return record?.violationListEmployee?.employeeViolation?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
