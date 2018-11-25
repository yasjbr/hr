package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ApplicantInspectionCategoryResult,ps.gov.epsilon.hr.firm.recruitment.Applicant,ps.gov.epsilon.hr.firm.lookups.InspectionCategory])
@Domain([ApplicantInspectionCategoryResult])
@TestMixin([HibernateTestMixin])
class ApplicantInspectionCategoryResultDomainSpec extends ConstraintUnitSpec {

    void "test ApplicantInspectionCategoryResult all constraints"() {
        when:
        List<Map> constraints = [
                [field: "applicant", value: null, testResult: TestResult.FAIL],
                [field: "committeeRoles", value: null, testResult: TestResult.PASS],
                [field: "inspectionCategory", value: null, testResult: TestResult.FAIL],
                [field: "inspectionCategory", value: ps.gov.epsilon.hr.firm.lookups.InspectionCategory.build(), testResult: TestResult.PASS],
                [field: "inspectionResult", value: null, testResult: TestResult.FAIL],
                [field: "inspectionResultRate", value: null, testResult: TestResult.PASS],
                [field: "mark", value: null, testResult: TestResult.PASS],
                [field: "mark", value: "mark", testResult: TestResult.PASS],
                [field: "mark", value: " ", testResult: TestResult.PASS],
                [field: "requestDate", value: null, testResult: TestResult.FAIL],
                [field: "requestDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "resultSummary", value: null, testResult: TestResult.PASS],
                [field: "resultSummary", value: "resultSummary", testResult: TestResult.PASS],
                [field: "testsResult", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(ApplicantInspectionCategoryResult,constraints)
    }
}
