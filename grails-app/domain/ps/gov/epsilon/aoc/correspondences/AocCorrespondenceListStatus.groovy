package ps.gov.epsilon.aoc.correspondences

import grails.util.Holders
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

class AocCorrespondenceListStatus {

    String encodedId

    EnumCorrespondenceStatus correspondenceStatus

    ZonedDateTime fromDate
    ZonedDateTime toDate

    static belongsTo = [correspondenceList:AocCorrespondenceList]

    TrackingInfo trackingInfo
    static embedded = ['trackingInfo']

    static nullableValues = ['toDate']

    static constraints = {
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }

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
}
