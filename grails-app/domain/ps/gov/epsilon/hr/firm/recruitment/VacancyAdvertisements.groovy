package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the vacancy advertisement information related to the firm
 * <h1>Usage</h1>
 * Used  as to define the period that the department allowed to send or propose the needed job positions
 * **/

class VacancyAdvertisements {

    String encodedId

    String id

    String title
    String description

    // vacancy advertisement start date
    ZonedDateTime postingDate
    //vacancy advertisement end date
    ZonedDateTime closingDate

    TrackingInfo trackingInfo

    // example newspaper,site, TV,Radio
    String toBePostedOn

    RecruitmentCycle recruitmentCycle

    static belongsTo = [firm:Firm]

    static embedded = ['trackingInfo', 'descriptionInfo']

    static hasMany = [joinedVacancyAdvertisement:JoinedVacancyAdvertisement]

    static constraints = {
        title(Constants.NAME)
        description(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"]+[blank:false])
        toBePostedOn(Constants.NAME_NULLABLE)
        recruitmentCycle nullable: true,widget:"autocomplete"
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService','encodedId']

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
        postingDate type: PersistentDocumentaryDate, {
            column name: 'posting_date_datetime'
            column name: 'posting_date_date_tz'
        }
        closingDate type: PersistentDocumentaryDate, {
            column name: 'closing_date_datetime'
            column name: 'closing_date_date_tz'
        }
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
