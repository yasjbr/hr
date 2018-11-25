package ps.gov.epsilon.hr.firm.training.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.training.v1.EnumEvaluationQuestionType
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the evaluation form questions in the evaluation form section that related to one evaluation form
 * <h1>Usage</h1>
 * Used  as to represents all the evaluation form question in the evaluation form section inside the evaluation form
 * **/

class EvaluationFormQuestion {

    String id

    String hint

    DescriptionInfo descriptionInfo

    TrackingInfo trackingInfo

    Short sequence

    Double maxMark=0.0

    Boolean showSequence

    EnumEvaluationQuestionType evaluationQuestionType

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [evaluationFormSection: EvaluationFormSection]

    static hasMany = [evaluationQuestionChoices: EvaluationQuestionChoices]

    static constraints = {
        sequence(nullable: false, unique:['evaluationFormSection'] )
        hint(Constants.STRING_NULLABLE)
        maxMark(Constants.POSITIVE_DOUBLE)
        evaluationQuestionType nullable: false
        showSequence nullable: false
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
