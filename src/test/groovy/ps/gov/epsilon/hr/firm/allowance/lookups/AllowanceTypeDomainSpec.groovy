package ps.gov.epsilon.hr.firm.allowance.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([AllowanceType,ps.gov.epsilon.hr.firm.Firm])
@Domain([AllowanceType])
@TestMixin([HibernateTestMixin])
class AllowanceTypeDomainSpec extends ConstraintUnitSpec {

    void "test AllowanceType all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "joinedAllowanceTypeStopReasons", value: null, testResult: TestResult.PASS],
                [field: "relationshipTypeId", value: null, testResult: TestResult.PASS],
                [field: "relationshipTypeId", value: 5, testResult: TestResult.PASS],
                [field: "relationshipTypeId", value: "Number", testResult: TestResult.FAIL],
                [field: "relationshipTypeId", value: "101", testResult: TestResult.FAIL],
                [field: "relationshipTypeId", value: -1, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(AllowanceType,constraints)
    }
}
