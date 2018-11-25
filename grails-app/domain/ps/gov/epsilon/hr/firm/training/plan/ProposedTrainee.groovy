package ps.gov.epsilon.hr.firm.training.plan

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the list of trainee who proposed by the firm to attend the department course
 * <h1>Usage</h1>
 * Used  as to represent the proposed trainee by the firm to attend the department course
 * **/

//قائمة المرشحين لحضور الدورة من قبل الدائرة
public class ProposedTrainee  {

    String id

    AnnualTrainingPlan annualTrainingPlan

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [
            firm: Firm,
            employee: Employee,
            departmentRegisteredCourse: DepartmentRegisteredCourse
    ]

    static constraints = {
        employee unique: 'departmentRegisteredCourse',widget:"autocomplete"
        annualTrainingPlan nullable: false,widget:"autocomplete"
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