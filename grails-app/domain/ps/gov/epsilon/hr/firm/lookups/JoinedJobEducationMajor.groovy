package ps.gov.epsilon.hr.firm.lookups

import ps.police.config.v1.Constants

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the Education Degree  many-to-many relation
 * **/

class JoinedJobEducationMajor {

    String id

    /**
     * Refer to the education degree in the core
     * */
    Long educationMajorId
    Job job

    static constraints = {
        educationMajorId(Constants.POSITIVE_LONG)
        job nullable: false,widget:"autocomplete"
    }
}
