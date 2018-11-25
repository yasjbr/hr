package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([VacancyAdvertisements,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle])
@Domain([VacancyAdvertisements])
@TestMixin([HibernateTestMixin])
class VacancyAdvertisementsDomainSpec extends ConstraintUnitSpec {

    void "test VacancyAdvertisements all constraints"() {
        when:
        List<Map> constraints = [
                [field: "closingDate", value: null, testResult: TestResult.FAIL],
                [field: "closingDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "description", value: null, testResult: TestResult.PASS],
                [field: "description", value: " ", testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "description", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "postingDate", value: null, testResult: TestResult.FAIL],
                [field: "postingDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: null, testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle.build(), testResult: TestResult.PASS],
                [field: "title", value: null, testResult: TestResult.FAIL],
                [field: "title", value: " ", testResult: TestResult.FAIL],
                [field: "title", value: "titleA1c_", testResult: TestResult.FAIL],
                [field: "title", value: "Vacancy Advertisement", testResult: TestResult.PASS],
                [field: "toBePostedOn", value: null, testResult: TestResult.PASS],
                [field: "toBePostedOn", value: " ", testResult: TestResult.FAIL],
                [field: "toBePostedOn", value: "toBePostedOnA1c_", testResult: TestResult.FAIL],
                [field: "toBePostedOn", value: "news paper", testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "vacancies", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(VacancyAdvertisements,constraints)
    }
}
