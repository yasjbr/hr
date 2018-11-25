package ps.gov.epsilon.hr.firm.disciplinary

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the the list of disciplinary records for one disciplinary judgment related to many reasons
 * <h1>Usage</h1>
 * Used  as to represent all list of disciplinary records for each disciplinary judgment
 * **/

class DisciplinaryList extends CorrespondenceList{


    def sharedService

    static hasMany = [disciplinaryRecordJudgment: DisciplinaryRecordJudgment]

    static constraints = {
    }


    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName)
            applicationName = "BootStrap"
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

        if (!this.code) {
            this.code = sharedService.generateListCode('ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryList', 'DISCIP', 20)
        }
    }
}