package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the inspection Category many-to-many relation
 * **/

class JoinedJobInspectionCategory {

    String id

    static belongsTo = [inspectionCategory:InspectionCategory,job:Job]

    static constraints = {
        inspectionCategory nullable: false,widget:"autocomplete"
        job nullable: false,widget:"autocomplete"
    }

}
