package ps.gov.epsilon.hr.firm.promotion

import grails.util.Holders
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankClassification
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankType
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the history of the employee promotion
 * <h1>Usage</h1>
 * Used  as to represent the employee promotion
 * example: Dani started work in 1/1/2010 as "ملازم"
 * so he has a record in EmployeePromotion with actualDueDate = 1/1/2010, dueDate=1/1/2010
 * (this rank (ملازم) needs 4 years to be next rank (ملازم أول)
 * so the expectation of next rank was in 2014. But for some reasons it was not changed.
 * in 1/1/2015 they gave him the new promotion so, Dani has a new record in EmployeePromotion with actualDueDate = 1/1/2015, dueDate=1/1/2014
 * **/

class EmployeePromotion {

    static auditable = true

    String id

    String encodedId

    Map transientData = [:]

    // military Rank Date  (rankDate) فعليا تاريخ الرد من الهيئة على الموافقة
    ZonedDateTime actualDueDate;

    //تاريخ الاستحقاق
    //calculated using military rank setup
    ZonedDateTime dueDate;

    //The reason of promotion like Eligible, Arrest Settlement, Education Settlement, Service Period Settlement, Exceptional
    EnumPromotionReason dueReason

    String note

    /**
     * Rank details
     * */
    MilitaryRank managerialRank
    ZonedDateTime managerialRankDate

   //filled automatically from list when the promotion created automatic by the promotion list
   String managerialOrderNumber


    ZonedDateTime militaryRankTypeDate //تاريخ نوع الرتبة
    ZonedDateTime orderDate //تاريخ الامر الاداري


    /****
     * The requestSource and promotionListEmployee
     * Entered as a result of other operations
     * 1- requestSource as result of approval of requests from (ARREST_SETTLEMENT,  EDUCATION_SETTLEMENT, SERVICE_PERIOD_SETTLEMENT)
     * 2- promotionListEmployee as result of approval on receiving promotionList from Saraya    (ELIGIBLE,EXCEPTIONAL)
     */
    Request requestSource

    PromotionListEmployee promotionListEmployee

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [employee: Employee, militaryRank:MilitaryRank, militaryRankType:MilitaryRankType, militaryRankClassification:MilitaryRankClassification,firm:Firm]

    static nullableValues = ['managerialRankDate','militaryRankTypeDate']

//    static includeInValidate = ['employee']

    static constraints = {
        promotionListEmployee nullable: true,widget:"autocomplete"
        requestSource nullable: true,widget:"autocomplete"
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        managerialRank nullable: true,widget:"autocomplete"
        militaryRankType nullable: true,widget:"autocomplete"
        militaryRankClassification nullable: true,widget:"autocomplete"
        dueReason nullable: false
        //managerialOrderNumber(Constants.NAME)
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
        managerialRankDate type: PersistentDocumentaryDate, {
            column name: 'managerial_rank_date_datetime'
            column name: 'managerial_rank_date_date_tz'
        }

        militaryRankTypeDate type: PersistentDocumentaryDate, {
            column name: 'military_rank_type_date_date_datetime'
            column name: 'military_rank_type_date_date_tz'
        }

        orderDate type: PersistentDocumentaryDate, {
            column name: 'order_date_date_datetime'
            column name: 'order_date_date_date_tz'
        }

        note type: "text"
    }

    transient springSecurityService

    static transients = ['springSecurityService','encodedId']

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
