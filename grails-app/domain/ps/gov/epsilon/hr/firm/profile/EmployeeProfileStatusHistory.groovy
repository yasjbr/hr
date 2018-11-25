package ps.gov.epsilon.hr.firm.profile

import grails.util.Holders
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.Period
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 *  To hold the Employees profile status history
 * <h1>Usage</h1>
 * Used as to represents the employees profile status change history
 * **/

class EmployeeProfileStatusHistory {

    static auditable = true

    String id

    String encodedId

    EnumProfileStatus employeeProfileStatus

    ZonedDateTime fromDate

    //To Date nullable: true
    ZonedDateTime toDate

    String note

    static belongsTo = [employee: Employee]

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static constraints = {
        note (Constants.DESCRIPTION_NULLABLE)
        employeeProfileStatus nullable: false, widget: "autocomplete"
        trackingInfo nullable: true, display: false
    }

    static nullableValues = ['toDate']

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData', 'statusPeriod']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
        note type: 'text'
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
        return period.toString()
    }

    @Override
    public String toString() {
        return employeeProfileStatus?.toString() + " (${fromDate?.format(PCPUtils.ZONED_DATE_FORMATTER)}) "
    }
}
