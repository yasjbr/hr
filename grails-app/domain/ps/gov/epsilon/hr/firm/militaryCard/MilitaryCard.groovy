package ps.gov.epsilon.hr.firm.militaryCard

import grails.util.Holders
import ps.gov.epsilon.hr.enums.militaryCard.v1.EnumMilitaryCardStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.settings.FirmSetting
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the employee military card that the employee granted due to lost,replacement or new employee
 * <h1>Usage</h1>
 * Used  as to represents all employee military card
 * **/

class MilitaryCard {

    String id

    String cardCode
    Boolean receivedOldCard
    EnumMilitaryCardStatus militaryCardStatus
    //the date of issue the military card
    ZonedDateTime issuingDate
    //the military card validity date
    ZonedDateTime validUntil
     // to keep history of the official Signature on the card
    FirmSetting cardSignature

    // toDO the picture on the card should be an attachment on the request with type  personal image
    TrackingInfo trackingInfo

    static embedded  = ['trackingInfo']

    static belongsTo = [militaryCardRequest:MilitaryCardRequest,firm:Firm]

    static nullableValues = ['validUntil']

    static constraints = {
        cardCode(Constants.STRING)
        receivedOldCard nullable: true
        militaryCardStatus nullable: false
        cardSignature nullable: false,widget:"autocomplete"
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        issuingDate type: PersistentDocumentaryDate, {
            column name: 'issuing_date_datetime'
            column name: 'issuing_date_date_tz'
        }
        validUntil type: PersistentDocumentaryDate, {
            column name: 'valid_until_datetime'
            column name: 'valid_until_date_tz'
        }
    }


    transient springSecurityService

    static transients = ['springSecurityService']

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
}
