package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.police.config.v1.Constants

/**
 * <h1>Purpose</h1>
 * To hold the Interview and the Committee Role many-to-many relation
 * **/

class JoinedInterviewCommitteeRole {

    String partyName

    String id

    static belongsTo = [interview:Interview,committeeRole:CommitteeRole]

    static constraints = {
        //set to nullable true in case we didn't know the party name in interview creation process
        partyName(Constants.NAME_NULLABLE)
    }
}
