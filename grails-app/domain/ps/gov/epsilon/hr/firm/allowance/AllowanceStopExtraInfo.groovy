package ps.gov.epsilon.hr.firm.allowance

import ps.gov.epsilon.hr.firm.allowance.lookups.AllowanceStopReason
import ps.gov.epsilon.hr.firm.request.RequestExtraInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

class AllowanceStopExtraInfo extends RequestExtraInfo{

    //why the employee stopped his/her allowance
    AllowanceStopReason allowanceStopReason;

    //if the employee stopped his/her allowance based on his request
    Boolean stoppedByEmployee;

    static constraints = {
        stoppedByEmployee(nullable: true)
        allowanceStopReason(nullable: false)
    }
}
