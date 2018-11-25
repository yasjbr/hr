package ps.gov.epsilon.aoc.correspondences

import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList

/**
 * represents the many to many relation between AocCorrespondenceList and hr CorrespondenceList
 */
class JoinedAocHrCorrespondenceList {

    /**
     * Correspondence list received or sent to HR system
     */
    static belongsTo = [aocCorrespondenceList:AocCorrespondenceList, hrCorrespondenceList:CorrespondenceList, firm:Firm]
    static constraints = {
    }

    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }
}
