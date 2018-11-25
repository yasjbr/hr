package ps.gov.epsilon.hr.firm.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the province information
 * <h1>Usage</h1>
 * Used  as to represent the province information required by AOC
 * <h1>Example</h1>
 *  northern cities, southern cities, outsider arenas, ...
 * **/

class Province {

    String encodedId

    DescriptionInfo descriptionInfo

    String note

    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    static hasMany = [provinceFirms: JoinedProvinceFirm, provinceLocations: ProvinceLocation]

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo', 'descriptionInfo']

    static constraints = {
        descriptionInfo widget: "DescriptionInfo"
        trackingInfo nullable: true, display: false
        note(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        universalCode nullable: false
    }

    static mapping = {
        provinceFirms lazy: true
        id generator: 'ps.police.postgresql.PCPSequenceGenerator', type: Long, params: [prefer_sequence_per_entity: true]
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

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    @Override
    public String toString() {
        return descriptionInfo.toString();
    }
}
