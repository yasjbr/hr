package ps.gov.epsilon.hr.firm.profile

import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion

/**
 * <h1>Purpose</h1>
 * To hold the employment record and the employee military rank many-to-many relation
 * **/

class JoinedEmploymentMilitaryRank {

    String id

    static belongsTo = [employee: Employee, employeeMilitaryRank:EmployeePromotion, employmentRecord:EmploymentRecord]

    static constraints = {
    }
}
