package ps.gov.epsilon.hr.firm.profileNotice

import grails.util.Holders
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profileNotice.lookups.ProfileNoticeCategory
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the employee notice information
 * <h1>Usage</h1>
 * Used  as to represents the information of employee profile notice, affects employee profile status
 */
class ProfileNotice {

    static auditable = true

    String id

    String encodedId

    Map transientData = [:]

    /* source organization that presented the notice */
    Long sourceOrganizationId

    EnumProfileNoticeStatus profileNoticeStatus

    /* Includes category and reason **/
    ProfileNoticeCategory profileNoticeCategory

    /** free text reason */
    String profileNoticeReason

    /** name of person who presented the notice **/
    String presentedBy

    /** name of the notice **/
    String name

    String noticeText

    ZonedDateTime noticeDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [employee: Employee, firm: Firm]

    static hasMany = [profileNoticeNotes:ProfileNoticeNote]

    static constraints = {
        sourceOrganizationId(Constants.POSITIVE_LONG_NULLABLE)
        profileNoticeCategory widget: "autocomplete"
        profileNoticeReason(Constants.LOOKUP_NAME_NULLABLE)
        profileNoticeStatus nullable: false
        presentedBy(Constants.LOOKUP_NAME_NULLABLE)
        name(Constants.LOOKUP_NAME_NULLABLE)
        noticeText(Constants.DESCRIPTION)
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

    static mapping = {
        noticeDate type: PersistentDocumentaryDate, {
            column name: 'note_date_datetime'
            column name: 'note_date_date_tz'
        }
        noticeText type: 'text'
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }
}
