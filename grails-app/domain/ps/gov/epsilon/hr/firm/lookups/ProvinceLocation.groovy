package ps.gov.epsilon.hr.firm.lookups

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

class ProvinceLocation {

    String encodedId

    /** From Core *****/
    Long locationId

    static belongsTo = [province: Province]

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    static constraints = {
        trackingInfo nullable: true
    }

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
        id generator: 'ps.police.postgresql.PCPSequenceGenerator', type: Long, params: [prefer_sequence_per_entity: true]
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    @Override
    public String toString(){
        return transientData?.locationDTO?.toString();
    }
}