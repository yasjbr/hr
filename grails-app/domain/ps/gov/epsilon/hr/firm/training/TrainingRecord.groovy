package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.training.lookup.TrainingClassification
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the training record of employee
 * <h1>Usage</h1>
 * Used  as to represents the training courses that an employee attended
 * **/


public class TrainingRecord {

    String id

    String encodedId
    /***
     * in case the organizationId is not null the organizationName will be the same of it
     * اسم المعهد
     */
    Long organizationId
    String organizationName;
    /***
     * in case the trainingCourse is not null the name will be the same of it
     */
    String trainingName;

    //when the training course is started
    ZonedDateTime fromDate;
    // the training course end date
    ZonedDateTime toDate;

    // The id reference to the core location
    Long locationId
    //general description about the training course location
    String unstructuredLocation;

    /***
     * The trainerName will take the trainer name in case it not null
     */
    Trainer trainer;
    String trainerName;

    Long  numberOfTrainee;


    String certificate //الشهادة
    Integer period //المدة
    Long unitId //الوحدة

    //general notes and information about the training course
    String note;

    /***
     * To keep history of the Employment Record when this training has been taken
     *
     * It has two sources:
     * 1) Entered from the profile screen in this case it will be selected manually from the list of employment Records
     * 2)Entered from the training module it will take the current employment Record of the employee
     *
     */
    EmploymentRecord employmentRecord
    EmployeePromotion employeeMilitaryRank

    TrackingInfo trackingInfo

    Map transientData = [:]

    static embedded = ['trackingInfo']

    static belongsTo = [employee:Employee, trainingCourse: TrainingCourse, trainingClassification: TrainingClassification, firm:Firm]


    static constraints = {
        organizationId(Constants.POSITIVE_LONG_NULLABLE +[ validator: { value, object,errors ->
            if (!value && !object.organizationName) errors.reject('trainingRecord.organizationId.error')
            return true
        }])
        organizationName (Constants.NAME_NULLABLE)
        employeeMilitaryRank nullable: true
        employmentRecord nullable: true
        trainingCourse(nullable: true, validator: { value, object,errors ->
            if (!value && !object.trainingName) errors.reject('trainingRecord.trainingCourse.error')
            return true
        })
        trainingName(Constants.NAME_NULLABLE)
        trainer(nullable: true)
        trainerName(Constants.NAME_NULLABLE)
        locationId(Constants.POSITIVE_LONG)
        trainingClassification(nullable: false)
        note(Constants.DESCRIPTION_NULLABLE+[widget: "textarea"])
        unstructuredLocation(Constants.DESCRIPTION_NULLABLE +[widget: "textarea",blank: false])
        numberOfTrainee Constants.POSITIVE_LONG_NULLABLE
        certificate(Constants.NAME_NULLABLE)
        period(nullable: true, validator: { value, object,errors ->
            if (!object?.period && object?.unitId != null) errors.reject('trainingRecord.period.error')
            return true
        })
        unitId(nullable: true, validator: { value, object,errors ->
            if (!object?.unitId && object?.period != null) errors.reject('trainingRecord.unitId.error')
            return true
        })


        trackingInfo nullable: true,display:false
    }

    static nullableValues = ['fromDate','toDate']

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

    transient springSecurityService

    static transients = ['springSecurityService','transientData','encodedId']

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