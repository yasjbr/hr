package ps.gov.epsilon.hr.firm.disciplinary

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the Employees who are added to a specific violation list.
 * <h1>Usage</h1>
 * Used as to represents the employees who are added to a specific violation list related to violation
 * **/

class ViolationListEmployee {

    String encodedId

    String id

//    String statusReason

    // removed no need for status, we assume that SARAYA replay by the disciplinary list for the related employee depends on the violation
    // EnumListRecordStatus recordStatus

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [employeeViolation: EmployeeViolation, violationList: ViolationList]

    static hasMany = [violationListEmployeeNotes:ViolationListEmployeeNote]

    static constraints = {
//        statusReason(nullable: true, blank: true, size: 0..250)
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

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

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
