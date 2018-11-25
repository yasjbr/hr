package ps.gov.epsilon.hr.firm.request

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the requests of a security coordination for external vacation
 * <h1>Usage</h1>
 * Used  as to represents how to request a security coordination for an employee who requests an external vacation
 * <h1>Example</h1>
 * for an employee who took an external vacation, he/she request a security coordination to be able to travel.
 * **/

class BordersSecurityCoordination extends Request {

    ZonedDateTime fromDate
    ZonedDateTime toDate

    // Identifier Type mainly should Be  a Passport used to travel
    Long legalIdentifierId

    // Address where the employee is going to Exit from (Alinbi ,.etc )
    // suggestion create table in the core new domain represent the entry points(Alinbi ,.etc )
    Long borderLocationId;
    String unstructuredLocation;

    //need to distinguish between the permanent, and temporary  addresses and the addresses that we used in the vacation in the core depends on
    // the contact info type
    static hasMany = [contactInfos: Long]

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static constraints = {
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE + [widget: "textarea", blank: false])
        legalIdentifierId(Constants.POSITIVE_LONG)
        borderLocationId(Constants.POSITIVE_LONG)
        trackingInfo nullable: true, display: false

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', "transientData"]

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
}
