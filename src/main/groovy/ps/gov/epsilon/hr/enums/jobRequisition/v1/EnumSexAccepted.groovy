package ps.gov.epsilon.hr.enums.jobRequisition.v1

/**
 * Created by aarmiti on 11/06/17.
 */
enum EnumSexAccepted {
    MALE (1L),
    FEMALE(2L) ,
    BOTH(3L)

    final Long value;

    EnumSexAccepted(Long value) {
        this.value = value;
    }

    String toString() {
        name()
    }

    String getKey() {
        name()
    }
}