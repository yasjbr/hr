package ps.gov.epsilon.hr.firm.vacation

import grails.converters.JSON
import grails.gorm.PagedResultList
import org.codehaus.groovy.runtime.DateGroovyMethods
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.enums.v1.GeneralStatus

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Period
import java.time.ZonedDateTime

import static org.springframework.http.HttpStatus.*
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils
import guiplugin.FormatService

/**
 * <h1>Purpose</h1>
 * Route EmployeeVacationBalance requests between model and views.
 * @see EmployeeVacationBalanceService
 * @see FormatService
 * */
class EmployeeVacationBalanceController {

    EmployeeVacationBalanceService employeeVacationBalanceService
    FormatService formatService

    static allowedMethods = [save: "POST", update: "POST"]

    /**
     * default action in controller
     */
    def index = {
        redirect action: "list", method: "GET"
    }

    /**
     * represent the list page
     */
    def list = {}

    /**
     * represent the show page with get instance
     */
    def show = {
        if (params.encodedId) {
            //todo: get the firm from params without need to use the session value in case the user is super admin
            params["firm.id"] = session.getAttribute("firmId")
            EmployeeVacationBalance employeeVacationBalance = employeeVacationBalanceService.getInstanceWithRemotingValues(params)
            if (employeeVacationBalance) {
                respond employeeVacationBalance
                return
            }
        } else {
            notFound()
        }
    }


    def create = {
        EmployeeVacationBalance employeeVacationBalance = new EmployeeVacationBalance(params)
        employeeVacationBalance.transientData.currentYear = Short.parseShort(DateGroovyMethods.format(new Date(), 'yyyy'))
        respond employeeVacationBalance
    }

    /**
     * filter data depends on parameter from request and render to data table
     */
    def filter = {
        PagedResultList pagedResultList = employeeVacationBalanceService.searchWithRemotingValues(params)
        render text: (employeeVacationBalanceService.resultListToMap(pagedResultList, params) as JSON), contentType: "application/json"
    }

    /**
     * this action is used to calculate one employee yearly balance
     */
    def calculateEmployeeYearlyBalance = {

        if (params["employee.id"]) {
            Map calculateEmployeeYearlyBalanceMap = employeeVacationBalanceService.calculateEmployeeYearlyBalanceById(params["employee.id"], params.short("year"), params.boolean("recalculate"))
            if (calculateEmployeeYearlyBalanceMap && calculateEmployeeYearlyBalanceMap.get("error")) {
                calculateEmployeeYearlyBalanceMap.success = false
                calculateEmployeeYearlyBalanceMap.error = message(code: calculateEmployeeYearlyBalanceMap.error)
            } else {
                calculateEmployeeYearlyBalanceMap.success = true
            }
            render text: (calculateEmployeeYearlyBalanceMap as JSON), contentType: "application/json"
        } else {
            render text: ([success: false] as JSON), contentType: "application/json"
        }
    }

    /**
     * this action is used to calculate for all employee yearly balance
     */
    def calculateAllEmployeeYearlyBalance = {
        Map calculateEmployeeYearlyBalanceMap = employeeVacationBalanceService.calculateAllEmployeeYearlyBalance(params.short("year"), params.boolean("recalculate"))
        if (calculateEmployeeYearlyBalanceMap && calculateEmployeeYearlyBalanceMap.get("error")) {
            calculateEmployeeYearlyBalanceMap.success = false
            calculateEmployeeYearlyBalanceMap.error = message(code: calculateEmployeeYearlyBalanceMap.error)
        } else {
            calculateEmployeeYearlyBalanceMap.success = true
        }
        render text: (calculateEmployeeYearlyBalanceMap as JSON), contentType: "application/json"

    }

    /**
     * to handle requests if object not found.
     * @return void
     */
    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'employeeVacationBalance.entity', default: 'EmployeeVacationBalance'), params?.id])
                redirect action: "list", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    /**
     * show employee vacation balance
     */
    def showEmployeeBalance = {

    }
}

