package ps.gov.epsilon.hr.firm.general

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 *  To hold the Employees who are added to a specific general list.
 * <h1>Usage</h1>
 * Used as to represents the employees who are added to a specific general list
 * **/

class GeneralListEmployee {

    String encodedId

    String id

    EnumListRecordStatus recordStatus

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    /**
     * Taken from employee : currentEmployeeMilitaryRank
     */
    EmployeePromotion employeeMilitaryRank

    static belongsTo = [generalList:GeneralList, employee: Employee]

    static hasMany = [generalListEmployeeNotes: GeneralListEmployeeNote]

    static constraints = {
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
