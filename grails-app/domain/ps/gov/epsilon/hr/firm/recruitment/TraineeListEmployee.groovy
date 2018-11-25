package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumTrainingEvaluation
import ps.gov.epsilon.hr.firm.lookups.TrainingRejectionReason
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the applicant in the recruitment list employee, so the recruitment list employee consider as holder of applicant
 * <h1>Usage</h1>
 * Used  as to represents the recruitment list employee that holds the applicant
 * **/

class TraineeListEmployee {
    String encodedId

    String id

    EnumListRecordStatus recordStatus

    Applicant applicant

    TrackingInfo trackingInfo

    // A,B or 50,90
    String mark
    //سبب الرفض
    TrainingRejectionReason trainingRejectionReason
//التقدير (ممتاز,متوسط)
    EnumTrainingEvaluation trainingEvaluation

    static embedded = ['trackingInfo']

    static belongsTo = [traineeList: TraineeList]

    static hasMany = [trainingListEmployeeNotes: TrainingListEmployeeNote]

    static constraints = {

        trainingRejectionReason(nullable: true, validator: { value, object, errors ->
            if ((object?.recordStatus == EnumListRecordStatus.REJECTED) && !value)
                errors.reject('traineeListEmployee.trainingRejectionReason.error.required')
            return true
        })

        trainingEvaluation(nullable: true, validator: { value, object, errors ->
            if ((object?.recordStatus == EnumListRecordStatus.APPROVED) && !value)
                errors.reject('traineeListEmployee.trainingEvaluation.error.required')
            return true
        })

        mark nullable: true
        recordStatus nullable: false
        applicant nullable: false, widget: "autocomplete"
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
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
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}
