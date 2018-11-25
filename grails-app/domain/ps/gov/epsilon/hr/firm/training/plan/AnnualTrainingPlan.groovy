package ps.gov.epsilon.hr.firm.training.plan

import grails.util.Holders
import ps.gov.epsilon.hr.enums.training.v1.EnumAnnualTrainingPlanStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the annual training plan of all department in the firm
 * <h1>Usage</h1>
 * Used  as to represent the annual training plan of departments in the firm
 * **/

public class AnnualTrainingPlan  {

    String id
    String code
    String name
    short year
    String description;

    ZonedDateTime suggestedStartDate;
    ZonedDateTime suggestedEndDate;

    EnumAnnualTrainingPlanStatus annualTrainingPlanStatus;

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [firm: Firm]

    static hasMany = [participatingDepartments:PlanParticipatingDepartment,departmentsRegisteredCourses:DepartmentRegisteredCourse]

    public AnnualTrainingPlan() {
        annualTrainingPlanStatus = EnumAnnualTrainingPlanStatus.UNDER_DEVELOPMENT;
    }

    static constraints = {
        code(Constants.STRING + [unique: true])
        name(Constants.NAME)
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        year(nullable: false, validator: {val, obj ->
            return  val >= new Date().year + 1900
        })
        annualTrainingPlanStatus nullable: false
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        description type: 'text'
        suggestedStartDate type: PersistentDocumentaryDate, {
            column name: 'suggested_start_date_datetime'
            column name: 'suggested_start_date_date_tz'
        }
        suggestedEndDate type: PersistentDocumentaryDate, {
            column name: 'suggested_end_date_datetime'
            column name: 'suggested_end_date_date_tz'
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