package ps.gov.epsilon.hr.firm.suspension

import ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.utils.v1.PCPUtils
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the suspension request related to an employee
 * <h1>Usage</h1>
 * Used as to represents the suspension request related to employee. The suspension request may be due to medical,disciplinary,requested,..etc.
 * **/

class SuspensionRequest extends Request {

    static auditable = true

    Short periodInMonth

    //suspension type like medical,disciplinary,requested
    EnumSuspensionType suspensionType

    ZonedDateTime fromDate
    ZonedDateTime toDate


    static nullableValues = ['internalOrderDate','externalOrderDate']


    public SuspensionRequest() {
        requestType = EnumRequestType.SUSPENSION
    }


    static constraints = {
        suspensionType(nullable: false, validator: { value, object, errors ->
            if (value == EnumSuspensionType.DISCIPLINARY && !object.internalOrderNumber && !object.externalOrderNumber) {
                errors.reject('suspensionRequest.error.orderNo.null.message')
            }
            return true
        })
        periodInMonth(Constants.POSITIVE_SHORT + [min: Short.parseShort("1"), max: Short.parseShort("12")])

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {

        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }

        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
    }
}
