package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([MilitaryRank,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.lookups.MilitaryRank])
@Domain([MilitaryRank])
@TestMixin([HibernateTestMixin])
class MilitaryRankDomainSpec extends ConstraintUnitSpec {

    void "test MilitaryRank all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "nextMilitaryRank", value: null, testResult: TestResult.PASS],
                [field: "nextMilitaryRank", value: ps.gov.epsilon.hr.firm.lookups.MilitaryRank.build(), testResult: TestResult.PASS],
                [field: "numberOfYearToPromote", value: null, testResult: TestResult.PASS],
                [field: "numberOfYearToPromote", value: 32769, testResult: TestResult.FAIL],
                [field: "numberOfYearToPromote", value: 32765, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(MilitaryRank,constraints)
    }
}
