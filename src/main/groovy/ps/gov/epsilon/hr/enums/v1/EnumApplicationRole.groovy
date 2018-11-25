package ps.gov.epsilon.hr.enums.v1

public enum EnumApplicationRole {

    ROLE_USER("ROLE_EPHR_USER", "مسؤول عن العمليات التي تخصه فقط"),
    ROLE_HR_DEPARTMENT("ROLE_EPHR_HR_DEPARTMENT", "مسؤول عن جميع العمليات الادارية ما عدا الصلاحيات و الجداول الثابته"),
    ROLE_LOOKUPS_ADMIN("ROLE_EPHR_LOOKUPS_ADMIN", "مسؤول عن الجداول الثابته فقط"),
    ROLE_SUPER_ADMIN("ROLE_EPHR_SUPER_ADMIN", "مسؤول عن صلاحيات المدير"),
    ROLE_SYSTEM_ADMIN("ROLE_EPHR_SYSTEM_ADMIN", "مسؤول عن اعدادات النظام"),
    ROLE_AUDIT("ROLE_EPHR_AUDIT", "مسؤول عن سجلات التدقيق"),
    ROLE_AOC("ROLE_AOC_FIRM", "مسؤول عن العمليات الخاصة بهيئة التنظيم والإدارة "),
    ROLE_AOC_VACATION_LIST("ROLE_AOC_VACATION_LIST", "مسؤول عن انشاء الصادر و الوارد للاجازات في هيئة التنظيم و الادارة"),
    ROLE_AOC_ALLOWANCE_LIST("ROLE_AOC_ALLOWANCE_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات العلاوات في هيئة التنظيم و الادارة"),
    ROLE_AOC_PROMOTION_LIST("ROLE_AOC_PROMOTION_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات الترقيات في هيئة التنظيم و الادارة"),
    ROLE_AOC_CHILD_LIST("ROLE_AOC_CHILD_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات المواليد في هيئة التنظيم و الادارة"),
    ROLE_AOC_VIOLATION_LIST("ROLE_AOC_VIOLATION_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات المخالفات في هيئة التنظيم و الادارة"),
    ROLE_AOC_MARITAL_STATUS_LIST("ROLE_AOC_MARITAL_STATUS_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات تعديل الحالة الاجتماعية في هيئة التنظيم و الادارة"),
    ROLE_AOC_EXTERNAL_TRANSFER_LIST("ROLE_AOC_EXTERNAL_TRANSFER_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات النقل الخارجية في هيئة التنظيم و الادارة"),
    ROLE_AOC_DISCIPLINARY_LIST("ROLE_AOC_DISCIPLINARY_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات العقوبات في هيئة التنظيم و الادارة"),
    ROLE_AOC_SUSPENSION_LIST("ROLE_AOC_SUSPENSION_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات الاستيداع في هيئة التنظيم و الادارة"),
    ROLE_AOC_LOAN_LIST("ROLE_AOC_LOAN_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات الندب في هيئة التنظيم و الادارة"),
    ROLE_AOC_LOAN_NOTICE_REPLAY_LIST("ROLE_AOC_LOAN_NOTICE_REPLAY_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات اشعارات الندب في هيئة التنظيم و الادارة"),
    ROLE_AOC_EVALUATION_LIST("ROLE_AOC_EVALUATION_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات التقييمات في هيئة التنظيم و الادارة"),
    ROLE_AOC_RETURN_FROM_ABSENCE_LIST("ROLE_AOC_RETURN_FROM_ABSENCE_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات اشعارات العودة في هيئة التنظيم و الادارة"),
    ROLE_AOC_DISPATCH_LIST("ROLE_AOC_DISPATCH_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات الايفاد في هيئة التنظيم و الادارة"),
    ROLE_AOC_RETURN_TO_SERVICE_LIST("ROLE_AOC_RETURN_TO_SERVICE_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات استدعاء للخدمة في هيئة التنظيم و الادارة"),
    ROLE_AOC_END_OF_SERVICE_LIST("ROLE_AOC_END_OF_SERVICE_LIST", "مسؤول عن انشاء الصادر و الوارد لمراسلات انهاء الخدمة في هيئة التنظيم و الادارة"),

    private final String authority;
    private final String description;

    EnumApplicationRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    String getAuthority() {
        return this.authority;
    }

    String getValue() {
        return this.authority;
    }

    String getDescription() {
        return this.description;
    }

    String toString() {
        return this.authority
    }

}