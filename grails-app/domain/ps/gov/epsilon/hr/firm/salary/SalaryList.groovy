package ps.gov.epsilon.hr.firm.salary

import ps.gov.epsilon.hr.enums.salary.v1.EnumSalaryListType
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceList

/**
 *<h1>Purpose</h1>
 * To hold the salary lists. This list contains many  records of salary list employee
 * <h1>Usage</h1>
 * Used  as to represents the salary lists in the system.
 * **/

class SalaryList extends CorrespondenceList{

    // the salary list type like STOP_SALARY,RETURN_SALARY
    EnumSalaryListType salaryListType

    public SalaryList() {
        salaryListType   = EnumSalaryListType.STOP_SALARY
    }

    static  hasMany = [salaryListEmployees:SalaryListEmployee]

    static constraints = {
        salaryListType nullable: false
    }
}
