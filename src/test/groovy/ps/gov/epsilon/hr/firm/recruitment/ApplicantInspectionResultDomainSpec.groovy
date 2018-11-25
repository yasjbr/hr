package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult
import spock.lang.Unroll

import java.time.ZonedDateTime

@Build([ApplicantInspectionResult,ps.gov.epsilon.hr.firm.lookups.Inspection,ps.gov.epsilon.hr.firm.recruitment.ApplicantInspectionCategoryResult])
@Domain([ApplicantInspectionResult])
@TestMixin([HibernateTestMixin])
class ApplicantInspectionResultDomainSpec extends ConstraintUnitSpec {

    void "test ApplicantInspectionResult all constraints"() {
        when:
        List<Map> constraints = [
                [field: "committeeRoles", value: null, testResult: TestResult.PASS],
                [field: "executionPeriod", value: null, testResult: TestResult.PASS],
                [field: "executionPeriod", value: "executionPeriod", testResult: TestResult.PASS],
                [field: "executionPeriod", value: " ", testResult: TestResult.PASS],
                [field: "inspection", value: null, testResult: TestResult.FAIL],
                [field: "inspection", value: ps.gov.epsilon.hr.firm.lookups.Inspection.build(), testResult: TestResult.PASS],
                [field: "inspectionCategoryResult", value: null, testResult: TestResult.FAIL],
                [field: "mark", value: null, testResult: TestResult.PASS],
                [field: "mark", value: "mark", testResult: TestResult.PASS],
                [field: "mark", value: " ", testResult: TestResult.PASS],
                [field: "receiveDate", value: null, testResult: TestResult.FAIL],
                [field: "receiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "resultSummary", value: null, testResult: TestResult.PASS],
                [field: "resultSummary", value: "resultSummary", testResult: TestResult.PASS],
                [field: "resultValue", value: null, testResult: TestResult.PASS],
                [field: "resultValue", value: getLongString(251), testResult: TestResult.FAIL],
                [field: "resultValue", value: getLongString(250), testResult: TestResult.PASS],
                [field: "resultValue", value: getLongString(251), testResult: TestResult.FAIL],
                [field: "resultValue", value: getLongString(250), testResult: TestResult.PASS],
                [field: "sendDate", value: null, testResult: TestResult.FAIL],
                [field: "sendDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
        ]
        then:
        validateObject(ApplicantInspectionResult,constraints)
    }

}
