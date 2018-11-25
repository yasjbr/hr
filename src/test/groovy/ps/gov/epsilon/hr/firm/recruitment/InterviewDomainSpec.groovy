package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult
import spock.lang.Unroll

import java.time.ZonedDateTime

@Build([Interview, ps.gov.epsilon.hr.firm.Firm, ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle, ps.gov.epsilon.hr.firm.recruitment.Vacancy])
@Domain([Interview])
@TestMixin([HibernateTestMixin])
class InterviewDomainSpec extends ConstraintUnitSpec {

    void "test Interview all constraints"() {
        when:
        List<Map> constraints = [
                [field: "applicants", value: null, testResult: TestResult.PASS],
                [field: "committeeRole", value: null, testResult: TestResult.PASS],
                [field: "description", value: null, testResult: TestResult.FAIL],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "interviewStatus", value: null, testResult: TestResult.FAIL],
                [field: "locationId", value: null, testResult: TestResult.FAIL],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: null, testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle.build(), testResult: TestResult.PASS],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: " ", testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "vacancy", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(Interview, constraints)
    }

    @Unroll("test interview date conflicts for #beginDate and #endDate")
    void "test date conflicts"() {

        given:
        Map props = [:]
        props.put(beginDate, ZonedDateTime.now().plusYears(4))
        props.put(endDate, ZonedDateTime.now())

        when:
        Interview interview = Interview.buildWithoutValidation(props)

        then:
        interview.validate()

        where:
        beginDate  | endDate
        'fromDate' | 'toDate'
    }
}
