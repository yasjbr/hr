package ps.gov.epsilon.hr.firm.disciplinary

import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason

/**
 * <h1>Purpose</h1>
 * To hold the Disciplinary Reason and the Disciplinary Judgment many-to-many relation
 * **/

class JoinedDisciplinaryEmployeeViolation {

    String id

    static belongsTo = [employeeViolation: EmployeeViolation, disciplinaryRequest: DisciplinaryRequest, firm: Firm]

    static constraints = {
    }

}
