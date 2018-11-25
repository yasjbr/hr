package ps.gov.epsilon.hr.firm.profile

import grails.util.Holders
import ps.gov.epsilon.aoc.firm.employee.EmployeeSalaryInfo
import ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus
import ps.gov.epsilon.hr.firm.Firm

//import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequest
import ps.gov.epsilon.hr.firm.lookups.AttendanceType
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.recruitment.Applicant
import ps.gov.epsilon.hr.firm.training.TrainingRecord
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.Period
import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the employee information
 * <h1>Usage</h1>
 * Used  as to represents the employee information
 * **/
class Employee {

    static auditable = true

    String id

    String encodedId

    Long personId

    Map transientData = [:]

    //تستخدم في الندب,الاعارة والنقل الخارجي
    Long sourceOrganizationId

    /**
     * Employment information
     * */
    //تاريخ الاخذ
    ZonedDateTime employmentDate;
    //the date when the employee joins the organization that worked in
    ZonedDateTime joinDate;
    String financialNumber
    String militaryNumber
    String archiveNumber

    //example police id
    String internalId

    EmploymentRecord currentEmploymentRecord
    EmployeePromotion currentEmployeeMilitaryRank

    AttendanceType attendanceType

    /**
     * reference to the core firm
     * */
    //the bank branch part of the employee banking information, each bank has many branches
    Long bankBranchId

    String bankAccountNumber;

    // the date of the current employee attendance status like "available, in vacation .... "
    ZonedDateTime attendanceStatusDate
    // the date of the current employee status. The employee category status is like "Committed, uncommitted..."
    ZonedDateTime categoryStatusDate;

    //Represent the category status as "Committed, uncommitted..."
    EmployeeStatusCategory categoryStatus

    // filled from the related applicant
    Applicant applicant

    Double employmentPeriodInMonths


    ZonedDateTime orderDate //تاريخ الامر الاداري
    ZonedDateTime yearsServiceDate //تاريخ سنوات الخدمة
    String employmentNumber //رقم امر الاخذ
    String computerNumber //رقم الحاسوب
    String internationalAccountNumber //IBAN

    /** Added to lock employee profile after receiving profile notice*/
    EnumProfileStatus profileStatus= EnumProfileStatus.ACTIVE

    TrackingInfo trackingInfo

    static embedded = ['trackingInfo']

    static belongsTo = [firm: Firm]

    //used to indicate the properties that is part of date scanning that the listener implement to set the default value of the date
    static includeInValidate = ['employeeMilitaryRank']

    static nullableValues = ['yearsServiceDate']

    static hasMany = [employeeMilitaryRank    : EmployeePromotion,
                      eploymentRecord         : EmploymentRecord,
                      employeeOperationalTasks: JoinedEmployeeOperationalTasks,
                      trainingRecords         : TrainingRecord,
                      employeeStatusHistories : EmployeeStatusHistory,
                      notes                   : ProfileNote,
                      employeeProfileStatusHistories: EmployeeProfileStatusHistory,
                      employeeSalaryInfos     : EmployeeSalaryInfo  ]

    static constraints = {
        applicant nullable: true, widget: "autocomplete"
//        employmentPeriod(Constants.POSITIVE_DOUBLE_NULLABLE)
        sourceOrganizationId(Constants.POSITIVE_LONG_NULLABLE)
        employeeMilitaryRank nullable: true, widget: "autocomplete"
        currentEmploymentRecord nullable: true
        currentEmployeeMilitaryRank nullable: true
        personId(Constants.POSITIVE_LONG)
        //employmentNumber(Constants.DOCUMENT_NUMBER)
        employmentNumber(Constants.LOOKUP_NAME_NULLABLE)
        computerNumber(Constants.DOCUMENT_NUMBER_NULLABLE)
        financialNumber nullable: true,unique: true
        militaryNumber nullable: true
        archiveNumber nullable: true
        internalId(Constants.DESCRIPTION_NULLABLE)
        attendanceType nullable: true, widget: "autocomplete"
        bankBranchId(Constants.POSITIVE_LONG_NULLABLE)
        bankAccountNumber(Constants.DOCUMENT_NUMBER_NULLABLE)
        internationalAccountNumber(Constants.DOCUMENT_NUMBER_NULLABLE)
        trackingInfo nullable: true, display: false
        profileStatus nullable: true
    }

    static mapping = {
        joinDate type: PersistentDocumentaryDate, {
            column name: 'join_date_datetime'
            column name: 'join_date_date_tz'
        }
        employmentDate type: PersistentDocumentaryDate, {
            column name: 'employment_date_datetime'
            column name: 'employment_date_date_tz'
        }
        attendanceStatusDate type: PersistentDocumentaryDate, {
            column name: 'attendance_status_date_datetime'
            column name: 'attendance_status_date_date_tz'
        }
        categoryStatusDate type: PersistentDocumentaryDate, {
            column name: 'category_status_date_datetime'
            column name: 'category_status_date_date_tz'
        }

        orderDate type: PersistentDocumentaryDate, {
            column name: 'order_date_date_datetime'
            column name: 'order_date_date_date_tz'
        }

        yearsServiceDate type: PersistentDocumentaryDate, {
            column name: 'years_service_date_date_datetime'
            column name: 'years_service_date_date_date_tz'
        }
      //  employmentPeriod formula: "date_part('month',age(employment_date_datetime))+(date_part('year',age(employment_date_datetime))*12)"
    }

    transient springSecurityService

    def sharedService

    static transients = ['springSecurityService', 'encodedId', 'transientData','employmentPeriodInMonths', 'employeeSalaryInfo']

    def beforeInsert() {

        if (!this.computerNumber) {
            //auto generate the computer code:
            this.computerNumber = sharedService?.generateListCode("ps.gov.epsilon.hr.firm.profile.Employee", "E", 20)
        }

        if(!this.archiveNumber){
            this.archiveNumber = this.computerNumber
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

    Double getEmploymentPeriodInMonths() {
        Period period = Period.between(employmentDate?.toLocalDate(),  ZonedDateTime.now().toLocalDate());
        return (period.months+(period.years*12.0))
    }

    EmployeeSalaryInfo getEmployeeSalaryInfo(){
        return employeeSalaryInfos?.find{it.active==true}
    }

    @Override
    public String toString() {
        if (transientData?.personDTO?.id) {
            String employeeInfo = this?.currentEmployeeMilitaryRank?.militaryRank?.toString()
            if (this?.currentEmployeeMilitaryRank?.militaryRankClassification) {
                employeeInfo = employeeInfo + " " + this?.currentEmployeeMilitaryRank?.militaryRankClassification?.toString()
            }
            if (this?.currentEmployeeMilitaryRank?.militaryRankType) {
                employeeInfo = employeeInfo + " " + this?.currentEmployeeMilitaryRank?.militaryRankType?.toString()
            }
            employeeInfo = employeeInfo + " / " + transientData?.personDTO?.localFullName
            return employeeInfo
        } else {
            return personId.toString();
        }
    }
}
