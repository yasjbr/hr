package ps.gov.epsilon.hr.firm.training

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.training.v1.EnumTrainingStatus
import ps.gov.epsilon.hr.firm.training.evaluation.JoinedEvaluationFormTrainingCategory
import ps.gov.epsilon.hr.firm.training.lookup.TrainingClassification
import ps.police.common.domains.v1.TrackingInfo
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * To hold the training course, a training course can be in the annual training plan or not included in it
 * <h1>Usage</h1>
 * Used  as to represents the container of all training courses in the system.
 * **/

public class TrainingCourse {

    String id

    //the code of the training course
    String courseCode;
    DescriptionInfo descriptionInfo

    String arabicDescription
    String englishDescription

    //the status of the training course (under development, activated, cancelled or INACTIVE)
    EnumTrainingStatus trainingStatus=EnumTrainingStatus.UNDER_DEVELOPMENT

    //the training course target group
    TargetGroup targetGroup
    //the training course trainer condition
    TrainerCondition trainerCondition

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo','descriptionInfo']

    static belongsTo = [trainingClassification: TrainingClassification]

    static hasMany = [trainingObjectives: TrainingObjective,
                       trainingConditions: TrainingCondition,
                       prerequisiteCourses: JoinedCoursesPrerequisite,
                       trainingCategoryEvaluationForm:JoinedEvaluationFormTrainingCategory]


    static constraints = {
        courseCode(Constants.STRING+[unique: true])
        arabicDescription(Constants.DESCRIPTION_NULLABLE)
        englishDescription(Constants.DESCRIPTION_NULLABLE)
        trainingStatus nullable: false
        targetGroup nullable: true,widget:"autocomplete"
        trainerCondition nullable: true,widget:"autocomplete"
        descriptionInfo widget:"DescriptionInfo"
        trackingInfo nullable: true,display:false
    }


    static mapping = {
        arabicDescription type: 'text'
        englishDescription type: 'text'
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
    }

    def beforeUpdate() {def applicationName = Holders.grailsApplication.config?.grails?.applicationName;if(!applicationName)applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username?:applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }


    @Override
    String toString(){
        return this?.descriptionInfo?.toString()
    }
}