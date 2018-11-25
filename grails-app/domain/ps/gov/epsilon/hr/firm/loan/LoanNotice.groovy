package ps.gov.epsilon.hr.firm.loan

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.Job
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.request.Request
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

//اشعار بطلب الانتداب لعسكري لدى الجهاز من جهاز اخر
//عند ورود اشعار يحتوي اكثر من وظيفة يتم تقسيمه الى اعدة اشعارات, مع استخدام نفس رقم الامر الاداري
//todo add domain to include the above case
class LoanNotice {

    static auditable = true

    String id

    String encodedId

//    //المؤسسة المطلوب منها ندب شخص معين الى مؤسسة أخرى
    Long requesterOrganizationId

//الامر الاداري
    String orderNo
   //تاريخ الامر الاداري
    ZonedDateTime orderDate

    //calculated
    Short periodInMonths

//    MilitaryRank militaryRank
    String description

    ZonedDateTime fromDate
    ZonedDateTime toDate

    TrackingInfo trackingInfo

    Job requestedJob
    String jobTitle
    Short numberOfPositions

    EnumLoanNoticeStatus loanNoticeStatus

    Map transientData = [:]

    static nullableValues = ['orderDate']

    static embedded  = ['trackingInfo']

    static belongsTo = [firm:Firm]

    public LoanNotice() {
        loanNoticeStatus = EnumLoanNoticeStatus.UNDER_NOMINATION
    }

    static constraints = {
//        requestPersonId(Constants.POSITIVE_LONG_NULLABLE,[validator: { value, object,errors ->
//            if (!object.militaryRank && !object.description && !value )
//                errors.reject('LoanNotice.requestPersonId.error.required')
//            return true
//        }])
//        requestedDepartment(Constants.NAME_NULLABLE)
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea",blank: false])
//        militaryRank nullable: true,widget:"autocomplete"
        requesterOrganizationId(Constants.POSITIVE_LONG)
        jobTitle(Constants.NAME_NULLABLE)
        requestedJob nullable: true
        periodInMonths(Constants.POSITIVE_SHORT)
        orderNo(Constants.STRING_NULLABLE)
        trackingInfo nullable: true,display:false
        numberOfPositions(Constants.POSITIVE_SHORT)
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId','transientData']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
        orderDate type: PersistentDocumentaryDate, {
            column name: 'order_date_datetime'
            column name: 'order_date_date_tz'
        }

        description type: "text"
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
