package ps.gov.epsilon.hr.firm.training.plan

import grails.util.Holders
import ps.gov.epsilon.hr.enums.training.v1.EnumParticipatingDepartmentStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime;

/**
 *<h1>Purpose</h1>
 * To hold the departments that included in the annual plan in the firm
 * <h1>Usage</h1>
 * Used  as to represent the department that included in the annual plan in the firm
 * **/

public class PlanParticipatingDepartment {

    String id

    EnumParticipatingDepartmentStatus participatingStatus

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [firm: Firm, annualTrainingPlan: AnnualTrainingPlan, department:Department]

    static constraints = {
        department unique: 'annualTrainingPlan',nullable: false
        participatingStatus nullable: false
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