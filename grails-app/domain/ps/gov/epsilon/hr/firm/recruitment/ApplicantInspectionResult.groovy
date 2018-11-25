package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.lookups.Inspection
import ps.police.common.utils.v1.HashHelper
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the applicant inspection result related to inspection category
 * <h1>Usage</h1>
 * Used  as to represents the results of applicant related to inspection
 *<h1>Example</h1>
 * NEW, REQUESTED_BY_APPLICANT, ACCEPTED,...etc.
 * **/

class ApplicantInspectionResult {

    String encodedId

    String id

    //nullable true
    ZonedDateTime receiveDate
    ZonedDateTime sendDate

    String resultValue
    //الوقت الذي استغرقه لانهاء الفحص او التمرين
    String executionPeriod

    String resultSummary

    String mark

    /**
     * Optional Organization that made the inspection and set the result
     * Remoting value from core organization
     */
    Long coreOrganizationId

    static belongsTo = [inspectionCategoryResult:ApplicantInspectionCategoryResult,inspection:Inspection]

    static hasMany = [committeeRoles:JoinedInspectionResultCommitteeRole]

    static nullableValues = ['sendDate','receiveDate']

    static transients = ['encodedId']

    static constraints = {
        mark nullable: true
        resultValue nullable: true, blank: false, size: 0..250
        resultSummary ([nullable: true]+ [blank: false]+ [widget: "textarea"])
        executionPeriod nullable: true
        coreOrganizationId nullable: true
    }

    static mapping = {
        receiveDate type: PersistentDocumentaryDate, {
            column name: 'receive_date_datetime'
            column name: 'receive_date_date_tz'
        }
        sendDate type: PersistentDocumentaryDate, {
            column name: 'send_date_datetime'
            column name: 'send_date_date_tz'
        }
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
