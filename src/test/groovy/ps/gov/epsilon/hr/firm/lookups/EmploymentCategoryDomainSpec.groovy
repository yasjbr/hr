package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([EmploymentCategory,ps.gov.epsilon.hr.firm.Firm])
@Domain([EmploymentCategory])
@TestMixin([HibernateTestMixin])
class EmploymentCategoryDomainSpec extends ConstraintUnitSpec {

    void "test EmploymentCategory all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(EmploymentCategory,constraints)
    }
}
