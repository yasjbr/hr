package ps.gov.epsilon.hr.firm.employmentService

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.employmentService.lookups.ServiceActionReason
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the Employees included in a Service List
 * <h1>Usage</h1>
 * Used  as to represents the Employees included in a Service List
 * **/

class ServiceListEmployee {

    String encodedId

    String id

    // affect employee status to be changed depends on, it filled automatic if it related to request from expectedDateEffective
    ZonedDateTime dateEffective

    EnumListRecordStatus recordStatus

    ServiceActionReason serviceActionReason

    /***
     * To keep history of the Employment Record  and military rank when this Disciplinary has been taken
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2)Entered from the Disciplinary module it will take the current employment Record of the employee
     *
     */
    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank


    TrackingInfo trackingInfo

    //TODO tell mureed that i put it as null
    static nullableValues = ['dateEffective']

    static embedded = ['trackingInfo']

    static belongsTo = [employmentServiceRequest:EmploymentServiceRequest,employee: Employee, serviceList: ServiceList, firm:Firm]
    static hasMany = [serviceListEmployeeNotes: ServiceListEmployeeNote]

    static constraints = {
        employmentServiceRequest nullable: true
        recordStatus nullable: false
        //if the request type END_OF_SERVICE nullable :false
        serviceActionReason nullable: true, widget:"autocomplete"
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        trackingInfo nullable: true,display:false
    }

    static mapping = {
        dateEffective type: PersistentDocumentaryDate, {
            column name: 'date_effective_datetime'
            column name: 'date_effective_date_tz'
        }
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
        if(recordStatus == null)
            recordStatus=EnumListRecordStatus.NEW
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if(!applicationName)
            applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
