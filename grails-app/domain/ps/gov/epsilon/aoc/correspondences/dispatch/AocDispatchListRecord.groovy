package ps.gov.epsilon.aoc.correspondences.dispatch

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.dispatch.DispatchListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

class AocDispatchListRecord extends AocListRecord{

    static belongsTo = [dispatchListEmployee:DispatchListEmployee]

    static constraints = {
    }

    static transients = ['employee']

    Employee getEmployee(){
        return dispatchListEmployee?.dispatchRequest?.employee
    }
    @Override
    public DispatchListEmployee getHrListEmployee(){
        return dispatchListEmployee
    }
}
