package ps.gov.epsilon.hr.firm.maritalStatus

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the marital status lists,which employees with marital status request approved so it will be added to last open list or create new open list
 * and it consider as correspondence between the firm and party outside the firm
 * <h1>Usage</h1>
 * Used  as to represent the list of marital status related to marital status list employee
 *<h1>Example</h1>
 * Marital status like SINGLE,MARRIED,DIVORCED,WIDOWED.
 *
 * After approval update employee relations reference record in pcore (on close list).
 */

class MaritalStatusList extends CorrespondenceList {
    def sharedService

    static hasMany = [maritalStatusListEmployees: MaritalStatusListEmployee]

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['maritalStatusListEmployees', 'correspondenceListStatuses']

    static constraints = {
    }

    transient springSecurityService

    static transients = ['springSecurityService']

    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService.generateListCode("ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusList", "MARITAL", 20)
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

        //todo add new status in before insert
//       this.addToCorrespondenceListStatuses(new CorrespondenceListStatus(fromDate:trackingInfo.dateCreatedUTC,EnumCorrespondenceListStatus.CREATED,receivingParty:receivingParty,firm: firm))
    }

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}