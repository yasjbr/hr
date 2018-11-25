package  ps.gov.epsilon.hr.enums.training.v1

public enum EnumParticipationDepartmentCourseStatus {
    SUGGESTED("SUGGESTED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    UNDER_SELECTION("UNDER_SELECTION"), // course
    UNDER_IMPLEMENTATION("UNDER_IMPLEMENTATION")  , // course
    NOT_IMPLEMENTED("NOT_IMPLEMENTED")  , // course
    CLOSED("CLOSED") // course

    final String value;

    EnumParticipationDepartmentCourseStatus(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }
}