package ps.gov.epsilon.aoc.correspondences.returnToService

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.employmentService.ServiceListEmployee
import ps.gov.epsilon.hr.firm.profile.Employee

class AocReturnToServiceListRecord extends AocListRecord{

    static belongsTo = [serviceListEmployee:ServiceListEmployee]

    static constraints = {
    }

    static transients = ['employee']

    Employee getEmployee(){
        return serviceListEmployee?.employmentServiceRequest?.employee
    }

    @Override
    public ServiceListEmployee getHrListEmployee(){
        return serviceListEmployee
    }
}
