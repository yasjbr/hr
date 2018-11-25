package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumInspectionResult
import ps.gov.epsilon.hr.enums.v1.EnumInspectionResultRate
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the inspection category result to applicant in the firm
 * <h1>Usage</h1>
 * Used  as to represents the results of applicant related to inspection category
 * **/

class ApplicantInspectionCategoryResult {

    String encodedId

    String id

    EnumInspectionResult inspectionResult


    EnumInspectionResultRate inspectionResultRate

    String resultSummary

    //تاريخ تعيين الفحص
    ZonedDateTime requestDate
    //تاريخ استلام الفحص العام, وتم اضافتها لمراعات الفحوص الطبية بشكل خاص والتي توجد التواريخ فقط على التصنيف وليس على الفحص بذاته
    ZonedDateTime receiveDate

    TrackingInfo trackingInfo

    // example A,B,C--> or can be calculated depends on the weight of the internal inspections that the category contains
     String mark

    static belongsTo = [applicant: Applicant, inspectionCategory: InspectionCategory]

    static hasMany = [testsResult: ApplicantInspectionResult, committeeRoles: JoinedInspectionCategoryResultCommitteeRole]

    static nullableValues = ['requestDate','receiveDate']

    static constraints = {
        resultSummary([nullable: true] + [blank: false] + [widget: "textarea"])
        inspectionResult nullable: false
        inspectionResultRate nullable: true
        mark nullable: true
        trackingInfo nullable: true, display: false
    }


    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    static embedded = ['trackingInfo']


    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
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


    static mapping = {
        requestDate type: PersistentDocumentaryDate, {
            column name: 'request_date_datetime'
            column name: 'request_date_date_tz'
        }

        receiveDate type: PersistentDocumentaryDate, {
            column name: 'receive_date_datetime'
            column name: 'receive_date_date_tz'
        }
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }
}
