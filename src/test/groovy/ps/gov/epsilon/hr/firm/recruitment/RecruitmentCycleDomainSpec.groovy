package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult
import spock.lang.Unroll

import java.time.ZonedDateTime

@Build([RecruitmentCycle, ps.gov.epsilon.hr.firm.Firm])
@Domain([RecruitmentCycle])
@TestMixin([HibernateTestMixin])
class RecruitmentCycleDomainSpec extends ConstraintUnitSpec {

    void "test RecruitmentCycle all constraints"() {
        when:
        List<Map> constraints = [
                [field: "description", value: null, testResult: TestResult.PASS],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],

                [field: "endDate", value: null, testResult: TestResult.FAIL],
                [field: "endDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],

                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],

                [field: "joinedRecruitmentCycleDepartment", value: null, testResult: TestResult.PASS],

                [field: "name", value: null, testResult: TestResult.FAIL],
                [field: "name", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: "nameA1c", testResult: TestResult.PASS],

                [field: "requisitionAnnouncementStatus", value: null, testResult: TestResult.PASS],

                [field: "startDate", value: null, testResult: TestResult.FAIL],
                [field: "startDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],

                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(RecruitmentCycle, constraints)
    }

    @Unroll("test RecruitmentCycle date conflicts for #beginDate and #endDate")
    void "test date conflicts"() {

        given:
        Map props = [:]
        props.put(beginDate, ZonedDateTime.now().plusYears(4))
        props.put(endDate, ZonedDateTime.now())

        when:
        RecruitmentCycle recruitmentCycle = RecruitmentCycle.buildWithoutValidation(props)

        then:
        recruitmentCycle.validate()

        where:
        beginDate   | endDate
        'startDate' | 'endDate'
    }
}
