package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JoinedJobTitleEducationDegree,ps.gov.epsilon.hr.firm.lookups.JobTitle])
@Domain([JoinedJobTitleEducationDegree])
@TestMixin([HibernateTestMixin])
class JoinedJobTitleEducationDegreeDomainSpec extends ConstraintUnitSpec {

    void "test JoinedJobTitleEducationDegree all constraints"() {
        when:
        List<Map> constraints = [
                [field: "educationDegreeId", value: null, testResult: TestResult.FAIL],
                [field: "jobTitle", value: null, testResult: TestResult.FAIL],
                [field: "jobTitle", value: ps.gov.epsilon.hr.firm.lookups.JobTitle.build(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(JoinedJobTitleEducationDegree,constraints)
    }
}
