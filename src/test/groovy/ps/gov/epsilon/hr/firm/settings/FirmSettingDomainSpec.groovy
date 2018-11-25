package ps.gov.epsilon.hr.firm.settings

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([FirmSetting,ps.gov.epsilon.hr.firm.Firm])
@Domain([FirmSetting])
@TestMixin([HibernateTestMixin])
class FirmSettingDomainSpec extends ConstraintUnitSpec {

    void "test FirmSetting all constraints"() {
        when:
        List<Map> constraints = [
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "propertyName", value: null, testResult: TestResult.FAIL],
                [field: "propertyName", value: "propertyName", testResult: TestResult.PASS],
                [field: "propertyName", value: " ", testResult: TestResult.FAIL],
                [field: "propertyValue", value: null, testResult: TestResult.FAIL],
                [field: "propertyValue", value: "propertyValue", testResult: TestResult.PASS],
                [field: "propertyValue", value: " ", testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(FirmSetting,constraints)
    }
}
