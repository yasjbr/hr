 package ps.gov.epsilon.hr.firm.disciplinary

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the disciplinary record of disciplinary categories in the firm
 * <h1>Usage</h1>
 * Used  as to represents all employees discipline instances.
 * **/

class DisciplinaryRequest extends Request{

    static auditable = true
    /****
     * We use the category not the reason to simplified the
     * statistical operations and bzu the reason used in the has many relation
     */
    static belongsTo = [disciplinaryCategory:DisciplinaryCategory, firm:Firm]

    static hasMany = [joinedDisciplinaryEmployeeViolations:JoinedDisciplinaryEmployeeViolation,disciplinaryJudgments:DisciplinaryRecordJudgment]

    DisciplinaryRequest() {
        requestType = EnumRequestType.DISCIPLINARY_REQUEST
    }

    static constraints = {
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {

    }
}