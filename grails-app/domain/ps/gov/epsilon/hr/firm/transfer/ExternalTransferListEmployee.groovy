package ps.gov.epsilon.hr.firm.transfer

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Employees who are added to a specific external transfer list.
 * <h1>Usage</h1>
 * Used as to represents the employees who are added to a specific external transfer list. This record created from approved ExternalTransferRequest, or exceptional cases
 * **/

class ExternalTransferListEmployee {

    String id

    String encodedId

    //this record created from approved ExternalTransferRequest, or exceptional cases

    //When we received the approval :
    //طباعة خلو طرف, وامر تسيير
    EnumListRecordStatus recordStatus

    EmploymentRecord currentEmploymentRecord

    Long toOrganizationId

    //تاريخ النقل
    ZonedDateTime effectiveDate

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static nullableValues = ['effectiveDate']

    static belongsTo = [employee: Employee, externalTransferList: ExternalTransferList, externalTransferRequest: ExternalTransferRequest]

    static hasMany = [externalTransferListEmployeeNotes: ExternalTransferListEmployeeNote]

    static constraints = {

        externalTransferRequest nullable: true
        recordStatus nullable: false
        currentEmploymentRecord nullable: false
        toOrganizationId(Constants.POSITIVE_LONG)
        trackingInfo nullable: true, display: false
    }

    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
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
