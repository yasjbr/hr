package ps.gov.epsilon.hr.enums.v1

/**
 * Created by mkharma on 21/02/17.
 */
enum EnumRequestStatus {
    CREATED,//جديد
    IN_PROGRESS,//بانتظار الموافقة
    APPROVED_BY_WORKFLOW,//موافق عليه من المؤسسة
    ADD_TO_LIST,//تمت اضافته للقائمة
    SENT_BY_LIST,//بانتظار الأمر الإداري
    APPROVED,//موافق عليه
    REJECTED,//مرفوض
    FINISHED,//منتهي
    PROCESSED,//معالج
    ADD_PETITION_REQUEST,//له طلب استرحام
    CANCELED, //ملغي
    OVERRIDEN //تم نسخه
}