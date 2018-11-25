package ps.gov.epsilon.hr.firm.disciplinary

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory
import ps.gov.epsilon.hr.firm.request.Request

/**
 * <h1>Purpose</h1>
 *  To hold the petition record of any disciplinary request you want to cancel
 * <h1>Usage</h1>
 *  Used to create new request in order to cancel the disciplinary. the original disciplinary request
 *  will be canceled when the petition is approved.
 *
 **/

class PetitionRequest extends Request {

    static auditable = true

    static belongsTo = [disciplinaryRequest: DisciplinaryRequest]

    PetitionRequest() {
        requestType = EnumRequestType.PETITION_REQUEST
    }

    static constraints = {
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {

    }


    @Override
    Boolean getIncludedInList() {
        def count = PetitionListEmployee.createCriteria().get {
            eq('petitionRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    static transients = ['canCancelRequest', 'canEditRequest']

    @Override
    public PetitionRequest clone() {
        PetitionRequest request = new PetitionRequest()
        request = super.cloneRequest(request)
        request.disciplinaryRequest = this.disciplinaryRequest
        request.employee = this.employee
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        return request
    }
}