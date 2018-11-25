package ps.gov.epsilon.hr.firm.training.evaluation

import ps.gov.epsilon.hr.firm.training.lookup.TrainingClassification

/**
 * <h1>Purpose</h1>
 * To hold the Training Category and the Evaluation Form many-to-many relation
 * **/

class JoinedEvaluationFormTrainingCategory {

    String id

    static belongsTo = [trainingClassification: TrainingClassification, evaluationForm:EvaluationForm]

    static constraints = {
    }
}
