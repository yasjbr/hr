package ps.gov.epsilon.hr.enums.promotion.v1

/**
 * Created by mkharma on 07/03/17.
 */
enum EnumPromotionReason {
    ELIGIBLE,//EmployeePromotion
    EXCEPTIONAL,//from any employee
    EXCEPTIONAL_REQUEST,//from any employee
    ELIGIBLE_REQUEST,//from eligible employee
    SITUATION_SETTLEMENT,
    PERIOD_SETTLEMENT_OLD_ARREST,
    PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD,
    PERIOD_SETTLEMENT,
    PERIOD_SETTLEMENT_CURRENT_ARREST,
    UPDATE_MILITARY_RANK_TYPE,
    UPDATE_MILITARY_RANK_CLASSIFICATION,
    OTHERS//from other employee
}