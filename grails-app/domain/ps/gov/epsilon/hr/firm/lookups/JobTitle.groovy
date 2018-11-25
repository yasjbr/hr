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
 *<h1>Purpose</h1>
 * To hold the job title
 * <h1>Usage</h1>
 * Used  as to represents the job title
 *<h1>Example</h1>
 * Department manager, ... etc.
 * **/

class JobTitle {

    String id

    String encodedId

    //the code of the job title
    String code
    DescriptionInfo descriptionInfo
    String note


  //هل يسمح بتكرار هذا المسمى ضمن نفس الوحدة الادارية
    Boolean allowToRepeetInUnit=Boolean.TRUE

    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode


    Map transientData=[:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static  hasMany=[
                        joinedJobTitleEducationDegrees:JoinedJobTitleEducationDegree,
                        joinedJobTitleOperationalTasks:JoinedJobTitleOperationalTask,
                        joinedJobTitleMilitaryRanks:JoinedJobTitleMilitaryRank,
                        joinedJobTitleJobRequirements:JoinedJobTitleJobRequirement
                     ]

    static belongsTo = [jobCategory:JobCategory,firm:Firm]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        //code(Constants.STRING)
        code(nullable: true)
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
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


    @Override
    public String toString() {
        return descriptionInfo.toString();
    }
}
