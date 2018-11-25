package ps.gov.epsilon.aoc.correspondences.allowance

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployee
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceType
import ps.gov.epsilon.hr.firm.profile.Employee

import java.time.ZonedDateTime

class AocAllowanceListRecord extends AocListRecord{

    static belongsTo = [allowanceListEmployee:AllowanceListEmployee]

    static constraints = {
    }

    static transients = ['employee', 'allowanceType', 'effectiveDate']

    Employee getEmployee(){
        return allowanceListEmployee?.allowanceRequest?.employee
    }

    AllowanceType getAllowanceType(){
        return allowanceListEmployee?.allowanceRequest?.allowanceType
    }

    ZonedDateTime getEffectiveDate(){
        return allowanceListEmployee?.allowanceRequest?.effectiveDate
    }

    @Override
    public AllowanceListEmployee getHrListEmployee(){
        return allowanceListEmployee
    }
}
