package ps.gov.epsilon.hr.firm.dispatch

import grails.util.Holders
import ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchListType
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Dispatch list that contains many records of dispatch list employee after get approval.
 * It consider as correspondence list and have a reference to distinguish between different dispatch list.
 * <h1>Usage</h1>
 * Used  as to represents the dispatch list which contains many records of dispatch list employee
 * **/

class DispatchList extends CorrespondenceList {
    def sharedService

    //The dispatch list type like Dispatch, Dispatch extension and Dispatch stop
    //EnumDispatchListType dispatchListType

    static hasMany = [dispatchListEmployees: DispatchListEmployee]

    static constraints = {
        //dispatchListType nullable: false
    }


    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['dispatchListEmployees','correspondenceListStatuses','currentStatus']


    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.dispatch.DispatchList", "DISPATCH", 20)
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

        //todo add new status in before insert
//       this.addToCorrespondenceListStatuses(new CorrespondenceListStatus(fromDate:trackingInfo.dateCreatedUTC,EnumCorrespondenceListStatus.CREATED,receivingParty:receivingParty,firm: firm))
    }
}
