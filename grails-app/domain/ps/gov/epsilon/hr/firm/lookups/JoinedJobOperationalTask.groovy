package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the OperationalTask  many-to-many relation
 * **/

class JoinedJobOperationalTask {

    String id

    static belongsTo = [operationalTask:OperationalTask,job:Job]

    static constraints = {
        operationalTask nullable: false,widget:"autocomplete"
        job nullable: false,widget:"autocomplete"
    }

}
