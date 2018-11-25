package ps.gov.epsilon.hr.firm.profile.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([EmployeeStatusCategory, ps.gov.epsilon.hr.firm.Firm])
@Domain([EmployeeStatusCategory])
@TestMixin([HibernateTestMixin])
class EmployeeStatusCategoryDomainSpec extends ConstraintUnitSpec {

    void "test EmployeeStatusCategory all constraints"() {
        when:
        List<Map> constraints = [
                [field: "description", value: null, testResult: TestResult.PASS],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(EmployeeStatusCategory, constraints)
    }
}
