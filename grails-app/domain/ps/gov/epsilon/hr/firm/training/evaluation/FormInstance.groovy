package ps.gov.epsilon.hr.firm.training.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the form instance for each employee as an instance of evaluation form but had been filled related to the employee
 * <h1>Usage</h1>
 * Used  as to represents all the instance of evaluation form that had been filled related to each employee
 *<h1>Example</h1>
 * For an employee who have training and we need to evaluate him/her, An form instance of the evaluation form for each employee must be filled.
 * **/

class FormInstance {

    String id

    // The value of the sequence =  max("sequence")+1 group by EvaluationExecution
    Long sequence

    //need to check (used to evaluate trainee)
    Employee trainee

    static belongsTo = [evaluationExecution:EvaluationExecution]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        sequence(Constants.POSITIVE_LONG_NULLABLE)
        trainee nullable: true,widget:"autocomplete"
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

    //    static beforeInsert={
//        FormInstance.withNewSession {
//            sequence=(FormInstance.createCriteria().get {
//                eq("evaluationRun",evaluationRun)
//                projections {
//                    max("sequence")
//                }
//            }?:0)+1
//        }
//    }
}
