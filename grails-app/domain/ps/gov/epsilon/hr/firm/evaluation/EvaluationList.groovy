package ps.gov.epsilon.hr.firm.evaluation

import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList
import ps.gov.epsilon.hr.firm.promotion.PromotionList

/**
 *<h1>Purpose</h1>
 * To hold the list of employee evaluations. This list contains many record of evaluation list employee
 * <h1>Usage</h1>
 * Used  as to represent evaluation form for the employees in promotion list
 * **/

class EvaluationList extends CorrespondenceList{

    SharedService sharedService

    static hasMany = [evaluationListEmployees:EvaluationListEmployee]

    static belongsTo = [relatedPromotionList:PromotionList]

    static constraints = {
        relatedPromotionList nullable: true
    }

    static transients = ['sharedService']

    def beforeInsert() {
        if (!this.code) {
            //auto generate the list code:
            this.code = sharedService.generateListCode(EvaluationList.getName(), "EVALUATION", 20)
        }
        super.beforeInsert()
    }
}
