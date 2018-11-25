package ps.gov.epsilon.hr.firm.dispatch

import ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the all dispatch requests, and this request will added automatic to the open dispatch list after this request get final approval
 * <h1>Usage</h1>
 * Used as to represents the dispatch requests.
 **/
class DispatchRequest extends Request {

    static auditable = true

    EnumDispatchType dispatchType

    Short periodInMonths

    ZonedDateTime fromDate
    ZonedDateTime toDate

    //يمثل الطلبات الخاصة بالموافقة على التجديد السنوي
    // nullable true if period less than a year
    // and represent the current verification process (DispatchVerification) planned date
    ZonedDateTime nextVerificationDate


    //In case the organizationId is not null the organizationName will be the same of it (اسم المؤسسة الموفد عليها)
    Long organizationId
    String organizationName;

    Long educationMajorId
    String educationMajorName

    String trainingName

    Long locationId
    //external information to describe the structured location
    String unstructuredLocation

    DispatchRequest() {
        requestType = EnumRequestType.DISPATCH_REQUEST
    }

    static hasMany = [dispatchVerifications:DispatchVerification]

    static nullableValues = ['nextVerificationDate', 'internalOrderDate', 'externalOrderDate']

    static transients = ['finalStartedDate', 'finalEndDate', 'canCancelRequest', 'canStopRequest', 'canExtendRequest', 'canEditRequest']


    static constraints = {
        organizationId(Constants.POSITIVE_LONG_NULLABLE)
        organizationName (Constants.NAME_NULLABLE)
        educationMajorId (Constants.POSITIVE_LONG_NULLABLE)
        educationMajorName (Constants.NAME_NULLABLE)
        trainingName (Constants.LOOKUP_NAME_NULLABLE)
        locationId (Constants.POSITIVE_LONG_NULLABLE)
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"]+[blank: false])
        periodInMonths(Constants.POSITIVE_SHORT_NULLABLE)
        dispatchType nullable: false
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
        nextVerificationDate type: PersistentDocumentaryDate, {
            column name: 'next_verification_date_datetime'
            column name: 'next_verification_date_date_tz'
        }
    }

    @Override
    Boolean getIncludedInList() {
        def count = DispatchListEmployee.createCriteria().get {
            eq('dispatchRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    @Override
    public DispatchRequest clone() {
        DispatchRequest request = new DispatchRequest()
        request = super.cloneRequest(request)
        request.dispatchType = this.dispatchType
        request.fromDate = this.fromDate
        request.toDate = this.toDate
        request.periodInMonths = this.periodInMonths
        request.nextVerificationDate = this.nextVerificationDate
        request.organizationId = this.organizationId
        request.organizationName = this.organizationName
        request.educationMajorId = this.educationMajorId
        request.educationMajorName = this.educationMajorName
        request.trainingName = this.trainingName
        request.locationId = this.locationId
        request.unstructuredLocation = this.unstructuredLocation
        request.employee = this.employee
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        return request
    }

    @Override
    ZonedDateTime getActualStartDate() {
        return fromDate
    }

    @Override
    ZonedDateTime getActualEndDate() {
        return toDate
    }

}
