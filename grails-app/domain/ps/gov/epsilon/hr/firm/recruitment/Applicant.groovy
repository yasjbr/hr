package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the applicant information belongs to the firm
 * <h1>Usage</h1>
 * Used  as to represents the applicant information
 * **/

class Applicant {

    static auditable = true

    String id

    Map transientData = [:]

    String encodedId

    Long personId

    String personName //save the person full name in phr

    String fatherName //save father name in phr

    String motherName //save mother name in phr

    /**
     * Employment information
     * */
    ZonedDateTime applyingDate;
    //duplicated from the core but it will be used for direct filtration
    Double age

    String archiveNumber

    Long locationId

    //general description about the applicant location
    String unstructuredLocation

    Vacancy vacancy
    RecruitmentCycle recruitmentCycle

    Double height
    Double weight

    //father Job
    Long fatherProfessionType
    String fatherJobDesc

    //mother job
    Long motherProfessionType
    String motherJobDesc

    //Applicant previous job
    Long previousProfessionType
    String previousJobDesc

    // الاقارب في الاجهزة الامنية
    String relativesInMilitaryFirms

    // الحكومي الاقارب في القطاع المدني
    String relativesInCivilianFirm

//جهة الترشيح
    String nominationParty

    String rejectionReason

    ApplicantStatusHistory applicantCurrentStatus

    //filled if the applicant become in the shortlist
    Interview interview

    TrackingInfo trackingInfo

    String specialMarksNote

    /*
      * Note:
      * 1) The contact info as phone ,email,mobile
      * will filled direct in the core person contact info and will view here when the
      * as part of related person info
      *
      * 2) The arrest history will also be same as contact info
      * 3) Education info will also be same as contact info
      * 4) general info will also be same as contact info
     */

    //todo remove the reference and use has-many instead of one-to-one for  (RecruitmentListEmployee,TraineeListEmployee)
    RecruitmentListEmployee recruitmentListEmployee
    TraineeListEmployee traineeListEmployee

    static belongsTo = [firm: Firm]

    static embedded = ['trackingInfo']

    //We add personDisabilityInfos,personHealthHistories,legalIdentifiers,arrestHistories to refer  to the person to take snapshot from the situation of the person
    //when he applied and filled the applicant
    static hasMany = [personDisabilityInfos: Long, personHealthHistories: Long, legalIdentifiers: Long, arrestHistories: Long, contactInfos: Long, educationEnfos: Long, inspectionCategoriesResult: ApplicantInspectionCategoryResult, statusHistory: ApplicantStatusHistory]

    static includeInValidate = ['statusHistory']


    static constraints = {
        /**
         * If the vacancy null the list of the
         * {@link ps.gov.epsilon.hr.firm.lookups.InspectionCategory}
         * determine depends on the inspection category type (isRequiredByFirmPolicy=true)
         *
         * If the vacancy null the list of the
         * {@link ps.gov.epsilon.hr.firm.settings.FirmDocument}
         *
         *
         */
        vacancy nullable: true, widget: "autocomplete"
        personName(Constants.NAME)
        /**
         * To be filled from vacancy if vacancy not null
         * */
        recruitmentCycle nullable: true, widget: "autocomplete"
        interview nullable: true, widget: "autocomplete"
        recruitmentListEmployee nullable: true
        traineeListEmployee nullable: true
        personId(Constants.POSITIVE_LONG)
        age(Constants.POSITIVE_DOUBLE)
        archiveNumber nullable: true, unique: ['personId']
        locationId(Constants.POSITIVE_LONG)
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"] + [blank: false])
        height(Constants.POSITIVE_DOUBLE)
        weight(Constants.POSITIVE_DOUBLE)
        fatherProfessionType(Constants.POSITIVE_LONG)
        fatherJobDesc(Constants.DESCRIPTION_NULLABLE)
        motherProfessionType(Constants.POSITIVE_LONG)
        motherJobDesc(Constants.DESCRIPTION_NULLABLE)
        previousProfessionType(Constants.POSITIVE_LONG_NULLABLE)
        previousJobDesc(Constants.DESCRIPTION_NULLABLE)
        relativesInMilitaryFirms(Constants.NAME_NULLABLE)
        relativesInCivilianFirm(Constants.NAME_NULLABLE)
        nominationParty(Constants.NAME_NULLABLE)
        applicantCurrentStatus widget: "autocomplete"
        nominationParty(Constants.NAME_NULLABLE)
        fatherName(Constants.NAME_NULLABLE)
        motherName(Constants.NAME_NULLABLE)
        specialMarksNote(Constants.DESCRIPTION_NULLABLE)
        applicantCurrentStatus nullable: true, widget: "autocomplete"
        trackingInfo nullable: true, display: false

        rejectionReason(nullable: true, validator: { value, object, errors ->

            if (!value && (object?.applicantCurrentStatus?.applicantStatus in [EnumApplicantStatus.REJECTED, EnumApplicantStatus.NOT_EMPLOYED, EnumApplicantStatus.REJECTED_FOR_EVER]))
                errors.reject('applicant.rejectionReason.error.required')
            return true
        })
    }

    static mapping = {
        applyingDate type: PersistentDocumentaryDate, {
            column name: 'applying_date_datetime'
            column name: 'applying_date_date_tz'
        }
        interview cascade: 'save-update'
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

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
}
