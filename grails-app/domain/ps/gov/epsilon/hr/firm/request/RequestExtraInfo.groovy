package ps.gov.epsilon.hr.firm.request

import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

class RequestExtraInfo {

    String reason

    String managerialOrderNo
    ZonedDateTime managerialOrderDate

    // affects all parent levels or only direct parent, mainly used for cancel
    Boolean allLevels= false

    static belongsTo = [request:Request]

    static nullableValues = ['managerialOrderDate']

    static constraints = {
        reason Constants.DESCRIPTION_NULLABLE
        managerialOrderNo nullable: true
    }

    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]

        tablePerHierarchy false // <=> use separate table per subclass

        managerialOrderDate type: PersistentDocumentaryDate, {
            column name: 'managerial_order_date_datetime'
            column name: 'managerial_order_date_date_tz'
        }
    }
}
