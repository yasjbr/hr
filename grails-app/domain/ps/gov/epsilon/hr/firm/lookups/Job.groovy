package ps.gov.epsilon.hr.firm.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the job title
 * <h1>Usage</h1>
 * Used  as to represents the job title
 * <h1>Example</h1>
 * Engineer,سائق,مبرمج,رسام ... etc.
 * **/

class Job {

    String id

    String encodedId

    //the code of the job
    String code
    DescriptionInfo descriptionInfo
    String note
    Short fromAge
    Short toAge
    Short fromWeight
    Short toWeight
    Short fromHeight
    Short toHeight


    Map transientData = [:]

    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo', 'descriptionInfo', 'transientData']

    //has many set used to be default values in the vacancy screen after select the job
    static hasMany = [
            joinedJobEducationDegrees    : JoinedJobEducationDegree,
            joinedJobEducationMajors     : JoinedJobEducationMajor,
            joinedJobInspectionCategories: JoinedJobInspectionCategory,
            joinedJobOperationalTasks    : JoinedJobOperationalTask,
            joinedJobMilitaryRanks       : JoinedJobMilitaryRank

    ]

    static belongsTo = [jobCategory: JobCategory, firm: Firm]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        fromHeight(Constants.POSITIVE_SHORT_NULLABLE)
        toHeight(Constants.POSITIVE_SHORT_NULLABLE)
        fromWeight(Constants.POSITIVE_SHORT_NULLABLE)
        toWeight(Constants.POSITIVE_SHORT_NULLABLE)
        fromAge(Constants.POSITIVE_SHORT_NULLABLE + [min: new Short("18"), max: new Short("30")])
        toAge(Constants.POSITIVE_SHORT_NULLABLE + [min: new Short("18"), max: new Short("30")])
        //code(Constants.STRING)
        code(Constants.STRING_NULLABLE)
        note(Constants.DESCRIPTION_NULLABLE + [widget: "textarea"])
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget: "DescriptionInfo"
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
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

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    @Override
    public String toString() {
        return descriptionInfo.toString();
    }
}
