package ps.gov.epsilon.hr.firm.training.plan

import grails.util.Holders
import ps.gov.epsilon.hr.enums.training.v1.EnumCourseNeedPriority
import ps.gov.epsilon.hr.enums.training.v1.EnumParticipationDepartmentCourseStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.training.TrainingCourse
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold information about registered training course by the department in the firm
 * <h1>Usage</h1>
 * Used  as to represent the information about registered training course by the department that included in the annual training plan in the firm
 * **/

public class DepartmentRegisteredCourse  {

    String id

    Short requestedAttendsNumber = 0;
    Short approvedAttendsNumber

    EnumParticipationDepartmentCourseStatus participationDepartmentCourseStatus

    EnumCourseNeedPriority courseNeedPriority


// فترة الترشيح
    ZonedDateTime nominationStartDate;
    ZonedDateTime nominationEndDate;

//فترة التنفيذ المتوقع
    ZonedDateTime expectedExecutionStartDate;
    ZonedDateTime expectedExecutionEndDate;

    // Explain why the department need to register in this course
    String registrationReason

    // Explain why the training department reject department registration request
    String rejectionReason

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']


    static belongsTo = [firm: Firm,trainingCourse: TrainingCourse,participatingDepartment: PlanParticipatingDepartment]



    public DepartmentRegisteredCourse() {
        participationDepartmentCourseStatus = EnumParticipationDepartmentCourseStatus.SUGGESTED;
        courseNeedPriority = EnumCourseNeedPriority.MEDIUM;
    }

    static constraints = {
        requestedAttendsNumber(Constants.POSITIVE_SHORT)
        approvedAttendsNumber(Constants.POSITIVE_SHORT_NULLABLE)
        participationDepartmentCourseStatus nullable: false
        courseNeedPriority nullable: false
        registrationReason(Constants.STRING)
        rejectionReason(Constants.STRING_NULLABLE + [validator: { value, object,errors ->
            if (object.participationDepartmentCourseStatus.REJECTED  && !value )
                errors.reject('DepartmentRegisteredCourse.rejectionReason.error.required')
            return true
        }])
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        registrationReason type: 'text'
        rejectionReason type: 'text'
        nominationStartDate type: PersistentDocumentaryDate, {
            column name: 'nomination_start_date_datetime'
            column name: 'nomination_start_date_date_tz'
        }
        nominationEndDate type: PersistentDocumentaryDate, {
            column name: 'nomination_end_date_datetime'
            column name: 'nomination_end_date_date_tz'
        }
        expectedExecutionStartDate type: PersistentDocumentaryDate, {
            column name: 'expected_execution_start_date_datetime'
            column name: 'expected_execution_start_date_date_tz'
        }
        expectedExecutionEndDate type: PersistentDocumentaryDate, {
            column name: 'expected_execution_end_date_datetime'
            column name: 'expected_execution_end_date_date_tz'
        }
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