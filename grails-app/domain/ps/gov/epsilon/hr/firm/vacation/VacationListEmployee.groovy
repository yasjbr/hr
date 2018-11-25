package ps.gov.epsilon.hr.firm.vacation

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 *  To hold the Employees who are added to a specific vacation list related to vacation request.
 * <h1>Usage</h1>
 * Used as to represents the employees who are added to a specific vacation list due to many reasons like annual leave, sick leave and others.
 * **/

//في حال الاجازة الخارجية الترصيد يكون بعد الموافقة من الهيئة على القوائم
class VacationListEmployee {

    String id

    String encodedId

    EnumListRecordStatus recordStatus

    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [vacationList: VacationList, vacationRequest: VacationRequest]

    static hasMany = [vacationListEmployeeNotes: VacationListEmployeeNote]

    static constraints = {

        recordStatus nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

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


    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }
}
