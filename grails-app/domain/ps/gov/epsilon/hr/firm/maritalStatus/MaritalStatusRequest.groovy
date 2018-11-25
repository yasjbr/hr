package ps.gov.epsilon.hr.firm.maritalStatus

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the employee request to update the marital status
 * <h1>Usage</h1>
 * Used  as to represent the employee request to update the marital status
 * * **/

//this request added automatic to the open list after request final approval
//and affect the employee status

class MaritalStatusRequest extends Request {

    static auditable = true

    //تاريخ الحالة الاجتماعية الجديدة
    ZonedDateTime maritalStatusDate

    //الحالة الاجتماعية الجديدة
    Long newMaritalStatusId

    //الحالة الاجتماعية القديمة
    Long oldMaritalStatusId

    //الشخص ذو العلاقة
    Long relatedPersonId

    //relationShip in core!
    Long personRelationShipId

    //person marital status in core!
    Long personMaritalStatusId

    /**
     * indicate if the related person was dependant
     * on this person as his sun or his daughter
     **/
    Boolean isDependent = false


    public MaritalStatusRequest() {
        requestType = EnumRequestType.MARITAL_STATUS_REQUEST
    }

    static constraints = {
        newMaritalStatusId(Constants.POSITIVE_LONG)
        oldMaritalStatusId(Constants.POSITIVE_LONG)
        relatedPersonId(Constants.POSITIVE_LONG)
        personRelationShipId(Constants.POSITIVE_LONG_NULLABLE)
        personMaritalStatusId(Constants.POSITIVE_LONG_NULLABLE)
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {
        maritalStatusDate type: PersistentDocumentaryDate, {
            column name: 'marital_status_date_datetime'
            column name: 'marital_status_date_date_tz'
        }
    }


    @Override
    Boolean getIncludedInList() {
        def count = MaritalStatusListEmployee.createCriteria().get {
            eq('maritalStatusRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    static transients = ['canCancelRequest', 'canEditRequest']

    @Override
    public MaritalStatusRequest clone() {
        MaritalStatusRequest request = new MaritalStatusRequest()
        request = super.cloneRequest(request)
        request.relatedPersonId = this.relatedPersonId
        request.maritalStatusDate = this.maritalStatusDate
        request.newMaritalStatusId = this.newMaritalStatusId
        request.oldMaritalStatusId = this.oldMaritalStatusId
        request.personRelationShipId = this.personRelationShipId
        request.personMaritalStatusId = this.personMaritalStatusId
        request.isDependent = this.isDependent
        request.employee = this.employee
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        return request
    }

}
