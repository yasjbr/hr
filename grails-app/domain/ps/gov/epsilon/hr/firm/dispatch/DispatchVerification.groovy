package ps.gov.epsilon.hr.firm.dispatch

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Dispatch Verification process information that applied on the Dispatch Request.
 * **/

class DispatchVerification {

    static auditable = true

    String id

    String encodedId

    String note

    Map transientData = [:]

    ZonedDateTime plannedVerificationDate

    ZonedDateTime actualVerificationDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static nullableValues = ['actualVerificationDate']

    static belongsTo = [dispatchRequest:DispatchRequest,firm:Firm]

    static constraints = {
        note (Constants.DESCRIPTION_NULLABLE + [widget: "textarea"]+[blank: false])
        trackingInfo nullable: true,display:false
    }

    static mapping = {

        plannedVerificationDate type: PersistentDocumentaryDate, {
            column name: 'planned_verification_date_datetime'
            column name: 'planned_verification_date_date_tz'
        }
        actualVerificationDate type: PersistentDocumentaryDate, {
            column name: 'actual_verification_date_datetime'
            column name: 'actual_verification_date_date_tz'
        }
        note type: "text"
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId','transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username?:applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"
    }

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
