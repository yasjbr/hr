package ps.gov.epsilon.aoc.correspondences

/**
 * represents the many to many relation between AocCorrespondenceList and AocRecordList
 */
class AocJoinedCorrespondenceListRecord {

    static belongsTo = [correspondenceList:AocCorrespondenceList, listRecord:AocListRecord]
    static constraints = {
    }

    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }
}
