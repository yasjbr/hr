package ps.gov.epsilon.hr.firm.general

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([GeneralListEmployee,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.general.GeneralList])
@Domain([GeneralListEmployee])
@TestMixin([HibernateTestMixin])
class GeneralListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test GeneralListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "employee", value: null, testResult: TestResult.FAIL],
                [field: "employee", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "employeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "employeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "generalList", value: null, testResult: TestResult.FAIL],
                [field: "generalList", value: ps.gov.epsilon.hr.firm.general.GeneralList.build(), testResult: TestResult.PASS],
                [field: "generalListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(GeneralListEmployee,constraints)
    }
}
