package ps.gov.epsilon.hr.firm.training.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.training.v1.EnumEvaluationFormType
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the evaluation form of the training that will have many sections
 * <h1>Usage</h1>
 * Used  as to represents all the evaluation form related to training in the firm
 * **/

class EvaluationForm  {

    String id

    DescriptionInfo descriptionInfo
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    EnumEvaluationFormType evaluationFormType

    TrackingInfo trackingInfo

    static belongsTo = [firm:Firm]

    static embedded = ['trackingInfo','descriptionInfo']

    static hasMany = [evaluationFormSections: EvaluationFormSection,joinedEvaluationFormTrainingCategories:JoinedEvaluationFormTrainingCategory]

    static constraints = {
        universalCode nullable: true,blank:false
        evaluationFormType nullable: false
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
