package ps.gov.epsilon.hr.firm.lookups

import ps.police.config.v1.Constants

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the Education Degree  many-to-many relation
 * **/

class JoinedJobEducationDegree {

    String id

    /**
     * Refer to the education degree in the core
     * */
    Long educationDegreeId
    Job job

    static constraints = {
        educationDegreeId(Constants.POSITIVE_LONG)
        job nullable: false,widget:"autocomplete"
    }
}
