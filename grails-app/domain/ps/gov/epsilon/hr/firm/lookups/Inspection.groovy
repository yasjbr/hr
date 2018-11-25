package ps.gov.epsilon.hr.firm.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the firm inspections
 * <h1>Usage</h1>
 * Used  as to represents the inspection in  the inspection categories
 *<h1>Example</h1>
 * فحص المخابرات، فحص الوقائي ، ... الخ
 * **/

class Inspection {

    String id

    String encodedId

    DescriptionInfo descriptionInfo

    //description about inspection under inspection category
    String description
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    String note

    // use only for sorting
    Short orderId

    Boolean hasMark
    Boolean hasPeriod
    //use to present the sending and receiving date in the application Inspection Result
    Boolean hasDates


    //هل يتم اضافته على القوائم او لا
    Boolean isIncludedInLists

    //تمثل الوزن على مستوى الcategory وتكون القيمة فارغة اي لا يوحج وزن
    Double weight

    TrackingInfo trackingInfo

    static belongsTo = [inspectionCategory:InspectionCategory,firm:Firm]

    static hasMany = [committeeRoles:JoinedInspectionCommitteeRole]

    static embedded = ['trackingInfo','descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        weight(Constants.POSITIVE_DOUBLE_NULLABLE)
        orderId(Constants.POSITIVE_SHORT)
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
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
