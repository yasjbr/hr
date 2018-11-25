package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the interview information for employee
 * <h1>Usage</h1>
 * Used  as to represents the information af interview for employee
 * **/

class Interview {

    static auditable = true

    String id

    String encodedId

    String description

    RecruitmentCycle recruitmentCycle

    ZonedDateTime fromDate
    ZonedDateTime toDate

    EnumInterviewStatus interviewStatus

    TrackingInfo trackingInfo

    Long locationId

//external information to describe the structured location
    String unstructuredLocation
    //used in case we want to create interview per vacancy
    Vacancy vacancy

    String note

    Map transientData


    static belongsTo = [firm: Firm]

    static embedded = ['trackingInfo']

    static hasMany = [applicants:Applicant,committeeRoles:JoinedInterviewCommitteeRole]

    static constraints = {
        description(Constants.DESCRIPTION+[widget:"textArea"])
        vacancy nullable: true,widget:"autocomplete"
        note(Constants.DESCRIPTION_NULLABLE+ [widget: "textarea"])
        recruitmentCycle nullable: true,widget:"autocomplete"
        interviewStatus nullable: false
        trackingInfo nullable: true,display:false
        locationId(Constants.POSITIVE_LONG)
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE +[widget: "textarea"]+[blank: false])
    }

    static mapping = {
        fromDate type: PersistentDocumentaryDate, {
            column name: 'from_date_datetime'
            column name: 'from_date_date_tz'
        }
        toDate type: PersistentDocumentaryDate, {
            column name: 'to_date_datetime'
            column name: 'to_date_date_tz'
        }
    }

    transient springSecurityService

    static transients = ['springSecurityService','encodedId','transientData']

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
