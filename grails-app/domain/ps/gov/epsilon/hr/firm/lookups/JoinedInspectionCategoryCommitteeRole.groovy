package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Inspection Category and the Committee Role  many-to-many relation
 * **/

class JoinedInspectionCategoryCommitteeRole {

    String id

    static belongsTo = [inspectionCategory:InspectionCategory,committeeRole:CommitteeRole]

    static constraints = {
    }
}
