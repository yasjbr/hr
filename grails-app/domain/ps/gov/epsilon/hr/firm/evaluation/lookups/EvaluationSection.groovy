package ps.gov.epsilon.hr.firm.evaluation.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

class EvaluationSection {

    String encodedId

    String id

    String hint

    /**
     * index of section in evaluation form
     */
    Integer index

    DescriptionInfo descriptionInfo

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [evaluationTemplate: EvaluationTemplate,firm:Firm]

    static hasMany = [evaluationItems: EvaluationItem]

    static constraints = {
        hint(Constants.STRING_NULLABLE)
        index (Constants.POSITIVE_INTEGER)
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    List availableItems

    List getAvailableItems(){
        return this?.evaluationItems?.findAll{it.trackingInfo.status == GeneralStatus.ACTIVE}?.sort{it.index}
    }

    static transients = ['springSecurityService', 'encodedId','availableItems']

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
