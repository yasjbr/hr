package ps.gov.epsilon.aoc.correspondences.child

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
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.child.ChildListEmployee
import ps.gov.epsilon.hr.firm.child.ChildListEmployeeNote
import ps.gov.epsilon.hr.firm.child.ChildListEmployeeService
import ps.gov.epsilon.hr.firm.child.ChildRequest
import ps.gov.epsilon.hr.firm.child.ChildRequestService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime

@Transactional
class AocChildListRecordService implements IListRecordService{
    ChildListEmployeeService childListEmployeeService
    ChildRequestService childRequestService
    EmployeeService employeeService
    PersonService personService
    AocCommonService aocCommonService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "childListEmployee.childRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "childListEmployee.childRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "childListEmployee.childRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return childListEmployeeService.LIST_DOMAIN_COLUMNS
    }


    @Override
    PagedList searchWithRemotingValues(def aocChildListRecord) {
        if (aocChildListRecord?.getTotalCount()>0) {
            /**
             * to employee name from core
             */
            List<AocChildListRecord> resultList= (List<AocChildListRecord>) aocChildListRecord?.resultList
            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(resultList?.childListEmployee?.childRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocChildListRecord aocPromotionListRecord ->

                ChildRequest childRequest = aocPromotionListRecord.childListEmployee.childRequest?.refresh()
                childRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == childRequest?.employee?.personId
                })
                log.info("getting person name from core: " + childRequest?.employee)
            }
        }
        if(aocChildListRecord instanceof PagedResultList){
            PagedList<AocChildListRecord> pagedList= new PagedList<AocChildListRecord>()
            pagedList.totalCount= aocChildListRecord.totalCount
            pagedList.resultList= aocChildListRecord.resultList
            return pagedList
        }else{
            return aocChildListRecord
        }
    }


    /**
     * search for childListEmployee records that already exist but not added to AOC Correspondence list
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
        queryString << "from ps.gov.epsilon.hr.firm.child.ChildListEmployee hrle "
        queryString << " where hrle.childRequest.firm.id =:firmId "
        if(rootCorrespondenceList){
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.childListEmployee.id from AocChildListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId']=rootCorrespondenceList.id
        }else{
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.childList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId']=hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.childListEmployee.id from AocChildListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId']=correspondenceList.id
        queryParams['firmId']=hrCorrespondenceList?.firm?.id

        String countquery= "select count(id) " + queryString.toString()
        def hrRecordsCount= ChildListEmployee.executeQuery(countquery, queryParams)[0]

        List<ChildListEmployee> hrRecords
        if(hrRecordsCount > 0){
            queryParams['max']= max
            queryParams['offset']= offset
            hrRecords= ChildListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList= searchPersonData(hrRecords?.childRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { ChildListEmployee childListEmployee ->
                childListEmployee?.childRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == childListEmployee?.childRequest?.employee?.personId
                })
            }
        }else{
            hrRecords=[]
        }

        PagedList<ChildListEmployee> pagedList= new PagedList<ChildListEmployee>()
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
        return childListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return childListEmployeeService.getInstance(params)
    }

    @Override
    AocChildListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocChildListRecord aocChildListRecord= (AocChildListRecord)aocListRecord

        /**
         * add child request to child list employee
         */
        if(params.listEmployeeId){
            aocChildListRecord.childListEmployee= ChildListEmployee.read(params.listEmployeeId)
            if(!aocChildListRecord.childListEmployee){
                throw new Exception("childListEmployee not found for id $params.listEmployeeId")
            }
        }else{
            aocChildListRecord.childListEmployee = new ChildListEmployee()
            aocChildListRecord.childListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocChildListRecord.childListEmployee.childList = hrList

            EnumRequestType requestType= params.requestType?EnumRequestType.valueOf(params.requestType):null

            // save child request
            if(!requestType || requestType.requestCategory== EnumRequestCategory.ORIGINAL){
                aocChildListRecord.childListEmployee.childRequest = childRequestService.save(params)
            }else{
                aocChildListRecord.childListEmployee.childRequest = childRequestService.saveOperation(params)
            }

            aocChildListRecord.childListEmployee.firm = aocChildListRecord.childListEmployee.childRequest.firm
            aocChildListRecord.childListEmployee.effectiveDate = searchPersonData([aocChildListRecord.childListEmployee.childRequest.relatedPersonId])[0]?.dateOfBirth

            if(aocChildListRecord.childListEmployee.childRequest.hasErrors()){
                throw new ValidationException("Failed to save child request", aocChildListRecord.childListEmployee.childRequest.errors)
            }
        }
        return aocChildListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"] && params["requestCategory"]) {
            GrailsParameterMap mapParam

            ChildRequest childRequest

            EnumRequestCategory requestCategory= EnumRequestCategory.valueOf(params["requestCategory"])
            String failMessage, requestKey= 'childRequest'
            Map resultMap= [:]

            if(requestCategory == EnumRequestCategory.ORIGINAL){
                childRequest = childRequestService.getPreCreateInstance(params)

            }else{
                if(!params.checked_requestIdsList){
                    failMessage = 'request.notChecked.error.label'
                }else{
                    String checkedRequestId = params["checked_requestIdsList"]
                    if(!checkedRequestId || checkedRequestId?.isEmpty()){
                        failMessage = 'request.notChecked.error.label'
                    }else{
                        requestKey= 'request'
                        mapParam = new GrailsParameterMap([id: checkedRequestId, 'firm.id':params['firmId']], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        println("mapParam = " + mapParam)
                        childRequest = childRequestService.getInstanceWithRemotingValues(mapParam)
                        switch (requestCategory){
                            case EnumRequestCategory.CANCEL:
                                if(childRequest.canCancelRequest){
                                    resultMap['requestType']= EnumRequestType.CHILD_CANCEL_REQUEST
                                    resultMap['formName']= 'cancelRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.cancelled.error.label'
                                }
                                break
                            case EnumRequestCategory.EDIT:
                                if(childRequest.canEditRequest){
                                    resultMap['requestType']= EnumRequestType.CHILD_EDIT_REQUEST
                                    resultMap['formName']= 'editRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.editted.error.label'
                                }
                                break
                        }
                    }
                }
            }
            if(failMessage){
                return [success: false, message: failMessage]
            }
            resultMap['success']= true
            resultMap[requestKey]= childRequest
            return resultMap
        } else {
            String failMessage = 'childRequest.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }



    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        Map result=[:]
        // if request category is original, return selectEmployeeForm
        // else return select requestForm
        if (params["employeeId"] && params["requestCategory"]) {
            EnumRequestCategory requestCategory= EnumRequestCategory.valueOf(params["requestCategory"])
            String employeeId= params.employeeId
            result['success']= true
            result['requestCategory']= requestCategory
            result['employeeId']= employeeId
            result['firmId']= params.firmId
            result['DOMAIN_COLUMNS']= 'LITE_DOMAIN_COLUMNS'
        } else {
            String failMessage = 'childRequest.employee.notFound.error.label'
            result['success']= false
            result['message']= failMessage
        }
        return result
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocChildListRecord record
        if(params.listEmployeeId){
            record= AocChildListRecord.createCriteria().get {
                eq('childListEmployee.id', params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocChildListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocChildListRecord> childListRecordList= (List<AocChildListRecord>)aocListRecordList

        ZonedDateTime now= ZonedDateTime.now()

        // TODO: changing this to sql will have better performance for large numbers

        childListRecordList?.each { aocRecord->
            if(aocRecord.childListEmployee.recordStatus== EnumListRecordStatus.NEW){
                aocRecord.childListEmployee.recordStatus= aocRecord.recordStatus
                aocRecord.childListEmployee.addToChildListEmployeeNotes(new ChildListEmployeeNote(orderNo: orderNumber, noteDate: now, childListEmployee: aocRecord.childListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.childListEmployee.save(flush:true)
            }
        }


    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for return from child
         */
        DetachedCriteria criteria= new DetachedCriteria(AocChildListRecord).build {

        }
        return criteria
    }

    /***
     * checks if employee profile is locked
     * @param listRecord
     * @return
     */
    @Override
    Boolean isEmployeeProfileLocked(AocListRecord listRecord){
        AocChildListRecord childListRecord= (AocChildListRecord) listRecord
        return childListRecord?.childListEmployee?.childRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
