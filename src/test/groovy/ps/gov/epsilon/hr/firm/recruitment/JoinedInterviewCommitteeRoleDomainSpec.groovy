package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedInterviewCommitteeRole, ps.gov.epsilon.hr.firm.lookups.CommitteeRole, ps.gov.epsilon.hr.firm.recruitment.Interview])
@Domain([JoinedInterviewCommitteeRole])
@TestMixin([HibernateTestMixin])
class JoinedInterviewCommitteeRoleDomainSpec extends ConstraintUnitSpec {

    void "test JoinedInterviewCommitteeRole all constraints"() {
        when:
        List<Map> constraints = [
                [field: "committeeRole", value: null, testResult: TestResult.FAIL],
                [field: "committeeRole", value: ps.gov.epsilon.hr.firm.lookups.CommitteeRole.build(), testResult: TestResult.PASS],
                [field: "interview", value: null, testResult: TestResult.FAIL],
                [field: "interview", value: ps.gov.epsilon.hr.firm.recruitment.Interview.build(), testResult: TestResult.PASS],
                [field: "partyName", value: null, testResult: TestResult.PASS],
                [field: "partyName", value: " ", testResult: TestResult.FAIL],
                [field: "partyName", value: "partyNameA1c", testResult: TestResult.PASS],
                [field: "partyName", value: "ddddd", testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedInterviewCommitteeRole, constraints)
    }
}
