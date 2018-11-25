package ps.gov.epsilon.hr.firm.allowance

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([AllowanceListEmployeeNote, ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployee])
@Domain([AllowanceListEmployeeNote])
@TestMixin([HibernateTestMixin])
class AllowanceListEmployeeNoteDomainSpec extends ConstraintUnitSpec {

    void "test AllowanceListEmployeeNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "allowanceListEmployee", value: null, testResult: TestResult.FAIL],
                [field: "allowanceListEmployee", value: ps.gov.epsilon.hr.firm.allowance.AllowanceListEmployee.build(), testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: "note", testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: 1243, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(AllowanceListEmployeeNote, constraints)
    }
}
