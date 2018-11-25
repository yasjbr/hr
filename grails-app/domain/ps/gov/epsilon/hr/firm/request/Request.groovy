package ps.gov.epsilon.hr.firm.request

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRequestCategory
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the all the request in the system
 * <h1>Usage</h1>
 * Used  as to represent the father class for all requests in the system.
 * **/

class Request {

    def messageSource
    RequestService requestService

    String id

    String encodedId

    //the request type like "VACATION_REQUEST "
    EnumRequestType requestType

    //todo add status history list for the request
    //the request status "CREATED, REJECTED ... "
    EnumRequestStatus requestStatus

    //ملاحظات على الطلب
    String requestStatusNote

    //Represent the actual request date
    ZonedDateTime requestDate

    Map transientData = [:]

    // general request information
    String requestReason
    /***
     * To keep history of the Employment Record  and military rank when this Disciplinary has been taken
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2)Entered from the Disciplinary module it will take the current employment Record of the employee
     *
     */
    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank

    //todo add current employee status for tracking

    /**
     * Use long instead of request to avoid automatic self join
     * and when we need to do the join the record will retrieve
     */
    //the direct parent request (if any). Used to group requests of same type
    String parentRequestId

    /**
     * id of the original request that this request belongs to
     * used for grouping all operational requests related to the same original request
     */
    String threadId

// لتوثيق البيانات في حال الطلب بالانابة
    Employee requester
    Department requesterDepartment
    EmploymentRecord currentRequesterEmploymentRecord

    TrackingInfo trackingInfo

    // used when request is not original
    RequestExtraInfo extraInfo

    //will be used by job to decide which requests need to be reflected, and which ones need to be reverted.
    Boolean changesApplied = false

    // Add internal order number and date issued by firm iteslf
    String internalOrderNumber

    ZonedDateTime internalOrderDate

    // Add external order number and date issued by external party
    String externalOrderNumber

    ZonedDateTime externalOrderDate

    static nullableValues = ['internalOrderDate', 'externalOrderDate']

    static belongsTo = [employee: Employee, firm: Firm]

    static embedded = ['trackingInfo']

    public Request() {
        requestType = EnumRequestType.GENERAL_REQUEST
        requestStatus = EnumRequestStatus.CREATED
        //TODO: revert this changes
//        requestStatus = EnumRequestStatus.APPROVED_BY_WORKFLOW
    }

    static constraints = {
        requestReason(Constants.DESCRIPTION_NULLABLE)
        parentRequestId(Constants.FIRM_PK_NULLABLE)
        requestStatusNote(Constants.DESCRIPTION_NULLABLE)
        requestType nullable: false
        requestStatus nullable: false

        employee nullable: true
        currentEmploymentRecord nullable: true
        currentEmployeeMilitaryRank nullable: true

//        currentEmploymentRecord nullable: false
//        currentEmployeeMilitaryRank nullable: false
        requester(nullable: true, widget: "autocomplete")
        currentRequesterEmploymentRecord(nullable: true, validator: { value, object, errors ->
            if (!value && object?.requester)
                errors.reject('Request.currentRequesterEmploymentRecord.error.required')
            return true
        })
        requesterDepartment(nullable: true, validator: { value, object, errors ->
            if (!value && object?.requester)
                errors.reject('Request.requesterDepartment.error.required')
            return true
        })
        trackingInfo nullable: true, display: false

        extraInfo nullable: true
        threadId(Constants.FIRM_PK_NULLABLE)

        changesApplied nullable: true

        internalOrderNumber nullable: true
        externalOrderNumber nullable: true
    }

    static mapping = {
        requestDate type: PersistentDocumentaryDate, {
            column name: 'request_date_datetime'
            column name: 'request_date_date_tz'
        }
        tablePerHierarchy false // <=> use separate table per subclass
        requestStatusNote type: "text"

        extraInfo cascade: 'all-delete-orphan'

        internalOrderDate type: PersistentDocumentaryDate, {
            column name: 'internal_order_date_datetime'
            column name: 'internal_order_date_date_tz'
        }

        externalOrderDate type: PersistentDocumentaryDate, {
            column name: 'external_order_date_datetime'
            column name: 'external_order_date_date_tz'
        }
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData', 'parentRequest', 'canHaveOperation', 'requestTypeValue',
                         'actualStartDate', 'actualEndDate', 'canCancelRequest', 'canStopRequest', 'canExtendRequest',
                         'canEditRequest', 'messageSource', 'requestTypeDescription', 'requestService', 'requestStatusValue']

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

