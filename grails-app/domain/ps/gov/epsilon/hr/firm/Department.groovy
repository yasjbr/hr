package ps.gov.epsilon.hr.firm

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.lookups.DepartmentType
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the department information
 * <h1>Usage</h1>
 * Used  as to represent of department information
 * **/
class Department {

    static auditable = true

    String id

    String encodedId

    DescriptionInfo descriptionInfo

    // the governorate exist already in the location but to simplified the search and department distribution we need to add it
    Long governorateId
    Long locationId

    //external information to describe the structured location
    String unstructuredLocation

    String managerialParentDeptId
    String functionalParentDeptId

    String note

    Map transientData = [:]

    //The employee related tasks depends on department related tasks
    static hasMany = [departmentOperationalTasks:JoinedDepartmentOperationalTasks,contactInfos:DepartmentContactInfo]

    TrackingInfo trackingInfo

    static belongsTo = [firm:Firm, departmentType:DepartmentType]

    static embedded = ['trackingInfo','descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
        governorateId(Constants.POSITIVE_LONG_NULLABLE)
        locationId (Constants.POSITIVE_LONG_NULLABLE)
        departmentType nullable: false
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE +[widget: "textarea"])
        note(Constants.DESCRIPTION_NULLABLE+ [widget: "textarea"])
        managerialParentDeptId(Constants.FIRM_PK_NULLABLE)
        functionalParentDeptId(Constants.FIRM_PK_NULLABLE)
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
        id bindable: true
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId','transientData']

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

    public String toString(){
        return descriptionInfo?.localName
    }
}
