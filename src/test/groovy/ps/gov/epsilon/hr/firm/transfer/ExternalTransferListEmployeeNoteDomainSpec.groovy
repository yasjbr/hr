package ps.gov.epsilon.hr.firm.transfer

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ExternalTransferListEmployeeNote,ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployee])
@Domain([ExternalTransferListEmployeeNote])
@TestMixin([HibernateTestMixin])
class ExternalTransferListEmployeeNoteDomainSpec extends ConstraintUnitSpec {

    void "test ExternalTransferListEmployeeNote all constraints"() {
        when:
        List<Map> constraints = [
                [field: "externalTransferListEmployee", value: null, testResult: TestResult.FAIL],
                [field: "externalTransferListEmployee", value: ps.gov.epsilon.hr.firm.transfer.ExternalTransferListEmployee.build(), testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "noteDate", value: null, testResult: TestResult.FAIL],
                [field: "noteDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "orderNo", value: null, testResult: TestResult.PASS],
                [field: "orderNo", value: " ", testResult: TestResult.FAIL],
                [field: "orderNo", value: "orderNoA1c_", testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(ExternalTransferListEmployeeNote,constraints)
    }
}
