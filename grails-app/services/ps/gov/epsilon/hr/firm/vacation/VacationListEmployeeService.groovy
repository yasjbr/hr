package ps.gov.epsilon.hr.firm.vacation

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
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -this service is aims to create vacation list employee
 * <h1>Usage</h1>
 * -this service is used to create vacation list employee
 * <h1>Restriction</h1>
 * -need vacation list & request created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class VacationListEmployeeService {

    MessageSource messageSource
    def formatService
    PersonService personService
    VacationListService vacationListService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationRequest.id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "vacationRequest.employee", type: "Employee", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "vacationRequest.requestTypeDescription", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationRequest.vacationType.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationRequest.fromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationRequest.toDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "vacationRequest.numOfDays", type: "integer", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacationRequest.external", type: "boolean", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "recordStatus", type: "enum", source: 'domain'],
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
        String vacationListId = params["vacationList.id"]
        Set vacationListEmployeeNotesIds = params.listString("vacationListEmployeeNotes.id")
        String vacationRequestId = params["vacationRequest.id"]
        String militaryRankId = params["militaryRank.id"]
        String employeeId = params["employee.id"]
        String vacationTypeId = params["vacationType.id"]
        ZonedDateTime requestDate = PCPUtils.parseZonedDateTime(params['requestDate'])
        ZonedDateTime fromDate = PCPUtils.parseZonedDateTime(params['fromDate'])
        ZonedDateTime returnDate = PCPUtils.parseZonedDateTime(params['returnDate'])
        ZonedDateTime toDate = PCPUtils.parseZonedDateTime(params['toDate'])
        ps.gov.epsilon.hr.enums.v1.EnumRequestStatus requestStatus = params["requestStatus"] ? ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.valueOf(params["requestStatus"]) : null
        String sSearchNumber = params["sSearch"]

        ZonedDateTime fromFromDate = PCPUtils.parseZonedDateTime(params['fromDateFrom'])
        ZonedDateTime toFromDate = PCPUtils.parseZonedDateTime(params['fromDateTo'])

        ZonedDateTime fromToDate = PCPUtils.parseZonedDateTime(params['toDateFrom'])
        ZonedDateTime toToDate = PCPUtils.parseZonedDateTime(params['toDateTo'])

        String external = params["external"]
        String status = params["status"]

        return VacationListEmployee.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    vacationRequest {
                        vacationType {
                            ilike("localName", sSearch)
                        }
                    }
                    if (sSearchNumber) {
                        vacationRequest {
                            eq("id", sSearchNumber)
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
                if (vacationListId) {
                    eq("vacationList.id", vacationListId)
                }
                if (vacationListEmployeeNotesIds) {
                    vacationListEmployeeNotes {
                        inList("id", vacationListEmployeeNotesIds)
                    }
                }
                if (vacationRequestId) {
                    eq("vacationRequest.id", vacationRequestId)
                }


                if (requestStatus) {
                    vacationRequest {
                        eq("requestStatus", requestStatus)
                    }
                }
                if (toDate) {
                    vacationRequest {
                        eq("toDate", toDate)
                    }
                }
                if (returnDate) {
                    vacationRequest {
                        eq("returnDate", returnDate)
                    }
                }

                if (fromDate) {
                    vacationRequest {
                        eq("fromDate", fromDate)
                    }
                }

                if (requestDate) {
                    vacationRequest {
                        eq("requestDate", requestDate)
                    }
                }

                if (vacationTypeId) {
                    vacationRequest {
                        vacationType {
                            eq("id", vacationTypeId)
                        }
                    }
                }

                if (employeeId) {
                    vacationRequest {
                        employee {
                            eq("id", employeeId)
                        }
                    }
                }
                if (militaryRankId) {
                    vacationRequest {
                        employee {
                            currentEmployeeMilitaryRank {
                                militaryRank {
                                    eq("id", militaryRankId)
                                }
                            }
                        }
                    }
                }

                //fromDate
                if (fromFromDate) {
                    vacationRequest {
                        ge("fromDate", fromFromDate)
                    }
                }

                if (toFromDate) {
                    vacationRequest {
                        lte("fromDate", toFromDate)
                    }

                }

                //toDate
                if (fromToDate) {
                    vacationRequest {
                        ge("toDate", fromToDate)
                    }
                }

                if (toToDate) {
                    vacationRequest {
                        lte("toDate", toToDate)
                    }
                }

                if (external) {
                    vacationRequest {
                        eq("external", Boolean.parseBoolean(external))
                    }
                }

                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {

                switch (columnName) {
                    case 'vacationRequest.vacationType.descriptionInfo.localName':
                        vacationRequest {
                            vacationType {
                                descriptionInfo {
                                    order("localName", dir)
                                }
                            }
                        }
                        break
                    case 'vacationRequest.fromDate':
                        vacationRequest {
                            order("fromDate", dir)
                        }
                        break
                    case 'vacationRequest.id':
                        vacationRequest {
                            order("trackingInfo.dateCreatedUTC", dir)
                        }
                        break
                    case 'vacationRequest.toDate':
                        vacationRequest {
                            order("toDate", dir)
                        }
                        break
                    case 'vacationRequest.numOfDays':
                        vacationRequest {
                            order("numOfDays", dir)
                        }
                        break
                    case 'vacationRequest.external':
                        vacationRequest {
                            order("external", dir)
                        }
                        break
                    case 'vacationRequest.requestStatus':
                        vacationRequest {
                            order("requestStatus", dir)
                        }
                        break
                    case 'id':
                        order("trackingInfo.dateCreatedUTC", dir)
                        break;
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
 * @return VacationListEmployee.
 */
    VacationListEmployee save(GrailsParameterMap params) {
        VacationListEmployee vacationListEmployeeInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            vacationListEmployeeInstance = VacationListEmployee.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (vacationListEmployeeInstance.version > version) {
                    vacationListEmployeeInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('vacationListEmployee.label', null, 'vacationListEmployee', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this vacationListEmployee while you were editing")
                    return vacationListEmployeeInstance
                }
            }
            if (!vacationListEmployeeInstance) {
                vacationListEmployeeInstance = new VacationListEmployee()
                vacationListEmployeeInstance.errors.reject('default.not.found.message', [messageSource.getMessage('vacationListEmployee.label', null, 'vacationListEmployee', LocaleContextHolder.getLocale())] as Object[], "This vacationListEmployee with ${params.id} not found")
                return vacationListEmployeeInstance
            }
        } else {
            vacationListEmployeeInstance = new VacationListEmployee()
        }
        try {
            vacationListEmployeeInstance.properties = params;
            vacationListEmployeeInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            vacationListEmployeeInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return vacationListEmployeeInstance
    }

/**
 * to delete model entry.
 * @param DeleteBean deleteBean.
 * @return DeleteBean.
 * @see DeleteBean.
 */
    DeleteBean delete(DeleteBean deleteBean, boolean isEncrypted = false) {
        try {

            List<VacationListEmployee> vacationListEmployeeList = null

            if (isEncrypted && HashHelper.decodeList(deleteBean.ids)) {
                /**
                 * get list of  vacation list employee by list of ids
                 */
                vacationListEmployeeList = VacationListEmployee.findAllByIdInList(HashHelper.decodeList(deleteBean.ids))
            } else if (deleteBean.ids) {
                /**
                 * get list of  vacation list employee by list of ids
                 */
                vacationListEmployeeList = VacationListEmployee.findAllByIdInList(deleteBean?.ids)
            }
            /**
             * get list of vacation request & revert status to APPROVED_BY_WORKFLOW
             */
            List<VacationRequest> vacationRequestList = vacationListEmployeeList?.vacationRequest
            vacationRequestList?.each { VacationRequest vacationRequest ->
                vacationRequest.requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
                if(vacationRequest?.internalOrderDate == null){
                    vacationRequest?.internalOrderDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                }
                if(vacationRequest?.externalOrderDate == null){
                    vacationRequest?.externalOrderDate = PCPUtils.getDEFAULT_ZONED_DATE_TIME()
                }
            }
            /**
             * delete list of vacation list employee
             */
            vacationListEmployeeList*.delete()
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
 * @return VacationListEmployee.
 */
    @Transactional(readOnly = true)
    VacationListEmployee getInstance(GrailsParameterMap params) {
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
     * @return vacationRequest.
     */
    @Transactional(readOnly = true)
    VacationListEmployee getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        PagedResultList vacationListEmployeeList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList


        if (vacationListEmployeeList) {

            /**
             * to employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: vacationListEmployeeList?.resultList?.vacationRequest?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList

            /**
             * assign employeeName for each employee in list
             */
            vacationListEmployeeList?.each { VacationListEmployee vacationListEmployee ->
                vacationListEmployee?.vacationRequest?.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == vacationListEmployee?.vacationRequest?.employee?.personId
                })
            }
        }
        return vacationListEmployeeList
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
        String id = params["vacationList.id"]
        GrailsParameterMap parameterMap = new GrailsParameterMap(["id": id], WebUtils.retrieveGrailsWebRequest().getCurrentRequest())
        VacationList vacationList = vacationListService.getInstance(parameterMap) // to disciplinary  list record
        PagedResultList resultList = this.searchWithRemotingValues(params) //get details
        Map map = [:]
        map.code = vacationList?.code
        map.coverLetter = vacationList?.coverLetter
        map.details = resultList
        return [map]
    }

}