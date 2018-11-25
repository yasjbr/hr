package ps.gov.epsilon.hr.firm.promotion

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the promotion lists information, its consider as correspondence list
 * <h1>Usage</h1>
 * Used  as to represent promotion lists information. Employees are added to these lists automatically based on approving their request.
 *<h1>Example</h1>
 * if an employee request a period settlement promotion and his request is accepted, then he is added to the list
 *
 *
 * Note:
 *
 * Created manually, but the system suggest the employees that eligible for promotion depends on
 * the calculation rank and due date, or add special cases from any employee who not
 * included in the eligible list
 */
class PromotionList extends CorrespondenceList{

    def sharedService

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['promotionListEmployee','correspondenceListStatuses']

    static hasMany = [promotionListEmployee: PromotionListEmployee]

    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.promotion.PromotionList", "PROMOTION", 20)
        }
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName)
            applicationName = "BootStrap"
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
}