package ps.gov.epsilon.aoc.correspondences.suspension

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.suspension.SuspensionListEmployee

class AocSuspensionListRecord extends AocListRecord {

    static constraints = {
    }

    static belongsTo = [suspensionListEmployee: SuspensionListEmployee]

    @Override
    public SuspensionListEmployee getHrListEmployee(){
        return suspensionListEmployee
    }
}
