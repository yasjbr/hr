package ps.gov.epsilon.hr.firm.profile

import grails.util.Holders
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

class EmployeeExternalAssignation {

    static auditable = true

    String id

    String encodedId
/**
 * الفرز الخارجي في جهاز اخر
 * */
    Long assignedToOrganizationId
    //nullable: true
    ZonedDateTime assignedToOrganizationFromDate

    String note

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static belongsTo = [employmentRecord:EmploymentRecord]

    transient springSecurityService

    static transients = ['springSecurityService','transientData','encodedId']


    static constraints = {
        assignedToOrganizationId (Constants.POSITIVE_LONG_NULLABLE)
        note(Constants.DESCRIPTION_NULLABLE +[widget: "textarea"])
        trackingInfo nullable: true, display: false
    }

    static mapping = {
      assignedToOrganizationFromDate type: PersistentDocumentaryDate, {
            column name: 'assigned_to_organization_from_date_datetime'
            column name: 'assigned_to_organization_from_date_date_tz'
        }
    }

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
