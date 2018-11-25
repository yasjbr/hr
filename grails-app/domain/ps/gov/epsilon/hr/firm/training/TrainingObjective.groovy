package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the objectives of each training course
 * <h1>Usage</h1>
 * Used  as to represents the objectives of each training course (why the training course is decided to be held)
 * **/

public class TrainingObjective {

    String id

    //the name of the objective
    String objectiveName;

    //each training course has only a main objective and many other objectives (minor objectives)
    Boolean isMainObjective;

    String note

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [
            trainingCourse: TrainingCourse]

    static mapping = {
        objectiveName type: 'text'

    }

    static constraints = {
        objectiveName(Constants.NAME)
        isMainObjective nullable: false
        note(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
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