    def afterInsert() {
        if (!threadId) {
            if (parentRequestId) {
                threadId = parentRequest?.threadId
            } else {
                threadId = id
            }
            this.save()
        }
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    Request getParentRequest() {
        return parentRequestId ? Request.get(parentRequestId) : null
    }

    /***
     * Clones request data to the new request
     * @param request
     * @return
     */
    protected Request cloneRequest(Request request) {

        request.requestType = this.requestType
        request.requestStatusNote = this.requestStatusNote
        request.requestReason = this.requestReason
        request.currentEmploymentRecord = this.currentEmploymentRecord
        request.currentEmployeeMilitaryRank = this.currentEmployeeMilitaryRank
        request.parentRequestId = this.parentRequestId
        request.threadId = this.threadId
        request.requester = this.requester
        request.requesterDepartment = this.requesterDepartment
        request.currentRequesterEmploymentRecord = this.currentRequesterEmploymentRecord
        request.employee = this.employee
        request.firm = this.firm
        request.requestStatus = EnumRequestStatus.CREATED
        request.requestDate = ZonedDateTime.now()

        return request
    }

    /**
     * Start date of request that will be reflected in employee profile
     * Must be overriden for requests that have fromDate
     * @return request actual from date
     */
    ZonedDateTime getActualStartDate() {
        return null
    }

    /**
     * End date of request that will be reflected in employee profile
     * Must be overriden for requests that have toDate
     * @return request actual to date
     */
    ZonedDateTime getActualEndDate() {
        return null
    }

    /**
     * validations required:
     * 1. request status in [approved, approved_by_workflow]
     * 2. No other operation exists for this request
     * @return true or false
     */
    Boolean getCanHaveOperation(List<EnumRequestStatus> statusList = [EnumRequestStatus.APPROVED]) {
        if (requestStatus in statusList) {
            int count = requestService.countChildRequests(id, [EnumRequestStatus.REJECTED, EnumRequestStatus.CANCELED])
            return count == 0
        }
        return false
    }

    /***
     * checks if request can be cancelled
     * @return true or false
     */
    Boolean getCanCancelRequest() {
        return requestType.requestCategory in [EnumRequestCategory.ORIGINAL, EnumRequestCategory.EDIT,
                                               EnumRequestCategory.STOP, EnumRequestCategory.EXTEND] &&
                getCanHaveOperation([EnumRequestStatus.APPROVED, EnumRequestStatus.APPROVED_BY_WORKFLOW])
    }

    /***
     * checks if request can be stopped
     * @return true or false
     */
    Boolean getCanStopRequest(){
        Boolean result= requestType.requestCategory in [EnumRequestCategory.ORIGINAL, EnumRequestCategory.EDIT,
                                                        EnumRequestCategory.EXTEND] && actualStartDate
        if(result){
            ZonedDateTime now= ZonedDateTime.now()
            if(now.isAfter(actualStartDate)){
                if(actualEndDate){
                    result= now.isBefore(actualEndDate)
                }
            }else{
                result= false
            }
        }
        return result && canHaveOperation
    }
/**
 * checks if request can be extended
 * Can be overriden for requests that have fromDate and toDate
 * @return
 */
    Boolean getCanExtendRequest(){
        Boolean result= requestType.requestCategory in [EnumRequestCategory.ORIGINAL, EnumRequestCategory.EDIT,
                                                        EnumRequestCategory.EXTEND] && actualStartDate && actualEndDate
        if(result){
            ZonedDateTime now= ZonedDateTime.now()
            if(now.isBefore(actualStartDate)){
                result= false
            }
        }
        return result && canHaveOperation
    }

    /**
     * Checks if request can be edited
     * Can be overriden for requests that have toDate
     * @return
     */
    Boolean getCanEditRequest() {
        Boolean result = requestType.requestCategory in [EnumRequestCategory.ORIGINAL, EnumRequestCategory.EDIT]
        if (result && actualEndDate) {
            ZonedDateTime now = ZonedDateTime.now()
            result = now.isBefore(actualEndDate)
        }
        result = result && canHaveOperation
        return result
    }

    /**
     * Checks if request is included in list
     * must be overriden for requests that have lists
     * @return
     */
    Boolean getIncludedInList() {
        return false
    }

    /**
     * Checks if user can set internal managerial order info for a request
     * @return
     */
    Boolean getCanSetOrderInfo() {
        return internalOrderNumber == null
    }

    /**
     * Checks if user can set External managerial order info for a request
     * @return boolean
     */
    Boolean getCanSetExternalOrderInfo() {
        return externalOrderNumber == null
    }

    String getRequestTypeDescription() {
        Locale ar = new Locale('ar')
        String message
        if (requestType.requestCategory == EnumRequestCategory.CANCEL) {
            if (!extraInfo.allLevels) {
                message = messageSource.getMessage('EnumRequestType.' + parentRequest?.requestType, null, requestType.toString(), ar)
                return messageSource.getMessage('request.cancelRequest.label', [message].toArray(), requestType.toString(), ar)
            }
        }
        message = messageSource.getMessage('EnumRequestType.' + requestType, null, requestType.toString(), ar)
        return message
    }

    /**
     * to return the request status without translation in domain columns
     * @return
     */
    String getRequestStatusValue(){
        return requestStatus?.name()
    }

    /**
     * to return the request type without translation in domain columns
     * @return
     */
    String getRequestTypeValue(){
        return requestType?.name()
    }
}
