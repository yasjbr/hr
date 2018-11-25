package ps.gov.epsilon.aoc.firm.employee

import grails.util.Holders
import ps.gov.epsilon.aoc.enums.employee.v1.EnumSalaryClassification
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * holds employee salary info when imported from militarty finance
 */
class EmployeeSalaryInfo {

    String id

    String encodedId

    double salary

    EnumSalaryClassification salaryClassification

    /**
     * From core
     */
    Long salaryCurrencyId

    Long bankId

    Long bankBranchId

    //IBAN
    String internationalAccountNumber

    String bankAccountNumber

    ZonedDateTime salaryDate;

    Boolean active= true

    Map transientData = [:]

    static belongsTo = [employee:Employee, firm:Firm]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        salary(Constants.POSITIVE_DOUBLE)
        bankId(Constants.POSITIVE_LONG)
        bankBranchId(Constants.POSITIVE_LONG)
        salaryCurrencyId(Constants.POSITIVE_LONG)
        internationalAccountNumber(Constants.DOCUMENT_NUMBER)
        bankAccountNumber(Constants.DOCUMENT_NUMBER_NULLABLE)
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

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
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    def afterInsert(){
        // set other salary info for this employee to not active
        EmployeeSalaryInfo previousInfo= this.employee.employeeSalaryInfos?.find{it.active==true && it.id != this.id}
        if(previousInfo){
            previousInfo.active= false
            previousInfo.save()
        }
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    static mapping = {
        salaryDate type: PersistentDocumentaryDate, {
            column name: 'salary_date_datetime'
            column name: 'salary_date_date_tz'
        }
    }
}
