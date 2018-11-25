package ps.gov.epsilon.hr.firm.child

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the  list of child for the employee related to the child request to add the child to employee profile and to child list
 * <h1>Usage</h1>
 * Used  as to represent child list for employee after add child request is approved.
 *
 * Note:
 * After approval add to employee allowance reference record
 * After approval add to employee relations reference record
 *
 **/

class ChildListEmployee {

    String id

    String encodedId

    EnumListRecordStatus recordStatus

    /**
     * indicate if the request has allowance or not
     * because of add new sun or his daughter
     **/
    Boolean hasAllowance = false

    //when the employee started to deserve the allowance
    ZonedDateTime effectiveDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [childRequest: ChildRequest, childList: ChildList, firm:Firm]

    static hasMany = [childListEmployeeNotes: ChildListEmployeeNote]

    static constraints = {
        recordStatus nullable: false
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static nullableValues = ['effectiveDate']

    static transients = ['springSecurityService', 'encodedId']

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
        if (recordStatus == null)
            recordStatus = EnumListRecordStatus.NEW
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }
}
