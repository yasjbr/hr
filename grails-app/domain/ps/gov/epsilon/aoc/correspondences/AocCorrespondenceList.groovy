package ps.gov.epsilon.aoc.correspondences

import grails.util.Holders
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.lookups.Province
import ps.gov.epsilon.hr.firm.lookups.ProvinceLocation
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * Main domain for all correspondences received and sent by AOC
 * <h1>Usage</h1>
 * Used  as to represent the correspondence list between the AOC and HR system for the firms
 * <h1>Example</h1>
 * correspondence between PCP and AOC, correspondence between DCO and AOC, ...etc.
 */
class AocCorrespondenceList {

    SharedService sharedService

    String encodedId

//    /**
//     * Correspondence list received or sent to HR system
//     */
//    CorrespondenceList hrCorrespondenceList

    /**
     * CorrespondencyType might be necessary for workflow, used to generate record sub classes
     * extracted from HR correspondenceList in automation,
     * chosen by employee when manual
     */
    EnumCorrespondenceType correspondenceType

    /**
     * Direction of correspondence: Incoming or Outgoing
     */
    EnumCorrespondenceDirection correspondenceDirection

    /**
     * Name of the correspondence
     */
    String name

    /**
     * Code of the correspondence
     */
    String code

    /**
     * CoverLetter of the correspondence
     */
    String coverLetter

    /**
     * AOC Notes are stored here
     */
    String notes

    /**
     * serial number for original correspondence in case of incoming case
     */
    String originalSerialNumber

    /**
     * Date of correspondence
     * Its the date when the correspondence takes the serial number for ougoing and incoming
     */
    ZonedDateTime archivingDate

    /**
     * Correspondence serial number
     */
    String serialNumber

    /**
     * Date when the correspondence is delivered
     */
    ZonedDateTime deliveryDate

    /**
     * Person name who delivered the corresondence
     */
    String deliveredBy

    /**
     * Person name who received the correspondence
     */
    String receivedBy

    /**
     * Needed to be enum for workflow usage
     * History is reserved in correspondenceListStatuses
     */
    EnumCorrespondenceStatus currentStatus = EnumCorrespondenceStatus.CREATED

    /**
     * the direct parent correspondence from which this correspondence is created
     */
    AocCorrespondenceList parentCorrespondenceList

    /**
     * id of the root correspondence from which al child correspondences are created
     */
    Long threadId

    TrackingInfo trackingInfo
    static embedded = ['trackingInfo']

    /***
     * Province of the party who sent or received the correspondence
     */
    Province province

    /**
     * Location of the province to which the receiver or sender belongs to
     */
    ProvinceLocation provinceLocation

    static hasMany = [correspondenceListStatuses     : AocCorrespondenceListStatus,
                      joinedCorrespondenceListRecords: AocJoinedCorrespondenceListRecord,
                      correspondenceListParties      : AocCorrespondenceListParty,
                      joinedAocHrCorrespondenceLists : JoinedAocHrCorrespondenceList]

    static constraints = {
        hrCorrespondenceList nullable: true  // might be filled in workflow
        receivedBy nullable: true
        deliveredBy nullable: true
        currentStatus nullable: true
        trackingInfo nullable: true, display: false
        notes nullable: true
        code nullable: true
        coverLetter(Constants.DESCRIPTION_NULLABLE)
        name(Constants.LOOKUP_NAME)
        originalSerialNumber(Constants.LOOKUP_NAME_NULLABLE)
        parentCorrespondenceList nullable: true
        threadId nullable: true
        province nullable: true
        provinceLocation nullable: true
    }

