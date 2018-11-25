package ps.gov.epsilon.hr.firm.suspension

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the requests for extending a suspension that is already started bot still not ended
 * <h1>Usage</h1>
 * Used as to represents the way of extending a suspension that is already started but still not ended
 * **/

public class SuspensionExtensionRequest extends Request {

    static auditable = true

    //fromDate suspension should be greater than suspension toDate
    // which represents the End Date for the Original Suspension
    ZonedDateTime fromDate
    //the new date ( requested new date)
    ZonedDateTime toDate

    Short periodInMonth

    TrackingInfo trackingInfo
    static embedded = ['trackingInfo']

    static belongsTo = [suspensionRequest: SuspensionRequest]

    public SuspensionExtensionRequest() {
        requestType = EnumRequestType.REQUEST_FOR_SUSPENSION_EXTENSION;
    }

    public SuspensionExtensionRequest(SuspensionRequest suspensionRequest) {
        this.employee = suspensionRequest.employee
        this.suspensionRequest = suspensionRequest
        requestType = EnumRequestType.REQUEST_FOR_SUSPENSION_EXTENSION;
    }

    static constraints = {
        trackingInfo nullable: true, display: false
        periodInMonth(Constants.POSITIVE_SHORT + [min: Short.parseShort("1"), max: Short.parseShort("12")])

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
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

    static transients = ['springSecurityService']

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

}

