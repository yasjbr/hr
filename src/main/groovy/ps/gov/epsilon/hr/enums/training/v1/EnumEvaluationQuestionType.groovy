package ps.gov.epsilon.hr.enums.training.v1


public enum EnumEvaluationQuestionType {

    TEXT("TEXT"),
    CHOICE("CHOICE"),
    CHECKBOXES("CHECKBOXES"),
    CHECKBOX("CHECKBOX") ,
    NUMBER("NUMBER") ,
    SEPARATOR("SEPARATOR")


    final String value;

    EnumEvaluationQuestionType(String value) {
        this.value = value;
    }
    String toString() {
        value;
    }
    String getKey() {
        name()
    }
}
