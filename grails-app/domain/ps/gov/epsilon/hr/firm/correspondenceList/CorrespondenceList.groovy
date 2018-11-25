package ps.gov.epsilon.hr.firm.correspondenceList

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumDeliveryStatus
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the correspondence list between the firm and party outside the firm
 * <h1>Usage</h1>
 * Used  as to represent the correspondence list between the firm and party outside the firm
 *<h1>Example</h1>
 *correspondence between PCP and SARAYA, correspondence between DCO and SARAYA, ...etc.
 *  * **/

class CorrespondenceList {

    String id

    String encodedId

    String code

    String name

    String coverLetter

    Map transientData=[:]

    //رقم الصادر
    String manualOutgoingNo

    //رقم وارد
    //in receiving must be filled
    String manualIncomeNo

    CorrespondenceListStatus currentStatus

    TrackingInfo trackingInfo

    EnumReceivingParty receivingParty

    /**
     * Used to indicate if the list is delivered to AOC
     * Used by job
     */
    EnumDeliveryStatus deliveryStatus= EnumDeliveryStatus.NOT_DELIVERED

    String orderNo

    static embedded = ['trackingInfo']

    static belongsTo = [firm:Firm]

    static hasMany = [correspondenceListStatuses:CorrespondenceListStatus]

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['correspondenceListStatuses','currentStatus']

    static constraints = {
        receivingParty nullable: true
        manualOutgoingNo(Constants.LOOKUP_NAME_NULLABLE)
        manualIncomeNo(Constants.LOOKUP_NAME_NULLABLE)
        code(Constants.NAME_NULLABLE)
        coverLetter(Constants.DESCRIPTION_NULLABLE)
        name(Constants.LOOKUP_NAME)
        orderNo(Constants.NAME_NULLABLE)
        currentStatus nullable: true,widget:"autocomplete"
        trackingInfo nullable: true,display:false
        deliveryStatus nullable: true
    }

    static mapping = {
        tablePerHierarchy false // <=> use separate table per subclass
        coverLetter type: 'text'
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if(!applicationName)
            applicationName = "BootStrap"
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

        //todo add new status in before insert
//       this.addToCorrespondenceListStatuses(new CorrespondenceListStatus(fromDate:trackingInfo.dateCreatedUTC,EnumCorrespondenceListStatus.CREATED,receivingParty:receivingParty,firm: firm))
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

}
