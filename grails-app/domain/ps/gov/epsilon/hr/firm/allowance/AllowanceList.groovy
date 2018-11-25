package ps.gov.epsilon.hr.firm.allowance

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/***
 * Created automatically after add child request approved it will be added to last open list
 * in case the list not opened the system will open new list
 */

/**
 * <h1>Purpose</h1>
 * To hold the allowance list that contains many records of allowance list employee.
 * <h1>Usage</h1>
 * Used as to represents the allowance list that contains many records of allowance list employee. And it consider as correspondence list from the firm to Saraya.
 * **/

class AllowanceList extends CorrespondenceList {

    String encodedId

    def sharedService

    Map transientData = [:]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']


    static hasMany = [allowanceListEmployee: AllowanceListEmployee]

    def beforeInsert() {


        if (!this.code) {
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.allowance.AllowanceList", 'ALLONLIST', 20)
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