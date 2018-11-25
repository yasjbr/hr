package ps.gov.epsilon.hr.firm.vacation

import grails.gorm.PagedResultList
import grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.runtime.DateGroovyMethods
import org.grails.web.util.WebUtils
import org.springframework.context.MessageSource
import grails.transaction.Transactional
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.lookups.ColorService
import ps.police.pcore.v2.entity.lookups.dtos.v1.ColorDTO
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.dtos.v1.PersonDTO

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * -this service is aims to create vacation balance for employee
 * <h1>Usage</h1>
 * -this service is used to create vacation balance for employee
 * <h1>Restriction</h1>
 * -need employee & vacation type created before
 * @see MessageSource
 * @see FormatService
 * */
@Transactional
class EmployeeVacationBalanceService {

    MessageSource messageSource
    def formatService
    PersonService personService
    ColorService colorService

    EmployeeService employeeService
    VacationConfigurationService vacationConfigurationService

    /**
     * to control model columns when processing model operations.
     * @return List < String > .
     */
    public static final List<String> DOMAIN_COLUMNS = [
            [sort: true, search: true, hidden: true, name: "encodedId", type: "string", source: 'domain'],
            [sort: true, search: true, hidden: true, name: "id", type: "long", source: 'domain'],
            [sort: false, search: false, hidden: false, name: "employee", type: "Employee", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacationConfiguration.vacationType.descriptionInfo.localName", type: "string", source: 'domain'],
            [sort: false, search: false, hidden: true, name: "vacationConfiguration.vacationType.transientData.colorDTO.rgbHexa", type: "string", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "annualBalance", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "balance", type: "Short", source: 'domain'],
            [sort: true, search: true, hidden: false, name: "numberOfTimesUsed", type: "Long", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "oldTransferBalance", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "validFromDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "validToDate", type: "ZonedDate", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "vacationDueYear", type: "Short", source: 'domain'],
            [sort: true, search: false, hidden: false, name: "isCurrent", type: "Boolean", source: 'domain']

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
        Short annualBalance = params.long("annualBalance")
        Short balance = params.long("balance")
        String employeeId = params["employee.id"]
        Short numberOfTimesUsed = params.short("numberOfTimesUsed")
        Short oldTransferBalance = params.long("oldTransferBalance")
        String vacationConfigurationId = params["vacationConfiguration.id"]
        Short vacationDueYear = params.long("vacationDueYear")
        ZonedDateTime validFromDate = PCPUtils.parseZonedDateTime(params['validFromDate'])
        ZonedDateTime validToDate = PCPUtils.parseZonedDateTime(params['validToDate'])
        String vacationTypeId = params["vacationType.id"]

        ZonedDateTime fromValidFromDate = PCPUtils.parseZonedDateTime(params['validFromDateFrom'])
        ZonedDateTime toValidFromDate = PCPUtils.parseZonedDateTime(params['validFromDateTo'])

        ZonedDateTime fromValidToDate = PCPUtils.parseZonedDateTime(params['validToDateFrom'])
        ZonedDateTime toValidToDate = PCPUtils.parseZonedDateTime(params['validToDateTo'])

        Integer sSearchNumber = params.int("sSearch")
        ZonedDateTime sSearchDate = PCPUtils.parseZonedDateTime(params["sSearch"])

        Short year = params.short("year")

        String status = params["status"]
        String militaryRankId = params["militaryRank.id"]

        String isCurrent = params["isCurrent"]



        return EmployeeVacationBalance.createCriteria().list(max: max, offset: offset) {
            if (sSearch) {
                or {

                    vacationConfiguration {
                        vacationType {
                            descriptionInfo {
                                ilike("localName", sSearch)
                            }
                        }
                    }


                    if (sSearchNumber) {
                        eq("annualBalance", sSearchNumber as Short)
                        eq("balance", sSearchNumber as Short)
                        eq("numberOfTimesUsed", sSearchNumber as Long)
                        eq("oldTransferBalance", sSearchNumber as Short)
                        eq("oldTransferBalance", sSearchNumber as Short)
                        eq("vacationDueYear", sSearchNumber as Short)
                    }

                    if (sSearchDate) {
                        le("validToDate", sSearchDate)
                        le("validFromDate", sSearchDate)
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
                if (annualBalance) {
                    eq("annualBalance", annualBalance)
                }
                if (balance) {
                    eq("balance", balance)
                }
                if (employeeId) {
                    eq("employee.id", employeeId)
                }
                if (numberOfTimesUsed) {
                    eq("numberOfTimesUsed", numberOfTimesUsed)
                }
                if (oldTransferBalance) {
                    eq("oldTransferBalance", oldTransferBalance)
                }
                if (vacationConfigurationId) {
                    eq("vacationConfiguration.id", vacationConfigurationId)
                }
                if (vacationTypeId) {
                    vacationConfiguration {
                        vacationType {
                            eq("id", vacationTypeId)
                        }
                    }
                }

                if (vacationDueYear) {
                    eq("vacationDueYear", vacationDueYear)
                }
                if (validFromDate) {
                    le("validFromDate", validFromDate)
                }
                if (validToDate) {
                    le("validToDate", validToDate)
                }

                //fromValidFromDate
                if (fromValidFromDate) {
                    ge("validFromDate", fromValidFromDate)
                }
                if (toValidFromDate) {
                    le("validFromDate", toValidFromDate)
                }
                //fromValidToDate
                if (fromValidToDate) {
                    ge("validToDate", fromValidToDate)
                }
                if (toValidToDate) {
                    le("validToDate", toValidToDate)
                }

                if (year) {
                    eq("vacationDueYear", year)
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

                employee {
                    eq("firm.id", PCPSessionUtils.getValue("firmId"))
                }


                if (isCurrent) {
                    if (isCurrent != "all") {
                        eq("isCurrent", params.boolean("isCurrent"))
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
                    case 'vacationConfiguration.vacationType.descriptionInfo.localName':
                        vacationConfiguration {
                            vacationType {
                                descriptionInfo {
                                    order("localName", dir)
                                }
                            }
                        }
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
 * @return EmployeeVacationBalance.
 */
    EmployeeVacationBalance save(GrailsParameterMap params) {
        EmployeeVacationBalance employeeVacationBalanceInstance

        if (params.encodedId) {
            params.id = HashHelper.decode(params.encodedId)
        }


        if (params.id) {
            employeeVacationBalanceInstance = EmployeeVacationBalance.get(params["id"])
            if (params.long("version")) {
                long version = params.long("version")
                if (employeeVacationBalanceInstance.version > version) {
                    employeeVacationBalanceInstance.errors.reject('default.optimistic.locking.failure', [messageSource.getMessage('employeeVacationBalance.label', null, 'employeeVacationBalance', LocaleContextHolder.getLocale())] as Object[], "Another user has updated this employeeVacationBalance while you were editing")
                    return employeeVacationBalanceInstance
                }
            }
            if (!employeeVacationBalanceInstance) {
                employeeVacationBalanceInstance = new EmployeeVacationBalance()
                employeeVacationBalanceInstance.errors.reject('default.not.found.message', [messageSource.getMessage('employeeVacationBalance.label', null, 'employeeVacationBalance', LocaleContextHolder.getLocale())] as Object[], "This employeeVacationBalance with ${params.id} not found")
                return employeeVacationBalanceInstance
            }
        } else {
            employeeVacationBalanceInstance = new EmployeeVacationBalance()
        }
        try {
            employeeVacationBalanceInstance.properties = params;
            employeeVacationBalanceInstance.save();
        }
        catch (Exception ex) {
            transactionStatus.setRollbackOnly()
            employeeVacationBalanceInstance.errors.reject('default.internal.server.error', [ex?.cause?.localizedMessage?.substring(0, ex?.cause?.localizedMessage?.indexOf("Detail:"))] as Object[], "")
        }
        return employeeVacationBalanceInstance
    }

    /**
     * to get model entry.
     * @param GrailsParameterMap params the search map.
     * @return EmployeeVacationBalance.
     */
    @Transactional(readOnly = true)
    EmployeeVacationBalance getInstance(GrailsParameterMap params) {
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
     * @return EmployeeVacationBalance.
     */
    @Transactional(readOnly = true)
    EmployeeVacationBalance getInstanceWithRemotingValues(GrailsParameterMap params) {
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
        PagedResultList employeeVacationBalanceList = search(params)

        SearchBean searchBean
        List<PersonDTO> personDTOList
        List<ColorDTO> colorDTOList


        if (employeeVacationBalanceList) {

            /**
             * to gt employee name from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: employeeVacationBalanceList?.resultList?.employee?.personId))
            personDTOList = personService?.searchPerson(searchBean)?.resultList
            /**
             * to get vacation's color from core
             */
            searchBean = new SearchBean()
            searchBean.searchCriteria.put("ids[]", new SearchConditionCriteriaBean(operand: "ids[]", value1: employeeVacationBalanceList?.resultList?.vacationConfiguration?.vacationType?.colorId))
            colorDTOList = colorService?.searchColor(searchBean)?.resultList

            employeeVacationBalanceList?.each { EmployeeVacationBalance employeeVacationBalance ->

                employeeVacationBalance.employee?.transientData?.put("personDTO", personDTOList?.find {
                    it?.id == employeeVacationBalance?.employee?.personId
                })

                employeeVacationBalance?.vacationConfiguration?.vacationType?.transientData?.put("colorDTO", colorDTOList?.find {
                    it?.id == employeeVacationBalance?.vacationConfiguration?.vacationType?.colorId
                })
            }
        }
        return employeeVacationBalanceList
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
     * This method used to be able to get the suitable vacation configration for  specific employee
     * @param String employeeId: id of the selected employee who we want to calculate his balance
     * @param short year : present the year that we want to calculate the employee balance in it current year is the default value
     * @param boolean recalculateBalance: Recalculate Balance represent if the selected year calculated before
     * @return Map
     */
    public Map calculateAllEmployeeYearlyBalance(Short year = new Short(DateGroovyMethods.format(new Date(), 'yyyy')), Boolean recalculateBalance = Boolean.FALSE) {
        Map returnValue = [:]

        //set default value for year if it null
        if (!year) {
            year = new Short(DateGroovyMethods.format(new Date(), 'yyyy'))
        }

        Map queryParams = [statusCategory: EnumEmployeeStatusCategory.COMMITTED.value, firmId: PCPSessionUtils.getValue("firmId")]
        try {
            // Get count of employees that we would like to calculate their balance
            //todo activate the firm in where condition
            String query = " from Employee e where e.categoryStatus.id=:statusCategory and e.firm.id=:firmId   "
            if (!recalculateBalance) {
                query = query + " and e.id not in (select eb.employee.id from EmployeeVacationBalance eb where eb.vacationDueYear=:vacationDueYear )"
                queryParams.put("vacationDueYear", year)
            }


            List<Integer> countOfEmployeesForCalculationList = Employee.executeQuery("select count(*)  " + query, queryParams)

            Integer countOfEmployeesForCalculation = countOfEmployeesForCalculationList?.get(0)

            if (countOfEmployeesForCalculation > 0) {
                int max = 100
                int offset = 0
                int calculatedRecords = countOfEmployeesForCalculation
                List<Employee> employeeLIst = null
                Map employeeCalculationResult

                queryParams.put("max", max)
                queryParams.put("offset", offset)

                while (calculatedRecords > 0) {
                    employeeLIst = Employee.executeQuery(query, queryParams)
                    if (employeeLIst) {
                        employeeLIst.each { Employee employee ->
                            employeeCalculationResult = calculateEmployeeYearlyBalance(employee, year, recalculateBalance)
                            if (employeeCalculationResult) {
                                returnValue.put(employee?.personId?.toString(), employeeCalculationResult.get("error"))
                            }
                        }
                        calculatedRecords = calculatedRecords - employeeLIst.size()
                        queryParams.put("offset", (offset + max))
                    } else {
                        calculatedRecords = 0
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
            returnValue.put("error", e?.message)
        }
        return returnValue
    }

    /**
     * This method used to be able to get the suitable vacation configration for  specific employee
     * @param String employeeId: id of the selected employee who we want to calculate his balance
     * @param short year : present the year that we want to calculate the employee balance in it current year is the default value
     * @param boolean recalculateBalance: Recalculate Balance represent if the selected year calculated before
     * @return Map
     */
    public Map calculateEmployeeYearlyBalanceById(String employeeId, Short year = new Short(DateGroovyMethods.format(new Date(), 'yyyy')), Boolean recalculateBalance = Boolean.FALSE) {
        Map returnValue = [:]

        //set default value for year if it null
        if (!year) {
            year = new Short(DateGroovyMethods.format(new Date(), 'yyyy'))
        } else {
            Short currentYear = new Short(DateGroovyMethods.format(new Date(), 'yyyy'))
            if (year - currentYear > 1) {
                returnValue.put("error", "employeeVacationBalance.yearBalance.notAllowed")
            } else if (year < currentYear) {
                returnValue.put("error", "employeeVacationBalance.yearBalance.notAllowed")
            }
        }

        try {
            //get employee information
            GrailsParameterMap params = new GrailsParameterMap([id: employeeId], null)

            Employee employee = employeeService.getInstanceWithRemotingValues(params)
            if (employee) {
                return calculateEmployeeYearlyBalance(employee, year, recalculateBalance)
            } else {

                // not valid employee return f
//            throw new Exception("employee.search.notFound")
                returnValue.put("error", "employee.search.notFound")
            }
        } catch (Exception e) {
            e.printStackTrace()
            returnValue[e?.getMessage()]
        }
        return returnValue
    }

    public Map calculateEmployeeYearlyBalance(Employee employee, Short year = new Short(DateGroovyMethods.format(new Date(), 'yyyy')), Boolean recalculateBalance = Boolean.FALSE) {

        Map returnValue = [:]

        //set default value for year if it null
        if (!year) {
            year = new Short(DateGroovyMethods.format(new Date(), 'yyyy'))
        }

        //get employee suitable vacation configuration for the selected employee
        List<VacationConfiguration> employeeVacationConfigrationsList = vacationConfigurationService.getEmployeeSuitableConfigration(employee)

        if (employeeVacationConfigrationsList) {
            ZonedDateTime validityFromDate = PCPUtils.parseZonedDateTime(("01/01/" + year))
            ZonedDateTime validityTodate = PCPUtils.parseZonedDateTime(("31/12/" + year))

            //check if it not the current year
            if (year < employee?.employmentDate.year) {
                returnValue.put("error", "employeeVacationBalance.calculationYear.lessThanEmploymentDate")
            } else {
                //check the validity date based on the employment period
                if (validityFromDate < employee.joinDate) {
                    validityFromDate = employee?.joinDate
                }

                EmployeeVacationBalance employeeVacationBalance
                EmployeeVacationBalance employeeOldVacationBalance

                //get the employee balance information for the specified year
                List<EmployeeVacationBalance> employeeVacationBalanceForCurrentYearList = EmployeeVacationBalance.findAllByEmployeeAndVacationDueYear(employee, year)
                //if the employee has old records for the selected year or the user want to recalculate we will recalculate the balance
                if (employeeVacationBalanceForCurrentYearList) {
                    if (recalculateBalance) {
                        // we will recalculate the balance but we will based on the saved balance not the configaration list to be able to set the balance to 0
                        //if the vacation type become not valid not as the insert process
                        VacationConfiguration vacationConfiguration

                        //get the balance of the selected employee for the previos year
                        List<EmployeeVacationBalance> employeeVacationBalanceForLastYearList = EmployeeVacationBalance.findAllByEmployeeAndVacationDueYear(employee, year - 1)

                        employeeVacationBalanceForCurrentYearList?.each {

                            vacationConfiguration = employeeVacationConfigrationsList.find { VacationConfiguration rowVacationConfiguration ->
                                //We did not use the ID of vacation config to find the value to avoid any changes in the config
                                it.vacationConfiguration.vacationType == rowVacationConfiguration.vacationType
                            }
                            if (vacationConfiguration) {
                                //get the old balance for  Vacation Configuration
                                employeeOldVacationBalance = employeeVacationBalanceForLastYearList?.find { EmployeeVacationBalance prevVacationBalance ->
                                    prevVacationBalance?.vacationConfiguration?.vacationType == vacationConfiguration?.vacationType
                                }

                                // Apply rules on the balance before save it
                                it = applyRulesOnEmployeeBalance(employee, vacationConfiguration, it, employeeOldVacationBalance)

                                employeeVacationConfigrationsList.remove(vacationConfiguration)
                            } else {//the configuration become not valid
//                                it.balance = 0
                                it.trackingInfo.status = GeneralStatus.DELETED
                            }

                            it.save(failOnError: true)
                        }

                        // apply the check for the configuration that not included in the old vacation and added to be suitable for employee after last calculation process
                        //add new vacation balance based on the configuration
                        //todo try to remove the code duplication
                        employeeVacationConfigrationsList.each { VacationConfiguration rowVacationConfiguration ->
                            //get the old balance for  Vacation Configuration
                            employeeOldVacationBalance = employeeVacationBalanceForLastYearList?.find {
                                it?.vacationConfiguration?.vacationType == rowVacationConfiguration?.vacationType
                            }
                            //set the new balance object value
                            employeeVacationBalance = new EmployeeVacationBalance(employee: employee, vacationConfiguration: rowVacationConfiguration, vacationDueYear: year,
                                    validFromDate: validityFromDate, validToDate: validityTodate, annualBalance: rowVacationConfiguration?.maxAllowedValue,
                                    balance: rowVacationConfiguration?.maxAllowedValue, numberOfTimesUsed: 0, oldTransferBalance: 0)

                            // Apply rules on the balance before save it
                            employeeVacationBalance = applyRulesOnEmployeeBalance(employee, rowVacationConfiguration, employeeVacationBalance, employeeOldVacationBalance)

                            employeeVacationBalance.save(failOnError: true)

                        }

                    } else {
                        // nothing to do just inform the user that the selected employee has old balance
//                        throw new Exception("employeeVacationBalance.currentYearBalance.exist")
                        returnValue.put("error", "employeeVacationBalance.currentYearBalance.exist")
                    }
                } else {
                    //the selected employee and the selected balance not exist and we need to create new records for it
                    //get the balance of the selected employee for the previos year
                    List<EmployeeVacationBalance> employeeVacationBalanceForLastYearList = EmployeeVacationBalance.findAllByEmployeeAndVacationDueYear(employee, year - 1)
                    //add new vacation balance based on the configuration
                    employeeVacationConfigrationsList.each { VacationConfiguration vacationConfiguration ->
                        //get the old balance for  Vacation Configuration
                        employeeOldVacationBalance = employeeVacationBalanceForLastYearList?.find {
                            it?.vacationConfiguration?.vacationType == vacationConfiguration?.vacationType
                        }
                        //set the new balance object value
                        employeeVacationBalance = new EmployeeVacationBalance(employee: employee, vacationConfiguration: vacationConfiguration, vacationDueYear: year,
                                validFromDate: validityFromDate, validToDate: validityTodate, annualBalance: vacationConfiguration?.maxAllowedValue,
                                balance: vacationConfiguration?.maxAllowedValue, numberOfTimesUsed: 0, oldTransferBalance: 0)

                        // Apply rules on the balance before save it
                        employeeVacationBalance = applyRulesOnEmployeeBalance(employee, vacationConfiguration, employeeVacationBalance, employeeOldVacationBalance)

                        employeeVacationBalance.save(failOnError: true)
                    }
                }
            }
        } else {
            //no vacation configration available for selected employee
            //update the old balance configaration to be deleted if it exists
            EmployeeVacationBalance.executeUpdate("update EmployeeVacationBalance eb set eb.trackingInfo.status=:deleteStatus where eb.employee.id=:employeeId ",
                    [
                            deleteStatus: GeneralStatus.DELETED,
                            employeeId  : employee.id
                    ])
//                throw new Exception("vacationConfiguration.forEmployee.notFound")
            returnValue.put("error", "vacationConfiguration.forEmployee.notFound")
        }

        return returnValue
    }

    public EmployeeVacationBalance applyRulesOnEmployeeBalance(Employee employee, VacationConfiguration vacationConfiguration, EmployeeVacationBalance employeeNewVacationBalance, EmployeeVacationBalance employeeOldVacationBalance) {

        Short consumedBalance = (employeeNewVacationBalance.annualBalance + employeeNewVacationBalance.oldTransferBalance) - employeeNewVacationBalance.balance

        // Check the employment less than one year specific for breakable vacation
        //if the employment period less than one year and breakable
        if (vacationConfiguration.isBreakable && employee.employmentPeriodInMonths < 12) {
            employeeNewVacationBalance.annualBalance = (Math.round(vacationConfiguration.maxAllowedValue / 12 * (employeeNewVacationBalance.validToDate.monthValue - employeeNewVacationBalance.validFromDate.monthValue + 1))).shortValue()
        } else {
            employeeNewVacationBalance.annualBalance = vacationConfiguration.maxAllowedValue
        }

        //apply the old balance effect
        if (employeeOldVacationBalance) {
            if (vacationConfiguration.isTransferableToNewYear) {
                employeeNewVacationBalance.oldTransferBalance = employeeOldVacationBalance.balance * vacationConfiguration.vacationTransferValue
                employeeNewVacationBalance.balance = employeeNewVacationBalance.annualBalance + employeeNewVacationBalance.oldTransferBalance - consumedBalance
            } else {
                employeeNewVacationBalance.oldTransferBalance = 0
                employeeNewVacationBalance.annualBalance = vacationConfiguration.maxAllowedValue
                employeeNewVacationBalance.balance = employeeNewVacationBalance.annualBalance - consumedBalance
            }
        } else {
            employeeNewVacationBalance.oldTransferBalance = vacationConfiguration.isTransferableToNewYear ? employeeNewVacationBalance.oldTransferBalance : 0
            employeeNewVacationBalance.balance = employeeNewVacationBalance.annualBalance - consumedBalance
        }

        //check if the balance exceed the max balance
        if (vacationConfiguration.maxBalance && employeeNewVacationBalance.balance > vacationConfiguration.maxBalance) {
            short exceededBalance = employeeNewVacationBalance.balance - vacationConfiguration.maxBalance
            employeeNewVacationBalance.balance = vacationConfiguration.maxBalance
            employeeNewVacationBalance.oldTransferBalance = employeeNewVacationBalance.oldTransferBalance - exceededBalance
        }

        //check if the vacation frequency of usage if it reach the frequency # set the balance to 0 to prevent employee from take it again
        if (vacationConfiguration.frequency != 0) {
            //get the count of taking this vacation over all employment period
            Integer frequence = getEmployeeVacationRequestCount(employee, vacationConfiguration.vacationType)
            if (frequence >= vacationConfiguration.frequency) {
                employeeNewVacationBalance.balance = 0
            }
        }
        return employeeNewVacationBalance
    }


    public Boolean updateEmployeeVacationBalance(String employeeId, String vacationTypeId, Short vacationDueYear, Short vacationDays) {
        Integer updateRecordsCount = EmployeeVacationBalance.executeUpdate(" update EmployeeVacationBalance eb set eb.balance=eb.balance-:vacationDays " +
                ", eb.numberOfTimesUsed=eb.numberOfTimesUsed+1 " +
                " where eb.employee.id=:employeeId " +
                "and eb.vacationDueYear=:vacationDueYear " +
                "and eb.vacationConfiguration.id=(select id from VacationConfiguration v where  v.vacationType.id=:vacationTypeId and v.id=eb.vacationConfiguration.id)",
                [
                        vacationDays   : vacationDays,
                        employeeId     : employeeId,
                        vacationDueYear: vacationDueYear,
                        vacationTypeId : vacationTypeId

                ])
        return (updateRecordsCount == 0 ? false : true)
    }


    public Integer setCurrentEmployeeVacationBalance(EmployeeVacationBalance employeeVacationBalance) {
        return EmployeeVacationBalance.executeUpdate("Update EmployeeVacationBalance set isCurrent=:isCurrent where id!=:id and employee=:employee and vacationConfiguration=:vacationConfiguration", [isCurrent: Boolean.FALSE, id: employeeVacationBalance?.id, employee: employeeVacationBalance.employee, vacationConfiguration: employeeVacationBalance?.vacationConfiguration])
    }

    /**
     * calculates the requests count for a vacation type within a time interval
     * @param employee
     * @param vacationType
     * @param fromDate
     * @param toDate
     * @return
     */
    public Integer getEmployeeVacationRequestCount(Employee employee, VacationType vacationType, ZonedDateTime fromDate = null, ZonedDateTime toDate = null) {
        List<Integer> result = VacationRequest.createCriteria().list {
            projections {
                count()
            }

            eq("employee", employee)
            eq("vacationType", vacationType)
            eq("trackingInfo.status", GeneralStatus.ACTIVE)
            eq("requestStatus", EnumRequestStatus.APPROVED)

            if (fromDate) {
                ge("fromDate", fromDate)
            }
            if (toDate) {
                le("toDate", toDate)
            }
        }
        return (result && result.size() > 0) ? result[0] : 0
    }

}