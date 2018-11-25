package ps.gov.epsilon.hr.firm.loan

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.Job
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To notice the request of loan an employee from this firm to another firm
 * <h1>Usage</h1>
 * Used as to represents the notification to the firm which other firm requested to loan an efficient employee from it.
 * **/

//امر تسيير
class EndorseOrder {

    static auditable = true

    String id

    String encodedId

   //الامر الاداري
    String orderNo
   //تاريخ الامر الاداري
    ZonedDateTime orderDate

    ZonedDateTime effectiveDate

    TrackingInfo trackingInfo

    String note

    Map transientData=[:]

//    static nullableValues = ['orderDate']

    static embedded  = ['trackingInfo']

    static belongsTo = [firm:Firm]

    static constraints = {
        orderNo(Constants.STRING_NULLABLE + [minSize: 2,maxSize: 25])
        trackingInfo nullable: true,display:false
        note (Constants.DESCRIPTION_NULLABLE + [validator: { value, object,errors ->
            if (!value && !object?.orderNo)
                errors.reject('endorseOrder.note.required.error')
            return true
        }])
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId','transientData']

    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
        orderDate type: PersistentDocumentaryDate, {
            column name: 'order_date_datetime'
            column name: 'order_date_date_tz'
        }

        note type: "text"
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

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
