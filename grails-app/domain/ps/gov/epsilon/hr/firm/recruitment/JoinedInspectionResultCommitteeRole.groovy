package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.police.config.v1.Constants

/**
 * <h1>Purpose</h1>
 * To hold the Applicant Inspection Result and the Committee Role many-to-many relation
 * **/

class JoinedInspectionResultCommitteeRole {

    //todo check the medical tests requirements
    String partyName

    String id

    static belongsTo = [applicantInspectionResult:ApplicantInspectionResult,committeeRole:CommitteeRole]

    static constraints = {
        //set to nullable true in case we didn't know the party name in Applicant Inspection Result creation process
        partyName(Constants.NAME_NULLABLE)
    }
}
