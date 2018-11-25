package ps.gov.epsilon.hr.firm.correspondenceList

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the correspondence list status of the correspondence list between the firm and party outside the firm
 * <h1>Usage</h1>
 * Used  as to represent the correspondence list status of the correspondence list between the firm and party outside the firm
 *<h1>Example</h1>
 * CREATED, SUBMITTED, RECEIVED, CLOSED
 * **/


class CorrespondenceListStatus {

    String id

    String encodedId

    EnumCorrespondenceListStatus correspondenceListStatus

    ZonedDateTime fromDate
    ZonedDateTime toDate

    EnumReceivingParty receivingParty

    static belongsTo = [correspondenceList:CorrespondenceList, firm:Firm]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static nullableValues = ['toDate']

    static constraints = {
        receivingParty nullable: true
        correspondenceListStatus nullable: false
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
    }

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

    def afterInsert(){
        // close other statuses not closed
        correspondenceList?.correspondenceListStatuses?.findAll {it.toDate==PCPUtils.DEFAULT_ZONED_DATE_TIME && it.id != id}?.each {
            it.toDate= fromDate
            it.save()
        }
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
