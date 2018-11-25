package ps.gov.epsilon.hr.firm.training.execution

import grails.util.Holders
import ps.gov.epsilon.hr.firm.training.Trainer
import ps.gov.epsilon.hr.firm.training.TrainingCourse
import ps.gov.epsilon.hr.firm.training.attendance.CourseExecutionSessionSchedule
import ps.gov.epsilon.hr.firm.training.lookup.ExecutingParty
import ps.gov.epsilon.hr.firm.training.lookup.FundProvider
import ps.gov.epsilon.hr.firm.training.plan.AnnualTrainingPlan
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the period that allowed to execute the course training related to annual training plan
 * <h1>Usage</h1>
 * Used  as to represents the period of the course execution
 * **/

public class CourseExecution {

    String id

    ZonedDateTime fromDate
    ZonedDateTime toDate

    Long locationId
    //general description about the location
    String unstructuredLocation

    FundProvider fundProvider;
    ExecutingParty executingParty;

    // indicate if the training will be internal in the same firm or external
    Boolean isInternal

    /**
     * if the late less than maxLateMinutes it consider as late
     * after this it will consider as absent
     */
    Short maxLateMinutes

    Short maxNumberOfAttendees

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static hasMany = [trainers: Trainer, courseExecutionSessionSchedule: CourseExecutionSessionSchedule]

    static belongsTo = [trainingCourse: TrainingCourse, annualTrainingPlan: AnnualTrainingPlan]

    static constraints = {
        fundProvider nullable: true,widget:"autocomplete"
        executingParty nullable: true,widget:"autocomplete"
        maxLateMinutes(Constants.POSITIVE_SHORT_NULLABLE)
        maxNumberOfAttendees(Constants.POSITIVE_SHORT_NULLABLE)
        locationId(Constants.POSITIVE_LONG)
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE + [widget: "textarea",blank: false] )
        isInternal nullable: false
        trackingInfo nullable: true,display:false
    }

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