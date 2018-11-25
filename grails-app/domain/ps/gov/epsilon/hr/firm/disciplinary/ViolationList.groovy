package ps.gov.epsilon.hr.firm.disciplinary

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/***
 * Created manually, but the system suggest the employees with violation record depends on
 * included in the eligible list
 */

/**
 *<h1>Purpose</h1>
 * To hold the violation lists in the system, and this list contains many record of violation list employee. It consider as correspondence list between the firm and party outside the firm
 * <h1>Usage</h1>
 * Used  as to represents the violation list that contains many records of violation list employee.
 * **/

class ViolationList extends CorrespondenceList{

    def sharedService

    static hasMany = [violationListEmployees: ViolationListEmployee]

    transient springSecurityService

    static transients = ['springSecurityService']

    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService.generateListCode("ps.gov.epsilon.hr.firm.disciplinary.ViolationList", "VL", 20)
        }
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)
            applicationName = "BootStrap"
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

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)
            applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}