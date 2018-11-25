package ps.gov.epsilon.aoc.correspondences.maritalStatus

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
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusEmployeeNote
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusListEmployee
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusListEmployeeService
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequestService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.MaritalStatusService
import ps.police.pcore.v2.entity.lookups.dtos.v1.MaritalStatusDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocMaritalStatusListRecordService implements IListRecordService {

    MaritalStatusListEmployeeService maritalStatusListEmployeeService
    MaritalStatusRequestService maritalStatusRequestService
    EmployeeService employeeService
    PersonService personService
    AocCommonService aocCommonService
    MaritalStatusService maritalStatusService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.employee.financialNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.transientData.oldMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.transientData.newMaritalStatusName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.maritalStatusDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.transientData.relatedPersonDTO.localFullName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "maritalStatusListEmployee.maritalStatusRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]


    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return maritalStatusListEmployeeService.LIST_DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocMaritalStatusRecordList) {
        if (aocMaritalStatusRecordList?.getTotalCount() > 0) {
            /**
             * to employee name from core
             */
            List<AocMaritalStatusListRecord> resultList = (List<AocMaritalStatusListRecord>) aocMaritalStatusRecordList?.resultList
            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(PCPUtils.union(resultList?.maritalStatusListEmployee?.maritalStatusRequest?.employee?.personId, resultList?.maritalStatusListEmployee?.maritalStatusRequest?.relatedPersonId))

            SearchBean searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: PCPUtils.union(resultList?.maritalStatusListEmployee?.maritalStatusRequest?.oldMaritalStatusId, resultList?.maritalStatusListEmployee?.maritalStatusRequest?.newMaritalStatusId)))
            List<MaritalStatusDTO> maritalStatusDTOList = maritalStatusService?.searchMaritalStatus(searchBean)?.resultList

            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocMaritalStatusListRecord aocPromotionListRecord ->

                MaritalStatusRequest maritalStatusRequest = aocPromotionListRecord.maritalStatusListEmployee.maritalStatusRequest?.refresh()
                maritalStatusRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == maritalStatusRequest?.employee?.personId
                })

                maritalStatusRequest?.transientData?.relatedPersonDTO = personDTOList.find {
                    it?.id == maritalStatusRequest?.relatedPersonId
                }

                maritalStatusRequest?.transientData?.oldMaritalStatusName = maritalStatusDTOList?.find {
                    it.id == maritalStatusRequest?.oldMaritalStatusId
                }?.descriptionInfo?.localName

                maritalStatusRequest?.transientData?.newMaritalStatusName = maritalStatusDTOList?.find {
                    it.id == maritalStatusRequest?.newMaritalStatusId
                }?.operationLocalName

                maritalStatusRequest.employee.transientData.put("personDTO", personDTOList?.find {
                    it?.id == maritalStatusRequest?.employee?.personId
                })

                log.info("getting person name from core: " + maritalStatusRequest?.employee)
            }
        }
        if (aocMaritalStatusRecordList instanceof PagedResultList) {
            PagedList<AocMaritalStatusListRecord> pagedList = new PagedList<AocMaritalStatusListRecord>()
            pagedList.totalCount = aocMaritalStatusRecordList.totalCount
            pagedList.resultList = aocMaritalStatusRecordList.resultList
            return pagedList
        } else {
            return aocMaritalStatusRecordList
        }
    }

    /**
     * search for maritalStatusListEmployee records that already exist but not added to AOC Correspondence list
     * @param params
     * @return paged list
     */
    @Override
    PagedList searchNotIncludedRecords(GrailsParameterMap params) {
        Long aocCorrespondenceListId = params.long('aocCorrespondenceList.id')
        AocCorrespondenceList correspondenceList = AocCorrespondenceList.read(aocCorrespondenceListId)
        AocCorrespondenceList rootCorrespondenceList = correspondenceList?.parentCorrespondenceList
        Long firmId = params.long('firm.id') ?: correspondenceList.hrFirmId
        CorrespondenceList hrCorrespondenceList = rootCorrespondenceList ? rootCorrespondenceList.getHrCorrespondenceList(firmId) : correspondenceList?.getHrCorrespondenceList(firmId)

        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Map queryParams = [:]

        StringBuilder queryString = new StringBuilder()
        queryString << "from ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusListEmployee hrle "
        queryString << " where hrle.maritalStatusRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is maritalStatus, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.maritalStatusListEmployee.id from AocMaritalStatusListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.maritalStatusList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.maritalStatusListEmployee.id from AocMaritalStatusListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :maritalStatusListId )"

        queryParams['maritalStatusListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id

        String countquery = "select count(id) " + queryString.toString()
        def hrRecordsCount = MaritalStatusListEmployee.executeQuery(countquery, queryParams)[0]

        List<MaritalStatusListEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = MaritalStatusListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList = searchPersonData(hrRecords?.maritalStatusRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { MaritalStatusListEmployee maritalStatusListEmployee ->
                maritalStatusListEmployee?.maritalStatusRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == maritalStatusListEmployee?.maritalStatusRequest?.employee?.personId
                })
            }
        } else {
            hrRecords = []
        }

        PagedList<MaritalStatusListEmployee> pagedList = new PagedList<MaritalStatusListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount = hrRecordsCount

        return pagedList
    }

    /**
     * search person transient data remotely
     * @param personIds
     * @return
     */
    private List<PersonDTO> searchPersonData(List<Long> personIds) {
        SearchBean searchBean
        List<PersonDTO> personDTOList = null

        if (!personIds?.isEmpty()) {
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: personIds))
            personDTOList = personService?.searchPerson(searchBean)?.resultList
        }
        return personDTOList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return maritalStatusListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return maritalStatusListEmployeeService.getInstance(params)
    }

    @Override
    AocMaritalStatusListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocMaritalStatusListRecord aocMaritalStatusListRecord = (AocMaritalStatusListRecord) aocListRecord

        /**
         * add maritalStatus request to maritalStatus list employee
         */
        if (params.listEmployeeId) {
            aocMaritalStatusListRecord.maritalStatusListEmployee = MaritalStatusListEmployee.read(params.listEmployeeId)
            if (!aocMaritalStatusListRecord.maritalStatusListEmployee) {
                throw new Exception("maritalStatusListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            aocMaritalStatusListRecord.maritalStatusListEmployee = new MaritalStatusListEmployee()
            aocMaritalStatusListRecord.maritalStatusListEmployee.recordStatus = EnumListRecordStatus.NEW
            aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusList = hrList

            EnumRequestType requestType = params.requestType ? EnumRequestType.valueOf(params.requestType) : null

            // save maritalStatus request
            if (!requestType || requestType.requestCategory == EnumRequestCategory.ORIGINAL) {
                aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusRequest = maritalStatusRequestService.save(params)
            } else {
                aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusRequest = maritalStatusRequestService.saveOperation(params)
            }

            aocMaritalStatusListRecord?.maritalStatusListEmployee?.firm = aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusRequest?.firm
            aocMaritalStatusListRecord?.maritalStatusListEmployee?.effectiveDate = aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusRequest?.maritalStatusDate

            if (aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusRequest.hasErrors()) {
                throw new ValidationException("Failed to save marital status request", aocMaritalStatusListRecord.maritalStatusListEmployee.maritalStatusRequest.errors)
            }
        }
        return aocMaritalStatusListRecord
    }


    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"] && params["requestCategory"]) {
            GrailsParameterMap mapParam

            MaritalStatusRequest maritalStatusRequest

            EnumRequestCategory requestCategory = EnumRequestCategory.valueOf(params["requestCategory"])
            String failMessage, requestKey = 'maritalStatusRequest'
            Map resultMap = [:]

            if (requestCategory == EnumRequestCategory.ORIGINAL) {
                maritalStatusRequest = maritalStatusRequestService.getPreCreateInstance(params)

            } else {
                if (!params.checked_requestIdsList) {
                    failMessage = 'request.notChecked.error.label'
                } else {
                    String checkedRequestId = params["checked_requestIdsList"]
                    if (!checkedRequestId || checkedRequestId?.isEmpty()) {
                        failMessage = 'request.notChecked.error.label'
                    } else {
                        requestKey = 'request'
                        mapParam = new GrailsParameterMap([id: checkedRequestId, 'firm.id': params['firmId']],
                                WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
                        maritalStatusRequest = maritalStatusRequestService.getInstanceWithRemotingValues(mapParam)
                        switch (requestCategory) {
                            case EnumRequestCategory.CANCEL:
                                if (maritalStatusRequest.canCancelRequest) {
                                    resultMap['requestType'] = EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST
                                    resultMap['formName'] = 'cancelRequestForm'
                                } else {
                                    failMessage = 'request.cant.be.cancelled.error.label'
                                }
                                break
                            case EnumRequestCategory.EDIT:
                                if (maritalStatusRequest.canEditRequest) {
                                    resultMap['requestType'] = EnumRequestType.MARITAL_STATUS_EDIT_REQUEST
                                    resultMap['formName'] = 'editRequestForm'
                                } else {
                                    failMessage = 'request.cant.be.editted.error.label'
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
            resultMap[requestKey] = maritalStatusRequest
            return resultMap
        } else {
            String failMessage = 'maritalStatusRequest.employee.notFound.error.label'
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
            result['DOMAIN_COLUMNS'] = 'LITE_DOMAIN_COLUMNS'
        } else {
            String failMessage = 'maritalStatusRequest.employee.notFound.error.label'
            result['success'] = false
            result['message'] = failMessage
        }
        return result
    }


    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocMaritalStatusListRecord record
        if (params.listEmployeeId) {
            record = AocMaritalStatusListRecord.createCriteria().get {
                eq('maritalStatusListEmployee.id', params.listEmployeeId)
            }
        }
        if (!record) {
            record = new AocMaritalStatusListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocMaritalStatusListRecord> aocMaritalStatusListRecord = (List<AocMaritalStatusListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()

        aocMaritalStatusListRecord?.each { aocRecord ->
            if (aocRecord.maritalStatusListEmployee.recordStatus == EnumListRecordStatus.NEW) {
                aocRecord.maritalStatusListEmployee.recordStatus = aocRecord.recordStatus
                aocRecord.maritalStatusListEmployee.addToMaritalStatusEmployeeNotes(new MaritalStatusEmployeeNote(orderNo: orderNumber,
                        noteDate: now, maritalStatusListEmployee: aocRecord.maritalStatusListEmployee))
                // flush is true to make changes visible to next phases through transaction
                aocRecord.maritalStatusListEmployee.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria = new DetachedCriteria(AocMaritalStatusListRecord).build {

        }
        return criteria
    }

    /***
     * Checks if employee profile is locked
     * @param listRecord
     * @return
     */
    @Override
    Boolean isEmployeeProfileLocked(AocListRecord listRecord) {
        AocMaritalStatusListRecord record = (AocMaritalStatusListRecord) listRecord
        return record?.maritalStatusListEmployee?.maritalStatusRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
