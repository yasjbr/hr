package ps.gov.epsilon.hr.firm.settings

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([FirmDocument,ps.gov.epsilon.hr.firm.Firm])
@Domain([FirmDocument])
@TestMixin([HibernateTestMixin])
class FirmDocumentDomainSpec extends ConstraintUnitSpec {

    void "test FirmDocument all constraints"() {
        when:
        List<Map> constraints = [
                [field: "descriptionInfo", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(FirmDocument,constraints)
    }
}
