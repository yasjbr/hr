package ps.gov.epsilon.hr.firm.absence

import grails.util.Holders
import ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the Employees who are added to a specific returnFromAbsence list.
 * <h1>Usage</h1>
 * Used as to represents the employees who are added to a specific returnFromAbsence list related to returnFromAbsence
 * **/

class ReturnFromAbsenceListEmployee {

    String encodedId

    String id

    EnumListRecordStatus recordStatus

    //when the employee return to work from absence
    ZonedDateTime actualReturnDate

    //The actual reason why the employee became absent. For example : vacation, medical, arrest
    EnumAbsenceReason actualAbsenceReason

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [returnFromAbsenceRequest: ReturnFromAbsenceRequest, returnFromAbsenceList: ReturnFromAbsenceList]

    static hasMany = [returnFromAbsenceListEmployeeNotes:ReturnFromAbsenceListEmployeeNote]

    static constraints = {
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

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

    static mapping = {
        actualReturnDate type: PersistentDocumentaryDate, {
            column name: 'actual_return_date_datetime'
            column name: 'actual_return_date_date_tz'
        }
    }
}
