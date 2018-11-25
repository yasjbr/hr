package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankType
import ps.gov.epsilon.hr.firm.lookups.OperationalTask
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime;

/**
 * <h1>Purpose</h1>
 * To hold the target group of a specific training course
 * <h1>Usage</h1>
 * This class represents the target group of a specific training course.
 * **/

public class TargetGroup {

    String id

    //the minimum age of the target group
    Integer fromAge;
    //the maximum age of the target group
    Integer toAge;

    // can put any note or restriction that related to weight or tall
    String note

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [trainingCourse: TrainingCourse]

    static hasMany = [
            operationalTasks : OperationalTask,
            languages        : Long,
            educationMajors  : Long,
            jobTitles        : JobTitle,
            educationLevels  : Long,
            militaryRanks    : MilitaryRank,
            competencies     : Long,
            militaryRankTypes: MilitaryRankType
    ]

    static constraints = {

        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        fromAge(nullable: false)
        toAge(nullable: false, validator: { val, obj ->
            return val > obj.fromAge
        })
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
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