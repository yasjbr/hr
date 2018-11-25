package ps.gov.epsilon.hr.firm.vacation.lookup

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
 * To hold the vacation types
 * <h1>Usage</h1>
 * Used  as to represents all vacation types in the system
 * <h1>Example</h1>
 * Haj, annual leave, sick leave, ..etc.
 * **/

class VacationType {

    String id

    String encodedId

    DescriptionInfo descriptionInfo

    //to distinguish between the types by colors in screen, each vacation type is related to a color
    Long colorId;
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    Map transientData = [:]


    TrackingInfo trackingInfo

    Boolean excludedFromServicePeriod = Boolean.FALSE

    //if request needs further approval from AOC for example, by default false, it can be changed on type creation
    Boolean needsExternalApproval = false


    static belongsTo = [firm: Firm]

    static embedded = ['trackingInfo', 'descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        colorId(Constants.POSITIVE_LONG_NULLABLE)
        universalCode(Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget: "DescriptionInfo"
        trackingInfo nullable: true, display: false
        id bindable: true
        needsExternalApproval nullable: true
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'transientData', 'encodedId']

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
