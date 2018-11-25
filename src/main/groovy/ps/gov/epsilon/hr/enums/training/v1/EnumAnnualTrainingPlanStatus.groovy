package ps.gov.epsilon.hr.enums.training.v1
//sway ............
enum EnumAnnualTrainingPlanStatus {

    UNDER_DEVELOPMENT("UNDER_DEVELOPMENT", 1),
    UNDER_SUGGESTED("UNDER_SUGGESTED", 2),
    UNDER_IMPLEMENTATION("UNDER_IMPLEMENTATION", 3),
    CLOSED("CLOSED", 4);

    final String value;
    final int stepNumber;  // Ordered Status by stepNumber

    EnumAnnualTrainingPlanStatus(String value, int stepNumber) {
        this.value = value;
        this.stepNumber = stepNumber;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }

    int getStepNumber() {
        return stepNumber;
    }

    static EnumAnnualTrainingPlanStatus getNextStatus(EnumAnnualTrainingPlanStatus status) {
        values().find { it.stepNumber == status.stepNumber + 1}
    }

    static EnumAnnualTrainingPlanStatus getPreviousStatus(EnumAnnualTrainingPlanStatus status) {
        values().find { it.stepNumber == status.stepNumber - 1}
    }

    static EnumAnnualTrainingPlanStatus findStepNumber(int stepNumber) {
        values().find { it.stepNumber == stepNumber }
    }

    static EnumAnnualTrainingPlanStatus findByName(String name) {
        values().find { it.name() == name }
    }
}