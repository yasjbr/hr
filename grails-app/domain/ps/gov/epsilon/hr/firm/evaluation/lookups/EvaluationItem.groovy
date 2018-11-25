package ps.gov.epsilon.hr.firm.evaluation.lookups

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
 * To hold the items that compose employee evaluation form
 * <h1>Usage</h1>
 * Used  as to represent the items in evaluation form
 *<h1>Example</h1>
 * Loyalty, Skills, ...
 * **/
class EvaluationItem {

    String encodedId

    String id

    DescriptionInfo descriptionInfo

    /**
     * Should be less than 100, also should take care that all items should sum to 100
     */
    Double maxMark

    /**
     * index of item in evaluation section
     */
    Integer index

    Map transientData = [:]

    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [firm:Firm, evaluationSection:EvaluationSection]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        index (Constants.POSITIVE_INTEGER)
        maxMark (Constants.POSITIVE_DOUBLE)
        universalCode (Constants.LOOKUP_NAME)//to use in excel
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
        id bindable: true
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

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

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)
            applicationName = "BootStrap";
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
