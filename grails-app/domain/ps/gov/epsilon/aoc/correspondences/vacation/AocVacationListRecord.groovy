package ps.gov.epsilon.aoc.correspondences.vacation

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.vacation.VacationListEmployee

class AocVacationListRecord extends AocListRecord {

    static constraints = {
    }

    static belongsTo = [vacationListEmployee: VacationListEmployee]

    @Override
    public VacationListEmployee getHrListEmployee(){
        return vacationListEmployee
    }
}
