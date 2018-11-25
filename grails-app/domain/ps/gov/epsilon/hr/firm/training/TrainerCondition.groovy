package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the conditions that a trainer must have to join a specific training course
 * <h1>Usage</h1>
 * Used  as to represents the conditions and the skills that a trainer must have to train a specific training course
 *<h1>Example</h1>
 * fitness,...etc.
 * **/

public class TrainerCondition  {

    String id

    String note

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [trainingCourse: TrainingCourse]

    static hasMany = [competencies: Long,majors:Long,educationLevels:Long,languages:Long]

    static constraints = {
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
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