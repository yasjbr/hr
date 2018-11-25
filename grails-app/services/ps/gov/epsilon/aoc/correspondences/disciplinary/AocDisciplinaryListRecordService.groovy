package ps.gov.epsilon.aoc.correspondences.disciplinary

import grails.gorm.DetachedCriteria
import grails.gorm.PagedResultList
import grails.transaction.Transactional
import grails.validation.ValidationException
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocJoinedCorrespondenceListRecord
import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.aoc.correspondences.common.AocCommonService
import ps.gov.epsilon.aoc.interfaces.correspondenceList.v1.IListRecordService
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRecordJudgment
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRecordJudgmentService
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequest
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequestService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.PagedList
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.pcore.v2.entity.lookups.CurrencyService
import ps.police.pcore.v2.entity.lookups.UnitOfMeasurementService
import ps.police.pcore.v2.entity.lookups.dtos.v1.CurrencyDTO
import ps.police.pcore.v2.entity.lookups.dtos.v1.UnitOfMeasurementDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import java.time.ZonedDateTime

@Transactional
class AocDisciplinaryListRecordService implements IListRecordService {

    DisciplinaryRecordJudgmentService disciplinaryRecordJudgmentService
    DisciplinaryRequestService disciplinaryRequestService
    EmployeeService employeeService
    PersonService personService
    UnitOfMeasurementService unitOfMeasurementService
    CurrencyService currencyService
    AocCommonService aocCommonService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryRecordJudgment.disciplinaryRequest.id", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryRecordJudgment.disciplinaryRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "disciplinaryRecordJudgment.disciplinaryRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRecordJudgment.disciplinaryRequest.disciplinaryCategory", type: "DisciplinaryCategory", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "disciplinaryRecordJudgment.disciplinaryJudgment", type: "DisciplinaryJudgment", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return disciplinaryRecordJudgmentService.DOMAIN_LIST_FOR_ADD_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocDisciplinaryRecordList) {
        if (aocDisciplinaryRecordList?.getTotalCount() > 0) {
            /**
             * to employee name from core
             */
            List<AocDisciplinaryListRecord> resultList = (List<AocDisciplinaryListRecord>) aocDisciplinaryRecordList?.resultList
            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(resultList?.disciplinaryRecordJudgment?.disciplinaryRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            resultList?.each { AocDisciplinaryListRecord aocPromotionListRecord ->

                DisciplinaryRequest disciplinaryRequest = aocPromotionListRecord.disciplinaryRecordJudgment.disciplinaryRequest?.refresh()

                aocPromotionListRecord?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == disciplinaryRequest?.employee?.personId
                })

                disciplinaryRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == disciplinaryRequest?.employee?.personId
                })
                log.info("getting person name from core: " + disciplinaryRequest?.employee)
            }
        }
        if (aocDisciplinaryRecordList instanceof PagedResultList) {
            PagedList<AocDisciplinaryListRecord> pagedList = new PagedList<AocDisciplinaryListRecord>()
            pagedList.totalCount = aocDisciplinaryRecordList.totalCount
            pagedList.resultList = aocDisciplinaryRecordList.resultList
            return pagedList
        } else {
            return aocDisciplinaryRecordList
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
        AocCorrespondenceList correspondenceList = AocCorrespondenceList.read(aocCorrespondenceListId)
        AocCorrespondenceList rootCorrespondenceList = correspondenceList?.parentCorrespondenceList
        Long firmId = params.long('firm.id') ?: correspondenceList.hrFirmId
        CorrespondenceList hrCorrespondenceList = rootCorrespondenceList ? rootCorrespondenceList.getHrCorrespondenceList(firmId) : correspondenceList?.getHrCorrespondenceList(firmId)

        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Map queryParams = [:]

        StringBuilder queryString = new StringBuilder()
        queryString << "from ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRecordJudgment hrle "
        queryString << " where hrle.disciplinaryRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.disciplinaryRecordJudgment.id from AocDisciplinaryListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.disciplinaryRecordsList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.disciplinaryRecordJudgment.id from AocDisciplinaryListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id

        String countquery = "select count(id) " + queryString.toString()
        def hrRecordsCount = DisciplinaryRecordJudgment.executeQuery(countquery, queryParams)[0]

        List<DisciplinaryRecordJudgment> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = DisciplinaryRecordJudgment.executeQuery(queryString.toString(), queryParams)

            List<Long> unitIds = hrRecords?.unitId?.findAll { it != null }?.unique()
            List<Long> currencyIds = hrRecords?.currencyId?.findAll { it != null }?.unique()

            List<CurrencyDTO> currencyDTOList = []
            List<UnitOfMeasurementDTO> unitOfMeasurementDTOList = []

            if (currencyIds) {
                SearchBean currencySearchBean = new SearchBean()
                currencySearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: currencyIds))
                currencyDTOList = currencyService.searchCurrency(currencySearchBean)?.resultList
            }

            if (unitIds) {
                SearchBean unitSearchBean = new SearchBean()
                unitSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: unitIds))
                unitOfMeasurementDTOList = unitOfMeasurementService.searchUnitOfMeasurement(unitSearchBean)?.resultList
            }

            List<PersonDTO> personDTOList = searchPersonData(hrRecords?.disciplinaryRequest?.employee?.personId)
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { DisciplinaryRecordJudgment disciplinaryRecordJudgment ->
                disciplinaryRecordJudgment?.disciplinaryRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == disciplinaryRecordJudgment?.disciplinaryRequest?.employee?.personId
                })
                if (disciplinaryRecordJudgment?.currencyId) {
                    disciplinaryRecordJudgment.transientData.currencyDTO = currencyDTOList?.find {
                        it.id == disciplinaryRecordJudgment?.currencyId
                    }
                }
                if (disciplinaryRecordJudgment?.unitId) {
                    disciplinaryRecordJudgment.transientData.unitDTO = unitOfMeasurementDTOList?.find {
                        it.id == disciplinaryRecordJudgment?.unitId
                    }
                }
            }
        } else {
            hrRecords = []
        }

        PagedList<DisciplinaryRecordJudgment> pagedList = new PagedList<DisciplinaryRecordJudgment>()
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
        return disciplinaryRecordJudgmentService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return disciplinaryRecordJudgmentService.getInstance(params)
    }

    @Override
    AocDisciplinaryListRecord save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocDisciplinaryListRecord aocDisciplinaryListRecord = (AocDisciplinaryListRecord) aocListRecord
        DisciplinaryRequest disciplinaryRequest
        /**
         * add allowance request to allowance list employee
         */
        if (params.listEmployeeId) {
            aocDisciplinaryListRecord.disciplinaryRecordJudgment = DisciplinaryRecordJudgment.read(params.listEmployeeId)
            if (!aocDisciplinaryListRecord.disciplinaryRecordJudgment) {
                throw new Exception("disciplinaryListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            params["requestStatus"] = EnumRequestStatus.APPROVED
            disciplinaryRequest = disciplinaryRequestService.save(params)
            if (disciplinaryRequest?.hasErrors()) {
                throw new ValidationException("Failed to save disciplinary request", disciplinaryRequest?.errors)
            }

            AocDisciplinaryListRecord aocDisciplinaryListRecord2
            // to add all disciplinaryRecordJudgments ,that belongs to request, to list
            for (int i = 1; i < disciplinaryRequest?.disciplinaryJudgments.size(); i++) {
                aocDisciplinaryListRecord2 = new AocDisciplinaryListRecord()
                aocDisciplinaryListRecord2.recordStatus = EnumListRecordStatus.APPROVED
                aocDisciplinaryListRecord2.addToJoinedCorrespondenceListRecords(new AocJoinedCorrespondenceListRecord(correspondenceList: aocDisciplinaryListRecord?.joinedCorrespondenceListRecords?.correspondenceList[0], listRecord: aocDisciplinaryListRecord2))
                aocDisciplinaryListRecord2.disciplinaryRecordJudgment = disciplinaryRequest.disciplinaryJudgments[i]
                aocDisciplinaryListRecord2.disciplinaryRecordJudgment.judgmentStatus = EnumJudgmentStatus.valueOf(EnumListRecordStatus.NEW.toString())
                aocDisciplinaryListRecord2.disciplinaryRecordJudgment.disciplinaryRecordsList = hrList
                aocDisciplinaryListRecord2.save(flush: true, failOnError: true)
            }

            /* if(aocDisciplinaryListRecord?.disciplinaryRecordJudgment?.disciplinaryRequest?.hasErrors()){
                 throw new ValidationException("Failed to save allowance request", aocDisciplinaryListRecord.disciplinaryRecordJudgment.disciplinaryRequest.errors)
             }*/
            aocDisciplinaryListRecord.disciplinaryRecordJudgment = disciplinaryRequest.disciplinaryJudgments[0]
            aocDisciplinaryListRecord.disciplinaryRecordJudgment.judgmentStatus = EnumJudgmentStatus.valueOf(EnumListRecordStatus.NEW.toString())
            aocDisciplinaryListRecord.disciplinaryRecordJudgment.disciplinaryRecordsList = hrList
        }
        return aocDisciplinaryListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {

        if (params["employeeId"]) {
            GrailsParameterMap mapParam

            DisciplinaryRequest disciplinaryRequest = new DisciplinaryRequest()
            /**
             * get selected employee
             */
            mapParam = new GrailsParameterMap([id: params["employeeId"], 'firm.id': params['firmId']], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
            disciplinaryRequest.employee = employeeService?.getInstanceWithRemotingValues(mapParam)

            return [success: true, disciplinaryRequest: disciplinaryRequest]
        } else {
            String failMessage = 'disciplinaryRequest.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocDisciplinaryListRecord record
        if (params.listEmployeeId) {
            record = AocDisciplinaryListRecord.createCriteria().get {
                eq('disciplinaryRecordJudgment.id', params.listEmployeeId)
            }
        }
        if (!record) {
            record = new AocDisciplinaryListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {

        List<AocDisciplinaryListRecord> aocDisciplinaryListRecord = (List<AocDisciplinaryListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()

        aocDisciplinaryListRecord?.each { aocRecord ->
            if (aocRecord.disciplinaryRecordJudgment.judgmentStatus == EnumJudgmentStatus.NEW) {
                aocRecord.disciplinaryRecordJudgment.judgmentStatus = aocRecord.recordStatus == EnumListRecordStatus.REJECTED ? EnumJudgmentStatus.CANCELED : EnumJudgmentStatus.valueOf(aocRecord.recordStatus.toString())
                /*aocRecord.disciplinaryRecordJudgment.addToAllowanceListEmployeeNotes(new AllowanceListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, disciplinaryRecordJudgment: aocRecord.disciplinaryRecordJudgment))*/
                // flush is true to make changes visible to next phases through transaction
                aocRecord.disciplinaryRecordJudgment.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for return from child
         */
        DetachedCriteria criteria = new DetachedCriteria(AocDisciplinaryListRecord).build {

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
        AocDisciplinaryListRecord disciplinaryListRecord = (AocDisciplinaryListRecord) listRecord
        return disciplinaryListRecord?.disciplinaryRecordJudgment?.disciplinaryRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
