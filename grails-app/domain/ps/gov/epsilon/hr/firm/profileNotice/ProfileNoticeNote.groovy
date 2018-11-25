package ps.gov.epsilon.hr.firm.profileNotice

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the notes for notice
 * <h1>Usage</h1>
 * Used  as to add notes for profile notice
 */
class ProfileNoticeNote {

    String id

    String note
    ZonedDateTime noteDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [profileNotice: ProfileNotice]

    static constraints = {
        note (Constants.DESCRIPTION)
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        noteDate type: PersistentDocumentaryDate, {
            column name: 'note_date_datetime'
            column name: 'note_date_date_tz'
        }
        note type: 'text'
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
}
