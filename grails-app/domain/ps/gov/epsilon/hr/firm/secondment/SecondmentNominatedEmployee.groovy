package ps.gov.epsilon.hr.firm.secondment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the employee who nominated to secondment from military firm to civilian firm
 * <h1>Usage</h1>
 * Used as to represents the secondment nominated employee from military firm to civilian firm
 * **/

//بعد ورود كتاب التسيير يجب تغيير الحالة في ملف الموظف الى معار بشكل تلقائي, ويتم التاثير على السجل الوظيفي باغلاقه
class SecondmentNominatedEmployee {

    String id

    String encodedId

    EnumListRecordStatus recordStatus

    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank


    Short periodInMonth

    ZonedDateTime effectiveDate

    ZonedDateTime fromDate
    ZonedDateTime toDate


    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [secondmentList:SecondmentList,employee:Employee]

    static hasMany = [secondmentListEmployeeNotes:SecondmentListEmployeeNote]

    static nullableValues = ['effectiveDate']

    static constraints = {
        recordStatus nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        periodInMonth(Constants.POSITIVE_SHORT)
        trackingInfo nullable: true,display:false
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
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

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

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}