package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.enums.v1.EnumRecruitmentCycleDepartmentStatus
import ps.gov.epsilon.hr.firm.Department
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the Recruitment Cycle and the Department many-to-many relation
 **/

class JoinedRecruitmentCycleDepartment {

    String id

    String encodedId

    RecruitmentCycle recruitmentCycle

    Department department

    EnumRecruitmentCycleDepartmentStatus recruitmentCycleDepartmentStatus

    Map transientData = [:]

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static constraints = {
        recruitmentCycle nullable: false,widget:"autocomplete"
        department nullable: false,widget:"autocomplete"
        recruitmentCycleDepartmentStatus nullable: false
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

}
