package ps.gov.epsilon.aoc.correspondences.promotion

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
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
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.promotion.PromotionRequest
import ps.gov.epsilon.hr.firm.promotion.PromotionRequestService
import ps.gov.epsilon.hr.firm.promotion.PromotionListEmployee
import ps.gov.epsilon.hr.firm.promotion.PromotionListEmployeeNote
import ps.gov.epsilon.hr.firm.promotion.PromotionListEmployeeService
import ps.gov.epsilon.hr.firm.promotion.PromotionListService
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocPromotionListRecordService implements IListRecordService{

    PromotionRequestService promotionRequestService
    PromotionListService promotionListService
    PromotionListEmployeeService promotionListEmployeeService
    EmployeeService employeeService
    PersonService personService
    AocCommonService aocCommonService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "promotionListEmployee.request.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "promotionListEmployee.request.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "employmentDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "financialNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "promotionListEmployee.promotionReason", type: "enum", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "promotionListEmployee.militaryRank", type: "MilitaryRank", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return promotionListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocPromotionRecordList) {
        if (aocPromotionRecordList?.getTotalCount()>0) {
            /**
             * to employee name from core
             */
            List<AocPromotionListRecord> resultList= (List<AocPromotionListRecord>) aocPromotionRecordList?.resultList
            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(resultList?.promotionListEmployee?.request?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocPromotionListRecord aocPromotionListRecord ->

                Request request = aocPromotionListRecord.promotionListEmployee.request?.refresh()
                request?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == request?.employee?.personId
                })
                log.info("getting person name from core: " + request?.employee)
            }
        }
        if(aocPromotionRecordList instanceof PagedResultList){
            PagedList<AocPromotionListRecord> pagedList= new PagedList<AocPromotionListRecord>()
            pagedList.totalCount= aocPromotionRecordList.totalCount
            pagedList.resultList= aocPromotionRecordList.resultList
            return pagedList
        }else{
            return aocPromotionRecordList
        }
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
        queryString << "from ps.gov.epsilon.hr.firm.promotion.PromotionListEmployee hrle "
        queryString << " where hrle.request.firm.id =:firmId "
        if(rootCorrespondenceList){
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.promotionListEmployee.id from AocPromotionListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId']=rootCorrespondenceList.id
        }else{
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.promotionList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId']=hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.promotionListEmployee.id from AocPromotionListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId']=correspondenceList.id
        queryParams['firmId']=hrCorrespondenceList?.firm?.id


        String countquery= "select count(id) " + queryString.toString()
        def hrRecordsCount= PromotionListEmployee.executeQuery(countquery, queryParams)[0]

        List<PromotionListEmployee> hrRecords
        if(hrRecordsCount > 0){
            queryParams['max']= max
            queryParams['offset']= offset
            hrRecords= PromotionListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList= searchPersonData(hrRecords?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { PromotionListEmployee promotionListEmployee ->
                promotionListEmployee?.request?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == promotionListEmployee?.request?.employee?.personId
                })
            }

        }else{
            hrRecords=[]
        }

        PagedList<PromotionListEmployee> pagedList= new PagedList<PromotionListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount= hrRecordsCount


        return pagedList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return promotionRequestService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return promotionRequestService.getInstance(params)
    }

    @Override
    AocPromotionListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocPromotionListRecord aocPromotionListRecord= (AocPromotionListRecord)aocListRecord
        /**
         * add promotion request to promotion list employee
         */
        if(params.listEmployeeId){
            aocPromotionListRecord.promotionListEmployee= PromotionListEmployee.read(params.listEmployeeId)
            if(!aocPromotionListRecord.promotionListEmployee){
                throw new Exception("promotionListEmployee not found for id $params.listEmployeeId")
            }
        }else{
            // save allowance request
//            println("request does not exist, create new")
            Request request = promotionRequestService.save(params)
            if(request.hasErrors()){
                throw new ValidationException("Failed to save promotion request", request.errors)
            }
//            println("request is created, creating new record")
            aocPromotionListRecord.promotionListEmployee= promotionListService.createPromotionListEmployeeFromRequest(request, hrList)
            aocPromotionListRecord.promotionListEmployee.validate()
            aocPromotionListRecord.validate()
//            println("record is created")
            aocPromotionListRecord.save(failOnError:true, flush:true)
        }
        return aocPromotionListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        if (params["employeeId"] && params["requestType"]) {
//            println params
            PromotionRequest promotionRequest = promotionRequestService.getPreCreateInstance(params)

            if (promotionRequest?.hasErrors()) {
                String failMessage = 'allowanceRequest.employee.notFound.error.label'
                return [success: false, message: failMessage]
            }else{
                return [success: true, promotionRequest:promotionRequest]
            }
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocPromotionListRecord record= null
        if(params.listEmployeeId){
            record= AocPromotionListRecord.createCriteria().get {eq('promotionListEmployee.id', params.listEmployeeId)}
        }
        if(!record){
            record= new AocPromotionListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {
        List<AocPromotionListRecord> promotionListRecords= (List<AocPromotionListRecord>) aocListRecordList
        ZonedDateTime now= ZonedDateTime.now()

        // update promotionListEmployee set approved or rejected
        promotionListRecords?.each {aocRecord->
            if(aocRecord.promotionListEmployee.recordStatus== EnumListRecordStatus.NEW){
                aocRecord.promotionListEmployee.recordStatus= aocRecord.recordStatus
                aocRecord.promotionListEmployee.addToPromotionListEmployeeNotes(new PromotionListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, promotionListEmployee: aocRecord.promotionListEmployee))
                aocRecord.promotionListEmployee.save(flush:true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria= new DetachedCriteria(AocPromotionListRecord).build {

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
        AocPromotionListRecord record= (AocPromotionListRecord) listRecord
        return record?.promotionListEmployee?.request?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}