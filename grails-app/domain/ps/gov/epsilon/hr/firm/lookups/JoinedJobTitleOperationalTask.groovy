package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the OperationalTask  many-to-many relation
 * **/

class JoinedJobTitleOperationalTask {

    String id

    static belongsTo = [operationalTask:OperationalTask,jobTitle:JobTitle]

    static constraints = {
        operationalTask nullable: false,widget:"autocomplete"
        jobTitle nullable: false,widget:"autocomplete"
    }

}
