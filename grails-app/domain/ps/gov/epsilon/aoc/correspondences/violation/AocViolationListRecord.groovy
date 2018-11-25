package ps.gov.epsilon.aoc.correspondences.violation

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.disciplinary.ViolationListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

class AocViolationListRecord extends AocListRecord{

    static belongsTo = [violationListEmployee:ViolationListEmployee]
    static constraints = {
    }

    static transients = ['employee']

    Employee getEmployee(){
        return violationListEmployee?.employeeViolation?.employee
    }


    @Override
    public ViolationListEmployee getHrListEmployee(){
        return violationListEmployee
    }
}
