package ps.gov.epsilon.aoc.correspondences.vacation

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.allowance.AocAllowanceListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.gov.epsilon.hr.firm.vacation.VacationListEmployee
import ps.gov.epsilon.hr.firm.vacation.VacationListEmployeeNote
import ps.gov.epsilon.hr.firm.vacation.VacationListEmployeeService
import ps.gov.epsilon.hr.firm.vacation.VacationRequest
import ps.gov.epsilon.hr.firm.vacation.VacationRequestService
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationTypeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocVacationListRecordService implements IListRecordService {

    VacationListEmployeeService vacationListEmployeeService
    AocCommonService aocCommonService
    VacationRequestService vacationRequestService
    EmployeeService employeeService
    VacationTypeService vacationTypeService
    FirmSettingService firmSettingService
    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationListEmployee.vacationRequest.id", type: "long", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationListEmployee.vacationRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "vacationListEmployee.vacationRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationListEmployee.vacationRequest.vacationType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationListEmployee.vacationRequest.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationListEmployee.vacationRequest.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationListEmployee.vacationRequest.numOfDays", type: "integer", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacationListEmployee.vacationRequest.external", type: "boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
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
        return vacationListEmployeeService.DOMAIN_COLUMNS
    }

    /**
     * search and include values from core
     * @param pagedResultList
     * @return: List of data meets search criteria
     */
    @Override
    PagedList searchWithRemotingValues(Object resultList) {
        List<PersonDTO> personDTOList
        List<Long> personIdList = resultList?.vacationListEmployee?.vacationRequest?.employee?.personId
        if (!personIdList?.isEmpty()) {

            //get remoting values from CORE
            personDTOList = aocCommonService.searchPersonData(personIdList)

            //assign remoting values for each list
            resultList?.vacationListEmployee?.each { VacationListEmployee vacationListEmployee ->
                vacationListEmployee?.vacationRequest?.employee?.transientData?.put("personDTO",
                        personDTOList?.find { it.id == vacationListEmployee?.vacationRequest?.employee?.personId })
            }
            PagedList<AocVacationListRecord> pagedList = new PagedList<AocVacationListRecord>()
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
        queryString << VacationListEmployee.getName()
        queryString << " hrle where hrle.vacationRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.vacationListEmployee.id from AocVacationListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.vacationList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.vacationListEmployee.id from AocVacationListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id


        String countQuery = "select count(id) " + queryString.toString()
        def hrRecordsCount = VacationListEmployee.executeQuery(countQuery, queryParams)[0]

        List<VacationListEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = VacationListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(hrRecords?.vacationRequest?.employee?.personId?.unique())
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { VacationListEmployee vacationListEmployee ->
                vacationListEmployee?.vacationRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == vacationListEmployee?.vacationRequest?.employee?.personId
                })
            }
        } else {
            hrRecords = []
        }

        PagedList<VacationListEmployee> pagedList = new PagedList<VacationListEmployee>()
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
        return vacationListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    /**
     * returns instance related to encoded id
     * @param params
     * @return VacationListEmployee
     */
    @Override
    Object getInstance(GrailsParameterMap params) {
        return vacationListEmployeeService.getInstance(params)
    }

    /**
     * Saves a listEmployee from params
     * @param params
     * @return a subclass
     */
    @Override
    Object save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocVacationListRecord aocVacationListRecord = (AocVacationListRecord) aocListRecord
        /**
         * add vacation request to vacation list employee
         */
        if (params.listEmployeeId) {
            aocVacationListRecord.vacationListEmployee = VacationListEmployee.read(params.listEmployeeId)
            if (!aocVacationListRecord.vacationListEmployee) {
                throw new Exception("vacationListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            aocVacationListRecord.vacationListEmployee = new VacationListEmployee()
            aocVacationListRecord.vacationListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocVacationListRecord.vacationListEmployee.vacationList = hrList

            // save vacation request
            EnumRequestType requestType = params.requestType ? EnumRequestType.valueOf(params.requestType) : null
            params["checkBalance"] = false
            if (!requestType || requestType.requestCategory == EnumRequestCategory.ORIGINAL) {
                aocVacationListRecord.vacationListEmployee.vacationRequest = vacationRequestService.save(params)
            } else {
                aocVacationListRecord.vacationListEmployee.vacationRequest = vacationRequestService.saveOperation(params)
            }


            if (aocVacationListRecord?.vacationListEmployee?.vacationRequest?.hasErrors()) {
                throw new ValidationException("Failed to save vacation request",
                        aocVacationListRecord.vacationListEmployee.vacationRequest.errors)
            }
            aocVacationListRecord.vacationListEmployee.currentEmploymentRecord = aocVacationListRecord.vacationListEmployee?.vacationRequest?.employee?.currentEmploymentRecord
            aocVacationListRecord.vacationListEmployee.currentEmployeeMilitaryRank = aocVacationListRecord.vacationListEmployee?.vacationRequest?.employee?.currentEmployeeMilitaryRank
        }
        return aocVacationListRecord
    }

    /**
     * used to get employee and any other related info necessary for creating request
     * @param params
     * @return VacationRequest
     */
    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"] && params["vacationType.id"] && params["requestCategory"]) {
            GrailsParameterMap mapParam
            VacationRequest vacationRequest
            EnumRequestCategory requestCategory = EnumRequestCategory.valueOf(params["requestCategory"])
            String failMessage, requestKey= 'vacationRequest'
            Map resultMap = [:]

            if (requestCategory == EnumRequestCategory.ORIGINAL) {
                vacationRequest = new VacationRequest()
                /**
                 * get selected employee
                 */
                mapParam = new GrailsParameterMap([id: params["employeeId"], 'firm.id': params['firmId']],
                        WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                vacationRequest.employee = employeeService?.getInstanceWithRemotingValues(mapParam)

                /**
                 * get selected vacation type
                 */
                // if firm is centralized with aoc, then allowance type with unversal code for the selected allowance should be used
                // otherwise, allowance type for aoc will be used
                Boolean centralizedWithAOC= firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.name(), params.long('firmId'))?.toBoolean()
                if(centralizedWithAOC && params.long('firmId') != PCPSessionUtils.getValue("firmId")){
                    String universalCode= VacationType.read(params["vacationType.id"])?.universalCode
                    if(universalCode){
                        mapParam = new GrailsParameterMap([universalCode: universalCode, 'firmId':params['firmId']], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        failMessage = 'request.not.found.for.universalCode.error.label'
                    }else{
                        failMessage = 'request.universalCode.not.defined.error.label'
                    }
                }else{
                    mapParam = new GrailsParameterMap([id: params["vacationType.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                }
                vacationRequest.vacationType = vacationTypeService?.getInstance(mapParam)
                if(vacationRequest.vacationType){
                    failMessage = null
                }
            } else {
                if (!params.checked_requestIdsList) {
                    failMessage = 'request.notChecked.error.label'
                } else {
                    String checkedRequestId = params["checked_requestIdsList"]
                    if (!checkedRequestId || checkedRequestId?.isEmpty()) {
                        failMessage = 'request.notChecked.error.label'
                    } else {
                        requestKey= 'request'
                        mapParam = new GrailsParameterMap([id: checkedRequestId, 'firm.id': params['firmId']],
                                WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        println("mapParam = " + mapParam)
                        vacationRequest = vacationRequestService.getInstanceWithRemotingValues(mapParam)
                        switch (requestCategory) {
                            case EnumRequestCategory.CANCEL:
                                if (vacationRequest.canCancelRequest) {
                                    resultMap['requestType'] = EnumRequestType.REQUEST_FOR_VACATION_CANCEL
                                    resultMap['formName'] = 'cancelRequestForm'
                                } else {
                                    failMessage = 'request.cant.be.cancelled.error.label'
                                }
                                break
                            case EnumRequestCategory.EDIT:
                                if (vacationRequest.canEditRequest) {
                                    resultMap['requestType'] = EnumRequestType.REQUEST_FOR_EDIT_VACATION
                                    resultMap['formName'] = 'editRequestForm'
                                } else {
                                    failMessage = 'request.cant.be.editted.error.label'
                                }
                                break
                            case EnumRequestCategory.STOP:
                                if (vacationRequest.canStopRequest) {
                                    resultMap['requestType'] = EnumRequestType.REQUEST_FOR_VACATION_STOP
                                    resultMap['formName'] = 'stopRequestForm'
                                } else {
                                    failMessage = 'request.cant.be.stopped.error.label'
                                }
                                break
                            case EnumRequestCategory.EXTEND:
                                if (vacationRequest.canExtendRequest) {
                                    resultMap['requestType'] = EnumRequestType.REQUEST_FOR_VACATION_EXTENSION
                                    resultMap['formName'] = 'extendRequestForm'
                                } else {
                                    failMessage = 'request.cant.be.extended.error.label'
                                }
                                break
                        }
                    }

                }
            }
            if (failMessage) {
                return [success: false, message: failMessage]
            }
            resultMap['success'] = true
            resultMap[requestKey] = vacationRequest
            return resultMap
        } else {
            String failMessage = 'vacationRequest.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        Map result = [:]
        // if request category is original, return selectEmployeeForm
        // else return select requestForm
        if (params["employeeId"] && params["requestCategory"]) {
            EnumRequestCategory requestCategory = EnumRequestCategory.valueOf(params["requestCategory"])
            String employeeId = params.employeeId
            result['success'] = true
            result['requestCategory'] = requestCategory
            result['employeeId'] = employeeId
            result['firmId'] = params.firmId
            if (params["vacationType.id"]) {
                result['vacationTypeId'] = params["vacationType.id"]
            }
            result['DOMAIN_COLUMNS'] = 'DOMAIN_COLUMNS'
        } else {
            String failMessage = 'vacationRequest.employee.notFound.error.label'
            result['success'] = false
            result['message'] = failMessage
        }
        return result
    }
    /**
     * returns a new empty instance
     * @param params
     * @return AocVacationListRecord
     */
    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocVacationListRecord record
        if(params.listEmployeeId){
            record= AocVacationListRecord.createCriteria().get {
                eq('vacationListEmployee.id', params.listEmployeeId)
            }
        }
        if(!record){
            record= new AocVacationListRecord()
        }
        return record
    }

    /**
     * updates hr record status and hr request status to rejected or approved after completing workflow
     * @param aocListRecordList
     */
    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {
        List<AocVacationListRecord> aocVacationListRecordList = (List<AocVacationListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()
        aocVacationListRecordList?.each { aocRecord ->
            if (aocRecord.vacationListEmployee.recordStatus == EnumListRecordStatus.NEW) {
                aocRecord.vacationListEmployee.recordStatus = aocRecord.recordStatus
                aocRecord.vacationListEmployee.addToVacationListEmployeeNotes(new VacationListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, vacationListEmployee: aocRecord.vacationListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.vacationListEmployee.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for vacation
         */
        DetachedCriteria criteria = new DetachedCriteria(AocVacationListRecord).build {

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
        AocVacationListRecord record= (AocVacationListRecord) listRecord
        return record?.vacationListEmployee?.vacationRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
