package ps.gov.epsilon.aoc.enums.employee.v1

/**
 * Created by muath on 17/05/18.
 */
enum EnumSalaryClassification {

    SERVICE_LAW('SERVICE_LAW', '1'),
    ALLOCATION('ALLOCATION', '2'),
    GIFTS('GIFTS','3'),
    PRIZES('PRIZES', '4'),
    EXTERNAL_FORCES('EXTERNAL_FORCES', '5')

    final String value;
    final String code;

    EnumSalaryClassification(String value, String code) {
        this.value = value;
        this.code = code;
    }

    String toString() {
        value;
    }

    String getValue() {
        return value
    }

    String getCode() {
        return code
    }
}