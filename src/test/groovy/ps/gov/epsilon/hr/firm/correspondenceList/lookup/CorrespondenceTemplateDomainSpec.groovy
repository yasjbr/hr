package ps.gov.epsilon.hr.firm.correspondenceList.lookup

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([CorrespondenceTemplate,ps.gov.epsilon.hr.firm.Firm])
@Domain([CorrespondenceTemplate])
@TestMixin([HibernateTestMixin])
class CorrespondenceTemplateDomainSpec extends ConstraintUnitSpec {

    void "test CorrespondenceTemplate all constraints"() {
        when:
        List<Map> constraints = [
                [field: "coverLetter", value: null, testResult: TestResult.PASS],
                [field: "coverLetter", value: " ", testResult: TestResult.FAIL],
                [field: "coverLetter", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "coverLetter", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(CorrespondenceTemplate,constraints)
    }
}
