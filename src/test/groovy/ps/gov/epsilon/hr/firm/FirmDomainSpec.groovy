package ps.gov.epsilon.hr.firm

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([Firm,ps.gov.epsilon.hr.firm.settings.FirmSupportContactInfo])
@Domain([Firm])
@TestMixin([HibernateTestMixin])
class FirmDomainSpec extends ConstraintUnitSpec {

    void "test Firm all constraints"() {
        when:
        List<Map> constraints = [
                [field: "code", value: null, testResult: TestResult.FAIL],
                [field: "code", value: "code", testResult: TestResult.PASS],
                [field: "code", value: " ", testResult: TestResult.FAIL],
                [field: "coreOrganizationId", value: null, testResult: TestResult.FAIL],
                [field: "departments", value: null, testResult: TestResult.PASS],
                [field: "firmActiveModules", value: null, testResult: TestResult.PASS],
                [field: "firmSettings", value: null, testResult: TestResult.PASS],
                [field: "name", value: null, testResult: TestResult.FAIL],
                [field: "name", value: " ", testResult: TestResult.FAIL],
                [field: "name", value: "nameA1c_", testResult: TestResult.FAIL],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.PASS],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "supportContactInfo", value: null, testResult: TestResult.PASS],
                [field: "supportContactInfo", value: ps.gov.epsilon.hr.firm.settings.FirmSupportContactInfo.build(), testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(Firm,constraints)
    }
}
