package ps.gov.epsilon.hr.firm.vacation

import grails.util.Holders
import ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the vacation configurations related to all employees in the system
 * <h1>Usage</h1>
 * Used as to represents the vacation configurations related to all employees in the HR application. To load new vacation balances by the start of
 * each year the configurations must be ready.
 * **/

class VacationConfiguration {

    String id

    static auditable = true

    String encodedId

    //the employee  gender identifier
    EnumSexAccepted sexTypeAccepted
    //the employee marital status identifier
    Long maritalStatusId   //null mean apply for all
    //he employment period required for a specific vacation type
    Short employmentPeriod = 0   //0 apply for all
    Short frequency = 0   //0 mean multiple times , 1 means one during employment life like "Haj vacation"

    Short allowedValue     // 0 mean  can take any value less than his current balance per each vacation
    Short maxAllowedValue  // the value of the annual balance that employee gains every year
    Short maxBalance
    // the value of the max balance that employee have after transfer balance from previous year if null we did not have limit


    Boolean checkForAnnualLeave

    Boolean isBreakable = false
    // vacation is divided on the whole year or not and it used only when the employee employment date less than one year
    Boolean takenFully = false   // vacation should be take as bulk(full) or in intervals
    Boolean isTransferableToNewYear = false  // transfer balances from old year to the new year
    Float vacationTransferValue = 1F


    TrackingInfo trackingInfo

    //the vacation type
    VacationType vacationType
    //the employee military rank. Each rank has special configuration
    MilitaryRank militaryRank

    Long religionId  // is it depends on the religion
    Boolean isExternal = false  //is this type use for external vacation

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static belongsTo = [firm: Firm]

    static constraints = {
        sexTypeAccepted(Constants.STRING)
        religionId(Constants.POSITIVE_LONG_NULLABLE)
        maritalStatusId(Constants.POSITIVE_LONG_NULLABLE)
        employmentPeriod(Constants.POSITIVE_SHORT)
        frequency(Constants.POSITIVE_SHORT)
        allowedValue(Constants.POSITIVE_SHORT + [validator: { value, object, errors ->
            if (value && (value > object?.maxAllowedValue)) {
                errors.reject('vacationConfiguration.allowedValueError.label')
            }
            return true
        }])
        maxAllowedValue(Constants.POSITIVE_SHORT + [max: 365 as short])

        maxBalance(nullable: true, validator: { value, object, errors ->
            if (value && (value < object?.maxAllowedValue)) {
                errors.reject('vacationConfiguration.maxBalance.maxAllowedValue.error.min')
            }
            return true
        })

        checkForAnnualLeave nullable: false
        isBreakable nullable: false
//		takenFully nullable: false
        isTransferableToNewYear nullable: false
        vacationTransferValue(Constants.POSITIVE_FLOAT + [min: 0F, max: 1F])
        vacationType(nullable: false, widget: "autocomplete")
        militaryRank nullable: false, widget: "autocomplete"
        trackingInfo nullable: true, display: false
    }

    static mapping = {
        vacationType cascade: 'save-update'
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

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }
}
