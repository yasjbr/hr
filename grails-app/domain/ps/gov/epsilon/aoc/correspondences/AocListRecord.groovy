package ps.gov.epsilon.aoc.correspondences

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * Main domain that holds the record details in AOC correspondence
 * <h1>Usage</h1>
 * wraps the record sent or received from HR, belongs to aoc correspondence
 * **/

class AocListRecord {

    String encodedId

    /**
     * status of the aoc record regardless of original record status in HR
     */
    EnumListRecordStatus recordStatus

    /**
     * managerial order number
     */
    String orderNo

    /**
     * managerial order date
     */
    ZonedDateTime orderDate

    /**
     * notes related to managerial order
     */
    String orderNotes

    static hasMany = [recordNotes:AocListRecordNote, joinedCorrespondenceListRecords:AocJoinedCorrespondenceListRecord]

    static nullableValues = ['orderDate']

    TrackingInfo trackingInfo
    static embedded = ['trackingInfo']

    static constraints = {
        orderNo(Constants.NAME_NULLABLE)
        orderNotes(Constants.DESCRIPTION_NULLABLE)

        trackingInfo nullable: true, display: false
    }

    static mapping = {
        tablePerHierarchy false // <=> use separate table per subclass
        orderNotes type: 'text'

        orderDate type: PersistentDocumentaryDate, {
            column name: 'order_date_datetime'
            column name: 'order_date_date_tz'
        }

        id generator: 'ps.police.postgresql.PCPSequenceGenerator',type:Long, params: [prefer_sequence_per_entity: true]
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'hrListEmployee']

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
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

    /**
     * should be overriden by cjild domains
     * @return
     */
    public Object getHrListEmployee(){
        return null
    }
}
