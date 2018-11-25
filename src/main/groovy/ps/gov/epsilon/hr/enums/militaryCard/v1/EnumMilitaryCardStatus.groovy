package ps.gov.epsilon.hr.enums.militaryCard.v1

/**
 * Created by mkharma on 05/04/17.
 */
enum EnumMilitaryCardStatus {
    PRINTED("PRINTED"),
    LOST("LOST"),
    REPLACED("REPLACED"),
    REQUESTED("REQUESTED"),
    DELIVERED("DELIVERED")

    final String value;

    EnumMilitaryCardStatus(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }
}