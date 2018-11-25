package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([RequisitionWorkExperience])
@Domain([RequisitionWorkExperience])
@TestMixin([HibernateTestMixin])
class RequisitionWorkExperienceDomainSpec extends ConstraintUnitSpec {

    void "test RequisitionWorkExperience all constraints"() {
        when:
        List<Map> constraints = [
                [field: "competency", value: null, testResult: TestResult.PASS],
                [field: "otherSpecifications", value: null, testResult: TestResult.PASS],
                [field: "otherSpecifications", value: " ", testResult: TestResult.FAIL],
                [field: "otherSpecifications", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "otherSpecifications", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "periodInYears", value: null, testResult: TestResult.FAIL],
                [field: "periodInYears", value: 32769, testResult: TestResult.FAIL],
                [field: "periodInYears", value: 32765, testResult: TestResult.PASS],
                [field: "professionType", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(RequisitionWorkExperience,constraints)
    }

    void "test custom validation for otherSpecifications"() {
        when:
        RequisitionWorkExperience requisitionWorkExperienceFail = RequisitionWorkExperience.buildWithoutValidation(otherSpecifications:null,workExperience:null)
        RequisitionWorkExperience requisitionWorkExperiencePass = RequisitionWorkExperience.buildWithoutValidation(otherSpecifications:"otherSpecifications")

        then:
        !requisitionWorkExperienceFail.validate()
        requisitionWorkExperienceFail.errors.allErrors.toString().contains("RequisitionWorkExperience.otherSpecifications.error")
        requisitionWorkExperiencePass.validate()
        !requisitionWorkExperiencePass?.hasErrors()
    }
}
