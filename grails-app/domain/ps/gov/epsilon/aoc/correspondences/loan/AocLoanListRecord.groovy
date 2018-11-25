package ps.gov.epsilon.aoc.correspondences.loan

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.loan.LoanListPerson

class AocLoanListRecord extends AocListRecord {

    static constraints = {
    }
    static belongsTo = [loanListPerson:LoanListPerson]

    @Override
    public LoanListPerson getHrListEmployee(){
        return loanListPerson
    }
}
