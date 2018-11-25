package ps.gov.epsilon.aoc.correspondences.allowance

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
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployee
import ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployeeNote
import ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployeeService
import ps.gov.epsilon.hr.firm.allowance.AllowanceRequest
import ps.gov.epsilon.hr.firm.allowance.AllowanceRequestService
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceType
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceTypeService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocAllowanceListRecordService implements IListRecordService{

    AllowanceListEmployeeService allowanceListEmployeeService
    AllowanceRequestService allowanceRequestService
    EmployeeService employeeService
    AllowanceTypeService allowanceTypeService
    PersonService personService
    FirmSettingService firmSettingService
    AocCommonService aocCommonService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "allowanceListEmployee.allowanceRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "allowanceListEmployee.allowanceRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceListEmployee.allowanceRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceType.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceListEmployee.allowanceRequest.toDate", type: "ZonedDate", source: 'domain'],
//            [sort: true, search: false, hidden: false, name: "allowanceListEmployee.allowanceRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return allowanceListEmployeeService.LIST_DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocAllowanceRecordList) {
        List<PersonDTO> personDTOList
        if (aocAllowanceRecordList?.getTotalCount()>0) {
            log.info("searching allowance records with remoting values")
            /**
             * to employee name from core
             */
            List<AocAllowanceListRecord> resultList= (List<AocAllowanceListRecord>) aocAllowanceRecordList?.resultList
            personDTOList = aocCommonService.searchPersonData(resultList?.allowanceListEmployee?.allowanceRequest?.employee?.personId)

            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocAllowanceListRecord aocAllowanceListRecord ->
//                println("record id=$aocAllowanceListRecord.id, employee= $aocAllowanceListRecord.employee, recordStatus= $aocAllowanceListRecord.recordStatus")
                AllowanceRequest allowanceRequest = aocAllowanceListRecord.allowanceListEmployee.allowanceRequest?.refresh()
                allowanceRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == allowanceRequest?.employee?.personId
                })
                log.info("getting person name from core: " + allowanceRequest?.employee)
            }
        }

        if(aocAllowanceRecordList instanceof PagedResultList){
            PagedList<AocAllowanceListRecord> pagedList= new PagedList<AocAllowanceListRecord>()
            pagedList.totalCount= aocAllowanceRecordList.totalCount
            pagedList.resultList= aocAllowanceRecordList.resultList
            return pagedList
        }else{
            return aocAllowanceRecordList
        }
    }

    /**
     * search for allowanceListEmployee records that already exist but not added to AOC Correspondence list
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
        queryString << "from ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployee hrle "
        queryString << " where hrle.allowanceRequest.firm.id =:firmId "
        if(rootCorrespondenceList){
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.allowanceListEmployee.id from AocAllowanceListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId']=rootCorrespondenceList.id
        }else{
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.allowanceList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId']=hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.allowanceListEmployee.id from AocAllowanceListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId']=correspondenceList.id
        queryParams['firmId']=hrCorrespondenceList?.firm?.id

//        println(queryString.toString())
//        println(queryParams)

        String countquery= "select count(id) " + queryString.toString()
        def hrRecordsCount= AllowanceListEmployee.executeQuery(countquery, queryParams)[0]

        List<AllowanceListEmployee> hrRecords
        if(hrRecordsCount > 0){
            queryParams['max']= max
            queryParams['offset']= offset
            hrRecords= AllowanceListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList= aocCommonService.searchPersonData(hrRecords?.allowanceRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { AllowanceListEmployee allowanceListEmployee ->
                allowanceListEmployee?.allowanceRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == allowanceListEmployee?.allowanceRequest?.employee?.personId
                })
            }
        }else{
            hrRecords=[]
        }

        PagedList<AllowanceListEmployee> pagedList= new PagedList<AllowanceListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount= hrRecordsCount

        return pagedList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return allowanceListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return allowanceListEmployeeService.getInstance(params)
    }

    @Override
    AocAllowanceListRecord save(AocListRecord aocListRecord, CorrespondenceList hrCorrespondenceList, GrailsParameterMap params) {
        AocAllowanceListRecord aocAllowanceListRecord= (AocAllowanceListRecord)aocListRecord

        /**
         * add allowance request to allowance list employee
         */
        if(params.listEmployeeId){
            aocAllowanceListRecord.allowanceListEmployee= AllowanceListEmployee.read(params.listEmployeeId)
            if(!aocAllowanceListRecord.allowanceListEmployee){
                throw new Exception("allowanceListEmployee not found for id $params.listEmployeeId")
            }
        }else{
            aocAllowanceListRecord.allowanceListEmployee = new AllowanceListEmployee()
            aocAllowanceListRecord.allowanceListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocAllowanceListRecord.allowanceListEmployee.allowanceList = hrCorrespondenceList

            EnumRequestType requestType= params.requestType?EnumRequestType.valueOf(params.requestType):null

            // save allowance request
            if(!requestType || requestType.requestCategory== EnumRequestCategory.ORIGINAL){
                aocAllowanceListRecord.allowanceListEmployee.allowanceRequest = allowanceRequestService.save(params)
            }else{
                aocAllowanceListRecord.allowanceListEmployee.allowanceRequest = allowanceRequestService.saveOperation(params)
            }

            if(aocAllowanceListRecord.allowanceListEmployee.allowanceRequest.hasErrors()){
                throw new ValidationException("Failed to save allowance request", aocAllowanceListRecord.allowanceListEmployee.allowanceRequest.errors)
            }
        }
        return aocAllowanceListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"] && params["allowanceType.id"] && params["requestCategory"]) {
            GrailsParameterMap mapParam

            AllowanceRequest allowanceRequest

            EnumRequestCategory requestCategory= EnumRequestCategory.valueOf(params["requestCategory"])
            String failMessage, requestKey= 'allowanceRequest'
            Map resultMap= [:]

            if(requestCategory == EnumRequestCategory.ORIGINAL){
                allowanceRequest = new AllowanceRequest()
                /**
                 * get selected employee
                 */
                mapParam = new GrailsParameterMap([id: params["employeeId"], 'firm.id':params['firmId']],
                        WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                allowanceRequest.employee = employeeService?.getInstanceWithRemotingValues(mapParam)

                /**
                 * get selected allowance type
                 */
                // if firm is centalized with aoc, then allowance type with unversal code for the selected allowance should be used
                // otherwise, allowance type for aoc will be used
                Boolean centralizedWithAOC= firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.name(), params.long('firmId'))?.toBoolean()
                if(centralizedWithAOC && params.long('firmId') != PCPSessionUtils.getValue("firmId")){
                    String universalCode= AllowanceType.read(params["allowanceType.id"])?.universalCode
                    if(universalCode){
                        mapParam = new GrailsParameterMap([universalCode: universalCode, 'firmId':params['firmId']], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        failMessage = 'request.not.found.for.universalCode.error.label'
                    }else{
                        failMessage = 'request.universalCode.not.defined.error.label'
                    }
                }else{
                    mapParam = new GrailsParameterMap([id: params["allowanceType.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                }
                allowanceRequest.allowanceType = allowanceTypeService?.getInstance(mapParam)
                if(allowanceRequest.allowanceType){
                    failMessage = null
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
                        allowanceRequest = allowanceRequestService.getInstanceWithRemotingValues(mapParam)
                        switch (requestCategory){
                            case EnumRequestCategory.CANCEL:
                                if(allowanceRequest.canCancelRequest){
                                    resultMap['requestType']= EnumRequestType.ALLOWANCE_CANCEL_REQUEST
                                    resultMap['formName']= 'cancelRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.cancelled.error.label'
                                }
                                break
                            case EnumRequestCategory.EDIT:
                                if(allowanceRequest.canEditRequest){
                                    resultMap['requestType']= EnumRequestType.ALLOWANCE_EDIT_REQUEST
                                    resultMap['formName']= 'editRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.editted.error.label'
                                }
                                break
                            case EnumRequestCategory.STOP:
                                if(allowanceRequest.canStopRequest){
                                    resultMap['requestType']= EnumRequestType.ALLOWANCE_STOP_REQUEST
                                    resultMap['formName']= 'stopRequestForm'
                                }else{
                                    failMessage= 'request.cant.be.stopped.error.label'
                                }
                                break
                            case EnumRequestCategory.EXTEND:
                                if(allowanceRequest.canExtendRequest){
                                    resultMap['requestType']= EnumRequestType.ALLOWANCE_CONTINUE_REQUEST
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
            resultMap[requestKey]= allowanceRequest
            return resultMap
        } else {
            String failMessage = 'allowanceRequest.employee.notFound.error.label'
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
            if(params["allowanceType.id"]){
                result['allowanceTypeId']= params["allowanceType.id"]
            }
            result['DOMAIN_COLUMNS']= 'LITE_DOMAIN_COLUMNS'
        } else {
            String failMessage = 'allowanceRequest.employee.notFound.error.label'
            result['success']= false
            result['message']= failMessage
        }
        return result
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocAllowanceListRecord record
        if(params.listEmployeeId){
            record= AocAllowanceListRecord.createCriteria().get {
                eq('allowanceListEmployee.id', params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocAllowanceListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocAllowanceListRecord> allowanceListRecordList= (List<AocAllowanceListRecord>)aocListRecordList

        ZonedDateTime now= ZonedDateTime.now()

        // TODO: changing this to sql will have better performance for large numbers

        /**
         *
         final session = sessionFactory.currentSession
         Query query = session.createSQLQuery(""" update employee_status_history eh
         set to_date_datetime = ? ,
         to_date_date_tz = ?
         where eh.to_date_datetime = '0003-03-03 03:03:03'
         and eh.employee_id = ?
         and eh.employee_status_id in
         (select es.id from employee_status es where es.employee_status_category_id!=?) """);
         //set the sql query params
         query.setParameter(0, java.util.Date?.from(employeeStatusHistory?.fromDate?.toInstant()))
         query.setParameter(1, employeeStatusHistory?.fromDate?.zone.toString())
         query.setParameter(2, employeeStatusHistory?.employee?.id)
         query.setParameter(3, employeeStatusHistory?.employeeStatus?.employeeStatusCategory?.id)
         //execute the sql query
         final queryResults = query?.executeUpdate()
         */

        allowanceListRecordList?.each { aocRecord->
            if(aocRecord.allowanceListEmployee.recordStatus== EnumListRecordStatus.NEW){
                aocRecord.allowanceListEmployee.recordStatus= aocRecord.recordStatus
                aocRecord.allowanceListEmployee.addToAllowanceListEmployeeNotes(new AllowanceListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, allowanceListEmployee: aocRecord.allowanceListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.allowanceListEmployee.save(flush:true)
            }
        }
//
//        // create notes with order number
//        Map params= [:]
//        params.recordStatusNew= EnumListRecordStatus.NEW
//        params.recordStatus= EnumListRecordStatus.APPROVED
//        params.recordIds= aocListRecordList.id
//
//        params.orderNo= orderNumber
//        params.noteDate= now
//
//        // insert notes for order number
//        StringBuilder sbInsertNotesQuery= new StringBuilder("insert into AllowanceListEmployeeNote(orderNo, noteDate, allowanceListEmployee) ")
//        sbInsertNotesQuery << "select :orderNo, :noteDate, allowanceListEmployee from AocAllowanceListRecord where id in (:recordIds) "
//        sbInsertNotesQuery << " and recordStatus=:recordStatus and allowanceListEmployee.recordStatus = :recordStatusNew"
//
//        println("insertQuery = " + sbInsertNotesQuery.toString())
//
//        int updated= AllowanceListEmployeeNote.executeUpdate(sbInsertNotesQuery.toString(), params)
//        println("$updated notes are created for approved records")
//
//        params.recordStatus= EnumListRecordStatus.REJECTED
//        updated= AllowanceListEmployeeNote.executeUpdate(sbInsertNotesQuery.toString(), params)
//        println("$updated notes are created for rejected records")
//
//        // update promotionListEmployee set approved or rejected
//        StringBuilder sbRecordUpdateQuery= new StringBuilder("update AllowanceListEmployee set recordStatus=:recordStatus ")
//        sbRecordUpdateQuery << " where id in "
//        sbRecordUpdateQuery << " (select allowanceListEmployee.id from AocAllowanceListRecord where id in (:recordIds) "
//        sbRecordUpdateQuery << " and recordStatus=:recordStatus ) and recordStatus = :recordStatusNew"
//
//        params.remove('orderNo')
//        params.remove('noteDate')
//
//        // update rejected records
//        updated= AllowanceListEmployee.executeUpdate(sbRecordUpdateQuery.toString(), params)
//        println("$updated records have been set to Rejcted")
//
////      // update approved records
//        params.recordStatus= EnumListRecordStatus.APPROVED
//        updated= AllowanceListEmployee.executeUpdate(sbRecordUpdateQuery.toString(), params)
//        println("$updated records have been set to Approved")

    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria= new DetachedCriteria(AocAllowanceListRecord).build {

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
        AocAllowanceListRecord allowanceListRecord= (AocAllowanceListRecord) listRecord
        return allowanceListRecord?.allowanceListEmployee?.allowanceRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
