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
 * To hold the criteria that interprets the evaluation result
 * <h1>Usage</h1>
 * Used  as to represent the criteria in evaluation form
 *<h1>Example</h1>
 * from 0 to 40: does not deserve promotion, from 41 to 60: promotion postponed to next year ...
 * **/
class EvaluationCriterium {

    String encodedId

    String id

    Double fromMark

    Double toMark

    DescriptionInfo descriptionInfo

    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [firm:Firm, evaluationTemplate:EvaluationTemplate]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        fromMark(Constants.POSITIVE_DOUBLE)
        toMark(Constants.POSITIVE_DOUBLE)
        universalCode (Constants.LOOKUP_NAME)
        descriptionInfo widget:"DescriptionInfo"
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
