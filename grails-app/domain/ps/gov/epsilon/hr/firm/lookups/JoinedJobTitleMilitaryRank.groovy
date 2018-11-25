package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the Military Rank  many-to-many relation
 * **/

class JoinedJobTitleMilitaryRank {

    String id

    static belongsTo = [militaryRank:MilitaryRank,jobTitle:JobTitle]

    static constraints = {
        militaryRank nullable: false,widget:"autocomplete"
        jobTitle nullable: false,widget:"autocomplete"
    }

}
