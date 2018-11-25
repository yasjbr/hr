package ps.gov.epsilon.hr.firm.employmentService

import ps.gov.epsilon.hr.firm.employmentService.lookups.ServiceActionReason
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime


//في حالة التقاعد يتم انشاء طلبات للعسكريين المرشحين للتقاعد قبل 90 يوم من تاريخ التقاعد
//بشكل تلقائي

//this request added automatic to the open list after request final approval

//Recall For Service Request use this request
/**
 *<h1>Purpose</h1>
 * To hold the employee requests
 * <h1>Usage</h1>
 * Used  as to represents the employee requests
 *<h1>Example</h1>
 * نهاية خدمة , اعادة للخدمة
 * **/

class EmploymentServiceRequest extends Request{

    static auditable = true

    // nullable true
    ZonedDateTime expectedDateEffective

    ServiceActionReason serviceActionReason

    static nullableValues = ['expectedDateEffective','internalOrderDate','externalOrderDate']

    static constraints = {
        serviceActionReason nullable: false,widget:"autocomplete"
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {
        expectedDateEffective type: PersistentDocumentaryDate, {
            column name: 'expected_date_effective_datetime'
            column name: 'expected_date_effective_date_tz'
        }
    }

    @Override
    Boolean getIncludedInList() {
        def count = ServiceListEmployee.createCriteria().get {
            eq('employmentServiceRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    static transients = ['canCancelRequest', 'canEditRequest']

    @Override
    public EmploymentServiceRequest clone() {
        EmploymentServiceRequest request = new EmploymentServiceRequest()
        request = super.cloneRequest(request)
        request.expectedDateEffective = this.expectedDateEffective
        request.serviceActionReason = this.serviceActionReason
        request.employee = this.employee
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        return request
    }
}
