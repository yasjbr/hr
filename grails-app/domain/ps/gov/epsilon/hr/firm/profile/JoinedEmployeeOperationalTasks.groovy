package ps.gov.epsilon.hr.firm.profile

import grails.util.Holders
import ps.gov.epsilon.hr.firm.lookups.OperationalTask
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Employee and the Operational Tasks many-to-many relation
 * **/

class JoinedEmployeeOperationalTasks {
    
    String encodedId

    String id

    Employee employee
    OperationalTask operationalTask

    ZonedDateTime fromDate

    ZonedDateTime toDate

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static nullableValues = ['toDate']


    static constraints = {
        employee nullable: false,widget:"autocomplete"
        operationalTask nullable: false,widget:"autocomplete"
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService','encodedId']

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

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }

    @Override
    String toString(){
        return this?.operationalTask?.toString()
    }
    
    
}
