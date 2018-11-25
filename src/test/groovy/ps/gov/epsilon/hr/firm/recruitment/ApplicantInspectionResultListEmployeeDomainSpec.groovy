package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ApplicantInspectionResultListEmployee,ps.gov.epsilon.hr.firm.recruitment.Applicant,ps.gov.epsilon.hr.firm.recruitment.ApplicantInspectionResultList])
@Domain([ApplicantInspectionResultListEmployee])
@TestMixin([HibernateTestMixin])
class ApplicantInspectionResultListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test ApplicantInspectionResultListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "applicant", value: null, testResult: TestResult.FAIL],
                [field: "applicant", value: ps.gov.epsilon.hr.firm.recruitment.Applicant.build(), testResult: TestResult.PASS],
                [field: "applicantInspectionResultList", value: null, testResult: TestResult.FAIL],
                [field: "applicantInspectionResultList", value: ps.gov.epsilon.hr.firm.recruitment.ApplicantInspectionResultList.build(), testResult: TestResult.PASS],
                [field: "applicantInspectionResultListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(ApplicantInspectionResultListEmployee,constraints)
    }
}
