package ps.gov.epsilon.aoc.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

class Committee {

    String encodedId

    DescriptionInfo descriptionInfo

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo', 'descriptionInfo']

    static constraints = {
        //TODO unique constraints without firm
//        importFrom DescriptionInfoUniqueConstrains
        descriptionInfo widget: "DescriptionInfo"
        trackingInfo nullable: true, display: false
    }

    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'name']

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

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    public String getName(){
        return descriptionInfo?.localName
    }
}
