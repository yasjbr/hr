package ps.gov.epsilon.hr.enums.training.v1

/**
 * Created by mkharma on 16/03/17.
 */
enum EnumSessionAttendanceStatus {
    LATE("LATE") ,
    ABSENT("ABSENT"),
    ATTEND("ATTEND")
    private final String value;

    EnumSessionAttendanceStatus(String value) {
        this.value = value;
    }
    String value() {
        value
    }
}