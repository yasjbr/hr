package ps.gov.epsilon.aoc.correspondences.disciplinary

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.child.ChildListEmployee
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRecordJudgment
import ps.gov.epsilon.hr.firm.profile.Employee

class AocDisciplinaryListRecord extends AocListRecord{

    static belongsTo = [disciplinaryRecordJudgment:DisciplinaryRecordJudgment]

    static constraints = {
    }

    static transients = ['employee']

    Employee getEmployee(){
        return disciplinaryRecordJudgment?.disciplinaryRequest?.employee
    }

    @Override
    public DisciplinaryRecordJudgment getHrListEmployee(){
        return disciplinaryRecordJudgment
    }
}
