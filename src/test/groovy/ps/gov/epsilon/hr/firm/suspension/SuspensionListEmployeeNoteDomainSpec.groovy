package ps.gov.epsilon.hr.firm.suspension

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([SuspensionListEmployeeNote, ps.gov.epsilon.hr.firm.suspension.SuspensionListEmployee])
@Domain([SuspensionListEmployeeNote])
@TestMixin([HibernateTestMixin])
class SuspensionListEmployeeNoteDomainSpec extends ConstraintUnitSpec {

    void "test SuspensionListEmployeeNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "orderNo", value: "orderNoA1c_", testResult: TestResult.FAIL],
                [field: "orderNo", value: "33333333", testResult: TestResult.PASS],
                [field: "suspensionListEmployee", value: null, testResult: TestResult.FAIL],
                [field: "suspensionListEmployee", value: ps.gov.epsilon.hr.firm.suspension.SuspensionListEmployee.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(SuspensionListEmployeeNote, constraints)
    }
}
