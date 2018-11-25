package ps.gov.epsilon.hr.firm.settings

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Firm Document and the Enum Operation many-to-many relation
 * **/

class JoinedFirmOperationDocument {

    String id

    String encodedId

    EnumOperation operation

    FirmDocument firmDocument

    //used to be in the template
    Boolean isMandatory


    TrackingInfo trackingInfo

    Map transientData


    static embedded = ['trackingInfo']

    static constraints = {
        operation nullable: false
        firmDocument nullable: false,widget:"autocomplete"
        isMandatory nullable: false
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
}
