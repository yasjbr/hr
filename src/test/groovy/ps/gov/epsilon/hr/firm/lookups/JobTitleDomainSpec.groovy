package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JobTitle, ps.gov.epsilon.hr.firm.Firm, ps.gov.epsilon.hr.firm.lookups.JobCategory])
@Domain([JobTitle])
@TestMixin([HibernateTestMixin])
class JobTitleDomainSpec extends ConstraintUnitSpec {

    void "test JobTitle all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.FAIL],
                [field: "code", value: "code", testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "jobCategory", value: null, testResult: TestResult.FAIL],
                [field: "jobCategory", value: ps.gov.epsilon.hr.firm.lookups.JobCategory.build(), testResult: TestResult.PASS],
                [field: "joinedJobTitleEducationDegree", value: null, testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(JobTitle, constraints)
    }
}
