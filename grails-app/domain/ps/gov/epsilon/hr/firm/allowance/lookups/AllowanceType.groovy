package ps.gov.epsilon.hr.firm.allowance.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the allowances types
 * <h1>Usage</h1>
 * Used as to represents the allowances types related to allowances requests
 * <h1>Example</h1>
 * "TRAVEL_ALLOWANCE",...etc.
 * **/

class AllowanceType {

    String id

    String encodedId

    DescriptionInfo descriptionInfo
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    //From core RelationshipType
    Long relationshipTypeId

    TrackingInfo trackingInfo


    Map transientData = [:]

    static belongsTo = [firm: Firm]

    static hasMany = [joinedAllowanceTypeStopReasons: JoinedAllowanceTypeStopReason]

    static embedded = ['trackingInfo', 'descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        relationshipTypeId nullable: true
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget: "DescriptionInfo"
        trackingInfo nullable: true, display: false
        id bindable: true
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
}
