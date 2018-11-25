package ps.gov.epsilon.hr.firm.allowance

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceType
import ps.gov.epsilon.hr.firm.request.RequestExtendExtraInfo
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the allowances requests
 * <h1>Usage</h1>
 * Used as to represents the allowances requests that can be requested by an employee.
 * <h1>Example</h1>
 * when an employee is sent to a training course outside then he will given an allowance.
 * **/

//add child and change Marital status created automatically from add child list and Marital Status List
class AllowanceRequest extends Request {

    static auditable = true

    // the allowance type like ALLOWANCE_OF_TRAVEL
    AllowanceType allowanceType

    //when the employee started to deserve the allowance : fromDate
    ZonedDateTime effectiveDate

    ZonedDateTime toDate

    // PersonRelationShips from core
    Long personRelationShipsId

    AllowanceRequest() {
        requestType = EnumRequestType.ALLOWANCE_REQUEST
    }

    static nullableValues = ['toDate','internalOrderDate','externalOrderDate']

    static transients = ['finalStartedDate', 'finalEndDate', 'canCancelRequest', 'canStopRequest', 'canExtendRequest', 'canEditRequest']

    static constraints = {
        allowanceType nullable: false, widget: "autocomplete"
        personRelationShipsId nullable: true
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }

        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
    }

    @Override
    public AllowanceRequest clone(){
        AllowanceRequest request= new AllowanceRequest()

        request= super.cloneRequest(request)

        request.allowanceType= this.allowanceType
        request.effectiveDate= this.effectiveDate
        request.toDate= this.toDate
        request.personRelationShipsId= this.personRelationShipsId

        return request
    }

    @Override
    ZonedDateTime getActualStartDate(){
        if(requestType != EnumRequestType.ALLOWANCE_CONTINUE_REQUEST){
            return effectiveDate
        }
        RequestExtendExtraInfo extendInfo= (RequestExtendExtraInfo)extraInfo
        return extendInfo?.fromDate
    }

    @Override
    ZonedDateTime getActualEndDate(){
        return toDate
    }

    /**
     * Checks if request is included in list
     * @return
     */
    Boolean getIncludedInList(){
        def count= AllowanceListEmployee.createCriteria().get {
            eq('allowanceRequest.id', id)
            projections{
                count('id')
            }
        }
        return count > 0
    }
}
