package ps.gov.epsilon.hr.firm.training.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.enums.training.v1.EnumEvaluationInsertionSource
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the evaluation answer of the evaluation form question from the form instance that had been filled related to each employee
 * <h1>Usage</h1>
 * Used  as to represent the evaluation answer from each employee form instance
 * **/

class EvaluationAnswer {

    String id

    String answer
    Double mark

    EnumEvaluationInsertionSource evaluationInsertionSource

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [evaluationFormQuestion: EvaluationFormQuestion, evaluationExecution:EvaluationExecution, formInstance:FormInstance]

    static constraints = {
        answer(Constants.STRING)
        mark (Constants.POSITIVE_DOUBLE_NULLABLE)
        evaluationInsertionSource nullable: false
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
