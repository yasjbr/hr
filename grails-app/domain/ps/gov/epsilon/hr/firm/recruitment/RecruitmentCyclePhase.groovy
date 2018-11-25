package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the applicant status history from the time that applicant applying to job
 * <h1>Usage</h1>
 * Used  as to represents the Recruitment Cycle Phase history
 *<h1>Example</h1>
 * NEW, OPEN, CLOSE,...etc.
 * **/

class RecruitmentCyclePhase {

    static auditable = true

    String id

    String encodedId

    EnumRequisitionAnnouncementStatus requisitionAnnouncementStatus

    ZonedDateTime fromDate
    ZonedDateTime toDate

    static belongsTo = [recruitmentCycle:RecruitmentCycle]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']
    static nullableValues = ['toDate']

    static constraints = {
        requisitionAnnouncementStatus nullable: false
        trackingInfo nullable: true,display:false
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
    }

    transient springSecurityService

    static transients = ['springSecurityService','encodedId']

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
