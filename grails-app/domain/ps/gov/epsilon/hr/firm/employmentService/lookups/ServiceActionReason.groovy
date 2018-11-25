package ps.gov.epsilon.hr.firm.employmentService.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the reasons that ends employee services
 * <h1>Usage</h1>
 * Used  as to represent the reasons that ends employee services
 *<h1>Example</h1>
 * RETIREMENT, FIRING, EXCEPTIONAL, MEDICAL,DEATH,RESIGNATION
 * **/

class ServiceActionReason {

    String encodedId

    String id

    DescriptionInfo descriptionInfo
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    //حالة الموظف التي يجب ادخالها للموظف في حال اصبح هذا الطلب موافق عليه
    EmployeeStatus employeeStatusResult

    TrackingInfo trackingInfo

    static belongsTo = [serviceActionReasonType:ServiceActionReasonType,firm:Firm]

    static embedded = ['trackingInfo','descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget:"DescriptionInfo"
        employeeStatusResult nullable: true
        trackingInfo nullable: true,display:false
        id bindable: true
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
    }

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    @Override
    String toString(){
        return this?.descriptionInfo?.toString()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
