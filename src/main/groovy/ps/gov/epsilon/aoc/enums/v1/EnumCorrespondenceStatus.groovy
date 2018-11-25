package ps.gov.epsilon.aoc.enums.v1

/**
 * Created by muath on 21/03/18.
 *
 * Added For AOC correspondences
 */
enum EnumCorrespondenceStatus {
    NEW,                // INITIAL STATUS
    CREATED,            // Ready to start workflow
//    RETURNED_FOR_EDIT,  // Ready to start workflow
    IN_PROGRESS,        // When the correspondence in workflow
    SUBMITTED,          // Used for outgoing correspondences
    STOPPED,            // Its not Rejected, but might be processed later
    APPROVED,           // Finished with all records approved
    PARTIALLY_APPROVED, // Finished but some records are rejected
    REJECTED,           // Finished with all records are rejected
    FINISHED            // Finished, used for child incoming correspondences
}