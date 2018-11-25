package ps.gov.epsilon.hr.firm.loan

import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the loan requests of a nominated person from the firm which will loan to another firm. To get the approval from the manager of the firm
 * <h1>Usage</h1>
 * Used as to represents the loan requests of a nominated person from the firm that will loan another firm to fet the manager approval from the this firm before sent this reply to Saraya
 * **/

// يقوم الجهاز المطلوب منه الانتداب بادخال طلب انتداب للموظف المطلوب  او المراد ترشيحه ليقوم مدير الجهاز بالموافقه قبل ارساله للهيئه
class LoanNoticeReplayRequest extends Request{

    static auditable = true

    //represent the source of the loan request
    Long requestedByOrganizationId

    ZonedDateTime fromDate
    ZonedDateTime toDate

    //calculated
    Short periodInMonths

    String description

    //nullable = true
    ZonedDateTime effectiveDate

    static belongsTo = [loanNotice:LoanNotice]

    static nullableValues = ['effectiveDate','internalOrderDate','externalOrderDate']

    static constraints = {
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea",blank: false])
        requestedByOrganizationId(Constants.POSITIVE_LONG)
        periodInMonths(Constants.POSITIVE_SHORT)

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    public LoanNoticeReplayRequest() {
        requestType = EnumRequestType.LOAN_NOTICE_REPLAY_REQUEST
        requestStatus = EnumRequestStatus.CREATED
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
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }

        description type: "text"
    }
}
