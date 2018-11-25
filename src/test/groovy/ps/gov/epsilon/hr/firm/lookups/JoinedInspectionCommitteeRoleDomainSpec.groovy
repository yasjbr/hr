package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedInspectionCommitteeRole,ps.gov.epsilon.hr.firm.lookups.CommitteeRole,ps.gov.epsilon.hr.firm.lookups.Inspection])
@Domain([JoinedInspectionCommitteeRole])
@TestMixin([HibernateTestMixin])
class JoinedInspectionCommitteeRoleDomainSpec extends ConstraintUnitSpec {

    void "test JoinedInspectionCommitteeRole all constraints"() {
        when:
        List<Map> constraints = [
                [field: "committeeRole", value: null, testResult: TestResult.FAIL],
                [field: "committeeRole", value: ps.gov.epsilon.hr.firm.lookups.CommitteeRole.build(), testResult: TestResult.PASS],
                [field: "inspection", value: null, testResult: TestResult.FAIL],
                [field: "inspection", value: ps.gov.epsilon.hr.firm.lookups.Inspection.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedInspectionCommitteeRole,constraints)
    }
}
