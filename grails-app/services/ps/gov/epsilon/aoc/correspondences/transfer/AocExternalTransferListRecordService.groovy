package ps.gov.epsilon.aoc.correspondences.transfer

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
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployee
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployeeNote
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployeeService
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequest
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequestService
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

@Transactional
class AocExternalTransferListRecordService implements IListRecordService {

    ExternalTransferListEmployeeService externalTransferListEmployeeService
    ExternalTransferRequestService externalTransferRequestService
    AocCommonService aocCommonService

    private static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "externalTransferListEmployee.externalTransferRequest.id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "externalTransferListEmployee.externalTransferRequest.firm.name", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "externalTransferListEmployee.employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "externalTransferListEmployee.externalTransferRequest.firm.name", type: 'string', source: 'domain'],
            [sort: false, search: false, hidden: false, name: "externalTransferListEmployee.transientData.organizationDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "externalTransferListEmployee.effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain']
    ]

    @Override
    List<String> getDomainColumns() {
        return DOMAIN_COLUMNS
    }

    @Override
    List<String> getHrDomainColumns() {
        return externalTransferListEmployeeService.DOMAIN_COLUMNS
    }

    @Override
    PagedList searchWithRemotingValues(def aocExternalTransferRecordList) {
        List<PersonDTO> personDTOList
        List<OrganizationDTO> organizationDTOList
        List<GovernorateDTO> governorateDTOList

        if (!aocExternalTransferRecordList?.resultList?.isEmpty()) {
            /**
             * to employee name from core
             */
            personDTOList = aocCommonService.searchPersonData(aocExternalTransferRecordList?.resultList?.externalTransferListEmployee?.employee?.personId)
            organizationDTOList = aocCommonService.searchOrganizationData(aocExternalTransferRecordList?.resultList?.externalTransferListEmployee?.toOrganizationId?.unique())
            governorateDTOList = aocCommonService.searchGovernoratesData(aocExternalTransferRecordList?.resultList?.externalTransferListEmployee?.currentEmploymentRecord?.department?.governorateId?.toList()?.unique())

            /**
             * assign employeeName for each employee in list
             */
            aocExternalTransferRecordList?.resultList?.each { AocExternalTransferListRecord aocExternalTransferListRecord ->
                ExternalTransferListEmployee externalTransferListEmployee = aocExternalTransferListRecord.externalTransferListEmployee
                externalTransferListEmployee?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == externalTransferListEmployee?.employee?.personId
                })
                externalTransferListEmployee?.transientData?.put("organizationDTO", organizationDTOList?.find {
                    it?.id == externalTransferListEmployee?.toOrganizationId
                })
                //set governorate info
                externalTransferListEmployee.currentEmploymentRecord.department.transientData.governorateDTO = governorateDTOList.find {
                    it.id == externalTransferListEmployee?.currentEmploymentRecord?.department?.governorateId
                }
            }
        }

        if (aocExternalTransferRecordList instanceof PagedResultList) {
            PagedList<AocExternalTransferListRecord> pagedList = new PagedList<AocExternalTransferListRecord>()
            pagedList.totalCount = aocExternalTransferRecordList.totalCount
            pagedList.resultList = aocExternalTransferRecordList.resultList
            return pagedList
        } else {
            return aocExternalTransferRecordList
        }
    }

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
        queryString << ExternalTransferListEmployee.getName()
        queryString << " hrle where hrle.externalTransferRequest.firm.id =:firmId "
        if (rootCorrespondenceList) {
            // this aoc is child, so search for hr records in parent aoc list
            queryString << " and hrle.id in ( select cr.externalTransferListEmployee.id from AocExternalTransferListRecord cr "
            queryString << " inner join cr.joinedCorrespondenceListRecords cjcl "
            queryString << " where cjcl.correspondenceList.id = :rootListId )"
            queryParams['rootListId'] = rootCorrespondenceList.id
        } else {
            // this aoc is root, then search for hr records in hr list
            queryString << " and hrle.externalTransferList.id = :hrCorrespondenceListId  "
            queryParams['hrCorrespondenceListId'] = hrCorrespondenceList?.id
        }
        queryString << " and hrle.id not in ( select r.externalTransferListEmployee.id from AocExternalTransferListRecord r "
        queryString << " inner join r.joinedCorrespondenceListRecords jcl "
        queryString << " where jcl.correspondenceList.id = :childListId )"

        queryParams['childListId'] = correspondenceList.id
        queryParams['firmId'] = hrCorrespondenceList?.firm?.id


        String countquery = "select count(id) " + queryString.toString()
        def hrRecordsCount = ExternalTransferListEmployee.executeQuery(countquery, queryParams)[0]

        List<ExternalTransferListEmployee> hrRecords
        if (hrRecordsCount > 0) {
            queryParams['max'] = max
            queryParams['offset'] = offset
            hrRecords = ExternalTransferListEmployee.executeQuery(queryString.toString(), queryParams)

            List<PersonDTO> personDTOList = aocCommonService.searchPersonData(hrRecords?.employee?.personId?.unique())
            List<OrganizationDTO> organizationDTOList = aocCommonService.searchOrganizationData(hrRecords?.toOrganizationId?.unique())
            List<GovernorateDTO> governorateDTOList = aocCommonService.searchGovernoratesData(hrRecords?.currentEmploymentRecord?.department?.governorateId?.toList()?.unique())
            /**
             * assign employeeName for each employee in list
             */
            hrRecords?.each { ExternalTransferListEmployee externalTransferListEmployee ->
                externalTransferListEmployee?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == externalTransferListEmployee?.employee?.personId
                })
                externalTransferListEmployee?.transientData?.put("organizationDTO", organizationDTOList?.find {
                    it?.id == externalTransferListEmployee?.toOrganizationId
                })
                //set governorate info
                externalTransferListEmployee.currentEmploymentRecord.department.transientData.governorateDTO = governorateDTOList.find {
                    it.id == externalTransferListEmployee?.currentEmploymentRecord?.department?.governorateId
                }
            }
        } else {
            hrRecords = []
        }

        PagedList<ExternalTransferListEmployee> pagedList = new PagedList<ExternalTransferListEmployee>()
        pagedList.setResultList(hrRecords)
        pagedList.totalCount = hrRecordsCount

        return pagedList
    }

    @Override
    Map resultListToMap(Object resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS) {
        return externalTransferListEmployeeService.resultListToMap(resultList, params, DOMAIN_COLUMNS)
    }

    @Override
    Object getInstance(GrailsParameterMap params) {
        return externalTransferListEmployeeService.getInstance(params)
    }

    @Override
    Object save(AocListRecord aocListRecord, CorrespondenceList hrList, GrailsParameterMap params) {
        AocExternalTransferListRecord externalTransferListRecord = (AocExternalTransferListRecord) aocListRecord

        /**
         * add allowance request to allowance list employee
         */
        if (params.listEmployeeId) {
            externalTransferListRecord.externalTransferListEmployee = ExternalTransferListEmployee.read(params.listEmployeeId)
            if (!externalTransferListRecord.externalTransferListEmployee) {
                throw new Exception("externalTransferListEmployee not found for id $params.listEmployeeId")
            }
        } else {
            externalTransferListRecord.externalTransferListEmployee = new ExternalTransferListEmployee()
            externalTransferListRecord.externalTransferListEmployee.recordStatus = EnumListRecordStatus.NEW
            externalTransferListRecord.externalTransferListEmployee.externalTransferList = hrList
            externalTransferListRecord.externalTransferListEmployee.effectiveDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()

            // save externalTransfer request
            params.effectiveDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
            externalTransferListRecord.externalTransferListEmployee.externalTransferRequest = externalTransferRequestService.save(params)

            if (externalTransferListRecord?.externalTransferListEmployee?.externalTransferRequest?.hasErrors()) {
                throw new ValidationException("Failed to save externalTransfer request",
                        externalTransferListRecord.externalTransferListEmployee.externalTransferRequest.errors)
            }
            externalTransferListRecord.externalTransferListEmployee.employee = externalTransferListRecord.externalTransferListEmployee.externalTransferRequest.employee
            externalTransferListRecord.externalTransferListEmployee.currentEmploymentRecord = externalTransferListRecord.externalTransferListEmployee.employee?.currentEmploymentRecord
            externalTransferListRecord.externalTransferListEmployee.toOrganizationId = externalTransferListRecord.externalTransferListEmployee.externalTransferRequest.toOrganizationId
        }
        return externalTransferListRecord
    }

    @Override
    Map getEmployeeRequestInfo(GrailsParameterMap params) {
        if (params["employeeId"]) {
            ExternalTransferRequest externalTransferRequest = externalTransferRequestService.getPreCreateInstance(params)
            if (externalTransferRequest?.hasErrors()) {
                return [success: false, message: externalTransferRequest.errors.globalError?.code]
            }
            return [success: true, externalTransferRequest: externalTransferRequest]
        } else {
            String failMessage = 'allowanceRequest.employee.notFound.error.label'
            return [success: false, message: failMessage]
        }
    }

    @Override
    Map getOperationFormInfo(GrailsParameterMap params) {
        return null
    }

    @Override
    Object getNewInstance(GrailsParameterMap params) {
        AocExternalTransferListRecord record
        if(params.listEmployeeId){
            record= AocExternalTransferListRecord.createCriteria().get {eq('externalTransferListEmployee.id', params.listEmployeeId)}
        }
        if(!record){
            record= new AocExternalTransferListRecord()
        }
        return record
    }

    @Override
    void updateHrRecordStatus(List<AocListRecord> aocListRecordList, String orderNumber) {
        List<AocExternalTransferListRecord> externalTransferListRecords = (List<AocExternalTransferListRecord>) aocListRecordList

        ZonedDateTime now = ZonedDateTime.now()
        externalTransferListRecords?.each { aocRecord ->
            if (aocRecord.externalTransferListEmployee.recordStatus == EnumListRecordStatus.NEW) {
                aocRecord.externalTransferListEmployee.recordStatus = aocRecord.recordStatus
                aocRecord.externalTransferListEmployee.addToExternalTransferListEmployeeNotes(new ExternalTransferListEmployeeNote(orderNo: orderNumber,
                        noteDate: now, externalTransferListEmployee: aocRecord.externalTransferListEmployee))
                // Set effective date same as order date
                aocRecord.externalTransferListEmployee.effectiveDate = now
                // flush is true to make changes visible to next phases through transaction
                aocRecord.externalTransferListEmployee.save(flush: true)
            }
        }
    }

    @Override
    DetachedCriteria search(GrailsParameterMap params) {
        /**
         * extract params and search for specific values for allowance
         */
        DetachedCriteria criteria= new DetachedCriteria(AocExternalTransferListRecord).build {

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
        AocExternalTransferListRecord record= (AocExternalTransferListRecord) listRecord
        return record?.externalTransferListEmployee?.externalTransferRequest?.employee?.profileStatus == EnumProfileStatus.LOCKED
    }
}
