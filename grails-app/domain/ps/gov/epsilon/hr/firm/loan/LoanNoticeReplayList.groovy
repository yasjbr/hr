package ps.gov.epsilon.hr.firm.loan

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the loan notice reply list which contains many records of loan nominated employee
 * <h1>Usage</h1>
 * Used as to represents the loan notice reply list which contains many records of loan nominated employee from the firm which other firm want to loan employee from it.
 * **/

// تمثل الرد على الانتداب للشخص المطلوب او لمواصفات وظيفية مطلوبة لارسالها للهيئة
class LoanNoticeReplayList extends CorrespondenceList{

    transient sharedService

    static transients = ['sharedService']

    static hasMany = [loanNominatedEmployees:LoanNominatedEmployee]

    static constraints = {
    }

    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayList", "LOANNOTICE", 20)
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

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}
