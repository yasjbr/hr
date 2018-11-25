package ps.gov.epsilon.aoc.correspondences.evaluation

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.endOfService.AocEndOfServiceListRecord
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.evaluation.EmployeeEvaluation
import ps.gov.epsilon.hr.firm.evaluation.EmployeeEvaluationService
import ps.gov.epsilon.hr.firm.evaluation.EvaluationListEmployee
import ps.gov.epsilon.hr.firm.evaluation.EvaluationListEmployeeNote
import ps.gov.epsilon.hr.firm.evaluation.EvaluationListEmployeeService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocEvaluationListRecordService implements IListRecordService{

    EvaluationListEmployeeService evaluationListEmployeeService
    EmployeeEvaluationService employeeEvaluationService
    EmployeeService employeeService
    PersonService personService
    FormatService formatService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "evaluationListEmployee.employeeEvaluation.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "evaluationListEmployee.employeeEvaluation.evaluationResult", type: "EvaluationCriterium", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "evaluationListEmployee.employeeEvaluation.evaluationSum", type: "Double", source: 'domain'],
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return evaluationListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocEvaluationRecordList) {
        List<PersonDTO> personDTOList
        if (!aocEvaluationRecordList?.resultList?.isEmpty()) {
            /**
             * to employee name from core
             */
            personDTOList = searchPersonData(aocEvaluationRecordList?.resultList?.evaluationListEmployee?.employeeEvaluation?.employee?.personId)

            /**
             * assign employeeName for each employee in list
             */
            aocEvaluationRecordList?.resultList?.each { AocEvaluationListRecord aocEvaluationListRecord ->
                EvaluationListEmployee evaluationListEmployee = aocEvaluationListRecord.evaluationListEmployee
                evaluationListEmployee?.employeeEvaluation?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == evaluationListEmployee?.employeeEvaluation?.employee?.personId
                })
            }
        }

        if(aocEvaluationRecordList instanceof PagedResultList){
            PagedList<AocEvaluationListRecord> pagedList= new PagedList<AocEvaluationListRecord>()
            pagedList.totalCount= aocEvaluationRecordList.totalCount
            pagedList.resultList= aocEvaluationRecordList.resultList
            return pagedList
        }else{
            return aocEvaluationRecordList
        }
    }

    /**
     * search for evaluationListEmployee records that already exist but not added to AOC Correspondence list
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
        queryString << "from ps.gov.epsilon.hr.firm.evaluation.EvaluationListEmployee hrle "
        queryString << " where hrle.employeeEvaluation.firm.id =:firmId "
        if(rootCorrespondenceList){
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.evaluationListEmployee.id from AocEvaluationListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId']=rootCorrespondenceList.id
        }else{
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.evaluationList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId']=hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.evaluationListEmployee.id from AocEvaluationListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId']=correspondenceList.id
        queryParams['firmId']=hrCorrespondenceList?.firm?.id


        String countquery= "select count(id) " + queryString.toString()
        def hrRecordsCount= EvaluationListEmployee.executeQuery(countquery, queryParams)[0]

        List<EvaluationListEmployee> hrRecords
        if(hrRecordsCount > 0){
            queryParams['max']= max
            queryParams['offset']= offset
            hrRecords= EvaluationListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList= searchPersonData(hrRecords?.employeeEvaluation?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { EvaluationListEmployee evaluationListEmployee ->
                evaluationListEmployee?.employeeEvaluation?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == evaluationListEmployee?.employeeEvaluation?.employee?.personId
                })
            }
        }else{
            hrRecords=[]
        }

        PagedList<EvaluationListEmployee> pagedList= new PagedList<EvaluationListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount= hrRecordsCount

        return pagedList
    }

    /**
     * search person transient data remotely
     * @param personIds
     * @return
     */
    private List<PersonDTO> searchPersonData(List<Long> personIds) {
        SearchBean searchBean
        List<PersonDTO> personDTOList=null

        if (!personIds?.isEmpty()) {
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: personIds))
            personDTOList = personService?.searchPerson(searchBean)?.resultList
        }
        return personDTOList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return evaluationListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return evaluationListEmployeeService.getInstance(params)
    }

    @Override
    AocEvaluationListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocEvaluationListRecord aocEvaluationListRecord= (AocEvaluationListRecord)aocListRecord

        /**
         * add evaluation request to evaluation list employee
         */
        if(params.listEmployeeId){
            aocEvaluationListRecord.evaluationListEmployee= EvaluationListEmployee.read(params.listEmployeeId)
            if(!aocEvaluationListRecord.evaluationListEmployee){
                throw new Exception("evaluationListEmployee not found for id $params.listEmployeeId")
            }
        }else{
            aocEvaluationListRecord.evaluationListEmployee = new EvaluationListEmployee()
            aocEvaluationListRecord.evaluationListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocEvaluationListRecord.evaluationListEmployee.evaluationList = hrList
            aocEvaluationListRecord.evaluationListEmployee?.firm = aocEvaluationListRecord.evaluationListEmployee.evaluationList?.firm


            // save evaluation request
            aocEvaluationListRecord.evaluationListEmployee.employeeEvaluation = employeeEvaluationService.save(params)

            if(aocEvaluationListRecord.evaluationListEmployee.employeeEvaluation.hasErrors()){
                throw new ValidationException("Failed to save evaluation request", aocEvaluationListRecord.evaluationListEmployee.employeeEvaluation.errors)
            }
        }
        return aocEvaluationListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"]) {
            GrailsParameterMap preCreateInstanceParams = new GrailsParameterMap(["employeeId": params["employeeId"], "templateType": params["templateType"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            EmployeeEvaluation employeeEvaluation = employeeEvaluationService.getPreCreateInstance(preCreateInstanceParams)

            if(employeeEvaluation?.hasErrors()){
                List<Map> errors = formatService.formatAllErrors(employeeEvaluation)
                String failMessage = ""
                errors?.each { Map map ->
                    failMessage += map?.message
                }
                return [success: false, message: failMessage]
            }
            return [success: true, employeeEvaluation:employeeEvaluation]
        } else {
            String failMessage = 'employeeEvaluation.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocEvaluationListRecord record
        if(params.listEmployeeId){
            record= AocEvaluationListRecord.createCriteria().get {
                eq('evaluationListEmployee.id', params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocEvaluationListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocEvaluationListRecord> evaluationListRecordList= (List<AocEvaluationListRecord>)aocListRecordList

        ZonedDateTime now= ZonedDateTime.now()

        evaluationListRecordList?.each { aocRecord->
            if(aocRecord.evaluationListEmployee.recordStatus== EnumListRecordStatus.NEW){
                aocRecord.evaluationListEmployee.recordStatus= aocRecord.recordStatus
                aocRecord.evaluationListEmployee.addToEvaluationListEmployeeNotes(new EvaluationListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, evaluationListEmployee: aocRecord.evaluationListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.evaluationListEmployee.save(flush:true)
            }
        }


    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria= new DetachedCriteria(AocEvaluationListRecord).build {

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
        AocEvaluationListRecord record= (AocEvaluationListRecord) listRecord
        return record?.evaluationListEmployee?.employeeEvaluation?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
