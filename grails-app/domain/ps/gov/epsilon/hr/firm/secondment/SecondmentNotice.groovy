package ps.gov.epsilon.hr.firm.secondment

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the secondment notification in military firm from civilian firm
 * <h1>Usage</h1>
 * Used as to represents the secondment notification from an civilian firm to military firm.
 * **/

//اشعار الاعارة
class SecondmentNotice {

    String id

    static auditable = true

    Long requesterOrganizationId
    String orderNo
    String jobTitle

    //calculated
    Short periodInMonths

    MilitaryRank militaryRank
    String description

    ZonedDateTime fromDate
    ZonedDateTime toDate

    TrackingInfo trackingInfo

    static embedded  = ['trackingInfo']

    static belongsTo = [firm:Firm]

    static constraints = {
        //TODO review the custom validator
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea",blank: false,validator: { value, object,errors ->
            if (!object.militaryRank && !value )
                errors.reject('SecondmentNotice.description.error.required')
            return true
        }])
        militaryRank nullable: true,widget:"autocomplete"
        requesterOrganizationId(Constants.POSITIVE_LONG)
        orderNo(Constants.STRING_NULLABLE)
        jobTitle (Constants.NAME_NULLABLE)
        periodInMonths(Constants.POSITIVE_SHORT)
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
