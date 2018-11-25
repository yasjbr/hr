package  ps.gov.epsilon.hr.enums.training.v1

public enum EnumTrainingStatus {
    UNDER_DEVELOPMENT("UNDER_DEVELOPMENT") ,
    SUGGESTED("SUGGESTED") ,
    ACTIVE("ACTIVE") ,
    INACTIVE("INACTIVE") ,
    CANCEL("CANCEL") ,

    final String value;

    EnumTrainingStatus(String value) {
        this.value = value;
    }
    String toString() {
        value;
    }
    String getKey() {
        name()
    }
}