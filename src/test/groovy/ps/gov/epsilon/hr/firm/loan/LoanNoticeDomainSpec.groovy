package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([LoanNotice,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.lookups.Job])
@Domain([LoanNotice])
@TestMixin([HibernateTestMixin])
class LoanNoticeDomainSpec extends ConstraintUnitSpec {

    void "test LoanNotice all constraints"() {
        when:
        List<Map> constraints = [
                [field: "description", value: null, testResult: TestResult.PASS],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "jobTitle", value: null, testResult: TestResult.PASS],
                [field: "jobTitle", value: " ", testResult: TestResult.FAIL],
                [field: "jobTitle", value: "jobTitleA1c_", testResult: TestResult.FAIL],
//                [field: "jobTitle", value: new nl.flotsam.xeger.Xeger("(\s?[a-zA-Z\p{InArabic}0-9\.]){2,250}").generate(), testResult: TestResult.PASS],
                [field: "numberOfPositions", value: null, testResult: TestResult.FAIL],
                [field: "numberOfPositions", value: 32769, testResult: TestResult.FAIL],
                [field: "numberOfPositions", value: 32765, testResult: TestResult.PASS],
                [field: "orderDate", value: null, testResult: TestResult.FAIL],
                [field: "orderDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "periodInMonths", value: null, testResult: TestResult.FAIL],
                [field: "periodInMonths", value: 32769, testResult: TestResult.FAIL],
                [field: "periodInMonths", value: 32765, testResult: TestResult.PASS],
                [field: "requestedJob", value: null, testResult: TestResult.PASS],
                [field: "requestedJob", value: ps.gov.epsilon.hr.firm.lookups.Job.build(), testResult: TestResult.PASS],
                [field: "requesterOrganizationId", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(LoanNotice,constraints)
    }
}
