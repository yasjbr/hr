package ps.gov.epsilon.hr.firm.training.attendance

import ps.gov.epsilon.hr.enums.training.v1.EnumSessionAttendanceStatus
import ps.gov.epsilon.hr.firm.training.Trainer
import ps.gov.epsilon.hr.firm.training.execution.CourseAttendee
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the information of trainee attendance to the session of the training course
 * <h1>Usage</h1>
 * Used  as to represent the information about trainee attendance to the session of the training course
 * **/

class SessionAttendance {

    String id

    ZonedDateTime dateOfSession

    ZonedDateTime fromTime
    ZonedDateTime toTime

    //session attendance status like: LATE,ABSENT ,...etc.
    EnumSessionAttendanceStatus sessionAttendanceStatus

    CourseExecutionSessionSchedule schedule


    static belongsTo = [courseAttendee:CourseAttendee]

    static hasMany = [trainer: Trainer]

    static constraints = {
        schedule nullable: true,widget:"autocomplete"
        sessionAttendanceStatus nullable: false
    }

    static mapping = {
        dateOfSession type: PersistentDocumentaryDate, {
            column name: 'date_of_session_datetime'
            column name: 'date_of_session_date_tz'
        }
        fromTime type: PersistentDocumentaryDate, {
            column name: 'from_time_datetime'
            column name: 'from_time_date_tz'
        }
        toTime type: PersistentDocumentaryDate, {
            column name: 'to_time_datetime'
            column name: 'to_time_date_tz'
        }
    }
}
