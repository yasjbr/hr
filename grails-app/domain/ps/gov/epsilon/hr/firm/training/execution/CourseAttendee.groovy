package ps.gov.epsilon.hr.firm.training.execution

import grails.util.Holders
import ps.gov.epsilon.hr.firm.training.plan.ProposedTrainee
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the proposed trainee attendance to the training course
 * <h1>Usage</h1>
 * Used  as to represent the attendance of all proposed trainee to the training course
 * **/


public class CourseAttendee {

    String id

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [courseExecutions: CourseExecution, proposedTrainee: ProposedTrainee]

    static constraints = {
        proposedTrainee unique: 'courseExecutions'
        trackingInfo nullable: true,display:false
    }

    static mapping = {

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

}