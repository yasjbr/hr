package ps.gov.epsilon.aoc.correspondences.transfer

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployee

class AocExternalTransferListRecord extends AocListRecord{

    static belongsTo = [externalTransferListEmployee:ExternalTransferListEmployee]

    static constraints = {
    }

    @Override
    public ExternalTransferListEmployee getHrListEmployee(){
        return externalTransferListEmployee
    }
}
