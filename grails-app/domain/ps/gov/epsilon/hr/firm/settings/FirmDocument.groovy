package ps.gov.epsilon.hr.firm.settings

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the list of the document that the firm used as
 * <h1>Usage</h1>
 * Used  as to represents the list of the document that the firm used as
 *<h1>Example</h1>
 * شهادة الميلاد,رخصة سواقة,صورة هوية,شهادة توجيهي
 * **/

class FirmDocument {

    String id

    String encodedId


    DescriptionInfo descriptionInfo

    TrackingInfo trackingInfo


    static belongsTo = [firm:Firm]

    static embedded = ['trackingInfo','descriptionInfo']

    static hasMany = [joinedFirmOperationDocument:JoinedFirmOperationDocument]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
        id bindable: true
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
