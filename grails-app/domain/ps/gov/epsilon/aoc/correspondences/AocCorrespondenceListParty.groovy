package ps.gov.epsilon.aoc.correspondences

import grails.util.Holders
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * Sender or receiver or copy of the aoc correspondence
 * <h1>Usage</h1>
 * Used  as to represent the correspondence list party for sendeing and receiving
 *
 */
class AocCorrespondenceListParty {

    String encodedId

    /**To, From, Copy*/
    EnumCorrespondencePartyType partyType

    /** class of party: firm, committee or core organization**/
    EnumCorrespondencePartyClass partyClass

    /** id of the firm, committee or core organization ***/
    Long partyId

    static belongsTo = [correspondenceList: AocCorrespondenceList]

    TrackingInfo trackingInfo
    static embedded = ['trackingInfo']

    static constraints = {
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }

    Map transientData=[:]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)
            applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if(springSecurityService?.isLoggedIn()){
            if (!trackingInfo.createdBy)
                trackingInfo.createdBy = springSecurityService?.principal?.username?:applicationName
            if (!trackingInfo.lastUpdatedBy)
                trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        }
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = applicationName
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
        if(!applicationName)applicationName = "BootStrap";
        if(springSecurityService?.isLoggedIn()){
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        }
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = applicationName

        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

    @Override
    public String toString(){
        partyType.toString() + " - " + name
    }

    public String getName(){
        return transientData.partyName
    }

}
