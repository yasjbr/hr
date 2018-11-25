package ps.gov.epsilon.hr.enums.training.v1

enum EnumEvaluationFormType {

    TRAINER_FROM_SUP_EVALUATION("TRAINER_FROM_SUP_EVALUATION"),
    TRAINER_FROM_TRAINEES_EVALUATION("TRAINER_FROM_TRAINEES_EVALUATION"),
    TRAINEES_FROM_SUP_EVALUATION("TRAINER_EVALUATION"),
    TRAINEES_FROM_TRAINER_EVALUATION("TRAINER_EVALUATION"),
    OUTPUT_EVALUATION("OUTPUT_EVALUATION"),
    MATERIAL_EVALUATION("MATERIAL_EVALUATION")


    final String value;

    EnumEvaluationFormType(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }
}