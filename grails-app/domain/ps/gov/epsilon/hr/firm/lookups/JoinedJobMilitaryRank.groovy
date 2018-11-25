package ps.gov.epsilon.hr.firm.lookups

/**
 * <h1>Purpose</h1>
 * To hold the Job Title and the Military Rank  many-to-many relation
 * **/

class JoinedJobMilitaryRank {

    String id

    static belongsTo = [militaryRank:MilitaryRank,job:Job]

    static constraints = {
        militaryRank nullable: false,widget:"autocomplete"
        job nullable: false,widget:"autocomplete"
    }

}
