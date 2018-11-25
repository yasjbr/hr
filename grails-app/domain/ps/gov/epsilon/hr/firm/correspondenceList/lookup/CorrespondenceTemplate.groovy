package ps.gov.epsilon.hr.firm.correspondenceList.lookup

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
 * To hold the correspondence cover letter template that will be based on the type of the correspondence
 * <h1>Usage</h1>
 * Used  as to represent the correspondence cover letter template
 *<h1>Example</h1>
 * الاسم: نص مخاطبة كتب تمديد الاجازة
 * النص: تحية الوطن, الاخوة في هيئة التنظيم والادارة القائمة المرفقة تحتوي اسماء الضباط المطلوب تمديد اجازاتهم ...etc
 *  * **/

class CorrespondenceTemplate {

    String id

    String encodedId

    DescriptionInfo descriptionInfo

    String coverLetter

    Map transientData=[:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [firm:Firm]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        coverLetter(Constants.DESCRIPTION_NULLABLE)
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        coverLetter type: 'text'
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)
            applicationName = "BootStrap"
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

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
