package ps.gov.epsilon.hr.firm.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationItem
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the EmployeeEvaluation and the Evaluation items many-to-many relation
 **/

class JoinedEmployeeEvaluationItems {

    String encodedId

    String id

    String notes

    Double mark

    static belongsTo = [employeeEvaluation:EmployeeEvaluation, evaluationItem:EvaluationItem]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        trackingInfo nullable: true,display:false
        notes(Constants.DESCRIPTION_NULLABLE)
    }

    static mapping = {
        notes type: 'text'
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

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)
            applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

    @Override
    String toString(){
        return this?.evaluationItem?.toString()
    }
}
