package ps.gov.epsilon.hr.firm.absence

import grails.util.Holders
import ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequest
import ps.gov.epsilon.hr.firm.disciplinary.EmployeeViolation
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
 * To hold the employee absence
 * <h1>Usage</h1>
 * Used  as to represent all employee absences.
 * **/
//todo دراسة اشعارات العودة بتفاصيلها
class Absence extends EmployeeViolation{

    static auditable = true

    //when the absence started. This will change the employee attendance status to "ABSENT"
    ZonedDateTime fromDate

    //when the absence ended. This will change the employee attendance status to "WORKING"
    //toDate nullable: true
    ZonedDateTime toDate

    //total absence days count
    Long numOfDays

    //why the employee became absent
    String reasonDescription

    //why the employee became absent. For example : vacation, medical, arrest
    EnumAbsenceReason absenceReason

    //The actual reason why the employee became absent. For example : vacation, medical, arrest
    EnumAbsenceReason actualAbsenceReason

    // يكون النوع انضباطية بشكل دائم EnumDisciplinaryCategory.DISCIPLINARY
    //each absence instance must be related to a discipline instance that may have many types
    // (like deducting from salary, deducting from annual vacation balance ...)
    //DisciplinaryRequest disciplinaryRecordRequest

    static nullableValues = ['toDate']

    static constraints = {
        reasonDescription (Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        actualAbsenceReason nullable: true
        numOfDays(Constants.POSITIVE_LONG_NULLABLE)
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
}
