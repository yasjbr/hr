package ps.gov.epsilon.hr.firm.allowance

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.person.PersonRelationShipsService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonRelationShipsDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service is aims to create allowance employee list
 * <h1>Usage</h1>
 * -this service is used to create allowance employee list
 * <h1>Restriction</h1>
 * -need allowance request & list
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class AllowanceListEmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    AllowanceListService allowanceListService
    PersonRelationShipsService personRelationShipsService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "allowanceRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.allowanceType.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            //[sort: true, search: true, hidden: false, name: "allowanceRequest.requestStatus", type: "enum", source: 'domain'],
    ]


    public static final List<String> LIST_DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "allowanceRequest.employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.allowanceType.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.transientData.personRelationShipsName", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.effectiveDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "allowanceRequest.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "allowanceRequest.requestDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
            //[sort: true, search: true, hidden: false, name: "allowanceRequest.requestStatus", type: "enum", source: 'domain'],
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
        String currentEmployeeMilitaryRankId = params["currentEmployeeMilitaryRank.id"]
        String currentEmploymentRecordId = params["currentEmploymentRecord.id"]
        ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus recordStatus = params["recordStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.valueOf(params["recordStatus"]) : null
        String allowanceListId = params["allowanceList.id"]
        Set allowanceListEmployeeNotesIds = params.listString("allowanceListEmployeeNotes.id")
        String allowanceRequestId = params["allowanceRequest.id"]
        String employeeId = params["employee.id"]
        String allowanceTypeId = params["allowanceType.id"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime returnDate = PCPUtils.parseZonedDateTime(params['returnDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null

        return AllowanceListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    allowanceRequest {
                        allowanceType {
                            ilike("localName", sSearch)
                        }
                    }
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (currentEmployeeMilitaryRankId) {
                    eq("currentEmployeeMilitaryRank.id", currentEmployeeMilitaryRankId)
                }
                if (currentEmploymentRecordId) {
                    eq("currentEmploymentRecord.id", currentEmploymentRecordId)
                }
                if (recordStatus) {
                    eq("recordStatus", recordStatus)
                }
                if (allowanceListId) {
                    eq("allowanceList.id", allowanceListId)
                }
                if (allowanceListEmployeeNotesIds) {
                    allowanceListEmployeeNotes {
                        inList("id", allowanceListEmployeeNotesIds)
                    }
                }
                if (allowanceRequestId) {
                    eq("allowanceRequest.id", allowanceRequestId)
                }


                if (requestStatus) {
                    allowanceRequest {
                        eq("requestStatus", requestStatus)
                    }
                }
                if (toDate) {
                    allowanceRequest {
                        eq("toDate", toDate)
                    }
                }
                if (returnDate) {
                    allowanceRequest {
                        eq("returnDate", returnDate)
                    }
                }

                if (fromDate) {
                    allowanceRequest {
                        eq("fromDate", fromDate)
                    }
                }

                if (requestDate) {
                    allowanceRequest {
                        eq("requestDate", requestDate)
                    }
                }

                if (allowanceTypeId) {
                    allowanceRequest {
                        allowanceType {
                            eq("id", allowanceTypeId)
                        }
                    }
                }

                if (employeeId) {
                    allowanceRequest {
                        employee {
                            eq("id", employeeId)
                        }
                    }
                }

            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {

                switch (columnName) {
                    case 'allowanceRequest.allowanceType.descriptionInfo.localName':
                        allowanceRequest {
                            allowanceType {
                                descriptionInfo {
                                    order("localName", dir)
                                }
                            }
                        }
                        break
                    case 'allowanceRequest.fromDate':
                        allowanceRequest {
                            order("fromDate", dir)
                        }
                        break
                    case 'allowanceRequest.toDate':
                        allowanceRequest {
                            order("toDate", dir)
                        }
                        break
                    case 'allowanceRequest.numOfDays':
                        allowanceRequest {
                            order("numOfDays", dir)
                        }
                        break
                    case 'allowanceRequest.internal':
                        allowanceRequest {
                            order("internal", dir)
                        }
                        break

                    case 'allowanceRequest.effectiveDate':
                        allowanceRequest {
                            order("effectiveDate", dir)
                        }
                        break
                    case 'allowanceRequest.requestDate':
                        allowanceRequest {
                            order("requestDate", dir)
                        }
                    case 'allowanceRequest.requestStatus':
                        allowanceRequest {
                            order("requestStatus", dir)
                        }
                        break
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break
                    default:
                        order(columnName, dir)
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
 * @return AllowanceListEmployee.
 */
    AllowanceListEmployee save(GrailsParameterMap params) {
        AllowanceListEmployee allowanceListEmployeeInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            allowanceListEmployeeInstance = AllowanceListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (allowanceListEmployeeInstance.version > version) {
                    allowanceListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('allowanceListEmployee.label', null, 'allowanceListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this allowanceListEmployee while you were editing")
                    return allowanceListEmployeeInstance
                }
            }
            if (!allowanceListEmployeeInstance) {
                allowanceListEmployeeInstance = new AllowanceListEmployee()
                allowanceListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('allowanceListEmployee.label', null, 'allowanceListEmployee', LocaleContextHolder.getLocale())] as Object[], "This allowanceListEmployee with ${params.id} not found")
                return allowanceListEmployeeInstance
            }
        } else {
            allowanceListEmployeeInstance = new AllowanceListEmployee()
        }
        try {
            allowanceListEmployeeInstance.properties = params;
            allowanceListEmployeeInstance.save(failOnError: true);
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            allowanceListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return allowanceListEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {

            List<AllowanceListEmployee> allowanceListEmployeeList = null

            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  allowance list employee by list of ids
                 */
                allowanceListEmployeeList = AllowanceListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  allowance list employee by list of ids
                 */
                allowanceListEmployeeList = AllowanceListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of allowance request & revert status to APPROVED_BY_WORKFLOW
             */
            List<AllowanceRequest> allowanceRequestList = allowanceListEmployeeList?.allowanceRequest
            allowanceRequestList?.each { AllowanceRequest allowanceRequest ->
                allowanceRequest.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                allowanceRequest?.validate()
                allowanceRequest?.save(failOnError:true, flush:true)
            }
            /**
             * delete list of allowance list employee
             */
            allowanceListEmployeeList*.delete()
            deleteBean.status = true
        }
        catch (Exception ex) {
            deleteBean.status = false
            deleteBean.responseMessage << ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))
        }
        return deleteBean
    }

/**
 * to get model entry.
 * @param GrailsParameterMap params the search map.
 * @return AllowanceListEmployee.
 */
    @Transactional(readOnly = true)
    AllowanceListEmployee getInstance(GrailsParameterMap params) {
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
     * @return AllowanceRequest.
     */
    @Transactional(readOnly = true)
    AllowanceListEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        PagedResultList allowanceListEmployeeList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<PersonDTO> relatedPersonDTOList
        List<PersonRelationShipsDTO> personRelationShipsDTOList

        if (allowanceListEmployeeList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: allowanceListEmployeeList?.resultList?.allowanceRequest?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * get list of relatedPersonId from list of  relationShipType
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: allowanceListEmployeeList?.resultList?.allowanceRequest?.personRelationShipsId))
            personRelationShipsDTOList = personRelationShipsService?.searchPersonRelationShips(searchBean)?.resultList
            /**
             * to related person name name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: personRelationShipsDTOList?.relatedPerson?.id))
            relatedPersonDTOList = personService?.searchPerson(searchBean)?.resultList


            allowanceListEmployeeList?.each { AllowanceListEmployee allowanceListEmployee ->

                /**
                 * assign employeeName for each employee in list
                 */
                allowanceListEmployee.allowanceRequest.employee.transientData.personDTO =  personDTOList?.find {
                    it?.id == allowanceListEmployee?.allowanceRequest?.employee?.personId
                }

                /**
                 * assign related person name for each employee in list
                 */
                PersonRelationShipsDTO personRelationShipsDTO = personRelationShipsDTOList?.find {
                    it.id == allowanceListEmployee?.allowanceRequest?.personRelationShipsId
                }
                PersonDTO personDTO = relatedPersonDTOList?.find {
                    it?.id == personRelationShipsDTO?.relatedPerson?.id
                }
                String relatedPersonName = personDTO?.localFullName?.toString()
                allowanceListEmployee.transientData.personRelationShipsName = relatedPersonName

            }

        }
        return allowanceListEmployeeList
    }

/**
 * to auto complete model entry.
 * @param GrailsParameterMap params the search map.
 * @return JSON.
 */
    @Transactional(readOnly = true)
    JSON autoComplete(GrailsParameterMap params) {
        List<Map> dataList = []
        String idProperty = params["idProperty"] ?: "id"
        String nameProperty = params["nameProperty"] ?: "descriptionInfo.localName"
        List autoCompleteReturnedInfo = params.list("autoCompleteReturnedInfo") ?: []
        try {
            grails.gorm.PagedResultList resultList = this.search(params)
            dataList = PCPUtils.toMapList(resultList, nameProperty, idProperty, autoCompleteReturnedInfo)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        return dataList as JSON
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
            DOMAIN_COLUMNS = this.LIST_DOMAIN_COLUMNS
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
        String id = params["allowanceList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        AllowanceList allowanceList = allowanceListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = allowanceList?.code
        map.coverLetter = allowanceList?.coverLetter
        map.details = resultList
        return [map]
    }

}