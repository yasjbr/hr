package ps.gov.epsilon.hr.firm.disciplinary

import grails.util.Holders
import ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment
import ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryReason
import ps.gov.epsilon.hr.firm.disciplinary.lookup.JoinedDisciplinaryJudgmentReason
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the the penalty record judgment
 * <h1>Usage</h1>
 * Used  as to represent the penalty record judgment by determine the penalty judgment related
 * to penalty reasons and  assign the penalty judgment to penalty group record in the firm
 *<h1>Example</h1>
 * ADOPTED, CANCELED,CANCELED_BY_REQUEST...etc.
 * **/

class DisciplinaryRecordJudgment {

    static auditable = true

    String id

    String encodedId

    String value
    Long unitId
    Double baseFactor
    Long currencyId

    /***
     * Filled when  disciplinary Records List created
     */
    DisciplinaryList disciplinaryRecordsList

    EnumJudgmentStatus judgmentStatus = EnumJudgmentStatus.NEW

    /**
     * based on DisciplinaryJudgment hasValidity value
     * the below fields appears in the screen
     * */

    //the penalty  start date
    ZonedDateTime fromDate;
    //nullable true
    // the penalty  end date
    ZonedDateTime toDate;

    TrackingInfo trackingInfo

    /**
     * We used the disciplinaryListNote to add note
     * */

    DisciplinaryListNote disciplinaryListNote

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static nullableValues = ['fromDate','toDate']

    /***
     * We use JoinedDisciplinaryJudgmentReason to give the system the ability to determine
     * the DisciplinaryRecordJudgment and the related Disciplinary Reason bzu the relation between
     * them many-to-many
     */
    static belongsTo = [disciplinaryRequest:DisciplinaryRequest, disciplinaryJudgment: DisciplinaryJudgment, firm:Firm]

    static hasMany = [disciplinaryReasons: DisciplinaryReason]

    static constraints = {
        disciplinaryRecordsList nullable: true,widget:"autocomplete"
        unitId(Constants.POSITIVE_LONG_NULLABLE)
        baseFactor(Constants.POSITIVE_DOUBLE_NULLABLE)
        currencyId(Constants.POSITIVE_LONG_NULLABLE)
        value(Constants.STRING_NULLABLE)
        judgmentStatus nullable: false
        disciplinaryListNote nullable: true
        trackingInfo nullable: true,display:false
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

    static transients = ['springSecurityService', 'encodedId','transientData']

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
