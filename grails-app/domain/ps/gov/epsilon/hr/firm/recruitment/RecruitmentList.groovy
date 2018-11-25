package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the recruitment list that contains many records of recruitment list employee.
 * It consider as correspondence list and have a reference to distinguish between different recruitment list.
 * <h1>Usage</h1>
 * Used  as to represents the recruitment list which contains many records of recruitment list employee
 * **/

class RecruitmentList extends CorrespondenceList {

    def sharedService

    String id

    String encodedId
    Map transientData = [:]

    static hasMany = [recruitmentListEmployees: RecruitmentListEmployee]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    static constraints = {
        code nullable: true
    }

    def beforeInsert() {

        if (!this.code) {
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.recruitment.RecruitmentList", 'RECLIST', 20)
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
