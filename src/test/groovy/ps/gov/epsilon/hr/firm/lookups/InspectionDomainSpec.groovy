package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([Inspection, ps.gov.epsilon.hr.firm.Firm, ps.gov.epsilon.hr.firm.lookups.InspectionCategory])
@Domain([Inspection])
@TestMixin([HibernateTestMixin])
class InspectionDomainSpec extends ConstraintUnitSpec {

    void "test Inspection all constraints"() {
        when:
        List<Map> constraints = [
                [field: "committeeRoles", value: null, testResult: TestResult.PASS],
                [field: "description", value: null, testResult: TestResult.PASS],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "hasMark", value: null, testResult: TestResult.FAIL],
                [field: "hasPeriod", value: null, testResult: TestResult.FAIL],
                [field: "inspectionCategory", value: null, testResult: TestResult.FAIL],
                [field: "inspectionCategory", value: ps.gov.epsilon.hr.firm.lookups.InspectionCategory.build(), testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "orderId", value: null, testResult: TestResult.FAIL],
                [field: "orderId", value: 32769, testResult: TestResult.FAIL],
                [field: "orderId", value: 32765, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(Inspection, constraints)
    }
}
