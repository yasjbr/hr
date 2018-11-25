package ps.gov.epsilon.hr.firm.disciplinary.lookup

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([DisciplinaryListJudgmentSetup, ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory, ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment, ps.gov.epsilon.hr.firm.Firm])
@Domain([DisciplinaryListJudgmentSetup])
@TestMixin([HibernateTestMixin])
class DisciplinaryListJudgmentSetupDomainSpec extends ConstraintUnitSpec {

    void "test DisciplinaryListJudgmentSetup all constraints"() {
        when:
        List<Map> constraints = [
                [field: "disciplinaryCategory", value: null, testResult: TestResult.FAIL],
                [field: "disciplinaryCategory", value: ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryCategory.build(), testResult: TestResult.PASS],
                [field: "disciplinaryJudgment", value: null, testResult: TestResult.FAIL],
                [field: "disciplinaryJudgment", value: ps.gov.epsilon.hr.firm.disciplinary.lookup.DisciplinaryJudgment.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "listNamePrefix", value: null, testResult: TestResult.PASS],
                [field: "listNamePrefix", value: " ", testResult: TestResult.FAIL],
                [field: "listNamePrefix", value: "listNamePrefixA1c_", testResult: TestResult.FAIL],
                [field: "listNamePrefix", value: "disciplinaryList", testResult: TestResult.PASS],
        ]
        then:
        validateObject(DisciplinaryListJudgmentSetup, constraints)
    }
}
