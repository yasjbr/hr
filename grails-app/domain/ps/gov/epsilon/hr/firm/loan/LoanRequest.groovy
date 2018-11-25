package ps.gov.epsilon.hr.firm.loan

import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.lookups.Job
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the loan requests
 * <h1>Usage</h1>
 * Used as to represents the firm requests to loan some efficient employee from another firm to work with them for specific period.
 * **/

// يمثل الجهاز الطالب للانتداب
class LoanRequest extends Request {

    static auditable = true

////الشخص المطلوب للانتداب
//    Long requestedPersonId

    Long requestedFromOrganizationId

    String requestedJobTitle

    Job requestedJob

    ZonedDateTime fromDate
    ZonedDateTime toDate

    Department toDepartment

//    MilitaryRank requestedMilitaryRank

    //calculated
    Short periodInMonths

    Short numberOfPositions

    String description

    static nullableValues = ['toDate','internalOrderDate','externalOrderDate']

    static hasMany = [loanRequestRelatedPersons: LoanRequestRelatedPerson]

    static constraints = {
        employee nullable: true
        currentEmploymentRecord nullable: true
        currentEmployeeMilitaryRank nullable: true


        toDepartment nullable: true, widget: "autocomplete"
//        requestedMilitaryRank nullable: true,widget:"autocomplete"
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea", blank: false])

//        requestedPersonId(Constants.POSITIVE_LONG_NULLABLE)
        requestedFromOrganizationId(Constants.POSITIVE_LONG_NULLABLE)
        requestedJobTitle(Constants.STRING_NULLABLE + [maxSize: Constants.getSTRING_MAX_SIZE()])
        requestedJob nullable: false
        periodInMonths(Constants.POSITIVE_SHORT)
        numberOfPositions(Constants.POSITIVE_SHORT)
    }

    public LoanRequest() {
        requestType = EnumRequestType.LOAN_REQUEST
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
    }
}
