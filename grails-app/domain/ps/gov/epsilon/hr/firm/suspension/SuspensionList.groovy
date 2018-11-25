package ps.gov.epsilon.hr.firm.suspension

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the suspension list that contains many records of suspension list employee.
 * <h1>Usage</h1>
 * Used as to represents the suspension list that contains many records of suspension list employee. And it consider as correspondence list from the firm to Saraya.
 * **/

// الاستيداع
class SuspensionList extends CorrespondenceList {
    def sharedService


    static hasMany = [suspensionListEmployees: SuspensionListEmployee]


    static constraints = {
    }


    def beforeInsert() {

        /**
         * auto generate vacation list code
         */
        if (!this.code) {
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.suspension.SuspensionList", "SUSPENSION", 20)
        }
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName)
            applicationName = "BootStrap"
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

}
