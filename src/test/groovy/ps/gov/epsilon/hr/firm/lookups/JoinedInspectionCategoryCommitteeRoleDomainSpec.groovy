package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedInspectionCategoryCommitteeRole,ps.gov.epsilon.hr.firm.lookups.CommitteeRole,ps.gov.epsilon.hr.firm.lookups.InspectionCategory])
@Domain([JoinedInspectionCategoryCommitteeRole])
@TestMixin([HibernateTestMixin])
class JoinedInspectionCategoryCommitteeRoleDomainSpec extends ConstraintUnitSpec {

    void "test JoinedInspectionCategoryCommitteeRole all constraints"() {
        when:
        List<Map> constraints = [
                [field: "committeeRole", value: null, testResult: TestResult.FAIL],
                [field: "committeeRole", value: ps.gov.epsilon.hr.firm.lookups.CommitteeRole.build(), testResult: TestResult.PASS],
                [field: "inspectionCategory", value: null, testResult: TestResult.FAIL],
                [field: "inspectionCategory", value: ps.gov.epsilon.hr.firm.lookups.InspectionCategory.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedInspectionCategoryCommitteeRole,constraints)
    }
}
