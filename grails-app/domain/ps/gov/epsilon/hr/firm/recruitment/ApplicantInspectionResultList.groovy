package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.lookups.Inspection
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Applicant Inspection Result list that contains many records of recruitment list employee.
 * It consider as correspondence list and have a reference to distinguish between different Applicant Inspection Result list.
 * <h1>Usage</h1>
 * Used  as to represents the Applicant Inspection Result list which contains many records of Applicant Inspection Result list employee
 * **/

class ApplicantInspectionResultList extends CorrespondenceList {

    def sharedService

    String id

    String encodedId
    Map transientData = [:]

    /**
     * Added to the domain since business might not mandate inspection
     */
    InspectionCategory inspectionCategory
    /**
     * Mandatory because result should be filled per inspection, it should not affect the category directly
     * Discussed with Yasin
     */
    Inspection inspection

    /**
     * Remoting value from core organization
     */
    Long coreOrganizationId

    static hasMany = [applicantInspectionResultListEmployees: ApplicantInspectionResultListEmployee]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    static constraints = {
        inspection nullable: false, blank: false
        code nullable: true
    }

    def beforeInsert() {

        if (!this.code) {
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.recruitment.ApplicantInspectionResultList", 'APPLINSP', 20)
        }

        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }
}
