package ps.gov.epsilon.hr.common.domains.v1

import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * Created by mkharma on 20/07/17.
 */
class ListNote {

    String id

    String orderNo
    String note
    ZonedDateTime noteDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        orderNo nullable: true
        note (Constants.DESCRIPTION_NULLABLE + [validator: { value, object,errors ->
            if (!value && !object?.orderNo)
                errors.reject('listNote.note.required.error')
            return true
        }])
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        noteDate type: PersistentDocumentaryDate, {
            column name: 'note_date_datetime'
            column name: 'note_date_date_tz'
        }
        note type: 'text'
    }
}
