package ps.gov.epsilon.hr.firm.transfer

import org.aspectj.apache.bcel.classfile.Constant
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime


/**
 * <h1>Purpose</h1>
 * To hold the employee transfer request internally inside the firm belongs to.
 * <h1>Usage</h1>
 * Used as to represents the internal transfer request related to an employee inside the firm
 * **/

class InternalTransferRequest extends Request {

    static auditable = true

    EmploymentRecord currentEmploymentRecord


    /*
    new employment record information
    we don't use employment record object becausmee the from date is filled when request approve
    */
    Department department
    EmploymentCategory employmentCategory
    JobTitle jobTitle
    ZonedDateTime effectiveDate

    Employee alternativeEmployee

    static nullableValues = ['effectiveDate','internalOrderDate','externalOrderDate']

    public InternalTransferRequest() {
        requestType = EnumRequestType.INTERNAL_TRANSFER_REQUEST
    }

    static constraints = {
        alternativeEmployee nullable: true, widget: "autocomplete"
        currentEmploymentRecord nullable: false
        jobTitle nullable: true
        employee nullable: false
        currentEmployeeMilitaryRank nullable: false
    }


    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
    }
}
