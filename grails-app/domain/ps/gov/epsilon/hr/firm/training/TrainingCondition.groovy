package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the conditions that should be met to held a specific training course
 * <h1>Usage</h1>
 * Used  as to represents the conditions that should be met to held a specific training course like "circular table"
 *<h1>Example</h1>
 * Like circular table to held a specific training course
 * **/

public class TrainingCondition  {

    String id

    //the name of the condition
    String conditionName;

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [
            trainingCourse: TrainingCourse]

    static constraints = {
        conditionName(Constants.NAME)
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        conditionName type: 'text'
    }

    transient springSecurityService

    static transients = ['springSecurityService']

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
}//end TrainingCondition