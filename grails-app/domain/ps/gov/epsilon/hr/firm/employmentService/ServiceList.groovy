package ps.gov.epsilon.hr.firm.employmentService

import grails.util.Holders
import ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the service list
 * <h1>Usage</h1>
 * Used  as to represents the way of ending the employee service or return him/her to the service
 *<h1>Example</h1>
 * END OF SERVICE, RETURN TO SERVICE
 * **/

class ServiceList extends CorrespondenceList{

    def sharedService

    EnumServiceListType serviceListType

    static constraints = {
        serviceListType nullable: false
    }

    static hasMany = [serviceListEmployees: ServiceListEmployee]

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['serviceListEmployees','correspondenceListStatuses','currentStatus']


    def beforeInsert() {

        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.employmentService.ServiceList", "SERVICELIST", 20)
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