    static mapping = {
        tablePerHierarchy false // <=> use separate table per subclass
        notes type: 'text'

        archivingDate type: PersistentDocumentaryDate, {
            column name: 'archiving_date_datetime'
            column name: 'archiving_date_date_tz'
        }

        deliveryDate type: PersistentDocumentaryDate, {
            column name: 'delivery_date_datetime'
            column name: 'delivery_date_date_tz'
        }

        id generator: 'ps.police.postgresql.PCPSequenceGenerator', type: Long, params: [prefer_sequence_per_entity: true]
    }

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['correspondenceListParties']

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'incomingSerial', 'outgoingSerial', 'correspondenceName', 'sharedService',
                         'sendingParty', 'receivingParty', 'outgoingDate', 'incomingDate','listRecordCount']

    def beforeInsert() {

        if (!this.code) {
            this.code = sharedService?.generateListCode(this.class.name, this.correspondenceType.code, 20)
        }

        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName)
            applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (springSecurityService?.isLoggedIn()) {
            if (!trackingInfo.createdBy)
                trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
            if (!trackingInfo.lastUpdatedBy)
                trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        }
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = applicationName
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
        if (springSecurityService?.isLoggedIn()) {
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        }
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = applicationName

        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    def afterInsert() {
        // set thread id equals id for root correspondences
        if (!this.threadId && this.id) {
            this.threadId = this.id
            this.save()
        }
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

    /**
     * incoming serial might be the serial number of this list (if it is incoming) or serial number of parent list (if this list is outgoing)
     * @return
     */
    public String getIncomingSerial() {
        if (this.correspondenceDirection == EnumCorrespondenceDirection.INCOMING) {
            return this.serialNumber
        }
        if (parentCorrespondenceList) {
            return parentCorrespondenceList?.incomingSerial
        }
        return null
    }

    /**
     * To get the count of aoc list record in the aoc correspondence list.
     * @return Integer
     */
    public Integer getListRecordCount() {
        return AocJoinedCorrespondenceListRecord.countByCorrespondenceList(this)
    }

    /**
     * To get the incoming date for aoc correspondence list.
     * @return ZonedDateTime
     */
    public ZonedDateTime getIncomingDate() {
        if (this.correspondenceDirection == EnumCorrespondenceDirection.INCOMING) {
            return this.archivingDate
        }
        if (parentCorrespondenceList) {
            return parentCorrespondenceList?.incomingDate
        }
        return null
    }

    /**
     * outgoing serial might be the serial number of this list (if it is outgoing) or serial number of parent list (if this list is incoming)
     * @return ZonedDateTime
     */
    public String getOutgoingSerial() {
        if (this.correspondenceDirection == EnumCorrespondenceDirection.OUTGOING) {
            return this.serialNumber
        }
        if (parentCorrespondenceList) {
            return parentCorrespondenceList?.outgoingSerial
        }
        return null
    }

    /**
     * To get the outgoing date for aoc correspondence list.
     * @return date
     */
    public ZonedDateTime getOutgoingDate() {
        if (this.correspondenceDirection == EnumCorrespondenceDirection.OUTGOING) {
            return this.archivingDate
        }
        if (parentCorrespondenceList) {
            return parentCorrespondenceList?.outgoingDate
        }
        return null
    }

    public String getCorrespondenceName() {
        return this?.name
    }

    /**
     * Origin of correspondence
     * @return
     */
    public AocCorrespondenceListParty getSendingParty() {
        return correspondenceListParties?.find { it.partyType == EnumCorrespondencePartyType.FROM }
    }

    /**
     * Target of correspondence
     * @return
     */
    public AocCorrespondenceListParty getReceivingParty() {
        return correspondenceListParties?.find { it.partyType == EnumCorrespondencePartyType.TO }
    }

    /**
     * Target of correspondence
     * @return
     */
    public List<AocCorrespondenceListParty> getCopyToPartyList() {
        return correspondenceListParties?.findAll { it.partyType == EnumCorrespondencePartyType.COPY }?.sort {
            it.id
        }?.toList()
    }

    /**
     * used to get the firm responsible of the list
     * @return
     */
    public Long getHrFirmId() {
        AocCorrespondenceListParty firmParty
        if (this.correspondenceDirection == EnumCorrespondenceDirection.INCOMING) {
            firmParty = sendingParty
        } else {
            firmParty = receivingParty
        }
        if (firmParty.partyClass == EnumCorrespondencePartyClass.FIRM) {
            return firmParty.partyId
        }
        return null
    }

    /**
     * used to get the hr correspondence list
     * @return
     */
    public CorrespondenceList getHrCorrespondenceList(Long firmId = null) {
        if (!firmId) {
            firmId = hrFirmId
        }
        if (firmId) {
            return joinedAocHrCorrespondenceLists?.find { it.firm.id == firmId }?.hrCorrespondenceList
        }
        return null
    }
}
