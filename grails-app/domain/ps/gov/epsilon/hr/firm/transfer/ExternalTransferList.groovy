package ps.gov.epsilon.hr.firm.transfer

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the external transfer list that contains many records of external transfer list employee.
 * <h1>Usage</h1>
 * Used as to represents the external transfer list that contains many records of external transfer list employee. And it consider as correspondence list from the firm to Saraya.
 * **/

class ExternalTransferList extends CorrespondenceList{


    String encodedId

    def sharedService

    Map transientData = [:]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['externalTransferListEmployees', 'correspondenceListStatuses','currentStatus']


    static hasMany = [externalTransferListEmployees:ExternalTransferListEmployee]
    static constraints = {
    }

    def beforeInsert() {


        if (!this.code) {
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.transfer.ExternalTransferList", 'EXTRNLIST', 20)
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
