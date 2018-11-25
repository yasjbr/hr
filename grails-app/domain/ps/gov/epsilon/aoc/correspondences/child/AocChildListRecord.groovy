package ps.gov.epsilon.aoc.correspondences.child

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.child.ChildListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

import java.time.ZonedDateTime

class AocChildListRecord extends AocListRecord{

    static belongsTo = [childListEmployee:ChildListEmployee]
    static constraints = {
    }

    static transients = ['employee', 'effectiveDate']

    Employee getEmployee(){
        return childListEmployee?.childRequest?.employee
    }
    ZonedDateTime getEffectiveDate(){
        return childListEmployee?.effectiveDate
    }

    @Override
    public ChildListEmployee getHrListEmployee(){
        return childListEmployee
    }

}
