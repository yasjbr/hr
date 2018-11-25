package ps.gov.epsilon.aoc.correspondences.maritalStatus

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

import java.time.ZonedDateTime

class AocMaritalStatusListRecord extends AocListRecord{

    static belongsTo = [maritalStatusListEmployee:MaritalStatusListEmployee]

    static constraints = {
    }

    static transients = ['employee', 'effectiveDate']

    Employee getEmployee(){
        return maritalStatusListEmployee?.maritalStatusRequest?.employee
    }

    ZonedDateTime getEffectiveDate(){
        return maritalStatusListEmployee?.effectiveDate
    }

    @Override
    public MaritalStatusListEmployee getHrListEmployee(){
        return maritalStatusListEmployee
    }
}
