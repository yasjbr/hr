package ps.gov.epsilon.hr.firm.transfer

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import guiplugin.FormatService
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

import java.time.format.DateTimeFormatter

/**
 * <h1>Purpose</h1>
 * -this service is aims to create external transfer list employee
 * <h1>Usage</h1>
 * -this service is used to create external transfer list employee
 * <h1>Restriction</h1>
 * - need external transfer list employee & external transfer request
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class ExternalTransferListEmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    OrganizationService organizationService
    GovernorateService governorateService
    ExternalTransferListService externalTransferListService

    /**
     * to represent old employment record
     */
    public static oldEmploymentRecordFormat = { formatService, ExternalTransferListEmployee dataRow, object, params ->
        if (dataRow?.currentEmploymentRecord) {
            return dataRow?.currentEmploymentRecord?.department?.transientData?.governorateDTO?.descriptionInfo?.toString() + " / " + dataRow?.currentEmploymentRecord?.department?.toString()
        }
        return ""
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "externalTransferRequest.id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "oldEmploymentRecord", type: oldEmploymentRecordFormat, source: 'domain'],
            [sort: false, search: false, hidden: false, name: "transientData.organizationDTO.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "externalTransferRequest.encodedId", type: "string", source: 'domain', messagePrefix: 'EnumRequestStatus'],
    ]

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList search(GrailsParameterMap params) {

        // global setting.
        Integer max = params.int('max') ?: 10
        Integer offset = params.int('offset') ?: 0
        Integer column = params.int("orderColumn")
        String dir = params["orderDirection"]
        String columnName
        if (column) {
            columnName = DOMAIN_COLUMNS[column]?.name
        }
        String sSearch = PCPUtils.advanceFormatString((params["sSearch"] as String))
        List<String> ids = params.listString('ids[]')
        String id
        //in case, encoded id is passed, do the decode and search on long id:
        if (params.encodedId) {
            id = (HashHelper.decode(params.encodedId))
        } else {
            //in case id is not encoded
            id = params['id']
        }


        List<Map<String, String>> orderBy = params.list("orderBy")
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        ZonedDateTime effectiveDate = PCPUtils.parseZonedDateTime(params['effectiveDate'])
        ZonedDateTime effectiveDateFrom = PCPUtils.parseZonedDateTime(params['effectiveDateFrom'])
        ZonedDateTime effectiveDateTo = PCPUtils.parseZonedDateTime(params['effectiveDateTo'])
        String employeeId = params["employee.id"]
        String employeeIdList = params.long("employee.idList")
        String externalTransferListId = params["externalTransferList.id"]
        Set externalTransferListEmployeeNotesIds = params.listString("externalTransferListEmployeeNotes.id")
        String externalTransferRequestId = params["externalTransferRequest.id"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        Long toOrganizationId = params.long("toOrganizationId")
        Long toOrganizationIdList = params.long("toOrganizationIdList")
        Long fromGovernorateId = params.long("fromGovernorateIdList")
        String fromDepartmentId = params["fromDepartmentIdList"]
        String militaryRankId = params["militaryRank.id"]

        if (employeeIdList) {
            employeeId = employeeIdList
        }

        if (toOrganizationIdList) {
            toOrganizationId = toOrganizationIdList
        }

        return ExternalTransferListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (externalTransferRequestId || currentEmploymentRecordId || fromGovernorateId || fromDepartmentId) {

                    externalTransferRequest {

                        if (externalTransferRequestId) {
                            eq("id", externalTransferRequestId)
                        }

                        currentEmploymentRecord {

                            if (currentEmploymentRecordId) {
                                eq("id", currentEmploymentRecordId)
                            }

                            if (fromGovernorateId || fromDepartmentId) {
                                department {

                                    if (fromDepartmentId) {
                                        eq("id", fromDepartmentId)
                                    }

                                    if (fromGovernorateId) {
                                        eq("governorateId", fromGovernorateId)
                                    }
                                }
                            }
                        }
                    }
                }
                if (effectiveDate) {
                    eq("effectiveDate", effectiveDate)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (externalTransferListId) {
                    eq("externalTransferList.id", externalTransferListId)
                }
                if (externalTransferListEmployeeNotesIds) {
                    externalTransferListEmployeeNotes {
                        inList("id", externalTransferListEmployeeNotesIds)
                    }
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (toOrganizationId) {
                    eq("toOrganizationId", toOrganizationId)
                }

                if (militaryRankId) {
                    employee {
                        currentEmployeeMilitaryRank {
                            militaryRank {
                                eq("id", militaryRankId)
                            }
                        }
                    }
                }

                if (effectiveDateTo) {
                    le("effectiveDate", effectiveDateTo)
                }
                if (effectiveDateFrom) {
                    ge("effectiveDate", effectiveDateFrom)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                if (columnName == "requestId") {
                    request
                } else {
                    // solution of sorting by id problem after id become string
                    switch (columnName) {
                        case 'id':
                            order("trackingInfo.dateCreatedUTC", dir)
                            break;
                        case 'externalTransferRequest.id':
                            externalTransferRequest{
                                order("trackingInfo.dateCreatedUTC", dir)
                            }
                            break;
                        default:
                            order(columnName, dir)
                    }
                }

            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

        }
    }

/**
 * to save/update model entry.
 * @param GrailsParameterMap params the search map.
 * @return ExternalTransferListEmployee.
 */
    ExternalTransferListEmployee save(GrailsParameterMap params) {
        ExternalTransferListEmployee externalTransferListEmployeeInstance
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            externalTransferListEmployeeInstance = ExternalTransferListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (externalTransferListEmployeeInstance.version > version) {
                    externalTransferListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('externalTransferListEmployee.label', null, 'externalTransferListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this externalTransferListEmployee while you were editing")
                    return externalTransferListEmployeeInstance
                }
            }
            if (!externalTransferListEmployeeInstance) {
                externalTransferListEmployeeInstance = new ExternalTransferListEmployee()
                externalTransferListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('externalTransferListEmployee.label', null, 'externalTransferListEmployee', LocaleContextHolder.getLocale())] as Object[], "This externalTransferListEmployee with ${params.id} not found")
                return externalTransferListEmployeeInstance
            }
        } else {
            externalTransferListEmployeeInstance = new ExternalTransferListEmployee()
        }
        try {
            Employee employee = null
            externalTransferListEmployeeInstance.properties = params;

            /**
             * get employee by id
             */
            if (params["employee.id"]) {
                employee = Employee?.load(params["employee.id"])
            }

            /**
             * assign current employment record
             */
            if (employee?.currentEmploymentRecord) {
                externalTransferListEmployeeInstance?.currentEmploymentRecord = employee?.currentEmploymentRecord
            }

            /**
             * set record status to NEW
             */
            externalTransferListEmployeeInstance.recordStatus = EnumListRecordStatus.NEW



            externalTransferListEmployeeInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            externalTransferListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return externalTransferListEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {
            List<ExternalTransferListEmployee> externalTransferListEmployeeList = []
            def ids = deleteBean?.ids
            if (isEncrypted) {
                ids = HashHelper.decodeList(deleteBean.ids)
            }
            externalTransferListEmployeeList = ExternalTransferListEmployee.findAllByIdInList(ids)
            externalTransferListEmployeeList.each { ExternalTransferListEmployee externalTransferListEmployee ->
                //delete externalTransferRequest
                externalTransferListEmployee.externalTransferRequest.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                externalTransferListEmployee.externalTransferRequest.save(flush: true)
                externalTransferListEmployee.delete(flush: true)
            }
            //check that at least on record is set to deleted
            if (externalTransferListEmployeeList) {
                deleteBean.status = true
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean

    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return ExternalTransferListEmployee.
 */
    @Transactional(readOnly = true)
    ExternalTransferListEmployee getInstance(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = search(params)
            if (results) {
                return results[0]
            }
        }
        return null

    }

    /**
     * to get model entry with remoting values
     * @param GrailsParameterMap params the search map.
     * @return ExternalTransferListEmployee.
     */
    @Transactional(readOnly = true)
    ExternalTransferListEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            // if result is exist and there is a remoting values using getRemotingValues to return values from remoting
            if (results) {
                return results[0]
            }
        }
        return null
    }

    /**
     * to search model entries with remoting value
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    public PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList externalTransferListEmployeeList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<OrganizationDTO> organizationDTOList
        List<GovernorateDTO> governorates


        if (externalTransferListEmployeeList) {

            /**
             * to get employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: externalTransferListEmployeeList?.resultList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * to get organization name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: externalTransferListEmployeeList?.resultList?.toOrganizationId))
            organizationDTOList = organizationService?.searchOrganization(searchBean)?.resultList

            //get governorate info
            List governorateIds = externalTransferListEmployeeList?.resultList?.currentEmploymentRecord?.department?.governorateId.toList()?.unique()
            governorates = governorateService.searchGovernorate(new SearchBean(searchCriteria: ["ids[]": new SearchConditionCriteriaBean(operand: 'ids[]', value1: governorateIds)]))?.resultList

            /**
             * assign employeeName & organizationName for each employee in list
             */
            externalTransferListEmployeeList?.each { ExternalTransferListEmployee externalTransferListEmployee ->

                externalTransferListEmployee?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == externalTransferListEmployee?.employee?.personId
                })

                externalTransferListEmployee?.transientData?.put("organizationDTO", organizationDTOList?.find {
                    it?.id == externalTransferListEmployee?.toOrganizationId
                })

                //set governorate info
                externalTransferListEmployee.currentEmploymentRecord.department.transientData.governorateDTO = governorates.find {
                    it.id == externalTransferListEmployee?.currentEmploymentRecord?.department?.governorateId
                }
            }
        }
        return externalTransferListEmployeeList
    }

/**
 * Convert paged result list to map depends on DOMAINS_COLUMNS.
 * @param def resultList may be PagedResultList or PagedList.
 * @param GrailsParameterMap params the search map
 * @param List < String >  DOMAIN_COLUMNS the list of model column names.
 * @return Map.
 * @see PagedResultList.
 * @see PagedList.
 */
    @Transactional(readOnly = true)
    public Map resultListToMap(def resultList, GrailsParameterMap params, List<String> DOMAIN_COLUMNS = null) {
        if (!DOMAIN_COLUMNS) {
            DOMAIN_COLUMNS = this.DOMAIN_COLUMNS
        }
        Map dataToRender = formatService.buildDataToDataTable(DOMAIN_COLUMNS, resultList, params)
        formatService.cleanUpGorm();
        return dataToRender
    }

    /**
     * custom method to get report data with custom format
     * @param params
     * @return PagedList to be passed to filter
     */
    @Transactional(readOnly = true)
    List getReportData(GrailsParameterMap params) {
        String id = params["externalTransferList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        ExternalTransferList externalTransferList = externalTransferListService.getInstance(parameterMap)
        // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = externalTransferList?.code
        map.coverLetter = externalTransferList?.coverLetter
        map.details = resultList
        return [map]
    }


}