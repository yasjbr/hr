package ps.gov.epsilon.hr.firm.vacation

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.request.BordersSecurityCoordination
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.request.RequestExtendExtraInfo
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the vacation requests
 * <h1>Usage</h1>
 * Used as to represents the vacation requests
 * **/

//todo دراسة اشعارات العودة بتفاصيلها
class VacationRequest extends Request {

    static auditable = true

    VacationType vacationType

    //vacation start date
    ZonedDateTime fromDate
    //vacation end date
    ZonedDateTime toDate

    //the number of vacation days (vacation duration)
    // calculated, it will used to calculate todate or it will calculated by period (from date, to date)
    Integer numOfDays

    //اعتمادا على اشعار العودة
    ZonedDateTime returnDate  // The actual date the employee returned back to work. Helpful for absence Management.

    //if the vacation is internal or external ( by default each vacation is internal)
    Boolean external = false

    //represent the vacation balance for the vacation type without take current request period and set after approval
    // in the screen before the approval should be calculated
    Double currentBalance

    //if the vacation is stopped
    Boolean isStopped

    //the employee who stopped the vacation (may be the requester, the HR, the employee direct manager ... "
    Employee stoppedBy

    ZonedDateTime expectedReturnDate  // Represent the to date in addition to any extension applied on this vacation.

    //internal created after approval on the external vacation
    BordersSecurityCoordination securityCoordination

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    //need to distinguish between the permanent, and temporary  addresses and the addresses that we used in the vacation in the core depends on
    // the contact info type
    static hasMany = [contactInfos: Long]

    public VacationRequest() {
        requestType = EnumRequestType.VACATION_REQUEST;
        isStopped = false;
    }

    static constraints = {
        isStopped(nullable: true)
        stoppedBy nullable: true, widget: "autocomplete"
        securityCoordination nullable: true, widget: "autocomplete"
        vacationType nullable: false, widget: "autocomplete"
        numOfDays(Constants.POSITIVE_INTEGER)
        currentBalance(Constants.POSITIVE_DOUBLE)
        trackingInfo nullable: true, display: false
//        fromDate(nullable: false)
//        toDate(nullable: false, validator: {val, obj ->
//            if (val == null || obj.fromDate == null)// if any of the fromDate or toDate is null , the constraint passes.
//                return true
//            return val.isAfterOrEqual(obj.fromDate) //  toDate >= fromDate
//        })
//        returnDate(nullable: true, validator: {val, obj ->
//            if (val == null || obj.toDate == null)// if any of the fromDate or toDate is null , the constraint passes.
//                return true
//            return val.isAfterOrEqual(obj.fromDate)   //  returnDate >= fromDate
//        })

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
        returnDate type: PersistentDocumentaryDate, {
            column name: 'return_date_datetime'
            column name: 'return_date_date_tz'
        }

        expectedReturnDate type: PersistentDocumentaryDate, {
            column name: 'expected_return_date_datetime'
            column name: 'expected_return_date_date_tz'
        }
    }

    transient springSecurityService

    static transients = ['springSecurityService']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    @Override
    Boolean getIncludedInList() {
        def count = VacationListEmployee.createCriteria().get {
            eq('vacationRequest.id', id)
            projections {
                count('id')
            }
        }
        return count > 0
    }

    @Override
    public VacationRequest clone() {
        VacationRequest request = new VacationRequest()
        request = super.cloneRequest(request)
        request.vacationType = this.vacationType
        request.fromDate = this.fromDate
        request.toDate = this.toDate
        request.numOfDays = this.numOfDays
        request.returnDate = this.returnDate
        request.external = this.external
        request.currentBalance = this.currentBalance
        request.isStopped = this.isStopped
        request.stoppedBy = this.stoppedBy
        request.expectedReturnDate = this.expectedReturnDate
        request.securityCoordination = this.securityCoordination
        return request
    }

    @Override
    ZonedDateTime getActualStartDate() {
        if (requestType != EnumRequestType.REQUEST_FOR_VACATION_EXTENSION) {
            return fromDate
        }
        RequestExtendExtraInfo extendInfo = (RequestExtendExtraInfo) extraInfo
        return extendInfo?.fromDate
    }

    @Override
    ZonedDateTime getActualEndDate() {
        return toDate
    }


    @Override
    Boolean getCanStopRequest() {
        Boolean result = requestType.requestCategory in [EnumRequestCategory.ORIGINAL, EnumRequestCategory.EDIT,
                                                         EnumRequestCategory.EXTEND] && actualStartDate
        if (result) {
            ZonedDateTime now = ZonedDateTime.now()
            if (now.isAfter(actualStartDate)) {
                if (actualEndDate) {
                    result = true
                }
            }
        }
        return result && canHaveOperation
    }

    @Override
    Boolean getCanExtendRequest() {
        Boolean result = requestType.requestCategory in [EnumRequestCategory.ORIGINAL, EnumRequestCategory.EDIT,
                                                         EnumRequestCategory.EXTEND] && actualStartDate && actualEndDate
        if (result) {
            ZonedDateTime now = ZonedDateTime.now()
            if (now.isAfter(actualStartDate)) {
                result = true
            }
        }
        return result && canHaveOperation
    }

}
