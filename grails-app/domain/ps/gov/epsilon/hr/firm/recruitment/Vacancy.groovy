package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted
import ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.Job
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.JobType
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * The vacancy source may be copied from job requisition or belong to it, or independent (Direct)
 *
 * **/

/**
 *<h1>Purpose</h1>
 * To hold the vacancy job information
 * <h1>Usage</h1>
 * Used  as to represents the information af vacancy job
 * **/

class Vacancy  {

    static auditable = true

    String id

    String encodedId

    ZonedDateTime fulfillFromDate
    ZonedDateTime fulfillToDate

    Job job

    JobType jobType

    MilitaryRank proposedRank

    Long numberOfPositions


    Short fromAge
    Short toAge

    Float fromTall
    Float toTall

    Float fromWeight
    Float toWeight


    // we discard the relation with the unit of measurement, and use only cm,kg
    String jobDescription
    String note

    EnumVacancyStatus vacancyStatus

    EnumSexAccepted sexTypeAccepted

    Long maritalStatusId

    //todo the workflow will be part of the transaction table to handle the approval and rejection and rejection reason.
    //TODO vacancy can be reannounced when status is posted or not-occupied
    //used to support the on behalf users and operations as HR work on behalf any department
    Department requestedByDepartment

    Map transientData=[:]
    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [requestedForDepartment:Department,recruitmentCycle:RecruitmentCycle,firm:Firm]

    static hasMany = [fromGovernorates:Long,governorates:Long,
                      educationDegrees:Long,
                      educationMajors:Long,requisitionWorkExperiences:RequisitionWorkExperience,
                      inspectionCategories:InspectionCategory]

    static nullableValues = ['fulfillFromDate','fulfillToDate']

    static constraints = {
        //in case we did not fill requestedForDepartment this mean we can request the vacancy for more than one department
        // example 5 drivers for the PCP or we will entered 5 vacancies
        requestedForDepartment nullable: true,widget:"autocomplete"
        recruitmentCycle nullable: true,widget:"autocomplete"
        proposedRank nullable: true,widget:"autocomplete"
        jobDescription(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        sexTypeAccepted nullable: true
        fromAge(nullable: true)
        toAge(nullable: true, validator: {val, obj ->
        return val >= obj.fromAge
        })
        fromTall nullable: true
        toTall(nullable: true, validator: {val, obj ->
            return val >= obj.fromTall
        })
        fromWeight nullable: true
        toWeight(nullable: true, validator: {val, obj ->
            return val >= obj.fromWeight
        })
        trackingInfo nullable: true,display:false
        job nullable: false, widget:"autocomplete"
        jobType nullable: false, widget:"autocomplete"
        numberOfPositions(Constants.POSITIVE_LONG)
        maritalStatusId(Constants.POSITIVE_LONG_NULLABLE)
        vacancyStatus nullable: false
        requestedByDepartment nullable: true,widget:"autocomplete"
    }

    transient springSecurityService

    static transients = ['springSecurityService','transientData','encodedId']

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

    static mapping = {
        fulfillFromDate type: PersistentDocumentaryDate, {
            column name: 'fulfill_from_date_datetime'
            column name: 'fulfill_from_date_date_tz'
        }
        fulfillToDate type: PersistentDocumentaryDate, {
            column name: 'fulfill_to_date_datetime'
            column name: 'fulfill_to_date_date_tz'
        }
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
