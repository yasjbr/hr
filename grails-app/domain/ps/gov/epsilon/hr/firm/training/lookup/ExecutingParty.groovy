package ps.gov.epsilon.hr.firm.training.lookup

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the information about executing party related to department or organization in the firm
 * <h1>Usage</h1>
 * Used  as to represent the information of executing party
 *<h1>Example</h1>
 * UNDP, Federal Police (Germany),...,etc.
 * **/

class ExecutingParty  {

    String id

    DescriptionInfo descriptionInfo
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    Long organizationId

    Department department

    TrackingInfo trackingInfo

    static belongsTo = [firm:Firm]

    static embedded = ['trackingInfo','descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        //todo Executing Party should be related to department or organization
        department nullable: true,widget:"autocomplete"
        organizationId(Constants.POSITIVE_LONG_NULLABLE + [validator: { value, object,errors ->
            if (!object.department  && !value )
                errors.reject('ExecutingParty.organizationId.error.required')
            return true
        }])
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService']

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
}
