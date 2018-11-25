package ps.gov.epsilon.hr.firm.recruitment

import grails.util.Holders
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the recruitment list that contains many records of recruitment list employee.
 * It consider as correspondence list and have a reference to distinguish between different recruitment list.
 * <h1>Usage</h1>
 * Used  as to represents the recruitment list which contains many records of recruitment list employee
 * **/

class TraineeList extends CorrespondenceList {

    def sharedService

    String encodedId

    Long trainingLocationId

    //external information to describe the structured location
    String unstructuredLocation

    ZonedDateTime fromDate
    ZonedDateTime toDate

    Map transientData = [:]

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId', 'transientData']

    static hasMany = [traineeListEmployees: TraineeListEmployee]

    static nullableValues = ['toDate']


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

    static constraints = {
        code nullable: true
        unstructuredLocation nullable: true
        trainingLocationId(Constants.POSITIVE_LONG)
    }

    def beforeInsert() {


        if (!this.code) {
            this.code = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.recruitment.TraineeList", 'TRAINLIST', 20)
        }

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


    @Override
    public String toString() {
        return "TraineeList{" +
                "id=" + id +
                ", version=" + version +
                ", correspondenceListStatuses=" + correspondenceListStatuses +
                ", traineeListEmployees=" + traineeListEmployees +
                ", firm=" + firm +
                ", encodedId='" + encodedId + '\'' +
                ", trainingLocationId=" + trainingLocationId +
                ", unstructuredLocation='" + unstructuredLocation + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", transientData=" + transientData +
                '}';
    }
}
