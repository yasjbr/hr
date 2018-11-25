package ps.gov.epsilon.hr.firm.child

import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants

/**
 *<h1>Purpose</h1>
 * To hold the employee request for add child , When a married employee gets a new child, he/she requests to add the child to his profile (this affects the employee salary)
 * <h1>Usage</h1>
 * Used  as to represent the employee request for adding new child to the employee profile
 * **/
class ChildRequest extends Request{

    static auditable = true

    Long relatedPersonId

    //relationShip in core!
    Long personRelationShipId

    /**
     * indicate if the related person was dependant
     * on this person as his sun or his daughter
     **/
    Boolean isDependent = false

    public ChildRequest() {
        requestType = EnumRequestType.CHILD_REQUEST
        requestStatus = EnumRequestStatus.CREATED
    }

    static constraints = {
        relatedPersonId(Constants.POSITIVE_LONG)
        employee nullable: false
        personRelationShipId(Constants.POSITIVE_LONG_NULLABLE)
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }


    @Override
    Boolean getIncludedInList() {
        def count = ChildListEmployee.createCriteria().get {
            eq('childRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    static transients = ['canCancelRequest', 'canEditRequest']

    @Override
    public ChildRequest clone() {
        ChildRequest request = new ChildRequest()
        request = super.cloneRequest(request)
        request.relatedPersonId = this.relatedPersonId
        request.personRelationShipId = this.personRelationShipId
        request.employee = this.employee
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        request.isDependent = this.isDependent
        return request
    }
}
