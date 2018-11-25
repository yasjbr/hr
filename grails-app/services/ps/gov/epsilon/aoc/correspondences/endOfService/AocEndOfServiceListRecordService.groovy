package ps.gov.epsilon.aoc.correspondences.endOfService

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.correspondences.evaluation.AocEvaluationListRecord
import ps.gov.epsilon.aoc.correspondences.violation.AocViolationListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.employmentService.EmploymentServiceRequest
import ps.gov.epsilon.hr.firm.employmentService.EmploymentServiceRequestService
import ps.gov.epsilon.hr.firm.employmentService.ServiceListEmployee
import ps.gov.epsilon.hr.firm.employmentService.ServiceListEmployeeNote
import ps.gov.epsilon.hr.firm.employmentService.ServiceListEmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocEndOfServiceListRecordService implements IListRecordService{

    ServiceListEmployeeService serviceListEmployeeService
    EmploymentServiceRequestService employmentServiceRequestService
    EmployeeService employeeService
    PersonService personService
    AocCommonService aocCommonService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "serviceListEmployee.employmentServiceRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "serviceListEmployee.employmentServiceRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return serviceListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocEndOfServiceRecordList) {
        List<PersonDTO> personDTOList
        if (!aocEndOfServiceRecordList?.resultList?.isEmpty()) {
            /**
             * to employee name from core
             */
            personDTOList = aocCommonService.searchPersonData(aocEndOfServiceRecordList?.resultList?.serviceListEmployee?.employmentServiceRequest?.employee?.personId)

            /**
             * assign employeeName for each employee in list
             */
            aocEndOfServiceRecordList?.resultList?.each { AocEndOfServiceListRecord aocEndOfServiceListRecord ->
                ServiceListEmployee serviceListEmployee = aocEndOfServiceListRecord.serviceListEmployee
                serviceListEmployee?.employmentServiceRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == serviceListEmployee?.employmentServiceRequest?.employee?.personId
                })
            }
        }

        if(aocEndOfServiceRecordList instanceof PagedResultList){
            PagedList<AocEndOfServiceListRecord> pagedList= new PagedList<AocEndOfServiceListRecord>()
            pagedList.totalCount= aocEndOfServiceRecordList.totalCount
            pagedList.resultList= aocEndOfServiceRecordList.resultList
            return pagedList
        }else{
            return aocEndOfServiceRecordList
        }
    }

    /**
     * search for serviceListEmployee records that already exist but not added to AOC Correspondence list
     * @param params
     * @return paged list
     */
    @Override
    PagedList searchNotIncludedRecords(GrailsParameterMap params) {
        Long aocCorrespondenceListId = params.long('aocCorrespondenceList.id')
        AocCorrespondenceList correspondenceList= AocCorrespondenceList.read(aocCorrespondenceListId)
        AocCorrespondenceList rootCorrespondenceList= correspondenceList?.parentCorrespondenceList
        Long firmId = params.long('firm.id')?:correspondenceList.hrFirmId
        CorrespondenceList hrCorrespondenceList= rootCorrespondenceList?rootCorrespondenceList.getHrCorrespondenceList(firmId):correspondenceList?.getHrCorrespondenceList(firmId)

        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Map queryParams= [:]

        StringBuilder queryString= new StringBuilder()
        queryString << "from ps.gov.epsilon.hr.firm.employmentService.ServiceListEmployee hrle "
        queryString << " where hrle.employmentServiceRequest.firm.id =:firmId "
        if(rootCorrespondenceList){
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.serviceListEmployee.id from AocEndOfServiceListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId']=rootCorrespondenceList.id
        }else{
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.serviceList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId']=hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.serviceListEmployee.id from AocEndOfServiceListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId']=correspondenceList.id
        queryParams['firmId']=hrCorrespondenceList?.firm?.id

        String countquery= "select count(id) " + queryString.toString()
        def hrRecordsCount= ServiceListEmployee.executeQuery(countquery, queryParams)[0]

        List<ServiceListEmployee> hrRecords
        if(hrRecordsCount > 0){
            queryParams['max']= max
            queryParams['offset']= offset
            hrRecords= ServiceListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList= aocCommonService.searchPersonData(hrRecords?.employmentServiceRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { ServiceListEmployee serviceListEmployee ->
                serviceListEmployee?.employmentServiceRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == serviceListEmployee?.employmentServiceRequest?.employee?.personId
                })
            }
        }else{
            hrRecords=[]
        }

        PagedList<ServiceListEmployee> pagedList= new PagedList<ServiceListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount= hrRecordsCount

        return pagedList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return serviceListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return serviceListEmployeeService.getInstance(params)
    }

    @Override
    AocEndOfServiceListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocEndOfServiceListRecord aocEndOfServiceListRecord= (AocEndOfServiceListRecord)aocListRecord

        /**
         * add service request to service list employee
         */
        if(params.listEmployeeId){
            aocEndOfServiceListRecord.serviceListEmployee= ServiceListEmployee.read(params.listEmployeeId)
            if(!aocEndOfServiceListRecord.serviceListEmployee){
                throw new Exception("serviceListEmployee not found for id $params.listEmployeeId")
            }
        }else{
            aocEndOfServiceListRecord.serviceListEmployee = new ServiceListEmployee()
            aocEndOfServiceListRecord.serviceListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocEndOfServiceListRecord.serviceListEmployee.serviceList = hrList

            // save service request
            aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest = employmentServiceRequestService.save(params)
            aocEndOfServiceListRecord.serviceListEmployee.firm = aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest.firm
            aocEndOfServiceListRecord.serviceListEmployee.employee = aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest?.employee
            aocEndOfServiceListRecord.serviceListEmployee.currentEmployeeMilitaryRank = aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest?.currentEmployeeMilitaryRank
            aocEndOfServiceListRecord.serviceListEmployee.currentEmploymentRecord = aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest?.currentEmploymentRecord
            aocEndOfServiceListRecord.serviceListEmployee?.serviceActionReason = aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest?.serviceActionReason
            aocEndOfServiceListRecord.serviceListEmployee?.dateEffective = PCPUtils.getDEFAULT_ZONED_DATE_TIME()

            if(aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest.hasErrors()){
                throw new ValidationException("Failed to save service request", aocEndOfServiceListRecord.serviceListEmployee.employmentServiceRequest.errors)
            }
        }
        return aocEndOfServiceListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"]) {
            //GrailsParameterMap mapParam = new GrailsParameterMap([requestType: EnumRequestType.END_OF_SERVICE], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            params["requestType"] = EnumRequestType.END_OF_SERVICE
            EmploymentServiceRequest employmentServiceRequest = employmentServiceRequestService.getPreCreateInstance(params)

            return [success: true, employmentServiceRequest:employmentServiceRequest]
        } else {
            String failMessage = 'employmentServiceRequest.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocEndOfServiceListRecord record
        if(params.listEmployeeId){
            record= AocEndOfServiceListRecord.createCriteria().get {
                eq('serviceListEmployee.id', params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocEndOfServiceListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocEndOfServiceListRecord> aocEndOfServiceListRecord= (List<AocEndOfServiceListRecord>)aocListRecordList

        ZonedDateTime now= ZonedDateTime.now()

        aocEndOfServiceListRecord?.each { aocRecord->
            if(aocRecord.serviceListEmployee.recordStatus== EnumListRecordStatus.NEW){
                aocRecord.serviceListEmployee.recordStatus= aocRecord.recordStatus
                aocRecord.serviceListEmployee.addToServiceListEmployeeNotes(new ServiceListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, serviceListEmployee: aocRecord.serviceListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.serviceListEmployee.save(flush:true)
            }
        }

    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria= new DetachedCriteria(AocEndOfServiceListRecord).build {

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
        AocEndOfServiceListRecord record= (AocEndOfServiceListRecord) listRecord
        return record?.serviceListEmployee?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
