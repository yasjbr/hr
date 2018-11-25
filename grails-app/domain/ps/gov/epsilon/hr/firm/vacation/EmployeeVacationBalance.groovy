package ps.gov.epsilon.hr.firm.vacation

import grails.util.Holders
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the employee vacation balance
 * <h1>Usage</h1>
 * Used  as to represents all employees vacation balances
 * **/


class EmployeeVacationBalance {

    String id

    String encodedId

    ZonedDateTime validFromDate
    ZonedDateTime validToDate
    //the employee vacations total balance
    Short balance = 0
    //the employee transferred vacations balance in the last year
    Short oldTransferBalance = 0
    // the annual vacation balance
    Short annualBalance = 0
    //the specified year
    Short vacationDueYear
    //the number of times an employee requested the vacation type
    Short numberOfTimesUsed = 0

    Map transientData=[:]

    //determines the employee vacation balance based on the employee military rank
    VacationConfiguration vacationConfiguration
    TrackingInfo trackingInfo

    Boolean isCurrent=Boolean.FALSE

    static embedded = ['trackingInfo']

    static belongsTo = [employee: Employee]

    static nullableValues = ['validToDate']

    static constraints = {
        isCurrent(nullable: true)
        balance(Constants.POSITIVE_SHORT)
        oldTransferBalance(Constants.POSITIVE_SHORT)
        annualBalance(Constants.POSITIVE_SHORT)
        vacationDueYear(Constants.POSITIVE_SHORT)
        numberOfTimesUsed(Constants.POSITIVE_SHORT)
        vacationConfiguration nullable: false
        trackingInfo nullable: true, display: false
    }


    static mapping = {// todo : balanceValidFromDate should be added to the composite key ?
        employee unique: ["vacationConfiguration", "vacationDueYear"]
        validFromDate type: PersistentDocumentaryDate, {
            column name: 'valid_from_date_datetime'
            column name: 'valid_from_date_date_tz'
        }
        validToDate type: PersistentDocumentaryDate, {
            column name: 'valid_to_date_datetime'
            column name: 'valid_to_date_date_tz'
        }
    }

    transient springSecurityService
    transient employeeVacationBalanceService

    static transients = ['employeeVacationBalanceService','springSecurityService', 'encodedId','transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"

        //set the inserted record to be the current balance for employee, and remove any old balance to be not the current balance
        isCurrent=Boolean.TRUE
        EmployeeVacationBalance.withNewSession { session ->
            employeeVacationBalanceService.setCurrentEmployeeVacationBalance(this)
        }
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }



    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
