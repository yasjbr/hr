package ps.gov.epsilon.aoc.correspondences.promotion

import ps.gov.epsilon.aoc.correspondences.AocListRecord
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.promotion.PromotionListEmployee

import java.time.ZonedDateTime

class AocPromotionListRecord extends AocListRecord{

    static belongsTo = [promotionListEmployee:PromotionListEmployee]
    static constraints = {
    }

    static transients = ['employee', 'employmentDate', 'financialNumber']

    Employee getEmployee(){
        return promotionListEmployee?.employee
    }

    ZonedDateTime getEmploymentDate(){
        return promotionListEmployee?.employee?.employmentDate
    }

    String getFinancialNumber(){
        return promotionListEmployee?.employee?.financialNumber
    }

    @Override
    public PromotionListEmployee getHrListEmployee(){
        return promotionListEmployee
    }
}
