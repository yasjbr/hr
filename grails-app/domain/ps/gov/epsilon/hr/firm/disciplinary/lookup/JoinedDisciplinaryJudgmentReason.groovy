package ps.gov.epsilon.hr.firm.disciplinary.lookup

import ps.gov.epsilon.hr.firm.Firm
import ps.police.config.v1.Constants


/**
 * <h1>Purpose</h1>
 * To hold the Disciplinary Reason and the Disciplinary Judgment many-to-many relation
 * **/

class JoinedDisciplinaryJudgmentReason {

    String id

    static belongsTo = [disciplinaryReason: DisciplinaryReason, disciplinaryJudgment: DisciplinaryJudgment, firm: Firm]

    static constraints = {
    }

}
