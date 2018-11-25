package ps.gov.epsilon.hr.firm.request

import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

class RequestExtendExtraInfo extends RequestExtraInfo{

    //fromDate taken from original request
    ZonedDateTime fromDate

    static constraints = {
    }

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
    }
}
