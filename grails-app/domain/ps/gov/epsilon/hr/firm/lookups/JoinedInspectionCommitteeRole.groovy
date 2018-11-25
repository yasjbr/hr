package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Inspection and the Committee Role  many-to-many relation
 * **/

class JoinedInspectionCommitteeRole {

    String id

    static belongsTo = [inspection:Inspection,committeeRole:CommitteeRole]

    static constraints = {
    }
}
