package ps.gov.epsilon.hr.firm.allowance

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the Employees who are added to a specific allowance list related allowance Requests(child , marital ,allowance..etc)
 * <h1>Usage</h1>
 * Used as to represents Allowance list for employee after add Allowance request is approved.
 * **/


class AllowanceListEmployee {

    String id

    String encodedId

//    String statusReason

    EnumListRecordStatus recordStatus

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [allowanceRequest: AllowanceRequest, allowanceList: AllowanceList]

    static hasMany = [allowanceListEmployeeNotes:AllowanceListEmployeeNote]

    static constraints = {
//        statusReason(nullable: true, blank: true, size: 0..250, validator: { value, object,errors ->
//            if (( object?.recordStatus == EnumListRecordStatus.REJECTED ) && !value)
//                errors.reject('AllowanceListEmployee.statusReason.error.required')
//            return true
//        })

        allowanceRequest nullable: true,widget:"autocomplete"
        recordStatus nullable: false
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
        if(recordStatus == null)
            recordStatus=EnumListRecordStatus.NEW
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }
    public String getEncodedId(){
        return HashHelper.encode(id.toString())
    }
}
