package ps.gov.epsilon.hr.firm.disciplinary

import grails.util.Holders
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate
import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the employee violation record in the firm
 * <h1>Usage</h1>
 * Used  as to represents all employee violation instances.
 * **/

class EmployeeViolation {

    static auditable = true

    String id

    String encodedId

    // nullable true
    ZonedDateTime violationDate;

    Long locationId
    //external information to describe the structured location
    String unstructuredLocation

    String note

    //nullable true, تاريخ التبليغ
    ZonedDateTime noticeDate

    // مصدر التبليغ
    Employee  informer

    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank


    EnumViolationStatus violationStatus=EnumViolationStatus.NEW

    TrackingInfo trackingInfo


    Map transientData = [:]


    static belongsTo = [employee:Employee, disciplinaryReason:DisciplinaryReason, firm:Firm]

    static hasMany = [joinedDisciplinaryEmployeeViolations:JoinedDisciplinaryEmployeeViolation]

    static nullableValues = ['violationDate']

    static embedded = ['trackingInfo']

    static constraints = {
        locationId(Constants.POSITIVE_LONG_NULLABLE)
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE + [widget: "textarea" ,blank: false])
        informer nullable: true,widget:"autocomplete"

        currentEmploymentRecord widget:"autocomplete"
        currentEmployeeMilitaryRank widget:"autocomplete"
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        violationDate type: PersistentDocumentaryDate, {
            column name: 'violation_date_datetime'
            column name: 'violation_date_date_tz'
        }

        noticeDate type: PersistentDocumentaryDate, {
            column name: 'notice_date_datetime'
            column name: 'notice_date_date_tz'
        }

        tablePerHierarchy false // <=> use separate table per subclass
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