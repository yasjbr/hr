package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the applicant in the Applicant Inspection Result list employee, so the Applicant Inspection Result list employee consider as holder of applicant
 * <h1>Usage</h1>
 * Used  as to represents the Applicant Inspection Result list employee that holds the applicant
 * **/

class ApplicantInspectionResultListEmployee {

    String encodedId

    String id

    EnumListRecordStatus recordStatus

    Applicant applicant

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [ApplicantInspectionResultList: ApplicantInspectionResultList]

    static hasMany = [applicantInspectionResultListEmployeeNotes: ApplicantInspectionResultListEmployeeNote]

    static constraints = {
        recordStatus nullable: false
        applicant nullable: false, widget: "autocomplete"
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', "encodedId"]

    def beforeInsert() {
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
