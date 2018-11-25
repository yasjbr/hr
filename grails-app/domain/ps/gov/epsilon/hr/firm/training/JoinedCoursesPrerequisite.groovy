package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the Training Course and the prerequisite Training Course many-to-many relation
 * <h1>Usage</h1>
 * Used  as to represents the prerequisite courses that an employee had attended to attend a specific course.
 *<h1>Example</h1>
 * For an employee to attend a training course X, he/she must have attended training courses [Y and Z]. [Y and Z] courses present the prerequisite courses of the course X.
 * **/

public class JoinedCoursesPrerequisite {

    String id

    TrainingCourse prerequisiteCourse
    TrainingCourse trainingCourse

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        trainingCourse unique: 'prerequisiteCourse',widget:"autocomplete"
        prerequisiteCourse nullable: true,widget:"autocomplete"
        trackingInfo nullable: true,display:false
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