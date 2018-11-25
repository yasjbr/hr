package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([RecruitmentListEmployeeNote, ps.gov.epsilon.hr.firm.recruitment.RecruitmentListEmployee])
@Domain([RecruitmentListEmployeeNote])
@TestMixin([HibernateTestMixin])
class RecruitmentListEmployeeNoteDomainSpec extends ConstraintUnitSpec {

    void "test RecruitmentListEmployeeNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.PASS],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "recruitmentListEmployee", value: null, testResult: TestResult.FAIL],
                [field: "recruitmentListEmployee", value: ps.gov.epsilon.hr.firm.recruitment.RecruitmentListEmployee.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(RecruitmentListEmployeeNote, constraints)
    }
}
