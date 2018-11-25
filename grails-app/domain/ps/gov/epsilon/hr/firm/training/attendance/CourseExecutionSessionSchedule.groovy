package ps.gov.epsilon.hr.firm.training.attendance

import ps.gov.epsilon.hr.enums.v1.EnumWeekDay
import ps.gov.epsilon.hr.firm.training.execution.CourseExecution
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the information schedule of each session of the training course when we start execute the course
 * <h1>Usage</h1>
 * Used  as to represent the information schedule of sessions related to training course whe we execute the course
 * **/

class CourseExecutionSessionSchedule {

    String id

    EnumWeekDay weekDay

    ZonedDateTime fromTime
    ZonedDateTime toTime

    static belongsTo = [courseExecution:CourseExecution]

    static constraints = {
        weekDay nullable: false
    }

    static mapping = {
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
