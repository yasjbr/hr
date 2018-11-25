package ps.gov.epsilon.hr.firm.training.lookup

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([TrainingClassification,ps.gov.epsilon.hr.firm.Firm])
@Domain([TrainingClassification])
@TestMixin([HibernateTestMixin])
class TrainingClassificationDomainSpec extends ConstraintUnitSpec {

    void "test TrainingClassification all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.FAIL],
                [field: "code", value: "code", testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "code", value: "code",isUnique:true, testResult: TestResult.FAIL],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(TrainingClassification,constraints)
    }
}
