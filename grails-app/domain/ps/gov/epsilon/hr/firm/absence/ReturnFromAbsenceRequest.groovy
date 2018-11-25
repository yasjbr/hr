package ps.gov.epsilon.hr.firm.absence

import grails.util.Holders
import ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the employee return from absence notice
 * <h1>Usage</h1>
 * Used  as to announce that the employee is returned back from his previous absence
 *
 **/
class ReturnFromAbsenceRequest extends Request{

    static auditable = true

    //The actual date, when the employee return to work from absence
    ZonedDateTime actualReturnDate

    //The actual reason why the employee became absent. For example : vacation, medical, arrest
    EnumAbsenceReason actualAbsenceReason

    static belongsTo = [absence:Absence]

    public ReturnFromAbsenceRequest() {
        requestType = EnumRequestType.RETURN_FROM_ABSENCE_REQUEST
    }

    static constraints = {
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {
        actualReturnDate type: PersistentDocumentaryDate, {
            column name: 'actual_return_date_datetime'
            column name: 'actual_return_date_date_tz'
        }
    }



    @Override
    Boolean getIncludedInList() {
        def count = ReturnFromAbsenceListEmployee.createCriteria().get {
            eq('returnFromAbsenceRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    static transients = ['canCancelRequest', 'canEditRequest']

    @Override
    public ReturnFromAbsenceRequest clone() {
        ReturnFromAbsenceRequest request = new ReturnFromAbsenceRequest()
        request = super.cloneRequest(request)
        request.actualReturnDate = this.actualReturnDate
        request.actualAbsenceReason = this.actualAbsenceReason
        request.absence = this.absence
        request.employee = this.employee
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        return request
    }
}
