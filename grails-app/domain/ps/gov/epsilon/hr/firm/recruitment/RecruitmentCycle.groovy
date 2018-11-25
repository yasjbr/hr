package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the period that allowed to the department to propose the needed job positions
 * <h1>Usage</h1>
 * Used  as to define the period that the department allowed to send or propose the needed job positions
 * **/

class RecruitmentCycle {

    static auditable = true

    String id

    String encodedId

    String name
    String description

    ZonedDateTime startDate
    ZonedDateTime endDate

    RecruitmentCyclePhase currentRecruitmentCyclePhase

    //todo the invited departments can proposed job requisition within the announcement period and status not closed
    //todo the status must be closed if the end date < current date
    //todo crud operation allowed only for the HR when the status closed (Specific role)

    static hasMany = [joinedRecruitmentCycleDepartment : JoinedRecruitmentCycleDepartment,
                      joinedRecruitmentCycleJobCategory: JoinedRecruitmentCycleJobCategory,
                      recruitmentCyclePhases           : RecruitmentCyclePhase]


    static belongsTo = [firm: Firm]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo', 'descriptionInfo']

    static constraints = {
        name(Constants.LOOKUP_NAME)
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"] + [blank: false])
        currentRecruitmentCyclePhase nullable: true
        trackingInfo nullable: true, display: false
    }

    static nullableValues = ['endDate']

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['joinedRecruitmentCycleDepartment', 'recruitmentCyclePhases']


    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

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
    static mapping = {
        startDate type: PersistentDocumentaryDate, {
            column name: 'start_date_datetime'
            column name: 'start_date_date_tz'
        }
        endDate type: PersistentDocumentaryDate, {
            column name: 'end_date_datetime'
            column name: 'end_date_date_tz'
        }
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    /**
     * This method is used to return the expected next phase for the recruitment cycle
     * we check what is current and return the next phase name
     * @return
     */
    public EnumRequisitionAnnouncementStatus getNextPhase() {
        if (currentRecruitmentCyclePhase) {
            String currentStatus = currentRecruitmentCyclePhase?.requisitionAnnouncementStatus?.toString()
            EnumRequisitionAnnouncementStatus enumRequisitionAnnouncementStatus
            switch (currentStatus) {
                case EnumRequisitionAnnouncementStatus.NEW.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.OPEN
                    break
                case EnumRequisitionAnnouncementStatus.OPEN.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.REVIEWED
                    break
                case EnumRequisitionAnnouncementStatus.REVIEWED.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.VACANCY
                    break
                case EnumRequisitionAnnouncementStatus.VACANCY.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.ADVERT
                    break
                case EnumRequisitionAnnouncementStatus.ADVERT.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.INTERVIEW
                    break
                case EnumRequisitionAnnouncementStatus.INTERVIEW.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.TRAINING
                    break
                case EnumRequisitionAnnouncementStatus.TRAINING.toString():
                    enumRequisitionAnnouncementStatus = EnumRequisitionAnnouncementStatus.CLOSED
                    break
                default:
                    enumRequisitionAnnouncementStatus = null
                    break
            }
            return enumRequisitionAnnouncementStatus
        } else {
            return null
        }

    }

    @Override
    public String toString() {
        return this?.name ?: "";
    }
}
