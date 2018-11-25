package ps.gov.epsilon.hr.firm.request

import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the requests that cancelled from his/her applicant
 * <h1>Usage</h1>
 * Used  as to represents the requests that his/her applicant canceled it.
 *<h1>Example</h1>
 * An employee requested an external vacation and before this vacation occurred he requested to cancel it.
 * **/

class RequestCancellation extends Request{

    ZonedDateTime cancellationDate

    String requestedBy

    static belongsTo = [requestToCancel:Request]

    static constraints = {
        requestedBy (Constants.NAME_NULLABLE)

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {
        cancellationDate type: PersistentDocumentaryDate, {
            column name: 'cancellation_date_datetime'
            column name: 'cancellation_date_date_tz'
        }
    }
}
