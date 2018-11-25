package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the trainer
 * <h1>Usage</h1>
 * This class represents all trainers
 * **/

public class Trainer {

    String id

    Long personId;
    // put as long to avoid direct join
    String employeeId;

    String note

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        employeeId(Constants.FIRM_PK_NULLABLE)
        personId(Constants.POSITIVE_LONG)
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    Map transientData = [:]

    static transients = ['springSecurityService','transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username?:applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"
    }

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}