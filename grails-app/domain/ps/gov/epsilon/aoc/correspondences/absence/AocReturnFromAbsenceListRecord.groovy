package ps.gov.epsilon.aoc.correspondences.absence

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceListEmployee
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

class AocReturnFromAbsenceListRecord extends AocListRecord{

    static belongsTo = [returnFromAbsenceListEmployee:ReturnFromAbsenceListEmployee]
    static constraints = {
    }

    static transients = ['employee']

    Employee getEmployee(){
        return returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.employee
    }

    @Override
    public ReturnFromAbsenceListEmployee getHrListEmployee(){
        return returnFromAbsenceListEmployee
    }
}
