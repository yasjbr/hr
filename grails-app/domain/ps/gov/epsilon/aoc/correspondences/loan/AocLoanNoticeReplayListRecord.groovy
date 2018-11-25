package ps.gov.epsilon.aoc.correspondences.loan

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.loan.LoanNominatedEmployee

class AocLoanNoticeReplayListRecord extends AocListRecord {

    static belongsTo = [loanNominatedEmployee: LoanNominatedEmployee]

    @Override
    public LoanNominatedEmployee getHrListEmployee(){
        return loanNominatedEmployee
    }

}
