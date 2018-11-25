package ps.gov.epsilon.hr.enums.v1

/**
 * Created by mkharma on 28/02/17.
 */
enum EnumRecruitmentCycleDepartmentStatus {

    //The translation is used as requested by QA in bug: EPHR-720

    NEW, //WHEN DEPARTMENT ASSIGNED --> تم الادراج
    VIEWED, //THE DEPARTMENT READ THE ANNOUNSMENT --> تم التبليغ
//    REMIND_ME_LATER,
//    IGNORED, //NO NEEDS FROM DEPARTMENT TO REPLAY
    CLOSED_BY_PERIOD, //THE END DATE OF THE CYCLE CLOSED --> تم اغلاق الدورة التجنيدية بدون تفاعل
    CLOSED_WITH_REPLAY //THE DEPARTMENT SEND IT RELATED NEEDS --> تم ملئ طلبات الاحتياج
}