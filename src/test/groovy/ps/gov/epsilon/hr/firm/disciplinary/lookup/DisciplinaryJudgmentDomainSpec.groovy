package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([DisciplinaryJudgment, ps.gov.epsilon.hr.firm.Firm])
@Domain([DisciplinaryJudgment])
@TestMixin([HibernateTestMixin])
class DisciplinaryJudgmentDomainSpec extends ConstraintUnitSpec {

    void "test DisciplinaryJudgment all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "joinedDisciplinaryJudgmentReasons", value: null, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
                [field: "isCurrencyUnit", value: true, testResult: TestResult.PASS],
                [field: "isCurrencyUnit", value: false, testResult: TestResult.PASS],
        ]
        then:
        validateObject(DisciplinaryJudgment, constraints)
    }
}
