package ps.gov.epsilon.hr.firm.transfer

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the information about the transferred person in the firm that received the employee
 * <h1>Usage</h1>
 * Used as to represents the transferred person and his information in the firm that received those employee
 * **/

//todo add reference in the employee if he is created from external transfer
class ExternalReceivedTransferredPerson {

    String id

    String encodedId
    //الشخص الذي تم نقله الى الجهاز الاخر
    Long personId

    //المؤسسة التي انتقل منها الشخص
    Long fromOrganizationId

    //المؤسسة النتى انتقل اليها الشخص
    Department toDepartment

    //تاريخ النقل
    ZonedDateTime effectiveDate

    // الامر الاداري
    String orderNo

    String note

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static belongsTo = [firm:Firm]

    static nullableValues = ['effectiveDate']

    static constraints = {
        toDepartment nullable: true,widget:"autocomplete"
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        orderNo (Constants.STRING_NULLABLE)
        personId(Constants.POSITIVE_LONG)
        fromOrganizationId(Constants.POSITIVE_LONG)
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        effectiveDate type: PersistentDocumentaryDate, {
            column name: 'effective_date_datetime'
            column name: 'effective_date_date_tz'
        }
        note type: "text"
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId','transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username?:applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"
    }

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
