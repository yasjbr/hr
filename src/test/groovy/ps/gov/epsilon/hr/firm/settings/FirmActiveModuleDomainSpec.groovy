package ps.gov.epsilon.hr.firm.settings

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([FirmActiveModule,ps.gov.epsilon.hr.firm.Firm])
@Domain([FirmActiveModule])
@TestMixin([HibernateTestMixin])
class FirmActiveModuleDomainSpec extends ConstraintUnitSpec {

    void "test FirmActiveModule all constraints"() {
        when:
        List<Map> constraints = [
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "systemModule", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(FirmActiveModule,constraints)
    }
}
