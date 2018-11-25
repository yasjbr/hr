package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.Job
import ps.gov.epsilon.hr.firm.lookups.JobType
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the job requisition information from the department in the firm belongs to recruitment cycle
 * <h1>Usage</h1>
 * Used  as to represents the information of job requisition
 * **/

class JobRequisition {

    String id

    static auditable = true

    String encodedId

    ZonedDateTime requestDate

    // USED TO DETERMINE THE ACTUAL OR EXPECTED DATE THAT THE DEPARTMENT WILL NEED THIS POSITION TO BE OCCUPIED

    ZonedDateTime fulfillFromDate
    ZonedDateTime fulfillToDate


    Job job

    JobType jobType

    MilitaryRank proposedRank


    //the position location
    Integer numberOfPositions
    //this value reflect the # of positions that the manager approved and it must be = to the vacancy numberOfPositions in case the
    // vacancy created from Job Requisition and belong to it
    Integer numberOfApprovedPositions

    Short fromAge
    Short toAge

    Float fromHeight
    Float toHeight

    Float fromWeight
    Float toWeight

    // we discard the relation with the unit of measurement, and use only cm,kg
    String jobDescription
    String note

    //سبب الرفض في حال كان موجود
    String rejectionReason

    EnumSexAccepted sexTypeAccepted

    Long maritalStatusId

    EnumRequestStatus requisitionStatus

    Map transientData=[:]
    //todo the workflow will be part of the transaction table to handle the approval and rejection and rejection reason.


    //used to support the on behalf users and operations as HR work on behalf any department
    Department requestedByDepartment
    static belongsTo = [requestedForDepartment:Department,recruitmentCycle:RecruitmentCycle,firm:Firm,employmentCategory:EmploymentCategory]

    static hasMany = [fromGovernorates    :Long, governorates:Long, educationDegrees:Long, educationMajors:Long,
                      requisitionWorkExperiences:RequisitionWorkExperience,
                      inspectionCategories:InspectionCategory]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static nullableValues = ['fulfillFromDate','fulfillToDate']

    static constraints = {
        rejectionReason (Constants.DESCRIPTION_NULLABLE+[ widget: "textarea"])
        recruitmentCycle nullable: true,widget:"autocomplete"
        proposedRank nullable: true,widget:"autocomplete"
        jobDescription(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        note(Constants.DESCRIPTION_NULLABLE+[ widget: "textarea"])
        sexTypeAccepted nullable: true
        fromAge(nullable: true)
        toAge(nullable: true, validator: {val, obj ->
            return val >= obj.fromAge
        })
        fromHeight nullable: true
        toHeight(nullable: true, validator: {val, obj ->
            return val >= obj.fromHeight
        })
        fromWeight nullable: true
        toWeight(nullable: true, validator: {val, obj ->
            return val >= obj.fromWeight
        })
        job nullable: false, widget:"autocomplete"
        jobType nullable: false, widget:"autocomplete"
        numberOfPositions(Constants.POSITIVE_INTEGER)
        maritalStatusId(Constants.POSITIVE_LONG_NULLABLE)
        requisitionStatus nullable: false
        numberOfApprovedPositions(Constants.POSITIVE_INTEGER_NULLABLE)
        requestedByDepartment nullable: true,widget:"autocomplete"
        trackingInfo nullable: true
    }

    static mapping = {
        fulfillFromDate type: PersistentDocumentaryDate, {
            column name: 'fulfill_from_date_datetime'
            column name: 'fulfill_from_date_date_tz'
        }
        fulfillToDate type: PersistentDocumentaryDate, {
            column name: 'fulfill_to_date_datetime'
            column name: 'fulfill_to_date_date_tz'
        }
        requestDate type: PersistentDocumentaryDate, {
            column name: 'request_Date_datetime'
            column name: 'request_Date_date_tz'
        }
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
