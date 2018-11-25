package ps.gov.epsilon.hr.firm.promotion

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants

/**
 *<h1>Purpose</h1>
 *  This request is initialized when an employee demands a new rank based on
 *  1- situation_settlement : تسوية وضع بسبب درجة علمية
 *  2- period_settlement_old_arrest : تسوية وضع بسبب اعتقال سابق
 *  3- period_settlement_employment_period : تعديل سنوات الخدمة
 *  4- period_settlement : احتساب مدة بدل فاقد
 *  5- period_settlement_current_arrest : تسوية وضع بسبب اعتقال حالي
 *  6- exceptional : استثنائي
 *  7- eligible : استحقاق
 * <h1>Usage</h1>
 *  Used as to represent the education level that the employee reached and his demands to a new rank based on employee education level.
 *<h1>Example</h1>
 *  if an employee reaches a new education level like "Master" or "PhD", he request a Situation Settlement to get a promotion.
 *
 **/
class PromotionRequest extends Request{

    static auditable = true

    //the identifier of education degree
    Long educationDegreeId

    public PromotionRequest() {
        requestType = EnumRequestType.ELIGIBLE_REQUEST
    }

    static constraints = {
        educationDegreeId (Constants.POSITIVE_LONG_NULLABLE + [validator: { value, object,errors ->
            if (object?.requestType==EnumRequestType.SITUATION_SETTLEMENT && !value)
                errors.reject('PromotionRequest.educationDegreeId.error.required')
            return true
        }])
        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }
}
