package ps.gov.epsilon.hr.firm

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Person or organization Contact Info
 * <h1>Usage</h1>
 * Used as to represents Person or organization information
 * **/
class DepartmentContactInfo {

    String encodedId

    String id

    ZonedDateTime fromDate
    ZonedDateTime toDate
//    Long locationId
    String value

    //TODO check if we can accept contactMethodId and contactTypeId as nullable values or not
    // method identifier of communication with the Business Contact “for example, phone, e-mail message, ..”
    Long contactMethodId
    Long contactTypeId

    Map transientData

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static nullableValues = ['fromDate', 'toDate']

    static belongsTo = [department: Department]

    static constraints = {
//        locationId(Constants.POSITIVE_LONG_NULLABLE+[widget:"location"])
        contactMethodId(Constants.POSITIVE_LONG_NULLABLE)
        contactTypeId(Constants.POSITIVE_LONG_NULLABLE)
        trackingInfo nullable: true, display: false
        value(Constants.DESCRIPTION,
//                validator: { valueString, object, errors ->
//            if (!valueString && !object?.locationId) errors.reject('DepartmentContactInfo.value.error')
//            return true
//        })
        )
    }

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

    transient springSecurityService

    static transients = ['springSecurityService', 'transientData', "encodedId"]

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
