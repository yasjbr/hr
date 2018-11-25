package ps.gov.epsilon.hr.firm.disciplinary.lookup

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
 * To hold the the penalty judgment of violation related to penalty reason and penalty category in the firm
 * <h1>Usage</h1>
 * Used  as to represent the penalty judgment of violation related to penalty reason and penalty category in the firm
 * <h1>Example</h1>
 * DEDUCT FROM SALARY, DELAY SENIORITY, ... etc.
 * **/

public class DisciplinaryJudgment {

    String id

    String encodedId

    DescriptionInfo descriptionInfo
    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    Map transientData = [:]
    TrackingInfo trackingInfo

    Boolean isCurrencyUnit = Boolean.FALSE

    //used to indicate if it contains period (from date, to date)
    Boolean hasValidity = Boolean.FALSE

    //this flag was added to indicate if the judgment affects promotion (military rank due date).
    //مثال: عقوبة ترك في الرتبة تؤثر على فترة الترقية
    Boolean excludedFromEligiblePromotion=Boolean.FALSE



    static belongsTo = [firm: Firm]

    static embedded = ['trackingInfo', 'descriptionInfo']
//unit id s in the core ex:Day,Month,JD,KG
    static hasMany = [joinedDisciplinaryJudgmentReasons: JoinedDisciplinaryJudgmentReason, unitIds: Long,currencyIds:Long]

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        descriptionInfo widget: "DescriptionInfo"
        trackingInfo nullable: true, display: false

    }


    static mapping = {
        joinedDisciplinaryJudgmentReasons cascade: 'save-update'
    }


    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
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
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    @Override
    public String toString() {
        return descriptionInfo.toString()
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }

}