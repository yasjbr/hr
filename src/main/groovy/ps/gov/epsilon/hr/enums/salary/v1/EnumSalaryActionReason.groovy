package ps.gov.epsilon.hr.enums.salary.v1

/**
 * Created by mkharma on 05/04/17.
 */
enum EnumSalaryActionReason {
    ABSENCE("ABSENCE"),
    DISCIPLINARY("DISCIPLINARY"),
    EXCEPTIONAL("EXCEPTIONAL"),
    END_OF_SERVICE("END_OF_SERVICE"),
    RETURN_TO_SERVICE("RETURN_TO_SERVICE"),
    OTHERS("OTHERS")

    final String value;

    EnumSalaryActionReason(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }


}