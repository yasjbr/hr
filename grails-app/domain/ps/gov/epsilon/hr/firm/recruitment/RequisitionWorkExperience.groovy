package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the work experience in the job requisition
 * <h1>Usage</h1>
 * Used  as to define the work experience needed in job requisition
 * **/

class RequisitionWorkExperience {

    WorkExperience workExperience

    String id

    String otherSpecifications
    //the period of work experience in years
    Short periodInYears

    Vacancy vacancy

    JobRequisition jobRequisition

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']


    static constraints = {
        workExperience(nullable: true)
        otherSpecifications(Constants.DESCRIPTION_NULLABLE +[widget: "textarea"]+[validator: { value, object, errors ->
            if (!value && !object.workExperience) {
                errors.reject('RequisitionWorkExperience.otherSpecifications.error')
            }
            return true
        }])
        periodInYears(Constants.POSITIVE_SHORT)
        trackingInfo nullable: true
        jobRequisition nullable: true
        vacancy nullable: true
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

