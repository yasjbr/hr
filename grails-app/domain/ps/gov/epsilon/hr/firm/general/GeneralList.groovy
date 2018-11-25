package ps.gov.epsilon.hr.firm.general

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold general correspondence list between the firm and any external party
 * <h1>Usage</h1>
 * Used  as to represent the correspondence list between the firm and any external party
 *<h1>Example</h1>
 *correspondence between PCP and Jawwal, correspondence between DCO and Mukhabarat, ...etc.
 *  * **/

class GeneralList extends CorrespondenceList{

    /**
     * Remoting value from core organization
     */
    Long coreOrganizationId

    static hasMany = [generalListEmployees: GeneralListEmployee]

    transient sharedService

    static transients = ['sharedService']


    static constraints = {
    }

    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.general.GeneralList", "General", 20)
        }
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName)
            applicationName = "BootStrap"
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
        if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}
