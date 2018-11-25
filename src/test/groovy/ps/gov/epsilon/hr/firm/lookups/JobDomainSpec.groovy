package ps.gov.epsilon.hr.firm.lookups

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([Job,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.lookups.JobCategory])
@Domain([Job])
@TestMixin([HibernateTestMixin])
class JobDomainSpec extends ConstraintUnitSpec {

    void "test Job all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.FAIL],
                [field: "code", value: "code", testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromAge", value: null, testResult: TestResult.PASS],
                [field: "fromAge", value: 16, testResult: TestResult.FAIL],
                [field: "fromAge", value: 20, testResult: TestResult.PASS],
                [field: "fromAge", value: 32, testResult: TestResult.FAIL],
                [field: "fromAge", value: 28, testResult: TestResult.PASS],
                [field: "fromWeight", value: null, testResult: TestResult.PASS],
                [field: "fromWeight", value: 32769, testResult: TestResult.FAIL],
                [field: "fromWeight", value: 32765, testResult: TestResult.PASS],
                [field: "jobCategory", value: null, testResult: TestResult.FAIL],
                [field: "jobCategory", value: ps.gov.epsilon.hr.firm.lookups.JobCategory.build(), testResult: TestResult.PASS],
                [field: "joinedJobEducationDegrees", value: null, testResult: TestResult.PASS],
                [field: "joinedJobEducationMajors", value: null, testResult: TestResult.PASS],
                [field: "joinedJobInspectionCategories", value: null, testResult: TestResult.PASS],
                [field: "joinedJobMilitaryRanks", value: null, testResult: TestResult.PASS],
                [field: "joinedJobOperationalTasks", value: null, testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "toAge", value: null, testResult: TestResult.PASS],
                [field: "toAge", value: 16, testResult: TestResult.FAIL],
                [field: "toAge", value: 20, testResult: TestResult.PASS],
                [field: "toAge", value: 32, testResult: TestResult.FAIL],
                [field: "toAge", value: 28, testResult: TestResult.PASS],
                [field: "toWeight", value: null, testResult: TestResult.PASS],
                [field: "toWeight", value: 32769, testResult: TestResult.FAIL],
                [field: "toWeight", value: 32765, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: null, testResult: TestResult.PASS],
                [field: "universalCode", value: "universalCode", testResult: TestResult.PASS],
                [field: "universalCode", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(Job,constraints)
    }
}
