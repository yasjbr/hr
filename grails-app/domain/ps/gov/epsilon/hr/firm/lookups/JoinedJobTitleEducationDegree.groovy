package ps.gov.epsilon.hr.firm.lookups

import ps.police.config.v1.Constants

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the Education Degree  many-to-many relation
 * **/

class JoinedJobTitleEducationDegree {

    String id

    /**
     * Refer to the education degree in the core
     * */
    Long educationDegreeId
    JobTitle jobTitle

    static constraints = {
        educationDegreeId(Constants.POSITIVE_LONG)
        jobTitle nullable: false,widget:"autocomplete"
    }
    static transients = ['educationDegreeName']

}
