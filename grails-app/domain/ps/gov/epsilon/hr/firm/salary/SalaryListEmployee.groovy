package ps.gov.epsilon.hr.firm.salary

import grails.util.Holders
import ps.gov.epsilon.hr.enums.salary.v1.EnumSalaryActionReason
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the Employees who are added to a specific salary list.
 * <h1>Usage</h1>
 * Used as to represents the Employees who are added to a specific salary list
 * **/

class SalaryListEmployee {

    String id

    EnumListRecordStatus recordStatus
    EnumSalaryActionReason salaryActionReason
    //the status of the request
//    String statusReason
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

    static embedded = ['trackingInfo']

    static belongsTo = [employee: Employee, salaryList: SalaryList]

    static hasMany = [salaryListEmployeeNotes:SalaryListEmployeeNote]

    static constraints = {
//        note(Constants.DESCRIPTION_NULLABLE, widget: "textarea",[validator: { value, object,errors ->
//            if (object.recordStatus.REJECTED && !value )
//                errors.reject('SalaryListEmployee.note.error.required')
//            return true
//        }])
//        orderNo (Constants.STRING_NULLABLE)
//        statusReason(Constants.DESCRIPTION_NULLABLE)
        recordStatus nullable: false
        salaryActionReason nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
        trackingInfo nullable: true,display:false
    }

    transient springSecurityService

    static transients = ['springSecurityService']

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

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
}
