package ps.gov.epsilon.aoc.correspondences

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * Note related to the Aoc Record,
 * <h1>Usage</h1>
 * Mainly used when record status is changed manually in workflow
 * **/

class AocListRecordNote {

    String encodedId

    /**
     * Note entered by user
     */
    String note

    String orderNo
    ZonedDateTime noteDate

    /**
     * Record status when this note is captured
     */
    EnumListRecordStatus recordStatus

    static belongsTo = [listRecord:AocListRecord]

    TrackingInfo trackingInfo
    static embedded = ['trackingInfo']

    static constraints = {
        recordStatus nullable: true
        trackingInfo nullable: true,display:false
        orderNo nullable: true
        note Constants.DESCRIPTION_NULLABLE
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)applicationName = "BootStrap"

        trackingInfo = new TrackingInfo()
        if(springSecurityService?.isLoggedIn()){
            if (!trackingInfo.createdBy)
                trackingInfo.createdBy = springSecurityService?.principal?.username?:applicationName
            if (!trackingInfo.lastUpdatedBy)
                trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        }
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
        if(!applicationName)applicationName = "BootStrap";
        if(springSecurityService?.isLoggedIn()) {
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        }
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

    static mapping = {
        noteDate type: PersistentDocumentaryDate, {
            column name: 'note_date_datetime'
            column name: 'note_date_date_tz'
        }
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }
}
