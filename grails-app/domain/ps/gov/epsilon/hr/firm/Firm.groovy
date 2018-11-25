package ps.gov.epsilon.hr.firm

import grails.util.Holders
import ps.gov.epsilon.hr.firm.lookups.JoinedProvinceFirm
import ps.gov.epsilon.hr.firm.settings.FirmActiveModule
import ps.gov.epsilon.hr.firm.settings.FirmSetting
import ps.gov.epsilon.hr.firm.settings.FirmSupportContactInfo
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the firm information
 * <h1>Usage</h1>
 * Used  as to represent the firm information
 * <h1>Example</h1>
 *  DCO, NSF, CD, PCP, AOC, PG.
 * **/

class Firm {

    static auditable = true

    String encodedId

    //Prefix for id generation must be english abbreviation
    String code
    //To override the core name from HR side
    String name
    /*
   Refer to the organization in the core
     */
    Long coreOrganizationId

    FirmSupportContactInfo supportContactInfo
    String note

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static hasMany = [firmSettings: FirmSetting, firmActiveModules: FirmActiveModule, departments: Department, provinceFirms: JoinedProvinceFirm]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    static constraints = {
        supportContactInfo nullable: true, widget: "autocomplete"
        coreOrganizationId(Constants.POSITIVE_LONG)
        code(Constants.STRING)
        name(Constants.NAME)
        note(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        trackingInfo nullable: true, display: false
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

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator', type: Long, params: [prefer_sequence_per_entity: true]
    }

}
