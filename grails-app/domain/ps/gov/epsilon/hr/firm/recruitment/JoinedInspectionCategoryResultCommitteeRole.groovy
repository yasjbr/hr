package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.police.config.v1.Constants

/**
 * <h1>Purpose</h1>
 * To hold the Applicant Inspection Category Result and the Committee Role many-to-many relation
 * **/

class JoinedInspectionCategoryResultCommitteeRole {

    String partyName

    String id

    static belongsTo = [applicantInspectionCategoryResult:ApplicantInspectionCategoryResult,committeeRole:CommitteeRole]

    static constraints = {
        //set to nullable true in case we didn't know the party name in Applicant Inspection Category Result creation process
        partyName(Constants.NAME_NULLABLE)
    }
}
