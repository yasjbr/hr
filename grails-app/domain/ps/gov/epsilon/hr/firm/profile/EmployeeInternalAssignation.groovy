package ps.gov.epsilon.hr.firm.profile

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Department
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *     // الفرز لاي دائرة
 */
class EmployeeInternalAssignation {

    String id

    String encodedId
/**
 *     // الفرز لاي دائرة
 */
    Department assignedToDepartment
    //nullable: true
    ZonedDateTime assignedToDepartmentFromDate
    ZonedDateTime assignedToDepartmentToDate

    String note

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [employmentRecord:EmploymentRecord]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']


    static nullableValues = ['assignedToDepartmentToDate']

    static constraints = {
        assignedToDepartment nullable: true,widget:"autocomplete"
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        trackingInfo nullable: true, display: false
    }

    static mapping = {
        assignedToDepartmentFromDate type: PersistentDocumentaryDate, {
            column name: 'assigned_to_department_from_date_datetime'
            column name: 'assigned_to_department_from_date_date_tz'
        }

        assignedToDepartmentToDate type: PersistentDocumentaryDate, {
            column name: 'assigned_to_department_to_date_datetime'
            column name: 'assigned_to_department_to_date_date_tz'
        }

    }

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
