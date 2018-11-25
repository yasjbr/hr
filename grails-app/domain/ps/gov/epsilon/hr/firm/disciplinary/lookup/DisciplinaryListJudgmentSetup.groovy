package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime


/**
 * <h1>Purpose</h1>
 * To hold the Disciplinary Category and the Disciplinary Judgment that will transfer to displinary list depends
 * on the list prefix and the Disciplinary Judgment
 *
 * **/

class DisciplinaryListJudgmentSetup {

    String id

    String encodedId
    // use to generate the name of the list
    String listNamePrefix

    TrackingInfo trackingInfo

    static belongsTo = [disciplinaryCategory: DisciplinaryCategory, disciplinaryJudgment: DisciplinaryJudgment, firm: Firm]


    static embedded = ['trackingInfo']

    static constraints = {
        listNamePrefix(Constants.NAME_NULLABLE)
        disciplinaryCategory unique: ['disciplinaryJudgment', 'firm']
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

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
