package ps.gov.epsilon.hr.firm.profile

import grails.util.Holders
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate
import java.time.Period
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 *  To hold the Employees status history
 * <h1>Usage</h1>
 * Used as to represents the employees status history
 * **/

//if the employee does not has any record in this table this mean this employee is committed and in his work (على راس عمله)
//This table accept many active statuses for the same employee at the same time
//We need to prevent the direct change or update on the status, we need to add this process
//in specific serves

class EmployeeStatusHistory {

    static auditable = true

    String id

    String encodedId

    EmployeeStatus employeeStatus

    ZonedDateTime fromDate

    //To Date nullable: true
    ZonedDateTime toDate

    static belongsTo = [employee: Employee]

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static constraints = {
        employeeStatus nullable: false, widget: "autocomplete"
        trackingInfo nullable: true, display: false
    }

    static nullableValues = ['toDate']

    transient springSecurityService
    transient employeeStatusHistoryService

    static transients = ['employeeStatusHistoryService', 'springSecurityService', 'encodedId', 'transientData']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
    }

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

        //this method used to update the employee category/date and to update old status histories (if the new status cat is different than current)
        employeeStatusHistoryService?.reflectCategoryStatusIntoEmployeeProfile(this)
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    String getStatusPeriod() {
        Period period = Period.between(fromDate?.toLocalDate(), toDate ? toDate.toLocalDate() : ZonedDateTime.now().toLocalDate());
//        String periodStr=""
        return period.toString()
//        if(period.years>0){
//            return (period.months+(period.years*12.0))
//        }else if(period.months>0){
//            return (period.months)
//        }else{
//            return (period.days)
//        }
    }


    @Override
    public String toString() {
        return employeeStatus?.toString() + " (${fromDate?.format(PCPUtils.ZONED_DATE_FORMATTER)}) "
    }
}
