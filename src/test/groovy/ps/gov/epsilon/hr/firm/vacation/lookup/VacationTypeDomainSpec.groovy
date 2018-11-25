package ps.gov.epsilon.hr.firm.vacation.lookup

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([VacationType,ps.gov.epsilon.hr.firm.Firm])
@Domain([VacationType])
@TestMixin([HibernateTestMixin])

class VacationTypeDomainSpec extends ConstraintUnitSpec{

    void "test VacationType all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
                [field: "colorId", value:10L, testResult: TestResult.PASS],
        ]
        then:
        validateObject(VacationType,constraints)
    }

}
