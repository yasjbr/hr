package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([Province,java.util.Map])
@Domain([Province])
@TestMixin([HibernateTestMixin])
class ProvinceDomainSpec extends ConstraintUnitSpec {

    void "test Province all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "provinceFirms", value: null, testResult: TestResult.PASS],
                [field: "provinceLocations", value: null, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "transientData", value: null, testResult: TestResult.PASS],
                [field: "transientData", value: java.util.Map.build(), testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.FAIL],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.PASS],
        ]
        then:
        validateObject(Province,constraints)
    }
}
