package ps.gov.epsilon.aoc.correspondences.evaluation

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.evaluation.EvaluationListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

class AocEvaluationListRecord extends AocListRecord{

    static belongsTo = [evaluationListEmployee:EvaluationListEmployee]

    static constraints = {
    }

    static transients = ['employee']

    Employee getEmployee(){
        return evaluationListEmployee?.employeeEvaluation?.employee
    }

    @Override
    public EvaluationListEmployee getHrListEmployee(){
        return evaluationListEmployee
    }
}
