package ps.gov.epsilon.hr.firm.suspension

import grails.util.Holders
import ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Employees who are added to a specific suspension list.
 * <h1>Usage</h1>
 * Used as to represents the employees who are added to a specific suspension list due to many reasons like medical suspension, requested suspension and others.
 * **/

//تمديد الاستيداع
class SuspensionExtensionListEmployee {

    String id

    String encodedId

    EnumListRecordStatus recordStatus

    /***
     * To keep history of the Employment Record  and military rank when this Disciplinary has been taken
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2)Entered from the Disciplinary module it will take the current employment Record of the employee
     *
     */
    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank


    Short periodInMonth
    // nullable true
    ZonedDateTime effectiveDate

    ZonedDateTime fromDate
    ZonedDateTime toDate


    TrackingInfo trackingInfo


    static embedded = ['trackingInfo']

    static belongsTo = [suspensionExtensionRequest: SuspensionExtensionRequest, suspensionExtensionList: SuspensionExtensionList]

    static hasMany = [suspensionExtensionListEmployeeNotes: SuspensionExtensionListEmployeeNote]

    static nullableValues = ['effectiveDate']

    static constraints = {
        periodInMonth(Constants.POSITIVE_SHORT)
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        recordStatus nullable: false
        trackingInfo nullable: true, display: false
    }

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

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
        if (recordStatus == null)
            recordStatus = EnumListRecordStatus.NEW
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
}
