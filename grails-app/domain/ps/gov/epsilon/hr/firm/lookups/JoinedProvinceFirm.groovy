package ps.gov.epsilon.hr.firm.lookups

import ps.gov.epsilon.hr.firm.Firm
import ps.police.postgresql.PCPSequenceGenerator

/**
 * <h1>Purpose</h1>
 * To hold the Firm and the Province  many-to-many relation
 * **/
class JoinedProvinceFirm {

    Boolean isDefault = false

    String id

    static belongsTo = [firm: Firm, province: Province]

    static constraints = {
        isDefault nullable: true
    }
    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator', type: Long, params: [prefer_sequence_per_entity: true]
    }
}
