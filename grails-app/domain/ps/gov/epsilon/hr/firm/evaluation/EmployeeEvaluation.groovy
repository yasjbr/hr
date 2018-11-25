package ps.gov.epsilon.hr.firm.evaluation

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationCriterium
import ps.gov.epsilon.hr.firm.evaluation.lookups.EvaluationTemplate
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the employee evaluation form including items, marks and result
 * <h1>Usage</h1>
 * Used  as to represent the employee evaluation form
 * **/
class EmployeeEvaluation {

    String encodedId

    String id

    //the request status "CREATED, REJECTED ... "
    EnumRequestStatus requestStatus

    /***
     * To keep history of the Employment Record  and military rank when this Evaluation was done
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2)Entered from the Evaluation module it will take the current employment Record of the employee
     *
     */
    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank

    /***
     * set the period   if we need to
     * use period
     *
     * can be nullable
     */
    ZonedDateTime fromDate
    ZonedDateTime toDate

    /**
     * sum of evaluation items' marks
     */
    Double evaluationSum

    /**
     * after calculating evaluation sum, under which criterium this employee evaluation is situated
     */
    EvaluationCriterium evaluationResult

    Map transientData = [:]

    static nullableValues = ['fromDate','toDate']

    static belongsTo = [employee: Employee, firm: Firm, evaluationTemplate:EvaluationTemplate]

    static hasMany = [employeeEvaluationItems: JoinedEmployeeEvaluationItems]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        currentEmploymentRecord nullable: true
        currentEmployeeMilitaryRank nullable: true
        evaluationSum Constants.POSITIVE_DOUBLE_NULLABLE
        evaluationResult nullable: true
        requestStatus nullable: false
        trackingInfo nullable: true,display:false
    }

    EmployeeEvaluation() {
        this.requestStatus = ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED
    }
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

    transient springSecurityService

    static transients = ['springSecurityService','encodedId', 'transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)
            applicationName = "BootStrap"
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
}
