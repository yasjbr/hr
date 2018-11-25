package ps.gov.epsilon.hr.firm.transfer

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.Province
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the employee transfer request from his/her firm to an external firm.
 * <h1>Usage</h1>
 * Used as to represents the external transfer request related to an employee from the firm belongs to to an external firm.
 * **/

class ExternalTransferRequest extends Request {

    static auditable = true

    //NEED TO SET THE TODATE OF THE CURRENT EMPLOYMENT RECORD = EFFECTIVE DATE
    EmploymentRecord currentEmploymentRecord
    Long toOrganizationId
    ZonedDateTime effectiveDate

    //When the request become approved by the external list user need to apply the clearance and transfer permision info. to reflect
    //the new status on the employee profile ()

    //خلو طرف
    Boolean hasClearance = false
    String clearanceOrderNo
    ZonedDateTime clearanceDate
    String clearanceNote

    //امر التسيير
    Boolean hasTransferPermission = false
    String transferPermissionOrderNo
    ZonedDateTime transferPermissionDate
    String transferPermissionNote

    Province fromProvince
    Province toProvince
    Firm fromFirm


    public ExternalTransferRequest() {
        requestType = EnumRequestType.EXTERNAL_TRANSFER_REQUEST
    }
    static nullableValues = ['effectiveDate', 'clearanceDate', 'transferPermissionDate', 'internalOrderDate', 'externalOrderDate']

    static constraints = {
        clearanceOrderNo(Constants.STRING_NULLABLE)
        transferPermissionOrderNo(Constants.STRING_NULLABLE)
        transferPermissionNote(Constants.DESCRIPTION_NULLABLE)
        clearanceNote(Constants.DESCRIPTION_NULLABLE)
        fromProvince nullable: true
        toProvince nullable: true
        toOrganizationId(Constants.POSITIVE_LONG)
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false

    }

    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }

        transferPermissionDate type: PersistentDocumentaryDate, {
            column name: 'transfer_Permission_date_datetime'
            column name: 'transfer_Permission_date_date_tz'
        }
        clearanceDate type: PersistentDocumentaryDate, {
            column name: 'clearance_date_datetime'
            column name: 'clearance_date_date_tz'
        }
    }
}
