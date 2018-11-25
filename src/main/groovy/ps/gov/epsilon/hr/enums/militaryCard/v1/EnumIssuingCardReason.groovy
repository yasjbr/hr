package ps.gov.epsilon.hr.enums.militaryCard.v1

/**
 * Created by mkharma on 13/03/17.
 */
enum EnumIssuingCardReason {
    NEW_EMPLOYEE("NEW_EMPLOYEE"),
    REPLACMENT("REPLACMENT"),
    LOST("LOST")

    final String value;

    EnumIssuingCardReason(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }
}