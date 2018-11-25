package ps.gov.epsilon.hr.firm.evaluation.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

class EvaluationTemplate {

    String encodedId

    String id

    DescriptionInfo descriptionInfo
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    TrackingInfo trackingInfo

    EnumEvaluationTemplateType templateType

    static belongsTo = [firm:Firm]


    Map transientData = [:]

    //TODO link to military ranks and job description

    static hasMany = [evaluationSections:EvaluationSection, evaluationCriteria:EvaluationCriterium,
                      evaluationTemplateCategory:JoinedEvaluationTemplateCategory]

    static embedded = ['trackingInfo','descriptionInfo']

    static constraints = {
        descriptionInfo widget:"DescriptionInfo"
        universalCode (Constants.LOOKUP_NAME)//to use in excel
        trackingInfo nullable: true,display:false
    }

    List availableSections

    List getAvailableSections(){
        return this?.evaluationSections?.findAll{it.trackingInfo.status == GeneralStatus.ACTIVE}?.sort{it.index}
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData','availableSections']

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
        return HashHelper.encode(id?.toString())
    }

}
