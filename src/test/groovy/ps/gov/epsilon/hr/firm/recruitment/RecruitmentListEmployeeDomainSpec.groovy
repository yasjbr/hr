package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([RecruitmentListEmployee,ps.gov.epsilon.hr.firm.recruitment.Applicant])
@Domain([RecruitmentListEmployee])
@TestMixin([HibernateTestMixin])
class RecruitmentListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test RecruitmentListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "applicant", value: null, testResult: TestResult.FAIL],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: "orderNo", testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "recruitmentList", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(RecruitmentListEmployee,constraints)
    }
}
