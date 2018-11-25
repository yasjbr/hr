package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([RecruitmentCyclePhase,ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle])
@Domain([RecruitmentCyclePhase])
@TestMixin([HibernateTestMixin])
class RecruitmentCyclePhaseDomainSpec extends ConstraintUnitSpec {

    void "test RecruitmentCyclePhase all constraints"() {
        when:
        List<Map> constraints = [
                [field: "fromDate", value: null, testResult: TestResult.FAIL],
                [field: "fromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: null, testResult: TestResult.FAIL],
                [field: "recruitmentCycle", value: ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle.build(), testResult: TestResult.PASS],
                [field: "requisitionAnnouncementStatus", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: null, testResult: TestResult.FAIL],
                [field: "toDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(RecruitmentCyclePhase,constraints)
    }
}
