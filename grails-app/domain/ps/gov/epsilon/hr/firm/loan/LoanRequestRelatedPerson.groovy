package ps.gov.epsilon.hr.firm.loan

import grails.util.Holders
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Loan List Person who requested from the firm and the replay from the OOC
 * <h1>Usage</h1>
 * Used as to represents the Loan List Person who requested from the firm and the replay from the OOC
 * **/

class LoanRequestRelatedPerson {

    String id

    String encodedId
    //the identifier of requested person
    Long requestedPersonId

    EnumPersonSource recordSource

    //nullable: true
    ZonedDateTime effectiveDate

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static nullableValues = ['effectiveDate']

    static belongsTo = [firm: Firm, loanRequest: LoanRequest]

    static constraints = {
        requestedPersonId(Constants.POSITIVE_LONG_NULLABLE)
        recordSource nullable: false
        trackingInfo nullable: true, display: false
    }

    static mapping = {
          effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
    }


    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId','transientData']


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
        if (recordSource == null)
            recordSource = EnumPersonSource.REQUESTED
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
