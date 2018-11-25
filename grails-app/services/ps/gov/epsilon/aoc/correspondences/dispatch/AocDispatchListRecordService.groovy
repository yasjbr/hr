package ps.gov.epsilon.aoc.correspondences.dispatch

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
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.dispatch.DispatchList
import ps.gov.epsilon.hr.firm.dispatch.DispatchListEmployee
import ps.gov.epsilon.hr.firm.dispatch.DispatchListEmployeeNote
import ps.gov.epsilon.hr.firm.dispatch.DispatchListEmployeeService
import ps.gov.epsilon.hr.firm.dispatch.DispatchListService
import ps.gov.epsilon.hr.firm.dispatch.DispatchRequest
import ps.gov.epsilon.hr.firm.dispatch.DispatchRequestService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime

@Transactional
class AocDispatchListRecordService implements IListRecordService{

    DispatchListEmployeeService dispatchListEmployeeService
    DispatchRequestService dispatchRequestService
    DispatchListService dispatchListService
    AocCommonService aocCommonService
    EmployeeService employeeService
    PersonService personService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "dispatchListEmployee.dispatchRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "dispatchListEmployee.dispatchRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "dispatchListEmployee.dispatchRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return dispatchListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocDispatchRecordList) {
        if (aocDispatchRecordList?.getTotalCount()>0) {
            /**
             * to employee name from core
             */
            List<AocDispatchListRecord> resultList= (List<AocDispatchListRecord>) aocDispatchRecordList?.resultList
            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(resultList?.dispatchListEmployee?.dispatchRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocDispatchListRecord aocPromotionListRecord ->

                DispatchRequest dispatchRequest = aocPromotionListRecord.dispatchListEmployee.dispatchRequest?.refresh()
                dispatchRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == dispatchRequest?.employee?.personId
                })
                log.info("getting person name from core: " + dispatchRequest?.employee)
            }
        }
        if(aocDispatchRecordList instanceof PagedResultList){
            PagedList<AocDispatchListRecord> pagedList= new PagedList<AocDispatchListRecord>()
            pagedList.totalCount= aocDispatchRecordList.totalCount
            pagedList.resultList= aocDispatchRecordList.resultList
            return pagedList
        }else{
            return aocDispatchRecordList
        }
    }

    /**
     * search for dispatchListEmployee records that already exist but not added to AOC Correspondence list
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
        queryString << "from ps.gov.epsilon.hr.firm.dispatch.DispatchListEmployee hrle "
        queryString << " where hrle.dispatchRequest.firm.id =:firmId "
        if(rootCorrespondenceList){
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.dispatchListEmployee.id from AocDispatchListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId']=rootCorrespondenceList.id
        }else{
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.dispatchList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId']=hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.dispatchListEmployee.id from AocDispatchListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId']=correspondenceList.id
        queryParams['firmId']=hrCorrespondenceList?.firm?.id

        String countquery= "select count(id) " + queryString.toString()
        def hrRecordsCount= DispatchListEmployee.executeQuery(countquery, queryParams)[0]

        List<DispatchListEmployee> hrRecords
        if(hrRecordsCount > 0){
            queryParams['max']= max
            queryParams['offset']= offset
            hrRecords= DispatchListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList= aocCommonService.searchPersonData(hrRecords?.dispatchRequest?.employee?.personId)
            List<GovernorateDTO> governorates = aocCommonService.searchGovernoratesData(hrRecords?.currentEmploymentRecord?.department?.governorateId?.toList()?.unique())

            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { DispatchListEmployee dispatchListEmployee ->
                dispatchListEmployee?.dispatchRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == dispatchListEmployee?.dispatchRequest?.employee?.personId
                })
                dispatchListEmployee?.currentEmploymentRecord?.department?.transientData?.put("governorateDTO", governorates?.find {
                    it.id == dispatchListEmployee?.currentEmploymentRecord?.department?.governorateId
                })
            }
        }else{
            hrRecords=[]
        }

        PagedList<DispatchListEmployee> pagedList= new PagedList<DispatchListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount= hrRecordsCount

        return pagedList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return dispatchListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return dispatchListEmployeeService.getInstance(params)
    }

    @Override
    AocDispatchListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocDispatchListRecord aocDispatchListRecord= (AocDispatchListRecord)aocListRecord
        /**
         * add dispatch request to dispatch list employee
         */
        if(params.listEmployeeId){
            aocDispatchListRecord.dispatchListEmployee= DispatchListEmployee.read(params.listEmployeeId)
            if(!aocDispatchListRecord.dispatchListEmployee){
                throw new Exception("dispatchListEmployee not found for id $params.listEmployeeId")
            }
        }else{

            aocDispatchListRecord.dispatchListEmployee = new DispatchListEmployee()
            aocDispatchListRecord.dispatchListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocDispatchListRecord.dispatchListEmployee.dispatchList = hrList

            EnumRequestType requestType= params.requestType?EnumRequestType.valueOf(params.requestType):null

            // save dispatch request
            if(!requestType || requestType.requestCategory== EnumRequestCategory.ORIGINAL){
                aocDispatchListRecord.dispatchListEmployee.dispatchRequest = dispatchRequestService.save(params)
            }else{
                aocDispatchListRecord.dispatchListEmployee.dispatchRequest = dispatchRequestService.saveOperation(params)
            }

            aocDispatchListRecord?.dispatchListEmployee?.fromDate = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.fromDate
            aocDispatchListRecord?.dispatchListEmployee?.toDate = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.toDate
            aocDispatchListRecord?.dispatchListEmployee?.currentEmploymentRecord = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.currentEmploymentRecord
            aocDispatchListRecord?.dispatchListEmployee?.currentEmployeeMilitaryRank = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.currentEmployeeMilitaryRank
            aocDispatchListRecord?.dispatchListEmployee?.periodInMonths = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.periodInMonths
            aocDispatchListRecord?.dispatchListEmployee?.nextVerificationDate = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.nextVerificationDate
            aocDispatchListRecord?.dispatchListEmployee?.organizationId = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.organizationId
            aocDispatchListRecord?.dispatchListEmployee?.organizationName = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.organizationName
            aocDispatchListRecord?.dispatchListEmployee?.educationMajorId = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.educationMajorId
            aocDispatchListRecord?.dispatchListEmployee?.educationMajorName = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.educationMajorName
            aocDispatchListRecord?.dispatchListEmployee?.locationId = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.locationId
            aocDispatchListRecord?.dispatchListEmployee?.unstructuredLocation = aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.unstructuredLocation

            if(aocDispatchListRecord?.dispatchListEmployee?.dispatchRequest?.hasErrors()){
                throw new ValidationException("Failed to save dispatch request", aocDispatchListRecord.dispatchListEmployee.dispatchRequest.errors)
            }
        }
        return aocDispatchListRecord
    }


    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"] && params["requestCategory"]) {
            GrailsParameterMap mapParam

            DispatchRequest dispatchRequest

            EnumRequestCategory requestCategory= EnumRequestCategory.valueOf(params["requestCategory"])
            String failMessage, requestKey= 'dispatchRequest'
            Map resultMap= [:]

            if(requestCategory == EnumRequestCategory.ORIGINAL){
                dispatchRequest = dispatchRequestService?.getPreCreateInstance(params)
                if(dispatchRequest?.hasErrors()){
                    resultMap['success']= false

                    dispatchRequest?.errors?.allErrors?.each {def error->
                        failMessage = error?.code
                    }

                    resultMap['message']= failMessage
                    return resultMap
                }
            }else{
                if(!params.checked_requestIdsList){
                    failMessage = 'request.notChecked.error.label'
                }else{
                    String checkedRequestId = params["checked_requestIdsList"]
                    if(!checkedRequestId || checkedRequestId?.isEmpty()){
                        failMessage = 'request.notChecked.error.label'
                    }else{
                        requestKey= 'request'
                        mapParam = new GrailsParameterMap([id: checkedRequestId, 'firm.id':params['firmId']],
                                WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        println("mapParam = " + mapParam)
                        dispatchRequest = dispatchRequestService.getInstanceWithRemotingValues(mapParam)
                        switch (requestCategory){
                            case EnumRequestCategory.CANCEL:
                                if(dispatchRequest.canCancelRequest){
                                    resultMap['requestType']= EnumRequestType.DISPATCH_CANCEL_REQUEST
                                    resultMap['formName']= 'cancelRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.cancelled.error.label'
                                }
                                break
                            case EnumRequestCategory.EDIT:
                                if(dispatchRequest.canEditRequest){
                                    resultMap['requestType']= EnumRequestType.DISPATCH_EDIT_REQUEST
                                    resultMap['formName']= 'editRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.editted.error.label'
                                }
                                break
                            case EnumRequestCategory.STOP:
                                if(dispatchRequest.canStopRequest){
                                    resultMap['requestType']= EnumRequestType.DISPATCH_STOP_REQUEST
                                    resultMap['formName']= 'stopRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.stopped.error.label'
                                }
                                break
                            case EnumRequestCategory.EXTEND:
                                if(dispatchRequest.canExtendRequest){
                                    resultMap['requestType']= EnumRequestType.DISPATCH_EXTEND_REQUEST
                                    resultMap['formName']= 'extendRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.extended.error.label'
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
            resultMap[requestKey]= dispatchRequest
            return resultMap
        } else {
            String failMessage = 'dispatchRequest.employee.notFound.error.label'
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
            if(params["dispatchType.id"]){
                result['dispatchTypeId']= params["dispatchType.id"]
            }
            result['DOMAIN_COLUMNS']= 'LITE_DOMAIN_COLUMNS'
        } else {
            String failMessage = 'dispatchRequest.employee.notFound.error.label'
            result['success']= false
            result['message']= failMessage
        }
        return result
    }


    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocDispatchListRecord record
        if(params.listEmployeeId){
            record= AocDispatchListRecord.createCriteria().get {
                eq('dispatchListEmployee.id', params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocDispatchListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocDispatchListRecord> aocDispatchListRecord= (List<AocDispatchListRecord>)aocListRecordList

        ZonedDateTime now= ZonedDateTime.now()

        aocDispatchListRecord?.each { aocRecord->
            if(aocRecord.dispatchListEmployee.recordStatus== EnumListRecordStatus.NEW){
                aocRecord.dispatchListEmployee.recordStatus= aocRecord.recordStatus
                aocRecord.dispatchListEmployee.addToDispatchListEmployeeNotes(new DispatchListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, dispatchListEmployee: aocRecord.dispatchListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.dispatchListEmployee.save(flush:true)
            }
        }

    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for dispatch
         */
        DetachedCriteria criteria= new DetachedCriteria(AocDispatchListRecord).build {

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
        AocDispatchListRecord record= (AocDispatchListRecord) listRecord
        return record?.dispatchListEmployee?.dispatchRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
