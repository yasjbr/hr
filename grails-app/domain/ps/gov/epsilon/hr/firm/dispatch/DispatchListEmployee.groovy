package ps.gov.epsilon.hr.firm.dispatch

import grails.util.Holders
import ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the  employee dispatch requests as dispatch list employee
 * <h1>Usage</h1>
 * Used  as to represents the employee dispatch requests
 * **/

class DispatchListEmployee {

    String encodedId

    String id

    EnumListRecordStatus recordStatus

    Map transientData = [:]

    /***
     * To keep history of the Employment Record  and military rank when this Disciplinary has been taken
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2)Entered from the Disciplinary module it will take the current employment Record of the employee
     *
     */
    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank


    Short periodInMonths

    ZonedDateTime fromDate
    ZonedDateTime toDate

    //يمثل الطلبات الخاصة بالموافقة على التجديد السنوي
    ZonedDateTime nextVerificationDate

    Long organizationId
    String organizationName
    Long educationMajorId
    String educationMajorName
    Long locationId

    //external information to describe the structured location
    String unstructuredLocation

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [dispatchRequest: DispatchRequest, dispatchList: DispatchList]

    static hasMany = [dispatchListEmployeeNotes:DispatchListEmployeeNote]

    static nullableValues = ['nextVerificationDate']

    static constraints = {
        periodInMonths(Constants.POSITIVE_SHORT_NULLABLE)
        recordStatus nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        organizationId(Constants.POSITIVE_LONG_NULLABLE)
        organizationName (Constants.NAME_NULLABLE)
        educationMajorId (Constants.POSITIVE_LONG_NULLABLE)
        educationMajorName (Constants.NAME_NULLABLE)
        locationId (Constants.POSITIVE_LONG_NULLABLE)
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"]+[blank: false])
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
        nextVerificationDate type: PersistentDocumentaryDate, {
            column name: 'next_verification_date_datetime'
            column name: 'next_verification_date_date_tz'
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
        if(recordStatus == null)
            recordStatus=EnumListRecordStatus.NEW
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)
            applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }


    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
