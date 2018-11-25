package ps.gov.epsilon.hr.firm.allowance.lookups

import ps.gov.epsilon.hr.firm.Firm

class JoinedAllowanceTypeStopReason {

    String id

    static belongsTo = [allowanceStopReason:AllowanceStopReason,allowanceType:AllowanceType,firm: Firm]

    static constraints = {
    }
}
