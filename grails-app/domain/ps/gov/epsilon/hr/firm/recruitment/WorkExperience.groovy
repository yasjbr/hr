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
 *
 * **/

class WorkExperience {

    static auditable = true

    String id

    //the specified profession type
    Long professionType
    //the set of competencies required
    Long competency

    Map transientData=[:]
    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        professionType(Constants.POSITIVE_LONG_NULLABLE+[unique: 'competency'])
        competency(Constants.POSITIVE_LONG_NULLABLE +[validator: { value, object, errors ->
            if (!value && !object.professionType) {
                errors.reject('RequisitionWorkExperience.competency.error')
            }
            return true
        }])
        trackingInfo nullable: true
    }

    transient springSecurityService

    static transients = ['springSecurityService','transientData']

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