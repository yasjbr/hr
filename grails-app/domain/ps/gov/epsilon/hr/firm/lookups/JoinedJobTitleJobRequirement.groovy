package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the Job Requirement  many-to-many relation
 * **/

class JoinedJobTitleJobRequirement {

    String id

    static belongsTo = [jobRequirement:JobRequirement,jobTitle:JobTitle]

    static constraints = {
        jobRequirement nullable: false,widget:"autocomplete"
        jobTitle nullable: false,widget:"autocomplete"
    }

}
