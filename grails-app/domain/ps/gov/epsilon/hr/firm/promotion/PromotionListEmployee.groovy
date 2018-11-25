package ps.gov.epsilon.hr.firm.promotion

import grails.util.Holders
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankClassification
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankType
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the list of employees who are promoted
 *<h1>Usage</h1>
 * Used  as to represent the list of employe es who are promoted. Employees are added to the list automatically based on approving their request
 *<h1>Example</h1>
 * if an employee request a period settlement promotion and his request is accepted then he is added to the list
 **/
class PromotionListEmployee   {

    String id

    String encodedId
    /**
     * In the screen you need to use only the below reason not all enum reason in the EnumPromotionReason
     * ELIGIBLE,EXCEPTIONAL
     */
    EnumPromotionReason promotionReason

    Request request

    /*
     * To keep history of the Employment Record  and military rank when this Disciplinary has been taken
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2) Entered from the Disciplinary module it will take the current employment Record of the employee
     */
    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank

    /*
     * The militaryRank, militaryRankType, militaryRankClassification added here to record the the values at the request time
     * to avoid the effect of changing in the militaryRank configuration
     */
    // requested rank
    MilitaryRank militaryRank
    // requested rank type
    MilitaryRankType militaryRankType
    // requested rank classification
    MilitaryRankClassification militaryRankClassification

    /*
     * the actualDueDate, dueDate, militaryRankTypeDate, employmentDate added here to record the the values per request to avoid
     * the effect of changing in the militaryRank configuration, also for performance issues
     * actualDueDate: the date we get from Saraya (التاريخ الفعلي من الهيئة للترقية)
     * dueDate: calculated using military rank setup
     * militaryRankTypeDate: تاريخ نوع الرتبة
     * ZonedDateTime: تاريخ الأخذ
     */
    ZonedDateTime actualDueDate;
    ZonedDateTime dueDate;
    ZonedDateTime militaryRankTypeDate;
    ZonedDateTime employmentDate

    //rank order number will be used in create EmployeePromotion.
    String managerialOrderNumber
    ZonedDateTime orderDate

    String statusReason

    EnumListRecordStatus recordStatus

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [employee: Employee, promotionList: PromotionList,firm:Firm]

    static hasMany = [promotionListEmployeeNotes:PromotionListEmployeeNote]

    static nullableValues = ['actualDueDate','dueDate','militaryRankTypeDate', 'employmentDate', 'orderDate']

//    static includeInValidate = ['employee']

    static constraints = {
        militaryRank(nullable: true,widget:"autocomplete")
        militaryRankType(nullable: true,widget:"autocomplete")
        militaryRankClassification(nullable: true,widget:"autocomplete")
        statusReason(nullable: true, blank: false, size: 0..250)
        employee(nullable: false, unique: 'promotionList')
        promotionReason nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        recordStatus nullable: false
        request nullable: true
        managerialOrderNumber(Constants.LOOKUP_NAME_NULLABLE)
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        actualDueDate type: PersistentDocumentaryDate, {
            column name: 'actual_due_date_datetime'
            column name: 'actual_due_date_date_tz'
        }
        dueDate type: PersistentDocumentaryDate, {
            column name: 'due_date_datetime'
            column name: 'due_date_date_tz'
        }
        employmentDate type: PersistentDocumentaryDate, {
            column name: 'employment_date_datetime'
            column name: 'employment_date_date_tz'
        }
        militaryRankTypeDate type: PersistentDocumentaryDate, {
            column name: 'military_rank_type_date_date_datetime'
            column name: 'military_rank_type_date_date_tz'
        }
        orderDate type: PersistentDocumentaryDate, {
            column name: 'order_date_date_date_datetime'
            column name: 'order_date_date_date_tz'
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
        if(!recordStatus)
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
