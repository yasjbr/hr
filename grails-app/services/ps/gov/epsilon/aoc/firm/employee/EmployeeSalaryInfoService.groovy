package ps.gov.epsilon.aoc.firm.employee

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.converters.JSON
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import ps.gov.epsilon.aoc.enums.employee.v1.EnumSalaryClassification
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.utils.v1.HashHelper
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.dtos.v1.GovernorateDTO
import ps.police.pcore.v2.entity.lookups.CurrencyService
import ps.police.pcore.v2.entity.lookups.dtos.v1.CurrencyDTO
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.dtos.v1.OrganizationDTO
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo

/**
 * <h1>Purpose</h1>
 * -This service aims to save salary details and history-
 * <h1>Usage</h1>
 * -use to save salary details and distory-
 * <h1>Restriction</h1>
 * -no edit or delete-
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class EmployeeSalaryInfoService {

    MessageSource messageSource
    def formatService
    OrganizationService organizationService
    CurrencyService currencyService
    EmployeeService employeeService


    public static getEmployeeName = { formatService, EmployeeSalaryInfo dataRow, object, params ->
        return dataRow?.employee?.toString()
    }

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "employeeName", type: getEmployeeName, source: 'domain'],
            [sort: false, search: true, hidden: false, name: "employee.financialNumber", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.bankDTO.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.bankBranchDTO.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "internationalAccountNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "bankAccountNumber", type: "String", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "salaryClassification", type: "enum", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "salary", type: "double", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.currencyDTO.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "salaryDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "active", type: "Boolean", source: 'domain'],
    ]

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_TAB_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.bankDTO.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.bankBranchDTO.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "internationalAccountNumber", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "bankAccountNumber", type: "String", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "salaryClassification", type: "enum", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "salary", type: "double", source: 'domain'],
            [sort: false, search: true, hidden: false, name: "transientData.currencyDTO.descriptionInfo.localName", type: "String", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "salaryDate", type: "ZonedDate", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "active", type: "Boolean", source: 'domain'],
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
        Boolean active = params.boolean("active")
        Long bankBranchId = params.long("bankBranchId")
        Long bankId = params.long("bankId")
        String employeeId = params["employee.id"]
        Long firmId = params.long("firm.id") ? params.long("firm.id") : PCPSessionUtils.getValue("firmId")
        String internationalAccountNumber = params["internationalAccountNumber"]
        String bankAccountNumber = params["bankAccountNumber"]
        Double salary = params.double("salary")
        ps.gov.epsilon.aoc.enums.employee.v1.EnumSalaryClassification salaryClassification = params["salaryClassification"] ? ps.gov.epsilon.aoc.enums.employee.v1.EnumSalaryClassification.valueOf(params["salaryClassification"]) : null
        Long salaryCurrencyId = params.long("salaryCurrencyId")

        ZonedDateTime salaryDate = PCPUtils.parseZonedDateTime(params['salaryDate'])
        ZonedDateTime salaryDateFrom = PCPUtils.parseZonedDateTime(params['salaryDateFrom'])
        ZonedDateTime salaryDateTo = PCPUtils.parseZonedDateTime(params['salaryDateTo'])

        String militaryRankId = params["militaryRank.id"]
        String financialNumber = params["financialNumber"]
        String status = params["status"]


        return EmployeeSalaryInfo.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {
                    ilike("internationalAccountNumber", sSearch)
                }
            }
            and {
                if (id) {
                    eq("id", id)
                }
                if (ids) {
                    inList("id", ids)
                }
                if (active) {
                    eq("active", active)
                }
                if (bankBranchId) {
                    eq("bankBranchId", bankBranchId)
                }
                if (bankId) {
                    eq("bankId", bankId)
                }
                if (employeeId || financialNumber || militaryRankId) {
                    employee {
                        if (employeeId) {
                            eq("id", employeeId)
                        }
                        if (financialNumber) {
                            eq("financialNumber", financialNumber)
                        }
                        if (militaryRankId) {
                            currentEmployeeMilitaryRank {
                                eq("militaryRank.id", militaryRankId)
                            }
                        }
                    }
                }
                if (firmId) {
                    eq("firm.id", firmId)
                }
                if (internationalAccountNumber) {
                    ilike("internationalAccountNumber", "%${internationalAccountNumber}%")
                }
                if (bankAccountNumber) {
                    ilike("bankAccountNumber", "%${bankAccountNumber}%")
                }
                if (salary) {
                    eq("salary", salary)
                }
                if (salaryClassification) {
                    eq("salaryClassification", salaryClassification)
                }
                if (salaryCurrencyId) {
                    eq("salaryCurrencyId", salaryCurrencyId)
                }
                //todo set this condition with role_admin only, all other users just view the active records
                if (status) {
                    eq("trackingInfo.status", GeneralStatus.valueOf(status))
                } else {
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                }
                if (salaryDate) {
                    eq("salaryDate", salaryDate)
                }
                if (salaryDateFrom) {
                    ge("salaryDate", salaryDateFrom)
                }
                if (salaryDateTo) {
                    le("salaryDate", salaryDateTo)
                }
            }
            if (orderBy) {
                orderBy.each { row ->
                    order(row.name, row.direction ?: "asc")
                }
            } else if (columnName) {
                order(columnName, dir)
            } else {
                //use as default sort to show the last inserted
                order("trackingInfo.dateCreatedUTC", "desc")
            }

        }
    }

    /**
     * to search model entries.
     * @param GrailsParameterMap params the search map.
     * @return PagedResultList.
     */
    @Transactional(readOnly = true)
    PagedResultList searchWithRemotingValues(GrailsParameterMap params) {
        PagedResultList pagedResultList = this.search(params)
        List salaryCurrencyIds = pagedResultList.resultList.salaryCurrencyId
        List organizationIds = pagedResultList.resultList.bankId
        organizationIds.addAll(pagedResultList.resultList.bankBranchId)
        SearchBean bankSearchBean = new SearchBean()
        SearchBean currencySearchBean = new SearchBean()
        bankSearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: organizationIds))
        currencySearchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: 'ids[]', value1: salaryCurrencyIds))
        List<OrganizationDTO> organizations = organizationService?.searchOrganization(bankSearchBean).resultList
        List<CurrencyDTO> currencies = currencyService?.searchCurrency(currencySearchBean).resultList

        //get employee remote details
        List<String> employeeIds = pagedResultList?.resultList?.employee?.id
        GrailsParameterMap employeesParams = new GrailsParameterMap(["ids[]": employeeIds], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
        List<Employee> employeeList = employeeService?.searchWithRemotingValues(employeesParams)


        pagedResultList.each { EmployeeSalaryInfo employeeSalaryInfo ->
            employeeSalaryInfo.transientData.bankDTO = organizations.find {
                it.id == employeeSalaryInfo?.bankId
            }
            employeeSalaryInfo.transientData.bankBranchDTO = organizations.find {
                it.id == employeeSalaryInfo?.bankBranchId
            }
            employeeSalaryInfo.transientData.currencyDTO = currencies.find {
                it.id == employeeSalaryInfo?.salaryCurrencyId
            }
            employeeSalaryInfo?.employee = employeeList?.find { it?.id == employeeSalaryInfo?.employee?.id }
        }
    }

    /**
     * to save/update model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeSalaryInfo.
     */
    EmployeeSalaryInfo save(GrailsParameterMap params) {
        EmployeeSalaryInfo employeeSalaryInfoInstance
        /**
         * in case: id is encoded
         */
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        if (params.id) {
            employeeSalaryInfoInstance = EmployeeSalaryInfo.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeSalaryInfoInstance.version > version) {
                    employeeSalaryInfoInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeeSalaryInfo.label', null, 'employeeSalaryInfo', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeSalaryInfo while you were editing")
                    return employeeSalaryInfoInstance
                }
            }
            if (!employeeSalaryInfoInstance) {
                employeeSalaryInfoInstance = new EmployeeSalaryInfo()
                employeeSalaryInfoInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeSalaryInfo.label', null, 'employeeSalaryInfo', LocaleContextHolder.getLocale())] as Object[], "This employeeSalaryInfo with ${params.id} not found")
                return employeeSalaryInfoInstance
            }
        } else {
            employeeSalaryInfoInstance = new EmployeeSalaryInfo()
        }
        try {
            employeeSalaryInfoInstance.properties = params;
            employeeSalaryInfoInstance.save(failOnError: true, flush: true);

            //update employee financial info
            if (employeeSalaryInfoInstance?.active) {
                Employee employee = employeeSalaryInfoInstance?.employee
                employee?.bankBranchId = employeeSalaryInfoInstance?.bankBranchId
                employee?.bankAccountNumber = employeeSalaryInfoInstance?.bankAccountNumber
                employee?.internationalAccountNumber = employeeSalaryInfoInstance?.internationalAccountNumber
                employee?.internationalAccountNumber = employeeSalaryInfoInstance?.internationalAccountNumber
                employee?.validate()
                employee.save(failOnError: true, flush: true)
            }
        }
        catch (Exception ex) {
            ex.printStackTrace()
            transactionStatus.setRollbackOnly()
            employeeSalaryInfoInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeeSalaryInfoInstance
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeSalaryInfo.
     */
    @Transactional(readOnly = true)
    EmployeeSalaryInfo getInstance(GrailsParameterMap params) {
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
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeSalaryInfo.
     */
    @Transactional(readOnly = true)
    EmployeeSalaryInfo getInstanceWithRemotingValues(GrailsParameterMap params) {
        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }
        //if id is not null then return values from search method
        if (params.id) {
            PagedResultList results = searchWithRemotingValues(params)
            if (results) {
                return results[0]
            }
        }
        return null
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


    Map importFinancialData(GrailsParameterMap params, def file) {
        //get the employee instance
        Employee employee
        Map dataMap = [:]
        Boolean saved = true
        List errors = []
        try {
            if (file) {
                if (!file?.empty) {
                    def sheetHeader = []
                    def values = []
                    def workbook = WorkbookFactory.create(file?.getInputStream())
                    def sheet = workbook.getSheetAt(0)

                    for (cell in sheet.getRow(0).cellIterator()) {
                        sheetHeader << cell.stringCellValue
                    }

                    def headerFlag = true
                    for (row in sheet.rowIterator()) {
                        if (headerFlag) {
                            headerFlag = false
                            continue
                        }
                        def value = ''
                        def map = [:]
                        for (cell in row.cellIterator()) {
                            DataFormatter formatter = new DataFormatter();
                            switch (cell.cellType) {
                                case 1:

                                    String val = formatter.formatCellValue(cell);
                                    //value = cell.stringCellValue
                                    map["${sheetHeader[cell.columnIndex]}"] = val
                                    break
                                case 0:
                                    String val = formatter.formatCellValue(cell);
                                    //value = cell.stringCellValue
                                    map["${sheetHeader[cell.columnIndex]}"] = val
                                    break
                                default:
                                    value = ''
                            }
                        }
                        values.add(map)
                    }

                    String financialNumber
                    Long bankId
                    Long bankBranchId
                    String bankNo
                    String iban
                    Double salary
                    String currencySymbol
                    String classification

                    EmployeeSalaryInfo employeeSalaryInfo
                    GrailsParameterMap saveParams

                    values.eachWithIndex { def list, Integer i ->
                        if (list) {
                            try {
                                financialNumber = list.financialNumber
                                bankId = list.bankId ? Long.parseLong(list.bankId) : -1
                                bankBranchId = list.bankBranchId ? Long.parseLong(list.bankBranchId) : -1
                                bankNo = list.bankNo
                                iban = list.IBAN
                                bankNo = list.bankAccountNo
                                salary = list.salary ? Double.parseDouble(list.salary) : 0.0
                                currencySymbol = list.currencySymbol
                                classification = list.classification
                            } catch (Exception ex) {
                                errors << [field  : "global",
                                           message: messageSource.getMessage("employeeSalaryInfo.importExcel.error.label", null as Object[], "importExcel error", LocaleContextHolder.getLocale())]
                                saved = false
                                throw new Exception("Error in parsing excel data.")
                            }

                            if (!financialNumber || bankId == -1 || bankBranchId == -1 || !iban || salary == 0 || !currencySymbol || !classification) {
                                errors << [
                                        field  : "global",
                                        message: messageSource.getMessage("employeeSalaryInfo.importExcel.error.label", null as Object[], "importExcel error", LocaleContextHolder.getLocale()) + "" +
                                                " row = ${i} : (financialNumber:${financialNumber}, " +
                                                "bankId: ${bankId}, bankBranchId:${bankBranchId}, iban:${iban}, " +
                                                "salary:${salary}, currencySymbol:${currencySymbol}, classification:${classification})"
                                ]
                                saved = false
                                throw new Exception("null data in excel file.")
                            }



                            if (financialNumber) {
                                employee = Employee.findByFinancialNumber(financialNumber)
                                if(employee){
                                    SearchBean currencySearchBean = new SearchBean()
                                    currencySearchBean.searchCriteria.put("currencySymbol", new SearchConditionCriteriaBean(operand: 'currencySymbol', value1: currencySymbol))
                                    CurrencyDTO currency = currencyService?.getCurrency(currencySearchBean)
                                    saveParams = new GrailsParameterMap([:], WebUtils?.retrieveGrailsWebRequest()?.getCurrentRequest())
                                    saveParams["employee.id"] = employee?.id
                                    saveParams["firm.id"] = employee?.firm?.id
                                    saveParams.bankBranchId= bankBranchId
                                    saveParams.bankId= bankId
                                    saveParams.bankAccountNumber= bankNo
                                    saveParams.internationalAccountNumber= iban
                                    saveParams.salary= salary
                                    saveParams.salaryCurrencyId= currency?.id
                                    saveParams.salaryClassification= EnumSalaryClassification.valueOf(classification)
                                    saveParams.salaryDate = PCPUtils.parseZonedDateTime(params['salaryDate'])
                                    employeeSalaryInfo = this.save(saveParams)
                                }
                                else {
                                    errors << [
                                            field  : "global",
                                            message: messageSource.getMessage("employeeSalaryInfo.financialNumber.error.label", [(i+1)] as Object[], "importExcel error", LocaleContextHolder.getLocale())
                                    ]
                                    saved = false
                                    throw new Exception("not correct employee Financial Number")
                                }
                            }
                        }
                    }
                }
            } else {
                errors << [field  : "global",
                           message: messageSource.getMessage("list.request.notSelected.error", null as Object[], "No rows were selected to Approved", LocaleContextHolder.getLocale())]
                saved = false
            }
        } catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            if (!errors) {
                errors << messageSource.getMessage('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], 'general system error', LocaleContextHolder.getLocale())
            }
        }

        dataMap.put("errors", errors)
        dataMap.put("saved", saved)
        return dataMap
    }


}