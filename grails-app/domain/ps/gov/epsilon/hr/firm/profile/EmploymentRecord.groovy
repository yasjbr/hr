package ps.gov.epsilon.hr.firm.profile

import grails.gorm.DetachedCriteria
import grails.util.Holders
import org.hibernate.FlushMode
import ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.Province
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.common.utils.v1.PCPUtils
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/***
 * If any of the following fields changed we need to add
 * new record in the database to present this change
 * (assignedToDepartmentId,departmentId,jobTitle,militaryId,managerialRankId)
 *
 * */

/**
 * <h1>Purpose</h1>
 * To hold the employment record of the employee
 * <h1>Usage</h1>
 * Used  as to represents the employment record of the employee
 * **/

class EmploymentRecord {

    static auditable = true

    String id

    String encodedId
    // department join date
    ZonedDateTime fromDate;
    /*
    //todo must close the end date in case employee leave the department as a result of
   // todo movement to another department or retirement ..etc.
     // nullable: true
     */
    ZonedDateTime toDate;

    Department department

    EmploymentCategory employmentCategory

    EmploymentRecord previousEmploymentRecords

    JobTitle jobTitle
    String jobDescription
    String note


    // Add internal order number and date issued by firm iteslf
    String internalOrderNumber
    ZonedDateTime internalOrderDate

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [employee: Employee, firm: Firm, province: Province]

    static hasMany = [employeeInternalAssignations: EmployeeInternalAssignation,
                      employeeExternalAssignations: EmployeeExternalAssignation]

    static nullableValues = ['toDate','internalOrderDate']

    static constraints = {
        internalOrderNumber nullable: true
        department nullable: false, widget: "autocomplete"
        employmentCategory nullable: false, widget: "autocomplete"
        jobTitle(nullable: true, widget: "autocomplete", validator: { value, object, errors ->
            if (value && !object?.jobTitle?.allowToRepeetInUnit) {
                //get value of uncommitted status
                EmployeeStatusCategory employeeStatusCategory = EmployeeStatusCategory.load(EnumEmployeeStatusCategory.COMMITTED.value)
                //check not added for same department if it's not allowed to add
                int count = 0
                EmploymentRecord.withNewSession { session ->
                    DetachedCriteria<EmploymentRecord> detachedCriteria = EmploymentRecord.where {
                        jobTitle == object?.jobTitle &&
                                department == object?.department &&
                                employee.categoryStatus == employeeStatusCategory &&
                                toDate == PCPUtils.DEFAULT_ZONED_DATE_TIME
                    }
                    count = detachedCriteria.where {
                        if (object?.id) {
                            id != object?.id
                        }
                    }.count()
                }
                if (count > 0) {
                    errors.reject('jobTitle.allowToRepeetInUnit.error')
                }
            }
            return true
        })
        jobDescription(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        note(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        trackingInfo nullable: true, display: false
        province nullable: true
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'previousEmploymentRecords', 'transientData']

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
        internalOrderDate type: PersistentDocumentaryDate, {
            column name: 'internal_order_date_datetime'
            column name: 'internal_order_date_date_tz'
        }
        note type: "text"
    }

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

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    @Override
    String toString() {
        return this?.employmentCategory?.toString() + " - " + this?.department?.toString()
    }
}
