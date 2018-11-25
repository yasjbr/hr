package ps.gov.epsilon.hr.firm.training.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.firm.training.Trainer
import ps.gov.epsilon.hr.firm.training.execution.CourseExecution
import ps.police.common.domains.v1.TrackingInfo
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the period that allowed to execute the evaluation form related to course execution
 * <h1>Usage</h1>
 * Used  as to represents the period of evaluation execution
 *<h1>Example</h1>
 * After course execution, the evaluation form execution allowed from 1/6/2017 to 6/6/2017 .
 * **/

class EvaluationExecution {

    String id

    /***
     * set the period   if we need to
     * use period
     *
     * can be nullable
     */
    ZonedDateTime fromDate
    ZonedDateTime toDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [evaluationForm:EvaluationForm,courseExecution:CourseExecution]

    static hasMany = [trainers: Trainer]

    static nullableValues = ['fromDate','toDate']

    static constraints = {
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
    }

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
